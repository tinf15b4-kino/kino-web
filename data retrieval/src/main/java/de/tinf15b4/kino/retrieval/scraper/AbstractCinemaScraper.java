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
import java.util.Iterator;
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
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.retrieval.tmdb.TmdbDataRetriever;
import de.tinf15b4.kino.utils.GsonFactory;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;

public abstract class AbstractCinemaScraper {

    private static final String REST_CALL_FAILED = "Call to REST Service failed";

    protected RemoteWebDriver driver;
    protected Logger logger;
    private TmdbDataRetriever tmdb;

    protected Cinema cinema;

    public AbstractCinemaScraper() {
        tmdb = new TmdbDataRetriever();
        cinema = new Cinema();
        cinema.setName("UNKNOWN");
    }

    public void scrape() {
        logger = getLogger();
        logger.info("Initializing Webdriver for scraper: [{}]", cinema.getName());
        initlializeWebdriver();

        cinema = getCinema();
        if (cinema == null)
            throw new IllegalStateException("Subclasses have to override this method and return a valid cinema");
        cinema = saveObject(cinema, Cinema.class);

        logger.info("Start gathering data from: [{}]", cinema.getName());
        Stopwatch watch = Stopwatch.createStarted();
        GatheringResult result = gatherData();
        logger.info("Finished gathering data from: [{}] in {} milliseconds", cinema.getName(),
                watch.elapsed(TimeUnit.MILLISECONDS));

        driver.quit();
        logger.info("Closing driver for scraper: [{}]", cinema.getName());

        // get information for and save movies
        // we also adjust the corresponding playlists here
        processMovies(result);

        // Reset playlist
        logger.info("Resetting playlist for scraper: [{}]", cinema.getName());
        deletePlaylistFuture(cinema);

        // adjust the cinema in playlist and save it
        logger.info("Saving playlist for scraper: [{}]", cinema.getName());
        processPlaylists(result);

    }

    private void processPlaylists(GatheringResult result) {
        for (Playlist playlist : result.getPlaylists()) {
            playlist.setCinema(cinema);
            Playlist p = saveObject(playlist, Playlist.class);
            logger.info("Movie {} is played at {} in {}", p.getMovie().getName(), p.getTime(), p.getCinema().getName());
        }
    }

    private void processMovies(GatheringResult result) {
        for (Movie movie : result.getMovies()) {
            Movie filledMovie = retrieveMovieInformation(movie);
            if (filledMovie != null) {
                saveAndUpdatePlaylists(result, movie, filledMovie);
            } else {
                // if tmdb does not know a movie we won't show it
                removePlaylists(result, movie);
            }
        }
    }

    private void removePlaylists(GatheringResult result, Movie movie) {
        Iterator<Playlist> iterator = result.getPlaylists().iterator();
        while (iterator.hasNext()) {
            Playlist playlist = iterator.next();
            if (playlist.getMovie() == movie) {
                iterator.remove();
            }
        }
    }

    private void saveAndUpdatePlaylists(GatheringResult result, Movie movie, Movie filledMovie) {
        Movie savedMovie = saveObject(filledMovie, Movie.class);
        for (Playlist playlist : result.getPlaylists()) {
            // We want to compare for the specific instance here as its
            // the only thing we have
            if (playlist.getMovie() == movie) {
                playlist.setMovie(savedMovie);
            }
        }
    }

    protected abstract Logger getLogger();

    protected abstract GatheringResult gatherData();

    protected abstract Cinema getCinema();

    private void initlializeWebdriver() {
        String drvstr = System.getProperty("kinotest.driver");
        String remote = System.getProperty("kinotest.seleniumHub");

        if (drvstr == null || drvstr.isEmpty())
            drvstr = "firefox";

        switch (drvstr) {
        case "firefox":
            FirefoxDriverManager.getInstance().setup("0.16.0");
            driver = new FirefoxDriver();
            logger.info("Firefox driver initialized for scraper: [{}]", cinema.getName());
            break;
        case "chrome":
            ChromeDriverManager.getInstance().setup("2.25");
            driver = new ChromeDriver();
            logger.info("Chrome driver initialized for scraper: [{}]", cinema.getName());
            break;
        case "remote":
            if (remote == null || remote.isEmpty())
                remote = "http://localhost:4444/wd/hub";

            try {
                driver = new RemoteWebDriver(new URL(remote), DesiredCapabilities.firefox());
                logger.info("Remote driver initialized for scraper: [{}]", cinema.getName());
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Maformed URL: [" + remote + "]", e);
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown driver '" + drvstr + '"');
        }
    }

    private Movie retrieveMovieInformation(Movie movie) {
        try {
            return tmdb.getMovie(movie);
        } catch (MovieDbException e) {
            // Movie DB does not know this movie for some reason. Basically this
            // means we won't show it
            logger.warn("TheMovieDB does not know movie [{}]. We skip this movie for now.", movie.getName());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T saveObject(Object toSave, Class<T> expectedResult) {
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
                if (status != 200) {
                    throw new IllegalStateException(REST_CALL_FAILED);
                } else {
                    try (Reader r = new InputStreamReader(connection.getInputStream())) {
                        return GsonFactory.buildGson().fromJson(r, expectedResult);
                    }
                }
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            throw new IllegalStateException(REST_CALL_FAILED, e);
        }
    }

    private void deletePlaylistFuture(Cinema cinema) {
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
                    throw new IllegalStateException(REST_CALL_FAILED);

            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            throw new IllegalStateException(REST_CALL_FAILED, e);
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
                    throw new IllegalStateException(REST_CALL_FAILED);
                } else {
                    try (InputStream is = connection.getInputStream(); Reader r = new InputStreamReader(is)) {
                        return type.findObject(r, toSave);
                    }
                }
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            throw new IllegalStateException(REST_CALL_FAILED, e);
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
