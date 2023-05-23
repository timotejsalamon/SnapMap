package si.uni_lj.fe.tnuv.snapmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import android.graphics.BitmapFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView ugibaj;
    private ImageView slikaj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ugibaj = findViewById(R.id.lokacija_gumb);
        slikaj = findViewById(R.id.kamera);

        ugibaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Lokacija.class);
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




        // Retrieve the list of image files
        List<File> imageFiles = getSavedImageFiles();

        // Display the first image in an ImageView (you can customize this as needed)
        if (!imageFiles.isEmpty()) {
            File firstImageFile = imageFiles.get(0);
            ImageView imageView = findViewById(R.id.imageView);
            File secondImageFile = imageFiles.get(1);
            if(secondImageFile != null){
                ImageView imageView2 = findViewById(R.id.lokacija2Slika);
                Bitmap bitmap2 = BitmapFactory.decodeFile(secondImageFile.getAbsolutePath());
                imageView2.setImageBitmap(bitmap2);
            }
            Bitmap bitmap = BitmapFactory.decodeFile(firstImageFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
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