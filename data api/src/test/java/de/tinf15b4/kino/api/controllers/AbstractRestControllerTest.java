package de.tinf15b4.kino.api.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class AbstractRestControllerTest {

    protected void assertInvalidResponse(ResponseEntity<?> response, HttpStatus expected, String containedError) {
        assertEquals(expected, response.getStatusCode());
        assertTrue(((String) response.getBody()).contains(containedError));
    }

    protected void assertValidResponse(ResponseEntity<?> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}