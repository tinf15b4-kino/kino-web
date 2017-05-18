package de.tinf15b4.kino.web.views;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.vaadin.server.WebBrowser;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import de.tinf15b4.kino.data.cinemas.Cinema;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;

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
                image.setWidth("250px");
                image.setId("movieDetailImage");

                content.addComponent(image);

                // Info-Box
                VerticalLayout information = createInfoBox(m);
                information.setId("movieInformationForm");

                // Ratings
                RestResponse ratingsResponse = userBean.getRestClient().getRatedMovies(m.getId());
                if (!ratingsResponse.hasError()) {
                    List<RatedMovie> ratedMovies = Lists.newArrayList((RatedMovie[]) ratingsResponse.getValue());

                    if (ratedMovies.size() > 0) {
                        VerticalLayout ratingsForm = createRatingsForm(ratedMovies);
                        information.addComponent(ratingsForm);
                    }
                }

                // Playlist
                RestResponse playlistResponse = userBean.getRestClient().getPlaylistForMovie(m.getId(), new Date(),
                        new Date(new Date().getTime() + 1000L * 3600 * 24 * 7));
                if (!playlistResponse.hasError()) {
                    List<Playlist> playlistEntries = Lists.newArrayList((Playlist[]) playlistResponse.getValue());

                    if (playlistEntries.size() > 0) {
                        information.addComponent(createPlaylistForm(playlistEntries));
                    }
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

    private VerticalLayout createRatingEntry(RatedMovie rm) {
        VerticalLayout ratingEntry = new VerticalLayout();

        HorizontalLayout userRow = new HorizontalLayout();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

        Label userLabel = new Label(rm.getUser().getName());
        userLabel.setPrimaryStyleName("ratingUserName");
        userLabel.setWidth(null);
        userRow.addComponent(userLabel);

        Label ratingsLabel = new Label(rm.getRating() + " / 10 ");
        ratingsLabel.setPrimaryStyleName("ratingsLabel");
        userRow.addComponent(ratingsLabel);
        userRow.setExpandRatio(ratingsLabel, 1f);

        Label dateLabel = new Label(sdf.format(rm.getTime()));
        dateLabel.setPrimaryStyleName("dateLabel");
        dateLabel.setWidth(null);
        userRow.addComponent(dateLabel);
        userRow.setComponentAlignment(dateLabel, Alignment.TOP_RIGHT);
        userRow.setPrimaryStyleName("userRow");
        userRow.setWidth("100%");

        HorizontalLayout commentRow = new HorizontalLayout();
        Label commentLabel = new Label(rm.getDescription()) ;
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

        return  playlistForm;
    }

    private HorizontalLayout createCinemaRow (Cinema c, List<Playlist> pE) {
        HorizontalLayout cinemaRow = new HorizontalLayout();
        cinemaRow.setPrimaryStyleName("cinemaPlaylistRow");

        Component cinemaImage = new Image(null,
                new ExternalResource(userBean.getRestClient().getCinemaPictureUrl(c)));
        cinemaImage.setHeight("200px");
        cinemaImage.setPrimaryStyleName("cinemaPlaylistImage");
        cinemaRow.addComponent(cinemaImage);

        LocalDate currentDate= LocalDate.now();

        DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("EE dd.MM", Locale.GERMAN);

        SimpleDateFormat movieTimeFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);

        HorizontalLayout playTimesTable = new HorizontalLayout();
        playTimesTable.setPrimaryStyleName("playTimesTable");

        for (int i = 0; i<6; i++) {

            // Add weekdays to Table
            VerticalLayout tmpPlaylistColumn = new VerticalLayout();
            Label dateRow = new Label(currentDate.plusDays(i).format(formattedDate));
            dateRow.setPrimaryStyleName("dateEntry");
            tmpPlaylistColumn.addComponent(dateRow);

            // Add playtimes to table
            for (Playlist p : pE) {
                LocalDate playDate = p.getTime().toInstant().atZone(ZoneId.of("GMT+1")).toLocalDate();
                if (currentDate.plusDays(i).isEqual(playDate)) {
                    if (p.getCinema().equals(c)) {
                        Label timeRow = new Label(movieTimeFormat.format(p.getTime()));
                        timeRow.setPrimaryStyleName("timeEntry");
                        tmpPlaylistColumn.addComponent(timeRow);
                    }
                }
            }

            tmpPlaylistColumn.setPrimaryStyleName("playListColumn");
            playTimesTable.addComponent(tmpPlaylistColumn);

        }

        cinemaRow.addComponent(createCinemaInformation(c, playTimesTable));
        cinemaRow.setSizeUndefined();
        cinemaRow.setSpacing(true);

        return cinemaRow;
    }

    private VerticalLayout createCinemaInformation(Cinema c, HorizontalLayout playTimesTable) {
        VerticalLayout cinemaInformation = new VerticalLayout();
        cinemaInformation.setPrimaryStyleName("cinemaPlaylistInformation");

        Label cinemaName = new Label(c.getName());
        cinemaName.setPrimaryStyleName("cinemaPlaylistName");
        cinemaInformation.addComponent(cinemaName);

        playTimesTable.setSizeUndefined();
     
        cinemaInformation.addComponent(playTimesTable);
        cinemaInformation.setSizeUndefined();
        cinemaInformation.setExpandRatio(playTimesTable, 1f);


        return cinemaInformation;
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
