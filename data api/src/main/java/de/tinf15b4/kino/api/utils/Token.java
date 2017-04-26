package de.tinf15b4.kino.api.utils;

import java.util.Calendar;

import com.google.common.annotations.VisibleForTesting;

public class Token {

    private static final int TOKEN_TIMEOUT = 10;

    private long expireTime;
    private String token;

    public Token(String token) {
        this.token = token;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, TOKEN_TIMEOUT);
        expireTime = cal.getTimeInMillis();
    }

    public String getToken() {
        return token;
    }

    public boolean isValid() {
        return getExpireTime() > Calendar.getInstance().getTimeInMillis();
    }

    @VisibleForTesting
    protected long getExpireTime() {
        return expireTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Token other = (Token) obj;
        if (token == null) {
            if (other.token != null)
                return false;
        } else if (!token.equals(other.token))
            return false;
        return true;
    }

}
