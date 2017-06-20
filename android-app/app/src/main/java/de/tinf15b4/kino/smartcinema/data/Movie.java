package de.tinf15b4.kino.smartcinema.data;

import java.io.Serializable;

public class Movie implements Serializable {
    public long id;
    public String name;
    public String description;
    public String lengthMinutes;
    public String ageControl;
    public String genre;
    public String studio;
    public String author;
    public String director;

    @Override
    public String toString() {
        return name;
    }
}
