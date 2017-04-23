package de.tinf15b4.kino.web.views;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.api.rest.RestResponse;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieFilterData;
import de.tinf15b4.kino.data.users.UserBean;
import de.tinf15b4.kino.web.components.AgeControlCheckboxes;
import de.tinf15b4.kino.web.components.DateTimeFilter;
import de.tinf15b4.kino.web.components.GenreCheckboxes;
import de.tinf15b4.kino.web.components.PriceFilter;
import de.tinf15b4.kino.web.controllers.PictureController;

@SpringView(name = MovieListView.VIEW_NAME)
public class MovieListView extends VerticalLayout implements View {
    private static final long serialVersionUID = -2619886715725464558L;

    public static final String VIEW_NAME = "movies";

    @Autowired
    private UserBean userBean;

    private MovieFilterData filterData;

    private VerticalLayout movieLayout;

    @PostConstruct
    public void init() {
        this.setMargin(true);
        this.setSpacing(true);
        filterData = new MovieFilterData();

        addComponent(createFilter());
        addComponent(createMovies());
    }

    private Component createMovies() {
        movieLayout = new VerticalLayout();
        for (Movie m : getFilteredMovies()) {
            HorizontalLayout row = new HorizontalLayout();
            row.setWidth(100, Unit.PERCENTAGE);

            Image image = new Image(null, new ExternalResource(PictureController.getMoviePictureUrl(m)));

            image.setHeight("100px");
            row.addComponent(image);

            Link l = new Link(m.getName(), new ExternalResource("#!" + MovieView.VIEW_NAME + "/" + m.getId()));

            row.addComponent(l);
            row.setComponentAlignment(l, Alignment.MIDDLE_LEFT);

            row.setExpandRatio(l, 1f);
            row.setSpacing(true);
            movieLayout.addComponent(row);
        }
        return movieLayout;
    }

    private List<Movie> getFilteredMovies() {
        RestResponse response = userBean.getRestClient().getFilteredMovies(filterData);
        if (!response.hasError())
            return Lists.newArrayList((Movie[]) response.getValue());
        return new ArrayList<>();
    }

    private Component createFilter() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.addComponent(new AgeControlCheckboxes(this, filterData));
        layout.addComponent(new GenreCheckboxes(this, filterData));
        layout.addComponent(new DateTimeFilter(this, filterData));
        layout.addComponent(new PriceFilter(this, filterData));
        return layout;
    }

    public void filterChanged() {
        removeComponent(movieLayout);
        addComponent(createMovies(), 1);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
