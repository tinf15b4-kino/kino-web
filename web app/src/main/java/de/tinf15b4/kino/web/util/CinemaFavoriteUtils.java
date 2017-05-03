package de.tinf15b4.kino.web.util;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.BaseTheme;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.web.rest.RestClient;
import de.tinf15b4.kino.web.rest.RestResponse;
import de.tinf15b4.kino.web.user.UserBean;

public class CinemaFavoriteUtils {

    public static Component createFavoriteButton(long cinemaId, UserBean userBean, ToggleFavoriteListener listener) {
        if (!userBean.isUserLoggedIn() || !isCinemaFavorite(cinemaId, userBean.getRestClient())) {
            // create button
            Button favBtn = new Button();
            favBtn.setCaption("Favorit");
            favBtn.setIcon(FontAwesome.HEART);
            favBtn.addClickListener(e -> markAsFavorite(cinemaId, userBean, listener));
            favBtn.setId("cinemaFavBtn_" + cinemaId);
            favBtn.addStyleName(BaseTheme.BUTTON_LINK);
            favBtn.setDescription("Als Favorit markieren");
            return favBtn;
        } else {
            Button unfavBtn = new Button();
            unfavBtn.setCaption("Favorit");
            unfavBtn.setIcon(FontAwesome.HEART);
            unfavBtn.addClickListener(e -> unmarkFavorite(cinemaId, listener, userBean));
            unfavBtn.setId("cinemaUnfavBtn_" + cinemaId);
            unfavBtn.addStyleName(BaseTheme.BUTTON_LINK);
            unfavBtn.setDescription("Als Favorit entfernen");
            return unfavBtn;
        }
    }

    private static void markAsFavorite(long cinemaId, UserBean userBean, ToggleFavoriteListener listener) {
        if (!userBean.isUserLoggedIn()) {
            Notification.show("Melden Sie sich an, um diese Funktion nutzen zu können.", Type.WARNING_MESSAGE);
        } else {
            Favorite fav = getFavorite(cinemaId, userBean.getRestClient());
            if (fav == null) {

                // create new favorite entry
                RestResponse cinemaResponse = userBean.getRestClient().getCinema(cinemaId);
                if (!cinemaResponse.hasError()) {
                    RestResponse favoriteResponse = userBean.getRestClient()
                            .saveFavorite(new Favorite(userBean.getCurrentUser(), (Cinema) cinemaResponse.getValue()));
                    if (!favoriteResponse.hasError()) {
                        Notification.show("Das Kino " + ((Favorite) favoriteResponse.getValue()).getCinema().getName()
                                + " wurde als Favorit markiert.", Type.TRAY_NOTIFICATION);
                        listener.favoriteAdded();
                    }
                }
            }
        }
    }

    private static void unmarkFavorite(long cinemaId, ToggleFavoriteListener listener, UserBean userBean) {
        Favorite fav = getFavorite(cinemaId, userBean.getRestClient());
        if (fav != null) {
            Notification.show("Das Kino " + fav.getCinema().getName() + " wurde als Favorit entfernt.",
                    Type.TRAY_NOTIFICATION);
            // remove favorite entry
            userBean.getRestClient().deleteFavorite(fav);
            listener.favoriteRemoved();
        }
    }

    public static boolean isCinemaFavorite(long cinemaId, RestClient restClient) {
        return getFavorite(cinemaId, restClient) != null;
    }

    private static Favorite getFavorite(long cinemaId, RestClient restClient) {
        RestResponse response = restClient.getFavorite(cinemaId);
        if (!response.hasError())
            return (Favorite) response.getValue();
        return null;
    }
}
