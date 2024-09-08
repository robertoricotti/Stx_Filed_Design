package activity_portrait;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.stx_field_design.R;

import org.rajawali3d.BuildConfig;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import dialogs.MyEpsgDialog;
import project.DataProjectSingleton;
import dialogs.CustomToast;

import services_and_bluetooth.AutoConnectionService;
import services_and_bluetooth.Bluetooth_CAN_Service;
import services_and_bluetooth.Bluetooth_GNSS_Service;
import services_and_bluetooth.DataSaved;
import utils.MyRW_IntMem;

public class MainActivity extends AppCompatActivity {
    int countProgress = 0;
    ProgressBar progressBar;
    ImageView to_NmeaConfig,openDigApp, btn_units, to_bt, openProject, to_new, to_settings, to_usbStick, to_mch, to_palina, to_info, toPairCan, btn_screenR, setCrs, toDebug;


    MyRW_IntMem myRWIntMem;
    DataProjectSingleton dataProject;
    private Handler handler;
    private boolean mRunning = true;
    MyEpsgDialog myEpsgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        findView();
        init();
        onClick();
        updateUI();


    }

    private void findView() {
        btn_units = findViewById(R.id.img10);
        to_bt = findViewById(R.id.img1);
        openProject = findViewById(R.id.openProject);
        to_new = findViewById(R.id.img3);
        to_settings = findViewById(R.id.img4);
        to_usbStick = findViewById(R.id.img7);
        to_mch = findViewById(R.id.img5);
        to_palina = findViewById(R.id.img6);
        to_info = findViewById(R.id.img9);
        toPairCan = findViewById(R.id.img8);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        btn_screenR = findViewById(R.id.img11);
        setCrs = findViewById(R.id.img12);
        toDebug = findViewById(R.id.img13);
        openDigApp = findViewById(R.id.img14);
        to_NmeaConfig = findViewById(R.id.img15);


    }

    private void init() {
        myEpsgDialog = new MyEpsgDialog(this);
        myRWIntMem = new MyRW_IntMem();
        dataProject = DataProjectSingleton.getInstance();

    }

    @SuppressLint("NewApi")
    private void onClick() {
        to_NmeaConfig.setOnClickListener(view -> {
            if(DataSaved.useDemo==0) {
                startActivity(new Intent(this, Activity_Gnss_Setup.class));
                finish();
            }else {
                new CustomToast(this, "Can't setup NMEA\nConnect a Device").show();
            }
        });
        openDigApp.setOnClickListener(view -> {
            openApp("com.example.stx_digging");

        });
        toDebug.setOnClickListener(view -> {
            startActivity(new Intent(this, Debug_Activity.class));
            finish();
        });
        setCrs.setOnClickListener(view -> {

            // new CustomToast(this,"UTM").show();

        });

        btn_units.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UOM_Activity.class);
            startActivity(intent);
            finish();

        });
        to_usbStick.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT <= 29) {
                new CustomToast(this, "Can't Use USB Stick\nOn This Device").show();
                Intent intent = new Intent(MainActivity.this, UsbActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(MainActivity.this, UsbActivity.class);
                startActivity(intent);
                finish();
            }
        });

        to_info.setOnClickListener(view -> {
            new CustomToast(this, "STX Field Design\nv " + BuildConfig.VERSION_NAME.toString()).show();
        });
        to_palina.setOnClickListener(view -> {

            startActivity(new Intent(MainActivity.this, Antennas_Blade_Activity.class));
            finish();
        });

        to_bt.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BT_DevicesActivity.class);
            BT_DevicesActivity.flag = "GPS";
            startActivity(intent);
            finish();
        });
        toPairCan.setOnClickListener(view -> {
            /*
            if(DataSaved.deviceType.equals("SRT8PROS")||DataSaved.deviceType.equals("SRT7PROS")){
                Intent intent = new Intent(MainActivity.this, CAN_DebugActivity.class);
                startActivity(intent);
                finish();
            }
            else {
            Intent intent = new Intent(MainActivity.this, BT_DevicesActivity.class);
            BT_DevicesActivity.flag = "CAN";
            startActivity(intent);
            finish();}*/
        });
        btn_screenR.setOnClickListener(view -> {
            DataSaved.DisplayOrient = (DataSaved.DisplayOrient + 1) % 2;
            myRWIntMem.MyWrite("display", String.valueOf(DataSaved.DisplayOrient), MainActivity.this);
            recreate();
        });
        openProject.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            if (progressBar.getVisibility() == View.VISIBLE) {
                (new Handler()).postDelayed(this::openProj, 500);
            }
        });

        to_new.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, MenuProject.class));
            finish();
        });
        to_settings.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));

            finish();
        });


        to_mch.setOnClickListener(view -> {

           /* startActivity(new Intent(MainActivity.this, MchMeaureActivity.class));

            finish();*/
        });

    }

    private void updateUI() {

        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRunning) {


                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            if (progressBar.getVisibility() == View.VISIBLE) {
                                countProgress++;
                                to_bt.setEnabled(false);
                                openProject.setEnabled(false);
                                to_new.setEnabled(false);
                                to_settings.setEnabled(false);
                                to_usbStick.setEnabled(false);

                                to_mch.setEnabled(false);
                                to_palina.setEnabled(false);
                                to_info.setEnabled(false);
                                toPairCan.setEnabled(false);
                            } else {
                                to_bt.setEnabled(true);
                                openProject.setEnabled(true);
                                to_new.setEnabled(true);
                                to_settings.setEnabled(true);
                                to_usbStick.setEnabled(true);

                                to_mch.setEnabled(true);
                                to_palina.setEnabled(true);
                                to_info.setEnabled(true);
                                toPairCan.setEnabled(true);
                            }
                            if (countProgress > 100) {
                                progressBar.setVisibility(View.INVISIBLE);
                                countProgress = 0;
                            }
                            if (Bluetooth_CAN_Service.canIsConnected) {

                            } else {

                            }


                        }
                    });
                    // sleep per intervallo update UI
                    try {
                        Thread.sleep(60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private void openProj() {

        String path = myRWIntMem.MyRead("projectPath", MainActivity.this);
        if (path == null) {

            new CustomToast(this, "No Project Selected\nPlease Choose One").show();

        } else {
            DataProjectSingleton.getInstance().readProject(path);

            if (dataProject.readProject(path)) {


              //  if (dataProject.getSize() == 4 || dataProject.getSize() == 6) {
                if (dataProject.getSize()>=3) {
                    startActivity(new Intent(MainActivity.this, AB_WorkActivity.class));
                    finish();
                } else if (dataProject.getSize() == 1) {
                    startActivity(new Intent(MainActivity.this, P_WorkActivity.class));
                    finish();
                } else {
                    new CustomToast(MainActivity.this, "Error Reading Project").show();
                }
            }
        }

    }

    private void openApp(String packageName) {
        try {


            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, "gui.boot_and_choose.LaunchScreenActivity")); // Sostituisci con il nome della tua attività principale

            if (intent.resolveActivity(getPackageManager()) != null) {
                stopService(new Intent(this, Bluetooth_GNSS_Service.class));
                stopService(new Intent(this, Bluetooth_CAN_Service.class));
                stopService(new Intent(this, AutoConnectionService.class));
                startActivity(intent);

                finish();
                finishAndRemoveTask();
                System.exit(0);
            } else {
                new CustomToast(MainActivity.this, packageName + " Not Found").show();
            }
        } catch (Exception e) {
            new CustomToast(MainActivity.this, packageName + " Not Found").show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunning = false;

    }

    


}