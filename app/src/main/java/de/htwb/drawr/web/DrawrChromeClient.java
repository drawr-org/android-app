package de.htwb.drawr.web;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import de.htwb.drawr.MainActivity;
import de.htwb.drawr.R;
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

        @JavascriptInterface
        public void joinSessionCallback(String success) {
            boolean suc = Boolean.parseBoolean(success);
            if(suc) {
                Toast.makeText(context, R.string.joining_session_success, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, R.string.joining_session_failed, Toast.LENGTH_LONG).show();
                ((MainActivity)context).finish();
            }
        }

        @JavascriptInterface
        public void newSessionCallback(String success, String sessionId) {
            boolean suc = Boolean.parseBoolean(success);
            if(suc) {
                Toast.makeText(context, R.string.creating_session_success, Toast.LENGTH_LONG).show();
                ((MainActivity)context).updateSessionId(sessionId);
            } else {
                Toast.makeText(context, R.string.creating_session_failed, Toast.LENGTH_LONG).show();
                ((MainActivity)context).finish();
            }

        }

        @JavascriptInterface
        public void serverShutdown() {
            Toast.makeText(context, R.string.server_shutdown, Toast.LENGTH_LONG).show();
            ((MainActivity)context).finish();
        }
    }


}
