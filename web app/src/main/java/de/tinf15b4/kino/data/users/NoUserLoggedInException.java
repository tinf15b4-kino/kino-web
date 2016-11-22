package de.tinf15b4.kino.data.users;

public class NoUserLoggedInException extends IllegalStateException {

    public NoUserLoggedInException(String message) {
        super(message);
    }

    private static final long serialVersionUID = -6335669243601065937L;

}
