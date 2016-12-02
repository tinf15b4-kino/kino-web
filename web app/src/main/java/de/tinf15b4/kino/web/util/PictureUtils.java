package de.tinf15b4.kino.web.util;

import java.io.ByteArrayInputStream;

import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;

public class PictureUtils {

    // Just give a Moive or a Cinema not both!
    public static Component getImage(Movie m, Cinema c) {
        StreamSource streamSource = new StreamResource.StreamSource() {
            @Override
            public ByteArrayInputStream getStream() {

                if (!(m == null)) {
                    return (m.getCover() == null) ? null : new ByteArrayInputStream(m.getCover());

                } else if (!(c == null)) {
                    return (c.getImage() == null) ? null : new ByteArrayInputStream(c.getImage());
                }

                return null;
            }
        };

        StreamResource imageResource = new StreamResource(streamSource, "");
        Image image = new Image(null, imageResource);

        return image;
    }

}
