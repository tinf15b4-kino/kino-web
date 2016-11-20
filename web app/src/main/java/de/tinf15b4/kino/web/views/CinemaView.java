package de.tinf15b4.kino.web.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import de.tinf15b4.kino.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SpringView(name = CinemaView.VIEW_NAME)
public class CinemaView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "cinema";

    @Autowired
    private CinemaRepository repo;

    @Autowired
    private FavoriteRepository favRepo;

    @Autowired
    private PlaylistRepository playlistRepo;

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

            this.addComponent(favButtonContainer);

            this.addComponent(new Label(c.getAddress(), ContentMode.PREFORMATTED));
            replaceFavoriteButton(c);

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

            this.addComponent(new Panel(movies));
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
        List<Favorite> existing = favRepo.findByCinemaId(id);
        if (existing.isEmpty()) {
            Cinema c = repo.findOne(id);

            // create new favorite entry
            favRepo.save(new Favorite(c));

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
}
