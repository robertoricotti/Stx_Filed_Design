package activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stx_field_design.R;

import bluetooth.BT_Conn_GPS;
import dialogs.ConnectDialog;
import gnss.Nmea_In;
import services.DataSaved;
import services.UpdateValues;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;

public class SettingsActivity extends AppCompatActivity {
    ImageView btn_exit,img_connect,imgTest,imgSave;
    TextView textCoord, txtSat, txtFix, txtCq, txtHdt, txtAltezzaAnt, txtRtk,txtsmootRmc;
    private Handler handler;
    EditText xyTol,zTol;
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
        init();
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
        imgSave=findViewById(R.id.btn_tognss);
        xyTol=findViewById(R.id.xy_tol);
        zTol=findViewById(R.id.z_tol);
        if(DataSaved.useRmc==0){
            ckrmc.setChecked(true);
            ckpos.setChecked(false);
        }else if(DataSaved.useRmc==1){
            ckpos.setChecked(true);
            ckrmc.setChecked(false);
        }


    }
    private void init(){
        xyTol.setText(String.format("%.3f",DataSaved.xy_tol));
        zTol.setText(String.format("%.3f",DataSaved.z_tol));
    }
    private void onClick(){
        imgSave.setOnClickListener(view -> {
            save();
        });
        img_connect.setOnClickListener(view -> {
            new ConnectDialog(this,1).show();

        });
        btn_exit.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this,MainActivity.class));

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
                            int rot= (int) Nmea_In.tractorBearing;
                            imgTest.setRotation(rot);
                            txtsmootRmc.setText("Bearing Average: \t\t"+DataSaved.rmcSize);
                            txtAltezzaAnt.setText(String.format("%.3f", DataSaved.D_AltezzaAnt).replace(",","."));
                            if (BT_Conn_GPS.GNSSServiceState) {
                                img_connect.setImageResource(R.drawable.btn_positionpage);

                                textCoord.setText("N: " + String.format("%.3f", Nmea_In.Nord1).replace(",", ".") + "\tE: " + String.format("%.3f", Nmea_In.Est1).replace(",", ".") + " Z: " + String.format("%.3f", Nmea_In.Quota1).replace(",", "."));
                                txtSat.setText("\t"+ Nmea_In.ggaSat);
                                if(Nmea_In.ggaQuality!=null){
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
                                        default:txtFix.setText("\tAUTONOMOUS");
                                            img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                                            break;
                                    }}
                                if(Nmea_In.VRMS_ !=null){
                                    txtCq.setText("\tH: "+ Nmea_In.HRMS_.replace(",",".")+"\tV: "+ Nmea_In.VRMS_.replace(",","."));}
                                else {txtCq.setText("H:---.-- V:---.--");}
                                txtHdt.setText("\t" + String.format("%.2f", Nmea_In.tractorBearing).replace(",","."));
                                txtRtk.setText("\t"+ Nmea_In.ggaRtk);

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

    private void save(){


        if(!xyTol.getText().toString().equals("")){
            new MyRW_IntMem().MyWrite("xy_tol", xyTol.getText().toString(), this);
        }
        if(!zTol.getText().toString().equals("")){
            new MyRW_IntMem().MyWrite("z_tol", zTol.getText().toString(), this);
        }
        startService(new Intent(SettingsActivity.this, UpdateValues.class));
        Toast.makeText(this, "SAVED!", Toast.LENGTH_SHORT).show();

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