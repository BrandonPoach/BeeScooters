package com.csit321mf03aproject.beescooters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

//Libraries for qr code scanner functionality
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

//Libraries for spannbale text
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.BackgroundColorSpan;
import android.graphics.Color;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class QRCodeScannerScreen extends AppCompatActivity {

    SurfaceView cameraPreview;
    TextView txtResult;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 4444;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_scanner_screen);

        //Formatting text for display
        //TextView txtRelaunch = findViewById(R.id.txtRelaunch);
        //String relaunchTxt = txtRelaunch.getText().toString();
        //SpannableString ss = new SpannableString(relaunchTxt);
        //ForegroundColorSpan fcsYellow = new ForegroundColorSpan(505050);
        //ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 3, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 36, 41, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 46, 54, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 60, 71, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //txtRelaunch.setText(ss);

        cameraPreview = findViewById(R.id.cameraPreview);
        txtResult = findViewById(R.id.txtResult);

        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(640, 480).build();

        //Add Event
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                //check to see if permission has been granted before
                //permission never been given
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request Permission
                    ActivityCompat.requestPermissions(QRCodeScannerScreen.this,
                            new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {
                    //try to open camera
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCode = detections.getDetectedItems();
                if (qrCode.size() != 0)
                {
                    txtResult.post(new Runnable() {
                        @Override
                        public void run() {
                            //Vibrate Phone when QR detected
                            Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            //displays scooter id from qr code
                            //must retrieve id and send to ride timer page
                            txtResult.setText(qrCode.valueAt(0).displayValue);

                            //code to change activity screens
                            //Intent intent  = new Intent (QRCodeScannerScreen.this, CCInfoScreen.class);
                            //startActivity(intent);
                        }
                    });
                }
            }
        });

    }
}