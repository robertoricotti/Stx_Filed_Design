package activity;



import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
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

import services_and_bluetooth.AutoConnectionService;
import services_and_bluetooth.UpdateValues;
import utils.FullscreenActivity;

@SuppressLint("CustomSplashScreen")
public class LaunchScreenActivity extends AppCompatActivity {

    String[] PERMISSIONS = null;
    final int PERMISSION_REQUEST_CODE = 1;
    private static final String ACTION_USB_PERMISSION = "com.example.stx_field_design.USB_PERMISSION";
    private ProgressBar pgBar;
    private int progress = 0;
    CountDownTimer count;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launchscreen_activity);
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

        checkExternalEnvironment();
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void checkExternalEnvironment() {
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
        } else if (Build.VERSION.SDK_INT < 33 && Build.VERSION.SDK_INT > 29) {
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
                    Manifest.permission.ACCESS_NETWORK_STATE,
            };
        } else if (Build.VERSION.SDK_INT <= 29) {
            PERMISSIONS = new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    UsbManager.ACTION_USB_ACCESSORY_ATTACHED
            };
        }

        if (checkPermissions()) {
            count.start();
            try {
                startService(new Intent(this, AutoConnectionService.class));
                Toast.makeText(this, "STX FIELD RUNNING...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // Gestisci l'eccezione, se necessario
            }
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            Log.d("PermissionDebug", "Requesting permissions...");
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
            }
        }
    }

    private void goMain() {
        startService(new Intent(LaunchScreenActivity.this, UpdateValues.class));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestUsbPermission() {
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        UsbAccessory[] accessories = usbManager.getAccessoryList();

        if (accessories != null && accessories.length > 0) {
            UsbAccessory accessory = accessories[0];
            PendingIntent permissionIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    new Intent(ACTION_USB_PERMISSION),
                    0
            );

            usbManager.requestPermission(accessory, permissionIntent);
        }
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (accessory != null) {
                            // Autorizzazione USB concessa, puoi fare qualcosa con l'accessorio
                        }
                    } else {
                        // Autorizzazione USB negata
                        Toast.makeText(context, "USB permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(usbReceiver);
    }

    @Override
    public void onBackPressed() {
    }
}

