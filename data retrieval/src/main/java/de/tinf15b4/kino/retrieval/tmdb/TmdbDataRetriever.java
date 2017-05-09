package de.tinf15b4.kino.retrieval.tmdb;

import java.util.List;

import org.apache.http.client.HttpClient;
import org.yamj.api.common.http.SimpleHttpClientBuilder;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.enumeration.SearchType;
import com.omertron.themoviedbapi.methods.TmdbMovies;
import com.omertron.themoviedbapi.methods.TmdbSearch;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.tools.HttpTools;

import de.tinf15b4.kino.data.movies.Movie;

public class TmdbDataRetriever {

    private List<MovieInfo> tmdbMovies;
    private final static String API_KEY = "9eda0433936b655a246eef78d367b530";
    private HttpClient httpClient;
    private HttpTools httpTools;
    private TmdbMovies moviesInstance;
    private TmdbSearch searchInstance;

    public TmdbDataRetriever() {
        httpClient = new SimpleHttpClientBuilder().build();
        httpTools = new HttpTools(httpClient);
        moviesInstance = new TmdbMovies(API_KEY, httpTools);
        searchInstance = new TmdbSearch(API_KEY, httpTools);
    }

    public Movie getMovie(Movie movie) throws MovieDbException {
        System.out.println(movie.getName());
        List<MovieInfo> movies = searchInstance.searchMovie(movie.getName(), 1, "de-DE", false, 0, 0, SearchType.NGRAM)
                .getResults();

        if (movies.size() != 0) {
            MovieInfo mi = movies.get(0);
        movie = new Movie();
        movie.setName(mi.getTitle());
        movie.setDescription(mi.getOverview());
        // m.setCover(cover);
        movie.setLengthMinutes(mi.getRuntime());
        // m.setAgeControl(ageControl);
        // m.setGenre(genre);
        movie.setTmdbId(mi.getId());
        } else {
            throw new MovieDbException(null, "Movie not found");
        }

        return movie;
    }

    public void getMoviexx() {
        try {
            tmdbMovies = moviesInstance.getNowPlayingMovies(15, "de-DE").getResults();

            Movie m;
            for (MovieInfo mi : tmdbMovies) {
                System.out.println(mi.getTitle());
                m = new Movie();
                m.setName(mi.getTitle());
                m.setDescription(mi.getOverview());
                // m.setCover(cover);
                m.setLengthMinutes(mi.getRuntime());
                System.out.println(mi.getRuntime());
                // m.setAgeControl(ageControl);
                // m.setGenre(genre);
                m.setTmdbId(mi.getId());
            }
        } catch (MovieDbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
