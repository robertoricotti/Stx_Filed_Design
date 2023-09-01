package activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.stx_field_design.R;

import bluetooth.BT_Conn;
import dialogs.ConnectDialog;
import gnss.NmeaListener_SingleHead;
import services.DataSaved;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;

public class SettingsActivity extends AppCompatActivity {
    ImageView btn_exit,img_connect,imgTest;
    TextView textCoord, txtSat, txtFix, txtCq, txtHdt, txtAltezzaAnt, txtRtk,txtsmootRmc;
    private Handler handler;
    private boolean mRunning = true;
    SeekBar seekRmc;
    CheckBox ckrmc,ckpos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        FullscreenActivity.setFullScreen(this);
        findView();
        onClick();
        updateUI();
    }
    private void findView(){
        btn_exit=findViewById(R.id.btn_exit);
        textCoord = findViewById(R.id.txt_coord);
        txtSat = findViewById(R.id.txt_satnr);
        txtFix = findViewById(R.id.txt_quality);
        txtCq = findViewById(R.id.txt_precision);
        txtHdt = findViewById(R.id.txt_hdt);
        txtAltezzaAnt = findViewById(R.id.txt_speed);
        txtRtk = findViewById(R.id.txt_rtk);
        img_connect = findViewById(R.id.img_connetti);
        seekRmc=findViewById(R.id.seekRmc);
        txtsmootRmc=findViewById(R.id.txtOrSmooth);
        seekRmc.setProgress(DataSaved.rmcSize);
        ckrmc=findViewById(R.id.ckRMC);
        ckpos=findViewById(R.id.ckPos);
        imgTest=findViewById(R.id.imgTest);
        if(DataSaved.useRmc==0){
            ckrmc.setChecked(true);
            ckpos.setChecked(false);
        }else if(DataSaved.useRmc==1){
            ckpos.setChecked(true);
            ckrmc.setChecked(false);
        }

    }
    private void onClick(){
        img_connect.setOnClickListener(view -> {
            new ConnectDialog(this).show();

        });
        btn_exit.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this,MainActivity.class));
            overridePendingTransition(0,0);
            finish();
        });

        ckrmc.setOnClickListener(view -> {
            ckpos.setChecked(false);
            ckrmc.setChecked(true);
            DataSaved.useRmc=0;
            new MyRW_IntMem().MyWrite("useRmc",String.valueOf(DataSaved.useRmc),SettingsActivity.this);
        });
        ckpos.setOnClickListener(view -> {
            ckpos.setChecked(true);
            ckrmc.setChecked(false);
            DataSaved.useRmc=1;
            new MyRW_IntMem().MyWrite("useRmc",String.valueOf(DataSaved.useRmc),SettingsActivity.this);
        });


        seekRmc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                DataSaved.rmcSize=i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                new MyRW_IntMem().MyWrite("rmcSize", String.valueOf(DataSaved.rmcSize),SettingsActivity.this);

            }
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
                            int rot= (int) NmeaListener_SingleHead.tractorBearing;
                            imgTest.setRotation(rot);
                            txtsmootRmc.setText("Bearing Average: \t\t"+DataSaved.rmcSize);
                            txtAltezzaAnt.setText(String.format("%.3f", DataSaved.D_AltezzaAnt).replace(",","."));
                            if (BT_Conn.GNSSServiceState) {
                                img_connect.setImageResource(R.drawable.btn_positionpage);

                                textCoord.setText("N: " + String.format("%.3f", NmeaListener_SingleHead.Nord1).replace(",", ".") + "\tE: " + String.format("%.3f", NmeaListener_SingleHead.Est1).replace(",", ".") + " Z: " + String.format("%.3f", NmeaListener_SingleHead.Quota1).replace(",", "."));
                                txtSat.setText("\t"+ NmeaListener_SingleHead.ggaSat);
                                if(NmeaListener_SingleHead.ggaQuality!=null){
                                    switch (NmeaListener_SingleHead.ggaQuality) {
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
                                        default:txtFix.setText("\tAUTONOMOUS");
                                            img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                                            break;
                                    }}
                                if(NmeaListener_SingleHead.VRMS_ !=null){
                                    txtCq.setText("\tH: "+ NmeaListener_SingleHead.HRMS_.replace(",",".")+"\tV: "+ NmeaListener_SingleHead.VRMS_.replace(",","."));}
                                else {txtCq.setText("H:---.-- V:---.--");}
                                txtHdt.setText("\t" + String.format("%.2f", NmeaListener_SingleHead.tractorBearing).replace(",","."));
                                txtRtk.setText("\t"+ NmeaListener_SingleHead.ggaRtk);

                            } else {
                                img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                                img_connect.setImageResource(R.drawable.btn_gpsoff);

                                textCoord.setText("\tDISCONNECTED");
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
                        Thread.sleep(100);
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
        mRunning=false;
    }
}