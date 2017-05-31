package de.tinf15b4.kino.smartcinema;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.MovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.tinf15b4.kino.smartcinema.data.ApiFactory;
import de.tinf15b4.kino.smartcinema.data.Movie;
import de.tinf15b4.kino.smartcinema.util.ScrollableAppBar;

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

    private void displayMovie(Movie m) {
        this.movie = m;

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(m.name);

        TextView descrTextView = (TextView) findViewById(R.id.movie_details_description);
        descrTextView.setText(m.description);

        TextView lengthView = (TextView) findViewById(R.id.movie_length);
        lengthView.setText(m.lengthMinutes + " Minuten");

        TextView genreText = (TextView) findViewById(R.id.movie_genre);
        genreText.setText(m.genre);

        TextView directorText = (TextView) findViewById(R.id.movie_director);
        directorText.setText(m.director);

        TextView authorText = (TextView) findViewById(R.id.movie_author);
        authorText.setText(m.author);

        TextView studioText = (TextView) findViewById(R.id.movie_studio);
        studioText.setText(m.studio);

        ImageView cover = (ImageView) findViewById(R.id.image);
        Picasso.with(this).load(ApiFactory.getPictureUrl(movie)).into(cover);
    }
}
