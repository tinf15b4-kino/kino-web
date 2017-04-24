package de.tinf15b4.kino.web.views;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.api.rest.PictureController;
import de.tinf15b4.kino.api.rest.RestResponse;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.data.ratedmovies.RatedMovie;
import de.tinf15b4.kino.data.users.UserBean;

@SpringView(name = MovieView.VIEW_NAME)
public class MovieView extends VerticalLayout implements View {
    private static final long serialVersionUID = -8396322298622735942L;

    public static final String VIEW_NAME = "movie";

    @Autowired
    private UserBean userBean;

    @Override
    public void enter(ViewChangeEvent event) {
        this.setMargin(true);
        this.setSpacing(true);

        this.removeAllComponents();
        if (event.getParameters() != null) {
            String idStr = event.getParameters();
            long id = Long.parseLong(idStr);
            RestResponse response = userBean.getRestClient().getMovie(id);
            if (response.hasError()) {
                // This id does not exist
                this.getUI().getNavigator().navigateTo(MovieListView.VIEW_NAME);
            } else {
                Movie m = (Movie) response.getValue();
                VerticalLayout left = new VerticalLayout();
                VerticalLayout right = new VerticalLayout();
                right.setMargin(true);
                left.addComponent(new Label(m.getName()));

                // Picture
                Component image = new Image(null, new ExternalResource(PictureController.getMoviePictureUrl(m)));
                image.setHeight("150px");
                left.addComponent(image);

                RestResponse averageRatingResponse = userBean.getRestClient().getAverageRatingForMovie(m.getId());
                double averageRating = 0d;
                if (!averageRatingResponse.hasError())
                    averageRating = (double) averageRatingResponse.getValue();

                right.addComponent(new Label("Länge: " + m.getLengthMinutes() + " Minuten"));
                right.addComponent(new Label("Genre: " + m.getGenre()));
                right.addComponent(new Label("Altersfreigabe: " + m.getAgeControl()));
                right.addComponent(new Label("Durschschnittliche Bewertung: " + averageRating));
                right.addComponent(new Label(m.getDescription()));
                this.addComponent(new HorizontalLayout(left, right));

                RestResponse ratingsResponse = userBean.getRestClient().getRatedMovies(m.getId());
                if (!ratingsResponse.hasError()) {
                    List<RatedMovie> ratedMovies = Lists.newArrayList((RatedMovie[]) ratingsResponse.getValue());

                    if (ratedMovies.size() > 0) {
                        GridLayout ratings = new GridLayout(4, 1);
                        ratings.setMargin(true);
                        ratings.setSpacing(true);
                        ratings.setSizeFull();

                        for (RatedMovie rm : ratedMovies) {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY);
                            ratings.addComponent(new Label(rm.getUser().getName()));
                            ratings.addComponent(new Label(rm.getRating() + ""));
                            ratings.addComponent(new Label(sdf.format(rm.getTime())));
                            ratings.addComponent(new Label(rm.getDescription()));
                        }

                        this.addComponent(new Panel("Bewertungen", ratings));
                    }
                }

                RestResponse playlistResponse = userBean.getRestClient().getPlaylistForMovie(m.getId(), new Date(),
                        new Date(new Date().getTime() + 1000L * 3600 * 24 * 7));
                if (!playlistResponse.hasError()) {
                    List<Playlist> playlistEntries = Lists.newArrayList((Playlist[]) playlistResponse.getValue());

                    if (playlistEntries.size() > 0) {
                        GridLayout playtimes = new GridLayout(3, 1);
                        playtimes.setMargin(true);
                        playtimes.setSpacing(true);
                        playtimes.setSizeFull();

                        for (Playlist p : playlistEntries) {
                            SimpleDateFormat sdf = new SimpleDateFormat("E HH:mm", Locale.GERMANY);
                            NumberFormat pricef = NumberFormat.getCurrencyInstance(Locale.GERMANY);
                            playtimes.addComponent(new Label(sdf.format(p.getTime())));
                            playtimes.addComponent(new Link(p.getCinema().getName(),
                                    new ExternalResource("#!" + CinemaView.VIEW_NAME + "/" + p.getCinema().getId())));
                            playtimes.addComponent(new Label(pricef.format(p.getPrice() / 100.0)));
                        }

                        this.addComponent(new Panel("Spielplan", playtimes));
                    }
                }
            }
        }
    }
}
