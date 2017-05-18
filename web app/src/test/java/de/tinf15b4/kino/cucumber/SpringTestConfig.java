package de.tinf15b4.kino.cucumber;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLDecoder;
import java.util.stream.Collectors;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import de.tinf15b4.kino.web.rest.RestApiUrlSource;

@TestConfiguration
public class SpringTestConfig {
    private static String startedServerUrl = null;

    private static void startServer() throws Exception {
        if (startedServerUrl != null)
            return;

        System.setProperty("spring.datasource.url", "jdbc:h2:file:~/smartCinemaDataBase/smartcinemaTest");
        System.setProperty("spring.jpa.hibernate.ddl-auto", "create-drop");

        String datadir = System.getenv("SMARTCINEMA_DATA_API_DIR");
        if (datadir == null) {
            // employ some hacks to find it ...
            String path = BrowserStepDefinitions.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");

            // crawl upwards until we find settings.gradle
            File f = new File(decodedPath).getParentFile();
            while (!new File(f, "settings.gradle").exists() && f.getParentFile() != null)
                f = f.getParentFile();

            // then downwards to the data dir
            datadir = new File(f, "data api").getAbsolutePath();
        }

        int port = 0;
        try (ServerSocket s = new ServerSocket(0)) {
            port = s.getLocalPort();
        }

        String command[];
        if (System.getProperty("os.name").startsWith("Windows")) {
            command = new String[] { "..\\gradlew.bat", "bootRun" };
        } else {
            command = new String[] { "../gradlew", "bootRun" };
        }
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(new File(datadir));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectInput(ProcessBuilder.Redirect.PIPE);
        pb.environment().put("SMARTCINEMA_DATA_API_LISTEN_ON", "" + port);
        pb.environment().put("SMARTCINEMA_DATA_API_KEEPALIVE_PIPE", "yeah");
        Process p = pb.start();

        for (int i = 0; i < 60; ++i) {
            System.err.println("DEBUG: Waiting for temporary data api server to come online (port " + port + ")");

            try {
                HttpURLConnection conn = (HttpURLConnection) new URL("http://localhost:" + port + "/rest/ping").openConnection();
                try {
                    conn.setRequestMethod("GET");
                    try (InputStream is = conn.getInputStream(); BufferedReader r = new BufferedReader(new InputStreamReader(is))) {
                        String result = r.lines().parallel().collect(Collectors.joining("\n"));

                        if (result.trim().equals("pong"))
                            break;
                    }
                } finally {
                    conn.disconnect();
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }

            Thread.sleep(1000);
        }

        System.err.println("DEBUG: Started temporary data api server on port " + port);
        startedServerUrl = "http://localhost:" + port;
    }

    @Bean
    @Primary
    public RestApiUrlSource getTestRestServer() throws Exception {
        // Start instance of rest api
        if (System.getenv("SMARTCINEMA_API_URL") == null) {
            startServer();

            return new RestApiUrlSource() {
                @Override
                public String getUrl() {
                    return SpringTestConfig.startedServerUrl;
                }
            };
        } else {
            return new RestApiUrlSource() {
                @Override
                public String getUrl() {
                    return System.getenv("SMARTCINEMA_API_URL");
                }
            };
        }
    }
}
