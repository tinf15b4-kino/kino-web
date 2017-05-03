package de.tinf15b4.kino.api;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.jsondoc.spring.boot.starter.EnableJSONDoc;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.tinf15b4.kino.data.initializer.DataInitializer;

@SpringBootApplication
@Configuration
@EnableJpaRepositories(basePackages = "de.tinf15b4.kino.data.*")
@EntityScan(basePackages = "de.tinf15b4.kino.data.*")
@EnableScheduling
@EnableJSONDoc
@ComponentScan({ "de.tinf15b4.kino.data.*", "de.tinf15b4.kino.api", "de.tinf15b4.kino.api.*" })
public class KinoWebDataService extends WebMvcConfigurerAdapter {
    public final org.slf4j.Logger logger = LoggerFactory.getLogger(KinoWebDataService.class);

    private class KillOnTimeoutInterceptor extends HandlerInterceptorAdapter {
        private Timer timer = new Timer();
        private int timeout;
        private TimerTask task;

        public KillOnTimeoutInterceptor(int timeout) {
            this.timeout = timeout;
            this.resetTimeout();
        }

        // the "synchronized" thing is very important here! Even though the
        // timer classes claim to be thread-safe, they apparently aren't.
        private synchronized void resetTimeout() {
            if (this.task != null)
                this.task.cancel();

            this.task = new TimerTask() {
                @Override
                public void run() {
                    logger.info("Exiting because of inactivity timeout");
                    System.exit(0);
                }
            };
            this.timer.schedule(this.task, this.timeout);
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            this.resetTimeout();
            return super.preHandle(request, response, handler);
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);

        // HACK: quit after timeout
        // We should invent something better soonish
        if (System.getenv("SMARTCINEMA_DATA_API_KEEPALIVE_PIPE") != null) {
            logger.info("Installing interceptor to quit after inactivity");
            registry.addInterceptor(new KillOnTimeoutInterceptor(60000));
        }

    }

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
        // HACK: Passing java system properties is hard, environment variables are easy
        String port = System.getenv("SMARTCINEMA_DATA_API_LISTEN_ON");
        if (port != null) {
            System.setProperty("server.port", port);
        }

        SpringApplication.run(KinoWebDataService.class, args);
    }
}
