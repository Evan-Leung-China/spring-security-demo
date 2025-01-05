package com.evan.example.web.system.authentication;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Objects;

public class RememberMeSignatureHelper {
    private static final TokenBasedRememberMeServices.RememberMeTokenAlgorithm MATCHING_ALGORITHM = TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256;

    private final String key;

    private final String digestAlgorithm;
    private String username;
    private String token;
    private long tokeExpiryTime;

    public RememberMeSignatureHelper(String key, String digestAlgorithm) {
        this.key = key;
        this.digestAlgorithm = StringUtils.defaultIfEmpty(digestAlgorithm, MATCHING_ALGORITHM.getDigestAlgorithm());
    }

    public RememberMeSignatureHelper withUsername(String username) {
        this.username = username;
        return this;
    }

    public RememberMeSignatureHelper withToken(String token) {
        this.token = token;
        return this;
    }
    public RememberMeSignatureHelper withDeadline(Date deadline) {
        this.tokeExpiryTime = Objects.requireNonNull(deadline).getTime();
        return this;
    }
    public RememberMeSignatureHelper withDeadline(long deadline) {
        this.tokeExpiryTime = deadline;
        return this;
    }

    String makeTokenSignature() {
        if (username == null || token == null || tokeExpiryTime == 0) {
            throw new IllegalStateException("username, token and deadline must be set");
        }

        String data = username + ":" + tokeExpiryTime + ":" + token + ":" + key;
        try {
            MessageDigest digest = MessageDigest.getInstance(digestAlgorithm);
            return new String(Hex.encode(digest.digest(data.getBytes())));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("No " + digestAlgorithm + " algorithm available!");
        }
    }
}
