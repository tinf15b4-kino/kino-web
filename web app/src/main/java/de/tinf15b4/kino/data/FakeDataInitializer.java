package de.tinf15b4.kino.data;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Locale;

@Service
public class FakeDataInitializer implements DataInitializer {
    @Autowired
    private CinemaRepository cineRepo;

    @Autowired
    private FavoriteRepository faveRepo;

    @Autowired
    private MovieRepository movieRepo;

    @Autowired
    private PlaylistRepository playlistRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    @Transactional
    public void initialize() {
        Faker faker = new Faker(new Locale("de"));

        // Some fake films
        for (int i = 0; i < 10; ++i) {
            Movie m = new Movie();
            m.setName(faker.superhero().name());
            m.setDescription(faker.shakespeare().kingRichardIIIQuote());
            m.setLengthMinutes(faker.number().numberBetween(20, 240));

            movieRepo.save(m);
        }

        // 10 fake cinemas
        for (int i = 0; i < 10; ++i) {
            Cinema c = new Cinema();
            c.setName(faker.company().name());
            Address a = faker.address();
            c.setAddress(a.streetName(), a.streetAddressNumber(), a.zipCode(), a.city());

            cineRepo.save(c);

            // Cinema 3,5,7 are favorites
            if (i == 3 || i == 5 || i == 7) {
                faveRepo.save(new Favorite(c));
            }

            // Now make them show some movies during the next week
            for (int j = 0; j < 20; ++j) {
                Playlist p = new Playlist();
                p.setCinema(c);
                p.setMovie(movieRepo.findOne(faker.number().numberBetween(1L, 10)));
                p.setPrice(faker.number().numberBetween(250, 1739));
                p.setTime(faker.date().between(new Date(), new Date(new Date().getTime() + 1000L*3600*24*7)));

                playlistRepo.save(p);
            }
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
    }
}
