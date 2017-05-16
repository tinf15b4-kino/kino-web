package de.tinf15b4.kino.retrieval.scraper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.omertron.themoviedbapi.MovieDbException;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.playlists.Playlist;

public class ZkmFilmpalastScraper extends AbstractCinemaScraper {
    private static final String ZKM_URL = "http://www.filmpalast.net/programm.html";
    private static final String ZKM_URL_2 = "http://www.filmpalast.net/programm-folgewoche.html";
    private Cinema cinema;

    public ZkmFilmpalastScraper() {
        super("Filmpalast am ZKM");
    }

    @Override
    public Logger getLogger() {
        return LoggerFactory.getLogger(ZkmFilmpalastScraper.class);
    }

    @Override
    public void gatherData() {
        deletePlaylistFuture(getCinema());

        for (String url : new String[] { ZKM_URL, ZKM_URL_2 }) {
            driver.get(url);

            // iterate over films
            for (WebElement el : driver.findElementsByCssSelector("article.film")) {
                WebElement titleEl = el.findElement(By.className("gwfilmdb-film-title"));
                String title = titleEl.getAttribute("innerText");

                // TODO: Read year for disambiguating films
                // but we don't save no year for Movies

                if (!title.trim().equals("")) {
                    Movie movie = new Movie();
                    movie.setName(title);

                    // Look up additional data from the movie db
                    try {
                        movie = retrieveMovieInformation(movie);
                    } catch (MovieDbException e) {
                        logger.warn("Could not retrieve movie db information from " + movie.getName());
                        logger.warn(e.toString());
                    }

                    movie = saveObject(movie, Movie.class);

                    // create playlist
                    for (WebElement e : el.findElements(By.cssSelector(".tab-pane.active .programm-table .slot-future a.performance-popover"))) {
                        String datestr = e.getAttribute("data-original-title");

                        // BAD: Hardcode prices
                        Map<DayOfWeek, Integer> priceMap = new HashMap<>();
                        priceMap.put(DayOfWeek.MONDAY, 850);
                        priceMap.put(DayOfWeek.TUESDAY, 650);
                        priceMap.put(DayOfWeek.WEDNESDAY, 850);
                        priceMap.put(DayOfWeek.THURSDAY, 850);
                        priceMap.put(DayOfWeek.FRIDAY, 950);
                        priceMap.put(DayOfWeek.SATURDAY, 950);
                        priceMap.put(DayOfWeek.SUNDAY, 950);

                        LocalDateTime d = LocalDateTime.parse(datestr,
                                new DateTimeFormatterBuilder().appendPattern("E d.M. H:m 'Uhr'")
                                .parseDefaulting(ChronoField.YEAR, Year.now().getValue()).toFormatter());

                        Playlist playlist = new Playlist();
                        playlist.setCinema(getCinema());
                        playlist.setMovie(movie);
                        playlist.setTime(Date.from(d.atZone(ZoneId.of("Europe/Berlin")).toInstant()));
                        playlist.setPrice(priceMap.get(d.getDayOfWeek()));
                        saveObject(playlist, Playlist.class);
                    }
                }
            }
        }
    }

    private Cinema getCinema() {
        if (cinema == null) {
            driver.navigate().to("http://www.filmpalast.net/information/filmpalast-am-zkm.html");

            // Find pictures
            WebElement bilderEl = driver.findElementByXPath("//h3[text() = 'Bilder']");
            WebElement container = bilderEl.findElement(By.xpath(".."));
            WebElement imgEl = container.findElement(By.cssSelector("img"));

            byte imgBytes[];
            try {
                String imgSrc = imgEl.getAttribute("src");

                URL imgUrl = new URI(driver.getCurrentUrl()).resolve(imgSrc).toURL();
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    try (InputStream is = imgUrl.openStream()) {
                        byte[] byteChunk = new byte[4096];
                        int n;

                        while ((n = is.read(byteChunk)) > 0) {
                            baos.write(byteChunk, 0, n);
                        }
                    }

                    imgBytes = baos.toByteArray();
                }
            } catch (URISyntaxException | IOException | NoSuchElementException e) {
                imgBytes = null;
            }

            cinema = saveObject(new Cinema("Filmpalast am ZKM", "Brauerstraße", "40", "76135",
                            "Karlsruhe", "Deutschland", imgBytes), Cinema.class);

            driver.navigate().back();
        }
        return cinema;
    }
}
