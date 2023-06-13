package si.uni_lj.fe.tnuv.snapmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Lokacija extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnCameraIdleListener, View.OnClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private CameraPosition lokacijaKamere;
    private LatLng coords;

    private int ix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.lokacija);

        //prikaz prave slike
        ImageView slika = findViewById(R.id.lokacijaSlika);
        ix = getIntent().getIntExtra("ix", 0);
        List<File> imageFiles = getSavedImageFiles();
        Bitmap bitmap = BitmapFactory.decodeFile(imageFiles.get(ix-3).getAbsolutePath());
        slika.setImageBitmap(bitmap);

        Button gumb = findViewById(R.id.potrdi_lokacijo);
        gumb.setOnClickListener(this);

        mapView = findViewById(R.id.mapa);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                    }
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (coords != null) {
            Intent intent = new Intent(this, Rezultat.class);
            intent.putExtra("lat", coords.latitude);
            intent.putExtra("lon", coords.longitude);
            intent.putExtra("ix", ix);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Lokacija ni izbrana!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        checkLocationPermission();
        googleMap.setOnMapClickListener(this);
        googleMap.setOnCameraIdleListener(this);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            requestLastLocation();
        }
    }

    private void requestLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Brez dovoljenja za lokacaijo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        googleMap.clear();

        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        Marker marker = googleMap.addMarker(markerOptions);
        marker.showInfoWindow();

        // Premakne kamero na kliknjeno lokacijo
        if (lokacijaKamere != null) {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(lokacijaKamere));
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
        }
        coords = latLng;
    }

    public void onCameraIdle() {
        lokacijaKamere = googleMap.getCameraPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        if (googleMap != null) {
            lokacijaKamere = googleMap.getCameraPosition();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private List<File> getSavedImageFiles() {
        List<File> imageFiles = new ArrayList<>();

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && storageDir.isDirectory()) {
            File[] files = storageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".jpg")) {
                        imageFiles.add(file);
                    }
                }
            }
        }

        return imageFiles;
    }

}
