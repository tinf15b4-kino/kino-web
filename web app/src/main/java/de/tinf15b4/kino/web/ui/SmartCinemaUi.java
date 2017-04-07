package de.tinf15b4.kino.web.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
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

import de.tinf15b4.kino.data.users.UserBean;
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

        // left part of naviagtor bar
        grid.addComponent(createNavigatorBarLeft(), 0, 1);

        // Navigaor bar under header
        grid.addComponent(createNavigatorBarRight(), 1, 1);

        // Main area
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setMargin(false);
        Panel panel = new Panel();
        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        layout.addComponent(panel);
        layout.setStyleName("layout");
        grid.addComponent(layout, 1, 2);
        panel.setSizeFull();


        // Footer
        // grid.addComponent(createFooter(), 0, 2, 1, 2);

        // Main View (in grid cell 1 1) will get all excess space
        grid.setColumnExpandRatio(1, 2);
        grid.setRowExpandRatio(2, 3);

        Navigator nav = new Navigator(this, panel);
        nav.addProvider(viewProvider);


    }

    private Component createHeader() {
        header = new HorizontalLayout();
        header.setSpacing(true);
        //header.setMargin(new MarginInfo(false, true));

        header.setWidth("100%");
        header.addStyleName("headerbackground");  
        header.setHeight("60px");

        Image logo = new Image(null, new ClassResource("/images/logo.png"));
        logo.setHeight("50px");
        header.addComponent(logo);
        header.setComponentAlignment(logo, Alignment.MIDDLE_LEFT);

        Panel searchPanel = new Panel();
        searchPanel.setStyleName("searchPanel");
        HorizontalLayout search = new HorizontalLayout();
        TextField searchField = new TextField();
        searchField.setWidth("20em");
        searchField.setInputPrompt("Suche nach Kinos und Filmen");
        searchField.setStyleName("cinemaSearchBox");
        searchField.addStyleName(ValoTheme.PANEL_BORDERLESS);
        Button searchButton = new Button();
        searchButton.setIcon(FontAwesome.SEARCH);
        searchButton.setStyleName("cinemaSearchBtn");
        searchButton.addStyleName(BaseTheme.BUTTON_LINK);
        searchButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        search.addComponent(searchField);
        search.addComponent(searchButton);
        searchButton.addClickListener(
                e -> getNavigator().navigateTo(SearchResultsView.VIEW_NAME + "/" + searchField.getValue()));
        ShortcutUtils.registerScopedShortcut(searchPanel, searchButton, ShortcutAction.KeyCode.ENTER);

        searchPanel.setContent(search);
        searchPanel.setWidthUndefined();
        searchPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        header.addComponent(searchPanel);
        header.setComponentAlignment(searchPanel, Alignment.MIDDLE_CENTER);

        if (userBean.isUserLoggedIn()) {
            Button user = new Button(userBean.getCurrentUser().getName(), e -> userClicked());
            header.addComponent(user);
            user.setStyleName("register_userBtn");
            user.addStyleName(BaseTheme.BUTTON_LINK);
            
            Button logout = new Button("Abmelden", e -> {
                userBean.logout();
                Page.getCurrent().reload();  
              
            });
            logout.setStyleName("login_logoutBtn");
            logout.addStyleName(BaseTheme.BUTTON_LINK);
            header.addComponent(logout);
            header.setComponentAlignment(user, Alignment.MIDDLE_RIGHT);
            header.setComponentAlignment(logout, Alignment.MIDDLE_RIGHT);
        } else {
            Button register = new Button("Registrieren", e -> navigateTo("register"));
            register.setStyleName("register_userBtn");
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
            login.setStyleName("login_logoutBtn");
            login.addStyleName(BaseTheme.BUTTON_LINK);
            login.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        }
        header.setExpandRatio(searchPanel, 1);

        return header;
    }

    private void userClicked() {
        // TODO Do stuff, probably navigate to account details
    }

    private Component createFooter() {
        VerticalLayout footer = new VerticalLayout();
        footer.setMargin(new MarginInfo(false, true));
        footer.setWidth("100%");
        footer.addStyleName("footerbackground");
        footer.setHeight("100px");

        // First row in the footer
        HorizontalLayout row1 = new HorizontalLayout();
        row1.setSpacing(true);
        row1.setSizeFull();

        Button account = new Button("Mein Konto", e -> navigateTo("account"));
        row1.addComponent(account);
        Button contact = new Button("Kontakt", e -> navigateTo("contact"));
        row1.addComponent(contact);

        row1.setComponentAlignment(account, Alignment.MIDDLE_LEFT);
        row1.setComponentAlignment(contact, Alignment.MIDDLE_LEFT);
        row1.setExpandRatio(contact, 1);

        // Second row in the footer
        HorizontalLayout row2 = new HorizontalLayout();
        row2.setSpacing(true);
        row2.setSizeFull();

        Label copyright = new Label("Copyright &copy; 2016 smartCinema", ContentMode.HTML);
        copyright.addStyleName("copyrightText");
        copyright.setSizeUndefined();
        row2.addComponent(copyright);
        Button impressum = new Button("Impressum", e -> navigateTo("impressum"));
        row2.addComponent(impressum);
        Button datenschutz = new Button("Datenschutz", e -> navigateTo("data_security"));
        row2.addComponent(datenschutz);

        row2.setComponentAlignment(copyright, Alignment.MIDDLE_LEFT);
        row2.setComponentAlignment(impressum, Alignment.MIDDLE_LEFT);
        row2.setComponentAlignment(datenschutz, Alignment.MIDDLE_LEFT);
        row2.setExpandRatio(datenschutz, 1);

        footer.addComponent(row1);
        footer.addComponent(row2);

        return footer;
    }

    private Component createNavigator() {
        VerticalLayout navigator = new VerticalLayout();
        navigator.setSizeUndefined();
        navigator.setStyleName("navigator");
        navigator.setHeight("100%");

        Label movieLabel =  new Label("FILME");
        movieLabel.setStyleName("navHeadings");
        navigator.addComponent(movieLabel);
        
        Button movieBtn = (Button) (createViewButton("Filme", MovieListView.VIEW_NAME, FontAwesome.PLAY));
        movieBtn.setStyleName("navigatorBtn");
        movieBtn.addStyleName(BaseTheme.BUTTON_LINK);
        navigator.addComponent(movieBtn);
        
        Button newMovieBtn = (Button) (createViewButton("Neu im Kino", "new", FontAwesome.EXCLAMATION_CIRCLE));
        newMovieBtn.setStyleName("navigatorBtn");
        newMovieBtn.addStyleName(BaseTheme.BUTTON_LINK);
        navigator.addComponent(newMovieBtn);
        
        Button soonBtn = (Button) (createViewButton("Demnächst", "coming_soon", FontAwesome.HISTORY));
        soonBtn.setStyleName("navigatorBtn");
        soonBtn.addStyleName(BaseTheme.BUTTON_LINK);
        navigator.addComponent(soonBtn);
        
        //navigator.addComponent(createViewButton("Neu im Kino", "new", FontAwesome.EXCLAMATION_CIRCLE));
        // navigator.addComponent(createViewButton("Demnächst", "coming_soon", FontAwesome.HISTORY));
        
        Label cinemaLabel = new Label("KINOS");
        cinemaLabel.setStyleName("navHeadings");
        navigator.addComponent(cinemaLabel);
        
        Button cinemaBtn = (Button) (createViewButton("Kinos", CinemaListView.VIEW_NAME, FontAwesome.VIDEO_CAMERA));
        cinemaBtn.setStyleName("navigatorBtn");
        cinemaBtn.addStyleName(BaseTheme.BUTTON_LINK);
        navigator.addComponent(cinemaBtn);
        
        Button favoriteBtn = (Button) (createViewButton("Favoriten", FavoriteListView.VIEW_NAME, FontAwesome.HEART));
        favoriteBtn.setStyleName("navigatorBtn");
        favoriteBtn.addStyleName(BaseTheme.BUTTON_LINK);
        navigator.addComponent(favoriteBtn);
        
        // navigator.addComponent(createViewButton("Kinos", CinemaListView.VIEW_NAME, FontAwesome.VIDEO_CAMERA));
        // navigator.addComponent(createViewButton("Favoriten", FavoriteListView.VIEW_NAME, FontAwesome.HEART));
        
        Label nothingLabel = new Label("");
        navigator.addComponent(nothingLabel);
        navigator.setExpandRatio(nothingLabel, 1f);
        
        navigator.setStyleName("navigator");
        navigator.addStyleName(BaseTheme.BUTTON_LINK);
        

        return navigator;
    }

    private Component createNavigatorBarLeft() {
        HorizontalLayout navigatorBarLeft = new HorizontalLayout();
        navigatorBarLeft.setWidth("100%");
        navigatorBarLeft.setHeight("60px");
        navigatorBarLeft.setStyleName("navigatorBarLeft");
        
        Button foldBtn = new Button();
        foldBtn.setIcon(FontAwesome.REORDER);
        foldBtn.setStyleName("foldBtn");
        foldBtn.addStyleName(BaseTheme.BUTTON_LINK);
        
        Button homeBtn = new Button("", e -> navigateTo("home"));
        homeBtn.setIcon(FontAwesome.HOME);
        homeBtn.setStyleName("homeBtn");
        homeBtn.addStyleName(BaseTheme.BUTTON_LINK);
        
        
        navigatorBarLeft.addComponent(foldBtn);
        navigatorBarLeft.setComponentAlignment(foldBtn, Alignment.TOP_LEFT);
        
        navigatorBarLeft.addComponent(homeBtn);
        navigatorBarLeft.setComponentAlignment(homeBtn, Alignment.TOP_RIGHT);
        
        return navigatorBarLeft;
    }

    private Component createNavigatorBarRight() {
        HorizontalLayout navigatorBarRight = new HorizontalLayout();
        navigatorBarRight.setWidth("100%");
        navigatorBarRight.setHeight("60px");
        navigatorBarRight.setStyleName("navigatorBarRight");

        Label navLabelR = new Label();
        navLabelR.setCaption("navR");

        navigatorBarRight.addComponent(navLabelR);

        return navigatorBarRight;
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
    }

    public void update() {
        grid.removeComponent(header);
        grid.addComponent(createHeader(), 0, 0, 1, 0);
    }
}
