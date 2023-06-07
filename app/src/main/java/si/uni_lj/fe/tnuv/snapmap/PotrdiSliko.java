package si.uni_lj.fe.tnuv.snapmap;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;


public class PotrdiSliko extends AppCompatActivity {

    Button ponovi;
    Button potrdi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.potrdi_sliko);

        double slikaLat = getIntent().getDoubleExtra("slikaLat", 0);
        double slikaLon = getIntent().getDoubleExtra("slikaLon", 0);
        convertCoordinatesToLocation(slikaLat, slikaLon);

        ponovi = findViewById(R.id.ponoviBtn);
        potrdi = findViewById(R.id.potrdiBtn);

        ponovi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PotrdiSliko.this, Slikaj.class);
                startActivity(intent);
            }
        });

        potrdi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PotrdiSliko.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void convertCoordinatesToLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String locationName = address.getAddressLine(0);

                TextView besedilo = findViewById(R.id.lokacijaText);
                besedilo.setText(locationName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


