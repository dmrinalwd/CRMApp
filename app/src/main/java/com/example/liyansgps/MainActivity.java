package com.example.liyansgps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLOutput;

public class MainActivity extends AppCompatActivity {
    WebView mWebView;

    public static class GeoWebChromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                       GeolocationPermissions.Callback callback) {
            // Always grant permission since the app itself requires location
            // permission and the user has therefore already granted it
            callback.invoke(origin, true, false);
        }
    }

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    //this for file upload option enable
    private final static int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri[]> mUploadMessage;
    //end
    public TextView textViewLatitude, textViewLongitude;
    public LocationManager locationManager;
    public String strilatitude,strilongitude;
    public class JavaScriptInterface {
        Context mContext;
       // static final int REQUEST_IMAGE_CAPTURE = 1;

        JavaScriptInterface(Context c) {
            mContext = c;
        }
       @JavascriptInterface
        public String getFromAndroid() {
            return strilatitude+","+strilongitude;

        }
//        @JavascriptInterface
//        public void showCamera(String toast) {
//            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
//            dispatchTakePictureIntent();
//        }


        /** Handle Camera result*/
//        private void dispatchTakePictureIntent() {
//            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//        }

//        public String[] getFromAndroid() {
//            String ar[] = new String[]{strilatitude,strilongitude};
//            ar[0]= strilatitude;
//            ar[1] =  strilongitude;
//
//            return ar;
//
//        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewLatitude= findViewById(R.id.latitude);
        textViewLongitude= findViewById(R.id.longitude);
        locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        WebView mWebView = findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//NETWORK_PROVIDER/GPS_PROVIDER
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                textViewLatitude.setText(String.valueOf(location.getLatitude()));
                textViewLongitude.setText(String.valueOf(location.getLongitude()));
                strilatitude=String.valueOf(location.getLatitude());
                strilongitude=String.valueOf(location.getLongitude());

            }
            @Override
            public  void onStatusChanged(String s, int i, Bundle bundle){

            }
            @Override
            public void onProviderEnabled(String s){

            }
            @Override
            public void onProviderDisabled(String s){

            }
        });

        mWebView.getSettings().setBuiltInZoomControls(true);
        //mWebView.setWebViewClient(new GeoWebViewClient());
        mWebView.clearCache(true);
        mWebView.clearHistory();
        // Below required for geolocation
        mWebView.getSettings().setGeolocationEnabled(true);
        //Set application Title
        getSupportActionBar().setTitle("Liyaans Lead Distribution");
        //end set title

        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebChromeClient(new GeoWebChromeClient());
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());

        // Load google.com
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setFocusable(true);
        mWebView.setFocusableInTouchMode(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");

        mWebView.loadUrl("http://103.192.61.198/Android_crm");
        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                mWebView.loadUrl("javascript:init('" + strilatitude + "," + strilongitude + "')");
               // mWebView.loadUrl("javascript:init('" + strilongitude + "')");
            }
            public void onReceivedError(WebView mywebView, int errorCode, String description, String failingUrl) {
                try {
                    mywebView.stopLoading();
                } catch (Exception e) {
                }

                if (mywebView.canGoBack()) {
                    mywebView.goBack();
                }

                mywebView.loadUrl("about:blank");
                AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Server is Offline or No Internet Connection .");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }
                });

                alertDialog.show();
                super.onReceivedError(mywebView, errorCode, description, failingUrl);
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:") || url.startsWith("sms:") || url.startsWith("mailto:"))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // manejo de seleccion de archivo
        if (requestCode == FILECHOOSER_RESULTCODE) {

            if (null == mUploadMessage || intent == null || resultCode != RESULT_OK) {
                return;
            }

            Uri[] result = null;
            String dataString = intent.getDataString();

            if (dataString != null) {
                result = new Uri[]{ Uri.parse(dataString) };
            }

            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }
    private class MyWebChromeClient extends WebChromeClient {

        // maneja la accion de seleccionar archivos
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

            // asegurar que no existan callbacks
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }

            mUploadMessage = filePathCallback;

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*"); // set MIME type to filter

            MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.FILECHOOSER_RESULTCODE );

            return true;
        }
    }
    @Override
    public void onBackPressed() {
        // Pop the browser back stack or exit the activity
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}