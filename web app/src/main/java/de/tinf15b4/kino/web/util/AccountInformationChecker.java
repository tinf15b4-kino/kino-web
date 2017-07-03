package de.tinf15b4.kino.web.util;

import com.google.common.base.Strings;

import de.tinf15b4.kino.data.users.User;

public class AccountInformationChecker {

    public static String checkUsername(String userName) {
        if (userName.length() > 99) {
            return "Der angegebene Benutzername ist zu lang";
        } else {
            if (userName.length() <= 2) {
                return "Bitte überprüfen Sie die Eingabe";
            }
        }
        return "";
    }

    public static String checkEmail(String email) {
        if (email.length() > 99) {
            return "Die angegebene E-Mailadresse ist zu lang";
        } else {
            if (!email.contains("@") && !email.contains(".") && email.length() <= 6) {
                return "Bitte überprüfen Sie die Eingabe";
            }
        }
        return "";
    }

    public static String checkNewPassword(String newPw, String newCheckPw) {
        if (Strings.isNullOrEmpty(newPw)) {
            return "Sie müssen ein neues Passwort angeben";
        } else if (newPw.length() > 99) {
            return "Das angegebene Passwort ist zu lang";
        } else if (!newPw.equals(newCheckPw)) {
            return "Die angegebenen Passwörter stimmen nicht überein";
        } else if (newPw.length() <= 7) {
            return "Das angegebene Passwort ist zu kurz";
        }
        return "";
    }

    public static String checkOldPassword(String oldPw, User currentUser) {
        if (Strings.isNullOrEmpty(oldPw)) {
            return "Wenn Sie das Passwort ändern möchten, müssen Sie das alte Passwort angeben";
        } else if (!oldPw.equals(currentUser.getPassword())) {
            return "Das angegbene Passwort ist falsch";
        }
        return "";
    }

}
