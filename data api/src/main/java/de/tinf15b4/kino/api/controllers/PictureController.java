package de.tinf15b4.kino.api.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

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

import com.google.common.annotations.VisibleForTesting;

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

    private byte[] defaultCinemaImage;
    private byte[] defaultMovieCover;

    @ApiMethod(description = "Returns the image of the cinema with the given id")
    @RequestMapping(value = "rest/cinemaPicture", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getCinemaPicture(
            @ApiQueryParam(name = "cinemaId", description = "Id of the cinema") @RequestParam(name = "cinemaId") long cinemaId) {
        Cinema c = cinemaService.findOne(cinemaId);
        if (c != null && c.getImage() != null && c.getImage().length > 0) {
            return c.getImage();
        } else {
            return getDefaultCinemaImage();
        }
    }

    @ApiMethod(description = "Returns the cover of the movie with the given id")
    @RequestMapping(value = "rest/moviePicture", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getMoviePicture(
            @ApiQueryParam(name = "movieId", description = "Id of the movie") @RequestParam(name = "movieId") long movieId) {
        Movie m = movieService.findOne(movieId);
        if (m != null && m.getCover() != null && m.getCover().length > 0) {
            return m.getCover();
        } else {
            return getDefaultMovieCover();
        }
    }

    private byte[] getDefaultCinemaImage() {
        if (defaultCinemaImage == null)
            defaultCinemaImage = loadDefaultCinemaImage();
        return defaultCinemaImage;
    }

    private byte[] getDefaultMovieCover() {
        if (defaultMovieCover == null)
            defaultMovieCover = loadDefaultMovieCover();
        return defaultMovieCover;
    }

    @VisibleForTesting
    byte[] loadDefaultCinemaImage() {
        try {
            BufferedImage originalImage = ImageIO
                    .read(this.getClass().getResourceAsStream("/images/defaultCinema.jpg"));

            // convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "jpg", baos);
            baos.flush();
            byte[] buffer = baos.toByteArray();
            baos.close();
            return buffer;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load default cinema image from disk", e);
        }
    }

    @VisibleForTesting
    byte[] loadDefaultMovieCover() {
        try {
            BufferedImage originalImage = ImageIO.read(this.getClass().getResourceAsStream("/images/defaultMovie.jpg"));

            // convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "jpg", baos);
            baos.flush();
            byte[] buffer = baos.toByteArray();
            baos.close();
            return buffer;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load default movie image from disk", e);
        }
    }
}
