//
// written by: Daniel Huang
// tested by: Daniel Huang
// debugged by: Daniel Huang

// interacts with the Google Maps Api to display a functional map on screen
// as well as adding Active period locations, and GPS polling for coordinates
// Class Diagram: Map Maker

package se2017.lex;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;





public class MapsTab extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapLongClickListener, OnMapClickListener {




    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    Marker mAPLocationMarker;
    private static long pollingRate = 6000;
    public String userid = "jariy";
    public DatabaseReference fDatabase;
    public DatabaseReference currDatabase;
    public DatabaseReference APDatabase;
    public static LocationObject[] APLocArray = new LocationObject[10];
    NotificationCompat.Builder notif;
    private static final int uniqueID = 481517;




    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(pollingRate);
        mLocationRequest.setFastestInterval(pollingRate);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    //All of the code used to get the Google Maps screen to display
    //When the Map is created, it will create a new location request and a new notification item
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createLocationRequest();


        setContentView(R.layout.activity_maps_tab);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        notif = new NotificationCompat.Builder(this);
        notif.setAutoCancel(true);

    }



    //When the map is created, the app will create a map, make sure Google Play services are running,
    //initialize the onclick listeners, and grab new data from the database, and place the AP markers gathered from the database.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);


        //Database code

        fDatabase = FirebaseDatabase.getInstance().getReference(userid+"/ActivePeriodLocations");
        currDatabase = FirebaseDatabase.getInstance().getReference(userid+"/CurrLocations");
        APDatabase = FirebaseDatabase.getInstance().getReference(userid+"/TimeSpentAtAP");
        //Creates an array of the APLocations
        fDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int i = 0;

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (i < (APLocArray.length)) {
                        APLocArray[i] = child.getValue(LocationObject.class);
                        i++;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Failed to read value.", error.toException());
            }
        });

        int counter = 0;

        //Traverses APLocation array in order to place new ones at each APLocation
        while (APLocArray[counter] != null)
        {
            LatLng latLng = new LatLng(APLocArray[counter].lat, APLocArray[counter].longi);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Active Period Location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            mMap.addMarker(markerOptions);
            counter++;
        }


    }



    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        //When Location changes, remove the current marker
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
          mCurrLocationMarker.remove();
        }


        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        long atTime = location.getTime();
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mCurrLocationMarker.setTitle(DateFormat.getTimeInstance().format(new Date(atTime)));


        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //Create child location with key
        String key = currDatabase.push().getKey();
        //Create a new Location Object (java class) to store goal info entered by the user
        LocationObject NewL = new LocationObject(location.getLatitude(), location.getLongitude(), location.getSpeed(), key);
        //Stores the Location into database
        currDatabase.child(key).setValue(NewL);

        if (isNearAP())
        {
            mCurrLocationMarker.setTitle("You are near an AP!");
            String key2 = APDatabase.push().getKey();
            //Create a new Location Object (java class) to store goal info entered by the user
            TimeObject NewT = new TimeObject(location.getTime(), key2);
            //Stores the Location into database
            APDatabase.child(key).setValue(NewT);
            sendNotification(null);
        }




    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapClick(LatLng point) {

    }

    @Override
    public void onMapLongClick(LatLng point) {

        //When Map is long clicked, the app will put a new Marker on the map, send that data to the database to be retrieved later.

        LatLng latLng = point;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Active Period Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mAPLocationMarker = mMap.addMarker(markerOptions);

        Location APLoc = new Location(LocationManager.GPS_PROVIDER);
        APLoc.setLatitude(latLng.latitude);
        APLoc.setLongitude(latLng.longitude);

        //Create child location with key
        String key = fDatabase.push().getKey();
        //Create a new Location Object (java class) to store goal info entered by the user
        LocationObject NewL = new LocationObject(APLoc.getLatitude(), APLoc.getLongitude(), APLoc.getSpeed(), key);
        //Stores the Location into database
        fDatabase.child(key).setValue(NewL);

    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean isNearAP()
    {
        //method for determining near AP Points, gathering the AP's from the database and comparing currnet location.
        boolean near = false;

        int counter = 0;

        while (APLocArray[counter] != null)
        {
            if (Math.abs(mLastLocation.getLatitude() - APLocArray[counter].lat) < 0.00027 && Math.abs(mLastLocation.getLongitude() - APLocArray[counter].longi) < 0.0027)
                near = true;
            counter++;
        }

        return near;
    }

    public void sendNotification(View view){

        //Method to send notification when near AP
        notif.setSmallIcon(R.drawable.app_logo);
        notif.setTicker("Ticker");
        notif.setWhen(System.currentTimeMillis());
        notif.setContentTitle("LifeExtender+");
        notif.setContentText("You're near an Active Period Location!, Recording Time at AP!");

        Intent intent = new Intent(this,HomeTab.class);
//        startActivity(intent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notif.setContentIntent(pendingIntent);

        NotificationManager nm =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID,notif.build());

    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
}