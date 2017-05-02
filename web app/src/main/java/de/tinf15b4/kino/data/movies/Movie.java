package de.tinf15b4.kino.data.movies;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;

import de.tinf15b4.kino.data.EntityModel;
import de.tinf15b4.kino.data.ImageContainer;

@Entity
@Table(name = "movie", uniqueConstraints = @UniqueConstraint(columnNames = { Movie.FieldInfos.NAME }))
public class Movie extends EntityModel implements ImageContainer {

    public interface FieldInfos {
        String NAME = "name";
        String DESCRIPTION = "description";
        String COVER = "cover";
        int COVER_LENGTH = 100000;
        String LENGTH_MINUTES = "lengthMinutes";
        String AGE_CONTROL = "ageControl";
        String GENRE = "genre";
        String THE_MOVIE_DB_ID = "theMovieDbId";
    }

    @Column(name = Movie.FieldInfos.NAME, nullable = false)
    private String name;

    @Column(name = Movie.FieldInfos.DESCRIPTION)
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Column(name = Movie.FieldInfos.COVER, length = Movie.FieldInfos.COVER_LENGTH)
    private byte[] cover;

    @Column(name = Movie.FieldInfos.LENGTH_MINUTES)
    private long lengthMinutes;

    @Column(name = Movie.FieldInfos.AGE_CONTROL)
    private AgeControl ageControl;

    @Column(name = Movie.FieldInfos.GENRE)
    private Genre genre;

    @Column(name = Movie.FieldInfos.THE_MOVIE_DB_ID)
    private int theMovieDbId;

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

    public int getTheMovieDbId() {
        return theMovieDbId;
    }

    public void setTheMovieDbId(int theMovieDbId) {
        this.theMovieDbId = theMovieDbId;
    }

    @Override
    public void doFilter() {
        setCover(null);
    }
}
