package de.tinf15b4.kino.retrieval;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tinf15b4.kino.retrieval.scraper.AbstractCinemaScraper;

public class DataRetriever {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataRetriever.class);

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
                LOGGER.error("Loading scrapers failed", e);
            }
        }
    }

    public void runScrapers() {
        scrapers.forEach(AbstractCinemaScraper::scrape);
    }

}
