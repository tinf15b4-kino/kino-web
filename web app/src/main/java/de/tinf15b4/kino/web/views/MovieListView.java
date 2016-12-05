package de.tinf15b4.kino.web.views;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieFilterData;
import de.tinf15b4.kino.data.movies.MovieService;

@SpringView(name = MovieListView.VIEW_NAME)
public class MovieListView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "movies";

    @Autowired
    private MovieService movieService;
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

            // Picture
            StreamSource streamSource = new StreamResource.StreamSource() {
                @Override
                public ByteArrayInputStream getStream() {
                    return (m.getCover() == null) ? null : new ByteArrayInputStream(m.getCover());
                }
            };

            StreamResource imageResource = new StreamResource(streamSource, "");

            Image image = new Image(null, imageResource);

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
        return movieService.allmightyFilter(filterData);
    }

    private Component createFilter() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.addComponent(new AgeControlCheckboxes(this, filterData));
        layout.addComponent(new GenreCheckboxes(this, filterData));
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
