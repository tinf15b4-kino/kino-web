package de.tinf15b4.kino.data.movies;

public enum Genre {
    ACTION("Action"), ABENTEUER("Abenteuer"), ANIMATION("Animation"), KOMOEDIE("Komödie"), KRIMI(
            "Krimi"), DOKUMENTARFILM("Dokumentarfilm"), DRAMA("Drama"), FAMILIE("Familie"), FANTASY(
                    "Fantasy"), HISTORIE("Historie"), HORROR("Horror"), MUSIK("Musik"), MYSTERY("Mystery"), LIEBESFILM(
                            "Liebesfilm"), SCIENCEFICTION("Science Fiction"), TVFILM(
                                    "TV-Film"), THRILLER("Thriller"), KRIEGSFILM(
                                            "Kriegsfilm"), WESTERN("Western"), UNBEKANNT("Unbekannt");

    private final String translation;

    Genre(String translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return this.translation;
    }
}
