package de.tinf15b4.kino.data.movies;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Movie {
    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String description;

    @Column(length = 100000)
    private byte[] cover;

    private long lengthMinutes;
    private AgeControl ageControl;
    private Genre genre;

    public Movie() {

    }

    public Movie(String name, String description, byte[] cover, long lengthMinutes, Genre genre,
            AgeControl ageControl) {
        this.name = name;
        this.description = description;
        this.cover = cover;
        this.lengthMinutes = lengthMinutes;
        this.genre = genre;
        this.ageControl = ageControl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public long getLengthMinutes() {
        return lengthMinutes;
    }

    public void setLengthMinutes(long lengthMinutes) {
        this.lengthMinutes = lengthMinutes;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public AgeControl getAgeControl() {
        return ageControl;
    }

    public void setAgeControl(AgeControl ageControl) {
        this.ageControl = ageControl;
    }
}
