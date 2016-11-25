package de.tinf15b4.kino.web.views;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieService;

@SpringView(name = MovieListView.VIEW_NAME)
public class MovieListView extends VerticalLayout implements View{
    public static final String VIEW_NAME = "movies";

    @Autowired
    private MovieService movieService;

    @PostConstruct
    public void init() {
        this.setMargin(true);
        this.setSpacing(true);

        for (Movie m : movieService.findAll()) {
            Panel p = new Panel();
            Link l = new Link(m.getName(), new ExternalResource("#!" + MovieView.VIEW_NAME + "/" + m.getId()));
            p.setContent(l);

            this.addComponent(p);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
