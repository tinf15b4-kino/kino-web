package de.tinf15b4.kino.cucumber;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaRepository;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.favorites.FavoriteRepository;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieRepository;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.data.playlists.PlaylistRepository;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinema;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaId;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaRepository;
import de.tinf15b4.kino.data.ratedmovies.RatedMovie;
import de.tinf15b4.kino.data.ratedmovies.RatedMovieId;
import de.tinf15b4.kino.data.ratedmovies.RatedMovieRepository;
import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.data.users.UserRepository;
import de.tinf15b4.kino.web.KinoWebApplication;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.core.StringEndsWith;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ContextConfiguration(classes = SpringTestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { KinoWebApplication.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
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
    private RatedMovieRepository rMovieRepo;

    @Autowired
    private PlaylistRepository playlistRepo;

    @Autowired
    private FavoriteRepository faveRepo;

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
            FirefoxDriverManager.getInstance().setup("0.11.1");
            driver = new FirefoxDriver();
            break;
        case "chrome":
            ChromeDriverManager.getInstance().setup("2.25");
            driver = new ChromeDriver();
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

    @Given("^I am logged in as \"([^\\\"]*)\"$")
    public void iAmLoggedIn(String username) throws Throwable {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setName(username);

        userRepo.save(mockUser);

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

    @Given("^the rating of User \"([^\\\"]*)\" for Cinema \"([^\\\"]*)\" with (.*) stars and description \"([^\\\"]*)\"$")
    public void withCinemaRating(String userName, String cinemaName, int stars, String desc) {
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

    @Given("^the rating of User \"([^\\\"]*)\" for Movie \"([^\\\"]*)\" with (.*) stars and description \"([^\\\"]*)\"$")
    public void withMovieRating(String userName, String movieName, int stars, String desc) {
        // FIXME Selecting by id doesn't seem to work as the ids are regenerated
        // when adding to repo. Seems to be only on my machine though (Marco)
        User user = null;
        for (User u : userRepo.findAll()) {
            if (u.getName().equals(userName))
                user = u;
        }

        Movie movie = null;
        for (Movie m : movieRepo.findAll()) {
            if (m.getName().equals(movieName))
                movie = m;
        }

        RatedMovieId id = new RatedMovieId(user, movie);
        RatedMovie rMovie = new RatedMovie(id, stars, desc, Calendar.getInstance().getTime());
        rMovieRepo.save(rMovie);
    }

    private static class FaveCinemaTableRow {
        public String user;
        public long cinema;
    }

    @Given("^the favorite cinemas")
    public void withFavoriteCinemas(List<FaveCinemaTableRow> faves) {
        for (FaveCinemaTableRow row : faves) {
            Favorite f = new Favorite(userRepo.findByName(row.user), cinemaRepo.findOne(row.cinema));
            faveRepo.save(f);
        }
    }

    @Given("^movie (.*) is played in cinema (.*) for (.*) cents")
    public void withPlayist(long movieId, long cinemaId, int price) {
        Playlist p = new Playlist();
        p.setCinema(cinemaRepo.findOne(cinemaId));
        p.setMovie(movieRepo.findOne(movieId));
        p.setPrice(price);
        p.setTime(new Date(new Date().getTime() + 1000L * 3600));
        playlistRepo.save(p);
        playlistRepo.findAll();
    }

    @When("^I search for \"([^\\\"]*)\"$")
    public void iSearchFor(String term) throws Throwable {
        typeInto(term, "#cinemaSearchBox");
        sendKey("RETURN");
    }

    @When("^I type \"([^\"]+)\" into \"([^\"]+)\"$")
    public void typeInto(String input, String selector) throws Throwable {
        WebElement box = driver.findElement(By.cssSelector(selector));
        // HACK: Focus element with javascript, this is needed on linux firefox
        ((JavascriptExecutor) driver)
                .executeScript("try { window.focus(); arguments[0].focus() } catch(e) {}; return null;", box);
        box.sendKeys(input);
    }

    @When("^I type \"([^\"]+)\"$")
    public void type(String input) throws Throwable {
        WebElement el = driver.switchTo().activeElement();
        // HACK: Focus element with javascript, this is needed on linux firefox
        ((JavascriptExecutor) driver)
                .executeScript("try { window.focus(); arguments[0].focus() } catch(e) {}; return null;", el);
        el.sendKeys(input);
    }

    @When("^I press (RETURN|ENTER|TAB)$")
    public void sendKey(String input) throws Throwable {
        WebElement box = driver.switchTo().activeElement();
        switch (input) {
        case "RETURN":
            box.sendKeys(Keys.RETURN);
            break;
        case "ENTER":
            box.sendKeys(Keys.ENTER);
            break;
        case "TAB":
            box.sendKeys(Keys.TAB);
            break;
        }
    }

    @Then("^the link \"([^\\\"]*)\" should redirect to \"([^\\\"]*)\"$")
    public void linkShouldRedirect(String linkLabel, String urlTail) throws Throwable {
        WebElement link = driver.findElement(By.linkText(linkLabel));
        link.click();

        Assert.assertThat(driver.getCurrentUrl(), StringEndsWith.endsWith(urlTail));
    }

    @Then("^the current URL should be \"([^\\\"]*)\"$")
    public void currentUrlShouldBe(String urlTail) throws Throwable {
        // HACK: Possibly wait a little bit
        for (int i = 0; i < 100; ++i) {
            if (driver.getCurrentUrl().endsWith(urlTail))
                break;

            Thread.sleep(10);
        }
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

    @When("^I click the button labeled \"([^\\\"]*)\"$")
    public void clickButton(String text) throws Throwable {
        // FIXME: This is actually shit because it will break when the text
        // contains funny characters
        driver.findElement(
                By.xpath("//div[contains(@class, 'v-button') and .//span[contains(text(), '" + text + "')]]")).click();
    }

    @When("^I click the menu item labeled \"([^\\\"]*)\"$")
    public void clickMenuitem(String text) throws Throwable {
        // FIXME: This is actually shit because it will break when the text
        // contains funny characters
        driver.findElement(
                By.xpath("//*[contains(@class, 'v-menubar-menuitem') and .//*[contains(text(), '" + text + "')]]"))
                .click();
    }

    @When("^I click the menu item labeled \"([^\\\"]*)\" below \"([^\"]+)\"$")
    public void clickMenuitemBelow(String text, String selector) throws Throwable {
        // FIXME: This is actually shit because it will break when the text
        // contains funny characters
        driver.findElement(By.cssSelector(selector))
                .findElement(By.xpath(
                        ".//*[contains(@class, 'v-menubar-menuitem') and .//*[contains(text(), '" + text + "')]]"))
                .click();
    }

    @When("^I click the button labeled \"([^\\\"]*)\" below \"([^\"]+)\"$")
    public void clickButtonBelow(String text, String selector) throws Throwable {
        // FIXME: This is actually shit because it will break when the text
        // contains funny characters
        WebElement button = driver.findElement(By.cssSelector(selector))
                .findElement(By.xpath(".//*[contains(@class, 'v-button') and .//*[contains(text(), '" + text + "')]]"));

        button.click();
    }

    @When("^I wait until an element containing \"([^\"]+)\" appears$")
    public void waitUntilLabelAppears(String text) throws Throwable {
        new WebDriverWait(driver, 10000)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), '" + text + "')]")));
    }

    @When("^I wait until every element containing \"([^\"]+)\" disappears")
    public void waitUntilLabelDisappears(String text) throws Throwable {
        new WebDriverWait(driver, 10000).until(
                ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[contains(text(), '" + text + "')]")));
    }

    @When("^I click on \"([^\"]+)\"$")
    public void clickOn(String selector) throws Throwable {
        driver.findElement(By.cssSelector(selector)).click();
    }

    @When("^I click the link labeled \"([^\\\"]*)\"$")
    public void clickLink(String text) throws Throwable {
        // FIXME: This is actually shit because it will break when the text
        // contains funny characters
        driver.findElement(By.xpath("//div[contains(@class, 'v-link') and .//span[contains(text(), '" + text + "')]]"))
                .click();
    }

    @When("^I wait until a label containing \"([^\"]+)\" is visible$")
    public void waitForLabel(String content) throws Throwable {
        new WebDriverWait(driver, 10).until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + content + "')]")));
    }

    @When("^I toggle checkbox filter \"([^\\\"]*)\"$")
    public void addFilter(String filter) {
        WebElement checkbox = driver.findElement(
                By.xpath(".//input[@type = 'checkbox' and ../label[contains(text(), '" + filter + "') ]]"));
        // FIXME: This fails because of stupid label::before covering the
        // checkbox. I can't seem to find a solution for this issue. Focusing
        // the checkbox and selecting it by space does not work either
        checkbox.click();
    }

    @When("^I enter \"([^\\\"]*)\" in the (lower-price|upper-price) filter$")
    public void enterFilter(String filterString, String filterId) {
        WebElement filterField = driver.findElement(By.id(filterId));
        filterField.clear();
        filterField.sendKeys(filterString);
        // Wait two second for the live filtering to catch up
        try {
            Thread.sleep(2000);
        } catch (Exception ex) {
        }
    }

    @Then("^I should see a label containing \"([^\\\"]*)\"$")
    public void iShouldSeeALabelContaining(String text) throws Throwable {
        // FIXME: This is actually shit because it will break when the text
        // contains funny characters
        driver.findElement(By.xpath("//*[contains(text(), '" + text + "')]"));
    }

    @Then("^I should see a label containing \"([^\\\"]*)\" below \"([^\"]+)\"$")
    public void iShouldSeeALabelContainingBelow(String text, String selector) throws Throwable {
        // FIXME: This is actually shit because it will break when the text
        // contains funny characters
        driver.findElement(By.cssSelector(selector)).findElement(By.xpath(".//*[contains(text(), '" + text + "')]"));
    }

    @Then("^I should see a button labeled \"([^\\\"]*)\"$")
    public void iShouldSeeAButtonLabeled(String text) throws Throwable {
        // FIXME: This is actually shit because it will break when the text
        // contains funny characters
        driver.findElement(By.xpath("//*[contains(@class, 'v-button') and contains(text(), '" + text + "')]"));
    }

    @Then("^I should not see a link labeled \"([^\\\"]*)\"$")
    public void iShouldNotSeeALinkLabeled(String text) throws Throwable {
        // FIXME: This is actually shit because it will break when the text
        // contains funny characters
        List<WebElement> els = driver
                .findElements(By.xpath("//*[contains(@class, 'v-link') and contains(text(), '" + text + "')]"));
        Assert.assertThat(els, IsEmptyCollection.empty());
    }

    @Then("^I should not see a label containing \"([^\"]+)\"$")
    public void iShouldNotSeeALabel(String text) {
        // FIXME: This is actually shit because it will break when the text
        // contains funny characters
        List<WebElement> els = driver.findElements(By.xpath("//*[contains(text(), '" + text + "')]"));
        Assert.assertThat(els, IsEmptyCollection.empty());
    }

    private void elementsAreAlignedHelper(String selector, String leftright) throws Exception {
        long pos = Long.MIN_VALUE;

        List<WebElement> els = driver.findElements(By.cssSelector(selector));
        for (WebElement el : els) {
            long npos = Long.MIN_VALUE;
            if (leftright.equals("left")) {
                npos = el.getLocation().getX();
            } else {
                npos = el.getLocation().getX() + el.getSize().getWidth();
            }

            if (pos > Long.MIN_VALUE && pos != npos) {
                throw new Exception("Element coordinates differ: " + pos + "px vs " + npos + "px");
            }
            pos = npos;
        }
    }

    @Then("^all elements matching \"([^\"]+)\" are (right|left)-aligned at ((?:\\d+x\\d+,)*\\d+x\\d+)$")
    public void elementsAreAligned(String selector, String leftright, String s) throws Throwable {
        ArrayList<Dimension> sizes = new ArrayList<>();

        for (String r : s.split(",")) {
            String wh[] = r.split("x");
            sizes.add(new Dimension(Integer.parseInt(wh[0]), Integer.parseInt(wh[1])));
        }

        sizes.add(driver.manage().window().getSize()); // restore size at the
                                                       // end

        for (Dimension size : sizes) {
            driver.manage().window().setSize(size);

            // HACK: Sometimes, the window needs a bit of time to adjust,
            // but most of the time waiting a second would just be a waste of
            // time.
            // I don't know how to actually solve this race condition, so if the
            // test fails, we'll just wait a second and run it again.
            try {
                elementsAreAlignedHelper(selector, leftright);
            } catch (Exception e) {
                try {
                    Thread.sleep(10000);
                    elementsAreAlignedHelper(selector, leftright);
                } catch (Exception ex) {
                    throw new Exception("Alignment mismatch at size " + size.getWidth() + "x" + size.getHeight(), ex);
                }
            }
        }
    }

    @Then("the database should have saved cinema (\\d+) as favorite")
    public void favoriteIsInDb(long cinemaId) {
        Assert.assertNotNull(faveRepo.findFavorite(cinemaRepo.findOne(cinemaId), testConfig.getFakeUser()));
    }

    @Then("the database should not have saved cinema (\\d+) as favorite")
    public void favoriteIsNotInDb(long cinemaId) {
        Assert.assertNull(faveRepo.findFavorite(cinemaRepo.findOne(cinemaId), testConfig.getFakeUser()));
    }

    @After
    public void teardown() {
        driver.quit();
    }
}
