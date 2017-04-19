package de.tinf15b4.kino.web.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

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

    private byte[] defaultCinemaImage;
    private byte[] defaultMovieCover;

    @RequestMapping(value = "/picture/cinema/{cid}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    byte[] getCinemaPicture(@PathVariable("cid") long cinemaid) {
        Cinema c = cineRepo.findOne(cinemaid);
        if (c != null) {
            if (c.getImage() == null)
                return getDefaultCinemaImage();
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
            if (m.getCover() == null)
                return getDefaultMovieCover();
            return m.getCover();
        } else {
            return null;
        }
    }

    private byte[] getDefaultCinemaImage() {
        if (defaultCinemaImage == null)
            loadDefaultCinemaImage();
        return defaultCinemaImage;
    }

    private byte[] getDefaultMovieCover() {
        if (defaultMovieCover == null)
            loadDefaultMovieCover();
        return defaultMovieCover;
    }

    public static String getCinemaPictureUrl(Cinema c) {
        return "/picture/cinema/" + c.getId();
    }

    public static String getMoviePictureUrl(Movie m) {
        return "/picture/movie/" + m.getId();
    }

    private void loadDefaultCinemaImage() {
        try {
            BufferedImage originalImage = ImageIO
                    .read(this.getClass().getResourceAsStream("/images/defaultCinema.jpg"));

            // convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "jpg", baos);
            baos.flush();
            defaultCinemaImage = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadDefaultMovieCover() {
        try {
            BufferedImage originalImage = ImageIO.read(this.getClass().getResourceAsStream("/images/defaultMovie.jpg"));

            // convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "jpg", baos);
            baos.flush();
            defaultMovieCover = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
