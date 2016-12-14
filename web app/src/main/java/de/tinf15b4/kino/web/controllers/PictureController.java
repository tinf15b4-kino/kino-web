package de.tinf15b4.kino.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaRepository;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieRepository;

@Controller
public class PictureController {
    @Autowired
    private CinemaRepository cineRepo;

    @Autowired
    private MovieRepository movieRepo;

    @RequestMapping(value = "/picture/cinema/{cid}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    byte[] getCinemaPicture(@PathVariable("cid") long cinemaid) {
        Cinema c = cineRepo.findOne(cinemaid);
        if (c != null) {
            return c.getImage();
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/picture/movie/{mid}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    byte[] getMoviePicture(@PathVariable("mid") long movieid) {
        Movie m = movieRepo.findOne(movieid);
        if (m != null) {
            return m.getCover();
        } else {
            return null;
        }
    }

    public static String getCinemaPictureUrl(Cinema c) {
        return "/picture/cinema/" + c.getId();
    }

    public static String getMoviePictureUrl(Movie m) {
        return "/picture/movie/" + m.getId();
    }
}
