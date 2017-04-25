package de.tinf15b4.kino.api.rest;

import java.util.Date;
import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthNone;
import org.jsondoc.core.annotation.ApiBodyObject;
import org.jsondoc.core.annotation.ApiError;
import org.jsondoc.core.annotation.ApiErrors;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.annotation.ApiVersion;
import org.jsondoc.core.pojo.ApiStage;
import org.jsondoc.core.pojo.ApiVisibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.tinf15b4.kino.data.ImageContainer;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieFilterData;
import de.tinf15b4.kino.data.movies.MovieService;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.data.playlists.PlaylistService;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinema;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaService;
import de.tinf15b4.kino.data.ratedmovies.RatedMovie;
import de.tinf15b4.kino.data.ratedmovies.RatedMovieService;
import de.tinf15b4.kino.data.search.SearchResult;
import de.tinf15b4.kino.data.search.SearchService;

@Api(name = "General services", description = "Offers all methods needed to hook into the database without any authentication", visibility = ApiVisibility.PUBLIC, stage = ApiStage.BETA)
@ApiVersion(since = "0.0.1")
@ApiAuthNone
@RestController
public class GeneralRestController {

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private RatedCinemaService ratedCinemaService;

    @Autowired
    private RatedMovieService ratedMovieService;

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private SearchService searchService;

    @ApiMethod(description = "Returns all cinemas")
    @RequestMapping(value = "rest/getCinemas", method = RequestMethod.GET)
    public ResponseEntity<?> getCinemas() {
        List<Cinema> cinemas = cinemaService.findAll();
        filterImages(cinemas);
        return ResponseEntity.ok(cinemas.toArray(new Cinema[0]));
    }

    @ApiMethod(description = "Returns the cinema with the given id")
    @RequestMapping(value = "rest/getCinema", method = RequestMethod.GET)
    public ResponseEntity<?> getCinema(
            @ApiQueryParam(name = "cinemaId", description = "Id of the cinema") @RequestParam(name = "cinemaId") long cinemaId) {
        Cinema cinema = cinemaService.findOne(cinemaId);
        if (cinema != null) {
            cinema.doFilter();
            return ResponseEntity.ok(cinema);
        }
        return ResponseEntity.badRequest().body(RestControllerConstants.INVALID_ID);
    }

    @ApiMethod(description = "Returns all movies")
    @RequestMapping(value = "rest/getMovies", method = RequestMethod.GET)
    public ResponseEntity<?> getMovies() {
        List<Movie> movies = movieService.findAll();
        filterImages(movies);
        return ResponseEntity.ok(movies.toArray(new Movie[0]));
    }

    @ApiMethod(description = "Returns the movie with the given id")
    @RequestMapping(value = "rest/getMovie", method = RequestMethod.GET)
    public ResponseEntity<?> getMovie(
            @ApiQueryParam(name = "movieId", description = "Id of the movie") @RequestParam(name = "movieId") long movieId) {
        Movie movie = movieService.findOne(movieId);
        if (movie != null) {
            movie.doFilter();
            return ResponseEntity.ok(movie);
        }
        return ResponseEntity.badRequest().body(RestControllerConstants.INVALID_ID);
    }

    @ApiMethod(description = "Returns all movies filtered by the given MovieFilterData")
    @RequestMapping(value = "rest/getFilteredMovies", method = RequestMethod.POST)
    public ResponseEntity<?> getFilteredMovies(
            @ApiBodyObject(clazz = MovieFilterData.class) @RequestBody MovieFilterData filterData) {
        List<Movie> movies = movieService.allmightyFilter(filterData);
        filterImages(movies);
        return ResponseEntity.ok(movies.toArray(new Movie[0]));
    }

    @ApiMethod(description = "Returns all playtimes for the cinema with the given id between the given dates")
    @ApiErrors(apierrors = { @ApiError(code = "400", description = "Date was not parseable or the id does not exist") })
    @RequestMapping(value = "rest/getPlaylistForCinema", method = RequestMethod.GET)
    public ResponseEntity<?> getPlaylistForCinema(
            @ApiQueryParam(name = "cinemaId", description = "Id of the cinema") @RequestParam(name = "cinemaId") long cinemaId,
            @ApiQueryParam(name = "from", description = "Start date in milliseconds from epoch") @RequestParam(name = "from") long from,
            @ApiQueryParam(name = "to", description = "End date in milliseconds from epoch") @RequestParam(name = "to") long to) {
        Cinema cinema = cinemaService.findOne(cinemaId);
        if (cinema != null) {
            List<Playlist> playlists = playlistService.findForCinema(cinema, new Date(from), new Date(to));
            filterImages(playlists);
            return ResponseEntity.ok(playlists.toArray(new Playlist[0]));
        }
        return ResponseEntity.badRequest().body(RestControllerConstants.INVALID_ID);
    }

    @ApiMethod(description = "Returns all playtimes for the movie with the given id between the given dates")
    @ApiErrors(apierrors = { @ApiError(code = "400", description = "Date was not parseable or the id does not exist") })
    @RequestMapping(value = "rest/getPlaylistForMovie", method = RequestMethod.GET)
    public ResponseEntity<?> getPlaylistForMovie(
            @ApiQueryParam(name = "movieId", description = "Id of the movie") @RequestParam(name = "movieId") long movieId,
            @ApiQueryParam(name = "from", description = "Start date in milliseconds from epoch") @RequestParam(name = "from") long from,
            @ApiQueryParam(name = "to", description = "End date in milliseconds from epoch") @RequestParam(name = "to") long to) {
        Movie movie = movieService.findOne(movieId);
        if (movie != null) {
            List<Playlist> playlists = playlistService.findForMovie(movie, new Date(from), new Date(to));
            filterImages(playlists);
            return ResponseEntity.ok(playlists.toArray(new Playlist[0]));
        }
        return ResponseEntity.badRequest().body(RestControllerConstants.INVALID_ID);
    }

    @ApiMethod(description = "Returns all ratings for the movie with the given id ")
    @ApiErrors(apierrors = { @ApiError(code = "400", description = "Id does not exist") })
    @RequestMapping(value = "rest/getRatedMovies", method = RequestMethod.GET)
    public ResponseEntity<?> getRatedMovies(
            @ApiQueryParam(name = "movieId", description = "Id of the movie") @RequestParam(name = "movieId") long movieId) {
        Movie movie = movieService.findOne(movieId);
        if (movie != null) {
            List<RatedMovie> movies = ratedMovieService.findRatingsByMovie(movie);
            filterImages(movies);
            return ResponseEntity.ok(movies.toArray(new RatedMovie[0]));
        }
        return ResponseEntity.badRequest().body(RestControllerConstants.INVALID_ID);
    }

    @ApiMethod(description = "Returns the average rating for the movie with the given id ")
    @ApiErrors(apierrors = { @ApiError(code = "400", description = "Id does not exist") })
    @RequestMapping(value = "rest/getAverageRatingForMovie", method = RequestMethod.GET)
    public ResponseEntity<?> getAverageRatingForMovie(
            @ApiQueryParam(name = "movieId", description = "Id of the movie") @RequestParam(name = "movieId") long movieId) {
        Movie movie = movieService.findOne(movieId);
        if (movie != null) {
            double rating = ratedMovieService.getAverageRatingForMovie(movie);
            return ResponseEntity.ok(rating);
        }
        return ResponseEntity.badRequest().body(RestControllerConstants.INVALID_ID);
    }

    @ApiMethod(description = "Returns all ratings for the cinema with the given id")
    @ApiErrors(apierrors = { @ApiError(code = "400", description = "Id does not exist") })
    @RequestMapping(value = "rest/getRatedCinemas", method = RequestMethod.GET)
    public ResponseEntity<?> getRatedCinemas(
            @ApiQueryParam(name = "cinemaId", description = "Id of the cinema") @RequestParam(name = "cinemaId") long cinemaId) {
        Cinema cinema = cinemaService.findOne(cinemaId);
        if (cinema != null) {
            List<RatedCinema> cinemas = ratedCinemaService.findRatingsByCinema(cinemaService.findOne(cinemaId));
            filterImages(cinemas);
            return ResponseEntity.ok(cinemas.toArray(new RatedCinema[0]));
        }
        return ResponseEntity.badRequest().body(RestControllerConstants.INVALID_ID);
    }

    @ApiMethod(description = "Returns the search result when searching for the given term")
    @RequestMapping(value = "rest/getSearchResult", method = RequestMethod.GET)
    public ResponseEntity<?> getSearchResult(
            @ApiQueryParam(name = "term", description = "search term") @RequestParam(name = "term") String term) {
        SearchResult result = searchService.search(term);
        result.doFilter();
        return ResponseEntity.ok(result);
    }

    private void filterImages(List<?> containers) {
        containers.stream().filter(o -> o instanceof ImageContainer)//
                .forEach(c -> ((ImageContainer) c).doFilter());
    }
}
