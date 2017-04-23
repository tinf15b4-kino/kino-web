package de.tinf15b4.kino.web.views;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.api.rest.RestResponse;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.search.SearchResult;
import de.tinf15b4.kino.data.users.UserBean;

@SpringView(name = SearchResultsView.VIEW_NAME)
public class SearchResultsView extends VerticalLayout implements View {
    private static final long serialVersionUID = 654947209531243500L;

    public static final String VIEW_NAME = "search";

    @Autowired
    private UserBean userBean;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        String searchTerm = event.getParameters();

        RestResponse response = userBean.getRestClient().search(searchTerm);
        if (!response.hasError()) {
            SearchResult r = (SearchResult) response.getValue();

            this.removeAllComponents();
            this.setMargin(true);

            if (r.hasMovies()) {
                VerticalLayout movieList = new VerticalLayout();
                movieList.setSpacing(true);
                movieList.setMargin(true);
                for (Movie m : r.getMovies()) {
                    Link l = new Link(m.getName(), new ExternalResource("#!" + MovieView.VIEW_NAME + "/" + m.getId()));
                    movieList.addComponent(l);
                }
                this.addComponent(new Panel("Filme", movieList));
            }

            if (r.hasCinemas()) {
                VerticalLayout cinemaList = new VerticalLayout();
                cinemaList.setSpacing(true);
                cinemaList.setMargin(true);
                for (Cinema c : r.getCinemas()) {
                    Link l = new Link(c.getName(), new ExternalResource("#!" + CinemaView.VIEW_NAME + "/" + c.getId()));
                    cinemaList.addComponent(l);
                }
                this.addComponent(new Panel("Kinos", cinemaList));
            }

            if (!r.hasMovies() && !r.hasCinemas()) {
                this.addComponent(new Label("Leider kein Ergebnis :("));
            }
        }
    }
}
