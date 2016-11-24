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
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.tinf15b4.kino.data.Cinema;
import de.tinf15b4.kino.data.Favorite;
import de.tinf15b4.kino.data.users.UserLoginBean;
import de.tinf15b4.kino.web.controllers.FavoriteListController;

@SpringView(name = FavoriteListView.VIEW_NAME)
public class FavoriteListView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "favourites";

    private VerticalLayout content;

    @Autowired
    private FavoriteListController controller;

    @Autowired
    private UserLoginBean userBean;

    @PostConstruct
    private void init() {
        if (userBean.isUserLoggedIn()) {
            List<Favorite> l = controller.getAllFavesForCurrentUser();

            this.addComponent(new Label("Favorite Cinemas"));

            content = new VerticalLayout();
            content.setMargin(true);
            content.setSpacing(true);
            this.addComponent(new Panel(content));

            if (l.isEmpty()) {
                content.addComponent(new Label("No Favorite Cinemas yet :("));
            } else {
                for (Favorite f : l) {
                    content.addComponent(buildListEntry(f.getCinema()));
                }
            }
        } else {
            this.addComponent(new Label("Sie müssen sich Anmelden"));
        }
    }
    
    private Component buildListEntry(Cinema c) {
        HorizontalLayout pav = new HorizontalLayout();
        pav.setWidth(100, Unit.PERCENTAGE);
        Link l = new Link(c.getName(), new ExternalResource("#!" + CinemaView.VIEW_NAME + "/" + c.getId()));
        pav.addComponent(l);
        pav.setComponentAlignment(l, Alignment.MIDDLE_LEFT);

        Button removeBtn = new Button("Remove");
        removeBtn.addClickListener(e -> removeFromFavorites(c.getId(), pav));
        pav.addComponent(removeBtn);
        pav.setComponentAlignment(removeBtn, Alignment.MIDDLE_RIGHT);

        return pav;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    private void removeFromFavorites(long cinemaId, HorizontalLayout row) {
        if (controller.isCinemaFavorite(cinemaId)) {
            // remove favorite entry
            String cinemaName = controller.getFave(cinemaId).getCinema().getName();
            controller.unmarkFavorite(cinemaId);

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

    private void undoRemove(long cinemaId, HorizontalLayout row) {
        controller.markFavorite(cinemaId);

        content.replaceComponent(row, buildListEntry(controller.getFave(cinemaId).getCinema()));
    }
}
