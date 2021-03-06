package de.tinf15b4.kino.web.components;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.movies.MovieFilterData;
import de.tinf15b4.kino.web.views.MovieListView;

public class DateTimeFilter extends VerticalLayout {
    private static final long serialVersionUID = -6267956035416269481L;

    public DateTimeFilter(MovieListView view, MovieFilterData filterData) {
        setSpacing(true);
        addComponent(new Label("Spielzeit"));
        DateField from = new DateField("zwischen");
        from.setId("lower-date");
        DateField to = new DateField("und");
        to.setId("upper-date");
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
