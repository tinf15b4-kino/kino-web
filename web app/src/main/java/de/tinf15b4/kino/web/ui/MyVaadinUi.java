package de.tinf15b4.kino.web.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("valo")
@SpringUI(path = "/vaadin")
public class MyVaadinUi extends UI {

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout root = new VerticalLayout();
		root.addComponent(new Label("Hello World! This is Vaadin!"));
		Button button = new Button("Click me to do stuff!");
		button.addClickListener(e -> button.setCaption("You clicked me!"));
		root.addComponent(button);

		setContent(root);
	}

}
