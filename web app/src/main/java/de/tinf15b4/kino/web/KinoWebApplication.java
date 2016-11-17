package de.tinf15b4.kino.web;

import de.tinf15b4.kino.data.Cinema;
import de.tinf15b4.kino.data.CinemaRepository;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS) // to make @Transactional work
                                                 // in vaadin views
@EnableJpaRepositories(basePackages = "de.tinf15b4.kino.data")
@EntityScan(basePackages = "de.tinf15b4.kino.data")
@EnableTransactionManagement
@EnableScheduling
@ComponentScan({ "de.tinf15b4.kino.data", "de.tinf15b4.kino.web", "de.tinf15b4.kino.web.*" })
public class KinoWebApplication {
    private static final Logger log = LoggerFactory.getLogger(KinoWebApplication.class);

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
    public CommandLineRunner loadData(CinemaRepository repository) {
        return (args) -> {
            // DEBUG CODE: Add some cinemas to the in-memory database

            // save a couple of customers
            repository.save(new Cinema("Demo Cinema"));
            repository.save(new Cinema("Dummy Cinema #2"));
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(KinoWebApplication.class, args);
    }
}
