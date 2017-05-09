package de.tinf15b4.kino.data.movies;

import java.util.Arrays;

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
        int COVER_LENGTH = 100000;
        String LENGTH_MINUTES = "lengthMinutes";
        String AGE_CONTROL = "ageControl";
        String GENRE = "genre";
        String TMDB_ID = "tmdbID";
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

    @Override
    public void doFilter() {
        setCover(null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((ageControl == null) ? 0 : ageControl.hashCode());
        result = prime * result + Arrays.hashCode(cover);
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((genre == null) ? 0 : genre.hashCode());
        result = prime * result + (int) (lengthMinutes ^ (lengthMinutes >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Movie other = (Movie) obj;
        if (ageControl != other.ageControl)
            return false;
        if (!Arrays.equals(cover, other.cover))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (genre != other.genre)
            return false;
        if (lengthMinutes != other.lengthMinutes)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
