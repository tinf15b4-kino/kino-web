package de.tinf15b4.kino.web.ui;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ClassResource;
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
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.users.UserLoginBean;
import de.tinf15b4.kino.web.views.Views;

@SuppressWarnings("serial")
@Theme("smartCinema")
@SpringUI(path = "/start")
public class MyVaadinUi extends UI {

    @Autowired
    private SpringViewProvider viewProvider;

    @Autowired
    private UserLoginBean userLoginBean;

    private HorizontalLayout header;

    private GridLayout grid;

    @PostConstruct
    public void postInit() {
        userLoginBean.setUi(this);
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
        grid.addComponent(createNavigator(), 0, 1);

        // Main area
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        Panel panel = new Panel();
        layout.addComponent(panel);
        grid.addComponent(layout, 1, 1);
        panel.setSizeFull();

        // Footer
        grid.addComponent(createFooter(), 0, 2, 1, 2);

        // Main View (in grid cell 1 1) will get all excess space
        grid.setColumnExpandRatio(1, 1);
        grid.setRowExpandRatio(1, 1);

        Navigator nav = new Navigator(this, panel);
        nav.addProvider(viewProvider);

    }

    private Component createHeader() {
        header = new HorizontalLayout();
        header.setSpacing(true);
        header.setMargin(new MarginInfo(false, true));
        header.setWidth("100%");
        header.addStyleName("headerbackground");
        header.setHeight("60px");

        Image logo = new Image(null, new ClassResource("/images/logo.png"));
        logo.setHeight("50px");
        header.addComponent(logo);
        header.setComponentAlignment(logo, Alignment.MIDDLE_LEFT);

        if (userLoginBean.isUserLoggedIn()) {
            Button user = new Button(userLoginBean.getCurrentUser().getName(), e -> userClicked());
            header.addComponent(user);
            Button logout = new Button("Abmelden", e -> userLoginBean.logout());
            header.addComponent(logout);
            header.setComponentAlignment(user, Alignment.MIDDLE_RIGHT);
            header.setComponentAlignment(logout, Alignment.MIDDLE_RIGHT);
        } else {
            Button register = new Button(Views.REGISTER.getReadableName(), e -> navigateTo(Views.REGISTER));
            header.addComponent(register);
            Button login = new Button(Views.LOGIN.getReadableName(), e -> navigateTo(Views.LOGIN));
            header.addComponent(login);
            header.setComponentAlignment(register, Alignment.MIDDLE_RIGHT);
            header.setComponentAlignment(login, Alignment.MIDDLE_RIGHT);
        }

        header.setExpandRatio(logo, 1);

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

        Button account = new Button(Views.ACCOUNT.getReadableName(), e -> navigateTo(Views.ACCOUNT));
        row1.addComponent(account);
        Button contact = new Button(Views.CONTACT.getReadableName(), e -> navigateTo(Views.CONTACT));
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
        Button impressum = new Button(Views.IMPRESSUM.getReadableName(), e -> navigateTo(Views.IMPRESSUM));
        row2.addComponent(impressum);
        Button datenschutz = new Button(Views.DATASECURITY.getReadableName(), e -> navigateTo(Views.DATASECURITY));
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
        navigator.setSpacing(true);
        navigator.setMargin(true);
        navigator.setSizeUndefined();

        for (Views view : Views.values()) {
            if (view.isInNavigator()) {
                Button button = new Button(view.getReadableName(), e -> navigateTo(view));
                // Setting 100% width here will not work as Vaadin does not know
                // how big the navigator will be.
                button.addStyleName("navigatorButton");
                button.setIcon(view.getIcon());
                navigator.addComponent(button);
            }
        }

        return navigator;
    }

    private void navigateTo(Views view) {
        // TODO implement all views
        this.getNavigator().navigateTo(view.getViewId());
    }

    public void update() {
        grid.removeComponent(header);
        grid.addComponent(createHeader(), 0, 0, 1, 0);
    }
}
