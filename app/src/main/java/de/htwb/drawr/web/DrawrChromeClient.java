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
import de.htwb.drawr.util.PenSettings;
import de.htwb.drawr.view.DrawingDialog;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by l.juenemann on 28.11.2016.
 */

public class DrawrChromeClient extends WebChromeClient {

    WebView webView;
    Context context;

    public DrawrChromeClient(Context context, WebView webView) {
        this.context = context;
        webView.setWebChromeClient(this);
        this.webView = webView;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new DrawrJavaScriptInterface(), "Android");

        webView.loadUrl("file:///android_asset/index.html");
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
            JSONObject object = PenSettings.getInstance().getJSONObject();
            Log.d("JavascriptInterface",object.toString());
            return object.toString();
        }
    }


}
