package activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stx_field_design.BuildConfig;
import com.example.stx_field_design.R;

import bluetooth.BT_Conn;
import dialogs.CloseAppDialog;
import dialogs.ConnectDialog;
import dialogs.CustomToast;
import gnss.NmeaListenerGGAH;
import services.DataSaved;
import utils.FullscreenActivity;

public class MainActivity extends AppCompatActivity {
    ImageView btn_exit, btn_tognss, to_bt, to_files, to_new, to_settings, to_stakeout, img_connect, to_mch, to_palina, to_info;
    TextView textCoord, txtSat, txtFix, txtCq, txtHdt, txtAltezzaAnt, txtRtk;
    private Handler handler;
    private boolean mRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FullscreenActivity.setFullScreen(this);
        findView();
        onClick();
        updateUI();

    }

    private void findView() {
        btn_exit = findViewById(R.id.btn_exit);
        btn_tognss = findViewById(R.id.btn_tognss);
        to_bt = findViewById(R.id.img1);
        to_files = findViewById(R.id.img2);
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
    }

    @SuppressLint("NewApi")
    private void onClick() {
        to_info.setOnClickListener(view -> {
            new CustomToast(this, "STX Field Design\n"+BuildConfig.VERSION_NAME.toString()).show();
        });
        to_palina.setOnClickListener(view -> {

        });
        btn_exit.setOnClickListener(view -> {
            new CloseAppDialog(this).show();
        });
        to_bt.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, BT_DevicesActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        to_files.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, FilesActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        to_new.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, NewProjectActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        to_settings.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, NewProjectActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        to_stakeout.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, StakeOutactivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        img_connect.setOnClickListener(view -> {
            new ConnectDialog(this).show();

        });
        to_mch.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, MchMeaureActivity.class));
            overridePendingTransition(0, 0);
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
                            txtAltezzaAnt.setText(String.format("%.3f", DataSaved.D_AltezzaAnt).replace(",", "."));
                            if (BT_Conn.GNSSServiceState) {
                                img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.green));
                                textCoord.setText("N: " + String.format("%.3f", NmeaListenerGGAH.Nord1).replace(",", ".") + "\tE: " + String.format("%.3f", NmeaListenerGGAH.Est1).replace(",", ".") + " Z: " + String.format("%.3f", NmeaListenerGGAH.Quota1).replace(",", "."));
                                txtSat.setText("\t" + NmeaListenerGGAH.ggaSat);
                                if (NmeaListenerGGAH.ggaQuality != null) {
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
                                        default:
                                            txtFix.setText("\tAUTONOMOUS");
                                            break;
                                    }
                                }
                                if (NmeaListenerGGAH.sCQ_v != null) {
                                    txtCq.setText("\tH: " + NmeaListenerGGAH.sCQ_h.replace(",", ".") + "\tV: " + NmeaListenerGGAH.sCQ_v.replace(",", "."));
                                } else {
                                    txtCq.setText("H:---.-- V:---.--");
                                }
                                txtHdt.setText("\t" + NmeaListenerGGAH.sHDT);
                                txtRtk.setText("\t" + NmeaListenerGGAH.ggaRtk);

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
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunning = false;
    }

    @Override
    public void onBackPressed() {

    }
}