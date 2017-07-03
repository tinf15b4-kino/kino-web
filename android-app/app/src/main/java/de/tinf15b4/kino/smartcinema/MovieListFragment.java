package de.tinf15b4.kino.smartcinema;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.tinf15b4.kino.smartcinema.data.ApiFactory;
import de.tinf15b4.kino.smartcinema.data.Cinema;
import de.tinf15b4.kino.smartcinema.data.Movie;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MovieListFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView movieList;

    public MovieListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movie_list, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.movieListSwipeRefreshLayout);
        movieList = (ListView)v.findViewById(R.id.movieList);

        swipeRefreshLayout.setOnRefreshListener(() -> refresh());
        refresh();

        movieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra("movie", (Movie)movieList.getItemAtPosition(position));
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        getActivity().setTitle("Filme");
    }

    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        ApiFactory.getGeneralService().getMovies().enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful()) {

                    final ArrayList<Movie> list = new ArrayList<>();
                    for (Movie m : response.body()) {
                        list.add(m);
                    }

                    final ArrayAdapter<Movie> adapter = new ArrayAdapter<>(getContext(),
                            R.layout.movie_list_entry, list);

                    movieList.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
