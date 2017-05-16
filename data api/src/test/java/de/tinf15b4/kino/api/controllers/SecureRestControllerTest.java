package de.tinf15b4.kino.api.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.base.Strings;

import de.tinf15b4.kino.api.utils.RestControllerConstants;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.data.users.UserService;

public class SecureRestControllerTest extends AbstractRestControllerTest {

    @InjectMocks
    private SecureRestController underTest;

    @Mock
    private UserService userService;

    @Mock
    private FavoriteService favoriteService;

    @Mock
    private CinemaService cinemaService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        User user = new User();
        user.setName("Mustermann");
        user.setPassword("muster");
        when(userService.findByName("Mustermann")).thenReturn(user);
    }

    @Test
    public void testAuthorizationValid() throws Exception {
        ResponseEntity<?> response = underTest.authorize("Mustermann", "muster");
        assertValidResponse(response);
        assertFalse(Strings.isNullOrEmpty((String) response.getBody()));
    }

    @Test
    public void testAuthorizationWrongPassword() throws Exception {
        ResponseEntity<?> response = underTest.authorize("Mustermann", "wrongPassword");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testAuthorizationWrongUser() throws Exception {
        ResponseEntity<?> response = underTest.authorize("Musterfrau", "muster");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testAuthorizationEmptyUserAndPassword() throws Exception {
        ResponseEntity<?> response = underTest.authorize("", "");
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals(RestControllerConstants.NOT_NULL, response.getBody());
    }

    @Test
    @Ignore
    public void testLoginTwice() throws Exception {
        ResponseEntity<?> response1 = underTest.authorize("Mustermann", "muster");
        assertValidResponse(response1);
        ResponseEntity<?> response2 = underTest.authorize("Mustermann", "muster");
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void testLogoutValid() throws Exception {
        String token = (String) underTest.authorize("Mustermann", "muster").getBody();
        ResponseEntity<?> response = underTest.logout(token);
        assertValidResponse(response);
    }

    @Test
    public void testLogoutInvalidToken() throws Exception {
        String token = (String) underTest.authorize("Mustermann", "muster").getBody();
        token = token.substring(1);
        ResponseEntity<?> response = underTest.logout(token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testLogoutTwice() throws Exception {
        String token = (String) underTest.authorize("Mustermann", "muster").getBody();
        underTest.logout(token);
        ResponseEntity<?> response = underTest.logout(token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetCallsWithValidToken() throws Exception {
        String token = (String) underTest.authorize("Mustermann", "muster").getBody();
        ResponseEntity<?> response = underTest.getFavorites(token);
        assertValidResponse(response);

        Mockito.when(cinemaService.findOne(0)).thenReturn(new Cinema());
        response = underTest.getFavorite(token, 0);
        assertValidResponse(response);

        Mockito.when(cinemaService.findOne(0)).thenReturn(null);
        response = underTest.getFavorite(token, 0);
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, "not exist");

        response = underTest.getUser(token);
        assertValidResponse(response);
    }

    @Test
    public void testGetCallsWithInvalidToken() throws Exception {
        String token = ((String) underTest.authorize("Mustermann", "muster").getBody()).substring(1);
        ResponseEntity<?> response = underTest.getFavorites(token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        response = underTest.getFavorite(token, 0);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        response = underTest.getUser(token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testSaveCallsWithValidToken() throws Exception {
        String token = (String) underTest.authorize("Mustermann", "muster").getBody();
        Mockito.when(favoriteService.save(Mockito.any())).thenAnswer(new Answer<Optional<Favorite>>() {
            @Override
            public Optional<Favorite> answer(InvocationOnMock invocation) throws Throwable {
                return Optional.of(invocation.getArgumentAt(0, Favorite.class));
            }
        });

        ResponseEntity<?> response = underTest.saveFavorite(token, new Favorite(new User(), new Cinema()));
        assertValidResponse(response);

        response = underTest.saveFavorite(token, null);
        assertInvalidResponse(response, HttpStatus.NOT_ACCEPTABLE, "null");
        Mockito.verify(favoriteService).save(Mockito.any());
    }

    @Test
    public void testSaveCallsWithInvalidToken() throws Exception {
        String token = ((String) underTest.authorize("Mustermann", "muster").getBody()).substring(1);
        ResponseEntity<?> response = underTest.saveFavorite(token, null);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testDeleteCallsWithValidToken() throws Exception {
        String token = (String) underTest.authorize("Mustermann", "muster").getBody();

        ResponseEntity<?> response = underTest.deleteFavorite(token, new Favorite(new User(), new Cinema()));
        assertValidResponse(response);

        response = underTest.deleteFavorite(token, null);
        assertInvalidResponse(response, HttpStatus.NOT_ACCEPTABLE, "null");
        Mockito.verify(favoriteService).delete(Mockito.any());
    }

    @Test
    public void testDeleteCallsWithInvalidToken() throws Exception {
        String token = ((String) underTest.authorize("Mustermann", "muster").getBody()).substring(1);
        ResponseEntity<?> response = underTest.deleteFavorite(token, null);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}
