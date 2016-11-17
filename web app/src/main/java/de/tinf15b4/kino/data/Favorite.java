package de.tinf15b4.kino.data;

import javax.persistence.*;

@Entity
public class Favorite {
    public Favorite() {
    }

    public Favorite(Cinema c) {
        setCinema(c);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Cinema cinema;

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }
}
