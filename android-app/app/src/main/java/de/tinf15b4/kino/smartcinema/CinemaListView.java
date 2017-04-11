package de.tinf15b4.kino.smartcinema;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import de.tinf15b4.kino.smartcinema.data.Cinema;
import de.tinf15b4.kino.smartcinema.data.CinemaListLoader;

public class CinemaListView extends AppCompatActivity {
    private ListView cinemaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_list_view);

        cinemaList = (ListView)findViewById(R.id.cinemaList);

        new ReadListTask().execute();
    }

    class ReadListTask extends AsyncTask<String, Integer, Cinema[]> {
        @Override
        protected Cinema[] doInBackground(String... params) {
            return CinemaListLoader.loadSync();
        }

        @Override
        protected void onPostExecute(Cinema[] result) {
            final ArrayList<String> list = new ArrayList<String>();
            for (Cinema c : result) {
                list.add(c.name);
            }
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(CinemaListView.this,
                    R.layout.cinema_list_entry, list);
            cinemaList.setAdapter(adapter);
        }
    }
}
