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

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.utils.GsonFactory;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;

public abstract class AbstractCinemaScraper {

    protected RemoteWebDriver driver;
    protected Logger logger;
    private String cinemaName;

    public AbstractCinemaScraper(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public void scrape() {
        logger = getLogger();
        logger.info(String.format("Initializing Webdriver for scraper: [%s]", cinemaName));
        String drvstr = System.getProperty("kinotest.driver");
        String remote = System.getProperty("kinotest.seleniumHub");

        if (drvstr == null || drvstr.isEmpty())
            drvstr = "chrome";

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

        logger.info(String.format("Start gathering data from: [%s]", cinemaName));
        Stopwatch watch = Stopwatch.createStarted();
        gatherData();
        logger.info(String.format("Finished gathering data from: [%s] in %s milliseconds", cinemaName,
                watch.elapsed(TimeUnit.MILLISECONDS)));
        driver.quit();
    }

    public abstract Logger getLogger();

    public abstract void gatherData();

    protected void retrieveMovieInformation(Movie movie) {
        // TODO hook theMovieDB here and fill the given movie with its necessary
        // information
    }

    protected <T> T saveObject(Object object, Class<T> expectedResult) {
        String baseUrl = createBaseUrl();
        try {
            // Create connection
            URL url;
            url = new URL(baseUrl + ObjectType.forClass(expectedResult).getUrlPostfix());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // Prepare request
            connection.setDoOutput(true);
            connection.setRequestProperty("content-type", "application/json");
            OutputStream stream = connection.getOutputStream();
            stream = new DataOutputStream(stream);
            stream.write(toJson(object));
            stream.flush();
            stream.close();

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
                    try (InputStream is = connection.getInputStream(); Reader r = new InputStreamReader(is)) {
                        return GsonFactory.buildGson().fromJson(r, expectedResult);
                    }
                }
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException("Call to REST Service failed. Is there a running instance of Data Api Project?",
                    e);
        }

    }

    private String createBaseUrl() {
        String envVar = System.getenv("SMARTCINEMA_API_URL");
        if (envVar == null) {
            // use default for local testing
            return "http://localhost:9090/rest-private";
        }
        return envVar;
    }

    private byte[] toJson(Object body) {
        return GsonFactory.buildGson().toJson(body).getBytes(Charsets.UTF_8);
    }

    public enum ObjectType {
        CINEMA("insertCinema"), MOVIE("insertMovie"), PLAYLIST("insertPlaylist");

        private String urlPostfix;

        private ObjectType(String urlPostfix) {
            this.urlPostfix = urlPostfix;
        }

        public String getUrlPostfix() {
            return urlPostfix;
        }

        public static ObjectType forClass(Class<?> clazz) {
            if (clazz.equals(Movie.class))
                return MOVIE;
            if (clazz.equals(Cinema.class))
                return CINEMA;
            if (clazz.equals(Playlist.class))
                return PLAYLIST;
            throw new IllegalArgumentException("Unknown object to save: " + clazz.getName());
        }
    }
}
