package de.tinf15b4.kino.web.views;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

import de.tinf15b4.kino.data.Cinema;
import de.tinf15b4.kino.data.CinemaRepository;
import de.tinf15b4.kino.data.Favorite;
import de.tinf15b4.kino.data.FavoriteRepository;
import de.tinf15b4.kino.data.users.UserLoginBean;

@SpringView(name = CinemaListView.VIEW_NAME)
public class CinemaListView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "cinemas";

    @Autowired
    private CinemaRepository repo;

    @Autowired
    private FavoriteRepository favRepo;

    @Autowired
    private UserLoginBean userBean;

    private HorizontalLayout favButtonContainer = new HorizontalLayout();

    @PostConstruct
    @Transactional
    public void init() {
        this.setMargin(true);
        this.setSpacing(true);

        for (Cinema c : repo.findAll()) {
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

        } else {
            return null;
        }
    }

    @Transactional
    private void markAsFavorite(long id) {
        List<Favorite> existing = favRepo.findByCinemaId(id);
        if (existing.isEmpty()) {
            Cinema c = repo.findOne(id);

            // create new favorite entry
            favRepo.save(new Favorite(userBean.getCurrentUser(), c));

            // replace button
            replaceFavoriteButton(c);
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

    private void replaceFavoriteButton(Cinema c) {
        this.removeAllComponents();
        this.init();
    }
}
