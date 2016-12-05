package de.tinf15b4.kino.web.views;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.movies.AgeControl;
import de.tinf15b4.kino.data.movies.MovieFilterData;

public class AgeControlCheckboxes extends VerticalLayout {

    private Set<AgeControl> selected;

    public AgeControlCheckboxes(MovieListView view, MovieFilterData filterData) {
        selected = new HashSet<>();
        addComponent(new Label("Altersfreigabe"));
        for (AgeControl age : AgeControl.values()) {
            CheckBox box = new CheckBox(age.getCaption(), false);
            addComponent(box);
            box.addValueChangeListener(e -> {
                if (box.getValue())
                    selected.add(age);
                else
                    selected.remove(age);
                filterData.setAgeControl(selected);
                view.filterChanged();
            });
        }
    }

    public Set<AgeControl> getSelectedAges() {
        return Collections.unmodifiableSet(selected);
    }
}
