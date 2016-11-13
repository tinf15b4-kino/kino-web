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
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

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

		Image logo = new Image("Logo");
		header.addComponent(logo);
		Button register = new Button("Registrieren", e -> registerNewUser());
		header.addComponent(register);
		Button login = new Button("Login", e -> loginUser());
		header.addComponent(login);

		header.setComponentAlignment(logo, Alignment.MIDDLE_LEFT);
		header.setComponentAlignment(register, Alignment.MIDDLE_RIGHT);
		header.setComponentAlignment(login, Alignment.MIDDLE_RIGHT);

		header.setExpandRatio(logo, 1);

		return header;
	}

	private Component createFooter() {
		HorizontalLayout footer = new HorizontalLayout();
		footer.setSpacing(true);
		footer.setMargin(true);
		footer.setWidth("100%");

		Label copyright = new Label("Copyright &copy; 2016 smartCinema", ContentMode.HTML);
		copyright.setSizeUndefined();
		footer.addComponent(copyright);
		Button impressum = new Button("Impressum", e -> getUI().getNavigator().navigateTo("impressum"));
		footer.addComponent(impressum);
		Button datenschutz = new Button("Datenschutz", e -> getUI().getNavigator().navigateTo("datenschutz"));
		footer.addComponent(datenschutz);

		footer.setComponentAlignment(copyright, Alignment.MIDDLE_LEFT);
		footer.setComponentAlignment(impressum, Alignment.MIDDLE_LEFT);
		footer.setComponentAlignment(datenschutz, Alignment.MIDDLE_LEFT);

		footer.setExpandRatio(datenschutz, 1);

		return footer;
	}

	private Component createNavigator() {
		VerticalLayout navigator = new VerticalLayout();
		navigator.setSpacing(true);
		navigator.setMargin(true);
		navigator.setSizeUndefined();

		navigator.addComponent(new Button("Filme"));
		navigator.addComponent(new Button("Kinos"));
		navigator.addComponent(new Button("Lieblingskinos"));
		navigator.addComponent(new Button("Neu im Kino"));
		navigator.addComponent(new Button("Demnächst"));

		return navigator;
	}

	private void loginUser() {
		// TODO Auto-generated method stub
	}

	private void registerNewUser() {
		// TODO Auto-generated method stub
	}

}
