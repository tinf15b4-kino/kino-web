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

import de.tinf15b4.kino.data.ratedcinemas.IRateable;
import de.tinf15b4.kino.web.rest.RestResponse;
import de.tinf15b4.kino.web.user.UserBean;

public class RatingDialog<Rated> {

    private Window dialog;
    private UserBean userBean;
    private IRateable<Rated> rateable;
    private Saver<Rated> saver;

    public void openDialog(UI ui, UserBean userBean, IRateable<Rated> rateable, Saver<Rated> saver) {
        this.userBean = userBean;
        this.rateable = rateable;
        this.saver = saver;
        dialog = new Window("Bewertung abgeben");
        dialog.setModal(true);
        dialog.setContent(createContent());
        ui.addWindow(dialog);
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
        buttons.addComponent(cancel);

        Button finish = new Button("Senden");
        finish.addClickListener(e -> trySave(rating.getValue(), (int) slider.getValue().doubleValue(), saver));
        buttons.addComponent(finish);

        mainLayout.addComponent(buttons);
        return mainLayout;
    }

    private void trySave(String rating, int stars, Saver<Rated> saver) {
        Rated rated = rateable.createRating(userBean.getCurrentUser(), stars, rating, new Date());

        RestResponse response = saver.save(rated);
        if (!response.hasError()) {
            Notification.show("Bewertung wurde gespeichert!", Type.HUMANIZED_MESSAGE);
            dialog.close();
        } else {
            Notification.show(response.getErrorMsg(), Type.ERROR_MESSAGE);
        }
    }

}
