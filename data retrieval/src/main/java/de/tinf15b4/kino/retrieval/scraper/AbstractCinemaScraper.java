package de.tinf15b4.kino.retrieval.scraper;

import java.net.URL;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;

public abstract class AbstractCinemaScraper {

    protected RemoteWebDriver driver;

    public AbstractCinemaScraper() throws Exception {
        String drvstr = System.getProperty("kinotest.driver");
        String remote = System.getProperty("kinotest.seleniumHub");

        if (drvstr == null || drvstr.isEmpty())
            drvstr = "firefox";

        switch (drvstr) {
        case "firefox":
            FirefoxDriverManager.getInstance().setup("0.16.0");
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

    public abstract void gatherData();

}
