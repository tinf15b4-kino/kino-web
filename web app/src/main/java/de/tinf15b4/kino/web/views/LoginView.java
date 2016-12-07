package de.tinf15b4.kino.web.views;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import de.tinf15b4.kino.data.users.UserBean;
import de.tinf15b4.kino.web.util.ShortcutUtils;

@SpringView(name = LoginView.VIEW_NAME)
public class LoginView extends Panel implements View {

    @Autowired
    UserBean userBean;

    public static final String VIEW_NAME = "login";
    private Label wrongInput;

    private String redirectTo;

    @PostConstruct
    public void init() {
        setSizeFull();

        FormLayout l = new FormLayout();
        l.setMargin(true);
        l.setSpacing(true);

        TextField usernameInput = new TextField("Benutzername oder E-Mail");
        usernameInput.addStyleName("login-username-field");
        l.addComponent(usernameInput);

        PasswordField passwordInput = new PasswordField("Passwort");
        passwordInput.addStyleName("login-password-field");
        l.addComponent(passwordInput);

        wrongInput = new Label("Benutzername oder Passwort falsch. Bitte erneut eingeben.");
        wrongInput.setVisible(false);
        l.addComponent(wrongInput);

        Button login = new Button("Anmelden", e -> tryLogin(usernameInput.getValue(), passwordInput.getValue()));
        login.addStyleName("login-submit-button");
        login.addStyleName(ValoTheme.BUTTON_PRIMARY);
        l.addComponent(login);

        ShortcutUtils.registerScopedShortcut(this, login, ShortcutAction.KeyCode.ENTER);

        this.setContent(l);
        this.addStyleName(ValoTheme.PANEL_BORDERLESS);
    }

    private void tryLogin(String username, String password) {
        if (userBean.login(username, password)) {
            if (redirectTo != null) {
                getUI().getPage().open(redirectTo, "");
            } else {
                getUI().getNavigator().navigateTo("");
            }
        } else {
            wrongInput.setVisible(true);
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        redirectTo = null;
        if (event.getParameters() != null && !event.getParameters().isEmpty()) {
            try {
                redirectTo = URLDecoder.decode(event.getParameters(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // Well, I guess then we won't redirect. Tough luck.
            }
        }
    }

}
