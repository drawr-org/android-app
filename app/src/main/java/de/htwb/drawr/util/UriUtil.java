package de.htwb.drawr.util;

import android.net.Uri;

/**
 * Created by laokoon on 25.03.17.
 */
public final class UriUtil {

    public static final String DRAWR_SCHEME = "drawr";

    private UriUtil() {}

    public static final Uri createUri(String host, String port, String sessionId) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(DRAWR_SCHEME)
                .encodedAuthority(host+":"+port)
                .appendPath(sessionId);
        return builder.build();
    }

    public static final String getHostFromUri(Uri uri) {


        return uri.getEncodedAuthority().split(":")[0];
    }

    public static String getPortFromUri(Uri uri) {
        if(uri == null || !uri.getScheme().equals(DRAWR_SCHEME)) {
            return null;
        }

        String[] parts = uri.getEncodedAuthority().split(":");

        if(parts.length > 1) {
            return parts[1];
        } else {
            return null;
        }
    }

    public static String getSessionIdFromUri(Uri uri) {
        if(uri == null || !uri.getScheme().equals(DRAWR_SCHEME)) {
            return null;
        }

        return uri.getPath().substring(1); //Avoid first "/" in Path
    }
}
