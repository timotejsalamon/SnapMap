package si.uni_lj.fe.tnuv.snapmap;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class Rezultat extends AppCompatActivity implements OnMapReadyCallback {

    private double pravilnoLat;
    private double praviloLon;
    private double izbranoLat;
    private double izbranoLon;

    private MapView mapa;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rezultat);

        mapa = findViewById(R.id.mapaRez);
        mapa.onCreate(savedInstanceState);
        mapa.getMapAsync(this);

        pravilnoLat = 46.045152;
        praviloLon = 14.489788;

        izbranoLat = getIntent().getDoubleExtra("lat", 0);
        izbranoLon = getIntent().getDoubleExtra("lon", 0);

        double razdalja = distance(pravilnoLat, praviloLon, izbranoLat, izbranoLon);
        int tocke = tockeIzracun(razdalja);

        TextView besedilo = findViewById(R.id.score);
        besedilo.setText("Razdalja: " + razdalja + "\nŠtevilo točk: " + tocke);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist * 1.609344); }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0); }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI); }

    private int tockeIzracun(double distance) {
        double minRazdalja = 0;
        double maxRazdalja = 80;
        int minTock = 0;
        int maxTock = 5000;

        if (distance > 80) {
            return 1;
        } else if (distance < 0.2) {
            return maxTock;
        }

        double slope = (minTock - maxTock) / (maxRazdalja - minRazdalja);
        int tocke = (int) (slope * (distance - minRazdalja) + maxTock);

        return tocke;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        LatLng pravilnoKoord = new LatLng(pravilnoLat, praviloLon);
        LatLng izbranoKoord = new LatLng(izbranoLat, izbranoLon);

        Marker pravilno = googleMap.addMarker(new MarkerOptions().position(pravilnoKoord));
        Marker izbrano = googleMap.addMarker(new MarkerOptions().position(izbranoKoord));
        pravilno.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        PolylineOptions polylineOptions = new PolylineOptions()
                .add(pravilnoKoord, izbranoKoord)
                .width(8)
                .color(Color.BLACK);
        Polyline crta = googleMap.addPolyline(polylineOptions);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pravilno.getPosition());
        builder.include(izbrano.getPosition());
        LatLngBounds meja = builder.build();
        int padding = 300;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(meja, padding);
        googleMap.animateCamera(cameraUpdate);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapa.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapa.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapa.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapa.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapa.onLowMemory();
    }
}
