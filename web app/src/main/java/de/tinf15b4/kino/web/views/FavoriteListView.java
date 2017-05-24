package de.tinf15b4.kino.web.views;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.web.rest.RestResponse;
import de.tinf15b4.kino.web.user.UserBean;
import de.tinf15b4.kino.web.util.ViewUtils;

@SpringView(name = FavoriteListView.VIEW_NAME)
public class FavoriteListView extends VerticalLayout implements View {
    private static final long serialVersionUID = -7534909329867215414L;

    public static final String VIEW_NAME = "favourites";

    private VerticalLayout content;

    @Autowired
    private UserBean userBean;

    @PostConstruct
    private void init() {
        if (userBean.isUserLoggedIn()) {
            RestResponse response = userBean.getRestClient().getAllFavorites();
            if (!response.hasError()) {
                List<Favorite> favorites = Lists.newArrayList((Favorite[]) response.getValue());

                this.addComponent(new Label("Favoritisierte Kinos"));

                content = new VerticalLayout();

                if (favorites.isEmpty()) {
                    content.addComponent(new Label("Bisher wurden noch keine Kinos zu den Favoriten hinzugefügt. :("));
                } else {
                    for (Favorite f : favorites) {
                        Cinema c = f.getCinema();
                        HorizontalLayout cinemaRow = ViewUtils.createCinemaRow(c, userBean);
                        content.addComponent(cinemaRow);
                    }
                }
            }
            content.setSpacing(true);
            this.addComponent(content);

        } else {
            this.addComponent(new Label("Sie müssen sich anmelden um diese Funktion nutzen zu können!"));
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
