package de.tinf15b4.kino.web.views;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

import de.tinf15b4.kino.data.Cinema;
import de.tinf15b4.kino.data.CinemaRepository;
import de.tinf15b4.kino.data.Favorite;
import de.tinf15b4.kino.data.FavoriteRepository;
import de.tinf15b4.kino.data.Playlist;
import de.tinf15b4.kino.data.PlaylistRepository;
import de.tinf15b4.kino.data.RatedCinema;
import de.tinf15b4.kino.data.RatedCinemaRepository;
import de.tinf15b4.kino.data.users.UserLoginBean;

@SpringView(name = CinemaView.VIEW_NAME)
public class CinemaView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "cinema";

    @Autowired
    private CinemaRepository repo;

    @Autowired
    private UserLoginBean userBean;

    @Autowired
    private FavoriteRepository favRepo;

    @Autowired
    private PlaylistRepository playlistRepo;

    @Autowired
    private RatedCinemaRepository ratedCinemaRepo;

    private HorizontalLayout favButtonContainer = new HorizontalLayout();

    @Override
    @Transactional
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        this.setMargin(true);
        this.setSpacing(true);

        this.removeAllComponents();
        if (event.getParameters() != null) {
            String idStr = event.getParameters();
            long id = Long.parseLong(idStr);

            Cinema c = repo.getOne(id);

            this.addComponent(new Label(c.getName()));

            // this.addComponent(c.getImage());

            this.addComponent(favButtonContainer);

            this.addComponent(new Label(c.getAddress(), ContentMode.PREFORMATTED));
            replaceFavoriteButton(c);

            GridLayout ratings = new GridLayout(4, 1);
            ratings.setMargin(true);
            ratings.setSpacing(true);
            ratings.setSizeFull();

            for (RatedCinema rc : ratedCinemaRepo.findRatingsByCinema(c)) {
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

            for (Playlist p : playlistRepo.findForCinema(c, new Date(), new Date(new Date().getTime() + 1000L*3600*24*7))) {
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

        List<Favorite> existing = favRepo.findByCinemaId(id);
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

    @Transactional
    private void markAsFavorite(long id) {
        if (userBean.isUserLoggedIn()) {

            List<Favorite> existing = favRepo.findByCinemaId(id);
            if (existing.isEmpty()) {
                Cinema c = repo.findOne(id);

                // create new favorite entry
                favRepo.save(new Favorite(userBean.getCurrentUser(), c));

                // replace button
                replaceFavoriteButton(c);
            }
        } else {
            Notification.show("Melde dich an, um diese Fuktion zu nutzen!", "", Notification.Type.HUMANIZED_MESSAGE);
        }
    }

    @Transactional
    private void unmarkFavorite(long id) {
        List<Favorite> existing = favRepo.findByCinemaId(id);
        if (!existing.isEmpty()) {
            // remove favorite entry
            favRepo.delete(existing);

            // replace button
            replaceFavoriteButton(repo.findOne(id));
        }
    }
}
