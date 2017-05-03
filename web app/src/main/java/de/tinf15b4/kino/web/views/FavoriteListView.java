package de.tinf15b4.kino.web.views;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.web.rest.RestResponse;
import de.tinf15b4.kino.web.user.UserBean;
import de.tinf15b4.kino.web.util.CinemaFavoriteUtils;
import de.tinf15b4.kino.web.util.ToggleFavoriteListener;

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
                        HorizontalLayout row = new HorizontalLayout();
                        row.setWidth(100, Unit.PERCENTAGE);
                        row.setId("cinemaRow_" + c.getId());

                        // Picture
                        Component image = new Image(null,
                                new ExternalResource(userBean.getRestClient().getCinemaPictureUrl(c)));
                        image.setId("cinemaImage_" + c.getId());
                        image.setHeight("200px");
                        row.addComponent(image);

                        // Info-Box
                        Component movieInfoBox = createCinemaInfoBox(c);
                        row.addComponent(movieInfoBox);

                        row.setExpandRatio(movieInfoBox, 1f);
                        row.setSpacing(true);
                        content.addComponent(row);
                    }
                }
            }
            content.setSpacing(true);
            this.addComponent(content);

        } else {
            this.addComponent(new Label("Sie müssen sich anmelden um diese Funktion nutzen zu können!"));
        }
    }

    private Component createCinemaInfoBox(Cinema c) {

        VerticalLayout cinemaInfoBox = new VerticalLayout();

        Link l = new Link(c.getName(), new ExternalResource("#!" + CinemaView.VIEW_NAME + "/" + c.getId()));
        l.setId("cinemaListLink_" + c.getId());
        cinemaInfoBox.addComponent(l);
        cinemaInfoBox.setComponentAlignment(l, Alignment.TOP_LEFT);

        cinemaInfoBox.addComponent(new Label(c.getAddress(), ContentMode.PREFORMATTED));

        createFavoriteBtn(c.getId(), cinemaInfoBox);

        return cinemaInfoBox;
    }

    private void createFavoriteBtn(long cinemaId, VerticalLayout row) {
        FavoriteListView.FavoriteBtnManager mgr = new FavoriteListView.FavoriteBtnManager();
        mgr.row = row;
        mgr.cinemaId = cinemaId;
        mgr.recreateBtn();
    }

    private class FavoriteBtnManager implements ToggleFavoriteListener {
        public Component button = null;
        public VerticalLayout row;
        public long cinemaId;

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

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
