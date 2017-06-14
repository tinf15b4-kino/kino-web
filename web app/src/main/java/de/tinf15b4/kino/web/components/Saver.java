package de.tinf15b4.kino.web.components;

import java.io.Serializable;

import de.tinf15b4.kino.web.rest.RestResponse;

public interface Saver<Rated> extends Serializable {

    public RestResponse save(Rated rated);

}
