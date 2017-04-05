package de.tinf15b4.kino.api.rest;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;

import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.data.users.UserService;

@RestController
public class KinoRestController {

    private static final String NOT_NULL = "Parameters or request body must not be null";
    private static final String WRONG_PASSWORD = "Wrong password";
    private static final String USER_LOGGED_IN = "This user is already logged in";
    private static final String TOKEN_INVALID = "Token invalid or expired";
    private static final Object LOGOUT_SUCCESS = "Logout successful";

    private ConcurrentHashMap<Token, String> tokens = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Token> users = new ConcurrentHashMap<>();

    @Autowired
    private UserService userService;

    @RequestMapping(value = "api/rest/authorize", method = RequestMethod.GET)
    public ResponseEntity<?> authorize(@RequestParam(name = "username") String username,
            @RequestParam(name = "password") String password) throws UnsupportedEncodingException {
        if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(NOT_NULL);

        username = URLDecoder.decode(username, "UTF-8");
        password = URLDecoder.decode(password, "UTF-8");

        User user = userService.findByName(username);

        if (users.containsKey(username))
            return ResponseEntity.badRequest().body(USER_LOGGED_IN);

        if (user != null && password.equals(user.getPassword())) {
            SecureRandom random = new SecureRandom();
            String tokenKey = new BigInteger(256, random).toString(32);
            Token token = new Token(tokenKey);
            updateTokenForUser(username, token);
            return ResponseEntity.ok(tokenKey);
        } else
            return ResponseEntity.badRequest().body(WRONG_PASSWORD);
    }

    @RequestMapping(value = "api/rest/logout", method = RequestMethod.GET)
    public ResponseEntity<?> logout(@RequestParam(name = "token") String token) {
        ResponseEntity<?> response = checkToken(token);
        if (response.getStatusCode() == HttpStatus.OK) {
            synchronized (tokens) {
                users.remove(tokens.remove(new Token(token)));
            }
            return ResponseEntity.ok(LOGOUT_SUCCESS);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALID);
    }

    @RequestMapping(value = "api/rest/getUser", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@RequestParam(name = "token") String token) {
        ResponseEntity<?> response = checkToken(token);
        if (response.getStatusCode() != HttpStatus.OK)
            return response;
        return ResponseEntity.ok(userService.findByName((String) response.getBody()));
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
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALID);
    }

    private void removeToken(Token token) {
        updateTokenForUser(null, token);
    }

    private void updateTokenForUser(String username, Token token) {
        if (username == null) {
            synchronized (tokens) {
                users.remove(tokens.remove(token));
            }
        } else {
            synchronized (tokens) {
                tokens.put(token, username);
                users.put(username, token);
            }
        }
    }
}
