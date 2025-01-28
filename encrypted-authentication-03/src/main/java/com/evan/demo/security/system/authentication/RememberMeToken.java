package com.evan.demo.security.system.authentication;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class RememberMeToken implements Serializable  {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String username;

    private final String series;

    private final String tokenValue;

    private final Date date;

    private final String signature;

    public RememberMeToken(String username, String series, String tokenValue, Date deadline, String signature) {
        this.username = username;
        this.series = series;
        this.tokenValue = tokenValue;
        this.date = deadline;
        this.signature = signature;
    }

    public Long getDeadline() {
        return date.getTime();
    }


    public String getUsername() {
        return username;
    }

    public String getSeries() {
        return series;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public Date getDate() {
        return date;
    }

    public String getSignature() {
        return signature;
    }
}
