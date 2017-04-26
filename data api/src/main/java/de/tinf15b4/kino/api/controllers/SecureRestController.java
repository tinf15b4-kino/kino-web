package de.tinf15b4.kino.api.controllers;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthToken;
import org.jsondoc.core.annotation.ApiBodyObject;
import org.jsondoc.core.annotation.ApiError;
import org.jsondoc.core.annotation.ApiErrors;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.annotation.ApiResponseObject;
import org.jsondoc.core.annotation.ApiVersion;
import org.jsondoc.core.pojo.ApiStage;
import org.jsondoc.core.pojo.ApiVisibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;

import de.tinf15b4.kino.api.utils.RestControllerConstants;
import de.tinf15b4.kino.api.utils.Token;
import de.tinf15b4.kino.data.ImageContainer;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.data.users.UserService;

@Api(name = "Secure services", description = "Offers all methods needed to hook into the database with authentication", visibility = ApiVisibility.PUBLIC, stage = ApiStage.BETA)
@ApiVersion(since = "0.0.1")
@ApiAuthToken
@RestController
public class SecureRestController {

    private ConcurrentHashMap<Token, String> tokens = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Token> users = new ConcurrentHashMap<>();

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private CinemaService cinemaService;

    @ApiMethod(description = "Attempts to log in the given user")
    @ApiErrors(apierrors = { @ApiError(code = "406", description = "username or password were null or empty"),
            @ApiError(code = "400", description = "This user is already logged in or the password was wrong") })
    @RequestMapping(value = "rest/authorize", method = RequestMethod.GET)
    public @ApiResponseObject ResponseEntity<?> authorize(
            @ApiQueryParam(name = "username", description = "URL-Encoded username that should be logged in") @RequestParam(name = "username") String username,
            @ApiQueryParam(name = "password", description = "URL-Encoded password in plain text for the given user") @RequestParam(name = "password") String password) {
        if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(RestControllerConstants.NOT_NULL);

        User user = userService.findByName(username);

        if (users.containsKey(username))
            return ResponseEntity.badRequest().body(RestControllerConstants.USER_LOGGED_IN);

        if (user != null && password.equals(user.getPassword())) {
            SecureRandom random = new SecureRandom();
            String tokenKey = new BigInteger(256, random).toString(32);
            Token token = new Token(tokenKey);
            updateTokenForUser(username, token);
            return ResponseEntity.ok(tokenKey);
        } else
            return ResponseEntity.badRequest().body(RestControllerConstants.WRONG_PASSWORD);
    }

    @ApiMethod(description = "Logs out the user for the given token")
    @ApiErrors(apierrors = { @ApiError(code = "401", description = "Token was invalid") })
    @RequestMapping(value = "rest/logout", method = RequestMethod.GET)
    public @ApiResponseObject ResponseEntity<?> logout(
            @ApiQueryParam(name = "token", description = "Authentication token for the current user") @RequestParam(name = "token") String token) {
        ResponseEntity<?> response = checkToken(token);
        if (response.getStatusCode() == HttpStatus.OK) {
            synchronized (tokens) {
                users.remove(tokens.remove(new Token(token)));
            }
            return ResponseEntity.ok(RestControllerConstants.LOGOUT_SUCCESS);
        }
        return response;
    }

    @ApiMethod(description = "Returns the user logged in with the given token")
    @ApiErrors(apierrors = { @ApiError(code = "401", description = "Token was invalid") })
    @RequestMapping(value = "rest/getUser", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(
            @ApiQueryParam(name = "token", description = "Authentication token for the current user") @RequestParam(name = "token") String token) {
        ResponseEntity<?> response = checkToken(token);
        if (response.getStatusCode() != HttpStatus.OK)
            return response;
        return ResponseEntity.ok(userService.findByName((String) response.getBody()));
    }

    @ApiMethod(description = "Returns all favorites for the given token")
    @ApiErrors(apierrors = { @ApiError(code = "401", description = "Token was invalid") })
    @RequestMapping(value = "rest/getFavorites", method = RequestMethod.GET)
    public ResponseEntity<?> getFavorites(
            @ApiQueryParam(name = "token", description = "Authentication token for the current user") @RequestParam(name = "token") String token) {
        ResponseEntity<?> response = checkToken(token);
        if (response.getStatusCode() != HttpStatus.OK)
            return response;
        User user = userService.findByName((String) response.getBody());
        List<Favorite> favorites = favoriteService.getAllFavoritesForUser(user);
        filterImages(favorites);
        return ResponseEntity.ok(favorites.toArray(new Favorite[0]));
    }

    @ApiMethod(description = "Returns the favorite for the given token and cinemaId. If there is none, return null")
    @ApiErrors(apierrors = { @ApiError(code = "401", description = "Token was invalid"),
            @ApiError(code = "400", description = "Id does not exist") })
    @RequestMapping(value = "rest/getFavorite", method = RequestMethod.GET)
    public ResponseEntity<?> getFavorite(
            @ApiQueryParam(name = "token", description = "Authentication token for the current user") @RequestParam(name = "token") String token,
            @ApiQueryParam(name = "cinemaId", description = "Id of the cinema") @RequestParam(name = "cinemaId") long cinemaId) {
        ResponseEntity<?> response = checkToken(token);
        if (response.getStatusCode() != HttpStatus.OK)
            return response;
        User user = userService.findByName((String) response.getBody());
        Cinema cinema = cinemaService.findOne(cinemaId);
        if (cinema == null)
            return ResponseEntity.badRequest().body(RestControllerConstants.INVALID_ID);
        Favorite favorite = favoriteService.findFavorite(user, cinema);
        if (favorite != null)
            favorite.doFilter();
        return ResponseEntity.ok(favorite);
    }

    @ApiMethod(description = "Saves the given favorite for the given token")
    @ApiErrors(apierrors = { @ApiError(code = "401", description = "Token was invalid"),
            @ApiError(code = "406", description = "favorite was null") })
    @RequestMapping(value = "rest/saveFavorite", method = RequestMethod.POST)
    public ResponseEntity<?> saveFavorite(
            @ApiQueryParam(name = "token", description = "Authentication token for the current user") @RequestParam(name = "token") String token,
            @ApiBodyObject(clazz = Favorite.class) @RequestBody Favorite favorite) {
        ResponseEntity<?> response = checkToken(token);
        if (response.getStatusCode() != HttpStatus.OK)
            return response;
        if (favorite == null)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(RestControllerConstants.NOT_NULL);
        Optional<Favorite> updated = favoriteService.save(favorite);
        if (updated.isPresent()) {
            updated.get().doFilter();
            return ResponseEntity.ok(updated.get());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RestControllerConstants.INTERNAL_SERVER_ERROR);
    }

    @ApiMethod(description = "Deletes the given favorite for the given token")
    @ApiErrors(apierrors = { @ApiError(code = "401", description = "Token was invalid"),
            @ApiError(code = "406", description = "favorite was null") })
    @RequestMapping(value = "rest/deleteFavorite", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFavorite(
            @ApiQueryParam(name = "token", description = "Authentication token for the current user") @RequestParam(name = "token") String token,
            @ApiBodyObject(clazz = Favorite.class) @RequestBody Favorite favorite) {
        ResponseEntity<?> response = checkToken(token);
        if (response.getStatusCode() != HttpStatus.OK)
            return response;
        if (favorite == null)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(RestControllerConstants.NOT_NULL);
        favoriteService.delete(favorite);
        return ResponseEntity.ok(RestControllerConstants.DELETE_SUCCESSFUL);
    }

    private ResponseEntity<?> checkToken(String tokenKey) {
        if (!Strings.isNullOrEmpty(tokenKey)) {
            Token token = new Token(tokenKey);
            String username;
            if (token.isValid() && !Strings.isNullOrEmpty(username = tokens.get(token))) {
                updateTokenForUser(username, token);
                return ResponseEntity.ok(username);
            } else {
                removeToken(token);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(RestControllerConstants.TOKEN_INVALID);
    }

    private void removeToken(Token token) {
        updateTokenForUser(null, token);
    }

    private void updateTokenForUser(String username, Token token) {
        if (username == null) {
            synchronized (tokens) {
                String tokenKey = tokens.remove(token);
                if (!Strings.isNullOrEmpty(tokenKey))
                    users.remove(tokenKey);
            }
        } else {
            synchronized (tokens) {
                tokens.put(token, username);
                users.put(username, token);
            }
        }
    }

    private void filterImages(List<?> containers) {
        containers.stream().filter(o -> o instanceof ImageContainer)//
                .forEach(c -> ((ImageContainer) c).doFilter());
    }
}
