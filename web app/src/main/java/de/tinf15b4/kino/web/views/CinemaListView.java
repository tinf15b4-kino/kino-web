package de.tinf15b4.kino.web.views;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.users.UserBean;
import de.tinf15b4.kino.web.controllers.PictureController;
import de.tinf15b4.kino.web.util.CinemaFavoriteUtils;
import de.tinf15b4.kino.web.util.ToggleFavoriteListener;

@SpringView(name = CinemaListView.VIEW_NAME)
public class CinemaListView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "cinemas";

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserBean userBean;

    @PostConstruct
    public void init() {
        this.setMargin(true);
        this.setSpacing(true);

        for (Cinema c : cinemaService.findAll()) {
            HorizontalLayout row = new HorizontalLayout();
            row.setWidth(100, Unit.PERCENTAGE);
            row.addStyleName("cinemaRow");

            // Picture
            Component image = new Image(null, new ExternalResource(PictureController.getCinemaPictureUrl(c)));
            image.addStyleName("cinemaListImage");
            image.setHeight("200px");
            row.addComponent(image);
            
            Component cinemaInfoBox = createCinemaInfoBox(c);
            row.addComponent(cinemaInfoBox);

            row.setExpandRatio(cinemaInfoBox, 1f);
            row.setSpacing(true);
            this.addComponent(row);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

    private Component createCinemaInfoBox(Cinema c) {

        VerticalLayout cinemaInfoBox = new VerticalLayout();

        Link l = new Link(c.getName(), new ExternalResource("#!" + CinemaView.VIEW_NAME + "/" + c.getId()));
        l.addStyleName("cinemaListLink");
        cinemaInfoBox.addComponent(l);
        cinemaInfoBox.setComponentAlignment(l, Alignment.TOP_LEFT);

        cinemaInfoBox.addComponent(new Label(c.getAddress(), ContentMode.PREFORMATTED));

        createFavoriteBtn(c.getId(), cinemaInfoBox);


        return cinemaInfoBox;
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

            button.addStyleName("list");

            row.addComponent(button);
            row.setComponentAlignment(button, Alignment.BOTTOM_LEFT);
        }
    }

    private void createFavoriteBtn(long cinemaId, VerticalLayout row) {
        FavoriteBtnManager mgr = new FavoriteBtnManager();
        mgr.row = row;
        mgr.cinemaId = cinemaId;
        mgr.recreateBtn();
    }

    private void updateView() {
        removeAllComponents();
        init();
    }
}
