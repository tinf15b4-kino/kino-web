package de.tinf15b4.kino.smartcinema.data;

import java.io.Serializable;

public class Cinema implements Serializable {
    public long id;
    public String name;
    public String street;
    public String hnr;
    public String postcode;
    public String city;
    public String country;

    @Override
    public String toString() {
        return name;
    }

    public String getAddress() {
        StringBuilder b = new StringBuilder();
        b.append(street);
        b.append(" ");
        b.append(hnr);
        b.append('\n');
        b.append(postcode);
        b.append(' ');
        b.append(city);
        return b.toString();
    }
}
