package de.tinf15b4.kino.smartcinema.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {
    private static final String BASE_URL = "https://smartcinema-dev.tinf15b4.de/rest/";

    public static Retrofit getBuilder() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static GeneralService getGeneralService() {
        return getBuilder().create(GeneralService.class);
    }

    public static String getPictureUrl(Movie m) {
        return BASE_URL + "/moviePicture?movieId=" + m.id;
    }

    public static String getPictureUrl(Cinema c) {
        return BASE_URL + "/cinemaPicture?cinemaId=" + c.id;
    }
}
