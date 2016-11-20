package de.tinf15b4.kino.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Cinema {
    public Cinema() {
        setName("");
    }

    public Cinema(String name) {
        setName(name);
    }

    public long getId() {
        return id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddress(String street, String number,
                           String zipCode, String city)
    {
        this.setAddress(String.format("%s %s\n%s %s", street, number, zipCode, city));
    }

    private String address;
}
