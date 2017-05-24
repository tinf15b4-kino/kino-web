package de.tinf15b4.kino.retrieval.scraper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.playlists.Playlist;

public class KurbelScraper extends AbstractCinemaScraper {

    private static final String KURBEL_URL = "http://www.kurbel-karlsruhe.de/index.php?show=week";
    private List<Movie> movies;
    private List<Playlist> playlists;

    @Override
    protected Logger getLogger() {
        return LoggerFactory.getLogger(KurbelScraper.class);
    }

    @Override
    protected GatheringResult gatherData() {
        movies = new ArrayList<>();
        playlists = new ArrayList<>();

        driver.get(KURBEL_URL);
        handleMovies();
        return new GatheringResult(movies, playlists);
    }

    private void handleMovies() {
        List<WebElement> movieElements = driver
                .findElementsByXPath(".//div[contains(@class, 'film_box')]//div[contains(@id, 'mitte')]");
        for (WebElement movieElement : movieElements) {
            String title = movieElement.findElement(By.xpath(".//h2")).getText();
            Movie movie = new Movie(title, null, null, 0, null, null);
            this.movies.add(movie);
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
                playlist.setTime(Date.from(dateTime.atZone(ZoneId.of("Europe/Berlin")).toInstant()));
                playlists.add(playlist);
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
            // TODO what does the website do at silvester? Currently there are
            // no years visible
            String formattedDateText = dateText.trim().substring(3, dateText.length()) + LocalDate.now().getYear();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            formatter = formatter.withLocale(Locale.GERMAN);
            return LocalDate.parse(formattedDateText, formatter);
        }
    }

    @Override
    protected Cinema getCinema() {
        return new Cinema("Die Kurbel", "Kaiserpassage", "6", "76133", "Karlsruhe", "Deutschland", null);
    }

}
