package de.tinf15b4.kino.web.views;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.movies.MovieFilterData;

public class DateTimeFilter extends VerticalLayout {

    public DateTimeFilter(MovieListView view, MovieFilterData filterData) {
        setSpacing(true);
        addComponent(new Label("Spielzeit"));
        DateField from = new DateField("zwischen");
        DateField to = new DateField("und");
        addComponent(from);
        addComponent(to);

        from.setResolution(Resolution.MINUTE);
        to.setResolution(Resolution.MINUTE);

        from.addValueChangeListener(e -> {
            filterData.setLowerTime(from.getValue());
            view.filterChanged();
        });
        to.addValueChangeListener(e -> {
            filterData.setUpperTime(to.getValue());
            view.filterChanged();
        });
    }

}
