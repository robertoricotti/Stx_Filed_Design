package activity_portrait;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.stx_field_design.BuildConfig;
import com.example.stx_field_design.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import project.DataProjectSingleton;
import dialogs.CustomToast;
import dialogs.DialogOffset;
import services_and_bluetooth.Bluetooth_CAN_Service;
import utils.MyRW_IntMem;

public class MainActivity extends AppCompatActivity {
    int countProgress=0;
    ProgressBar progressBar;
    ImageView btn_units, to_bt, openProject, to_new, to_settings, to_usbStick, to_mch, to_palina, to_info, toPairCan;


    MyRW_IntMem myRWIntMem;
    DataProjectSingleton dataProject;
    private Handler handler;
    private boolean mRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        findView();
        init();
        onClick();
        updateUI();

    }

    private void findView() {
        btn_units=findViewById(R.id.img10);
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

    }

    private void init() {

        myRWIntMem = new MyRW_IntMem();
        dataProject = DataProjectSingleton.getInstance();
    }

    @SuppressLint("NewApi")
    private void onClick() {

        btn_units.setOnClickListener(view -> {
            startActivity(new Intent(this,UOM_Activity.class));
            finish();

        });
        to_usbStick.setOnClickListener(view -> {
            if(Build.VERSION.SDK_INT <= 29){

                new CustomToast(this,"Can't COPY TO USB Stick\nOn This Device").show();
                Intent intent = new Intent(MainActivity.this, UsbActivity.class);
                startActivity(intent);
                finish();
            }else {
            Intent intent = new Intent(MainActivity.this, UsbActivity.class);
            startActivity(intent);
            finish();}
        });

        to_info.setOnClickListener(view -> {
            new CustomToast(this, "STX Field Design\nv " + BuildConfig.VERSION_NAME.toString()).show();
        });
        to_palina.setOnClickListener(view -> {
            new DialogOffset(MainActivity.this).show();
        });

        to_bt.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BT_DevicesActivity.class);
            BT_DevicesActivity.flag = "GPS";
            startActivity(intent);
            finish();
        });
        toPairCan.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BT_DevicesActivity.class);
            BT_DevicesActivity.flag = "CAN";
            startActivity(intent);
            finish();
        });
        openProject.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            if(progressBar.getVisibility()==View.VISIBLE) {
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
            startActivity(new Intent(MainActivity.this, MchMeaureActivity.class));

            finish();
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

                            if(progressBar.getVisibility()==View.VISIBLE){
                                countProgress++;
                            to_bt.setEnabled(false);
                            openProject.setEnabled(false);
                            to_new.setEnabled(false);
                            to_settings.setEnabled(false);
                            to_usbStick.setEnabled(false);

                            to_mch.setEnabled(false);
                            to_palina.setEnabled(false);
                            to_info.setEnabled(false);
                            toPairCan.setEnabled(false);}
                            else {
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
                            if(countProgress>100){
                                progressBar.setVisibility(View.INVISIBLE);
                                countProgress=0;
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

        new CustomToast(this,"No Project Selected\nPlease Choose One").show();

    } else {
        DataProjectSingleton.getInstance().readProject(path);

        if (dataProject.readProject(path)) {
            startActivity(new Intent(MainActivity.this, AB_WorkActivity.class));
            finish();
        }
    }

    }




    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunning = false;

    }


}