package de.tinf15b4.kino.retrieval.scraper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.omertron.themoviedbapi.MovieDbException;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.playlists.Playlist;

public class KurbelScraper extends AbstractCinemaScraper {

    private Cinema cinema;

    public KurbelScraper() {
        super("Die Kurbel");
    }

    @Override
    public Logger getLogger() {
        return LoggerFactory.getLogger(KurbelScraper.class);
    }

    @Override
    public void gatherData() {
        cinema = getCinema();
        driver.get("http://www.kurbel-karlsruhe.de/index.php");
        driver.findElementByXPath(".//a[contains(text(), 'Programm/Tickets')]").click();
        handleMovies();
    }

    private void handleMovies() {
        List<WebElement> movies = driver
                .findElementsByXPath(".//div[contains(@class, 'film_box')]//div[contains(@id, 'mitte')]");
        for (WebElement movieElement : movies) {
            String title = movieElement.findElement(By.xpath(".//h2")).getText();
            Movie movie = saveObject(new Movie(title, null, null, 0, null, null), Movie.class);
            try {
                movie = retrieveMovieInformation(movie);
            } catch (MovieDbException e) {
                logger.warn("Failed to retrieve data from movie db for " + title);
                logger.warn(e.toString());
            }
            handlePlaytimes(movie, movieElement);
        }
    }

    private void handlePlaytimes(Movie movie, WebElement parent) {
        List<WebElement> dateElements = parent.findElements(By.xpath(".//tbody/tr[1]/th"));
        List<WebElement> timeElementContainers = parent.findElements(By.xpath(".//tbody/tr[2]/td"));
        // next 7 days
        for (int i = 0; i < 7; i++) {
            WebElement dateElement = dateElements.get(i);
            WebElement timeElementContainer = timeElementContainers.get(i);

            LocalDate date = parseDate(dateElement.getText());

            List<WebElement> timeElements = timeElementContainer.findElements(By.xpath(".//a"));
            for (WebElement timeElement : timeElements) {
                LocalDateTime dateTime = addTimeToDate(timeElement.getText(), date);

                Playlist playlist = new Playlist();
                playlist.setCinema(cinema);
                playlist.setMovie(movie);
                playlist.setTime(Date.from(dateTime.atZone(ZoneId.of("GMT+1")).toInstant()));
                saveObject(playlist, Playlist.class);
            }

        }
    }

    private LocalDateTime addTimeToDate(String timeText, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        formatter = formatter.withLocale(Locale.GERMAN);
        LocalTime time = LocalTime.parse(timeText, formatter);
        return date.atTime(time);
    }

    private LocalDate parseDate(String dateText) {
        if (dateText.trim().equals("Heute")) {
            return LocalDate.now();
        } else {
            dateText = dateText.trim();
            dateText = dateText.substring(3, dateText.length());
            // TODO what does the website do at silvester? Currently there are
            // no years visible
            dateText = dateText + LocalDate.now().getYear();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            formatter = formatter.withLocale(Locale.GERMAN);
            return LocalDate.parse(dateText, formatter);
        }
    }

    private Cinema getCinema() {
        return saveObject(new Cinema("Die Kurbel", "Kaiserpassage", "6", "76133", "Karlsruhe", "Deutschland", null),
                Cinema.class);
    }

}
