package de.tinf15b4.kino.data.initializer;

import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieService;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.data.playlists.PlaylistService;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinema;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaId;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaService;
import de.tinf15b4.kino.data.ratedmovies.RatedMovie;
import de.tinf15b4.kino.data.ratedmovies.RatedMovieId;
import de.tinf15b4.kino.data.ratedmovies.RatedMovieService;
import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.data.users.UserRepository;

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
    private UserRepository userRepo;

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

            movieService.save(m);
        }

        // One fake user that is guaranteed to exist, for debugging
        User u = new User();
        u.setName("Max Mustermann");
        u.setEmail("max.mustermann@example.com");
        u.setPassword("muster");
        userRepo.save(u);

        // Some more fake users
        for (int i = 0; i < 10; ++i) {
            u = new User();
            u.setName(faker.name().fullName());
            u.setEmail(faker.internet().emailAddress());
            u.setPassword(faker.beer().name());
            userRepo.save(u);
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
            // c.setImage(ImageIO.read(this.getClass().getResource("/images/defaultCinema.png")).);

            cinemaService.save(c);

            // Cinema 3,5,7 are favorites
            if (i == 3 || i == 5 || i == 7) {
                favoriteService.save(new Favorite(userRepo.getOne(1l), c));
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
            long quantity = (long) rnd.nextInt(4);

            for (long j = 0; j < quantity; j++) {
                RatedCinema rc = new RatedCinema();
                u = userRepo.getOne((long) rnd.nextInt((int) userRepo.count()) + 1L);
                rc.setId(new RatedCinemaId(u, c));
                rc.setRating(rnd.nextInt(6));
                rc.setDescription(faker.chuckNorris().fact());
                rc.setTime(faker.date().between(new Date(), new Date(new Date().getTime() + 1000L * 3600 * 24 * 7)));

                ratedCinemaService.save(rc);
            }
        }

        // Some fake Movie Ratings
        for (Movie m : movieService.findAll()) {
            // Add random 0..3 ratings
            long quantity = (long) rnd.nextInt(4);

            for (long j = 0; j < quantity; j++) {
                RatedMovie rm = new RatedMovie();
                u = userRepo.getOne((long) rnd.nextInt((int) userRepo.count()) + 1L);
                rm.setId(new RatedMovieId(u, m));
                rm.setRating(rnd.nextInt(6));
                rm.setDescription(faker.chuckNorris().fact());
                rm.setTime(faker.date().between(new Date(), new Date(new Date().getTime() + 1000L * 3600 * 24 * 7)));

                ratedMovieService.save(rm);
            }
        }

    }
}