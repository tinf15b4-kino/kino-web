package de.tinf15b4.kino.data.users;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import de.tinf15b4.kino.api.rest.RestClient;
import de.tinf15b4.kino.api.rest.RestResponse;
import de.tinf15b4.kino.web.ui.SmartCinemaUi;

@Component
@SessionScope
public class UserBean {

    private static final String BASE_URL = "http://localhost:8080";

    private RestClient restClient;

    private SmartCinemaUi ui;
    private User currentUser;

    @PostConstruct
    public void init() {
        restClient = new RestClient(BASE_URL);
    }

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    public boolean login(String nameOrMail, String password) {
        restClient = new RestClient(nameOrMail, password, BASE_URL);
        RestResponse loginResponse = restClient.authorize();
        if (!loginResponse.hasError()) {
            // login was successful
            RestResponse userResponse = restClient.getUser();
            if (!userResponse.hasError()) {
                // which should always be the case
                currentUser = (User) userResponse.getValue();
                ui.update();
                return true;
            }
        }
        return false;
    }

    public boolean logout() {
        if (currentUser == null) {
            // Nobody is logged in. This should never happen as there should be
            // no option to hit logout in this case
            throw new NoUserLoggedInException("There is no user logged in. Logout failed");
        } else {
            RestResponse response = restClient.logout();
            if (!response.hasError()) {
                currentUser = null;
                ui.update();
                return true;
            }
            return false;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setUi(SmartCinemaUi ui) {
        this.ui = ui;
    }

    public RestClient getRestClient() {
        return restClient;
    }

}
