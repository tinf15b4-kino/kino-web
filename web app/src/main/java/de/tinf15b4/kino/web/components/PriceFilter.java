package de.tinf15b4.kino.web.components;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.tinf15b4.kino.data.movies.MovieFilterData;
import de.tinf15b4.kino.web.views.MovieListView;

public class PriceFilter extends VerticalLayout {
    private static final long serialVersionUID = -8899650706074387697L;

    public PriceFilter(MovieListView view, MovieFilterData filterData) {
        setSpacing(true);
        addComponent(new Label("Preis"));
        TextField from = new TextField("zwischen");
        from.setId("lower-price");
        TextField to = new TextField("und");
        to.setId("upper-price");
        addComponent(from);
        addComponent(to);

        from.addTextChangeListener(e -> {
            String value = e.getText();
            filterData.setLowerPrice(convert(value));
            view.filterChanged();
        });
        to.addTextChangeListener(e -> {
            String value = e.getText();
            filterData.setUpperPrice(convert(value));
            view.filterChanged();
        });
    }

    private Integer convert(String value) {
        try {
            NumberFormat format = NumberFormat.getNumberInstance(Locale.GERMAN);
            Number number = format.parse(value);
            return Math.round(number.floatValue() * 100);
        } catch (ParseException e) {
            //do nothing here
        }
        return 0;
    }

}
