package de.tinf15b4.kino.web.views;

import com.google.common.base.Strings;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.UserError;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.web.rest.RestResponse;
import de.tinf15b4.kino.web.ui.SmartCinemaUi;
import de.tinf15b4.kino.web.user.UserBean;
import org.springframework.beans.factory.annotation.Autowired;

@SpringView(name = RegisterView.VIEW_NAME)
public class RegisterView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "register";

    @Autowired
    private UserBean userBean;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        this.setMargin(true);
        this.setSpacing(true);

        this.removeAllComponents();

        FormLayout userForm = new FormLayout();
        userForm.setId("registerForm");

        TextField userField = new TextField();
        userField.setCaption("Benutzername");
        userField.setIcon(FontAwesome.USER);
        userField.setRequired(true);
        userForm.addComponent(userField);

        TextField emailField = new TextField();
        emailField.setCaption("E-Mail");
        emailField.setIcon(FontAwesome.ENVELOPE);
        emailField.setRequired(true);
        userForm.addComponent(emailField);

        Label pwLabel = new Label("Passwort festlegen");
        pwLabel.setId("pwLabel");
        userForm.addComponent(pwLabel);

        HorizontalLayout newPwRow = new HorizontalLayout();
        PasswordField newPwField = new PasswordField();
        newPwField.setCaption("Passwort (mind. 8 Zeichen)");
        newPwRow.addComponent(newPwField);
        userForm.addComponent(newPwRow);

        HorizontalLayout newPwCheckRow = new HorizontalLayout();
        PasswordField newPwCheckField = new PasswordField();
        newPwCheckField.setCaption("Passwort Wiederholen");
        newPwCheckRow.addComponent(newPwCheckField);
        userForm.addComponent(newPwCheckRow);

        Button registerButton = new Button("Registrieren",
                e -> tryRegister(userField, emailField, newPwField, newPwCheckField));

        userForm.addComponent(registerButton);
        registerButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

        registerButton.addShortcutListener(new ShortcutListener(null, ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                registerButton.click();
            }
        });

        this.addComponent(userForm);
    }

    private void tryRegister(TextField userField, TextField emailField, PasswordField newPwField,
                             PasswordField newPwCheckField) {

        String userName = userField.getValue();
        String email = emailField.getValue();
        String newPw = newPwField.getValue();
        String newCheckPw = newPwCheckField.getValue();

        boolean valid = true;


        userField.setComponentError(null);
        emailField.setComponentError(null);
        newPwField.setComponentError(null);
        newPwCheckField.setComponentError(null);

        // userName
        String userNameErrorMsg = checkUsername(userName);
        if (!Strings.isNullOrEmpty(userNameErrorMsg)) {
            userField.setComponentError(new UserError(userNameErrorMsg));
            valid = false;
        }

        // email
        String emailErrorMsg = checkEmail(email);
        if (!Strings.isNullOrEmpty(emailErrorMsg)) {
            emailField.setComponentError(new UserError(emailErrorMsg));
            valid = false;
        }

        // PW
        String pwErrorMsg = checkNewPassword(newPw, newCheckPw);
        if (!Strings.isNullOrEmpty(pwErrorMsg)) {
            newPwField.setComponentError(new UserError(pwErrorMsg));
            valid = false;
        }

        if (valid) {
            performRegistration(userName, email, newPw);
            // clear Passwords after change (no matter whether successful or not)
            newPwField.clear();
            newPwCheckField.clear();
        }
    }

    private String checkUsername(String userName) {
        if (userName.length() > 99) {
            return "Der angegebene Benutzername ist zu lang";
        } else {
            if (userName.length() <= 2) {
                return "Bitte überprüfen Sie die Eingabe";
            }
        }
        return "";
    }

    private String checkEmail(String email) {
        if (email.length() > 99) {
            return "Die angegebene E-Mailadresse ist zu lang";
        } else {
            if (!email.contains("@") && !email.contains(".") && email.length() <= 6) {
                return "Bitte überprüfen Sie die Eingabe";
            }
        }
        return "";
    }

    private String checkNewPassword(String newPw, String newCheckPw) {
        if (Strings.isNullOrEmpty(newPw)) {
            return "Sie müssen ein neues Passwort angeben";
        } else if (newPw.length() > 99) {
            return "Das angegebene Passwort ist zu lang";
        } else if (!newPw.equals(newCheckPw)) {
            return "Die angegebenen Passwörter stimmen nicht überein";
        } else if (newPw.length() <= 7) {
            return "Das angegebene Passwort ist zu kurz";
        }
        return "";
    }

    private void performRegistration(String userName, String email, String newPw) {

        User newUser = new User();
        newUser.setName(userName);
        newUser.setEmail(email);
        newUser.setPassword(newPw);

        RestResponse userResponse = userBean.getRestClient().saveUser(newUser);
        if (!userResponse.hasError()) {
            ((SmartCinemaUi) getUI()).update();
            Notification.show("Registrierung erfolgreich abgeschlossen!", Notification.Type.HUMANIZED_MESSAGE);
        } else {
            Notification.show(userResponse.getErrorMsg(), Notification.Type.ERROR_MESSAGE);
        }
    }
}
