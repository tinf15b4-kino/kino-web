package de.tinf15b4.kino.data.cinemas;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String street;
    private String hnr;
    private String postcode;
    private String city;
    private String country;
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

    public long getId() {
        return id;
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

}
