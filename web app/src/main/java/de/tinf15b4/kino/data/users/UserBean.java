package de.tinf15b4.kino.data.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import de.tinf15b4.kino.web.ui.SmartCinemaUi;

@Component
@SessionScope
public class UserBean {

    @Autowired
    private UserService userService;

    private SmartCinemaUi ui;
    private User currentUser;

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    public boolean login(String nameOrMail, String password) {
        User user = userService.findByName(nameOrMail);
        if (user == null) {
            // Try to find the user by its email if finding by username fails
            user = userService.findByEmail(nameOrMail);
        }
        if (user != null && user.getPassword().equals(password)) {
            // login successful
            this.currentUser = user;
            ui.update();
            return true;
        } else {
            // login failed
            return false;
        }
    }

    public boolean logout() {
        if (currentUser == null) {
            // Nobody is logged in. This should never happen as there should be
            // no option to hit logout in this case
            throw new NoUserLoggedInException("There is no user logged in. Logout failed");
        } else {
            currentUser = null;
            ui.update();
            return true;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setUi(SmartCinemaUi ui) {
        this.ui = ui;
    }

}