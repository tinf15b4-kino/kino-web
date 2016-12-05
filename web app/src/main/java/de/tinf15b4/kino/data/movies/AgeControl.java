package de.tinf15b4.kino.data.movies;

public enum AgeControl {
    USK0, USK6, USK12, USK16, USK18;

    public String getCaption() {
        return toString();
    }
}
