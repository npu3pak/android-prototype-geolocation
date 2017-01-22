package ru.npu3pak.citythroughmyeyes.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import ru.npu3pak.citythroughmyeyes.R;
import ru.npu3pak.citythroughmyeyes.location.GeocodingService;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final String LOG_TAG = "GEOCODING";
    public static final int LOCATE_ME_ZOOM_LEVEL = 17;

    private GoogleApiClient googleApiClient;
    private NetworkStatusReceiver networkStatusReceiver;
    private LocationStatusReceiver locationStatusReceiver;
    private GoogleMap googleMap;
    private Location lastUserLocation;

    private View zoomInButton;
    private View zoomOutButton;
    private View locateMeButton;

    /*
    Жизненный цикл активити
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        zoomInButton = findViewById(R.id.button_zoom_in);
        zoomOutButton = findViewById(R.id.button_zoom_out);
        locateMeButton = findViewById(R.id.button_locate_me);

        zoomInButton.setVisibility(View.INVISIBLE);
        zoomOutButton.setVisibility(View.INVISIBLE);
        locateMeButton.setVisibility(View.INVISIBLE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        networkStatusReceiver = new NetworkStatusReceiver();
        locationStatusReceiver = new LocationStatusReceiver();
    }

    protected void onStart() {
        if (isNetworkAvailable()) {
            googleApiClient.connect();
        }
        //Начинаем слушать состояние сети
        registerReceiver(networkStatusReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(locationStatusReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        super.onStart();
    }

    protected void onStop() {
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
        unregisterReceiver(networkStatusReceiver);
        unregisterReceiver(locationStatusReceiver);
        super.onStop();
    }


    /*
    Карта готова
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        zoomInButton.setVisibility(View.VISIBLE);
        zoomOutButton.setVisibility(View.VISIBLE);
    }

    /*
    Подключились к Google API
     */
    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (!isLocationEnabled()) {
            Log.i(LOG_TAG, "Геолокация выключена");
            //todo показываем статус "Геолокация выключена"
        }
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            //Пользователь запретил приложению пользоваться определением местоположения
            //todo показываем сообщение об ошибке.
        }
    }

    /*
    Подключение к Google API прервано
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "onConnectionSuspended");
    }

    /*
    Ошибка подключения к Google API
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "onConnectionFailed");
    }

    /*
    Положение пользователя изменилось
     */
    @Override
    public void onLocationChanged(Location location) {
        if (this.lastUserLocation == null) {
            locateMeButton.setVisibility(View.VISIBLE);
            LatLng lastLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, LOCATE_ME_ZOOM_LEVEL));
        }
        this.lastUserLocation = location;
        Intent geocoderIntent = new Intent(this, GeocodingService.class);
        geocoderIntent.putExtra(GeocodingService.INTENT_KEY_LOCATION, location);
        GeocodingResultReceiver geocodingResultReceiver = new GeocodingResultReceiver();
        geocoderIntent.putExtra(GeocodingService.INTENT_KEY_RESULT_RECEIVER, geocodingResultReceiver);
        startService(geocoderIntent);
    }

    /*
    Определение состояния сетевого подключения
     */
    private void onNetworkConnectionBecameAvailable() {
        Log.i(LOG_TAG, "Появился доступ с сети");
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    private void onNetworkConnectionBecameUnavailable() {
        Log.i(LOG_TAG, "Доступ к сети отвалился");
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    /*
    Определение состояния сервиса определения местоположения
     */

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void onLocationBecameEnabled() {
        Log.i(LOG_TAG, "Геолокация включена");
        //todo показываем статус "Геолокация включена"
    }

    private void onLocationBecameDisabled() {
        Log.i(LOG_TAG, "Геолокация выключена");
        //todo показываем статус "Геолокация выключена"
    }

    /*
    Обработчик ответов от геокодера
     */
    @SuppressLint("ParcelCreator")
    private class GeocodingResultReceiver extends ResultReceiver {
        public GeocodingResultReceiver() {
            super(new Handler());
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle data) {
            if (resultCode == GeocodingService.RESULT_CODE_SUCCESS) {
                Address address = data.getParcelable(GeocodingService.RESULT_KEY_ADDRESS);
                if (address == null) {
                    Log.i(LOG_TAG, "Geocoding Success. Address is empty");
                } else {
                    Log.i(LOG_TAG, "Geocoding Success");
                    Log.i(LOG_TAG, address.toString());
                }
            } else {
                Log.i(LOG_TAG, "Geocoding Error");
                Exception e = (Exception) data.getSerializable(GeocodingService.RESULT_KEY_EXCEPTION);
                if (e != null) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
    Кнопки активити
     */

    public void onLocateMeButtonClick(View view) {
        if (lastUserLocation != null) {
            LatLng lastLatLng = new LatLng(lastUserLocation.getLatitude(), lastUserLocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, LOCATE_ME_ZOOM_LEVEL));
        }
    }

    public void onZoomInButtonClick(View view) {
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    public void onZoomOutButtonClick(View view) {
        googleMap.animateCamera(CameraUpdateFactory.zoomOut());
    }


    /*
    Слушатель изменения статуса доступа к сети
     */
    private class NetworkStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkAvailable()) {
                onNetworkConnectionBecameAvailable();
            } else {
                onNetworkConnectionBecameUnavailable();
            }
        }
    }

    /*
    Слушатель изменения статуса доступа к геолокации
     */
    public class LocationStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isLocationEnabled()) {
                onLocationBecameEnabled();
            } else {
                onLocationBecameDisabled();
            }
        }
    }
}
