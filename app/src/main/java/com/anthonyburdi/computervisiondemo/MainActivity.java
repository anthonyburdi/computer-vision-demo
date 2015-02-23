package com.anthonyburdi.computervisiondemo;

import android.hardware.Camera;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    //    ------------------------------ CAMERA ------------------------------
    //    FROM https://developer.android.com/training/camera/photobasics.html
    //    https://developer.android.com/training/camera/cameradirect.html
    //    https://developer.android.com/guide/topics/media/camera.html#custom-camera
    //    https://github.com/josnidhin/Android-Camera-Example
    protected static final String CamTAG = "TAG - Camera Code: ";
//    static final int REQUEST_IMAGE_CAPTURE = 1;
//    public ImageView mImageView;


    //    ------------------------------ LOCATION ------------------------------
    // FROM https://developer.android.com/training/location/retrieve-current.html
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    protected TextView mLatitudeText;
    protected TextView mLongitudeText;

    protected static final String LocTAG = "TAG - Location Info Code: ";
    //    ------------------------------ LOCATION ------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //    ------------------------------ LOCATION ------------------------------
        // FROM https://developer.android.com/training/location/retrieve-current.html
        mLatitudeText = (TextView) findViewById(R.id.latitude_text);
        mLongitudeText = (TextView) findViewById(R.id.longitude_text);
        buildGoogleApiClient();
        //    ------------------------------ LOCATION ------------------------------
        //    ------------------------------ CAMERA ------------------------------
//        mImageView = (ImageView) findViewById(R.id.thumbnail_image_view);
//        dispatchTakePictureIntent();
        //    ------------------------------ CAMERA ------------------------------
    }

    //    ------------------------------ LOCATION ------------------------------
    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(LocTAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(LocTAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    //    ------------------------------ LOCATION ------------------------------


    //    ------------------------------ CAMERA ------------------------------
    // FROM https://developer.android.com/guide/topics/media/camera.html#custom-camera
    /** A safe way to get an instance of the Camera object. */
//    public static Camera getCameraInstance(){
//        Camera c = null;
//        try {
//            c = Camera.open(); // attempt to get a Camera instance
//        }
//        catch (Exception e){
//            // Camera is not available (in use or does not exist)
//        }
//        return c; // returns null if camera is unavailable
//    }

    //    ------------------------------ CAMERA ------------------------------

//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);
//        }
//    }
    //    ------------------------------ CAMERA ------------------------------

    //    ------------------------------ BOILERPLATE ------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //    ------------------------------ BOILERPLATE ------------------------------


    //    ------------------------------------------------------------------------------
    // FROM https://developer.android.com/guide/topics/media/camera.html#custom-camera
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(CamTAG, "Error creating media file, check storage permissions: " /*+
                        e.getMessage()*/);
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(CamTAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(CamTAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    //    ------------------------------------------------------------------------------
    // FROM https://developer.android.com/guide/topics/media/camera.html#custom-camera
    // Add a listener to the Capture button
//    final Button captureButton = (Button) findViewById(R.id.button_capture);

//    captureButton.setOnClickListener(
//            new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            // get an image from the camera
//            mCamera.takePicture(null, null, mPicture);
//        }
//    }
//    );
//    ------------------------------ THIS NEEDS DEBUG ------------------------------
//    View.OnClickListener listener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            // get an image from the camera
//            mCamera.takePicture(null, null, mPicture);
//        }
//    };
//
//    captureButton.setOnClickListener(listener);

    // Does this require OnClickListener to be implemented in the class descriptor? i.e.
    // http://stackoverflow.com/questions/17540013/declaring-that-a-class-implements-onclicklistener-vs-declaring-it-yourself

    //    ------------------------------------------------------------------------------

}
