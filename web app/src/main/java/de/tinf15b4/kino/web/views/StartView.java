package de.tinf15b4.kino.web.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@SpringView(name = StartView.VIEW_NAME)
public class StartView extends VerticalLayout implements View {

	public static final String VIEW_NAME = "";

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO
	}

}
