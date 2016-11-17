package de.tinf15b4.kino.web.views;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

public enum Views {
	DEFAULT("", "Start", false, null), 
	DATASECURITY("data_security", "Datenschutz", false, null), 
	IMPRESSUM("impressum", "Impressum",	false, null),
	MOVIES("movies", "Filme", true, FontAwesome.PLAY_CIRCLE_O),
	CINEMAS("cinemas", "Kinos", true, FontAwesome.VIDEO_CAMERA),
	FAVOURITES("favourites", "Favoriten", true, FontAwesome.HEART),
	NEWMOVIES("new", "Neu im Kino", true, FontAwesome.EXCLAMATION_CIRCLE),
	COMINGSOON("coming_soon", "Demnächst", true, FontAwesome.HISTORY),
	REGISTER("register", "Registieren",	false, null),
	LOGIN("login", "Anmelden", false, null),
	ACCOUNT("account", "Mein Konto", false, null),
	CONTACT("contact", "Kontakt", false, null);

	private String viewId;
	private boolean inNavigator;
	private String readableName;
	private Resource icon;

	private Views(String viewId, String readableName, boolean inNavigator, Resource icon) {
		this.viewId = viewId;
		this.readableName = readableName;
		this.inNavigator = inNavigator;
		this.icon = icon;
	}

	public String getViewId() {
		return viewId;
	}

	public boolean isInNavigator() {
		return inNavigator;
	}

	public String getReadableName() {
		return readableName;
	}

	public Resource getIcon() {
		return icon;
	}
}
