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


@SpringView(name = AccountView.VIEW_NAME)

public class AccountView extends VerticalLayout implements View {


    public static final String VIEW_NAME = "account";

    @Autowired
    private UserBean userBean;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        this.setMargin(true);
        this.setSpacing(true);

        this.removeAllComponents();

        RestResponse response = userBean.getRestClient().getUser();
        if (response.hasError()) {
            this.getUI().getNavigator().navigateTo(StartView.VIEW_NAME);
        } else {

            User currentUser = (User) response.getValue();

            FormLayout userForm = new FormLayout();
            userForm.setId("accountForm");

            TextField userField = new TextField();
            userField.setCaption("Benutzername");
            userField.setIcon(FontAwesome.USER);
            userField.setRequired(true);
            userField.setValue(currentUser.getName());
            userForm.addComponent(userField);

            TextField emailField = new TextField();
            emailField.setCaption("E-Mail");
            emailField.setIcon(FontAwesome.ENVELOPE);
            emailField.setRequired(true);
            emailField.setValue(currentUser.getEmail());
            userForm.addComponent(emailField);

            Label pwLabel = new Label("Passwort ändern");
            pwLabel.setId("pwLabel");
            userForm.addComponent(pwLabel);

            HorizontalLayout oldPwRow = new HorizontalLayout();
            PasswordField oldPwField = new PasswordField();
            oldPwField.setCaption("Altes Passwort");
            oldPwField.setIcon(FontAwesome.LOCK);
            oldPwRow.addComponent(oldPwField);
            userForm.addComponent(oldPwRow);

            HorizontalLayout newPwRow = new HorizontalLayout();
            PasswordField newPwField = new PasswordField();
            newPwField.setCaption("Neues Passwort (mind. 8 Zeichen)");
            newPwRow.addComponent(newPwField);
            userForm.addComponent(newPwRow);

            HorizontalLayout newPwCheckRow = new HorizontalLayout();
            PasswordField newPwCheckField = new PasswordField();
            newPwCheckField.setCaption("Neues Passwort Wiederholen");
            newPwCheckRow.addComponent(newPwCheckField);
            userForm.addComponent(newPwCheckRow);


            Button saveButton = new Button("Speichern", e -> trySave(
                    userField,
                    emailField,
                    oldPwField,
                    newPwField,
                    newPwCheckField,
                    currentUser));

            userForm.addComponent(saveButton);
            saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

            saveButton.addShortcutListener(new ShortcutListener(null, ShortcutAction.KeyCode.ENTER, null) {
                @Override
                public void handleAction(Object sender, Object target) {
                    saveButton.click();
                }
            });


            this.addComponent(userForm);
        }
    }

    private void trySave(TextField userField, TextField emailField, PasswordField oldPwField, PasswordField newPwField,
                         PasswordField newPwCheckField, User currentUser) {
        if (userBean.isUserLoggedIn()) {

            String userName = userField.getValue();
            String email = emailField.getValue();
            String oldPw = oldPwField.getValue();
            String newPw = newPwField.getValue();
            String newCheckPw = newPwCheckField.getValue();

            boolean changeUsername = false;
            boolean changeEmail = false;
            boolean changePw = false;
            boolean valid = true;

            userField.setComponentError(null);
            emailField.setComponentError(null);
            oldPwField.setComponentError(null);
            newPwField.setComponentError(null);
            newPwCheckField.setComponentError(null);

            // change userName
            if (!userName.equals(currentUser.getName())) {
                if (userName.length() > 99) {
                    userField.setComponentError(new UserError("Der angegebene Benutzername ist zu lang"));
                    valid = false;
                } else {
                    if (userName.length() > 2) {
                        changeUsername = true;
                    } else {
                        userField.setComponentError(new UserError("Bitte überprüfen Sie die Eingabe"));
                        valid = false;
                    }
                }
            }

            // change email
            if (!email.equals(currentUser.getEmail())) {
                if (email.length() > 99) {
                    emailField.setComponentError(new UserError("Die angegebene E-Mailadresse" +
                            " ist zu lang"));
                    valid = false;
                } else {
                    if (email.contains("@") && email.contains(".") && email.length() > 6) {

                        changeEmail = true;
                    } else {
                        emailField.setComponentError(new UserError("Bitte überprüfen Sie die Eingabe"));
                        valid = false;
                    }
                }
            }

            // change PW
            if (!Strings.isNullOrEmpty(oldPw) || !Strings.isNullOrEmpty(newPw)) {
                if (Strings.isNullOrEmpty(oldPw)) {
                    oldPwField.setComponentError(new UserError("Wenn Sie das Passwort ändern möchten," +
                            " müssen Sie das alte Passwort angeben"));
                    valid = false;
                } else if (Strings.isNullOrEmpty(newPw)) {
                    newPwField.setComponentError(new UserError("Wenn Sie das Passwort ändern möchten," +
                            " müssen Sie ein neues Passwort angeben"));
                    valid = false;
                } else if (!oldPw.equals(currentUser.getPassword())) {
                    oldPwField.setComponentError(new UserError("Das angegbene Passwort ist falsch"));
                    valid = false;
                } else if (newPw.length() > 99) {
                    newPwField.setComponentError(new UserError("Das angegebene Passwort ist zu lang"));
                    valid = false;
                } else if (!newPw.equals(newCheckPw)) {
                    newPwField.setComponentError(new UserError("Die angegebenen Passwörter stimmen" +
                            " nicht überein"));
                    valid = false;
                } else if (oldPw.equals(currentUser.getPassword())) {
                    if (newPw.length() > 7) {
                        changePw = true;
                    } else {
                        newPwField.setComponentError(new UserError("Das angegebene Passwort ist zu kurz"));
                        valid = false;
                    }
                }
            }

            if (valid) {
                if (changeUsername) {
                    currentUser.setName(userName);
                }
                if (changeEmail) {
                    currentUser.setEmail(email);
                }
                if (changePw) {
                    currentUser.setPassword(newPw);

                    // clear Passwords after succesful change
                    oldPwField.clear();
                    newPwField.clear();
                    newPwCheckField.clear();
                }


                RestResponse userResponse = userBean.getRestClient().saveUser(currentUser);
                if (!userResponse.hasError()) {
                    ((SmartCinemaUi) getUI()).update();
                    Notification.show("Die Änderungen wurden erfolgreich übernommen", Notification.Type.HUMANIZED_MESSAGE);
                } else {
                    Notification.show(userResponse.getErrorMsg(), Notification.Type.ERROR_MESSAGE);
                }
            }
        }
    }
}
