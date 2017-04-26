package de.tinf15b4.kino.web;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.jsondoc.spring.boot.starter.EnableJSONDoc;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.tinf15b4.kino.data.initializer.DataInitializer;

@SpringBootApplication
@Configuration
@EnableJpaRepositories(basePackages = "de.tinf15b4.kino.data.*")
@EntityScan(basePackages = "de.tinf15b4.kino.data.*")
@EnableScheduling
@EnableJSONDoc
@ComponentScan({ "de.tinf15b4.kino.data.*", "de.tinf15b4.kino.web", "de.tinf15b4.kino.api.*" })
public class KinoWebDataService {

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

    @Bean
    public CommandLineRunner loadData(DataInitializer initializer) {
        return (args) -> {
            initializer.initialize();
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(KinoWebDataService.class, args);
    }
}
