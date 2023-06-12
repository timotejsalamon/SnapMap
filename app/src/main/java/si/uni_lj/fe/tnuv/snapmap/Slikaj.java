package si.uni_lj.fe.tnuv.snapmap;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;


public class Slikaj extends AppCompatActivity implements SurfaceHolder.Callback, LocationListener {
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private Camera camera;
    private SurfaceHolder surfaceHolder;


    private Location currentLocation;
    private LocationManager locationManager;

    private boolean isCameraInitialized = false;
    //private boolean isSurfaceCreated = false;
    public static String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.slikaj);

        SurfaceView surfaceView = findViewById(R.id.cameraPreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Display rationale dialog explaining why the camera permission is needed
                showPermissionRationaleDialog();
            } else {
                // No explanation needed, request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        } else {
            initializeCamera();
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    public void capturePicture(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            if (camera != null) {
                camera.takePicture(null, null, pictureCallback);
                lokacija();
            }
        }
    }


    public void lokacija() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ce nima dovoljenja, vprasa za lokacijo
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            // Ce ima ze dovoljenje, pridobi trenutno lokacijo
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        locationManager.removeUpdates(this);

        Intent intent = new Intent(this, PotrdiSliko.class);
        intent.putExtra("slikaLat", currentLocation.getLatitude());
        intent.putExtra("slikaLon", currentLocation.getLongitude());
        intent.putExtra("slika" , imagePath);
        startActivity(intent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ce ima ze dovoljenje, pridobi trenutno lokacijo
                getCurrentLocation();
            } else {
                // Nima dovoljenja do lokacije
                Toast.makeText(this, "Dostop do lokacije zavrnjen", Toast.LENGTH_SHORT).show();
            }
        }

        //ZA KAMERO
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeCamera();
            } else {
                Toast.makeText(this, "Dostop do kamere zavrnjen. Slikanje ni mogoče", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            saveImageLocally(imageBitmap);
            camera.startPreview();
        }
    };

    private void saveImageLocally(Bitmap imageBitmap) {
        Toast.makeText(Slikaj.this, "NALAGANJE...", Toast.LENGTH_SHORT).show();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName);

        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imagePath = imageFile.getAbsolutePath();

        // Doda sliko v medie sistema
        //Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        //Uri contentUri = Uri.fromFile(imageFile);
        //mediaScanIntent.setData(contentUri);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initializeCamera();
        /*
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
         */

        //setCameraDisplayOrientation();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Sprosti kamero
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void initializeCamera() {
        if (!isCameraInitialized) {
            try {
                camera = Camera.open();
                camera.setPreviewDisplay(surfaceHolder);


                setCameraDisplayOrientation();


                camera.startPreview();
                isCameraInitialized = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*
        if (camera == null) {
            try {
                camera = Camera.open();
                isCameraInitialized = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

         */
    }

    private void releaseCamera() {
        if (camera != null) {
            if (isCameraInitialized) {
                camera.stopPreview();
                isCameraInitialized = false;
            }
            camera.release();
            camera = null;
        }
    }

    private void setCameraDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private void showPermissionRationaleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Potreben dostop do kamere");
        builder.setMessage("Aplikacija za slikanje potrebuje odobren dostop do kamere");
        builder.setPositiveButton("Dovoli", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(Slikaj.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        });
        builder.setNegativeButton("Zavrni", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(Slikaj.this, "Dostop do kamere zavrnjen. Slikanje ni mogoče", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }
}