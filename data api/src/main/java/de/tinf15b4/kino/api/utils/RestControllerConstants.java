package de.tinf15b4.kino.api.utils;

public class RestControllerConstants {

    private RestControllerConstants() {
        // Just used to hide the public constructor
        // Sonarqube seems to like that
    }

    public static final String NOT_NULL = "Parameter bzw. Request Body darf nicht null sein";
    public static final String WRONG_PASSWORD = "Benutzername oder Passwort falsch";
    public static final String USER_LOGGED_IN = "Benutzer ist bereits angemeldet";
    public static final String TOKEN_INVALID = "Ungültiges oder abgelaufenes Token";
    public static final String LOGOUT_SUCCESS = "Anmelden erfolgreich";
    public static final String INVALID_ID = "Id existiert nicht";
    public static final String DELETE_SUCCESSFUL = "Löschen erfolgreich";
    public static final String INTERNAL_SERVER_ERROR = "Interner Serverfehler";
    public static final String INVALID_USERDATA = "Ungültige Werte in Benutzerdaten";
    public static final String USER_EXISTS = "Benutzername bereits vergeben";

}
