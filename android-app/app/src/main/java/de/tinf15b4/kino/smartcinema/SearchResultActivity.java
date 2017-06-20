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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.tinf15b4.kino.smartcinema.data.ApiFactory;
import de.tinf15b4.kino.smartcinema.data.Cinema;
import de.tinf15b4.kino.smartcinema.data.Movie;
import de.tinf15b4.kino.smartcinema.data.SearchResult;
import de.tinf15b4.kino.smartcinema.util.SeparatedListAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView resultsList;
    private String lastSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_search_result_layout);
        resultsList = (ListView) findViewById(R.id.search_result_list);

        clearSearch();

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            performSearch(query);
        }

        swipeRefreshLayout.setOnRefreshListener(() -> performSearch(lastSearch));

        // Setup click handler
        resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = resultsList.getItemAtPosition(position);

                if (item instanceof Movie) {
                    Intent intent = new Intent(SearchResultActivity.this, MovieDetailsActivity.class);
                    intent.putExtra("movie", (Movie) item);
                    startActivity(intent);
                }
                if (item instanceof Cinema) {
                    Intent intent = new Intent(SearchResultActivity.this, CinemaDetailsActivity.class);
                    intent.putExtra("cinema", (Cinema) item);
                    startActivity(intent);
                }
            }
        });
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
        resultsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[] { "Kein Suchbegriff eingegeben!" }));
    }

    private void performSearch(String query) {
        if (query == null || query.length() == 0) {
            clearSearch();
            return;
        }

        lastSearch = query;

        swipeRefreshLayout.setRefreshing(true);
        ApiFactory.getGeneralService().getSearchResult(query).enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful()) {
                    SeparatedListAdapter adapter = new SeparatedListAdapter(SearchResultActivity.this);

                    SearchResult result = response.body();
                    if (result.movies.size() > 0) {
                        final ArrayAdapter<Movie> movieAdapter = new ArrayAdapter<Movie>(SearchResultActivity.this, android.R.layout.simple_list_item_1);
                        for (Movie m : result.movies) {
                            movieAdapter.add(m);
                        }

                        adapter.addSection("Filme", movieAdapter);
                    }

                    if (result.cinemas.size() > 0) {
                        final ArrayAdapter<Cinema> cinemaAdapter = new ArrayAdapter<Cinema>(SearchResultActivity.this, android.R.layout.simple_list_item_1);
                        for (Cinema c : result.cinemas) {
                            cinemaAdapter.add(c);
                        }

                        adapter.addSection("Kinos", cinemaAdapter);
                    }

                    if (adapter.isEmpty()) {
                        resultsList.setAdapter(new ArrayAdapter<String>(SearchResultActivity.this, android.R.layout.simple_list_item_1, new String[] { "Leider kein Ergebnis!" }));
                    } else {
                        resultsList.setAdapter(adapter);
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
