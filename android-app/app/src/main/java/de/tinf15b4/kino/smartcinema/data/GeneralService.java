package de.tinf15b4.kino.smartcinema.data;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeneralService {
    @GET("getCinemas")
    Call<List<Cinema>> getCinemas();

    @GET("getMovies")
    Call<List<Movie>> getMovies();

    @GET("getSearchResult")
    Call<SearchResult> getSearchResult(@Query("term") String term);

    @GET("getPlaylistForMovie")
    Call<List<PlaylistEntry>> getPlaylistForMovie(@Query("movieId") long movieId,
                                                  @Query("from") long fromMillis, @Query("to") long toMillis);
}
