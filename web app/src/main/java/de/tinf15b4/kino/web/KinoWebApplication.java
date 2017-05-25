package de.tinf15b4.kino.web;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.tinf15b4.kino.web.rest.RestApiUrlSource;

@SpringBootApplication
@Configuration
@EnableScheduling
@ComponentScan({ "de.tinf15b4.kino.web", "de.tinf15b4.kino.web.*" })
public class KinoWebApplication {

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        JettyEmbeddedServletContainerFactory jetty = new JettyEmbeddedServletContainerFactory();
        jetty.addServerCustomizers(KinoWebApplication::jettyEnableInherit);
        return jetty;
    }

    @Bean
    public ServletRegistrationBean publicRestApiProxy(RestApiUrlSource apiUrl) {
        // We proxy the public rest API here in case we are firewalled and no proper reverse proxy has been set up
        // A proper reverse proxy will map the rest APIs entrypoints below /rest on the web app, exactly as we're doing here.
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new ProxyServlet(), "/rest/*");
        servletRegistrationBean.addInitParameter("targetUri", apiUrl.getUrl() + "/rest");
        servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG, "false");
        return servletRegistrationBean;
    }

    private static void jettyEnableInherit(Server server) {
        for (Connector c : server.getConnectors()) {
            if (c instanceof ServerConnector) {
                ((ServerConnector) c).setInheritChannel(true);
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(KinoWebApplication.class, args);
    }
}
