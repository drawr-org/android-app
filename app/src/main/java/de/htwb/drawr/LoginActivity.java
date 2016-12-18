package de.htwb.drawr;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import de.htwb.drawr.view.CustomEditText;
import de.htwb.drawr.web.CoreLibDownloader;

/**
 * Created by Lao on 03.11.2016.
 */

public class LoginActivity extends AppCompatActivity {

    public static final int QR_CAMERA_REQUEST_CODE = 1;
    public static final String KEY_QR_VALUE = "value";

    CustomEditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText = (CustomEditText)findViewById(R.id.sessionIdED);

        Button newSession = (Button)findViewById(R.id.login_new_session);
        newSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle(R.string.new_session_title);
                builder.setMessage(R.string.new_session_question);
                builder.setPositiveButton(R.string.online, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(LoginActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(R.string.offline, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        final Button cameraButton = (Button)findViewById(R.id.login_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, QRScanActivity.class);
                startActivityForResult(i, QR_CAMERA_REQUEST_CODE);
            }
        });

        downloadCoreLib();
    }

    private void downloadCoreLib() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(R.string.updating_lib);
        dialog.setMax(100);
        CoreLibDownloader downloader = new CoreLibDownloader(this, new CoreLibDownloader.DownLoadProgressListener() {
            @Override
            public void progressChanged(int newProgress) {
                Log.d("LoginFragment","Progress: "+newProgress);
                dialog.setProgress(newProgress);
            }

            @Override
            public void downloadDone(boolean result) {
                dialog.dismiss();
                Toast toast;
                if(result) {
                    toast = Toast.makeText(LoginActivity.this, R.string.download_successful, Toast.LENGTH_SHORT);
                } else {
                    toast = Toast.makeText(LoginActivity.this, R.string.download_failed, Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        });
        dialog.show();
        downloader.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("LoginActivity","Received result for request: "+requestCode+" result: "+resultCode);
        if(requestCode == QR_CAMERA_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                String value = data.getExtras().getString(KEY_QR_VALUE);
                editText.setText(value);
            }
        }
    }
}
