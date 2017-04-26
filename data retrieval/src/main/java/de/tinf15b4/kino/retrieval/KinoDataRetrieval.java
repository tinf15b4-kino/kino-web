package de.tinf15b4.kino.retrieval;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.tinf15b4.kino.retrieval.scraper.KinemathekScraper;

@SpringBootApplication
@Configuration
@EnableScheduling
public class KinoDataRetrieval {

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        JettyEmbeddedServletContainerFactory jetty = new JettyEmbeddedServletContainerFactory();
        jetty.addServerCustomizers((JettyServerCustomizer) server -> jettyEnableInherit(server));
        return jetty;
    }

    private static void jettyEnableInherit(Server server) {
        for (Connector c : server.getConnectors()) {
            if (c instanceof ServerConnector) {
                ((ServerConnector) c).setInheritChannel(true);
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(KinoDataRetrieval.class, args);

        DataRetriever retriever = new DataRetriever();
        registerScrapers(retriever);
    }

    private static void registerScrapers(DataRetriever retriever) {
        try {
            retriever.registerScraper(new KinemathekScraper());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
