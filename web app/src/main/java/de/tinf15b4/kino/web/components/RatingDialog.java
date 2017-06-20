package de.tinf15b4.kino.web.components;

import java.io.Serializable;
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
import com.vaadin.ui.themes.ValoTheme;

import de.tinf15b4.kino.data.ratedcinemas.IRateable;
import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.web.rest.RestResponse;
import de.tinf15b4.kino.web.user.UserBean;

public class RatingDialog<Rated> implements Serializable {

    private static final long serialVersionUID = 8954699169695925477L;

    public void openDialog(UI ui, UserBean userBean, IRateable<Rated> rateable, Saver<Rated> saver) {
        Window dialog = new Window("Bewertung abgeben");
        dialog.setModal(true);
        dialog.setClosable(false);
        dialog.setContent(createContent(dialog, userBean, rateable, saver));
        dialog.setSizeUndefined();
        ui.addWindow(dialog);
    }

    private Component createContent(Window dialog, UserBean userBean, IRateable<Rated> rateable, Saver<Rated> saver) {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        Label starLabel = new Label("Bewertung");
        starLabel.setId("ratingsStarLabel");
        Slider slider = new Slider(0, 10);
        HorizontalLayout givenRatingRow = new HorizontalLayout();
        givenRatingRow.addComponent(starLabel);
        givenRatingRow.addComponent(slider);
        mainLayout.addComponent(givenRatingRow);
        mainLayout.setId("giveRatingsPanel");

        Label ratingLabel = new Label("Bewertungstext");
        ratingLabel.setId("giveRatingsHeading");
        HorizontalLayout ratingsTextRow = new HorizontalLayout();
        ratingsTextRow.addComponent(ratingLabel);
        ratingsTextRow.setSpacing(true);
        mainLayout.addComponent(ratingsTextRow);

        TextField rating = new TextField();
        rating.setId("ratingsField");
        rating.setWidth("400px");
        rating.setHeight("100px");
        HorizontalLayout ratingsFieldRow = new HorizontalLayout();
        ratingsFieldRow.setSpacing(true);
        ratingsFieldRow.addComponent(rating);
        mainLayout.addComponent(ratingsFieldRow);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.setId("ratingsButtonRow");
        Button cancel = new Button("Abbrechen");
        cancel.setId("cancelRatingsButton");
        cancel.addStyleName(ValoTheme.BUTTON_DANGER);
        cancel.addClickListener(e -> dialog.close());
        buttons.addComponent(cancel);

        Button finish = new Button("Senden");
        finish.setId("finishRatingsButton");
        finish.addStyleName(ValoTheme.BUTTON_PRIMARY);
        finish.addClickListener(e -> trySave(rating.getValue(), (int) slider.getValue().doubleValue(), saver, userBean,
                rateable, dialog));
        buttons.addComponent(finish);

        mainLayout.addComponent(buttons);
        return mainLayout;
    }

    private void trySave(String rating, int stars, Saver<Rated> saver, UserBean userBean, IRateable<Rated> rateable,
            Window dialog) {
        User currentUser = userBean.getCurrentUser();
        if (currentUser == null) {
            Notification.show("Sie müssen sich anmelden um Bewertungen abzugeben", Type.WARNING_MESSAGE);
            return;
        }
        Rated rated = rateable.createRating(currentUser, stars, rating, new Date());

        RestResponse response = saver.save(rated);
        if (!response.hasError()) {
            Notification.show("Bewertung wurde erfolgreich abgegebn!", Type.TRAY_NOTIFICATION);
            dialog.close();
        } else {
            Notification.show(response.getErrorMsg(), Type.ERROR_MESSAGE);
        }
    }

}
