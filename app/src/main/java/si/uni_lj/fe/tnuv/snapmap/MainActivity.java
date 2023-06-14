package si.uni_lj.fe.tnuv.snapmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import android.graphics.BitmapFactory;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;

    private ImageView ugibaj1;
    private ImageView ugibaj2;
    private ImageView ugibaj3;
    private ImageView slikaj;
    private int stSlik = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        // Request location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            // Permission is already granted, proceed with your logic
        }

        ugibaj1 = findViewById(R.id.lokacija_gumb1);
        ugibaj2 = findViewById(R.id.lokacija_gumb2);
        ugibaj3 = findViewById(R.id.lokacija_gumb3);
        slikaj = findViewById(R.id.kamera);

        ugibaj1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Lokacija.class);
                intent.putExtra("ix", 0);
                startActivity(intent);
            }
        });

        ugibaj2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Lokacija.class);
                intent.putExtra("ix", 1);
                startActivity(intent);
            }
        });

        ugibaj3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Lokacija.class);
                intent.putExtra("ix", 2);
                startActivity(intent);
            }
        });

        slikaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Slikaj.class);
                startActivity(intent);
            }
        });

    }
    private void dodeliLokacije() {
        LatLng slika1 = new LatLng(46.045152, 14.489788);
        CoordinateManager.saveCoord(this, slika1);
        LatLng slika2 = new LatLng(46.049046, 14.486340);
        CoordinateManager.saveCoord(this, slika2);
        LatLng slika3 = new LatLng(46.369919, 15.086530);
        CoordinateManager.saveCoord(this, slika3);
    }

    public void mojeSlike() {
        LinearLayout linearLayout = findViewById(R.id.glavna);

        List<File> imageFiles = getSavedImageFiles();
        for (int i=0; i < imageFiles.size(); i++) {
            final int ix = i+3;
            ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.okno_slike, null);
            ImageView imageView = constraintLayout.findViewById(R.id.lokacija1Slika);
            Bitmap bitmap = BitmapFactory.decodeFile(imageFiles.get(i).getAbsolutePath());
            imageView.setImageBitmap(bitmap);
            ImageView btn = constraintLayout.findViewById(R.id.lokacija_gumb);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, Lokacija.class);
                    intent.putExtra("ix", ix);
                    startActivity(intent);
                }
            });
            linearLayout.addView(constraintLayout);
            //i++;
        }
        ImageView konec = new ImageView(MainActivity.this);
        float scale = getResources().getDisplayMetrics().density;
        int height = (int) (75 * scale + 0.5f);
        konec.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)); // Set the layout parameters
        konec.setImageResource(R.drawable.rob2);
        linearLayout.addView(konec);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, proceed with your logic
            } else {
                // Location permission denied, handle accordingly (e.g., show a message)
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (CoordinateManager.getCoord(this).isEmpty()) {
            dodeliLokacije();
        }

        //Pridobi niz slik
        List<File> imageFiles = getSavedImageFiles();
        int dolzina = imageFiles.size();
        if (dolzina != 0 && dolzina != stSlik) {
            mojeSlike();
            stSlik = dolzina;
        }

        /* Prikaz slik
        if (!imageFiles.isEmpty()) {
            File firstImageFile = imageFiles.get(0);
            ImageView imageView = findViewById(R.id.lokacija1Slika);
            if (dolzina == 2) {
                File secondImageFile = imageFiles.get(1);
                ImageView imageView2 = findViewById(R.id.lokacija2Slika);
                Bitmap bitmap2 = BitmapFactory.decodeFile(secondImageFile.getAbsolutePath());
                imageView2.setImageBitmap(bitmap2);
            }
            Bitmap bitmap = BitmapFactory.decodeFile(firstImageFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }*/
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