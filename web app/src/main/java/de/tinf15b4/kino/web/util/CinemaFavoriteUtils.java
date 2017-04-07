package de.tinf15b4.kino.web.util;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.BaseTheme;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.users.UserBean;

public class CinemaFavoriteUtils {

    public static Component createFavoriteButton(Cinema c, FavoriteService favoriteService, UserBean userBean,
            ToggleFavoriteListener listener) {
        if (!userBean.isUserLoggedIn() || !favoriteService.isCinemaFavorite(userBean.getCurrentUser(), c)) {
            // create button
            Button favBtn = new Button();
            favBtn.setCaption("Favorit");
            favBtn.setIcon(FontAwesome.HEART);
            favBtn.addClickListener(e -> markAsFavorite(c, favoriteService, userBean, listener));
            favBtn.setStyleName("cinemaFavBtn");
            favBtn.addStyleName(BaseTheme.BUTTON_LINK);
            return favBtn;
        } else {

            Button unfavBtn = new Button();
            unfavBtn.setCaption("Favorit");
            unfavBtn.setIcon(FontAwesome.HEART);
            unfavBtn.addClickListener(e -> markAsFavorite(c, favoriteService, userBean, listener));
            unfavBtn.setStyleName("cinemaUnfavBtn");
            unfavBtn.addStyleName(BaseTheme.BUTTON_LINK);
            return unfavBtn;
        }
    }

    public static void markAsFavorite(Cinema c, FavoriteService favoriteService, UserBean userBean,
            ToggleFavoriteListener listener) {
        if (!userBean.isUserLoggedIn()) {
            Notification.show("Melden Sie sich an, um diese Funktion nutzen zu können.", Type.WARNING_MESSAGE);
        }
        if (userBean.isUserLoggedIn()) {
            if (!favoriteService.isCinemaFavorite(userBean.getCurrentUser(), c)) {
                // create new favorite entry
                favoriteService.save(new Favorite(userBean.getCurrentUser(), c));
                listener.favoriteAdded();
            }
        }
    }

    public static void unmarkFavorite(Cinema c, FavoriteService favoriteService, ToggleFavoriteListener listener,
            UserBean userBean) {
        Favorite fav = favoriteService.findFavorite(userBean.getCurrentUser(), c);
        if (fav != null) {
            // remove favorite entry
            favoriteService.delete(fav);
            listener.favoriteRemoved();
        }
    }
}
