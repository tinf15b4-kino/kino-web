package de.tinf15b4.kino.web.views;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

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

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.users.UserBean;
import de.tinf15b4.kino.web.controllers.PictureController;

@SpringView(name = FavoriteListView.VIEW_NAME)
public class FavoriteListView extends VerticalLayout implements View {
    private static final long serialVersionUID = -7534909329867215414L;

    public static final String VIEW_NAME = "favourites";

    private VerticalLayout content;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserBean userBean;

    @PostConstruct
    private void init() {
        if (userBean.isUserLoggedIn()) {
            List<Favorite> l = favoriteService.getAllFavoritesForUser(userBean.getCurrentUser());

            this.addComponent(new Label("Favoritisierte Kinos"));

            content = new VerticalLayout();
            content.setMargin(true);
            content.setSpacing(true);
            this.addComponent(new Panel(content));

            if (l.isEmpty()) {
                content.addComponent(new Label("Bisher wurden noch keine Kinos zu den Favoriten hinzugefügt. :("));
            } else {
                for (Favorite f : l) {
                    content.addComponent(buildListEntry(f.getCinema()));
                }
            }
        } else {
            this.addComponent(new Label("Sie müssen sich anmelden!"));
        }
    }

    private Component buildListEntry(Cinema c) {
        HorizontalLayout pav = new HorizontalLayout();
        pav.addStyleName("favorite-cinema-row-"+c.getId());
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
        if (favoriteService.isCinemaFavorite(userBean.getCurrentUser(), c)) {
            // remove favorite entry
            String cinemaName = favoriteService.findFavorite(userBean.getCurrentUser(), c).getCinema().getName();
            favoriteService.unmarkFavorite(userBean.getCurrentUser(), c);

            row.removeAllComponents();

            GridLayout g = new GridLayout(2, 1);

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

    private void undoRemove(Cinema c, HorizontalLayout row) {
        favoriteService.markFavorite(userBean.getCurrentUser(), c);
        content.replaceComponent(row,
                buildListEntry(favoriteService.findFavorite(userBean.getCurrentUser(), c).getCinema()));
    }
}
