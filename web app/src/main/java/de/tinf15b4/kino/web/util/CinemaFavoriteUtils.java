package de.tinf15b4.kino.web.util;

import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.themes.ValoTheme;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.users.UserBean;

public class CinemaFavoriteUtils {

    public static Component createFavoriteButton(Cinema c, FavoriteService favoriteService, UserBean userBean,
            ToggleFavoriteListener listener) {
        if (favoriteService.isCinemaFavorite(c.getId())) {
            // create button
            Button favBtn = new Button();
            favBtn.setCaption("Mark as favorite");
            favBtn.addClickListener(e -> markAsFavorite(c, favoriteService, userBean, listener));
            return favBtn;
        } else {
            MenuBar unfavMenu = new MenuBar();
            unfavMenu.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
            MenuBar.MenuItem menu = unfavMenu.addItem("Marked as favorite", null);
            menu.addItem("Remove from favorites", i -> unmarkFavorite(c, favoriteService, listener));
            return unfavMenu;
        }
    }

    public static void markAsFavorite(Cinema c, FavoriteService favoriteService, UserBean userBean,
            ToggleFavoriteListener listener) {
        if (favoriteService.isCinemaFavorite(c.getId())) {
            // create new favorite entry
            favoriteService.save(new Favorite(userBean.getCurrentUser(), c));
            listener.favoriteAdded();
        }
    }

    public static void unmarkFavorite(Cinema c, FavoriteService favoriteService, ToggleFavoriteListener listener) {
        List<Favorite> existing = favoriteService.findByCinemaId(c.getId());
        if (!existing.isEmpty()) {
            // remove favorite entry
            favoriteService.delete(existing);
            listener.favoriteRemoved();
        }
    }
}
