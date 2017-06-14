package de.tinf15b4.kino.web.components;

import java.util.Date;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinema;
import de.tinf15b4.kino.web.rest.RestResponse;
import de.tinf15b4.kino.web.user.UserBean;

public class RatingDialog {

    private Window dialog;
    private UserBean userBean;
    private Cinema cinema;

    public void openDialog(UI ui, UserBean userBean, Cinema cinema) {
        this.userBean = userBean;
        this.cinema = cinema;
        dialog = new Window("Bewertung abgeben");
        dialog.setModal(true);
        dialog.setContent(createContent());
        ui.addWindow(dialog);
    }

    public void closeDialog() {
        dialog.close();
    }

    private Component createContent() {
        VerticalLayout mainLayout = new VerticalLayout();

        Label ratingLabel = new Label("Bewertungstext");
        TextField rating = new TextField();
        HorizontalLayout row = new HorizontalLayout();
        row.addComponent(ratingLabel);
        row.addComponent(rating);
        mainLayout.addComponent(row);

        Label starLabel = new Label("Bewertung");
        Slider slider = new Slider(0, 5);
        row = new HorizontalLayout();
        row.addComponent(starLabel);
        row.addComponent(slider);
        mainLayout.addComponent(row);

        HorizontalLayout buttons = new HorizontalLayout();
        Button cancel = new Button("Abbrechen");
        cancel.addClickListener(e -> dialog.close());

        Button finish = new Button("Senden");
        finish.addClickListener(e -> trySave(rating.getValue(), (int) slider.getValue().doubleValue()));
        mainLayout.addComponent(buttons);
        return mainLayout;
    }

    private void trySave(String rating, int stars) {
        RatedCinema ratedCinema = new RatedCinema(userBean.getCurrentUser(), cinema, stars, rating, new Date());
        RestResponse response = userBean.getRestClient().saveRatedCinema(ratedCinema);
        if (!response.hasError()) {
            Notification.show("Bewertung wurde gespeichert!", Type.HUMANIZED_MESSAGE);
            dialog.close();
        } else {
            Notification.show(response.getErrorMsg(), Type.ERROR_MESSAGE);
        }
    }

}
