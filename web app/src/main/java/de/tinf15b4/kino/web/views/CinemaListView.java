package de.tinf15b4.kino.web.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import de.tinf15b4.kino.data.Cinema;
import de.tinf15b4.kino.data.CinemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@SpringView(name = CinemaListView.VIEW_NAME)
public class CinemaListView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "";

    @Autowired
    private CinemaRepository repo;

    @PostConstruct
    @Transactional
    public void init() {
        this.setMargin(true);
        this.setSpacing(true);

        for (Cinema c : repo.findAll()) {
            Panel p = new Panel();
            Link l = new Link(c.getName(), new ExternalResource(
                "#!" + CinemaView.VIEW_NAME + "/" + c.getId()
            ));
            p.setContent(l);

            this.addComponent(p);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
