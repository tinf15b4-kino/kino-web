package de.tinf15b4.kino.api.controllers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PingControllerTest {

    @Test
    public void testPingPong() throws Exception {
        PingController controller = new PingController();
        assertEquals("pong", controller.ping());
    }

}
