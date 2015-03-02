package com.anthonyburdi.computervisiondemo;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.parse.Parse;
import com.parse.ParseObject;


public class MainActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    //    ------------------------------ LOCATION ------------------------------
    // FROM https://developer.android.com/training/location/retrieve-current.html
    // Create objects for use later
    private GoogleApiClient mGoogleApiClient;
    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }
    protected Location mLastLocation;
    String mLatitude;
    String mLongitude;
    // Create a tag for the log:
    protected static final String LocTAG = "TAG - Location: ";
    //    ------------------------------ LOCATION ------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //    ------------------------------ PARSE DATA STORE ------------------------------
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        // Set keys
        Parse.initialize(this, "yrdc3T0K43BDjOO2Eysq6HcbDmx92VaZlLO9O3bn",
                "jnFQK4OAfVTasij0jrG5ejSKbHOqlSQNNwiF6iML");
        //    ------------------------------ PARSE DATA STORE ------------------------------

        //    ------------------------------ CAMERA2 ------------------------------
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
        //    ------------------------------ CAMERA2 ------------------------------

        //    ------------------------------ LOCATION ------------------------------
        buildGoogleApiClient();
        //    ------------------------------ LOCATION ------------------------------

    }

    //    ------------------------------ LOCATION ------------------------------
    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = String.valueOf(mLastLocation.getLatitude());
            mLongitude = String.valueOf(mLastLocation.getLongitude());
            Log.d(LocTAG, "This is the latitude: "+mLatitude);
            Log.d(LocTAG, "This is the longitude: "+mLongitude);
            Toast.makeText(this, "This is the lat, long: " + mLatitude + ", " + mLongitude, Toast.LENGTH_LONG).show();
            //    ------------------------------ PARSE DATA STORE ------------------------------
            // Set lat & long objects
            ParseObject mParseSessionObject = new ParseObject("LocationAndPics");
            mParseSessionObject.put("Latitude", mLatitude);
            mParseSessionObject.put("Longitude", mLongitude);
            mParseSessionObject.saveInBackground();
            mParseSessionObject.pinInBackground();
            //    ------------------------------ PARSE DATA STORE ------------------------------

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

}
