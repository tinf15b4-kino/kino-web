package de.tinf15b4.kino.web.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import de.tinf15b4.kino.data.Movie;
import de.tinf15b4.kino.data.MovieRepository;
import de.tinf15b4.kino.data.Playlist;
import de.tinf15b4.kino.data.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SpringView(name = MovieView.VIEW_NAME)
public class MovieView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "movie";

    @Autowired
    private MovieRepository repo;

    @Autowired
    private PlaylistRepository playlistRepo;

    @Override
    @Transactional
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        this.setMargin(true);
        this.setSpacing(true);

        this.removeAllComponents();
        if (event.getParameters() != null) {
            String idStr = event.getParameters();
            long id = Long.parseLong(idStr);

            Movie m = repo.getOne(id);

            this.addComponent(new Label(m.getName()));
            this.addComponent(new Label("Länge: " + m.getLengthMinutes() + " Minuten"));
            this.addComponent(new Label(m.getDescription()));

            GridLayout playtimes = new GridLayout(3, 1);
            playtimes.setMargin(true);
            playtimes.setSpacing(true);
            playtimes.setSizeFull();

            for (Playlist p : playlistRepo.findForMovie(m, new Date(), new Date(new Date().getTime() + 1000L*3600*24*7))) {
                SimpleDateFormat sdf = new SimpleDateFormat("E HH:mm", Locale.GERMANY);
                NumberFormat pricef = NumberFormat.getCurrencyInstance(Locale.GERMANY);
                playtimes.addComponent(new Label(sdf.format(p.getTime())));
                playtimes.addComponent(new Link(p.getCinema().getName(),
                        new ExternalResource("#!" + CinemaView.VIEW_NAME + "/" + p.getCinema().getId())));
                playtimes.addComponent(new Label(pricef.format(p.getPrice()/100.0)));
            }

            this.addComponent(new Panel(playtimes));
        }
    }
}
