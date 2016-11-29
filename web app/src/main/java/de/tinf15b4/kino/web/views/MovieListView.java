package de.tinf15b4.kino.web.views;

import java.io.ByteArrayInputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieService;

@SpringView(name = MovieListView.VIEW_NAME)
public class MovieListView extends VerticalLayout implements View{
    public static final String VIEW_NAME = "movies";

    @Autowired
    private MovieService movieService;

    @PostConstruct
    public void init() {
        this.setMargin(true);
        this.setSpacing(true);

        for (Movie m : movieService.findAll()) {
            HorizontalLayout row = new HorizontalLayout();
            row.setWidth(100, Unit.PERCENTAGE);

            // Picture
            StreamSource streamSource = new StreamResource.StreamSource() {
                @Override
                public ByteArrayInputStream getStream() {
                    return (m.getCover() == null) ? null : new ByteArrayInputStream(m.getCover());
                }
            };

            StreamResource imageResource = new StreamResource(streamSource, "");

            Image image = new Image(null, imageResource);

            image.setHeight("100px");
            row.addComponent(image);

            Link l = new Link(m.getName(), new ExternalResource("#!" + MovieView.VIEW_NAME + "/" + m.getId()));

            row.addComponent(l);
            row.setComponentAlignment(l, Alignment.MIDDLE_LEFT);

            row.setExpandRatio(l, 1f);
            row.setSpacing(true);
            this.addComponent(row);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
