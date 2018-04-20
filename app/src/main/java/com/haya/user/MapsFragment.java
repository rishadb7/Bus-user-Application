package com.haya.user;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;


/**
 *
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment {

    private View v;
    private MapView mMapView;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    List<LocationPoints> locationPointsList;
    List<LocationPoints2> locationPointsList2;
    List<Bus> busList;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(32.014149, 35.869197);
    private static final int DEFAULT_ZOOM = 17;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    final Handler handler = new Handler();
    private static Timer timer = new Timer();

    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_maps, container, false);
        mMapView = (MapView) v.findViewById(R.id.mapp);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        locationPointsList= new ArrayList<>();
        getLocationPoints();

        locationPointsList2= new ArrayList<>();
        getLocationPoints2();

        busList=new ArrayList<>();
        getBusLocation();

        final TimerTask doAsynchronousTask = new TimerTask()
        {
            @Override
            public void run()
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getBusLocation();

                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask,0,5000);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }



        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getContext(), null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getContext(), null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                LatLng sydney = new LatLng(31.999775, 35.878565);
                mMap.addMarker(new MarkerOptions().position(new LatLng(32.021792,35.844270)).title("AL-Sweileh Bus Station").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_1)));
                mMap.addMarker(new MarkerOptions().position(new LatLng(32.014914,35.868213)).title("The University Of Jordan").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_1)));
                mMap.addMarker(new MarkerOptions().position(new LatLng(31.993451,35.920697)).title("AL-Shamal Bus Station").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_1)));
                mMap.addMarker(new MarkerOptions().position(new LatLng(31.960407,35.958735)).title("compound leg").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_1)));
                mMap.addMarker(new MarkerOptions().position(new LatLng(31.921335,35.958735)).title("Middle East Investment Co").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_1)));
                mMap.addMarker(new MarkerOptions().position(new LatLng(31.911677,35.921403)).title("AL-Sakhrah AL-Musharrafah").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_1)));
                mMap.addMarker(new MarkerOptions().position(new LatLng(31.945850,35.927247)).title("Jordan National Museum").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_1)));

               /* try{*/
                /*    for(int i = 0; i < busList.size(); i++){

                        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(busList.get(i).getxCode()),Double.valueOf(busList.get(i).getyCode()))).title("bus location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.buses)));
                    }
*/
              /*  }catch (Exception e){
                    e.printStackTrace();
                }*/



                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                UiSettings mUiSettings = mMap.getUiSettings();
                mUiSettings.setZoomControlsEnabled(true);


                // Use a custom info window adapter to handle multiple lines of text in the
                // info window contents.
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    // Return null here, so that getInfoContents() is called next.
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        // Inflate the layouts for the info window, title and snippet.
                        View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                                (FrameLayout) v.findViewById(R.id.mapp), false);

                        TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                        title.setText(marker.getTitle());

                        TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                        snippet.setText(marker.getSnippet());

                        return infoWindow;
                    }
                });

                // Prompt the user for permission.
                getLocationPermission();

                // Turn on the My Location layer and the related control on the map.
                updateLocationUI();

                // Get the current location of the device and set the position of the map.
                getDeviceLocation();

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);


            }
        });


        return v;
    }
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                          //  Log.d(TAG, "Current location is null. Using defaults.");
                            //Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }}

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final
            Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                // Set the count, handling cases where less than 5 entries are returned.
                                int count;
                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                    count = likelyPlaces.getCount();
                                } else {
                                    count = M_MAX_ENTRIES;
                                }

                                int i = 0;
                                mLikelyPlaceNames = new String[count];
                                mLikelyPlaceAddresses = new String[count];
                                mLikelyPlaceAttributions = new String[count];
                                mLikelyPlaceLatLngs = new LatLng[count];

                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    // Build a list of likely places to show the user.
                                    mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                    mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                            .getAddress();
                                    mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                            .getAttributions();
                                    mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                    i++;
                                    if (i > (count - 1)) {
                                        break;
                                    }
                                }

                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();

                                // Show a dialog offering the user the list of likely places, and add a
                                // marker at the selected place.
                                openPlacesDialog();

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            // The user has not granted permission.
           Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }



    public void getLocationPoints()
    {
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl("https://sireen1195.000webhostapp.com/")

                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService service = retrofit2.create(ApiService.class);
        Call<List<LocationPoints>> call = service.getLocationPoints("12345");

        call.enqueue(new Callback<List<LocationPoints>>() {

            @Override
            public void onResponse(Call<List<LocationPoints>> call, retrofit2.Response<List<LocationPoints>> response) {


                List<LocationPoints> list = response.body();
                LocationPoints locationPoints = null;

                try {
                    PolylineOptions rectOptions = new PolylineOptions();
                    rectOptions.color(Color.argb(255, 255, 0, 0));
                    LatLng startLatLng = null;
                    LatLng endLatLng = null;



                    for (int i = 0; i < list.size(); i++) {
                        locationPoints = new LocationPoints();


                        String dbId = list.get(i).getDbId();
                        String locationName = list.get(i).getLocationName();
                        String latitude =list.get(i).getLatitude();
                        String logitude =list.get(i).getLongitude();



                          // Toast.makeText(getActivity(),locationName,Toast.LENGTH_LONG).show();

                        Double lat=Double.valueOf(latitude);
                        Double lon=Double.valueOf(logitude);




                        LatLng sydney = new LatLng(lat, lon);

                        if (i == 0) {
                            startLatLng = sydney;
                        }
                        if (i == list.size() - 1) {
                            endLatLng = sydney;
                        }
                        rectOptions.add(sydney);

                   /*     mMap.addMarker(new MarkerOptions().position(sydney).title(locationName));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
*/
                        locationPoints.setDbId(dbId);
                        locationPoints.setLocationName(locationName);
                        locationPoints.setLatitude(latitude);
                        locationPoints.setLongitude(logitude);
                        locationPointsList.add(locationPoints);


                    }

                    mMap.addPolyline(rectOptions);

                } catch (Exception e) {
                    e.printStackTrace();
                    //    Toast.makeText(getApplicationContext(),"No items available",Toast.LENGTH_SHORT).show();

                }
            }




            @Override
            public void onFailure(Call<List<LocationPoints>> call, Throwable t) {

            }
        });
    }

    public void getLocationPoints2()
    {
        Retrofit retrofit3 = new Retrofit.Builder()
                .baseUrl("https://sireen1195.000webhostapp.com/")

                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService service = retrofit3.create(ApiService.class);
        Call<List<LocationPoints2>> call = service.getLocationPoints2("994595");

        call.enqueue(new Callback<List<LocationPoints2>>() {
            @Override
            public void onResponse(Call<List<LocationPoints2>> call, retrofit2.Response<List<LocationPoints2>> response) {

                List<LocationPoints2> list = response.body();
                LocationPoints2 locationPoints2 = null;

                try {
                    PolylineOptions rectOptions = new PolylineOptions();
                    rectOptions.color(Color.argb(255, 0, 255, 0));
                    LatLng startLatLng = null;
                    LatLng endLatLng = null;



                    for (int i = 0; i < list.size(); i++) {
                        locationPoints2 = new LocationPoints2();


                        String dbId = list.get(i).getDbId();
                        String locationName = list.get(i).getLocationName();
                        String latitude =list.get(i).getLatitude();
                        String logitude =list.get(i).getLongitude();



                        //   Toast.makeText(getApplicationContext(),locationName,Toast.LENGTH_LONG).show();

                        Double lat=Double.valueOf(latitude);
                        Double lon=Double.valueOf(logitude);




                        LatLng sydney = new LatLng(lat, lon);

                        if (i == 0) {
                            startLatLng = sydney;
                        }
                        if (i == list.size() - 1) {
                            endLatLng = sydney;
                        }
                        rectOptions.add(sydney);

                     /*   mMap.addMarker(new MarkerOptions().position(sydney).title(locationName));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
*/
                        locationPoints2.setDbId(dbId);
                        locationPoints2.setLocationName(locationName);
                        locationPoints2.setLatitude(latitude);
                        locationPoints2.setLongitude(logitude);
                        locationPointsList2.add(locationPoints2);


                    }

                    mMap.addPolyline(rectOptions);

                } catch (Exception e) {
                    e.printStackTrace();
                    //    Toast.makeText(getApplicationContext(),"No items available",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<LocationPoints2>> call, Throwable t) {

            }
        });
    }



    public void getBusLocation(){

        Retrofit retrofit4 = new Retrofit.Builder()
                .baseUrl("https://hayajitan.000webhostapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService service = retrofit4.create(ApiService.class);
        Call<List<Bus>> call = service.getBusLocation("994595");

        call.enqueue(new Callback<List<Bus>>() {
            @Override
            public void onResponse(Call<List<Bus>> call, Response<List<Bus>> response) {

                List<Bus> list = response.body();
                Bus bus = null;

                try {
                    PolylineOptions rectOptions = new PolylineOptions();
                    rectOptions.color(Color.argb(255, 0, 255, 0));
                    LatLng startLatLng = null;
                    LatLng endLatLng = null;
                    ArrayList<LatLng> points = new ArrayList<LatLng>();


                    for (int i = 0; i < list.size(); i++) {
                        bus = new Bus();


                        String dbId = list.get(i).getBusId();
                        String latitude =list.get(i).getxCode();
                        String logitude =list.get(i).getyCode();



                        //   Toast.makeText(getActivity(),"wha",Toast.LENGTH_LONG).show();

                        Double lat=Double.valueOf(latitude);
                        Double lon=Double.valueOf(logitude);


                        createMarker(lat,lon,dbId);


                        mMap.clear();
                      /*  LatLng sydney = new LatLng(lat, lon);

                        if (i == 0) {
                            startLatLng = sydney;
                        }
                        if (i == list.size() - 1) {
                            endLatLng = sydney;
                        }
                        rectOptions.add(sydney);


                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                        bus.setBusId(dbId);
                        bus.setxCode(latitude);
                        bus.setyCode(logitude);
                        busList.add(bus);

                        LatLng point = new LatLng(lat, lon);

                        points.add(new LatLng(lat, lon));


                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(point).title(dbId)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.buses))

                        );*/



                    }





                } catch (Exception e) {
                    e.printStackTrace();
                    //    Toast.makeText(getApplicationContext(),"No items available",Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<List<Bus>> call, Throwable t) {

            }
        });





    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    protected Marker createMarker(double latitude, double longitude, String title) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.buses)));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        timer.cancel();

    }
}