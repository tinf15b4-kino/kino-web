
package de.tinf15b4.kino.api.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.base.Strings;

import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.data.users.UserService;

public class KinoRestControllerTest {

    @InjectMocks
    private KinoRestController underTest;

    @Mock
    private UserService userService;

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
        assertEquals(HttpStatus.OK, response.getStatusCode());
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
    public void testLoginTwice() throws Exception {
        ResponseEntity<?> response1 = underTest.authorize("Mustermann", "muster");
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        ResponseEntity<?> response2 = underTest.authorize("Mustermann", "muster");
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void testLogoutValid() throws Exception {
        String token = (String) underTest.authorize("Mustermann", "muster").getBody();
        ResponseEntity<?> response = underTest.logout(token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
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

}
