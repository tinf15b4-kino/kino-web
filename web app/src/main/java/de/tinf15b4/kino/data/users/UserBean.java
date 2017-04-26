package de.tinf15b4.kino.data.users;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import de.tinf15b4.kino.web.ui.SmartCinemaUi;
import de.tinf15b4.kino.web.util.RestClient;
import de.tinf15b4.kino.web.util.RestResponse;

@Component
@SessionScope
public class UserBean {

    private RestClient restClient;

    private SmartCinemaUi ui;
    private User currentUser;

    @Value("${local.server.port}")
    private int port = 0;

    @PostConstruct
    public void init() {
        String baseUrl = System.getenv("SMARTCINEMA_API_URL");

        if (baseUrl == null)
            baseUrl = "http://localhost:" + port;

        restClient = new RestClient(baseUrl);
    }

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    public boolean login(String nameOrMail, String password) {
        restClient = new RestClient(nameOrMail, password, "http://localhost:" + port);
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
