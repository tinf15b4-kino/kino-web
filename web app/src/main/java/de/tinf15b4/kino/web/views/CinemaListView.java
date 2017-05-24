package de.tinf15b4.kino.web.views;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.web.rest.RestResponse;
import de.tinf15b4.kino.web.user.UserBean;
import de.tinf15b4.kino.web.util.ViewUtils;

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
                HorizontalLayout cinemaRow = ViewUtils.createCinemaRow(c, userBean);
                this.addComponent(cinemaRow);
            }
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        //do nothing
    }
}
