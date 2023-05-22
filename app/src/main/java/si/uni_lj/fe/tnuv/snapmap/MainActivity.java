package si.uni_lj.fe.tnuv.snapmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

    }
}