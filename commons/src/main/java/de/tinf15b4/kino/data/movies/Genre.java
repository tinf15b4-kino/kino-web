package de.tinf15b4.kino.data.movies;

public enum Genre {
    Action("Action"), Abenteuer("Abenteuer"), Animation("Animation"), Komoedie("Komödie"), Krimi(
            "Krimi"), Dokumentarfilm("Dokumentarfilm"), Drama("Drama"), Familie("Familie"), Fantasy(
                    "Fantasy"), Historie("Historie"), Horror("Horror"), Musik("Musik"), Mystery("Mystery"), Liebesfilm(
                            "Liebesfilm"), ScienceFiction("Science Fiction"), TvFilm(
                                    "TV-Film"), Thriller("Thriller"), Kriegsfilm(
                                            "Kriegsfilm"), Western("Western"), Unbekannt("Unbekannt");

    private final String translation;

    Genre(String translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return this.translation;
    }
}
