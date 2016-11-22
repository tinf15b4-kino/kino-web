package de.tinf15b4.kino.web.util;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField;

@SuppressWarnings("serial")
// FIXME There might be a better approach. This installs a global KeyListener if
// the textfield is focused and uninstalls it on blur
public abstract class EnterKeyListener {

    private final ShortcutListener enterShortCut = new ShortcutListener("EnterOnTextAreaShorcut",
            ShortcutAction.KeyCode.ENTER,
            null) {
        @Override
        public void handleAction(Object sender, Object target) {
            onEnterKeyPressed();
        }
    };

    public void installOn(final AbstractTextField component) {
        component.addFocusListener(new FieldEvents.FocusListener() {

            @Override
            public void focus(FieldEvents.FocusEvent event) {
                component.addShortcutListener(enterShortCut);
            }

        });

        component.addBlurListener(new FieldEvents.BlurListener() {

            @Override
            public void blur(FieldEvents.BlurEvent event) {
                component.removeShortcutListener(enterShortCut);
            }

        });
    }

    public abstract void onEnterKeyPressed();

}
