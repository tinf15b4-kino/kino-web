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
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.data.ratedmovies.RatedMovie;
import de.tinf15b4.kino.web.rest.RestResponse;
import de.tinf15b4.kino.web.user.UserBean;

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
                HorizontalLayout content = new HorizontalLayout();

                // Image
                Component image = new Image(null, new ExternalResource(userBean.getRestClient().getMoviePictureUrl(m)));
                image.setHeight("400px");
                image.setId("movieImage_" + m.getId());

                content.addComponent(image);

                // Info-Box
                VerticalLayout information = new VerticalLayout();
                information = createInfoBox(m);

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

                        Panel ratingsPanel = new Panel("Bewertungen", ratings);
                        ratingsPanel.setId("ratingsPanel_" + m.getId());
                        information.addComponent(ratingsPanel);
                    }

                    RestResponse playlistResponse = userBean.getRestClient().getPlaylistForMovie(m.getId(), new Date(),
                            null);
//                            new Date(new Date().getTime() + 1000L * 3600 * 24 * 7));
                    if (!playlistResponse.hasError()) {
                        List<Playlist> playlistEntries = Lists.newArrayList((Playlist[]) playlistResponse.getValue());

                        if (playlistEntries.size() > 0) {
                            GridLayout playtimes = new GridLayout(3, 1);
                            playtimes.setMargin(true);
                            playtimes.setSpacing(true);
                            playtimes.setSizeFull();

                            for (Playlist p : playlistEntries) {
                                SimpleDateFormat sdf = new SimpleDateFormat("E, dd.MM.yyyy HH:mm", Locale.GERMANY);
                                NumberFormat pricef = NumberFormat.getCurrencyInstance(Locale.GERMANY);
                                playtimes.addComponent(new Label(sdf.format(p.getTime())));
                                playtimes.addComponent(new Link(p.getCinema().getName(), new ExternalResource(
                                        "#!" + CinemaView.VIEW_NAME + "/" + p.getCinema().getId())));
                                playtimes.addComponent(new Label(pricef.format(p.getPrice() / 100.0)));
                            }

                            Panel playtimesPanel = new Panel("Spielplan", playtimes);
                            playtimesPanel.setId("playtimesPanel_" + m.getId());
                            information.addComponent(playtimesPanel);
                        }
                        information.setMargin(new MarginInfo(false, true));
                        content.addComponent(information);
                        content.setSizeFull();
                        content.setExpandRatio(information, 1f);
                        content.setMargin(true);
                        content.setSpacing(true);
                        this.addComponent(content);
                    }
                }
            }
        }
    }

    private VerticalLayout createInfoBox(Movie m) {

        VerticalLayout infoBox = new VerticalLayout();

        // heading
        Label movieNameLabel = new Label(m.getName());
        movieNameLabel.setId("movieNameLabel_" + m.getId());
        infoBox.addComponent(movieNameLabel);

        // Year-Row
        HorizontalLayout yearRow = new HorizontalLayout();

        Label movieReleaseYear = new Label("2017");
        movieReleaseYear.setId("releaseYear_" + m.getId());
        yearRow.addComponent(movieReleaseYear);

        RestResponse avgRatingResponse = userBean.getRestClient().getAverageRatingForMovie(m.getId());
        double avgRating = 0d;
        if (!avgRatingResponse.hasError())
            avgRating = (Double) avgRatingResponse.getValue();
        Label rating = new Label("" + avgRating);
        rating.setId("rating_" + m.getId());
        rating.setWidth(null);
        yearRow.addComponent(rating);
        yearRow.setId("yearRow_" + m.getId());
        yearRow.setExpandRatio(movieReleaseYear, 1f);
        yearRow.setSizeFull();

        infoBox.addComponent(yearRow);

        // Duration-Row
        HorizontalLayout durationRow = new HorizontalLayout();

        Label duration = new Label(m.getLengthMinutes() + " Minuten  -  Jetzt im Kino!");
        duration.setId("duration" + m.getId());
        durationRow.addComponent(duration);

        Label genre = new Label("" + m.getGenre());
        genre.setId("genre_" + m.getId());
        genre.setWidth(null);
        durationRow.addComponent(genre);
        durationRow.setId("durationRow" + m.getId());
        durationRow.setExpandRatio(duration, 1f);
        durationRow.setSizeFull();

        infoBox.addComponent(durationRow);

        // Strich hinzufügen
        infoBox.addComponent(new Label("<hr />", ContentMode.HTML));

        // Informationen (Regie, Autor, Studio, Beschreibung)
        HorizontalLayout regieRow = new HorizontalLayout();

        Label regie = new Label("Regie");
        regie.setPrimaryStyleName("movieInfoLabel");
        regieRow.addComponent(regie);
        Label regiePerson = new Label(m.getDirector());
        regiePerson.setPrimaryStyleName("movieInfo");
        regieRow.addComponent(regiePerson);

        infoBox.addComponent(regieRow);

        HorizontalLayout autorRow = new HorizontalLayout();

        Label autor = new Label("Autor");
        autor.setPrimaryStyleName("movieInfoLabel");
        autorRow.addComponent(autor);
        Label autorPerson = new Label(m.getAuthor());
        autorPerson.setPrimaryStyleName("movieInfo");
        autorRow.addComponent(autorPerson);

        infoBox.addComponent(autorRow);

        HorizontalLayout studioRow = new HorizontalLayout();

        Label studio = new Label("Studio");
        studio.setPrimaryStyleName("movieInfoLabel");
        studioRow.addComponent(studio);
        Label studioPerson = new Label(m.getStudio());
        studioPerson.setPrimaryStyleName("movieInfo");
        studioRow.addComponent(studioPerson);

        infoBox.addComponent(studioRow);

        // Description

        Label description = new Label(m.getDescription());
        description.setId("description_" + m.getId());

        infoBox.addComponent(description);

        return infoBox;
    }
}
