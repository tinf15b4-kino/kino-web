package de.tinf15b4.kino.web.views;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.web.rest.RestResponse;
import de.tinf15b4.kino.web.ui.SmartCinemaUi;
import de.tinf15b4.kino.web.user.UserBean;
import de.tinf15b4.kino.web.util.AccountInformationChecker;

@SpringView(name = AccountView.VIEW_NAME)

public class AccountView extends VerticalLayout implements View {

    private static final long serialVersionUID = -5445455208818252072L;

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

            Button saveButton = new Button("Speichern",
                    e -> trySave(userField, emailField, oldPwField, newPwField, newPwCheckField, currentUser));

            userForm.addComponent(saveButton);
            saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

            saveButton.addShortcutListener(new ShortcutListener(null, ShortcutAction.KeyCode.ENTER, null) {
                private static final long serialVersionUID = -5604254966514700302L;

                @Override
                public void handleAction(Object sender, Object target) {
                    saveButton.click();
                }
            });

            Button delete = new Button("Löschen", e -> checkDelete());
            delete.addStyleName(ValoTheme.BUTTON_DANGER);
            userForm.addComponent(delete);

            this.addComponent(userForm);
        }
    }

    private void checkDelete() {
        Window dialog = new Window("Benutzer löschen");
        dialog.setModal(true);
        dialog.setClosable(false);
        dialog.setSizeUndefined();
        getUI().addWindow(dialog);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        layout.addComponent(
                new Label("Achtung! Diese Aktion kann nicht rückgängig gemacht werden. Benutzer wirklich löschen?"));

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        Button cancel = new Button("Abbrechen");
        cancel.addClickListener(e -> dialog.close());
        buttons.addComponent(cancel);

        Button finish = new Button("Löschen");
        finish.addStyleName(ValoTheme.BUTTON_DANGER);
        finish.addClickListener(e -> {
            dialog.close();
            tryDelete();
        });
        buttons.addComponent(finish);

        layout.addComponent(buttons);
        dialog.setContent(layout);
    }

    private void tryDelete() {
        RestResponse response = userBean.getRestClient().deleteUser();
        if (response.hasError()) {
            Notification.show(response.getErrorMsg(), Type.ERROR_MESSAGE);
        } else {
            Notification.show("Account wurde erfolgreich gelöscht", Type.HUMANIZED_MESSAGE);
            this.getUI().getNavigator().navigateTo(StartView.VIEW_NAME);
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
                String errorMsg = AccountInformationChecker.checkUsername(userName);
                if (Strings.isNullOrEmpty(errorMsg)) {
                    changeUsername = true;
                } else {
                    userField.setComponentError(new UserError(errorMsg));
                    valid = false;
                }
            }

            // change email
            if (!email.equals(currentUser.getEmail())) {
                String errorMsg = AccountInformationChecker.checkEmail(email);
                if (Strings.isNullOrEmpty(errorMsg)) {
                    changeEmail = true;
                } else {
                    emailField.setComponentError(new UserError(errorMsg));
                    valid = false;
                }
            }

            // change PW
            if (!Strings.isNullOrEmpty(oldPw) || !Strings.isNullOrEmpty(newPw)) {
                String oldPwErrorMsg = AccountInformationChecker.checkOldPassword(oldPw, currentUser);
                if (Strings.isNullOrEmpty(oldPwErrorMsg)) {
                    String newPwErrorMsg = AccountInformationChecker.checkNewPassword(newPw, newCheckPw);
                    if (Strings.isNullOrEmpty(newPwErrorMsg)) {
                        changePw = true;
                    } else {
                        newPwField.setComponentError(new UserError(newPwErrorMsg));
                        valid = false;
                    }
                } else {
                    oldPwField.setComponentError(new UserError(oldPwErrorMsg));
                    valid = false;
                }
            }

            if (valid) {
                performChange(currentUser, userName, email, newPw, changeUsername, changeEmail, changePw);
                // clear Passwords after change (no matter whether successful or not)
                oldPwField.clear();
                newPwField.clear();
                newPwCheckField.clear();
            }
        }
    }

    private void performChange(User currentUser, String userName, String email, String newPw, boolean changeUsername,
            boolean changeEmail, boolean changePw) {
        if (changeUsername) {
            currentUser.setName(userName);
        }
        if (changeEmail) {
            currentUser.setEmail(email);
        }
        if (changePw) {
            currentUser.setPassword(newPw);

        }

        RestResponse userResponse = userBean.getRestClient().updateUser(currentUser);
        if (!userResponse.hasError()) {
            ((SmartCinemaUi) getUI()).update();
            Notification.show("Die Änderungen wurden erfolgreich übernommen", Notification.Type.HUMANIZED_MESSAGE);
        } else {
            Notification.show(userResponse.getErrorMsg(), Notification.Type.ERROR_MESSAGE);
        }
    }
}
