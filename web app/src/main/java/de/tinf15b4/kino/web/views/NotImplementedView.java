package de.tinf15b4.kino.web.views;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = NotImplementedView.VIEW_NAME)
public class NotImplementedView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "not_implemented";

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        this.setMargin(true);
        this.setSpacing(true);

        this.removeAllComponents();
        this.addComponent(new Label("Diese Funktion wird bald implementiert!"));
    }
}
