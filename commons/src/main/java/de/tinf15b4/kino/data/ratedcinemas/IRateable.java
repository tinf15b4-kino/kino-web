package de.tinf15b4.kino.data.ratedcinemas;

import java.util.Date;

import de.tinf15b4.kino.data.users.User;

public interface IRateable<Rated> {

    public Rated createRating(User user, int rating, String description, Date date);

}
