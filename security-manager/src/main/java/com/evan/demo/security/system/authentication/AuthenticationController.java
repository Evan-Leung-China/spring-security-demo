package com.evan.demo.security.system.authentication;

import com.evan.demo.security.core.CommonResult;
import com.evan.demo.security.core.ErrorCodeEnum;
import com.evan.demo.security.system.user.pojo.dto.request.ReqLoginDTO;
import com.evan.demo.security.utils.AesUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Setter
@RestController
public class AuthenticationController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${security.authentication.aesKey:abcdef1234567890}")
    private String aesKey;
    @Autowired
    private ObjectMapper objectMapper;

    public static final String MOCK_KALT = "123456";
    public static final String MOCK_PASSWORD = "Aa123456";

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private CaptchaService captchaService;

    @PostMapping("/login")
    public CommonResult<String> login(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
        ReqLoginDTO loginRequest;
        try {
            // 解析解密请求体
            log.info("requestBody: {}", requestBody);
            loginRequest = parseAndDecryptBody(requestBody);
        } catch (BadCredentialsException e) {
            return CommonResult.buildFailure(ErrorCodeEnum.AUTHENTICATION_ERROR, e.getMessage(), String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResult.buildFailure(ErrorCodeEnum.ERROR, "解析请求体失败", String.class);
        }

        try {
            // 1. 认证
            KaltAuthentication authResult = authenticationService.authentication(request, response, loginRequest);

            // 3. 加密返回
            String jsonResponse = objectMapper.writer().writeValueAsString(authResult.getDetails());
            String encryptResponse = AesUtils.encrypt(jsonResponse, aesKey);
            return CommonResult.buildSuccess(encryptResponse);
        } catch (Exception ex) {
            // Authentication failed
            logger.error(ex.getMessage(), ex);
//            this.securityContextHolderStrategy.clearContext();
            return CommonResult.buildFailure(ErrorCodeEnum.AUTHENTICATION_ERROR, String.class);
        }
    }

    private ReqLoginDTO parseAndDecryptBody(String requestBody) {

        ReqLoginDTO loginRequest;
        loginRequest = Optional.ofNullable(requestBody)
                .map(encryptedBody -> AesUtils.decrypt(encryptedBody, aesKey))
                .filter(StringUtils::isNotEmpty)
                .map(jsonBody -> {
                    try {
                        return objectMapper.readerFor(ReqLoginDTO.class).readValue(jsonBody, ReqLoginDTO.class);
                    } catch (IOException e) {
                        throw new RuntimeException("解析失败", e);
                    }
                })
                .orElseThrow(() -> new BadCredentialsException("用户名或密码不能为空"));
        return loginRequest;
    }


    @GetMapping("/captcha")
    public ResponseEntity<byte[]> getCaptchaImage(HttpSession httpSession) throws IOException {
        // 生成图形验证码
        RenderedImage bi = captchaService.getCaptchaImage(httpSession);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "jpg", baos);
        byte[] bytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        // 可以设置缓存控制等头部信息
        // headers.setCacheControl(CacheControl.noStore().getHeaderValue());

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

}
