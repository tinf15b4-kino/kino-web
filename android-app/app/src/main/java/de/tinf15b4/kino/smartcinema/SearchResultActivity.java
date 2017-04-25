package de.tinf15b4.kino.smartcinema;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.tinf15b4.kino.smartcinema.data.ApiFactory;
import de.tinf15b4.kino.smartcinema.data.Cinema;
import de.tinf15b4.kino.smartcinema.data.Movie;
import de.tinf15b4.kino.smartcinema.data.SearchResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView movieList;
    private ListView cinemaList;
    private TextView movieHeader;
    private TextView cinemaHeader;
    private String lastSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_search_result_layout);
        movieList = (ListView) findViewById(R.id.searchresult_movie_list);
        movieHeader = (TextView) findViewById(R.id.searchresult_movie_header);
        cinemaList = (ListView) findViewById(R.id.searchresult_cinema_list);
        cinemaHeader = (TextView) findViewById(R.id.searchresult_cinema_header);

        clearSearch();

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            performSearch(query);
        }

        swipeRefreshLayout.setOnRefreshListener(() -> performSearch(lastSearch));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            performSearch(query);
        }
    }

    private void clearSearch() {
        lastSearch = null;
        TextView notice = (TextView) findViewById(R.id.activity_search_result_txt);
        notice.setVisibility(View.VISIBLE);
        notice.setText("Kein Suchbegriff eingegeben");
        movieList.setAdapter(null);
        movieHeader.setVisibility(View.GONE);
        cinemaList.setAdapter(null);
        cinemaHeader.setVisibility(View.GONE);
    }

    private void performSearch(String query) {
        if (query == null || query.length() == 0) {
            clearSearch();
            return;
        }

        lastSearch = query;

        TextView notice = (TextView) findViewById(R.id.activity_search_result_txt);

        swipeRefreshLayout.setRefreshing(true);
        ApiFactory.getGeneralService().getSearchResult(query).enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful()) {
                    notice.setVisibility(View.GONE);

                    SearchResult result = response.body();
                    if (result.movies.size() > 0) {
                        final ArrayList<String> list = new ArrayList<String>();
                        for (Movie m : result.movies) {
                            list.add(m.name);
                        }

                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchResultActivity.this,
                                R.layout.movie_list_entry, list);

                        movieList.setAdapter(adapter);
                        movieHeader.setVisibility(View.VISIBLE);
                    } else {
                        movieList.setAdapter(null);
                        movieHeader.setVisibility(View.GONE);
                    }

                    if (result.cinemas.size() > 0) {
                        final ArrayList<String> list = new ArrayList<String>();
                        for (Cinema c : result.cinemas) {
                            list.add(c.name);
                        }

                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchResultActivity.this,
                                R.layout.cinema_list_entry, list);

                        cinemaList.setAdapter(adapter);
                        cinemaHeader.setVisibility(View.VISIBLE);
                    } else {
                        cinemaList.setAdapter(null);
                        cinemaHeader.setVisibility(View.GONE);
                    }

                    if (result.cinemas.size() == 0 && result.movies.size() == 0) {
                        notice.setVisibility(View.VISIBLE);
                        notice.setText("Leider keine Ergebnisse :(");
                    }
                } else {
                    Toast.makeText(SearchResultActivity.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(SearchResultActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_activity_menu, menu);

        // Associate searchable configuration with the SearchView
        MenuItem mi = menu.findItem(R.id.search);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) mi.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        MenuItemCompat.expandActionView(mi);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
