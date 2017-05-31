package de.tinf15b4.kino.web.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.vaadin.ui.Link;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinema;
import de.tinf15b4.kino.web.rest.RestResponse;
import de.tinf15b4.kino.web.user.UserBean;
import de.tinf15b4.kino.web.util.CinemaFavoriteUtils;
import de.tinf15b4.kino.web.util.ToggleFavoriteListener;
import de.tinf15b4.kino.web.util.ViewUtils;

@SpringView(name = CinemaView.VIEW_NAME)
public class CinemaView extends VerticalLayout implements View, ToggleFavoriteListener {
    private static final long serialVersionUID = 4293327123523820239L;

    public static final String VIEW_NAME = "cinema";

    @Autowired
    private UserBean userBean;

    private Component favoriteButton;

    private Cinema c;

    @Override
    public void enter(ViewChangeEvent event) {
        this.setMargin(true);
        this.setSpacing(true);

        this.removeAllComponents();
        if (event.getParameters() != null) {
            String idStr = event.getParameters();
            long id = Long.parseLong(idStr);
            RestResponse response = userBean.getRestClient().getCinema(id);
            if (!response.hasError()) {
                c = (Cinema) response.getValue();
                if (c == null) {
                    this.getUI().getNavigator().navigateTo(CinemaListView.VIEW_NAME);
                } else {
                    HorizontalLayout content = new HorizontalLayout();

                    // image
                    Component image = new Image(null,
                            new ExternalResource(userBean.getRestClient().getCinemaPictureUrl(c)));
                    image.setWidth("250px");
                    image.setId("cinemaImage");
                    content.addComponent(image);

                    // Information
                    VerticalLayout informationForm = new VerticalLayout();
                    informationForm.setId("cinemaInformationForm");

                    informationForm.addComponent(createCinemaInformation(c));

                    // ratings
                    createRatingSection(informationForm);

                    // playtimes
                    createPlaylistSection(informationForm);

                    informationForm.setMargin(new MarginInfo(false, true));
                    content.addComponent(informationForm);
                    content.setSizeFull();
                    content.setExpandRatio(informationForm, 1f);
                    content.setMargin(true);
                    content.setSpacing(true);
                    this.addComponent(content);
                }
            }
        }
    }

    private void createPlaylistSection(VerticalLayout informationForm) {
        RestResponse playlistResponse = userBean.getRestClient().getPlaylistForCinemas(c.getId(),
                new Date(), new Date(new Date().getTime() + 1000L * 3600 * 24 * 7));
        if (!playlistResponse.hasError()) {
            List<Playlist> playlistEntries = Lists.newArrayList((Playlist[]) playlistResponse.getValue());

            if (!playlistEntries.isEmpty()) {
                informationForm.addComponent(createPlaylistForm(playlistEntries));
            }
        }
    }

    private void createRatingSection(VerticalLayout informationForm) {
        RestResponse ratedCinemaResponse = userBean.getRestClient().getRatedCinemas(c.getId());
        if (!ratedCinemaResponse.hasError()) {
            List<RatedCinema> ratedCinemas = Lists
                    .newArrayList((RatedCinema[]) ratedCinemaResponse.getValue());

            if (!ratedCinemas.isEmpty()) {
                VerticalLayout ratingsForm = createRatingsForm(ratedCinemas);
                informationForm.addComponent(ratingsForm);
            }
        }
    }

    private VerticalLayout createPlaylistForm(List<Playlist> playlistEntries) {
        VerticalLayout playlistForm = new VerticalLayout();

        Label playlistHeading = new Label("Spielplan");
        playlistHeading.setId("playlistHeading");
        playlistForm.addComponent(playlistHeading);

        List<Movie> movieList = new ArrayList<>();

        for (Playlist p : playlistEntries) {

            if (!movieList.contains(p.getMovie())) {
                movieList.add(p.getMovie());
            }
        }

        for (Movie m : movieList) {

            playlistForm.addComponent(createMovieRow(m, playlistEntries));
        }

        return playlistForm;
    }

    private HorizontalLayout createMovieRow(Movie movie, List<Playlist> playlists) {
        HorizontalLayout movieRow = new HorizontalLayout();
        movieRow.setPrimaryStyleName("moviePlaylistRow");

        Component movieImage = new Image(null,
                new ExternalResource(userBean.getRestClient().getMoviePictureUrl(movie)));
        movieImage.setWidth("200px");
        movieImage.setPrimaryStyleName("moviePlaylistImage");
        movieRow.addComponent(movieImage);

        HorizontalLayout playTimesTable = ViewUtils.createPlaylistTable(playlists, c);

        movieRow.addComponent(createMovieInformation(movie, playTimesTable));
        movieRow.setSizeUndefined();
        movieRow.setSpacing(true);

        return movieRow;
    }

    private VerticalLayout createMovieInformation(Movie movie, HorizontalLayout playTimesTable) {
        VerticalLayout movieInformation = new VerticalLayout();
        movieInformation.setPrimaryStyleName("moviePlaylistInformation");

        Link movieLink = new Link(movie.getName(), new ExternalResource("#!" + MovieView.VIEW_NAME + "/" + movie.getId()));
        movieLink.setPrimaryStyleName("moviePlaylistLink_" + movie.getId());
        movieInformation.addComponent(movieLink);

        playTimesTable.setSizeUndefined();

        movieInformation.addComponent(playTimesTable);
        movieInformation.setSizeUndefined();
        movieInformation.setExpandRatio(playTimesTable, 1f);

        return movieInformation;
    }

    private VerticalLayout createRatingsForm(List<RatedCinema> ratedCinemas) {
        VerticalLayout ratingsForm = new VerticalLayout();
        ratingsForm.setId("cinemaRatingsForm");

        Label heading = new Label("Bewertungen");
        heading.setId("ratingsHeading");
        ratingsForm.addComponent(heading);

        for (RatedCinema rC : ratedCinemas) {

            ratingsForm.addComponent(createRatingEntry(rC));

        }

        return ratingsForm;

    }

    private VerticalLayout createRatingEntry(RatedCinema ratedCinema) {
        VerticalLayout ratingEntry = new VerticalLayout();

        HorizontalLayout userRow = new HorizontalLayout();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

        Label userLabel = new Label(ratedCinema.getUser().getName());
        userLabel.setPrimaryStyleName("ratingUserName");
        userLabel.setWidth(null);
        userRow.addComponent(userLabel);

        Label ratingsLabel = new Label(ratedCinema.getRating() + " / 10 ");
        ratingsLabel.setPrimaryStyleName("ratingsLabel");
        userRow.addComponent(ratingsLabel);
        userRow.setExpandRatio(ratingsLabel, 1f);

        Label dateLabel = new Label(sdf.format(ratedCinema.getTime()));
        dateLabel.setPrimaryStyleName("dateLabel");
        dateLabel.setWidth(null);
        userRow.addComponent(dateLabel);
        userRow.setComponentAlignment(dateLabel, Alignment.TOP_RIGHT);
        userRow.setPrimaryStyleName("userRow");
        userRow.setWidth("100%");

        HorizontalLayout commentRow = new HorizontalLayout();
        Label commentLabel = new Label(ratedCinema.getDescription());
        commentLabel.setPrimaryStyleName("ratingsComment");
        commentRow.addComponent(commentLabel);
        commentRow.setPrimaryStyleName("commentRow");

        ratingEntry.addComponent(userRow);
        ratingEntry.addComponent(commentRow);
        ratingEntry.addComponent(new Label("<hr />", ContentMode.HTML));
        ratingEntry.setPrimaryStyleName("ratingEntry");

        return ratingEntry;
    }

    private VerticalLayout createCinemaInformation(Cinema cinema) {
        VerticalLayout informationForm = new VerticalLayout();
        informationForm.setId("cinemaAdress");

        // headingRow
        HorizontalLayout headingRow = new HorizontalLayout();
        Label heading = new Label(cinema.getName());
        headingRow.setId("cinemaHeading");
        headingRow.addComponent(heading);
        informationForm.addComponent(headingRow);

        // streetRow
        HorizontalLayout streetRow = new HorizontalLayout();
        Label streetLabel = new Label(cinema.getStreet() + " " + cinema.getHnr());
        streetRow.setId("cinemaStreetRow");
        streetRow.addComponent(streetLabel);
        informationForm.addComponent(streetRow);

        // cityRow
        HorizontalLayout cityRow = new HorizontalLayout();
        Label cityLabel = new Label(cinema.getPostcode() + " " + cinema.getCity());
        cityRow.setId("cinemaCityRow");
        cityRow.addComponent(cityLabel);
        informationForm.addComponent(cityRow);

        // favBtn
        favoriteButton = CinemaFavoriteUtils.createFavoriteButton(cinema.getId(), userBean, this);
        informationForm.addComponent(favoriteButton);

        return informationForm;
    }

    private void replaceFavoriteButton() {
        AbstractOrderedLayout favParent = (AbstractOrderedLayout) favoriteButton.getParent();
        int index = favParent.getComponentIndex(favoriteButton);
        favParent.removeComponent(favoriteButton);
        favoriteButton = CinemaFavoriteUtils.createFavoriteButton(c.getId(), userBean, this);
        favParent.addComponent(favoriteButton, index);
    }

    @Override
    public void favoriteRemoved() {
        replaceFavoriteButton();
    }

    @Override
    public void favoriteAdded() {
        replaceFavoriteButton();
    }
}
