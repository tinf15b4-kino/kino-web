package de.tinf15b4.kino.data.initializer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.movies.AgeControl;
import de.tinf15b4.kino.data.movies.Genre;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieService;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.data.playlists.PlaylistService;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinema;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaService;
import de.tinf15b4.kino.data.ratedmovies.RatedMovie;
import de.tinf15b4.kino.data.ratedmovies.RatedMovieService;
import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.data.users.UserService;

@Service
public class FakeDataInitializer implements DataInitializer {
    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private UserService userService;

    @Autowired
    private RatedCinemaService ratedCinemaService;

    @Autowired
    private RatedMovieService ratedMovieService;

    Random rnd = new Random();

    @Override
    public void initialize() {
        Faker faker = new Faker(new Locale("de"));

        // Some fake films
        for (int i = 0; i < 10; ++i) {
            Movie m = new Movie();
            m.setName(faker.superhero().name());
            m.setDescription(faker.shakespeare().kingRichardIIIQuote());
            m.setLengthMinutes(faker.number().numberBetween(20, 240));
            m.setGenre(Genre.values()[rnd.nextInt(Genre.values().length)]);
            m.setAgeControl(AgeControl.values()[rnd.nextInt(AgeControl.values().length)]);

            // Picture
            try {

                byte[] imageInByte;
                BufferedImage originalImage = ImageIO
                        .read(this.getClass().getResourceAsStream("/images/defaultMovie.jpg"));

                // convert BufferedImage to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(originalImage, "jpg", baos);
                baos.flush();
                imageInByte = baos.toByteArray();
                baos.close();

                m.setCover(imageInByte);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            movieService.save(m);
        }

        // One fake user that is guaranteed to exist, for debugging
        User u = new User();
        u.setName("Max Mustermann");
        u.setEmail("max.mustermann@example.com");
        u.setPassword("muster");
        userService.save(u);

        // Some more fake users
        for (int i = 0; i < 10; ++i) {
            u = new User();
            u.setName(faker.name().fullName());
            u.setEmail(faker.internet().emailAddress());
            u.setPassword(faker.beer().name());
            userService.save(u);
        }

        // 10 fake cinemas
        for (int i = 0; i < 10; ++i) {
            Cinema c = new Cinema();
            c.setName(faker.company().name());
            Address a = faker.address();
            c.setStreet(a.streetName());
            c.setHnr(a.streetAddressNumber());
            c.setPostcode(a.zipCode());
            c.setCity(a.cityName());
            c.setCountry(a.country());

            // Picture
            try {

                byte[] imageInByte;
                BufferedImage originalImage = ImageIO
                        .read(this.getClass().getResourceAsStream("/images/defaultCinema.jpg"));

                // convert BufferedImage to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(originalImage, "jpg", baos);
                baos.flush();
                imageInByte = baos.toByteArray();
                baos.close();

                c.setImage(imageInByte);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            cinemaService.save(c);

            // Cinema 4,6,8 are favorites
            if (i == 3 || i == 5 || i == 7) {
                favoriteService.save(new Favorite(userService.findOne(1l), c));
            }

            // Now make them show some movies during the next week
            for (int j = 0; j < 20; ++j) {
                Playlist p = new Playlist();
                p.setCinema(c);
                p.setMovie(movieService.findOne(faker.number().numberBetween(1L, 10)));
                p.setPrice(faker.number().numberBetween(250, 1739));
                p.setTime(faker.date().between(new Date(), new Date(new Date().getTime() + 1000L * 3600 * 24 * 7)));

                playlistService.save(p);
            }
        }

        // Some fake Cinema Ratings
        for (Cinema c : cinemaService.findAll()) {
            // Add random 0..3 ratings
            if (c.getId() == cinemaService.findOne(1L).getId()) {
                // First Cinema doesn't get a Rating
            } else {
                long quantity = (long) rnd.nextInt(4);

                for (long j = 0; j < quantity; j++) {
                    RatedCinema rc = new RatedCinema();
                    u = userService.findOne((long) rnd.nextInt((int) userService.count()) + 1L);
                    rc.setUser(u);
                    rc.setCinema(c);
                    rc.setRating(rnd.nextInt(6));
                    rc.setDescription(faker.chuckNorris().fact());
                    rc.setTime(
                            faker.date().between(new Date(), new Date(new Date().getTime() + 1000L * 3600 * 24 * 7)));

                    if (!ratedCinemaService.save(rc).isPresent()) {
                        System.out.println(String.format("Rating for User: %s and Cinema: %s already exist",
                                rc.getUser().getName(), rc.getCinema().getName()));
                    }
                }
            }
        }

        // Some fake Movie Ratings
        for (Movie m : movieService.findAll()) {
            // Add random 0..3 ratings
            long quantity = (long) rnd.nextInt(4);

            if (m.getId() == movieService.findOne(1L).getId()) {
                // First Movie doesn't get a Rating
            } else {
                for (long j = 0; j < quantity; j++) {
                    RatedMovie rm = new RatedMovie();
                    u = userService.findOne((long) rnd.nextInt((int) userService.count()) + 1L);
                    rm.setUser(u);
                    rm.setMovie(m);
                    rm.setRating(rnd.nextInt(6));
                    rm.setDescription(faker.chuckNorris().fact());
                    rm.setTime(
                            faker.date().between(new Date(), new Date(new Date().getTime() + 1000L * 3600 * 24 * 7)));

                    if (!ratedMovieService.save(rm).isPresent()) {
                        System.out.println(String.format("Rating for User: \"%s\" and Movie: \"%s\" already exist",
                                rm.getUser().getName(), rm.getMovie().getName()));
                    }
                }
            }
        }
    }
}
