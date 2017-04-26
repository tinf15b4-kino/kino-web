package de.tinf15b4.kino.retrieval;

import java.util.ArrayList;
import java.util.List;

import de.tinf15b4.kino.retrieval.scraper.AbstractCinemaScraper;

public class DataRetriever {

    List<AbstractCinemaScraper> scrapers;

    public DataRetriever() {
        scrapers = new ArrayList<>();
    }

    public void registerScraper(AbstractCinemaScraper scraper) {
        scrapers.add(scraper);
    }

    public void runScrapers() {
        for (AbstractCinemaScraper scraper : scrapers) {
            scraper.gatherData();
        }
    }

}
