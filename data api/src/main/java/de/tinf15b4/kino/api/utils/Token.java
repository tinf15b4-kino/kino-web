package de.tinf15b4.kino.api.utils;

import java.util.Calendar;

import com.google.common.annotations.VisibleForTesting;

public class Token {

    private static final int TOKEN_TIMEOUT = 10;

    private long expireTime;
    private String tokenString;

    public Token(String token) {
        this.tokenString = token;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, TOKEN_TIMEOUT);
        expireTime = cal.getTimeInMillis();
    }

    public String getToken() {
        return tokenString;
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
        result = prime * result + ((tokenString == null) ? 0 : tokenString.hashCode());
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
        if (tokenString == null) {
            if (other.tokenString != null)
                return false;
        } else if (!tokenString.equals(other.tokenString))
            return false;
        return true;
    }

}
