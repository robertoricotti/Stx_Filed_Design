package activity_portrait;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.example.stx_field_design.R;


import java.io.File;


import dialogs.CustomToast;
import services_and_bluetooth.AutoConnectionService;
import services_and_bluetooth.UpdateValues;
import utils.FullscreenActivity;


@SuppressLint("CustomSplashScreen")
public class LaunchScreenActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    String[] PERMISSIONS = null;
    final int PERMISSION_REQUEST_CODE = 1;
    private ProgressBar pgBar;
    private int progress = 0;
    CountDownTimer count;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.launchscreen_activity);
        super.onCreate(savedInstanceState);
        pgBar = findViewById(R.id.progressBar);
        FullscreenActivity.setFullScreen(this);

        count = new CountDownTimer(3000, 1) {
            @Override
            public void onTick(long l) {
                progress++;
                pgBar.setProgress((int) progress++);
            }

            @Override
            public void onFinish() {
                createSystemFolders();
                goMain();
            }
        };

        checkExternalEnviroment();
        init();


    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void checkExternalEnviroment() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    private void init() {
        Log.d("VersioneAnd", String.valueOf(Build.VERSION.SDK_INT));
        if (Build.VERSION.SDK_INT >= 33) {
            PERMISSIONS = new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,


            };

        }
        if (Build.VERSION.SDK_INT < 33 && Build.VERSION.SDK_INT > 29) {
            PERMISSIONS = new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE
            };

        }
        if (Build.VERSION.SDK_INT <= 29) {
            PERMISSIONS = new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE};
            new CustomToast(LaunchScreenActivity.this, "Limitated USB Features").show();

        }

        if (checkPermissions()) {
            count.start();
            try {

                startService(new Intent(this, AutoConnectionService.class));

                Toast.makeText(this, "STX FIELD RUNNING...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                //
            }

        } else {
            requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
        }

    }


    private void createSystemFolders() {
        File directory = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getPath(), "Stx Field");
        Log.d("MyPath", directory.getPath().toString());
        if (!directory.exists()) {
            if (directory.mkdir()) {

                File projectsDirectory = new File(directory + "/Projects");

                File devicesDirectory = new File(directory + "/Devices");

                if (projectsDirectory.mkdir() && devicesDirectory.mkdir()) {
                    Log.e("CreateFolders", "Folder created OK");
                } else {
                    Log.e("CreateFolders", "Failed to create subdirectories.");
                }
            } else {
                Log.e("CreateFolders", "Failed to create directory Stx Field.");
            }
            Log.e("CreateFolders", "Salvataggio Dati");

        }


    }

    private boolean checkPermissions() {
        if (requestOverlayPermission()) {
            requestOverlayPermission();
        }
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (checkPermissions()) {
                count.start();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot proceed further", Toast.LENGTH_SHORT).show();

                (new Handler()).postDelayed(this::finish, 3000);

            }
        }
    }



    private boolean requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent a = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(a);
            return false;
        } else {
            return true;
        }
    }

    protected void goMain() {
        //Toast.makeText(LaunchScreenActivity.this, String.valueOf(Build.VERSION.SDK_INT), Toast.LENGTH_SHORT).show();
        startService(new Intent(LaunchScreenActivity.this, UpdateValues.class));
        startActivity(new Intent(LaunchScreenActivity.this, MainActivity.class));
        finish();
    }


    @Override
    public void onBackPressed() {
    }

    public static void executeAdbCommand(String command) {
        try {

            Log.d("TestADB", "1: " + command);
            Runtime.getRuntime().exec(command);
            Log.d("TestADB", "2: " + command);
        } catch (Exception e) {
            Log.d("TestADB", e.toString());

        }
    }
}