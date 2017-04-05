package de.tinf15b4.kino.api.rest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

public class RestClient {

    private static final String ENCODING = "UTF-8";

    private static final String AUTHORIZE = "/authorize?name=%s&password=%s";
    private static final String LOGOUT = "/logout?token=%s";

    private static final String MISSING_AUTHORIZATION = "Token invalid or expired";
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    private String token;
    private String baseUrl;
    private String trainerName;
    private String password;

    private boolean authorized;

    public RestClient(String trainerName, String password, String baseUrl) {
        this.trainerName = trainerName;
        this.password = password;
        this.baseUrl = baseUrl + "/api/rest";
    }

    public RestResponse authorize() {
        String requestUrl;
        try {
            requestUrl = baseUrl + String.format(AUTHORIZE, URLEncoder.encode(trainerName, ENCODING),
                    URLEncoder.encode(password, ENCODING));
            authorized = true;
            RestResponse response = doGetRequest(requestUrl, String.class, false);
            if (response.hasError()) {
                authorized = false;
            } else {
                token = (String) response.getValue();
            }
            return response;
        } catch (UnsupportedEncodingException e) {
            // should never happen
            throw new RuntimeException(e);
        }
    }

    public RestResponse logout() {
        String requestUrl = baseUrl + String.format(LOGOUT, token);
        RestResponse response = doGetRequest(requestUrl, String.class, true);
        if (!response.hasError()) {
            authorized = false;
            token = null;
        }
        return response;
    }

    private RestResponse doGetRequest(String urlString, Class<?> expectedResult, boolean needAuthorization) {
        return doRestCall(parseUrl(urlString), expectedResult, RequestMethod.GET, toJson(""), needAuthorization);
    }

    // Might be used soonish?
    @SuppressWarnings("unused")
    private RestResponse doPostRequest(String urlString, Class<?> expectedResult, Object postObject,
            boolean needAuthorization) {
        return doRestCall(parseUrl(urlString), expectedResult, RequestMethod.POST, toJson(postObject),
                needAuthorization);
    }

    // Might be used soonish?
    @SuppressWarnings("unused")
    private RestResponse doDeleteRequest(String urlString, Object deleteObject, boolean needAuthorization) {
        return doRestCall(parseUrl(urlString), String.class, RequestMethod.DELETE, toJson(deleteObject),
                needAuthorization);
    }

    private RestResponse doRestCall(URL url, Class<?> expectedResult, RequestMethod method, byte[] body,
            boolean needAuthorization) {
        if (needAuthorization && !authorized) {
            return new RestResponse(MISSING_AUTHORIZATION, HttpStatus.UNAUTHORIZED, null);
        }

        try {
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method.toString());

            // Prepare request
            if (method == RequestMethod.DELETE || method == RequestMethod.POST) {
                connection.setDoOutput(true);
                connection.setRequestProperty("content-type", "application/json");
                OutputStream stream = connection.getOutputStream();
                stream = new DataOutputStream(stream);
                stream.write(body);
                stream.flush();
                stream.close();
            }

            // Do request
            connection.connect();

            // Get result
            HttpStatus status = HttpStatus.valueOf(connection.getResponseCode());
            switch (status) {
            case OK:
                // Parse result
                if (!expectedResult.equals(byte[].class)) {
                    String result = parseString(connection.getInputStream());
                    if (!expectedResult.equals(String.class)) {
                        Object parsed = GsonFactory.buildGson().fromJson(result, expectedResult);
                        return new RestResponse(null, status, parsed);
                    } else {
                        return new RestResponse(null, status, result);
                    }
                } else {
                    InputStream inputStream = connection.getInputStream();
                    byte[] data = ByteStreams.toByteArray(inputStream);
                    return new RestResponse(null, status, data);
                }
            case UNAUTHORIZED:
                // Token has expired
                authorize();
                if (token != null)
                    return doRestCall(url, expectedResult, method, body, needAuthorization);
            default:
                String error = parseString(connection.getErrorStream());
                return new RestResponse(error, status, null);
            }
        } catch (IOException e) {
            throw new RuntimeException(INTERNAL_SERVER_ERROR, e);
        }
    }

    private String parseString(InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder result = new StringBuilder();
            while (reader.ready()) {
                result.append(reader.readLine());
            }
            return result.toString();
        }
    }

    private URL parseUrl(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException(INTERNAL_SERVER_ERROR, e);
        }
    }

    private byte[] toJson(Object body) {
        return GsonFactory.buildGson().toJson(body).getBytes(Charsets.UTF_8);
    }
}
