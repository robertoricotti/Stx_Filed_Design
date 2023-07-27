package activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stx_field_design.R;

import bluetooth.BT_Conn;
import dialogs.ConnectDialog;
import gnss.NmeaListenerGGAH;
import services.DataSaved;
import utils.FullscreenActivity;

public class SettingsActivity extends AppCompatActivity {
    ImageView btn_exit,img_connect;
    TextView textCoord, txtSat, txtFix, txtCq, txtHdt, txtAltezzaAnt, txtRtk;
    private Handler handler;
    private boolean mRunning = true;
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
                            if (BT_Conn.GNSSServiceState) {
                                img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.green));

                                textCoord.setText("N: " + String.format("%.3f", NmeaListenerGGAH.Nord1).replace(",", ".") + "\tE: " + String.format("%.3f", NmeaListenerGGAH.Est1).replace(",", ".") + " Z: " + String.format("%.3f", NmeaListenerGGAH.Quota1).replace(",", "."));
                                txtSat.setText("\t"+NmeaListenerGGAH.ggaSat);
                                if(NmeaListenerGGAH.ggaQuality!=null){
                                    switch (NmeaListenerGGAH.ggaQuality) {
                                        case "":
                                        case "0":
                                        case "1":
                                            txtFix.setText("\tAUTONOMOUS");
                                            break;
                                        case "2":
                                            txtFix.setText("\tDGNSS");
                                            break;
                                        case "4":
                                            txtFix.setText("\tFIX");
                                            break;
                                        case "5":
                                            txtFix.setText("\tFLOAT");
                                            break;
                                        case "6":
                                            txtFix.setText("\tINS");
                                            break;
                                        default:txtFix.setText("\tAUTONOMOUS");
                                            break;
                                    }}
                                if(NmeaListenerGGAH.sCQ_v!=null){
                                    txtCq.setText("\tH: "+NmeaListenerGGAH.sCQ_h.replace(",",".")+"\tV: "+NmeaListenerGGAH.sCQ_v.replace(",","."));}
                                else {txtCq.setText("H:---.-- V:---.--");}
                                txtHdt.setText("\t"+NmeaListenerGGAH.sHDT);
                                txtRtk.setText("\t"+NmeaListenerGGAH.ggaRtk);

                            } else {
                                img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));

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