package de.htwb.drawr.util;

import android.content.Context;
import de.htwb.drawr.R;

/**
 * Created by laokoon on 12/12/16.
 */
public final class DrawingUtil {
    private DrawingUtil() {}

    public static final String colorToHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public static final int hexToColor(String hex) {
        return Integer.parseInt(hex.replace("#",""), 16);
    }

    public static final String getStringForTool(Context context, PenSettings.Tool tool) {
        switch (tool) {
            case PEN:
                return context.getString(R.string.pen);
            case ERASER:
                return context.getString(R.string.eraser);
            default:
                return context.getString(R.string.pen);
        }
    }

}
