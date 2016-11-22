package de.tinf15b4.kino.web.views;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import de.tinf15b4.kino.data.users.UserLoginBean;
import de.tinf15b4.kino.web.util.EnterKeyListener;

@SpringView(name = LoginView.VIEW_NAME)
public class LoginView extends GridLayout implements View {

    @Autowired
    UserLoginBean userLoginBean;

    public static final String VIEW_NAME = "login";
    private Label wrongInput;

    @PostConstruct
    public void init() {
        setSizeFull();
        setMargin(true);
        setSpacing(true);
        setColumns(2);
        setRows(4);

        setColumnExpandRatio(1, 1);
        setRowExpandRatio(3, 1);

        Label username = new Label("Benutzername oder Email:");
        username.setSizeUndefined();
        TextField usernameInput = new TextField();

        addComponent(username, 0, 0);
        addComponent(usernameInput, 1, 0);

        Label password = new Label("Passwort:");
        password.setSizeUndefined();
        PasswordField passwordInput = new PasswordField();
        addComponent(password, 0, 1);
        addComponent(passwordInput, 1, 1);

        wrongInput = new Label("Benutzername oder Passwort falsch. Bitte erneut eingeben");
        wrongInput.setVisible(false);
        addComponent(wrongInput, 1, 2);

        EnterKeyListener listener = new EnterKeyListener() {

            @Override
            public void onEnterKeyPressed() {
                tryLogin(usernameInput.getValue(), passwordInput.getValue());
            }
        };
        listener.installOn(passwordInput);

        Button login = new Button("Anmelden", e -> tryLogin(usernameInput.getValue(), passwordInput.getValue()));
        addComponent(login, 0, 3, 1, 3);
    }

    private void tryLogin(String username, String password) {
        if (userLoginBean.login(username, password)) {
            getUI().getNavigator().navigateTo(Views.DEFAULT.getViewId());
        } else {
            wrongInput.setVisible(true);
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

}
