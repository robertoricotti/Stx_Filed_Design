package activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.stx_field_design.BuildConfig;
import com.example.stx_field_design.R;
import can.Can_Decoder;
import gnss.My_LocationCalc;
import gnss.Nmea_In;
import project.DataProjectSingleton;
import dialogs.CloseAppDialog;
import dialogs.ConnectDialog;
import dialogs.CustomToast;
import dialogs.DialogOffset;
import services_and_bluetooth.Bluetooth_CAN_Service;
import services_and_bluetooth.Bluetooth_GNSS_Service;
import services_and_bluetooth.DataSaved;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;

public class MainActivity extends AppCompatActivity {
    int countProgress=0;
    boolean showCoord = false;
    ProgressBar progressBar;
    ImageView btn_exit, btn_to_can, to_bt, openProject, to_new, to_settings, to_stakeout, img_connect, to_mch, to_palina, to_info, toPairCan;
    TextView textCoord, txtSat, txtFix, txtCq, txtHdt, txtAltezzaAnt, txtRtk, txt_tilt;

    MyRW_IntMem myRWIntMem;
    DataProjectSingleton dataProject;
    private Handler handler;
    private boolean mRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FullscreenActivity.setFullScreen(this);
        findView();
        init();
        onClick();
        updateUI();

    }

    private void findView() {
        btn_exit = findViewById(R.id.btn_exit);
        btn_to_can = findViewById(R.id.btn_tognss);
        to_bt = findViewById(R.id.img1);
        openProject = findViewById(R.id.openProject);
        to_new = findViewById(R.id.img3);
        to_settings = findViewById(R.id.img4);
        to_stakeout = findViewById(R.id.img7);
        img_connect = findViewById(R.id.img_connetti);
        textCoord = findViewById(R.id.txt_coord);
        txtSat = findViewById(R.id.txt_satnr);
        txtFix = findViewById(R.id.txt_quality);
        txtCq = findViewById(R.id.txt_precision);
        txtHdt = findViewById(R.id.txt_hdt);
        txtAltezzaAnt = findViewById(R.id.txt_speed);
        txtRtk = findViewById(R.id.txt_rtk);
        to_mch = findViewById(R.id.img5);
        to_palina = findViewById(R.id.img6);
        to_info = findViewById(R.id.img9);
        toPairCan = findViewById(R.id.img8);
        txt_tilt = findViewById(R.id.tx_slope);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

    }

    private void init() {

        myRWIntMem = new MyRW_IntMem();
        dataProject = DataProjectSingleton.getInstance();
    }

    @SuppressLint("NewApi")
    private void onClick() {
        to_stakeout.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UsbActivity.class);
            startActivity(intent);
            finish();
        });
        btn_to_can.setOnClickListener(view -> {
            new ConnectDialog(this, 2).show();
        });
        to_info.setOnClickListener(view -> {
            new CustomToast(this, "STX Field Design\n" + BuildConfig.VERSION_NAME.toString()).show();
        });
        to_palina.setOnClickListener(view -> {
            new DialogOffset(MainActivity.this).show();
        });
        btn_exit.setOnClickListener(view -> {
            new CloseAppDialog(this).show();
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

        img_connect.setOnClickListener(view -> {
            new ConnectDialog(this, 1).show();

        });
        to_mch.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, MchMeaureActivity.class));

            finish();
        });
        textCoord.setOnClickListener(view -> {
            showCoord = !showCoord;
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
                            btn_exit.setEnabled(false);
                            btn_to_can.setEnabled(false);
                            to_bt.setEnabled(false);
                            openProject.setEnabled(false);
                            to_new.setEnabled(false);
                            to_settings.setEnabled(false);
                            to_stakeout.setEnabled(false);
                            img_connect.setEnabled(false);
                            to_mch.setEnabled(false);
                            to_palina.setEnabled(false);
                            to_info.setEnabled(false);
                            toPairCan.setEnabled(false);}
                            else {
                                btn_exit.setEnabled(true);
                                btn_to_can.setEnabled(true);
                                to_bt.setEnabled(true);
                                openProject.setEnabled(true);
                                to_new.setEnabled(true);
                                to_settings.setEnabled(true);
                                to_stakeout.setEnabled(true);
                                img_connect.setEnabled(true);
                                to_mch.setEnabled(true);
                                to_palina.setEnabled(true);
                                to_info.setEnabled(true);
                                toPairCan.setEnabled(true);
                            }
                            if(countProgress>100){
                                progressBar.setVisibility(View.INVISIBLE);
                                countProgress=0;
                            }
                            txtAltezzaAnt.setText(String.format("%.3f", DataSaved.D_AltezzaAnt).replace(",", "."));
                            if (Bluetooth_CAN_Service.canIsConnected) {
                                txt_tilt.setText(String.valueOf("Pitch: " + String.format("%.2f", Can_Decoder.correctPitch) + "°       Roll: " + String.format("%.2f", Can_Decoder.correctRoll) + "°"));

                                btn_to_can.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.green));
                                btn_to_can.setImageResource(R.drawable.btn_ecu_connect);
                            } else {
                                txt_tilt.setText(String.valueOf("CAN DISCONNECTED"));

                                btn_to_can.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color._____cancel_text));
                                btn_to_can.setImageResource(R.drawable.btn_can_disconn);
                            }

                            if (showCoord) {
                                textCoord.setText("Lat: " + My_LocationCalc.decimalToDMS(Nmea_In.mLat_1) + "\tLon: "
                                        + My_LocationCalc.decimalToDMS(Nmea_In.mLon_1) + " Z: "
                                        + String.format("%.3f", Nmea_In.Quota1).replace(",", "."));
                            } else {
                                textCoord.setText("E: " + String.format("%.3f", Nmea_In.Crs_Est).replace(",", ".") + "\t\tN: "
                                        + String.format("%.3f", Nmea_In.Crs_Nord).replace(",", ".") + " Z: "
                                        + String.format("%.3f", Nmea_In.Quota1).replace(",", "."));
                            }

                            if (Bluetooth_GNSS_Service.gpsIsConnected) {

                                img_connect.setImageResource(R.drawable.btn_positionpage);
                                txtSat.setText("\t" + Nmea_In.ggaSat);
                                if (Nmea_In.ggaQuality != null) {
                                    switch (Nmea_In.ggaQuality) {
                                        case "":
                                        case "0":
                                        case "1":
                                            txtFix.setText("\tAUTONOMOUS");
                                            img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color._____cancel_text));
                                            break;
                                        case "2":
                                            txtFix.setText("\tDGNSS");
                                            img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.yellow));
                                            break;
                                        case "4":
                                            txtFix.setText("\tFIX");
                                            img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.green));
                                            break;
                                        case "5":
                                            txtFix.setText("\tFLOAT");
                                            img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.yellow));
                                            break;
                                        case "6":
                                            txtFix.setText("\tINS");
                                            img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.yellow));
                                            break;
                                        default:
                                            txtFix.setText("\tAUTONOMOUS");
                                            img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color._____cancel_text));
                                            break;
                                    }
                                }
                                if (Nmea_In.VRMS_ != null) {
                                    txtCq.setText("\tH: " + Nmea_In.HRMS_.replace(",", ".") + "\tV: " + Nmea_In.VRMS_.replace(",", "."));
                                } else {
                                    txtCq.setText("H:---.-- V:---.--");
                                }
                                txtHdt.setText("\t" + String.format("%.2f", Nmea_In.tractorBearing).replace(",", "."));
                                txtRtk.setText("\t" + Nmea_In.ggaRtk);
                                textCoord.setTextColor(Color.BLACK);

                            } else {
                                img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color._____cancel_text));
                                img_connect.setImageResource(R.drawable.btn_gpsoff);
                                // textCoord.setText("\tDISCONNECTED");
                                textCoord.setTextColor(Color.RED);
                                txtSat.setText("\t" + Nmea_In.ggaSat);
                                txtFix.setText("---");
                                txtCq.setText("H:---.-- V:---.--");
                                txtHdt.setText("---.--");
                                txtRtk.setText("----");
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