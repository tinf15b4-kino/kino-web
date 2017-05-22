package de.tinf15b4.kino.web.user;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.web.rest.RestApiUrlSource;
import de.tinf15b4.kino.web.rest.RestClient;
import de.tinf15b4.kino.web.rest.RestResponse;
import de.tinf15b4.kino.web.ui.SmartCinemaUi;

@Component
@SessionScope
public class UserBean implements Serializable {

    private static final long serialVersionUID = -666267056021357220L;

    private RestClient restClient;

    private transient SmartCinemaUi ui;

    @Autowired
    private RestApiUrlSource restApiUrl;

    @PostConstruct
    public void init() {

        restClient = new RestClient(restApiUrl.getUrl());
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public boolean login(String nameOrMail, String password) {
        restClient = new RestClient(nameOrMail, password, restApiUrl.getUrl());
        RestResponse loginResponse = restClient.authorize();
        if (!loginResponse.hasError()) {
            // login was successful
            RestResponse userResponse = restClient.getUser();
            if (!userResponse.hasError()) {
                // which should always be the case
                ui.update();
                return true;
            }
        }
        return false;
    }

    public boolean logout() {
        if (getCurrentUser() == null) {
            // Nobody is logged in. This should never happen as there should be
            // no option to hit logout in this case
            throw new NoUserLoggedInException("There is no user logged in. Logout failed");
        } else {
            RestResponse response = restClient.logout();
            if (!response.hasError()) {
                ui.update();
                return true;
            }
            return false;
        }
    }

    public User getCurrentUser() {
        RestResponse currentUser = restClient.getUser();
        if (currentUser.hasError()) {
            return null;
        }
        else{
            return (User) currentUser.getValue();
        }
    }

    public void setUi(SmartCinemaUi ui) {
        this.ui = ui;
    }

    public RestClient getRestClient() {
        return restClient;
    }

}
