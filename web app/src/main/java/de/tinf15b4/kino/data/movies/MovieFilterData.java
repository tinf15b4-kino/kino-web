package de.tinf15b4.kino.data.movies;

import java.util.Date;
import java.util.Set;

public class MovieFilterData {

    private AgeControl ageControl;
    private Genre genre;
    private Integer upperPrice;
    private Integer lowerPrice;
    private Date upperTime;
    private Date lowerTime;

    public MovieFilterData() {
        // don't initialize anything
        // this means everything will be shown
    }

    public MovieFilterData(AgeControl ageControl, Genre genre, Integer upperPrice, Integer lowerPrice, Date upperTime,
            Date lowerTime) {
        this.ageControl = ageControl;
        this.genre = genre;
        this.upperPrice = upperPrice;
        this.lowerPrice = lowerPrice;
        this.upperTime = upperTime;
        this.lowerTime = lowerTime;
    }

    public AgeControl getAgeControl() {
        return ageControl;
    }

    public void setAgeControl(Set<AgeControl> ageControl) {
        // FIXME: TEMPORARY!! Waiting for fix...
        this.ageControl = ageControl.size() > 0 ? ageControl.iterator().next() : null;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
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
