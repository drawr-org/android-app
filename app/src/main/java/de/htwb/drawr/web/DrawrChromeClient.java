package de.htwb.drawr.web;

import android.content.Context;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by l.juenemann on 28.11.2016.
 */

public class DrawrChromeClient extends WebChromeClient {

    WebView webView;
    String currentColor;
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

    public void setColor(int c) {
        currentColor = String.format("#%06X", (0xFFFFFF & c));
        callJavaScript("updateOptions()");
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Log.d("DrawrChromeClient", consoleMessage.message());
        return super.onConsoleMessage(consoleMessage);
    }

    //JS Interface

    private final class DrawrJavaScriptInterface {
        DrawrJavaScriptInterface() {}

        @JavascriptInterface
        public String getOptions() {
            Toast.makeText(context, "getOptions()", Toast.LENGTH_SHORT).show();
            JSONObject object = new JSONObject();
            try {
                object.put("color", currentColor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("JavascriptInterface",object.toString());
            return object.toString();
        }
    }


}
