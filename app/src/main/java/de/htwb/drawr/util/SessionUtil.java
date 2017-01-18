package de.htwb.drawr.util;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Created by laokoon on 12/14/16.
 */
public final class SessionUtil {

    public static final Pattern ULID_PATTERN = Pattern.compile("[A-Z0-9]*");
    public static final int ULID_LENGTH = 26;

    private static final String VALIDATION_ENDPOINT = "session";

    private SessionUtil(){}

    /**
     * Validates a String if it matches the Session Id Pattern
     * @param ulid the string to validate
     * @return <code>true</code> when ulid matches Session Id pattern, otherwise returns <code>false</code>
     */
    public static final boolean validateUlid(String ulid) {
        if(ulid != null && ulid.length() == ULID_LENGTH) {
            return ULID_PATTERN.matcher(ulid).matches();
        }
        return false;
    }

    public static final void validateSessionAtHost(String sessionId, String host, String port, final AsyncWaiterListener<Integer> listener) {
        Log.d("SessionUtil", "Validating Session at host...");
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http")
                .encodedAuthority(host+":"+port)
                .appendPath(VALIDATION_ENDPOINT)
                .appendPath(sessionId);
        String endpoint = uriBuilder.build().toString();


        HTTPExecutionTask task = new HTTPExecutionTask() {
            @Override
            protected void onPostExecute(Integer integer) {
                listener.resultDelivered(integer);
            }
        };
        task.execute(endpoint);
    }

    private static abstract class HTTPExecutionTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            if(params.length > 0) {
                try {
                    String endpoint = params[0];
                    Log.d("SessionUtil","Validation At Host endpoint: "+endpoint);
                    URL url = new URL(endpoint);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    Log.d("SessionUtil", "Validate Session ResponseCode: "+responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        return responseCode;
                    }

                } catch (MalformedURLException e) {
                    Log.e("SessionUtil", "Malformed Url! ", e);
                    return HttpURLConnection.HTTP_NOT_FOUND;
                } catch (IOException e) {
                    Log.e("SessionUtil", "IO Exception! ", e);
                    return HttpURLConnection.HTTP_NOT_FOUND;
                }
            }
            return HttpURLConnection.HTTP_NOT_FOUND;
        }
    }

    public interface AsyncWaiterListener<T> {
        void resultDelivered(T result);
    }

}
