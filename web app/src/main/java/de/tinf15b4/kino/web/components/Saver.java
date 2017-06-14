package de.tinf15b4.kino.web.components;

import de.tinf15b4.kino.web.rest.RestResponse;

public interface Saver<Rated> {

    public RestResponse save(Rated rated);

}
