package de.tinf15b4.kino.web.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.PostConstruct;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.web.rest.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.ValoTheme;

import de.tinf15b4.kino.web.user.UserBean;
import de.tinf15b4.kino.web.util.ShortcutUtils;
import de.tinf15b4.kino.web.views.CinemaListView;
import de.tinf15b4.kino.web.views.FavoriteListView;
import de.tinf15b4.kino.web.views.LoginView;
import de.tinf15b4.kino.web.views.MovieListView;
import de.tinf15b4.kino.web.views.SearchResultsView;

@SuppressWarnings("serial")
@Theme("smartCinema")
@SpringUI(path = "/")
@Title("smartCinema")
public class SmartCinemaUi extends UI {

    @Autowired
    private SpringViewProvider viewProvider;

    @Autowired
    private UserBean userBean;

    private HorizontalLayout header;

    private GridLayout grid;

    @PostConstruct
    public void postInit() {
        userBean.setUi(this);
    }

    @Override
    protected void init(VaadinRequest request) {
        grid = new GridLayout(2, 3);
        setContent(grid);
        setSizeFull();
        grid.setSizeFull();

        // Header
        grid.addComponent(createHeader(), 0, 0, 1, 0);

        // Navigator at left side
        grid.addComponent(createNavigator(), 0, 2);

        // Left Tool-Bar
        grid.addComponent(createToolBarLeft(), 0, 1);

        // Right Tool-Bar
        grid.addComponent(createToolBarRight(), 1, 1);

        // Main area
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        Panel panel = new Panel();
        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        layout.addComponent(panel);
        layout.setId("layout");
        grid.addComponent(layout, 1, 2);
        panel.setSizeFull();


        // Main View (in grid cell 1 2) will get all excess space
        grid.setColumnExpandRatio(1, 2);
        grid.setRowExpandRatio(2, 3);

        Navigator nav = new Navigator(this, panel);
        nav.addProvider(viewProvider);
    }

    private Component createHeader() {
        header = new HorizontalLayout();

        header.setWidth("100%");
        header.setId("headerbackground");
        header.setHeight("60px");

        Image logo = new Image(null, new ClassResource("/images/logo.png"));
        logo.setHeight("50px");
        header.addComponent(logo);
        header.setComponentAlignment(logo, Alignment.MIDDLE_LEFT);

        Panel searchPanel = new Panel();
        HorizontalLayout search = new HorizontalLayout();
        TextField searchField = new TextField();
        searchField.setWidth("20em");
        searchField.setInputPrompt("Suche nach Kinos und Filmen");
        searchField.setId("cinemaSearchBox");
        searchField.addStyleName(ValoTheme.PANEL_BORDERLESS);
        Button searchButton = new Button();
        searchButton.setIcon(FontAwesome.SEARCH);
        searchButton.setId("cinemaSearchBtn");
        searchButton.addStyleName(BaseTheme.BUTTON_LINK);
        searchButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        search.addComponent(searchField);
        search.addComponent(searchButton);
        searchButton.addClickListener(
                e -> getNavigator().navigateTo(
                        SearchResultsView.VIEW_NAME + "/" + searchField.getValue()));
        ShortcutUtils.registerScopedShortcut(searchPanel, searchButton, ShortcutAction.KeyCode.ENTER);

        searchPanel.setContent(search);
        searchPanel.setWidthUndefined();
        searchPanel.setId("searchPanel");
        searchPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        header.addComponent(searchPanel);
        header.setComponentAlignment(searchPanel, Alignment.MIDDLE_CENTER);

        if (userBean.isUserLoggedIn()) {
            Button user = new Button(userBean.getCurrentUser().getName(), e -> userClicked());
            header.addComponent(user);
            user.setId("registerUserBtn");
            user.addStyleName(BaseTheme.BUTTON_LINK);

            Button logout = new Button("Abmelden", e -> {
                userBean.logout();
                Page.getCurrent().reload();

            });
            logout.setStyleName(ValoTheme.BUTTON_LINK);
            logout.setId("loginLogoutBtn");
            header.addComponent(logout);
            header.setComponentAlignment(user, Alignment.MIDDLE_RIGHT);
            header.setComponentAlignment(logout, Alignment.MIDDLE_RIGHT);
        } else {
            Button register = new Button("Registrieren", e -> navigateTo("register"));
            register.setId("registerUserBtn");
            register.addStyleName(BaseTheme.BUTTON_LINK);

            header.addComponent(register);
            Button login = new Button("Anmelden", e -> {
                try {
                    navigateTo(LoginView.VIEW_NAME + "/"
                            + URLEncoder.encode(Page.getCurrent().getLocation().toASCIIString(), "UTF-8"));
                } catch (UnsupportedEncodingException e1) {
                    // FIXME: This should never happen. If it does, we'll
                    // quietly limp along.
                    navigateTo(LoginView.VIEW_NAME);
                }
            });

            header.addComponent(login);
            header.setComponentAlignment(register, Alignment.MIDDLE_RIGHT);
            header.setComponentAlignment(login, Alignment.MIDDLE_RIGHT);
            login.addStyleName(BaseTheme.BUTTON_LINK);
            login.setId("loginLogoutBtn");
        }
        header.setExpandRatio(searchPanel, 1);

        return header;
    }

    private void userClicked() {
        // TODO Do stuff, probably navigate to account details
    }

    private Component createNavigator() {
        VerticalLayout navigator = new VerticalLayout();
        navigator.setSizeUndefined();
        navigator.setHeight("100%");

        Label movieLabel =  new Label("FILME");
        movieLabel.setId("navHeadings_Movie");
        navigator.addComponent(movieLabel);

        Button movieBtn = (Button) (createViewButton(
                "Filme", MovieListView.VIEW_NAME, FontAwesome.PLAY));
        movieBtn.setId("navigatorBtn_Movie");
        movieBtn.addStyleName(BaseTheme.BUTTON_LINK);
        navigator.addComponent(movieBtn);

        Button newMovieBtn = (Button) (createViewButton(
                "Neu im Kino", "new", FontAwesome.EXCLAMATION_CIRCLE));
        newMovieBtn.setId("navigatorBtn_New");
        newMovieBtn.addStyleName(BaseTheme.BUTTON_LINK);
        navigator.addComponent(newMovieBtn);

        Button soonBtn = (Button) (createViewButton(
                "Demnächst", "coming_soon", FontAwesome.HISTORY));
        soonBtn.setId("navigatorBtn_Soon");
        soonBtn.addStyleName(BaseTheme.BUTTON_LINK);
        navigator.addComponent(soonBtn);

        Label cinemaLabel = new Label("KINOS");
        cinemaLabel.setId("navHeadings_Cinemas");
        navigator.addComponent(cinemaLabel);

        Button cinemaBtn = (Button) (createViewButton(
                "Kinos", CinemaListView.VIEW_NAME, FontAwesome.VIDEO_CAMERA));
        cinemaBtn.setId("navigatorBtn_Cinemas");
        cinemaBtn.addStyleName(BaseTheme.BUTTON_LINK);
        navigator.addComponent(cinemaBtn);

        Button favoriteBtn = (Button) (createViewButton(
                "Favoriten", FavoriteListView.VIEW_NAME, FontAwesome.HEART));
        favoriteBtn.setId("navigatorBtn_Fav");
        favoriteBtn.addStyleName(BaseTheme.BUTTON_LINK);
        navigator.addComponent(favoriteBtn);

        Button aboutBtn = (Button) (createViewButton(
                "über smartCinema", "aboutView", FontAwesome.INFO));
        aboutBtn.setId("navigatorBtn_About");
        aboutBtn.addStyleName(BaseTheme.BUTTON_LINK);
        navigator.addComponent(aboutBtn);
        navigator.setComponentAlignment(aboutBtn, Alignment.BOTTOM_CENTER);
        navigator.setExpandRatio(aboutBtn, 1f);

        navigator.setId("navigator");
        navigator.addStyleName(BaseTheme.BUTTON_LINK);

        return navigator;
    }

    private Component createToolBarLeft() {
        HorizontalLayout navigatorBarLeft = new HorizontalLayout();
        navigatorBarLeft.setWidth("100%");
        navigatorBarLeft.setHeight("60px");
        navigatorBarLeft.setId("toolBarLeft");

        Button homeBtn = new Button("", e -> navigateTo("home"));
        homeBtn.setIcon(FontAwesome.HOME);
        homeBtn.setId("homeBtn");
        homeBtn.addStyleName(BaseTheme.BUTTON_LINK);

        Button foldBtn = new Button();
        foldBtn.setIcon(FontAwesome.REORDER);
        foldBtn.setId("foldBtn");
        foldBtn.addStyleName(BaseTheme.BUTTON_LINK);

        navigatorBarLeft.addComponent(homeBtn);
        navigatorBarLeft.setComponentAlignment(homeBtn, Alignment.MIDDLE_LEFT);

        navigatorBarLeft.addComponent(foldBtn);
        navigatorBarLeft.setComponentAlignment(foldBtn, Alignment.MIDDLE_RIGHT);


        return navigatorBarLeft;
    }

    private Component createToolBarRight() {
        HorizontalLayout navigatorBarRight = new HorizontalLayout();
        navigatorBarRight.setWidth("100%");
        navigatorBarRight.setHeight("60px");
        navigatorBarRight.setId("toolBarRight");

        navigatorBarRight.addComponent(getContentLabel());

        return navigatorBarRight;
    }

    private Label getContentLabel(){
        Label contentLabel = new Label();

        String uri = Page.getCurrent().getUriFragment();
        if (uri != null) {
            if (uri.contains("movies")) {
                contentLabel.setValue("Filme");
            } else if (uri.contains("movie")) {
                RestResponse r = userBean.getRestClient().getMovie(Integer.parseInt(uri.substring(7)));
                Movie m = (Movie) r.getValue();
                contentLabel.setValue(m.getName());
            } else if (uri.contains("cinemas")) {
                contentLabel.setValue("Kinos");
            } else if (uri.contains("cinema")) {
                RestResponse r = userBean.getRestClient().getCinema(Integer.parseInt(uri.substring(8)));
                Cinema c = (Cinema) r.getValue();
                contentLabel.setValue(c.getName());
            } else if (uri.contains("favourites")) {
                contentLabel.setValue("Favoriten");
            }
        }
        contentLabel.setId("toolBarLabel");

        return contentLabel;
    }

    private Component createViewButton(String readableName, String viewId, FontAwesome icon) {
        Button button = new Button(readableName, e -> navigateTo(viewId));
        // Setting 100% width here will not work as Vaadin does not know
        // how big the navigator will be.
        button.addStyleName("navigatorButton");
        button.setIcon(icon);

        return button;
    }

    private void navigateTo(String viewId) {
        // TODO implement all views
        this.getNavigator().navigateTo(viewId);
        grid.removeComponent(1, 1);
        grid.addComponent(createToolBarRight(), 1, 1);
    }

    public void update() {
        grid.removeComponent(header);
        grid.addComponent(createHeader(), 0, 0, 1, 0);
    }
}