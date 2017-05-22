package de.tinf15b4.kino.web.views;

import com.google.common.base.Strings;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


@SpringView(name = AccountView.VIEW_NAME)

public class AccountView extends VerticalLayout implements View {


    public static final String VIEW_NAME = "account";

    @Autowired
    private UserBean userBean;

    private String redirectTo;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        this.setMargin(true);
        this.setSpacing(true);

        this.removeAllComponents();

        RestResponse response = userBean.getRestClient().getUser();
        if (response.hasError()) {
            this.getUI().getNavigator().navigateTo(StartView.VIEW_NAME);
        } else {

            redirectTo = null;
            if (event.getParameters() != null && !event.getParameters().isEmpty()) {
                try {
                    redirectTo = URLDecoder.decode(event.getParameters(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // Well, I guess then we won't redirect. Tough luck.
                }
            }

            User currentUser = (User) response.getValue();

            FormLayout userForm = new FormLayout();
            userForm.setId("accountForm");

            TextField userField = new TextField("Benutzername");
            userField.setRequired(true);
            userField.setValue(currentUser.getName());
            userForm.addComponent(userField);

            TextField emailField = new TextField("E-Mail");
            emailField.setRequired(true);
            emailField.setValue(currentUser.getEmail());
            userForm.addComponent(emailField);

            Label pwLabel = new Label("Passwort ändern");
            pwLabel.setId("pwLabel");
            userForm.addComponent(pwLabel);

            HorizontalLayout oldPwRow = new HorizontalLayout();
            PasswordField oldPwField = new PasswordField();
            oldPwField.setCaption("Altes Passwort");
            oldPwRow.addComponent(oldPwField);
            userForm.addComponent(oldPwRow);

            HorizontalLayout newPwRow = new HorizontalLayout();
            PasswordField newPwField = new PasswordField();
            newPwField.setCaption("Neues Passwort");
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


            this.addComponent(userForm);
        }
    }

    private void trySave(TextField userField, TextField emailField, PasswordField oldPwField, PasswordField newPwField,
                         PasswordField newPwCheckField, User currentUser){
        if (userBean.isUserLoggedIn()){

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
            if (!userName.equals(currentUser.getName())){
                if (userName.length() > 2){
                    changeUsername = true;
                }
                else {
                    userField.setComponentError(new UserError("Bitte Überprüfen Sie die Eingabe"));
                    valid = false;
                }
            }

            // change email
            if (!email.equals(currentUser.getEmail())){
                if (email.contains("@") && email.contains(".") && email.length() > 6){

                    changeEmail = true;
                }
                else{
                    emailField.setComponentError(new UserError("Bitte Überprüfen Sie die Eingabe"));
                    valid = false;
                }
            }

            // change PW
            if (!Strings.isNullOrEmpty(oldPw) || !Strings.isNullOrEmpty(newPw)){
                if (Strings.isNullOrEmpty(oldPw)){
                    oldPwField.setComponentError(new UserError("Wenn Sie das Passwort ändern möchten," +
                            " müssen Sie das alte Passwort angeben"));
                    valid = false;
                }
                else if (Strings.isNullOrEmpty(newPw)){
                    newPwField.setComponentError(new UserError("Wenn Sie das Passwort ändern möchsten," +
                            " müssen Sie ein neues Passwort angeben"));
                    valid = false;
                }
                else if (!oldPw.equals(currentUser.getPassword())){
                    oldPwField.setComponentError(new UserError("Das eingegbene Passwort ist Falsch"));
                    valid = false;
                }
                else if (!newPw.equals(newCheckPw)){
                    newPwField.setComponentError(new UserError("Die Angegebenen Passwörter stimmen" +
                            " nicht überein"));
                    valid = false;
                }
                else if (oldPw.equals(currentUser.getPassword())){
                    if (newPw.length() > 7) {
                        changePw = true;
                    }
                    else {
                        newPwField.setComponentError(new UserError("Das angegebene Passwort ist zu kurz"));
                        valid = false;
                    }
                }
            }

            if (valid) {
                if (changeUsername){
                    currentUser.setName(userName);
                }
                if (changeEmail){
                    currentUser.setEmail(email);
                }
                if (changePw){
                    currentUser.setPassword(newPw);
                }

                RestResponse userResponse = userBean.getRestClient().saveUser(currentUser);
                if (!userResponse.hasError()) {
                    ((SmartCinemaUi) getUI()).update();
                    Notification.show("Die Änderungen wurden erfolgreich übernommen", Notification.Type.HUMANIZED_MESSAGE);
                }
                else {
                    Notification.show(userResponse.getErrorMsg(), Notification.Type.ERROR_MESSAGE);
                }
            }
        }
    }
}
