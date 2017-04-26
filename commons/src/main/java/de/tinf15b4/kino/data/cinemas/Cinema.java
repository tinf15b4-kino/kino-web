package de.tinf15b4.kino.data.cinemas;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.tinf15b4.kino.data.EntityModel;
import de.tinf15b4.kino.data.ImageContainer;

@Entity
@Table(name = "cinema", uniqueConstraints = @UniqueConstraint(columnNames = { Cinema.FieldInfos.NAME }))
public class Cinema extends EntityModel implements ImageContainer {

    public interface FieldInfos {
        String NAME = "name";
        String STREET = "street";
        String HNR = "hnr";
        String POSTCODE = "postcode";
        String CITY = "city";
        String COUNTRY = "country";
        String IMAGE = "image";
        int IMAGE_LENGTH = 100000;
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
        return String.format("%s %s\n%s %s, %S", street, hnr, postcode, city, country);
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
}
