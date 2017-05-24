package de.tinf15b4.kino.web.util;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.web.user.UserBean;

public class ButtonManager implements ToggleFavoriteListener {

    private Component button = null;
    private VerticalLayout row;
    private long cinemaId;

    private UserBean userBean;

    public ButtonManager(UserBean userBean, VerticalLayout row, long cinemaId) {
        this.userBean = userBean;
        this.row = row;
        this.cinemaId = cinemaId;
    }

    @Override
    public void favoriteRemoved() {
        recreateBtn();
    }

    @Override
    public void favoriteAdded() {
        recreateBtn();
    }

    public void recreateBtn() {
        if (button != null)
            row.removeComponent(button);

        button = CinemaFavoriteUtils.createFavoriteButton(cinemaId, userBean, this);

        row.addComponent(button);
        row.setComponentAlignment(button, Alignment.BOTTOM_LEFT);
    }

}
