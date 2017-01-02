package de.htwb.drawr.web;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by laokoon on 12/18/16.
 */
public class CoreLibDownloader {

    public static final String CORE_LIB_FILE_NAME = "drawr-core.min.js";
    private static final String CORE_LIB_URL = "https://jenkins.etsag.de/userContent/artifacts/drawr-core-lib/"+CORE_LIB_FILE_NAME;

    private DownloadTask task;


    public CoreLibDownloader(Context context, DownLoadProgressListener listener) {
        task = new DownloadTask(context, listener);
    }

    public void start() {
        task.execute();
    }

    public interface DownLoadProgressListener {
        void progressChanged(int newProgress);
        void downloadDone(boolean result);
    }


    private static class DownloadTask extends AsyncTask<Void, Integer, Boolean> {

        private Context mContext;
        private DownLoadProgressListener listener;
        private static final int BUFFER_SIZE = (int)Math.round(Math.pow(2, 20));

        private DownloadTask(Context context, DownLoadProgressListener listener) {
            mContext = context;
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            File dstFile = new File(mContext.getFilesDir().getPath()+CORE_LIB_FILE_NAME);

            if(dstFile.exists()) {
                boolean result = dstFile.delete();
                Log.d("DownloadTask", "Old Lib deleted: "+result);
            }

            InputStream input = null;
            OutputStream output = null;

            try {
                URL url = new URL(CORE_LIB_URL);
                URLConnection connection = url.openConnection();
                connection.connect();

                int fileLength = connection.getContentLength();
                Log.d("DownloadTask", "CoreLib size: "+fileLength);

                input = connection.getInputStream();
                output = new FileOutputStream(dstFile);

                byte data[] = new byte[BUFFER_SIZE];
                long total = 0L;
                int count;
                while ((count = input.read(data))!= -1) {
                    Log.d("DownloadTask", String.format("downloaded %d byte", count));
                    total += count;
                    output.write(data, 0, count);
                    if(fileLength > 0) {
                        publishProgress(Math.round(total*100 / fileLength));
                    }
                }
                output.flush();
            } catch (MalformedURLException e) {
                Log.e("DownloadTask","Malformed URL!", e);
                return false;
            } catch (IOException e) {
                Log.e("DownloadTask","Error while downloading file!", e);
                return false;
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException e) {
                    Log.e("DownloadTask","Error closing streams!", e);
                }
            }

            Log.d("DownloadTask","Done!");
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            listener.downloadDone(result);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            listener.progressChanged(values[0]);
        }
    }
}
