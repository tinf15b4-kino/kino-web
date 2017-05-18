package de.tinf15b4.kino.data.movies;

public enum AgeControl {
    USK0("FSK 0"), USK6("FSK 6"), USK12("FSK 12"), USK16("FSK 16"), USK18("FSK 18"), UNBEKANNT("Keine Angabe");

    private String caption;

    private AgeControl(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }
}
