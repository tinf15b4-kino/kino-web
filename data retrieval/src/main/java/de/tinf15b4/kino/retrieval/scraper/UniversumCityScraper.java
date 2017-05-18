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

public class UniversumCityScraper extends AbstractCinemaScraper {

    private static final String UNIVERSUM_URL = "https://www.kinopolis.de/ka/programm/woche-1";

    private List<Movie> movies;
    private List<Playlist> playlists;

    @Override
    protected Logger getLogger() {
        return LoggerFactory.getLogger(UniversumCityScraper.class);
    }

    @Override
    protected Cinema getCinema() {
        return new Cinema("Universum City", "Kaiserstrasse", "152", "76133", "Karlsruhe", "Deutschland", null);
    }

    @Override
    protected GatheringResult gatherData() {
        movies = new ArrayList<>();
        playlists = new ArrayList<>();

        driver.get(UNIVERSUM_URL);
        List<WebElement> movieBoxElements = driver.findElementsByXPath(".//div[contains(@class, 'kc_box_in')]");
        List<LocalDate> dates = handleDates();
        handleMovieBoxes(movieBoxElements, dates);
        return new GatheringResult(movies, playlists);
    }

    private List<LocalDate> handleDates() {
        List<LocalDate> dates = new ArrayList<>();
        List<WebElement> dateElements = driver
                .findElements(By.xpath(".//table[contains(@class, 'week_nav')]//td/a/time"));
        for (WebElement dateElement : dateElements) {
            String dateText = dateElement.getText();
            dates.add(parseDate(dateText));
        }
        return dates;
    }

    private void handleMovieBoxes(List<WebElement> movieBoxElements, List<LocalDate> dates) {
        for (WebElement movieBoxElement : movieBoxElements) {
            List<WebElement> versionElements = movieBoxElement
                    .findElements(By.xpath(".//h4[contains(@class, 'headline_4')]"));
            for (WebElement versionElement : versionElements) {
                String title = versionElement.findElement(By.xpath(".//span")).getText();
                Movie movie = new Movie(title, null, null, 0, null, null);
                movies.add(movie);
                handlePlaytimes(versionElement.findElement(By.xpath(".//following-sibling::table")), dates, movie);
            }
        }
    }

    private void handlePlaytimes(WebElement playtimeTableElement, List<LocalDate> dates, Movie movie) {
        for (WebElement playtimeRowElement : playtimeTableElement.findElements(By.xpath(".//tr"))) {
            List<WebElement> playtimeElements = playtimeRowElement.findElements(By.xpath(".//td"));
            for (WebElement playtimeElement : playtimeElements) {
                if (playtimeElement.getAttribute("class").equals("empty"))
                    continue;
                String timeText = playtimeElement.findElement(By.xpath(".//a/time")).getText();
                LocalDateTime dateTime = addTimeToDate(timeText, dates.get(playtimeElements.indexOf(playtimeElement)));

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
        // TODO what does the website do at silvester? Currently there are
        // no years visible
        dateText = dateText + LocalDate.now().getYear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        formatter = formatter.withLocale(Locale.GERMAN);
        return LocalDate.parse(dateText, formatter);
    }

}
