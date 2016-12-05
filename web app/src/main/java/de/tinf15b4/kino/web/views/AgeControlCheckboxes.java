package de.tinf15b4.kino.web.views;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.movies.AgeControl;

public class AgeControlCheckboxes extends VerticalLayout {

    Set<AgeControl> selected;

    public AgeControlCheckboxes(MovieListView view) {
        selected = new HashSet<>();
        this.addComponent(new Label("Altersfreigabe"));
        for (AgeControl age : AgeControl.values()) {
            CheckBox box = new CheckBox(age.getCaption(), true);
            this.addComponent(box);
            box.addValueChangeListener(e -> {
                if (box.getValue())
                    selected.add(age);
                else
                    selected.remove(age);
                view.filterChanged();
            });
        }
    }

    public Set<AgeControl> getSelectedAges() {
        return Collections.unmodifiableSet(selected);
    }
}
