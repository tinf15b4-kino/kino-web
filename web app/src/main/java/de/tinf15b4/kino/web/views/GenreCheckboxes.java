package de.tinf15b4.kino.web.views;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.movies.Genre;
import de.tinf15b4.kino.data.movies.MovieFilterData;

public class GenreCheckboxes extends VerticalLayout {

    private Set<Genre> selected;

    public GenreCheckboxes(MovieListView view, MovieFilterData filterData) {
        selected = new HashSet<>();
        addComponent(new Label("Altersfreigabe"));
        for (Genre genre : Genre.values()) {
            CheckBox box = new CheckBox(genre.toString(), false);
            addComponent(box);
            box.addValueChangeListener(e -> {
                if (box.getValue())
                    selected.add(genre);
                else
                    selected.remove(genre);
                filterData.setGenre(selected);
                view.filterChanged();
            });
        }
    }

}
