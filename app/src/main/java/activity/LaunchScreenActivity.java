package activity;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.stx_field_design.R;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import services_and_bluetooth.AutoConnectionService;
import services_and_bluetooth.UpdateValues;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;


@SuppressLint("CustomSplashScreen")
public class LaunchScreenActivity extends AppCompatActivity {

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
    private void checkExternalEnviroment(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()){
            startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    private void init(){
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
        if(Build.VERSION.SDK_INT<33&&Build.VERSION.SDK_INT>29){
            PERMISSIONS = new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_PRIVILEGED,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE
            };

        }
         if(Build.VERSION.SDK_INT <= 29){
             PERMISSIONS = new String[]{
                     Manifest.permission.BLUETOOTH,
                     Manifest.permission.READ_EXTERNAL_STORAGE,
                     Manifest.permission.WRITE_EXTERNAL_STORAGE,
                     Manifest.permission.INTERNET,
                     Manifest.permission.ACCESS_FINE_LOCATION,
                     Manifest.permission.ACCESS_COARSE_LOCATION,
                     Manifest.permission.ACCESS_NETWORK_STATE};

        }

         if(checkPermissions()){
            count.start();
            try {

                startService(new Intent(this, AutoConnectionService.class));
                Toast.makeText(this, "STX FIELD RUNNING...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                //
            }

        }
        else{
            requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
        }

    }



    private void createSystemFolders() {
        File directory = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getPath(), "Stx Field");
        Log.d("MyPath",directory.getPath().toString());
        if (!directory.exists()) {
            if (directory.mkdir()) {

                File projectsDirectory = new File(directory + "/Projects");
                File csvDirectory = new File(projectsDirectory + "/CSV");
                File xmlDirectory = new File(projectsDirectory + "/LandXML");
                File dxfDirectory = new File(projectsDirectory + "/DXF");
                File devicesDirectory = new File(projectsDirectory + "/Devices");

                if (projectsDirectory.mkdir() && csvDirectory.mkdir() && xmlDirectory.mkdir() && devicesDirectory.mkdir()&& dxfDirectory.mkdir()) {
                    Log.e("CreateFolders", "Folder created OK");
                } else {
                    Log.e("CreateFolders", "Failed to create subdirectories.");
                }
            } else {
                Log.e("CreateFolders", "Failed to create directory Stx Field.");
            }
        }{
            Log.e("CreateFolders", "Salvataggio Dati");
            saveBTdevices(new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getPath(), "Stx Field/Devices").getPath().toString());

        }
    }

    private boolean checkPermissions() {
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
            if(checkPermissions()){
                count.start();
            }
            else {
                Toast.makeText(this, "Until you grant the permission, we cannot proceed further", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void goMain() {
        //Toast.makeText(this, "DONE", Toast.LENGTH_LONG).show();
        startService(new Intent(LaunchScreenActivity.this, UpdateValues.class));
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
    private void saveBTdevices(String path){


        File devicesDirectory = new File(path);


        if (!devicesDirectory.exists()) {
            devicesDirectory.mkdirs();
        }

        // Creare un oggetto File per il file .txt nella cartella
        File txtFile = new File(devicesDirectory, "bt_devices.txt");

        try {
            // Creare un oggetto FileWriter per scrivere nel file .txt
            FileWriter writer = new FileWriter(txtFile);

            // Scrivere le due stringhe nel file (puoi sostituire con le tue stringhe)
            writer.write("GPS MACADDRESS: "+new MyRW_IntMem().MyRead("_macaddress",LaunchScreenActivity.this)+"\n");
            writer.write("GPS NAME      : "+new MyRW_IntMem().MyRead("_gpsname",LaunchScreenActivity.this)+"\n");
            writer.write("CAN MACADDRESS: "+new MyRW_IntMem().MyRead("_macaddress_can",LaunchScreenActivity.this)+"\n");
            writer.write("CAN NAME      : "+new MyRW_IntMem().MyRead("_canname",LaunchScreenActivity.this)+"\n");

            // Chiudere il writer per salvare le modifiche
            writer.close();

            System.out.println("File scritto con successo!");

        } catch (IOException e) {
            // Gestire l'eccezione in modo appropriato
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {}
}
