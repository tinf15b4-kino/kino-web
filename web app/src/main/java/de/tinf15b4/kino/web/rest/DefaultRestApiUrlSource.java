package de.tinf15b4.kino.web.rest;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component
@ApplicationScope
public class DefaultRestApiUrlSource implements RestApiUrlSource {

    private static final long serialVersionUID = 4476430176881271243L;

    private static final int PORT = 9090;

    @Override
    public String getUrl() {
        String restApiUrl = System.getenv("SMARTCINEMA_API_URL");

        if (restApiUrl == null)
            restApiUrl = System.getProperty("smartcinema.apiurl");

        if (restApiUrl == null)
            restApiUrl = "http://localhost:" + PORT;

        return restApiUrl;
    }
}
