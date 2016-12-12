package de.tinf15b4.kino.web.util;

import java.io.ByteArrayInputStream;

import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;

public class PictureUtils {

    public static Component getMovieCover(Movie m) {
        StreamSource streamSource = new StreamResource.StreamSource() {
            @Override
            public ByteArrayInputStream getStream() {

                    return (m.getCover() == null) ? null : new ByteArrayInputStream(m.getCover());
            }
        };

        StreamResource imageResource = new StreamResource(streamSource, "");
        Image image = new Image(null, imageResource);

        return image;
    }

    public static Component getCinemaImage(Cinema c) {
        StreamSource streamSource = new StreamResource.StreamSource() {
            @Override
            public ByteArrayInputStream getStream() {

                    return (c.getImage() == null) ? null : new ByteArrayInputStream(c.getImage());
            }
        };

        StreamResource imageResource = new StreamResource(streamSource, "");
        Image image = new Image(null, imageResource);

        return image;
    }

}
