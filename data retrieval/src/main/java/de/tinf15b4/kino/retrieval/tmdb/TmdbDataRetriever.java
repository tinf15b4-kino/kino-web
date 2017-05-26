package de.tinf15b4.kino.retrieval.tmdb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.http.client.HttpClient;
import org.yamj.api.common.http.SimpleHttpClientBuilder;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.enumeration.ReleaseType;
import com.omertron.themoviedbapi.enumeration.SearchType;
import com.omertron.themoviedbapi.methods.TmdbMovies;
import com.omertron.themoviedbapi.methods.TmdbSearch;
import com.omertron.themoviedbapi.model.credits.MediaCreditCrew;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.model.movie.ProductionCompany;
import com.omertron.themoviedbapi.model.movie.ReleaseDate;
import com.omertron.themoviedbapi.model.movie.ReleaseDates;
import com.omertron.themoviedbapi.tools.HttpTools;

import de.tinf15b4.kino.data.movies.AgeControl;
import de.tinf15b4.kino.data.movies.Genre;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.utils.ImageLoader;

public class TmdbDataRetriever {

    private static final String API_KEY = "9eda0433936b655a246eef78d367b530";
    private static final String IMAGE_URL = "http://image.tmdb.org/t/p/w500";
    private static final String NO_INFORMATION = "Keine Angabe";
    private HttpClient httpClient;
    private HttpTools httpTools;
    private TmdbMovies moviesInstance;
    private TmdbSearch searchInstance;

    private Map<String, CachedResult> cache = new HashMap<>();

    private interface CachedResult {
        Movie get() throws MovieDbException;
    }

    private static class CachedSuccess implements CachedResult {
        private Movie movie;

        public CachedSuccess(Movie movie) {
            this.movie = movie;
        }

        @Override
        public Movie get() throws MovieDbException {
            return movie;
        }
    }

    private static class CachedError implements CachedResult {
        private MovieDbException exception;

        public CachedError(MovieDbException exception) {
            this.exception = exception;
        }

        @Override
        public Movie get() throws MovieDbException {
            throw exception;
        }
    }

    public TmdbDataRetriever() {
        httpClient = new SimpleHttpClientBuilder().build();
        httpTools = new HttpTools(httpClient);
        moviesInstance = new TmdbMovies(API_KEY, httpTools);
        searchInstance = new TmdbSearch(API_KEY, httpTools);
    }

    public Movie getMovie(Movie movie) throws MovieDbException {
        String movieName = movie.getName();
        if (cache.containsKey(movieName)) {
            return cache.get(movieName).get();
        } else {
            try {
                Movie retval = getMovieFromDb(movie);
                cache.put(movieName, new CachedSuccess(retval));
                return retval;
            } catch (MovieDbException e) {
                cache.put(movieName, new CachedError(e));
                throw e;
            }
        }
    }

    public Movie getMovieFromDb(Movie movie) throws MovieDbException {
        List<MovieInfo> movies = searchInstance.searchMovie(movie.getName(), 1, "de-DE", false, 0, 0, SearchType.NGRAM)
                .getResults();

        if (!movies.isEmpty()) {
            // reorder results by year
            // We want the movie from the most recent year (e.g. the 2017 version of "Die Schöne und das Biest"),
            // but for movies from the same year, we want the movie db to decide ("Alien: Covenant" vs the Rick and Morty clip)
            movies.sort(Comparator.comparing((MovieInfo m) -> m.getReleaseDate().substring(0,Math.min(m.getReleaseDate().length(), 4))).reversed());

            MovieInfo mi = moviesInstance.getMovieInfo(movies.get(0).getId(), "de-DE", "");
            movie.setName(mi.getTitle());
            movie.setDescription(mi.getOverview());
            movie.setTmdbId(mi.getId());
            movie.setStudio(getStudio(mi));

            if (mi.getPosterPath() != null)
                movie.setCover(getImage(mi));

            // movie.setLengthMinutes(mi.getRuntime()); Can't use this one cause
            // it seems to be broken (returns allways 0)
            movie.setLengthMinutes(mi.getRuntime());
            movie.setAgeControl(getAgeControl(mi));
            movie.setGenre(getGenre(mi));
            movie.setAuthor(getAuthor(mi));
            movie.setDirector(getDirector(mi));
        } else {
            throw new MovieDbException(null, "Movie not found");
        }

        return movie;
    }

    private byte[] getImage(MovieInfo mi) {
        return new ImageLoader().getImage(IMAGE_URL + mi.getPosterPath(), "jpg");
    }

    private String getStudio(MovieInfo mi) {
        String result = mi.getProductionCompanies().stream().map(ProductionCompany::getName)
                .collect(Collectors.joining(", "));
        
        return !result.isEmpty() ? result : NO_INFORMATION;
    }

    private String getAuthor(MovieInfo mi) throws MovieDbException {
        List<MediaCreditCrew> crew = moviesInstance.getMovieCredits(mi.getId()).getCrew();

        String result = crew.stream()
                .filter(c -> c.getDepartment().equals("Writing") && c.getJob().equals("Screenplay"))
                .map(MediaCreditCrew::getName).collect(Collectors.joining(", "));

        return !result.isEmpty() ? result : NO_INFORMATION;
    }

    private String getDirector(MovieInfo mi) throws MovieDbException {
        List<MediaCreditCrew> crew = moviesInstance.getMovieCredits(mi.getId()).getCrew();

        String result = crew.stream()
                .filter(c -> c.getDepartment().equals("Directing") && c.getJob().equals("Director"))
                .map(MediaCreditCrew::getName)
                .collect(Collectors.joining(", "));

        return !result.isEmpty() ? result : NO_INFORMATION;
    }

    private Genre getGenre(MovieInfo mi) {
        // TODO: Bei Gelegenheit umstellen dass ein Film mehere Genres haben
        // kann
        List<com.omertron.themoviedbapi.model.Genre> genres = mi.getGenres();
        if (!genres.isEmpty()) {
            return Arrays.stream(Genre.values()).filter(g -> g.toString().equalsIgnoreCase(genres.get(0).getName())).findAny().get();
        } else {
            return Genre.UNBEKANNT;
        }
    }

    private AgeControl getAgeControl(MovieInfo mi) throws MovieDbException {
        // Get all Release Date Information
        List<ReleaseDates> releases = moviesInstance.getReleaseDates(mi.getId()).getResults();

        // Get Germany Release Date Information
        Optional<ReleaseDates> germany = releases.stream().filter(r -> r.getCountry().equals("DE")).findFirst();

        if (germany.isPresent()) {
            // Get Cinema Release Information
            Optional<ReleaseDate> cinemaRelease = germany.get().getReleaseDate().stream()
                    .filter(r -> r.getType() == ReleaseType.THEATRICAL).findFirst();

            if (cinemaRelease.isPresent()) {
                String age = cinemaRelease.get().getCertification();

                if (!age.equals("")) {
                    return AgeControl.valueOf("USK" + age);
                }
            }
        }
        return AgeControl.UNBEKANNT;
    }
}
