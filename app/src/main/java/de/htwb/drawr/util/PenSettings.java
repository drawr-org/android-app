package de.htwb.drawr.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.balysv.materialmenu.MaterialMenuDrawable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by laokoon on 16.01.17.
 */
public class PenSettings {

    public static enum Tool {
        PEN,
        ERASER
    }

    private static PenSettings instance;

    public static final int DEFAULT_COLOR = 0;
    public static final int DEFAULT_STROKE = 10;
    public static final int MINIMUM_STROKE = 1;
    public static final int MAXIMUM_STROKE = 100;

    private int color;
    private int stroke;
    private Tool penTool;

    private PenSettings() {
        color = DEFAULT_COLOR;
        stroke = DEFAULT_STROKE;
        penTool = Tool.PEN;
    }

    public static PenSettings getInstance() {
        if(instance == null) {
            instance = new PenSettings();
        }
        return instance;
    }

    public void setColor(String hexColor) {
        color = DrawingUtil.hexToColor(hexColor);
    }

    public void setColor(int newColor) {
        color = newColor;
    }

    public void setStroke(int newStroke) {
        stroke = newStroke;
    }

    public int getStroke() {
        return stroke;
    }

    public int getColor() {
        return color;
    }

    public void setTool(Tool tool) {
        if (tool != null) {
            penTool = tool;
        }
    }

    public Tool getTool() {
        return penTool;
    }

    public String getColorHexString() {
        return DrawingUtil.colorToHex(color);
    }

    public JSONObject getJSONObject() {
        JSONObject object = new JSONObject();
        try {
            String strColor = getColorHexString();
            object.put("color", strColor);
            object.put("width", stroke);
            object.put("type", penTool.name().toLowerCase());
        } catch (JSONException e) {
            Log.e("DrawrChromeClient", "JSON Exception in getJSONObject()", e);
        }
        return object;
    }
}
