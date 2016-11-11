package de.tinf15b4.kino.web;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import de.tinf15b4.kino.data.Cinema;
import de.tinf15b4.kino.data.CinemaRepository;
import de.tinf15b4.kino.data.Favorite;
import de.tinf15b4.kino.data.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringView(name = FavoriteListView.VIEW_NAME)
public class FavoriteListView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "favorites";

    @Autowired
    private FavoriteRepository repo;

    @Autowired
    private CinemaRepository cineRepo;

    private VerticalLayout content;

    @PostConstruct
    private void init() {
        this.addComponent(new Label("Favorite Cinemas"));

        content = new VerticalLayout();
        content.setMargin(true);
        content.setSpacing(true);
        this.addComponent(new Panel(content));
    }

    private Component buildListEntry(Cinema c) {
        HorizontalLayout pav = new HorizontalLayout();
        pav.setWidth(100, Unit.PERCENTAGE);
        Link l = new Link(c.getName(), new ExternalResource(
                "#!" + CinemaView.VIEW_NAME + "/" + c.getId()));
        pav.addComponent(l);
        pav.setComponentAlignment(l, Alignment.MIDDLE_LEFT);

        Button removeBtn = new Button("Remove");
        removeBtn.addClickListener(e -> removeFromFavorites(c.getId(), pav));
        pav.addComponent(removeBtn);
        pav.setComponentAlignment(removeBtn, Alignment.MIDDLE_RIGHT);

        return pav;
    }

    @Override
    @Transactional
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        content.removeAllComponents();

        List<Favorite> l = repo.findAll();
        if (l.isEmpty()) {
            content.addComponent(new Label("No Favorite Cinemas yet :("));
        } else {
            for (Favorite f : l) {
                content.addComponent(buildListEntry(f.getCinema()));
            }
        }
    }

    @Transactional
    private void removeFromFavorites(long cinemaId, HorizontalLayout row) {
        List<Favorite> existing = repo.findByCinemaId(cinemaId);
        if (!existing.isEmpty()) {
            // remove favorite entry
            String cinemaName = existing.get(0).getCinema().getName();
            repo.delete(existing);

            row.removeAllComponents();

            GridLayout g = new GridLayout(2, 1);

            Label l = new Label(String.format("Favorite Cinema \"%s\" has been removed", cinemaName));
            g.addComponent(l, 0, 0);
            g.setComponentAlignment(l, Alignment.MIDDLE_LEFT);

            Button undo = new Button("Undo");
            undo.setStyleName(ValoTheme.BUTTON_LINK);
            undo.addClickListener(e -> undoRemove(cinemaId, row));
            g.addComponent(undo, 1, 0);
            g.setComponentAlignment(undo, Alignment.MIDDLE_LEFT);
            g.setColumnExpandRatio(1, 1.0f);

            row.addComponent(g);
        }
    }

    @Transactional
    private void undoRemove(long cinemaId, HorizontalLayout row) {
        Cinema c = cineRepo.findOne(cinemaId);

        repo.save(new Favorite(c));

        content.replaceComponent(row, buildListEntry(c));
    }
}
