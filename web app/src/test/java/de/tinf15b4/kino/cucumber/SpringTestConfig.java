package de.tinf15b4.kino.cucumber;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.context.annotation.SessionScope;

import de.tinf15b4.kino.data.initializer.DataInitializer;
import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.data.users.UserBean;


@TestConfiguration
public class SpringTestConfig {
    private User fakeLogin;

    public void setFakeUser(User u) {
        fakeLogin = u;
    }

    public User getFakeUser() {
        return fakeLogin;
    }

    @Bean
    @Primary
    @SessionScope
    public UserBean possiblyMockedUserBean() {
        if (fakeLogin != null) {
            UserBean b = Mockito.mock(UserBean.class);
            Mockito.doReturn(true).when(b).isUserLoggedIn();
            Mockito.doReturn(fakeLogin).when(b).getCurrentUser();
            return b;
        } else {
            return new UserBean();
        }
    }

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
