package de.tinf15b4.kino.data.cinemas;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.tinf15b4.kino.data.EntityModel;
import de.tinf15b4.kino.data.ImageContainer;
import de.tinf15b4.kino.data.ratedcinemas.IRateable;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinema;
import de.tinf15b4.kino.data.users.User;

@Entity
@Table(name = "cinema", uniqueConstraints = @UniqueConstraint(columnNames = { Cinema.FieldInfos.NAME }))
public class Cinema extends EntityModel implements ImageContainer, Serializable, IRateable<RatedCinema> {

    private static final long serialVersionUID = -7984776023686706282L;

    static class FieldInfos {
        private FieldInfos() {
            // Just used to hide the public constructor
            // Sonarqube seems to like that
        }

        public static final String NAME = "name";
        public static final String STREET = "street";
        public static final String HNR = "hnr";
        public static final String POSTCODE = "postcode";
        public static final String CITY = "city";
        public static final String COUNTRY = "country";
        public static final String IMAGE = "image";
        public static final int IMAGE_LENGTH = 10000000;
    }

    @Column(name = FieldInfos.NAME, nullable = false)
    private String name;

    @Column(name = FieldInfos.STREET)
    private String street;

    @Column(name = FieldInfos.HNR)
    private String hnr;

    @Column(name = FieldInfos.POSTCODE)
    private String postcode;

    @Column(name = FieldInfos.CITY)
    private String city;

    @Column(name = FieldInfos.COUNTRY)
    private String country;

    @Column(name = FieldInfos.IMAGE, length = FieldInfos.IMAGE_LENGTH)
    private byte[] image;

    public Cinema() {
    }

    public Cinema(String name, String street, String hnr, String postcode, String city, String country, byte[] image) {
        this.name = name;
        this.street = street;
        this.hnr = hnr;
        this.postcode = postcode;
        this.city = city;
        this.country = country;
        this.image = image;
    }

    public String getAddress() {
        return String.format("%s %s%n%s %s, %S", street, hnr, postcode, city, country);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHnr() {
        return hnr;
    }

    public void setHnr(String hnr) {
        this.hnr = hnr;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public void doFilter() {
        setImage(null);
    }

    @Override
    public RatedCinema createRating(User user, int rating, String description, Date date) {
        return new RatedCinema(user, this, rating, description, date);
    }
}
