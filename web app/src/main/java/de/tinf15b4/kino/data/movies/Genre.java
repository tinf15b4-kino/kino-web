package de.tinf15b4.kino.data.movies;

public enum Genre {
    Drama("Drama"), Comedy("Komödie"), Thriller("Thriller"), Romantic("Liebesfilm"), Horror("Horror"), SciFi(
            "Science Fiction");

    private final String translation;

    Genre(String translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return this.translation;
    }
}
