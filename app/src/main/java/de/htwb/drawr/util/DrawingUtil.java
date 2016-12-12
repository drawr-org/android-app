package de.htwb.drawr.util;

/**
 * Created by laokoon on 12/12/16.
 */
public final class DrawingUtil {
    private DrawingUtil() {}

    public static final String colorToHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

}
