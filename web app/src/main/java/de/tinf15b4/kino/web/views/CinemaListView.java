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
import de.tinf15b4.kino.data.users.UserBean;
import de.tinf15b4.kino.web.util.CinemaFavoriteUtils;
import de.tinf15b4.kino.web.util.RestClient;
import de.tinf15b4.kino.web.util.RestResponse;
import de.tinf15b4.kino.web.util.ToggleFavoriteListener;

@SpringView(name = CinemaListView.VIEW_NAME)
public class CinemaListView extends VerticalLayout implements View {
    private static final long serialVersionUID = -7735249245890741447L;

    public static final String VIEW_NAME = "cinemas";

    @Autowired
    private UserBean userBean;

    @PostConstruct
    public void init() {
        this.setMargin(true);
        this.setSpacing(true);

        RestResponse cinemaResponse = userBean.getRestClient().getCinemas();
        if (!cinemaResponse.hasError()) {
            for (Cinema c : (Cinema[]) cinemaResponse.getValue()) {
                HorizontalLayout row = new HorizontalLayout();
                row.setWidth(100, Unit.PERCENTAGE);
                row.setId("cinemaRow_" + c.getId());

                // Picture
                Component image = new Image(null, new ExternalResource(RestClient.getCinemaPictureUrl(c)));
                image.setId("cinemaImage_" + c.getId());
                image.setHeight("200px");
                row.addComponent(image);

                // Info-Box
                Component movieInfoBox = createCinemaInfoBox(c);
                row.addComponent(movieInfoBox);

                row.setExpandRatio(movieInfoBox, 1f);
                row.setSpacing(true);
                this.addComponent(row);
            }
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
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

            button = CinemaFavoriteUtils.createFavoriteButton(cinemaId, userBean, this);

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
}
