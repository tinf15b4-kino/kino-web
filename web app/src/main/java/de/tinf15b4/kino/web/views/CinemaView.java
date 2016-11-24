package de.tinf15b4.kino.web.views;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.data.playlists.PlaylistService;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinema;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaService;
import de.tinf15b4.kino.data.users.UserBean;

@SpringView(name = CinemaView.VIEW_NAME)
public class CinemaView extends VerticalLayout implements View {
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

    private HorizontalLayout favButtonContainer = new HorizontalLayout();

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        this.setMargin(true);
        this.setSpacing(true);

        this.removeAllComponents();
        if (event.getParameters() != null) {
            String idStr = event.getParameters();
            long id = Long.parseLong(idStr);

            Cinema c = cinemaService.findOne(id);

            this.addComponent(new Label(c.getName()));

            // this.addComponent(c.getImage());

            this.addComponent(favButtonContainer);

            this.addComponent(new Label(c.getAddress(), ContentMode.PREFORMATTED));
            replaceFavoriteButton(c);

            GridLayout ratings = new GridLayout(4, 1);
            ratings.setMargin(true);
            ratings.setSpacing(true);
            ratings.setSizeFull();

            for (RatedCinema rc : ratedCinemaService.findRatingsByCinema(c)) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY);
                ratings.addComponent(new Label(rc.getUser().getName()));
                ratings.addComponent(new Label(rc.getRating() + ""));
                ratings.addComponent(new Label(sdf.format(rc.getTime())));
                ratings.addComponent(new Label(rc.getDescription()));
            }

            this.addComponent(new Panel("Ratings", ratings));

            GridLayout movies = new GridLayout(3, 1);
            movies.setMargin(true);
            movies.setSpacing(true);
            movies.setSizeFull();

            for (Playlist p : playlistService.findForCinema(c, new Date(),
                    new Date(new Date().getTime() + 1000L * 3600 * 24 * 7))) {
                SimpleDateFormat sdf = new SimpleDateFormat("E HH:mm", Locale.GERMANY);
                NumberFormat pricef = NumberFormat.getCurrencyInstance(Locale.GERMANY);
                movies.addComponent(new Label(sdf.format(p.getTime())));
                movies.addComponent(new Link(p.getMovie().getName(),
                        new ExternalResource("#!" + MovieView.VIEW_NAME + "/" + p.getMovie().getId())));
                movies.addComponent(new Label(pricef.format(p.getPrice()/100.0)));
            }

            this.addComponent(new Panel("Movies", movies));
        }
    }

    private void replaceFavoriteButton(Cinema c) {
        favButtonContainer.removeAllComponents();
        favButtonContainer.addComponent(createFavoriteButton(c));
    }

    private Component createFavoriteButton(Cinema c) {
        // TODO: Check for login

        long id = c.getId();

        List<Favorite> existing = favoriteService.findByCinemaId(id);
        if (existing.isEmpty()) {
            // create button
            Button favBtn = new Button();
            favBtn.setCaption("Mark as favorite"); // TODO: Deutsch
            favBtn.addClickListener(e -> markAsFavorite(id));

            return favBtn;
        } else {
            MenuBar unfavMenu = new MenuBar();
            unfavMenu.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
            MenuBar.MenuItem menu = unfavMenu.addItem("Marked as favorite", null);
            menu.addItem("Remove from favorites", i -> unmarkFavorite(id));

            return unfavMenu;
        }
    }

    private void markAsFavorite(long id) {
        if (userBean.isUserLoggedIn()) {

            List<Favorite> existing = favoriteService.findByCinemaId(id);
            if (existing.isEmpty()) {
                Cinema c = cinemaService.findOne(id);

                // create new favorite entry
                favoriteService.save(new Favorite(userBean.getCurrentUser(), c));

                // replace button
                replaceFavoriteButton(c);
            }
        } else {
            Notification.show("Melde dich an, um diese Fuktion zu nutzen!", "", Notification.Type.HUMANIZED_MESSAGE);
        }
    }

    private void unmarkFavorite(long id) {
        List<Favorite> existing = favoriteService.findByCinemaId(id);
        if (!existing.isEmpty()) {
            // remove favorite entry
            favoriteService.delete(existing);

            // replace button
            replaceFavoriteButton(cinemaService.findOne(id));
        }
    }
}
