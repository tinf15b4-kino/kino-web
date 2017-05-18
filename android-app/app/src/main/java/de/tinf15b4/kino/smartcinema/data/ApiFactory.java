package de.tinf15b4.kino.smartcinema.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {
    public static Retrofit getBuilder() {
        return new Retrofit.Builder()
                //FIXME
                .baseUrl("https://smartcinema-dev.tinf15b4.de/rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static GeneralService getGeneralService() {
        return getBuilder().create(GeneralService.class);
    }
}
