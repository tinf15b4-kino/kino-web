package de.tinf15b4.kino.web.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.web.user.UserBean;
import de.tinf15b4.kino.web.views.CinemaView;

public class ViewUtils {

    private ViewUtils() {
        // Just used to hide the public constructor
        // Sonarqube seems to like that
    }

    public static HorizontalLayout createCinemaRow(Cinema c, UserBean userBean) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidth(100, Unit.PERCENTAGE);
        row.setId("cinemaRow_" + c.getId());

        // Picture
        Component image = new Image(null, new ExternalResource(userBean.getRestClient().getCinemaPictureUrl(c)));
        image.setId("cinemaImage_" + c.getId());
        image.setHeight("200px");
        row.addComponent(image);

        // Info-Box
        Component movieInfoBox = createCinemaInfoBox(c, userBean);
        row.addComponent(movieInfoBox);

        row.setExpandRatio(movieInfoBox, 1f);
        row.setSpacing(true);
        return row;
    }

    public static HorizontalLayout createPlaylistTable(List<Playlist> playlists, Cinema cinema) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("EE dd.MM", Locale.GERMAN);
        SimpleDateFormat movieTimeFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);

        HorizontalLayout playTimesTable = new HorizontalLayout();
        playTimesTable.setPrimaryStyleName("playTimesTable");

        for (int i = 0; i < 6; i++) {
            // Add weekdays to Table
            VerticalLayout tmpPlaylistColumn = new VerticalLayout();
            Label dateRow = new Label(currentDate.plusDays(i).format(formattedDate));
            dateRow.setPrimaryStyleName("dateEntry");
            tmpPlaylistColumn.addComponent(dateRow);

            // Add playtimes to table
            for (Playlist p : playlists) {
                LocalDate playDate = p.getTime().toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate();
                if (currentDate.plusDays(i).isEqual(playDate) && p.getCinema().equals(cinema)) {
                    Label timeRow = new Label(movieTimeFormat.format(p.getTime()));
                    timeRow.setPrimaryStyleName("timeEntry");
                    tmpPlaylistColumn.addComponent(timeRow);
                }
            }

            tmpPlaylistColumn.setPrimaryStyleName("playListColumn");
            playTimesTable.addComponent(tmpPlaylistColumn);

        }
        return playTimesTable;
    }

    private static Component createCinemaInfoBox(Cinema c, UserBean userBean) {

        VerticalLayout cinemaInfoBox = new VerticalLayout();

        Link l = new Link(c.getName(), new ExternalResource("#!" + CinemaView.VIEW_NAME + "/" + c.getId()));
        l.setId("cinemaListLink_" + c.getId());
        cinemaInfoBox.addComponent(l);
        cinemaInfoBox.setComponentAlignment(l, Alignment.TOP_LEFT);

        cinemaInfoBox.addComponent(new Label(c.getAddress(), ContentMode.PREFORMATTED));

        createFavoriteBtn(c.getId(), cinemaInfoBox, userBean);

        return cinemaInfoBox;
    }

    private static void createFavoriteBtn(long cinemaId, VerticalLayout row, UserBean userBean) {
        ButtonManager mgr = new ButtonManager(userBean, row, cinemaId);
        mgr.recreateBtn();
    }

}
