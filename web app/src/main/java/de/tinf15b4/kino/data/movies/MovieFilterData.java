package de.tinf15b4.kino.data.movies;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MovieFilterData {

    private Set<AgeControl> ageControl;
    private Set<Genre> genre;
    private Integer upperPrice;
    private Integer lowerPrice;
    private Date upperTime;
    private Date lowerTime;

    public MovieFilterData() {
        this.ageControl = new HashSet<>();
        this.genre = new HashSet<>();
    }

    public MovieFilterData(Set<AgeControl> ageControl, Set<Genre> genre, Integer upperPrice, Integer lowerPrice,
            Date upperTime,
            Date lowerTime) {
        this.ageControl = ageControl;
        this.genre = genre;
        this.upperPrice = upperPrice;
        this.lowerPrice = lowerPrice;
        this.upperTime = upperTime;
        this.lowerTime = lowerTime;
    }

    public Set<AgeControl> getAgeControl() {
        return ageControl;
    }

    public void setAgeControl(Set<AgeControl> ageControl) {
        this.ageControl = ageControl;
    }

    public Set<Genre> getGenre() {
        return genre;
    }

    public void setGenre(Set<Genre> genre) {
        this.genre = genre;
    }

    public Integer getUpperPrice() {
        return upperPrice;
    }

    public void setUpperPrice(Integer upperPrice) {
        this.upperPrice = upperPrice;
    }

    public Integer getLowerPrice() {
        return lowerPrice;
    }

    public void setLowerPrice(Integer lowerPrice) {
        this.lowerPrice = lowerPrice;
    }

    public Date getUpperTime() {
        return upperTime;
    }

    public void setUpperTime(Date upperTime) {
        this.upperTime = upperTime;
    }

    public Date getLowerTime() {
        return lowerTime;
    }

    public void setLowerTime(Date lowerTime) {
        this.lowerTime = lowerTime;
    }

}
