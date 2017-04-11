package de.tinf15b4.kino.smartcinema.data;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CinemaListLoader {
    public static Cinema[] loadSync() {
        // FIXME we should not hardcode this
        try {
            URL url = new URL("https://smartcinema-dev.tinf15b4.de/rest/getCinemas");
            Gson gson = new Gson();

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());

                return gson.fromJson(in, Cinema[].class);
            } finally {
                urlConnection.disconnect();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Cinema[] {};
    }
}

