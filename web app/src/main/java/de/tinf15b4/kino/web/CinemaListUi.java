package de.tinf15b4.kino.web;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

@SpringUI(path = "/crudui")
@Theme("valo")
public class CinemaListUi extends UI {

    @Autowired
    private SpringViewProvider provider;

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout root = new VerticalLayout();

        Label title = new Label("SmartCinema CRUD Demo");
        title.addStyleName(ValoTheme.LABEL_H1);
        root.addComponent(title);

        MenuBar nav = new MenuBar();
        nav.addItem("All Cinemas", e -> getNavigator().navigateTo(CinemaListView.VIEW_NAME));
        nav.addItem("Favorites", e -> getNavigator().navigateTo(FavoriteListView.VIEW_NAME));
        root.addComponent(nav);

        // In this panel the views will be shown. Everything outside this panel
        // will be the same on all views
        Panel container = new Panel();
        root.addComponent(container);

        // Create the navigator inside the panel with the autowired provider
        Navigator navigator = new Navigator(this, container);
        navigator.addProvider(provider);

        setContent(root);
    }

}

