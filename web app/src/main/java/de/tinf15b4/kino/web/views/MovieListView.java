package de.tinf15b4.kino.web.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieFilterData;
import de.tinf15b4.kino.data.movies.MovieService;
import de.tinf15b4.kino.data.ratedmovies.RatedMovieService;
import de.tinf15b4.kino.web.components.AgeControlCheckboxes;
import de.tinf15b4.kino.web.components.DateTimeFilter;
import de.tinf15b4.kino.web.components.GenreCheckboxes;
import de.tinf15b4.kino.web.components.PriceFilter;
import de.tinf15b4.kino.web.controllers.PictureController;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringView(name = MovieListView.VIEW_NAME)
public class MovieListView extends VerticalLayout implements View {
    private static final long serialVersionUID = -2619886715725464558L;

    public static final String VIEW_NAME = "movies";

    @Autowired
    private MovieService movieService;
    private MovieFilterData filterData;

    private VerticalLayout movieLayout;

    @Autowired
    private RatedMovieService ratedMovieService;

    @PostConstruct
    public void init() {
        this.setMargin(true);
        this.setSpacing(true);
        filterData = new MovieFilterData();

        Component filter = createFilter();
        filter.setId("movieFilter");
        addComponent(filter);
        addComponent(createMovies());
    }

    private Component createMovies() {
        movieLayout = new VerticalLayout();
        for (Movie m : getFilteredMovies()) {
            HorizontalLayout row = new HorizontalLayout();
            row.setWidth(100, Unit.PERCENTAGE);

            Image image = new Image(null, new ExternalResource(PictureController.getMoviePictureUrl(m)));

            image.setHeight("200px");
            row.addComponent(image);

            Component movieInfoBox = createMovieInfoBox(m);
            movieInfoBox.setId("movieInfoBox_" + m.getId());

            row.addComponent(movieInfoBox);
            row.setComponentAlignment(movieInfoBox, Alignment.TOP_LEFT);

            row.setExpandRatio(movieInfoBox, 1f);
            row.setSpacing(true);
            row.setId("movieRow_" + m.getId());

            movieLayout.addComponent(row);
        }
        return movieLayout;
    }

    private Component createMovieInfoBox(Movie m) {

        VerticalLayout movieInfoBox = new VerticalLayout();

        Link l = new Link(m.getName(), new ExternalResource("#!" + MovieView.VIEW_NAME + "/" + m.getId()));
        l.setId("movieListLink_" + m.getId());
        movieInfoBox.addComponent(l);
        movieInfoBox.setComponentAlignment(l, Alignment.TOP_LEFT);

        movieInfoBox.addComponent(new Label("Länge: " + m.getLengthMinutes() + " Minuten"));
        movieInfoBox.addComponent(new Label("Genre: " + m.getGenre()));
        movieInfoBox.addComponent(new Label("Altersfreigabe: " + m.getAgeControl()));
        movieInfoBox.addComponent(
                new Label("Durschschnittliche Bewertung: " + ratedMovieService.getAverageRatingForMovie(m)));
        movieInfoBox.addComponent(new Label(m.getDescription()));


        return movieInfoBox;
    }

    private List<Movie> getFilteredMovies() {
        return movieService.allmightyFilter(filterData);
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
