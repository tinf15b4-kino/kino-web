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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.omertron.themoviedbapi.MovieDbException;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.AgeControl;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.playlists.Playlist;

public class ZkmFilmpalastScraper extends AbstractCinemaScraper {
    private static final String ZKM_URL = "http://www.filmpalast.net/programm.html";
    private static final String ZKM_URL_2 = "http://www.filmpalast.net/programm-folgewoche.html";
    private static final Logger logger = LoggerFactory.getLogger(ZkmFilmpalastScraper.class);

    private Cinema cinema;

    public ZkmFilmpalastScraper() {
        super("Filmpalast am ZKM");
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void gatherData() {
        deletePlaylistFuture(getCinema());

        for (String url : new String[] { ZKM_URL, ZKM_URL_2 }) {
            driver.get(url);

            // iterate over films
            for (WebElement el : driver.findElementsByCssSelector("article.film")) {
                String title;
                try {
                    WebElement titleEl = el.findElement(By.className("gwfilmdb-film-title"));
                    title = titleEl.getAttribute("innerText");
                } catch (NoSuchElementException e) {
                    title = null;
                }

                int length;
                try {
                    WebElement lengthEl = el.findElement(By.className("gwfilmdb-film-length"));
                    String lengthTxt = lengthEl.getText();
                    Pattern p = Pattern.compile("Länge:\\s*(\\d+)\\s*Min\\.");
                    Matcher m = p.matcher(lengthTxt);
                    m.matches();
                    length = Integer.parseInt(m.group(1));
                } catch (NoSuchElementException | IllegalStateException | NumberFormatException e) {
                    length = 0;
                }

                String fsk;
                try {
                    WebElement fskEl = el.findElement(By.cssSelector(".gwfilmdb-film-rating.hidden-xs"));
                    fsk = fskEl.getText();
                } catch (NoSuchElementException e) {
                    fsk = "FSK: Keine Jugendfreigabe"; // safe default
                }

                String descr;
                try {
                    WebElement descrEl = el.findElement(By.cssSelector(".gwfilmdb-film-description"));
                    descr = descrEl.getText();
                } catch (NoSuchElementException e) {
                    descr = "";
                }

                byte imgBytes[];
                try {
                    WebElement imgEl = el.findElement(By.cssSelector(".item-image"));
                    String imgSrc = imgEl.getAttribute("data-src");

                    URL imgUrl = new URI(driver.getCurrentUrl()).resolve(imgSrc).toURL();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    InputStream is = null;

                    is = imgUrl.openStream ();
                    byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
                    int n;

                    while ( (n = is.read(byteChunk)) > 0 ) {
                        baos.write(byteChunk, 0, n);
                    }

                    imgBytes = baos.toByteArray();
                } catch (URISyntaxException | IOException | NoSuchElementException e) {
                    imgBytes = null;
                }

                // TODO: Read year for disambiguating films
                // but we don't save no year for Movies

                if (!title.trim().equals("")) {
                    Movie movie = new Movie();
                    movie.setName(title);

                    // BAD HACK: description field is severely limited
                    movie.setDescription(descr.substring(0, Math.min(descr.length(), 255)));
                    movie.setLengthMinutes(length);

                    if (fsk.equals("FSK: Ohne Altersbeschränkung"))
                        movie.setAgeControl(AgeControl.USK0);
                    else if (fsk.equals("FSK: Ab 6 Jahren"))
                        movie.setAgeControl(AgeControl.USK6);
                    else if (fsk.equals("FSK: Ab 12 Jahren"))
                        movie.setAgeControl(AgeControl.USK12);
                    else if (fsk.equals("FSK: Ab 16 Jahren"))
                        movie.setAgeControl(AgeControl.USK16);
                    else
                        movie.setAgeControl(AgeControl.USK18);

                    movie.setCover(imgBytes);

                    // Look up additional data from the movie db
                    try {
                        movie = retrieveMovieInformation(movie);
                    } catch (MovieDbException e) {
                        logger.warn("Could not retrieve movie db information from " + movie.getName());
                        logger.warn(e.toString());
                    }

                    movie = saveObject(movie, Movie.class);

                    // create playlist
                    for (WebElement e : el.findElements(By.cssSelector(".programm-table .slot-future a.performance-popover"))) {
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
