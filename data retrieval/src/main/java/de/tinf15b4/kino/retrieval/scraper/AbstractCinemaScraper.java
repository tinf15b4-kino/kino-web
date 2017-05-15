package de.tinf15b4.kino.retrieval.scraper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.omertron.themoviedbapi.MovieDbException;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.retrieval.tmdb.TmdbDataRetriever;
import de.tinf15b4.kino.utils.GsonFactory;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;

public abstract class AbstractCinemaScraper {

    protected RemoteWebDriver driver;
    protected Logger logger;
    private String cinemaName;
    private TmdbDataRetriever tmdb;

    public AbstractCinemaScraper(String cinemaName) {
        this.cinemaName = cinemaName;
        tmdb = new TmdbDataRetriever();
    }

    public void scrape() {
        logger = getLogger();
        logger.info(String.format("Initializing Webdriver for scraper: [%s]", cinemaName));
        initlializeWebdriver();

        logger.info(String.format("Start gathering data from: [%s]", cinemaName));
        Stopwatch watch = Stopwatch.createStarted();
        gatherData();
        logger.info(String.format("Finished gathering data from: [%s] in %s milliseconds", cinemaName,
                watch.elapsed(TimeUnit.MILLISECONDS)));

        driver.quit();
    }

    private void initlializeWebdriver() {
        String drvstr = System.getProperty("kinotest.driver");
        String remote = System.getProperty("kinotest.seleniumHub");

        if (drvstr == null || drvstr.isEmpty())
            drvstr = "firefox";

        switch (drvstr) {
        case "firefox":
            FirefoxDriverManager.getInstance().setup("0.16.0");
            driver = new FirefoxDriver();
            logger.info(String.format("Firefox driver initialized for scraper: [%s]", cinemaName));
            break;
        case "chrome":
            ChromeDriverManager.getInstance().setup("2.25");
            driver = new ChromeDriver();
            logger.info(String.format("Chrome driver initialized for scraper: [%s]", cinemaName));
            break;
        case "remote":
            if (remote == null || remote.isEmpty())
                remote = "http://localhost:4444/wd/hub";

            try {
                driver = new RemoteWebDriver(new URL(remote), DesiredCapabilities.firefox());
                logger.info(String.format("Remote driver initialized for scraper: [%s]", cinemaName));
            } catch (MalformedURLException e) {
                throw new RuntimeException("Maformed URL: [" + remote + "]", e);
            }
            break;
        default:
            throw new RuntimeException("Unknown driver '" + drvstr + '"');
        }
    }

    public abstract Logger getLogger();

    public abstract void gatherData();

    protected Movie retrieveMovieInformation(Movie movie) throws MovieDbException {
        return tmdb.getMovie(movie);
    }

    @SuppressWarnings("unchecked")
    protected <T> T saveObject(Object toSave, Class<T> expectedResult) {
        ObjectType type = ObjectType.forClass(expectedResult);
        Object contained = getContainedObject(toSave, type);
        if (contained != null)
            return (T) contained;
        return saveToDatabase(toSave, expectedResult, type);
    }

    private <T> T saveToDatabase(Object toSave, Class<T> expectedResult, ObjectType type) {
        try {
            // Create connection
            URL url = new URL(getBaseUrl() + type.getPostUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // Prepare request
            writeBodyToConnection(toSave, connection);

            // Do request
            try {
                connection.connect();

                // Get result
                int status = connection.getResponseCode();
                if (status != 200)
                    throw new IllegalStateException("REST Service call failed.");

                if (expectedResult.equals(Void.class)) {
                    return null;
                } else {
                    try (Reader r = new InputStreamReader(connection.getInputStream())) {
                        return GsonFactory.buildGson().fromJson(r, expectedResult);
                    }
                }
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException("Call to REST Service failed.", e);
        }
    }

    public void deletePlaylistFuture(Cinema cinema) {
        try {
            // Create connection
            URL url = new URL(getBaseUrl() + "/rest-private/clearPlaylistFutureForCinema");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // Prepare request
            writeBodyToConnection(cinema, connection);

            // Do request
            try {
                connection.connect();

                // Get result
                int status = connection.getResponseCode();
                if (status != 200)
                    throw new IllegalStateException("REST Service call failed.");

            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException("Call to REST Service failed.", e);
        }
    }

    private void writeBodyToConnection(Object toSave, HttpURLConnection connection) throws IOException {
        connection.setDoOutput(true);
        connection.setRequestProperty("content-type", "application/json");
        OutputStream stream = connection.getOutputStream();
        stream = new DataOutputStream(stream);
        stream.write(toJson(toSave));
        stream.flush();
        stream.close();
    }

    private Object getContainedObject(Object toSave, ObjectType type) {
        if (type == ObjectType.PLAYLIST)
            return null;
        try {
            // Create connection
            URL url = new URL(getBaseUrl() + type.getGetUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Do request
            try {
                connection.connect();

                // Get result
                int status = connection.getResponseCode();
                if (status != 200) {
                    throw new IllegalStateException("REST Service call failed.");
                } else {
                    try (InputStream is = connection.getInputStream(); Reader r = new InputStreamReader(is)) {
                        return type.findObject(r, toSave);
                    }
                }
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException("Call to REST Service failed.", e);
        }
    }

    private String getBaseUrl() {
        String envVar = System.getenv("SMARTCINEMA_API_URL");
        if (envVar == null) {
            // use default for local testing
            return "http://localhost:9090";
        }
        return envVar;
    }

    private byte[] toJson(Object body) {
        return GsonFactory.buildGson().toJson(body).getBytes(Charsets.UTF_8);
    }

}
