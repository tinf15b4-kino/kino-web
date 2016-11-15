package de.tinf15b4.kino.web.views;

public enum Views {
	DEFAULT("", "Start", false), 
	DATASECURITY("data_security", "Datenschutz", false), 
	IMPRESSUM("impressum", "Impressum",	false),
	MOVIES("movies", "Filme", true),
	CINEMAS("cinemas", "Kinos", true),
	FAVOURITES("favourites", "Favoriten", true),
	NEWMOVIES("new", "Neu im Kino", true),
	COMINGSOON("coming_soon", "Demnächst", true),
	REGISTER("register", "Registieren",	false),
	LOGIN("login", "Anmelden", false),
	ACCOUNT("account", "Mein Konto", false),
	CONTACT("contact", "Kontakt", false);

	private String viewId;
	private boolean inNavigator;
	private String readableName;

	private Views(String viewId, String readableName, boolean inNavigator) {
		this.viewId = viewId;
		this.readableName = readableName;
		this.inNavigator = inNavigator;
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
}
