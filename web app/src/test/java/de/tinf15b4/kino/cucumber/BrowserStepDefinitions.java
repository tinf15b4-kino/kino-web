package de.tinf15b4.kino.cucumber;

import java.net.InetAddress;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.core.StringEndsWith;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaRepository;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieRepository;
import de.tinf15b4.kino.data.playlists.PlaylistRepository;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinema;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaId;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaRepository;
import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.data.users.UserRepository;
import de.tinf15b4.kino.web.KinoWebApplication;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import io.github.bonigarcia.wdm.InternetExplorerDriverManager;

@ContextConfiguration(classes = SpringTestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { KinoWebApplication.class })
public class BrowserStepDefinitions {
    private WebDriver driver = null;

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private SpringTestConfig testConfig;

    @Autowired
    private MovieRepository movieRepo;

    @Autowired
    private CinemaRepository cinemaRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RatedCinemaRepository rCinemaRepo;

    @Autowired
    private PlaylistRepository playlistRepo;

    public BrowserStepDefinitions() throws Exception {
        // These properties can be set on the gradle command line, e.g.
        // ./gradlew test -Dkinotest.driver=chrome
        // Default is Firefox because it is the only one that just works(TM) for
        // me,
        // the Jenkins setup uses a headless firefox installation.
        String drvstr = System.getProperty("kinotest.driver");
        String remote = System.getProperty("kinotest.seleniumHub");

        if (drvstr == null || drvstr.isEmpty())
            drvstr = "firefox";

        switch (drvstr) {
        case "firefox":
            FirefoxDriverManager.getInstance().setup();
            driver = new FirefoxDriver();
            break;
        case "chrome":
            ChromeDriverManager.getInstance().setup();
            driver = new ChromeDriver();
            break;
        case "explorer":
            InternetExplorerDriverManager.getInstance().setup();
            driver = new InternetExplorerDriver();
            break;
        case "remote":
            if (remote == null || remote.isEmpty())
                remote = "http://localhost:4444/wd/hub";

            driver = new RemoteWebDriver(new URL(remote), DesiredCapabilities.firefox());
            break;
        default:
            throw new Exception("Unknown driver '" + drvstr + '"');
        }
    }

    @Given("^I am not logged in$")
    public void iAmNotLoggedIn() throws Throwable {
        testConfig.setFakeUser(null);
    }

    @Given("^I am logged in as (.*)$")
    public void iAmLoggedIn(String username) throws Throwable {
        User mockUser = new User();
        mockUser.setName(username);

        testConfig.setFakeUser(mockUser);
    }

    @Given("^the movies$")
    public void withMovies(List<Movie> table) throws Throwable {
        for (Movie m : table)
            movieRepo.save(m);
    }

    @Given("^the cinemas")
    public void withCinemas(List<Cinema> table) throws Throwable {
        for (Cinema m : table)
            cinemaRepo.save(m);
    }

    @Given("^the users")
    public void withUsers(List<User> table) throws Throwable {
        for (User m : table)
            userRepo.save(m);
    }

    @Given("^the rating of User (.*) for Cinema (.*) with (.*) stars and description (.*)$")
    public void withRating(String userName, String cinemaName, int stars, String desc) {
        // FIXME Selecting by id doesn't seem to work as the ids are regenerated
        // when adding to repo. Seems to be only on my machine though (Marco)
        User user = null;
        for (User u : userRepo.findAll()) {
            if (u.getName().equals(userName))
                user = u;
        }

        Cinema cinema = null;
        for (Cinema c : cinemaRepo.findAll()) {
            if (c.getName().equals(cinemaName))
                cinema = c;
        }

        RatedCinemaId id = new RatedCinemaId(user, cinema);
        RatedCinema rCinema = new RatedCinema(id, stars, desc, Calendar.getInstance().getTime());
        rCinemaRepo.save(rCinema);
    }

    @When("^I search for (.*)$")
    public void iSearchFor(String term) throws Throwable {
        WebElement searchBox = driver.findElement(By.className("kino-search-box"));
        searchBox.sendKeys(term);
        searchBox.sendKeys(Keys.ENTER);
    }

    @Then("^the link (.*) should redirect to (.*)$")
    public void linkShouldRedirect(String linkLabel, String urlTail) throws Throwable {
        WebElement link = driver.findElement(By.linkText(linkLabel));
        link.click();

        Assert.assertThat(driver.getCurrentUrl(), StringEndsWith.endsWith(urlTail));
    }

    @When("^I open the start page$")
    public void iOpenTheStartPage() throws Throwable {
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        // Do not use localhost but the real hostname, since the real hostname
        // even works when the selenium node is in a docker container
        // or another (physical or virtual) machine on the local network
        driver.get("http://" + InetAddress.getLocalHost().getHostName() + ":" + port + "/");

        // sanity check to make sure the page is loaded
        driver.findElement(By.xpath("//*[contains(text(), 'smartCinema')]"));
    }

    @When("^I click the button labeled (.*)$")
    public void clickButton(String text) throws Throwable {
        // FIXME: This is actually shit because it will break when the text
        // contains funny characters
        driver.findElement(
                By.xpath("//div[contains(@class, 'v-button') and .//span[contains(text(), '" + text + "')]]")).click();
    }

    @When("^I click the link labeled (.*)$")
    public void clickLink(String text) throws Throwable {
        // FIXME: This is actually shit because it will break when the text
        // contains funny characters
        driver.findElement(By.xpath("//div[contains(@class, 'v-link') and .//span[contains(text(), '" + text + "')]]"))
                .click();
    }

    @Then("^I should see a label containing (.*)$")
    public void iShouldSeeALabelContaining(String text) throws Throwable {
        // FIXME: This is actually shit because it will break when the text
        // contains funny characters
        driver.findElement(By.xpath("//*[contains(text(), '" + text + "')]"));
    }

    @Then("^I should see a button labeled (.*)$")
    public void iShouldSeeAButtonLabeled(String text) throws Throwable {
        // FIXME: This is actually shit because it will break when the text
        // contains funny characters
        driver.findElement(By.xpath("//*[contains(@class, 'v-button') and contains(text(), '" + text + "')]"));
    }

    @Then("^I should not see a link labeled (.*)$")
    public void iShouldNotSeeALinkLabeled(String text) throws Throwable {
        // FIXME: This is actually shit because it will break when the text
        // contains funny characters
        List<WebElement> els = driver
                .findElements(By.xpath("//*[contains(@class, 'v-link') and contains(text(), '" + text + "')]"));
        Assert.assertThat(els, IsEmptyCollection.empty());
    }

    @After
    public void teardown() {
        driver.quit();
        movieRepo.deleteAll();
        cinemaRepo.deleteAll();
    }
}
