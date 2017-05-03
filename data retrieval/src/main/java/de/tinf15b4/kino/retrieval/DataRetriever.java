package de.tinf15b4.kino.retrieval;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import de.tinf15b4.kino.retrieval.scraper.AbstractCinemaScraper;

public class DataRetriever {

    private List<AbstractCinemaScraper> scrapers;

    public DataRetriever() {
        scrapers = new ArrayList<>();
        loadScrapers();
    }

    private void loadScrapers() {
        Reflections reflections = new Reflections("de.tinf15b4");
        Set<Class<? extends AbstractCinemaScraper>> scraperClazzes = reflections
                .getSubTypesOf(AbstractCinemaScraper.class);
        for (Class<? extends AbstractCinemaScraper> clazz : scraperClazzes) {
            try {
                scrapers.add(clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void runScrapers() {
        scrapers.forEach(scraper -> scraper.scrape());
    }

}
