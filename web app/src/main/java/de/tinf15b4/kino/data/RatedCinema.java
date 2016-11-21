package de.tinf15b4.kino.data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
// @IdClass(RatedCinemaId.class)
public class RatedCinema {
    
    // // FIXME: This should not have its own id
    // @Id
    // @GeneratedValue
    // private long id;
    //
    // @Id
    // @ManyToOne
    // private User user;
    //
    // @Id
    // private String cinema;
    // @ManyToOne
    // private Cinema cinema;

    @EmbeddedId
    private RatedCinemaId id;

    private int rating;
    private String description;
    private Date time;

    public RatedCinemaId getId() {
        return id;
    }

    public void setId(RatedCinemaId id) {
        this.id = id;
    }

    public RatedCinema() {

    }

    // public RatedCinema(User user, String cinema, int rating, String
    // description, Date time) {
    // this.user = user;
    // this.cinema = cinema;
    // this.rating = rating;
    // this.description = description;
    // this.time = time;
    // }

    public RatedCinema(RatedCinemaId id, int rating, String description, Date time) {
        this.id = id;
        this.rating = rating;
        this.description = description;
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public User getUser() {
        return id.getUser();
    }

    public void setUser(User user) {
        id.setUser(user);
    }

    public Cinema getCinema() {
        return id.getCinema();
    }

    public void setCinema(Cinema cinema) {
        id.setCinema(cinema);
    }

    // public User getUser() {
    // return user;
    // }
    //
    // public void setUser(User user) {
    // this.user = user;
    // }
    //
    // public String getCinema() {
    // return cinema;
    // }
    //
    // public void setCinema(String cinema) {
    // this.cinema = cinema;
    // }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

@Embeddable
class RatedCinemaId implements Serializable {

    private static final long serialVersionUID = 7236718476657754622L;
    
    @ManyToOne
    private User user;

    @ManyToOne
    private Cinema cinema;

    public RatedCinemaId(User user, Cinema cinema) {
        this.user = user;
        this.cinema = cinema;
    }

    public RatedCinemaId() {

    }

    @Override
    public int hashCode() {
        return cinema.hashCode() + user.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RatedCinemaId))
            return false;

        RatedCinemaId that = (RatedCinemaId) o;
        return user.getId() == that.getUser().getId() && cinema.getId() == that.getCinema().getId();

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }
}