package de.tinf15b4.kino.web.util;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.users.UserBean;

public class CinemaFavoriteUtils {

    public static Component createFavoriteButton(Cinema c, FavoriteService favoriteService, UserBean userBean,
            ToggleFavoriteListener listener) {
        if (!userBean.isUserLoggedIn() || !favoriteService.isCinemaFavorite(c.getId(), userBean.getCurrentUser())) {
            // create button
            Button favBtn = new Button();
            favBtn.setCaption("Zu Favoriten hinzufügen");
            favBtn.addClickListener(e -> markAsFavorite(c, favoriteService, userBean, listener));
            return favBtn;
        } else {
            MenuBar unfavMenu = new MenuBar();
            unfavMenu.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
            MenuBar.MenuItem menu = unfavMenu.addItem("Zu Favoriten hinzugefügt", null);
            menu.addItem("Aus Favoriten entfernen", i -> unmarkFavorite(c, favoriteService, listener, userBean));
            return unfavMenu;
        }
    }

    public static void markAsFavorite(Cinema c, FavoriteService favoriteService, UserBean userBean,
            ToggleFavoriteListener listener) {
        if (!userBean.isUserLoggedIn()) {
            Notification.show("Melden sie sich an, um diese Funktion nutzen zu können.", Type.WARNING_MESSAGE);
        }
        if (!favoriteService.isCinemaFavorite(c.getId(), userBean.getCurrentUser())) {
            // create new favorite entry
            favoriteService.save(new Favorite(userBean.getCurrentUser(), c));
            listener.favoriteAdded();
        }
    }

    public static void unmarkFavorite(Cinema c, FavoriteService favoriteService, ToggleFavoriteListener listener,
            UserBean userBean) {
        Favorite fav = favoriteService.findFavorite(c.getId(), userBean.getCurrentUser());
        if (fav != null) {
            // remove favorite entry
            favoriteService.delete(fav);
            listener.favoriteRemoved();
        }
    }
}
