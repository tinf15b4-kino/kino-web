package de.tinf15b4.kino.web.rest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieFilterData;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinema;
import de.tinf15b4.kino.data.ratedmovies.RatedMovie;
import de.tinf15b4.kino.data.search.SearchResult;
import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.utils.GsonFactory;

public class RestClient implements Serializable {

    private static final long serialVersionUID = 142015013387782716L;

    private static final String ENCODING = "UTF-8";

    private static final String AUTHORIZE = "/authorize?username=%s&password=%s";
    private static final String LOGOUT = "/logout?token=%s";
    private static final String GET_USER = "/getUser?token=%s";
    private static final String GET_CINEMAS = "/getCinemas";
    private static final String GET_CINEMA = "/getCinema?cinemaId=%s";
    private static final String GET_FAVORITE = "/getFavorite?token=%s&cinemaId=%s";
    private static final String SAVE_FAVORITE = "/saveFavorite?token=%s";
    private static final String DELETE_FAVORITE = "/deleteFavorite?token=%s";
    private static final String UPDATE_USER = "/saveUser?token=%s";
    private static final String REGISTER_USER = "/registerUser";
    private static final String GET_RATED_CINEMAS = "/getRatedCinemas?cinemaId=%s";
    private static final String GET_PLAYLIST_CINEMA = "/getPlaylistForCinema?cinemaId=%s&from=%s&to=%s";
    private static final String GET_FAVORITES = "/getFavorites?token=%s";
    private static final String GET_MOVIE = "/getMovie?movieId=%s";
    private static final String GET_MOVIES = "/getMovies";
    private static final String GET_FILTERED_MOVIES = "/getFilteredMovies";
    private static final String GET_RATING_FOR_MOVIE = "/getAverageRatingForMovie?movieId=%s";
    private static final String GET_RATING_FOR_CINEMA = "/getAverageRatingForCinema?cinemaId=%s";
    private static final String GET_RATED_MOVIES = "/getRatedMovies?movieId=%s";
    private static final String GET_PLAYLIST_MOVIE = "/getPlaylistForMovie?movieId=%s&from=%s&to=%s";
    private static final String GET_SEARCH_RESULT = "/getSearchResult?term=%s";
    private static final String SAVE_RATED_CINEMA = "/saveRatedCinema?token=%s";
    private static final String SAVE_RATED_MOVIE = "/saveRatedMovie?token=%s";

    private static final String MISSING_AUTHORIZATION = "Token invalid or expired";
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    private String token;
    private String baseUrl;
    private String userNameOrEmail;
    private String password;

    private boolean authorized;

    public RestClient(String baseUrl) {
        this(null, null, baseUrl);
    }

    public RestClient(String userNameOrEmail, String password, String baseUrl) {
        this.userNameOrEmail = userNameOrEmail;
        this.password = password;
        this.baseUrl = baseUrl + "/rest";
    }

    public RestResponse authorize() {
        try {
            URLEncoder.encode(userNameOrEmail, ENCODING);
            String requestUrl = baseUrl
                    + String.format(AUTHORIZE, URLEncoder.encode(userNameOrEmail, ENCODING), URLEncoder.encode(password, ENCODING));
            authorized = true;
            RestResponse response = doGetRequest(requestUrl, String.class, false);
            if (response.hasError()) {
                authorized = false;
            } else {
                token = (String) response.getValue();
            }
            return response;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(INTERNAL_SERVER_ERROR, e);
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

    public RestResponse getUser() {
        String requestUrl = baseUrl + String.format(GET_USER, token);
        return doGetRequest(requestUrl, User.class, true);
    }

    public RestResponse getCinemas() {
        String requestUrl = baseUrl + String.format(GET_CINEMAS, token);
        return doGetRequest(requestUrl, Cinema[].class, false);
    }

    public RestResponse getFavorite(long cinemaId) {
        String requestUrl = baseUrl + String.format(GET_FAVORITE, token, cinemaId);
        return doGetRequest(requestUrl, Favorite.class, true);
    }

    public RestResponse getCinema(long cinemaId) {
        String requestUrl = baseUrl + String.format(GET_CINEMA, cinemaId);
        return doGetRequest(requestUrl, Cinema.class, false);
    }

    public RestResponse saveFavorite(Favorite favorite) {
        String requestUrl = baseUrl + String.format(SAVE_FAVORITE, token);
        return doPostRequest(requestUrl, Favorite.class, favorite, true);
    }

    public RestResponse deleteFavorite(Favorite favorite) {
        String requestUrl = baseUrl + String.format(DELETE_FAVORITE, token);
        return doDeleteRequest(requestUrl, favorite, true);
    }

    public RestResponse updateUser(User user) {
        String requestUrl = baseUrl + String.format(UPDATE_USER, token);
        return doPostRequest(requestUrl, User.class, user, true);
    }

    public RestResponse registerUser(User user) {
        String requestUrl = baseUrl + REGISTER_USER;
        return doPostRequest(requestUrl, User.class, user, false);
    }

    public RestResponse getRatedCinemas(long cinemaId) {
        String requestUrl = baseUrl + String.format(GET_RATED_CINEMAS, cinemaId);
        return doGetRequest(requestUrl, RatedCinema[].class, false);
    }

    public RestResponse saveRatedCinema(RatedCinema ratedCinema) {
        String requestUrl = baseUrl + String.format(SAVE_RATED_CINEMA, token);
        return doPostRequest(requestUrl, RatedCinema.class, ratedCinema, true);
    }

    public RestResponse saveRatedMovie(RatedMovie ratedMovie) {
        String requestUrl = baseUrl + String.format(SAVE_RATED_MOVIE, token);
        return doPostRequest(requestUrl, RatedMovie.class, ratedMovie, true);
    }

    public RestResponse getPlaylistForCinemas(long cinemaId, Date from, Date to) {
        String requestUrl = baseUrl
                + String.format(GET_PLAYLIST_CINEMA, cinemaId, (from != null) ? from.getTime() : "", (to != null) ? to.getTime() : "");
        return doGetRequest(requestUrl, Playlist[].class, false);
    }

    public RestResponse getAllFavorites() {
        String requestUrl = baseUrl + String.format(GET_FAVORITES, token);
        return doGetRequest(requestUrl, Favorite[].class, true);
    }

    public RestResponse getMovie(long movieId) {
        String requestUrl = baseUrl + String.format(GET_MOVIE, movieId);
        return doGetRequest(requestUrl, Movie.class, false);
    }

    public RestResponse getMovies() {
        String requestUrl = baseUrl + String.format(GET_MOVIES);
        return doGetRequest(requestUrl, Movie[].class, false);
    }

    public RestResponse getFilteredMovies(MovieFilterData filterData) {
        String requestUrl = baseUrl + String.format(GET_FILTERED_MOVIES);
        // HACK:
        // This is a POST request to submit the filterdata, otherwise we would
        // need to add all fields as URL parameters and I dont want to do that
        // (like... not at all)
        return doPostRequest(requestUrl, Movie[].class, filterData, false);
    }

    public RestResponse getRatedMovies(long movieId) {
        String requestUrl = baseUrl + String.format(GET_RATED_MOVIES, movieId);
        return doGetRequest(requestUrl, RatedMovie[].class, false);
    }

    public RestResponse getPlaylistForMovie(long movieId, Date from, Date to) {
        String requestUrl = baseUrl
                + String.format(GET_PLAYLIST_MOVIE, movieId, (from != null) ? from.getTime() : "", (to != null) ? to.getTime() : "");
        return doGetRequest(requestUrl, Playlist[].class, false);
    }

    public RestResponse getAverageRatingForMovie(long movieId) {
        String requestUrl = baseUrl + String.format(GET_RATING_FOR_MOVIE, movieId);
        return doGetRequest(requestUrl, Double.class, false);
    }

    public RestResponse getAverageRatingForCinema(long cinemaId) {
        String requestUrl = baseUrl + String.format(GET_RATING_FOR_CINEMA, cinemaId);
        return doGetRequest(requestUrl, Double.class, false);
    }

    public RestResponse search(String searchTerm) {
        String requestUrl = baseUrl + String.format(GET_SEARCH_RESULT, searchTerm);
        return doGetRequest(requestUrl, SearchResult.class, false);
    }

    private RestResponse doGetRequest(String urlString, Class<?> expectedResult, boolean needAuthorization) {
        return doRestCall(parseUrl(urlString), expectedResult, RequestMethod.GET, toJson(""), needAuthorization);
    }

    private RestResponse doPostRequest(String urlString, Class<?> expectedResult, Object postObject, boolean needAuthorization) {
        return doRestCall(parseUrl(urlString), expectedResult, RequestMethod.POST, toJson(postObject), needAuthorization);
    }

    private RestResponse doDeleteRequest(String urlString, Object deleteObject, boolean needAuthorization) {
        return doRestCall(parseUrl(urlString), String.class, RequestMethod.DELETE, toJson(deleteObject), needAuthorization);
    }

    private RestResponse doRestCall(URL url, Class<?> expectedResult, RequestMethod method, byte[] body, boolean needAuthorization) {
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
                throw new IllegalStateException(INTERNAL_SERVER_ERROR);
            default:
                String error = parseString(connection.getErrorStream());
                return new RestResponse(error, status, null);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(INTERNAL_SERVER_ERROR, e);
        }
    }

    private String parseString(InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder result = new StringBuilder();
            while (reader.ready()) {
                result.append(reader.readLine());
            }
            return result.toString();
        } catch (NullPointerException e) {
            // URL must have been wrong. We got an error, but there is no stream
            throw new IllegalArgumentException("URL does not exist", e);
        }
    }

    private URL parseUrl(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(INTERNAL_SERVER_ERROR, e);
        }
    }

    private byte[] toJson(Object body) {
        return GsonFactory.buildGson().toJson(body).getBytes(Charsets.UTF_8);
    }

    public String getCinemaPictureUrl(Cinema c) {
        // NOTE: This is a URL accessible by the browser, not the internal URL because the
        // REST API might be firewalled and not publicly accessible. We assume that a reverse
        // proxy is running so that the public REST API is always accessible on /rest/*
        return "/rest/cinemaPicture?cinemaId=" + c.getId();
    }

    public String getMoviePictureUrl(Movie m) {
        return "/rest/moviePicture?movieId=" + m.getId();
    }

}