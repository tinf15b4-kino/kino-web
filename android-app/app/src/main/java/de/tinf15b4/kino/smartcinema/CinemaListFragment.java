package de.tinf15b4.kino.smartcinema;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.tinf15b4.kino.smartcinema.data.ApiFactory;
import de.tinf15b4.kino.smartcinema.data.Cinema;
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

        refresh();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Kinos");
    }

    private void refresh() {
        swipeLayout.setRefreshing(true);
        ApiFactory.getGeneralService().getCinemas().enqueue(new Callback<List<Cinema>>() {
            @Override
            public void onResponse(Call<List<Cinema>> call, Response<List<Cinema>> response) {
                swipeLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    final ArrayList<String> list = new ArrayList<String>();
                    for (Cinema c : response.body()) {
                        list.add(c.name);
                    }

                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            R.layout.cinema_list_entry, list);

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
