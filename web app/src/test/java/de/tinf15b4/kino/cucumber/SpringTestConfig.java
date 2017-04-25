package de.tinf15b4.kino.cucumber;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import de.tinf15b4.kino.data.initializer.DataInitializer;

@TestConfiguration
public class SpringTestConfig {

    @Bean
    @Primary
    public DataInitializer emptyMemoryDbDataInitializer() {
        return new DataInitializer() {
            @Override
            public void initialize() {
                // Do nothing
            }
        };
    }
}
