package de.htwb.drawr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture;
import com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic;
import com.google.android.gms.vision.barcode.Barcode;
import de.htwb.drawr.util.SessionUtil;
import de.htwb.drawr.util.UriUtil;
import xyz.belvi.mobilevisionbarcodescanner.BarcodeRetriever;

import java.util.List;

/**
 * Created by laokoon on 12/12/16.
 */
public class QRScanActivity extends AppCompatActivity implements BarcodeRetriever {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        BarcodeCapture capture = (BarcodeCapture) getSupportFragmentManager().findFragmentById(R.id.barcode);
        capture.setRetrieval(this);
        capture.setShowDrawRect(true);
        capture.setRectColors(new Integer[]{R.color.colorPrimary});
    }

    @Override
    public void onRetrieved(final Barcode barcode) {
        Log.d("QRScanActivity","Uri: "+barcode.displayValue);
        Uri uri = Uri.parse(barcode.displayValue);
        String ulid = UriUtil.getSessionIdFromUri(uri);
        if(SessionUtil.validateUlid(ulid)) {
            Log.d("QRScanActivity", "valid: "+ulid);
            String host = UriUtil.getHostFromUri(uri);
            String port = UriUtil.getPortFromUri(uri);
            if(host == null || port == null) {

            } else {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(DrawrPreferenceActivity.KEY_HOST_URL, host);
                editor.putString(DrawrPreferenceActivity.KEY_HOST_PORT, port);
                editor.commit();
            }
            Intent i = new Intent();
            i.putExtra(LoginActivity.KEY_QR_VALUE, ulid);
            setResult(RESULT_OK, i);
            finish();
        } else {
            Log.d("QRScanActivity", "invalid: "+ulid);
        }

    }

    @Override
    public void onBackPressed() {
        Log.d("QRScanActivity", "Canceled!");
        setResult(LoginActivity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void onRetrievedMultiple(Barcode barcode, List<BarcodeGraphic> list) {}

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {}

    @Override
    public void onRetrievedFailed(String s) {
        Toast.makeText(this, R.string.try_again, Toast.LENGTH_LONG).show();
    }
}
