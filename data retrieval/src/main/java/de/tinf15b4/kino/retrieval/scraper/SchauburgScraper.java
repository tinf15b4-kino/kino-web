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

public class SchauburgScraper extends AbstractCinemaScraper {

    private static final String SCHAUBURG_URL = "http://www.schauburg.de/programm.php";

    private List<Movie> movies;
    private List<Playlist> playlists;

    @Override
    protected Logger getLogger() {
        return LoggerFactory.getLogger(SchauburgScraper.class);
    }

    @Override
    protected Cinema getCinema() {
        return new Cinema("Filmtheater Schauburg", "Marienstraße", "16", "76137", "Karlsruhe", "Deutschland", null);
    }

    @Override
    protected GatheringResult gatherData() {
        movies = new ArrayList<>();
        playlists = new ArrayList<>();

        driver.get(SCHAUBURG_URL);
        List<WebElement> dateElements = driver.findElementsByXPath(".//h5");
        handleDates(dateElements);
        return new GatheringResult(movies, playlists);
    }

    private void handleDates(List<WebElement> dateElements) {
        List<LocalDate> dates = new ArrayList<>();
        for (WebElement dateElement : dateElements) {
            String dateText = dateElement.getText();
            dates.add(parseDate(dateText));
        }
        List<WebElement> movieElements = driver.findElements(By.xpath(".//table"));
        handleMovies(movieElements, dates);
    }

    private void handleMovies(List<WebElement> movieElements, List<LocalDate> dates) {
        for (WebElement movieElement : movieElements) {
            for (WebElement movieRow : movieElement.findElements(By.xpath(".//tr"))) {
                String timeText = movieRow.findElement(By.xpath(".//td[1]")).getText();
                String title = movieRow.findElement(By.xpath(".//td[2]/*[1]")).getText();
                LocalDateTime dateTime = addTimeToDate(timeText, dates.get(movieElements.indexOf(movieElement)));

                Movie movie = new Movie(title, null, null, 0, null, null);
                movies.add(movie);

                Playlist playlist = new Playlist();
                playlist.setCinema(cinema);
                playlist.setMovie(movie);
                playlist.setTime(Date.from(dateTime.atZone(ZoneId.of("Europe/Berlin")).toInstant()));
                playlists.add(playlist);
            }
        }
    }

    private LocalDateTime addTimeToDate(String timeText, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH.mm");
        formatter = formatter.withLocale(Locale.GERMAN);
        LocalTime time = LocalTime.parse(timeText, formatter);
        return date.atTime(time);
    }

    private LocalDate parseDate(String dateText) {
        if (dateText.contains("Heute")) {
            return LocalDate.now();
        } else {
            // TODO what does the website do at silvester? Currently there are
            // no years visible
            String formattedDateText = dateText.substring(4, dateText.length()).substring(0, 6)
                    + LocalDate.now().getYear();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            formatter = formatter.withLocale(Locale.GERMAN);
            return LocalDate.parse(formattedDateText, formatter);
        }
    }

}
