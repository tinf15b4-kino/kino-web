package de.tinf15b4.kino.web.util;

import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;

public class ShortcutUtils {

    private ShortcutUtils() {
        // Just used to hide the public constructor
        // Sonarqube seems to like that
    }

    @SuppressWarnings("serial")
    public static void registerScopedShortcut(Panel scope, Button button, int key) {
        scope.addAction(new ShortcutListener(null, key, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                button.click();
            }
        });
    }
}
