package de.tinf15b4.kino.cucumber;

import java.net.InetAddress;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.tinf15b4.kino.web.KinoWebApplication;

@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KinoWebApplication.class)
public class BrowserStepDefinitions {
    private WebDriver driver = null;

    @Value("${local.server.port}")
    private int port;

    public BrowserStepDefinitions() throws Exception {
        // These properties can be set on the gradle command line, e.g.
        // ./gradlew test -Dkinotest.driver=chrome
        // Default is Firefox because it is the only one that just works(TM) for me,
        // the Jenkins setup uses a headless firefox installation.
        String drvstr = System.getProperty("kinotest.driver", "firefox");
        String remote = System.getProperty("kinotest.seleniumHub");
        switch (drvstr) {
            case "firefox":
                driver = new FirefoxDriver();
                break;
            case "chrome":
                driver = new ChromeDriver();
                break;
            case "explorer":
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
        // FIXME! Fake the login bean
    }

    @When("^I open the start page$")
    public void iOpenTheStartPage() throws Throwable {
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // Do not use localhost but the real hostname, since the real hostname
        // even works when the selenium node is in a docker container
        // or another (physical or virtual) machine on the local network
        driver.get("http://" + InetAddress.getLocalHost().getHostName() + ":" + port + "/");

        // sanity check to make sure the page is loaded
        driver.findElement(By.xpath("//*[contains(text(), 'smartCinema')]"));
    }

    @Then("^I should see a label containing (.*)$")
    public void iShouldSeeALabelContaining(String text) throws Throwable {
        //FIXME: This is actually shit because it will break when the text contains funny characters
        driver.findElement(By.xpath("//*[contains(text(), '"+text+"')]"));
    }

    @Then("^I should see a button labeled (.*)$")
    public void iShouldSeeAButtonLabeled(String text) throws Throwable {
        //FIXME: This is actually shit because it will break when the text contains funny characters
        driver.findElement(By.xpath("//*[contains(@class, 'v-button') and contains(text(), '"+text+"')]"));
    }

    @After
    public void teardown() {
        driver.quit();
    }
}
