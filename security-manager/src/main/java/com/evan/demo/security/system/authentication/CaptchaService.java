package com.evan.demo.security.system.authentication;

import com.google.code.kaptcha.Producer;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Random;

@Slf4j
@Service
public class CaptchaService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Autowired
    private Producer captchaProducer;

    private static final int CODE_LENGTH = 4;
    public static final String SESSION_ATTRIBUTE_CAPTCHA = "captcha";
    private static final Random random = new Random();

    public BufferedImage getCaptchaImage(HttpSession httpSession) {

        // 生成验证码文本
        String capText = generateVerificationCode();
        // 生成验证码图片
        BufferedImage bi = captchaProducer.createImage(capText);
        httpSession.setAttribute(SESSION_ATTRIBUTE_CAPTCHA, capText);
        return bi;
    }

    public static String generateVerificationCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }

    public boolean validateCaptcha(HttpSession httpSession, String captcha) {
        String sessionCaptcha = (String) httpSession.getAttribute(SESSION_ATTRIBUTE_CAPTCHA);
        log.info("sessionCaptcha: {}, requestCaptcha:{}", sessionCaptcha, captcha);
        return sessionCaptcha != null && sessionCaptcha.equalsIgnoreCase(captcha);
    }
}
