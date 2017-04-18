package de.tinf15b4.kino.web.util;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import de.tinf15b4.kino.api.rest.RestClient;
import de.tinf15b4.kino.api.rest.RestResponse;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.users.UserBean;

public class CinemaFavoriteUtils {

    public static Component createFavoriteButton(long cinemaId, UserBean userBean, ToggleFavoriteListener listener) {
        if (!userBean.isUserLoggedIn() || !isCinemaFavorite(cinemaId, userBean.getRestClient())) {
            // create button
            Button favBtn = new Button();
            favBtn.setCaption("Zu Favoriten hinzufügen");
            favBtn.addClickListener(e -> markAsFavorite(cinemaId, userBean, listener));
            favBtn.addStyleName("cinema-favorite-button");
            return favBtn;
        } else {
            MenuBar unfavMenu = new MenuBar();
            unfavMenu.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
            MenuBar.MenuItem menu = unfavMenu.addItem("Zu Favoriten hinzugefügt", null);
            menu.addItem("Aus Favoriten entfernen", i -> unmarkFavorite(cinemaId, listener, userBean));
            return unfavMenu;
        }
    }

    private static void markAsFavorite(long cinemaId, UserBean userBean, ToggleFavoriteListener listener) {
        if (!userBean.isUserLoggedIn()) {
            Notification.show("Melden Sie sich an, um diese Funktion nutzen zu können.", Type.WARNING_MESSAGE);
        } else {
            if (!isCinemaFavorite(cinemaId, userBean.getRestClient())) {
                // create new favorite entry
                RestResponse cinemaResponse = userBean.getRestClient().getCinema(cinemaId);
                if (!cinemaResponse.hasError()) {
                    userBean.getRestClient()
                            .saveFavorite(new Favorite(userBean.getCurrentUser(), (Cinema) cinemaResponse.getValue()));
                    listener.favoriteAdded();
                }
            }
        }
    }

    private static void unmarkFavorite(long cinemaId, ToggleFavoriteListener listener, UserBean userBean) {
        RestResponse response = userBean.getRestClient().getFavorite(cinemaId);
        if (!response.hasError()) {
            Favorite fav = (Favorite) response.getValue();
            if (fav != null) {
                // remove favorite entry
                userBean.getRestClient().deleteFavorite(fav);
                listener.favoriteRemoved();
            }
        }
    }

    public static boolean isCinemaFavorite(long cinemaId, RestClient restClient) {
        RestResponse response = restClient.getFavorite(cinemaId);
        if (!response.hasError())
            if (response.getValue() != null)
                return true;
        return false;
    }
}
