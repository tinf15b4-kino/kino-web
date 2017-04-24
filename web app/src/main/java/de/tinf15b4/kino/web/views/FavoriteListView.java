package de.tinf15b4.kino.web.views;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.tinf15b4.kino.api.rest.PictureController;
import de.tinf15b4.kino.api.rest.RestResponse;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.users.UserBean;

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
                content.setMargin(true);
                content.setSpacing(true);
                this.addComponent(new Panel(content));

                if (favorites.isEmpty()) {
                    content.addComponent(new Label("Bisher wurden noch keine Kinos zu den Favoriten hinzugefügt. :("));
                } else {
                    for (Favorite f : favorites) {
                        content.addComponent(buildListEntry(f.getCinema()));
                    }
                }
            }
        } else {
            this.addComponent(new Label("Sie müssen sich anmelden!"));
        }
    }

    private Component buildListEntry(Cinema c) {
        HorizontalLayout pav = new HorizontalLayout();
        pav.addStyleName("favorite-cinema-row-" + c.getId());
        pav.setWidth(100, Unit.PERCENTAGE);

        Component image = new Image(null, new ExternalResource(PictureController.getCinemaPictureUrl(c)));
        image.setHeight("100px");
        pav.addComponent(image);

        Link l = new Link(c.getName(), new ExternalResource("#!" + CinemaView.VIEW_NAME + "/" + c.getId()));
        pav.addComponent(l);
        pav.setComponentAlignment(l, Alignment.MIDDLE_LEFT);

        Button removeBtn = new Button("Entfernen");
        removeBtn.addClickListener(e -> removeFromFavorites(c, pav));
        pav.addComponent(removeBtn);
        pav.setComponentAlignment(removeBtn, Alignment.MIDDLE_RIGHT);
        pav.setExpandRatio(removeBtn, 1f);
        pav.setSpacing(true);

        return pav;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    private void removeFromFavorites(Cinema c, HorizontalLayout row) {
        RestResponse favoriteResponse = userBean.getRestClient().getFavorite(c.getId());
        if (!favoriteResponse.hasError()) {
            RestResponse deleteResponse = userBean.getRestClient()
                    .deleteFavorite((Favorite) favoriteResponse.getValue());
            if (!deleteResponse.hasError()) {
                row.removeAllComponents();

                GridLayout g = new GridLayout(2, 1);

                String cinemaName = ((Favorite) favoriteResponse.getValue()).getCinema().getName();
                Label l = new Label(String.format("Das Kino \"%s\" wurde aus den Favoriten entfernt.", cinemaName));
                g.addComponent(l, 0, 0);
                g.setComponentAlignment(l, Alignment.MIDDLE_LEFT);

                Button undo = new Button("Rückgängig machen");
                undo.setStyleName(ValoTheme.BUTTON_LINK);
                undo.addClickListener(e -> undoRemove(c, row));
                g.addComponent(undo, 1, 0);
                g.setComponentAlignment(undo, Alignment.MIDDLE_LEFT);
                g.setColumnExpandRatio(1, 1.0f);

                row.addComponent(g);
            }
        }
    }

    private void undoRemove(Cinema c, HorizontalLayout row) {
        RestResponse response = userBean.getRestClient().saveFavorite(new Favorite(userBean.getCurrentUser(), c));
        if (!response.hasError())
            content.replaceComponent(row, buildListEntry(((Favorite) response.getValue()).getCinema()));
    }
}
