package de.htwb.drawr.web;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import de.htwb.drawr.util.DrawingUtil;
import de.htwb.drawr.view.DrawingDialog;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by l.juenemann on 28.11.2016.
 */

public class DrawrChromeClient extends WebChromeClient {

    WebView webView;
    Context context;
    SharedPreferences sharedPreferences;

    public DrawrChromeClient(Context context, WebView webView) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(DrawingDialog.DRAWING_SHARED_PREF_KEY, Context.MODE_PRIVATE);
        webView.setWebChromeClient(this);
        this.webView = webView;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new DrawrJavaScriptInterface(), "Android");

        final String INDEX_HTML =
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "  <head>\n" +
                        "    <title>Playing with canvas drawing</title>\n" +
                        "    <link rel=\"stylesheet\" href=\"style.css\" />\n" +
                        "  </head>\n" +
                        "  <body>\n" +
                        "    <div id=\"canvasDiv\"></div>\n" +
                        "    <script type=\"text/javascript\" src=file://"+context.getFilesDir().getAbsolutePath()+CoreLibDownloader.CORE_LIB_FILE_NAME+"></script>\n" +
                        "    <script type=\"text/javascript\" src=\"main.js\"></script>\n" +
                        "  </body>\n" +
                        "</html>\n";

        webView.loadDataWithBaseURL("file:///android_asset/", INDEX_HTML, "text/html", "utf-8", "");
    }

    public void callJavaScript(String function) {
        String s = "javascript:"+function;
        Log.d("DrawrChromeClient", "Called: "+s);
        webView.loadUrl(s);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Log.d("DrawrChromeClient", consoleMessage.message());
        return super.onConsoleMessage(consoleMessage);
    }

    //JS Interface

    private final class DrawrJavaScriptInterface {

        @JavascriptInterface
        public String getOptions() {
            int intColor = sharedPreferences.getInt(DrawingDialog.SHARED_PREF_KEY_COLOR,
                    DrawingDialog.SHARED_PREF_DEFAULT_COLOR);
            String strColor = DrawingUtil.colorToHex(intColor);
            String stroke = sharedPreferences.getString(DrawingDialog.SHARED_PREF_KEY_STROKE,
                    DrawingDialog.SHARED_PREF_DEFAULT_STROKE);

            JSONObject object = new JSONObject();
            try {
                object.put("color", strColor);
                object.put("strokeWidth", stroke);
            } catch (JSONException e) {
                Log.e("DrawrChromeClient", "JSON Exception in getOptions()", e);
                e.printStackTrace();
            }
            Log.d("JavascriptInterface",object.toString());
            return object.toString();
        }
    }


}
