package de.tinf15b4.kino.smartcinema;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.tinf15b4.kino.smartcinema.data.ApiFactory;
import de.tinf15b4.kino.smartcinema.data.GeneralService;
import de.tinf15b4.kino.smartcinema.data.Movie;
import de.tinf15b4.kino.smartcinema.data.PlaylistEntry;
import de.tinf15b4.kino.smartcinema.util.ScrollableAppBar;
import de.tinf15b4.kino.smartcinema.util.SeparatedListAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ScrollableAppBar appBar = (ScrollableAppBar) findViewById(R.id.app_bar);
        appBar.collapseToolbar();

        Intent intent = getIntent();
        if (savedInstanceState != null && savedInstanceState.containsKey("movie"))
            displayMovie((Movie) savedInstanceState.getSerializable("movie"));
        if (intent.hasExtra("movie"))
            displayMovie((Movie)intent.getSerializableExtra("movie"));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putSerializable("movie", this.movie);

        super.onSaveInstanceState(outState, outPersistentState);
    }

    private Adapter buildSimpleAdapter(int layoutId, Map<Integer,?> data)
    {
        List<Map<String, ?>> a = new ArrayList<>();
        Map<String, Object> sadata = new HashMap<>();
        a.add(sadata);
        String[] from = new String[data.size()];
        int[] to = new int[data.size()];
        int i = 0;
        for (Integer key : data.keySet()) {
            sadata.put(key.toString(), data.get(key));
            from[i] = key.toString();
            to[i] = key;
            i++;
        }

        return new SimpleAdapter(this, a, layoutId, from, to);
    }

    private Map<String,?> createTwoTextItem(String text1, String text2)
    {
        Map<String,String> m = new HashMap<>();
        m.put("text1", text1);
        m.put("text2", text2);
        return m;
    }

    private Adapter buildTwoTextListAdapter(List<Map<String,?>> items)
    {
        return new SimpleAdapter(this, items, android.R.layout.simple_list_item_2,
                new String[] { "text1", "text2" }, new int[] { android.R.id.text1, android.R.id.text2 });
    }

    private void displayMovie(Movie m) {
        this.movie = m;

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(m.name);

        SeparatedListAdapter a = new SeparatedListAdapter(this);

        Map<Integer,String> infoMap = new HashMap<>();
        infoMap.put(R.id.movie_length, m.lengthMinutes + " Minuten");
        infoMap.put(R.id.movie_genre, m.genre);
        infoMap.put(R.id.movie_director, m.director);
        infoMap.put(R.id.movie_author, m.author);
        infoMap.put(R.id.movie_studio, m.studio);

        a.addSection("Informationen", buildSimpleAdapter(R.layout.content_movie_details_infos, infoMap));

        Map<Integer,String> descrMap = new HashMap<>();
        descrMap.put(R.id.movie_details_description, m.description);

        a.addSection("Beschreibung", buildSimpleAdapter(R.layout.content_movie_details_description, descrMap));

        a.addSection("Spielplan", new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1));

        ApiFactory.getGeneralService().getPlaylistForMovie(m.id, new Date().getTime(), new Date(new Date().getTime() + 7*24*3600*1000).getTime())
                .enqueue(new Callback<List<PlaylistEntry>>() {
                    @Override
                    public void onResponse(Call<List<PlaylistEntry>> call, Response<List<PlaylistEntry>> response) {
                        if (response.isSuccessful()) {
                            List<Map<String,?>> items = new ArrayList<Map<String,?>>();

                            for (PlaylistEntry entry : response.body()) {
                                SimpleDateFormat format = new SimpleDateFormat("E, dd.MM. kk:mm", Locale.GERMANY);
                                items.add(createTwoTextItem(format.format(new Date(entry.time)), entry.cinema.name));
                            }

                            a.replaceSection("Spielplan", buildTwoTextListAdapter(items));
                        } else {
                            Toast.makeText(MovieDetailsActivity.this, response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PlaylistEntry>> call, Throwable t) {
                        Toast.makeText(MovieDetailsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        ListView v = (ListView) findViewById(R.id.movie_details_listview);
        ViewCompat.setNestedScrollingEnabled(v, true);
        v.setAdapter(a);

        ImageView cover = (ImageView) findViewById(R.id.image);
        Picasso.with(this).load(ApiFactory.getPictureUrl(movie)).into(cover);
    }
}
