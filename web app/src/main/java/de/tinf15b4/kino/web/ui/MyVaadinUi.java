package de.tinf15b4.kino.web.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.web.views.Views;

@SuppressWarnings("serial")
@Theme("smartCinema")
@SpringUI(path = "/start")
public class MyVaadinUi extends UI {

	@Autowired
	private SpringViewProvider viewProvider;

	@Override
	protected void init(VaadinRequest request) {
		GridLayout grid = new GridLayout(2, 3);
		setContent(grid);
		setSizeFull();
		grid.setSizeFull();

		// Header
		grid.addComponent(createHeader(), 0, 0, 1, 0);

		// Navigator at left side
		grid.addComponent(createNavigator(), 0, 1);

		// Main area
		Panel panel = new Panel();
		grid.addComponent(panel, 1, 1);
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
		HorizontalLayout header = new HorizontalLayout();
		header.setSpacing(true);
		header.setMargin(true);
		header.setWidth("100%");

		// TODO Add logo
		Image logo = new Image("Logo");
		header.addComponent(logo);
		Button register = new Button(Views.REGISTER.getReadableName(), e -> navigateTo(Views.REGISTER));
		header.addComponent(register);
		Button login = new Button(Views.LOGIN.getReadableName(), e -> navigateTo(Views.LOGIN));
		header.addComponent(login);

		header.setComponentAlignment(logo, Alignment.MIDDLE_LEFT);
		header.setComponentAlignment(register, Alignment.MIDDLE_RIGHT);
		header.setComponentAlignment(login, Alignment.MIDDLE_RIGHT);

		header.setExpandRatio(logo, 1);

		return header;
	}

	private Component createFooter() {
		VerticalLayout footer = new VerticalLayout();
		footer.setMargin(true);
		footer.setWidth("100%");

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
				navigator.addComponent(button);
			}
		}

		return navigator;
	}

	private void navigateTo(Views view) {
		// TODO implement views
		// this.getNavigator().navigateTo(view.getViewId());
		Notification.show(String.format("Navigate to view: %s", view.getViewId()), Type.TRAY_NOTIFICATION);
	}
}
