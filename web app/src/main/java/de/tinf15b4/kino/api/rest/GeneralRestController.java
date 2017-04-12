package de.tinf15b4.kino.api.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthNone;
import org.jsondoc.core.annotation.ApiError;
import org.jsondoc.core.annotation.ApiErrors;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.annotation.ApiVersion;
import org.jsondoc.core.pojo.ApiStage;
import org.jsondoc.core.pojo.ApiVisibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieService;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.data.playlists.PlaylistService;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaService;
import de.tinf15b4.kino.data.ratedmovies.RatedMovie;
import de.tinf15b4.kino.data.ratedmovies.RatedMovieService;

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

    @ApiMethod(description = "Returns all cinemas")
    @RequestMapping(value = "rest/getCinemas", method = RequestMethod.GET)
    public ResponseEntity<?> getCinemas() {
        return ResponseEntity.ok(cinemaService.findAll().toArray(new Cinema[0]));
    }

    @ApiMethod(description = "Returns all movies")
    @RequestMapping(value = "rest/getMovies", method = RequestMethod.GET)
    public ResponseEntity<?> getMovies() {
        return ResponseEntity.ok(movieService.findAll().toArray(new Movie[0]));
    }

    @ApiMethod(description = "Returns all playtimes for the cinema with the given id between the given dates")
    @ApiErrors(apierrors = { @ApiError(code = "400", description = "Date was not parseable or the id does not exist") })
    @RequestMapping(value = "rest/getPlaylistForCinema", method = RequestMethod.GET)
    public ResponseEntity<?> getPlaylistForCinema(
            @ApiQueryParam(name = "cinemaId", description = "Id of the cinema") @RequestParam(name = "cinemaId") long cinemaId,
            @ApiQueryParam(name = "from", description = "Start date in format yyyy-MM-dd") @RequestParam(name = "from") String from,
            @ApiQueryParam(name = "to", description = "End date in format yyyy-MM-dd") @RequestParam(name = "to") String to)
            throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Cinema cinema = cinemaService.findOne(cinemaId);
            if (cinema != null)
                return ResponseEntity
                        .ok(playlistService.findForCinema(cinema, dateFormatter.parse(from), dateFormatter.parse(to))
                                .toArray(new Playlist[0]));
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.badRequest().body(RestControllerConstants.INVALID_ID);
    }

    @ApiMethod(description = "Returns all playtimes for the movie with the given id between the given dates")
    @ApiErrors(apierrors = { @ApiError(code = "400", description = "Date was not parseable or the id does not exist") })
    @RequestMapping(value = "rest/getPlaylistForMovie", method = RequestMethod.GET)
    public ResponseEntity<?> getPlaylistForMovie(
            @ApiQueryParam(name = "movieId", description = "Id of the movie") @RequestParam(name = "movieId") long movieId,
            @ApiQueryParam(name = "from", description = "Start date in format yyyy-MM-dd") @RequestParam(name = "from") String from,
            @ApiQueryParam(name = "to", description = "End date in format yyyy-MM-dd") @RequestParam(name = "to") String to)
            throws ParseException {
        try {
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Movie movie = movieService.findOne(movieId);
            if (movie != null)
                return ResponseEntity
                        .ok(playlistService.findForMovie(movie, dateFormatter.parse(from), dateFormatter.parse(to))
                                .toArray(new Playlist[0]));
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.badRequest().body(RestControllerConstants.INVALID_ID);
    }

    @ApiMethod(description = "Returns all ratings for the movie with the given id ")
    @ApiErrors(apierrors = { @ApiError(code = "400", description = "Id does not exist") })
    @RequestMapping(value = "rest/getRatedMovies", method = RequestMethod.GET)
    public ResponseEntity<?> getRatedMovies(
            @ApiQueryParam(name = "movieId", description = "Id of the movie") @RequestParam(name = "movieId") long movieId) {
        Movie movie = movieService.findOne(movieId);
        if (movie != null)
            return ResponseEntity.ok(ratedMovieService.findRatingsByMovie(movie).toArray(new RatedMovie[0]));
        return ResponseEntity.badRequest().body(RestControllerConstants.INVALID_ID);
    }

    @ApiMethod(description = "Returns all ratings for the cinema with the given id")
    @ApiErrors(apierrors = { @ApiError(code = "400", description = "Id does not exist") })
    @RequestMapping(value = "rest/getRatedCinemas", method = RequestMethod.GET)
    public ResponseEntity<?> getRatedCinemas(
            @ApiQueryParam(name = "cinemaId", description = "Id of the cinema") @RequestParam(name = "cinemaId") long cinemaId) {
        Cinema cinema = cinemaService.findOne(cinemaId);
        if (cinema != null)
            return ResponseEntity.ok(
                    ratedCinemaService.findRatingsByCinema(cinemaService.findOne(cinemaId)).toArray(new RatedMovie[0]));
        return ResponseEntity.badRequest().body(RestControllerConstants.INVALID_ID);
    }
}
