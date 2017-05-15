package de.tinf15b4.kino.data.movies;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;

import de.tinf15b4.kino.data.EntityModel;
import de.tinf15b4.kino.data.ImageContainer;

@Entity
@Table(name = "movie", uniqueConstraints = { @UniqueConstraint(columnNames = { Movie.FieldInfos.NAME }),
        @UniqueConstraint(columnNames = { Movie.FieldInfos.TMDB_ID }) })
public class Movie extends EntityModel implements ImageContainer {

    public interface FieldInfos {
        String NAME = "name";
        String DESCRIPTION = "description";
        String COVER = "cover";
        int COVER_LENGTH = 1000000;
        String LENGTH_MINUTES = "lengthMinutes";
        String AGE_CONTROL = "ageControl";
        String GENRE = "genre";
        String TMDB_ID = "tmdbID";
        String STUDIO = "studio";
        String AUTHOR = "author";
        String DIRECTOR = "director";
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

    @Column(name = Movie.FieldInfos.TMDB_ID)
    private int tmdbId;

    @Column(name = Movie.FieldInfos.STUDIO)
    @Type(type = "org.hibernate.type.TextType")
    private String studio;

    @Column(name = Movie.FieldInfos.AUTHOR)
    @Type(type = "org.hibernate.type.TextType")
    private String author;

    @Column(name = Movie.FieldInfos.DIRECTOR)
    @Type(type = "org.hibernate.type.TextType")
    private String director;

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

    public int getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    @Override
    public void doFilter() {
        setCover(null);
    }
}
