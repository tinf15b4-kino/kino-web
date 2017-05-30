package de.tinf15b4.kino.web.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.vaadin.ui.Link;
import com.vaadin.ui.themes.ValoTheme;
import de.tinf15b4.kino.web.ui.SmartCinemaUi;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.data.ratedmovies.RatedMovie;
import de.tinf15b4.kino.web.rest.RestResponse;
import de.tinf15b4.kino.web.user.UserBean;
import de.tinf15b4.kino.web.util.ViewUtils;

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

        SmartCinemaUi.panel.setScrollLeft(0);
        SmartCinemaUi.panel.setScrollTop(0);

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
                image.setWidth("250px");
                image.setId("movieDetailImage");

                content.addComponent(image);

                // Info-Box
                VerticalLayout information = createInfoBox(m);
                information.setId("movieInformationForm");

                // Ratings
                createRatingsSection(m, information);

                // Playlist
                createPlaylistSection(m, information);

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

    private void createPlaylistSection(Movie m, VerticalLayout information) {
        RestResponse playlistResponse = userBean.getRestClient().getPlaylistForMovie(m.getId(), new Date(),
                new Date(new Date().getTime() + 1000L * 3600 * 24 * 7));
        if (!playlistResponse.hasError()) {
            List<Playlist> playlistEntries = Lists.newArrayList((Playlist[]) playlistResponse.getValue());

            if (!playlistEntries.isEmpty()) {
                information.addComponent(createPlaylistForm(playlistEntries));
            }
        }
    }

    private void createRatingsSection(Movie m, VerticalLayout information) {
        RestResponse ratingsResponse = userBean.getRestClient().getRatedMovies(m.getId());
        if (!ratingsResponse.hasError()) {
            List<RatedMovie> ratedMovies = Lists.newArrayList((RatedMovie[]) ratingsResponse.getValue());

            if (!ratedMovies.isEmpty()) {
                VerticalLayout ratingsForm = createRatingsForm(ratedMovies);
                information.addComponent(ratingsForm);
            }
        }
    }

    private VerticalLayout createRatingsForm(List<RatedMovie> ratedMovies) {
        VerticalLayout ratingsForm = new VerticalLayout();

        Label heading = new Label("Bewertungen");
        heading.setId("ratingsHeading");
        ratingsForm.addComponent(heading);

        for (RatedMovie rm : ratedMovies) {
            ratingsForm.addComponent(createRatingEntry(rm));
        }

        ratingsForm.setId("ratingsForm");

        return ratingsForm;
    }

    private VerticalLayout createRatingEntry(RatedMovie ratedMovie) {
        VerticalLayout ratingEntry = new VerticalLayout();
        HorizontalLayout userRow = new HorizontalLayout();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

        Label userLabel = new Label(ratedMovie.getUser().getName());
        userLabel.setPrimaryStyleName("ratingUserName");
        userLabel.setWidth(null);
        userRow.addComponent(userLabel);

        Label ratingsLabel = new Label(ratedMovie.getRating() + " / 10 ");
        ratingsLabel.setPrimaryStyleName("ratingsLabel");
        userRow.addComponent(ratingsLabel);
        userRow.setExpandRatio(ratingsLabel, 1f);

        Label dateLabel = new Label(sdf.format(ratedMovie.getTime()));
        dateLabel.setPrimaryStyleName("dateLabel");
        dateLabel.setWidth(null);
        userRow.addComponent(dateLabel);
        userRow.setComponentAlignment(dateLabel, Alignment.TOP_RIGHT);
        userRow.setPrimaryStyleName("userRow");
        userRow.setWidth("100%");

        HorizontalLayout commentRow = new HorizontalLayout();
        Label commentLabel = new Label(ratedMovie.getDescription());
        commentLabel.setPrimaryStyleName("ratingsComment");
        commentRow.addComponent(commentLabel);
        commentRow.setPrimaryStyleName("commentRow");

        ratingEntry.addComponent(userRow);
        ratingEntry.addComponent(commentRow);
        ratingEntry.addComponent(new Label("<hr />", ContentMode.HTML));
        ratingEntry.setPrimaryStyleName("ratingEntry");

        return ratingEntry;
    }

    private VerticalLayout createPlaylistForm(List<Playlist> playlistEntries) {
        VerticalLayout playlistForm = new VerticalLayout();

        Label playlistHeading = new Label("Spielplan");
        playlistHeading.setId("playlistHeading");
        playlistForm.addComponent(playlistHeading);

        List<Cinema> cinemaList = new ArrayList<>();

        for (Playlist p : playlistEntries) {
            if (!cinemaList.contains(p.getCinema())) {
                cinemaList.add(p.getCinema());
            }
        }

        for (Cinema c : cinemaList) {
            playlistForm.addComponent(createCinemaRow(c, playlistEntries));
        }

        return playlistForm;
    }

    private HorizontalLayout createCinemaRow(Cinema cinema, List<Playlist> playlists) {
        HorizontalLayout cinemaRow = new HorizontalLayout();
        cinemaRow.setPrimaryStyleName("cinemaPlaylistRow");

        Component cinemaImage = new Image(null,
                new ExternalResource(userBean.getRestClient().getCinemaPictureUrl(cinema)));
        cinemaImage.setWidth("200px");
        cinemaImage.setPrimaryStyleName("cinemaPlaylistImage");
        cinemaRow.addComponent(cinemaImage);

        HorizontalLayout playTimesTable = ViewUtils.createPlaylistTable(playlists, cinema);

        cinemaRow.addComponent(createCinemaInformation(cinema, playTimesTable));
        cinemaRow.setSizeUndefined();
        cinemaRow.setSpacing(true);

        return cinemaRow;
    }

    private VerticalLayout createCinemaInformation(Cinema cinema, HorizontalLayout playTimesTable) {
        VerticalLayout cinemaInformation = new VerticalLayout();
        cinemaInformation.setPrimaryStyleName("cinemaPlaylistInformation");

        Link cinemaLink = new Link(cinema.getName(), new ExternalResource("#!" + CinemaView.VIEW_NAME + "/" + cinema.getId()));
        cinemaLink.setId("cinemaPlaylistLink_" + cinema.getId());
        cinemaInformation.addComponent(cinemaLink);

        playTimesTable.setSizeUndefined();

        cinemaInformation.addComponent(playTimesTable);
        cinemaInformation.setSizeUndefined();
        cinemaInformation.setExpandRatio(playTimesTable, 1f);

        return cinemaInformation;
    }

    private VerticalLayout createInfoBox(Movie movie) {

        VerticalLayout infoBox = new VerticalLayout();

        // heading
        Label movieNameLabel = new Label(movie.getName());
        movieNameLabel.setId("movieNameLabel_" + movie.getId());
        infoBox.addComponent(movieNameLabel);

        // Year-Row
        HorizontalLayout yearRow = new HorizontalLayout();

        Label movieReleaseYear = new Label("2017");
        movieReleaseYear.setId("releaseYear_" + movie.getId());
        yearRow.addComponent(movieReleaseYear);

        RestResponse avgRatingResponse = userBean.getRestClient().getAverageRatingForMovie(movie.getId());
        double avgRating = 0d;
        if (!avgRatingResponse.hasError())
            avgRating = (Double) avgRatingResponse.getValue();
        Label rating = new Label(Double.toString(avgRating));
        rating.setId("rating_" + movie.getId());
        rating.setWidth(null);
        yearRow.addComponent(rating);
        yearRow.setId("yearRow_" + movie.getId());
        yearRow.setExpandRatio(movieReleaseYear, 1f);
        yearRow.setSizeFull();

        infoBox.addComponent(yearRow);

        // Duration-Row
        HorizontalLayout durationRow = new HorizontalLayout();

        Label duration = new Label(movie.getLengthMinutes() + " Minuten  -  Jetzt im Kino!");
        duration.setId("duration" + movie.getId());
        durationRow.addComponent(duration);

        Label genre = new Label("" + movie.getGenre());
        genre.setId("genre_" + movie.getId());
        genre.setWidth(null);
        durationRow.addComponent(genre);
        durationRow.setId("durationRow" + movie.getId());
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
        Label regiePerson = new Label(movie.getDirector());
        regiePerson.setPrimaryStyleName("movieInfo");
        regieRow.addComponent(regiePerson);

        infoBox.addComponent(regieRow);

        HorizontalLayout autorRow = new HorizontalLayout();

        Label autor = new Label("Autor");
        autor.setPrimaryStyleName("movieInfoLabel");
        autorRow.addComponent(autor);
        Label autorPerson = new Label(movie.getAuthor());
        autorPerson.setPrimaryStyleName("movieInfo");
        autorRow.addComponent(autorPerson);

        infoBox.addComponent(autorRow);

        HorizontalLayout studioRow = new HorizontalLayout();

        Label studio = new Label("Studio");
        studio.setPrimaryStyleName("movieInfoLabel");
        studioRow.addComponent(studio);
        Label studioPerson = new Label(movie.getStudio());
        studioPerson.setPrimaryStyleName("movieInfo");
        studioRow.addComponent(studioPerson);

        infoBox.addComponent(studioRow);

        // Description

        Label description = new Label(movie.getDescription());
        description.setId("description_" + movie.getId());

        infoBox.addComponent(description);

        return infoBox;
    }
}
