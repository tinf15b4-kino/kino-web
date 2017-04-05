package de.tinf15b4.kino.web.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.users.UserBean;
import de.tinf15b4.kino.web.controllers.PictureController;
import de.tinf15b4.kino.web.util.CinemaFavoriteUtils;
import de.tinf15b4.kino.web.util.ToggleFavoriteListener;
import org.springframework.beans.factory.annotation.Autowired;
import de.tinf15b4.kino.data.cinemas.CinemaService;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringView(name = FavoriteListView.VIEW_NAME)
public class FavoriteListView extends VerticalLayout implements View {
    private static final long serialVersionUID = -7534909329867215414L;

    public static final String VIEW_NAME = "favourites";

    private VerticalLayout content;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserBean userBean;

    @Autowired
    private CinemaService cinemaService;

    @PostConstruct
    private void init() {
        if (userBean.isUserLoggedIn()) {
            List<Favorite> l = favoriteService.getAllFavoritesForUser(userBean.getCurrentUser());

            this.addComponent(new Label("Favoritisierte Kinos"));

            content = new VerticalLayout();

            if (l.isEmpty()) {
                content.addComponent(new Label("Bisher wurden noch keine Kinos zu den Favoriten hinzugefügt. :("));
            } else {
                for (Cinema c : cinemaService.findAll()) {
                    if (favoriteService.isCinemaFavorite(userBean.getCurrentUser(), c)) {
                        HorizontalLayout row = new HorizontalLayout();
                        row.setWidth(100, Unit.PERCENTAGE);
                        row.setId("cinemaRow_" + c.getId());

                        // Picture
                        Component image = new Image(null, new ExternalResource(PictureController.getCinemaPictureUrl(c)));
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

            button = CinemaFavoriteUtils.createFavoriteButton(cinemaService.findOne(cinemaId),
                    favoriteService, userBean, this);

            row.addComponent(button);
            row.setComponentAlignment(button, Alignment.BOTTOM_LEFT);
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
