package si.uni_lj.fe.tnuv.snapmap;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
public class Rezultat extends AppCompatActivity {

    private double pravilnoLat;
    private double praviloLon;
    private double izbranoLat;
    private double izbranoLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rezultat);

        pravilnoLat = 46.045152;
        praviloLon = 14.489788;

        izbranoLat = getIntent().getDoubleExtra("lat", 0);
        izbranoLon = getIntent().getDoubleExtra("lon", 0);

        String tocke = "Distance: " + distance(pravilnoLat, praviloLon, izbranoLat, izbranoLon) + "\nStevilo tock: " + tockeIzracun(distance(pravilnoLat, praviloLon, izbranoLat, izbranoLon));

        Toast.makeText(this, tocke, Toast.LENGTH_SHORT).show();
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
            return 0;
        } else if (distance < 0.1) {
            return maxTock;
        }

        double slope = (minTock - maxTock) / (maxRazdalja - minRazdalja);
        int tocke = (int) (slope * (distance - minRazdalja) + maxTock);

        return tocke;
    }
}
