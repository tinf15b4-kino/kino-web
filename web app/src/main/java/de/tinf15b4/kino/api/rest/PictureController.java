package de.tinf15b4.kino.api.rest;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthNone;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.annotation.ApiVersion;
import org.jsondoc.core.pojo.ApiStage;
import org.jsondoc.core.pojo.ApiVisibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieService;

@Api(name = "Picture services", description = "Offers all methods needed to retrieve pictures from the database", visibility = ApiVisibility.PUBLIC, stage = ApiStage.BETA)
@ApiVersion(since = "0.0.1")
@ApiAuthNone
@RestController
public class PictureController {

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private MovieService movieService;

    @ApiMethod(description = "Returnsthe cover of the cinema with the given id")
    @RequestMapping(value = "rest/cinemaPicture", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getCinemaPicture(
            @ApiQueryParam(name = "cinemaId", description = "Id of the cinema") @RequestParam(name = "cinemaId") long cinemaId) {
        Cinema c = cinemaService.findOne(cinemaId);
        if (c != null) {
            return c.getImage();
        } else {
            return null;
        }
    }

    @ApiMethod(description = "Returns the cover of the movie with the given id")
    @RequestMapping(value = "rest/moviePicture", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getMoviePicture(
            @ApiQueryParam(name = "movieId", description = "Id of the movie") @RequestParam(name = "movieId") long movieId) {
        Movie m = movieService.findOne(movieId);
        if (m != null) {
            return m.getCover();
        } else {
            return null;
        }
    }

    public static String getCinemaPictureUrl(Cinema c) {
        return "/rest/cinemaPicture?cinemaId=" + c.getId();
    }

    public static String getMoviePictureUrl(Movie m) {
        return "/rest/moviePicture?movieId=" + m.getId();
    }
}
