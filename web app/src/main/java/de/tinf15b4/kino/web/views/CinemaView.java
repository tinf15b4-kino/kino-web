package de.tinf15b4.kino.web.views;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.data.playlists.PlaylistService;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinema;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaService;
import de.tinf15b4.kino.data.users.UserBean;
import de.tinf15b4.kino.web.controllers.PictureController;
import de.tinf15b4.kino.web.util.CinemaFavoriteUtils;
import de.tinf15b4.kino.web.util.ToggleFavoriteListener;

@SpringView(name = CinemaView.VIEW_NAME)
public class CinemaView extends VerticalLayout implements View, ToggleFavoriteListener {
    public static final String VIEW_NAME = "cinema";

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private UserBean userBean;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private RatedCinemaService ratedCinemaService;

    private Component favoriteButton;
    private Cinema c;

    @Override
    public void enter(ViewChangeEvent event) {
        this.setMargin(true);
        this.setSpacing(true);

        this.removeAllComponents();
        if (event.getParameters() != null) {
            String idStr = event.getParameters();
            long id = Long.parseLong(idStr);
            c = cinemaService.findOne(id);
            // This id does not exist
            if (c == null) {
                this.getUI().getNavigator().navigateTo(CinemaListView.VIEW_NAME);
            } else {
                HorizontalLayout heading = new HorizontalLayout();
                Label cinemaName = new Label(c.getName());
                cinemaName.setStyleName("cinemaNameLabel");
                heading.addComponent(cinemaName);

                this.addComponent(heading);

                HorizontalLayout informationRow = new HorizontalLayout();
                // Picture
                Component image = new Image(null, new ExternalResource(PictureController.getCinemaPictureUrl(c)));
                image.setHeight("200px");

                informationRow.addComponent(image);
                informationRow.addComponent(createCinemaInfoBox(c));
                informationRow.setSpacing(true);

                this.addComponent(informationRow);

                List<RatedCinema> ratedCinemas = ratedCinemaService.findRatingsByCinema(c);

                if (ratedCinemas.size() > 0) {
                    GridLayout ratings = new GridLayout(4, 1);
                    ratings.setMargin(true);
                    ratings.setSpacing(true);
                    ratings.setSizeFull();

                    for (RatedCinema rc : ratedCinemas) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY);
                        ratings.addComponent(new Label(rc.getUser().getName()));
                        ratings.addComponent(new Label(rc.getRating() + ""));
                        ratings.addComponent(new Label(sdf.format(rc.getTime())));
                        ratings.addComponent(new Label(rc.getDescription()));
                    }

                    this.addComponent(new Panel("Bewertungen", ratings));
                }

                List<Playlist> playlistEntries = playlistService.findForCinema(c, new Date(),
                        new Date(new Date().getTime() + 1000L * 3600 * 24 * 7));

                if (playlistEntries.size() > 0) {
                    GridLayout movies = new GridLayout(3, 1);
                    movies.setMargin(true);
                    movies.setSpacing(true);
                    movies.setSizeFull();

                    for (Playlist p : playlistEntries) {
                        SimpleDateFormat sdf = new SimpleDateFormat("E HH:mm", Locale.GERMANY);
                        NumberFormat pricef = NumberFormat.getCurrencyInstance(Locale.GERMANY);
                        movies.addComponent(new Label(sdf.format(p.getTime())));
                        movies.addComponent(new Link(p.getMovie().getName(),
                                new ExternalResource("#!" + MovieView.VIEW_NAME + "/" + p.getMovie().getId())));
                        movies.addComponent(new Label(pricef.format(p.getPrice() / 100.0)));
                    }

                    this.addComponent(new Panel("Filme", movies));
                }

            }
        }
    }

    private Component createCinemaInfoBox(Cinema c) {
        VerticalLayout infoBox = new VerticalLayout();

        infoBox.addComponent(new Label(c.getAddress(), ContentMode.PREFORMATTED));

        favoriteButton = CinemaFavoriteUtils.createFavoriteButton(c, favoriteService, userBean, this);
        favoriteButton.addStyleName("detail");
        infoBox.addComponent(favoriteButton);

        return infoBox;
    }

    private void replaceFavoriteButton() {
        AbstractOrderedLayout favParent = (AbstractOrderedLayout) favoriteButton.getParent();
        int index = favParent.getComponentIndex(favoriteButton);
        favParent.removeComponent(favoriteButton);
        favoriteButton = CinemaFavoriteUtils.createFavoriteButton(c, favoriteService, userBean, this);
        favParent.addComponent(favoriteButton, index);
    }

    @Override
    public void favoriteRemoved() {
        replaceFavoriteButton();
    }

    @Override
    public void favoriteAdded() {
        replaceFavoriteButton();
    }
}
