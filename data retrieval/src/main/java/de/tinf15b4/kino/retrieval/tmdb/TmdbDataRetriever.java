package de.tinf15b4.kino.retrieval.tmdb;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

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

public class TmdbDataRetriever {

    private List<MovieInfo> tmdbMovies;
    private final static String API_KEY = "9eda0433936b655a246eef78d367b530";
    private final static String IMAGE_URL = "http://image.tmdb.org/t/p/w500";
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
        List<MovieInfo> movies = searchInstance.searchMovie(movie.getName(), 1, "de-DE", false, 0, 0, SearchType.NGRAM)
                .getResults();

        if (movies.size() != 0) {
            MovieInfo mi = moviesInstance.getMovieInfo(movies.get(0).getId(), "de-DE", "");
            // MovieInfo mi = moviesInstance.getMovieInfo(550, "de-DE",
            // "RELEASES");
            movie = new Movie();
            movie.setName(mi.getTitle());
            movie.setDescription(mi.getOverview());
            movie.setTmdbId(mi.getId());
            movie.setStudio(getStudio(mi));
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

        try {

            byte[] imageInByte;
            System.out.println(IMAGE_URL + mi.getPosterPath());
            BufferedImage originalImage = ImageIO
                    .read(new URL(IMAGE_URL + mi.getPosterPath()));

            // convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "jpg", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();

            return imageInByte;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private String getStudio(MovieInfo mi) {
        String result = "";
        List<ProductionCompany> list = mi.getProductionCompanies();
        if (list.size() > 0) {
            Iterator<ProductionCompany> i = list.iterator();
            while (i.hasNext()) {
                ProductionCompany item = i.next();
                result += item.getName() + (i.hasNext() ? ", " : "");
            }
            return result;
        }
        return "Keine Angabe";
    }

    private String getAuthor(MovieInfo mi) throws MovieDbException {
        List<MediaCreditCrew> crew = moviesInstance.getMovieCredits(mi.getId()).getCrew();

        String result = "";

        List<MediaCreditCrew> list = crew.stream()
                .filter(c -> c.getDepartment().equals("Writing") && c.getJob().equals("Screenplay"))
                .collect(Collectors.toList());

        if (list.size() > 0) {
            Iterator<MediaCreditCrew> i = list.iterator();
            while (i.hasNext()) {
                MediaCreditCrew item = i.next();
                result += item.getName() + (i.hasNext() ? ", " : "");
            }
            return result;
        }
        return "Keine Angabe";
    }

    private String getDirector(MovieInfo mi) throws MovieDbException {
        List<MediaCreditCrew> crew = moviesInstance.getMovieCredits(mi.getId()).getCrew();

        String result = "";

        List<MediaCreditCrew> list = crew.stream()
                .filter(c -> c.getDepartment().equals("Directing") && c.getJob().equals("Director"))
                .collect(Collectors.toList());

        if (list.size() > 0) {
            Iterator<MediaCreditCrew> i = list.iterator();
            while (i.hasNext()) {
                MediaCreditCrew item = i.next();
                result += item.getName() + (i.hasNext() ? ", " : "");
            }
            return result;
        }
        return "Keine Angabe";
    }

    private Genre getGenre(MovieInfo mi) {
        // TODO: Bei Gelegenheit umstellen dass ein Film mehere Genres haben
        // kann
        List<com.omertron.themoviedbapi.model.Genre> genres = mi.getGenres();
        if (genres.size() > 0) {
            String name = genres.get(0).getName();

            switch (name) {
            case "Komödie":
                name = "Komoedie";
                break;
            case "Science Fiction":
                name = "ScienceFiction";
                break;
            case "TV-Film":
                name = "TvFilm";
            }

            return Genre.valueOf(name);
        }
        return Genre.Unbekannt;
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
