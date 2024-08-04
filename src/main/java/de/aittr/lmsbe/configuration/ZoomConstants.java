package de.aittr.lmsbe.configuration;

public class ZoomConstants {

    private ZoomConstants() {
        throw new IllegalArgumentException("This is constants class");

    }

    public static final String ZOOM_API_URL = "https://api.zoom.us/v2/users/%s/meetings";
}
