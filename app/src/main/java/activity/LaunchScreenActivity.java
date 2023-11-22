package activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.example.stx_field_design.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;

import services_and_bluetooth.AutoConnectionService;
import services_and_bluetooth.UpdateValues;
import utils.FullscreenActivity;


@SuppressLint("CustomSplashScreen")
public class
LaunchScreenActivity extends AppCompatActivity {
    private ProgressBar pgBar;

    private int progress = 0;
    String[] PERMISSIONS;
    int PERMISSION_ALL = 1;
    CountDownTimer count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.launchscreen_activity);
        super.onCreate(savedInstanceState);
        pgBar = findViewById(R.id.progressBar);
        FullscreenActivity.setFullScreen(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PERMISSIONS = new String[]{


                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_PRIVILEGED,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,

                    };
        }


        askPermission();
        createSystemFolders();

        try {

            startService(new Intent(this, AutoConnectionService.class));
        } catch (Exception e) {
            //
        }

        count = new CountDownTimer(3000, 1) {
            @Override
            public void onTick(long l) {
                progress++;
                pgBar.setProgress((int) progress++);
            }

            @Override
            public void onFinish() {

                goMain();

            }
        };
    }

    private void createSystemFolders() {
        /*File exdirectory= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            exdirectory = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "/STX").getPath());
        }
        if(exdirectory.exists()){
            File xprojectsDirectory = new File(exdirectory, "/Projects");
                xprojectsDirectory.mkdir();
                Log.d("MyPath",xprojectsDirectory.getPath().toString());

        }else {
            File[] files = exdirectory.listFiles();
            Log.d("MyPath Files", "FileName:" + files.length);
            Toast.makeText(this, files.length+"   len", Toast.LENGTH_LONG).show();
            for (int i = 0; i < files.length; i++)
            {
                Log.d("MyPath Files", "FileName:" + files[i].getName());
            }
        }
*/


        ////////////////////////////////////////////////////////////////////////////

        File directory = new File(getExternalFilesDir(null) + "/Stx Field");

        if (!directory.exists()) {
            if (directory.mkdir()) {

                File projectsDirectory = new File(directory.getAbsolutePath() + "/Projects");
               // File csvDirectory = new File(directory.getAbsolutePath() + "/CSV");
                //File stxDirectory = new File(directory.getAbsolutePath() + "/STX");
                //File devicesDirectory = new File(directory.getAbsolutePath() + "/Devices");

                if (projectsDirectory.mkdir()  ) {
                    Log.e("CreateFolders", "Folder created OK");
                } else {
                    Log.e("CreateFolders", "Failed to create subdirectories.");
                }
            } else {
                Log.e("CreateFolders", "Failed to create directory Stx Field.");
            }
        }
    }


    @Override
    public void onBackPressed() {
    }

    protected void goMain() {
        startService(new Intent(LaunchScreenActivity.this, UpdateValues.class));
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();


    }


    public void askPermission() {

        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS, PERMISSION_ALL);
                return;
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                count.start();
            } else {
                try {
                    count.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Toast.makeText(this, "Until you grant the permission, we cannot proceed further", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}