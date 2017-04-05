package de.tinf15b4.kino.web.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = StartView.VIEW_NAME)
public class StartView extends VerticalLayout implements View {
    private static final long serialVersionUID = -3023236022867313886L;

    public static final String VIEW_NAME = "";

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO
    }

}
