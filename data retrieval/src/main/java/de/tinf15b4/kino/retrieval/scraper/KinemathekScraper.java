package de.tinf15b4.kino.retrieval.scraper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.playlists.Playlist;

public class KinemathekScraper extends AbstractCinemaScraper {

    private static final String KINEMATHEK_URL = "http://kinemathek-karlsruhe.de/programm.php";
    private List<Movie> movies;
    private List<Playlist> playtimes;

    public KinemathekScraper() {
        super("Kinemathek");
    }

    @Override
    public Logger getLogger() {
        return LoggerFactory.getLogger(KinemathekScraper.class);
    }

    @Override
    public void gatherData() {
        Cinema cinema = new Cinema("Kinemathek", "Kaiserpassage", "6", "76133", "Karlsruhe", "Deutschland", null);
        movies = new ArrayList<>();
        playtimes = new ArrayList<>();

        driver.get(KINEMATHEK_URL);
        List<WebElement> sections = driver.findElementsByXPath(".//div[contains(@class, 'programm-container')]");
        handleDates(sections);
    }

    private void handleDates(List<WebElement> programmsForDate) {
        for (WebElement programmForDate : programmsForDate) {
            // dont fix the typo in "progamm-timer". This is wrong on the
            // website so it needs to be like that
            WebElement dateElement = programmForDate.findElement(By.xpath(".//div[contains(@class, 'progamm-date')]"));
            String dateText = dateElement.getText();
            LocalDate date = parseDate(dateText);
            List<WebElement> movieElements = programmForDate
                    .findElements(By.xpath(".//div[contains(@class, 'programm-timer')]"));
            handleMovies(movieElements, LocalDate.from(date));
        }
    }

    private void handleMovies(List<WebElement> movieElements, LocalDate date) {
        for (WebElement movieElement : movieElements) {
            String timeText = movieElement.findElement(By.xpath(".//div[contains(@class, 'programm-time')]")).getText();
            LocalDateTime dateTime = addTimeToDate(timeText, date);

            String title = movieElement.findElement(By.xpath(".//div[contains(@class, 'programm-title')]/a")).getText();
            System.out.println(String.format("Movie [%s] is played at [%s]", title, dateTime));
        }
    }

    private LocalDateTime addTimeToDate(String timeText, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        formatter = formatter.withLocale(Locale.GERMAN);
        LocalTime time = LocalTime.parse(timeText, formatter);
        return date.atTime(time);
    }

    private LocalDate parseDate(String dateText) {
        if (dateText.equals("heute")) {
            return LocalDate.now();
        } else {
            dateText = dateText.substring(3, dateText.length());
            // TODO what does the website do at silvester? Currently there are
            // no years visible
            dateText = dateText + " " + LocalDate.now().getYear();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMMM yyyy");
            formatter = formatter.withLocale(Locale.GERMAN);
            return LocalDate.parse(dateText, formatter);
        }
    }

}
