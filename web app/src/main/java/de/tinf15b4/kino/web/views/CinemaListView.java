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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.users.UserBean;

@SpringView(name = CinemaListView.VIEW_NAME)
public class CinemaListView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "cinemas";

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserBean userBean;

    private HorizontalLayout favButtonContainer = new HorizontalLayout();

    @PostConstruct
    public void init() {
        this.setMargin(true);
        this.setSpacing(true);

        for (Cinema c : cinemaService.findAll()) {
            HorizontalLayout pav = new HorizontalLayout();
            pav.setWidth(100, Unit.PERCENTAGE);

            Link l = new Link(c.getName(), new ExternalResource("#!" + CinemaView.VIEW_NAME + "/" + c.getId()));
            pav.addComponent(l);

            if (userBean.isUserLoggedIn()) {
                Component button = createFavoriteButton(c);
                pav.addComponent(button);
                pav.setComponentAlignment(button, Alignment.MIDDLE_RIGHT);
            }
            this.addComponent(pav);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    private Component createFavoriteButton(Cinema c) {
        if (userBean.isUserLoggedIn()) {

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

        } else {
            return null;
        }
    }

    private void markAsFavorite(long id) {
        List<Favorite> existing = favoriteService.findByCinemaId(id);
        if (existing.isEmpty()) {
            Cinema c = cinemaService.findOne(id);

            // create new favorite entry
            favoriteService.save(new Favorite(userBean.getCurrentUser(), c));

            // replace button
            replaceFavoriteButton(c);
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

    private void replaceFavoriteButton(Cinema c) {
        this.removeAllComponents();
        this.init();
    }
}
