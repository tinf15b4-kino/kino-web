package de.tinf15b4.kino.web.views;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.users.UserBean;
import de.tinf15b4.kino.web.util.CinemaFavoriteUtils;
import de.tinf15b4.kino.web.util.PictureUtils;
import de.tinf15b4.kino.web.util.ToggleFavoriteListener;

@SpringView(name = CinemaListView.VIEW_NAME)
public class CinemaListView extends VerticalLayout implements View, ToggleFavoriteListener {
    public static final String VIEW_NAME = "cinemas";

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserBean userBean;

    @PostConstruct
    public void init() {
        this.setMargin(true);
        this.setSpacing(true);

        for (Cinema c : cinemaService.findAll()) {
            HorizontalLayout row = new HorizontalLayout();
            row.setWidth(100, Unit.PERCENTAGE);

            // Picture
            Component image = PictureUtils.getImage(null, c);

            image.setHeight("100px");
            row.addComponent(image);

            Link l = new Link(c.getName(), new ExternalResource("#!" + CinemaView.VIEW_NAME + "/" + c.getId()));
            row.addComponent(l);
            row.setComponentAlignment(l, Alignment.MIDDLE_LEFT);

            Component button = CinemaFavoriteUtils.createFavoriteButton(c, favoriteService, userBean, this);
            row.addComponent(button);
            row.setComponentAlignment(button, Alignment.MIDDLE_RIGHT);
            row.setExpandRatio(button, 1f);
            row.setSpacing(true);
            this.addComponent(row);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

    @Override
    public void favoriteRemoved() {
        updateView();
    }

    @Override
    public void favoriteAdded() {
        updateView();
    }

    private void updateView() {
        removeAllComponents();
        init();
    }
}
