package de.tinf15b4.kino.smartcinema.data;

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
}
