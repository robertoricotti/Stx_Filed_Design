package activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stx_field_design.R;

import bluetooth.BT_Conn_GPS;
import dialogs.ConnectDialog;
import gnss.Nmea_In;
import services.DataSaved;
import utils.FullscreenActivity;

public class StakeOutactivity extends AppCompatActivity {
    ImageView btn_exit,img_connect;
    TextView textCoord, txtSat, txtFix, txtCq, txtHdt, txtAltezzaAnt, txtRtk;
    private Handler handler;
    private boolean mRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stake_outactivity);
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

    }
    private void onClick(){
        img_connect.setOnClickListener(view -> {
            new ConnectDialog(this,1).show();

        });
        btn_exit.setOnClickListener(view -> {
            startActivity(new Intent(StakeOutactivity.this,MainActivity.class));

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
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunning=false;
    }
}