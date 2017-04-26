package de.tinf15b4.kino.web.user;

public class NoUserLoggedInException extends IllegalStateException {

    public NoUserLoggedInException(String message) {
        super(message);
    }

    private static final long serialVersionUID = -6335669243601065937L;

}
