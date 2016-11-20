package de.tinf15b4.kino.data.users;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import de.tinf15b4.kino.web.ui.MyVaadinUi;

@Component
@SessionScope
public class UserLoginBean {

    private boolean loggedIn;
    private MyVaadinUi ui;

    public boolean isUserLoggedIn() {
        // TODO Implement by null check on user
        return loggedIn;
    }

    public boolean login(String username, String password) {
        // TODO Implement check for credentials;
        if (username.equals("Max") && password.equals("Mustermann")) {
            loggedIn = true;
            ui.update();
            return true;
        } else {
            return false;
        }
    }

    public boolean logout() {
        // TODO Implement
        if (loggedIn) {
            loggedIn = false;
            ui.update();
            return true;
        }
        return false;
    }

    public String getUser() {
        // TODO Implement
        return "Max Mustermann";
    }

    public void setUi(MyVaadinUi ui) {
        this.ui = ui;

    }

}
