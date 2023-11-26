package activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stx_field_design.BuildConfig;
import com.example.stx_field_design.R;

import can.Can_Decoder;
import dialogs.PickProjectDialog;
import gnss.My_LocationCalc;
import gnss.Nmea_In;
import project.DataProjectSingleton;
import project.MenuProject;
import dialogs.CloseAppDialog;
import dialogs.ConnectDialog;
import dialogs.CustomToast;
import dialogs.DialogOffset;
import project.LoadProject;
import services_and_bluetooth.Bluetooth_CAN_Service;
import services_and_bluetooth.Bluetooth_GNSS_Service;
import services_and_bluetooth.DataSaved;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;

public class MainActivity extends AppCompatActivity {
    boolean showCoord=false;
    ImageView btn_exit, btn_to_can, to_bt, openProject, to_new, to_settings, to_stakeout, img_connect, to_mch, to_palina, to_info,toPairCan;
    TextView textCoord, txtSat, txtFix, txtCq, txtHdt, txtAltezzaAnt, txtRtk,txt_tilt;
    PickProjectDialog pickProjectDialog;
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
        toPairCan=findViewById(R.id.img8);
        txt_tilt=findViewById(R.id.tx_slope);

    }

    private void init(){
        pickProjectDialog = new PickProjectDialog(this);
        myRWIntMem = new MyRW_IntMem();
        dataProject = DataProjectSingleton.getInstance();
    }

    @SuppressLint("NewApi")
    private void onClick() {
        to_stakeout.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity.this, UsbActivity.class);
            startActivity(intent);
            finish();
        });
        btn_to_can.setOnClickListener(view -> {
            new ConnectDialog(this,2).show();
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
            Intent intent=new Intent(MainActivity.this, BT_DevicesActivity.class);
            BT_DevicesActivity.flag="GPS";
            startActivity(intent);
            finish();
        });
        toPairCan.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity.this, BT_DevicesActivity.class);
            BT_DevicesActivity.flag="CAN";
            startActivity(intent);
            finish();
        });
        openProject.setOnClickListener(view -> {

            String path = myRWIntMem.MyRead("projectPath", this);

            if(path == null){
                if(!pickProjectDialog.dialog.isShowing())
                    pickProjectDialog.show();
            }
            else{
                DataProjectSingleton.getInstance().readProject(path);

                startActivity(new Intent(this, LoadProject.class));

                finish();
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
            new ConnectDialog(this,1).show();

        });
        to_mch.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, MchMeaureActivity.class));

            finish();
        });
        textCoord.setOnClickListener(view -> {
            showCoord=!showCoord;
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
                            txtAltezzaAnt.setText(String.format("%.3f", DataSaved.D_AltezzaAnt).replace(",", "."));
                            if(Bluetooth_CAN_Service.canIsConnected){
                                txt_tilt.setText(String.valueOf("Pitch: "+String.format("%.2f",Can_Decoder.correctPitch)+"°       Roll: "+String.format("%.2f",Can_Decoder.correctRoll)+"°"));

                                btn_to_can.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.green));
                                btn_to_can.setImageResource(R.drawable.btn_ecu_connect);
                            }else{
                                txt_tilt.setText(String.valueOf("CAN DISCONNECTED"));

                                btn_to_can.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                                btn_to_can.setImageResource(R.drawable.btn_can_disconn);
                            }

                            if(showCoord){
                                textCoord.setText("Lat: " + My_LocationCalc.decimalToDMS(Nmea_In.mLat_1) + "\tLon: "
                                        + My_LocationCalc.decimalToDMS(Nmea_In.mLon_1) + " Z: "
                                        + String.format("%.3f", Nmea_In.Quota1).replace(",", "."));
                            }else {
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
                                            img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
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
                                            img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                                            break;
                                    }
                                }
                                if (Nmea_In.VRMS_ != null) {
                                    txtCq.setText("\tH: " + Nmea_In.HRMS_.replace(",", ".") + "\tV: " + Nmea_In.VRMS_.replace(",", "."));
                                } else {
                                    txtCq.setText("H:---.-- V:---.--");
                                }
                                txtHdt.setText("\t" + String.format("%.2f", Nmea_In.tractorBearing).replace(",","."));
                                txtRtk.setText("\t" + Nmea_In.ggaRtk);
                                textCoord.setTextColor(Color.BLACK);

                            } else {
                                img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                                img_connect.setImageResource(R.drawable.btn_gpsoff);
                               // textCoord.setText("\tDISCONNECTED");
                                textCoord.setTextColor(Color.RED);
                                txtSat.setText("--");
                                txtFix.setText("---");
                                txtCq.setText("H:---.-- V:---.--");
                                txtHdt.setText("---.--");
                                txtRtk.setText("----");
                            }


                        }
                    });
                    // sleep per intervallo update UI
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

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