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


public class CinemaListFragment extends Fragment {
    public CinemaListFragment() {
        // Required empty public constructor
    }

    private ListView cinemaList;
    private SwipeRefreshLayout swipeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cinema_list, container, false);

        cinemaList = (ListView)v.findViewById(R.id.cinemaList);
        swipeLayout = (SwipeRefreshLayout)v.findViewById(R.id.cinemaListSwipeRefreshLayout);
        swipeLayout.setOnRefreshListener(() -> refresh());

        cinemaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CinemaDetailsActivity.class);
                intent.putExtra("cinema", (Cinema)cinemaList.getItemAtPosition(position));
                startActivity(intent);
            }
        });

        refresh();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        getActivity().setTitle("Kinos");
    }

    private void refresh() {
        swipeLayout.setRefreshing(true);
        ApiFactory.getGeneralService().getCinemas().enqueue(new Callback<List<Cinema>>() {
            @Override
            public void onResponse(Call<List<Cinema>> call, Response<List<Cinema>> response) {
                swipeLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    ArrayAdapter<Cinema> adapter = new ArrayAdapter<Cinema>(getContext(),
                            android.R.layout.simple_list_item_1);

                    for (Cinema c : response.body()) {
                        adapter.add(c);
                    }

                    cinemaList.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cinema>> call, Throwable t) {
                swipeLayout.setRefreshing(false);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
