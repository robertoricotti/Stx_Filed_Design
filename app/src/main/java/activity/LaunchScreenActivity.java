package activity;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.pm.PackageManager;

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
import java.util.ArrayList;
import java.util.List;


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


    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.launchscreen_activity);
        super.onCreate(savedInstanceState);
        pgBar = findViewById(R.id.progressBar);
        FullscreenActivity.setFullScreen(this);


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
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO};

        } else if(Build.VERSION.SDK_INT == 31){
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
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE};

        }
        else if(Build.VERSION.SDK_INT == 29){
            PERMISSIONS = new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_PRIVILEGED,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE};

        }else {
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
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE};

        }


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
        checkAndRequestPermissions();
        createAppFolders();
       // createFolder( "STX FIELD");
        //logFilesInFolder("STX FIELD/Projects");
    }


    public static void createFolder(String folderName) {
        // Ottieni il percorso della directory dell'applicazione specifica
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), folderName);

        // Crea la cartella se non esiste già
        if (!folder.exists()) {
            boolean success = folder.mkdirs();

            if (success) {
                Log.d("FileUtil_scrittura", "Cartella creata con successo: " + folder.getAbsolutePath());
            } else {
                Log.e("FileUtil_scrittura", "Errore durante la creazione della cartella: " + folder.getAbsolutePath());
            }
        } else {
            Log.d("FileUtil_scrittura", "La cartella esiste già: " + folder.getAbsolutePath());
        }
        File folder2=new File(folder,"/Projects");
        if (!folder2.exists()) {
            boolean success = folder2.mkdirs();

            if (success) {
                Log.d("FileUtil_scrittura", "Cartella creata con successo: " + folder.getAbsolutePath());
            } else {
                Log.e("FileUtil_scrittura", "Errore durante la creazione della cartella: " + folder.getAbsolutePath());
            }
        } else {
            Log.d("FileUtil_scrittura", "La cartella esiste già: " + folder.getAbsolutePath());
        }




    }

    public static void logFilesInFolder(String folderName) {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), folderName);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null && files.length > 0) {
                for (File file : files) {
                    Log.d("FileUtil_lettura", "Nome file: " + file.getName() + ", Percorso: " + file.getAbsolutePath());
                }
            } else {
                Log.d("FileUtil_lettura", "La cartella è vuota: " + folder.getAbsolutePath());
            }
        } else {
            Log.e("FileUtil_lettura", "La cartella non esiste o non è una directory: " + folder.getAbsolutePath());
        }
    }







    protected void goMain() {
        startService(new Intent(LaunchScreenActivity.this, UpdateValues.class));
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();


    }
    private void createAppFolders() {


        File directory = new File(getExternalFilesDir(null) + "/Stx Field");

        if (!directory.exists()) {
            if (directory.mkdir()) {

                File projectsDirectory = new File(directory.getAbsolutePath() + "/Projects");

                if (projectsDirectory.mkdir()) {
                    Log.e("CreateFolders", "Folder created OK");
                } else {
                    Log.e("CreateFolders", "Failed to create subdirectories.");
                }
            } else {
                Log.e("CreateFolders", "Failed to create directory Stx Field.");
            }
        }
    }

    public void checkAndRequestPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            // Richiedi i permessi mancanti
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), PERMISSION_ALL);
        } else {
            // Tutti i permessi sono già concessi, avvia il timer
            count.start();
        }
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_ALL) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // Tutti i permessi sono stati concessi, avvia il timer
                count.start();
            } else {
                // Almeno un permesso non è stato concesso, gestisci di conseguenza (es. chiudi l'app)
                Toast.makeText(this, "Per continuare, devi concedere tutti i permessi richiesti.", Toast.LENGTH_SHORT).show();
            }
        }
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}