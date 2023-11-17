package project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.stx_field_design.R;

import activity.MainActivity;
import bluetooth.BT_Conn_GPS;
import dialogs.ConnectDialog;
import dialogs.PickProjectDialog;
import gnss.My_LocationCalc;
import gnss.Nmea_In;
import services.DataSaved;
import utils.FullscreenActivity;

public class MenuProject extends AppCompatActivity {
private boolean showCoord=false;
    ImageButton plane, ab, delaunay;
    ImageView btnExit, imgConnect;
    TextView textCoord, txtSat, txtFix, txtCq, txtHdt, txtAltezzaAnt, txtRtk;
    ImageView loadProject;
    Handler handler;
    Runnable updateRunnable;
    PickProjectDialog pickProjectDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        FullscreenActivity.setFullScreen(this);
        findView();
        init();
        onClick();
        updateUI();
    }

    private void findView(){
        plane = findViewById(R.id.plane);
        ab = findViewById(R.id.ab);
        delaunay = findViewById(R.id.delaunay);
        btnExit = findViewById(R.id.btn_exit);
        textCoord = findViewById(R.id.txt_coord);
        txtSat = findViewById(R.id.txt_satnr);
        txtFix = findViewById(R.id.txt_quality);
        txtCq = findViewById(R.id.txt_precision);
        txtHdt = findViewById(R.id.txt_hdt);
        txtAltezzaAnt = findViewById(R.id.txt_speed);
        txtRtk = findViewById(R.id.txt_rtk);
        imgConnect = findViewById(R.id.img_connetti);
        loadProject = findViewById(R.id.loadProject);
    }

    private void init(){
        pickProjectDialog = new PickProjectDialog(this);
    }

    private void onClick(){
        textCoord.setOnClickListener(view -> {
            showCoord=!showCoord;
        });
        plane.setOnClickListener((View v) -> {
            startActivity(new Intent(this, PlaneProject.class));

            finish();
        });

        ab.setOnClickListener((View v) -> {
            startActivity(new Intent(this, ABProject.class));

            finish();
        });

        delaunay.setOnClickListener((View v) -> {
            startActivity(new Intent(this, DelaunayProject.class));

            finish();
        });

        btnExit.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));

            finish();
        });

        imgConnect.setOnClickListener(view -> {
            new ConnectDialog(this,1).show();
        });

        loadProject.setOnClickListener((View v) -> {
            if(!pickProjectDialog.dialog.isShowing())
                pickProjectDialog.show();
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateUI() {
        handler = new Handler();
        updateRunnable = () -> {

            txtAltezzaAnt.setText(String.format("%.3f", DataSaved.D_AltezzaAnt).replace(",", "."));
            if (BT_Conn_GPS.GNSSServiceState) {
                imgConnect.setImageResource(R.drawable.btn_positionpage);
                if(showCoord){
                    textCoord.setText("Lat: " + My_LocationCalc.decimalToDMS(Nmea_In.mLat_1) + "\tLon: "
                            + My_LocationCalc.decimalToDMS(Nmea_In.mLon_1) + " Z: "
                            + String.format("%.3f", Nmea_In.Quota1).replace(",", "."));
                }else {
                    textCoord.setText("E: " + String.format("%.3f", Nmea_In.Crs_Est).replace(",", ".") + "\tN: "
                            + String.format("%.3f", Nmea_In.Crs_Nord).replace(",", ".") + " Z: "
                            + String.format("%.3f", Nmea_In.Quota1).replace(",", "."));
                }

                txtSat.setText("\t" + Nmea_In.ggaSat);
                if (Nmea_In.ggaQuality != null) {
                    switch (Nmea_In.ggaQuality) {
                        case "2":
                            txtFix.setText("\tDGNSS");
                            imgConnect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.yellow));
                            break;
                        case "4":
                            txtFix.setText("\tFIX");
                            imgConnect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.green));
                            break;
                        case "5":
                            txtFix.setText("\tFLOAT");
                            imgConnect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.yellow));
                            break;
                        case "6":
                            txtFix.setText("\tINS");
                            imgConnect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.yellow));
                            break;
                        default:
                            txtFix.setText("\tAUTONOMOUS");
                            imgConnect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                            break;
                    }
                }
                if (Nmea_In.VRMS_ != null) {
                    txtCq.setText("\tH: " + Nmea_In.HRMS_.replace(",", ".") + "\tV: " + Nmea_In.VRMS_.replace(",", "."));
                }
                else {
                    txtCq.setText("H:---.-- V:---.--");
                }
                txtHdt.setText("\t" + String.format("%.2f", Nmea_In.tractorBearing).replace(",", "."));
                txtRtk.setText("\t" + Nmea_In.ggaRtk);
            }
            else {
                imgConnect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                imgConnect.setImageResource(R.drawable.btn_gpsoff);

                textCoord.setText("\tDISCONNECTED");
                txtSat.setText("--");
                txtFix.setText("---");
                txtCq.setText("H:---.-- V:---.--");
                txtHdt.setText("---.--");
                txtRtk.setText("----");
            }

            handler.postDelayed(updateRunnable, 100);
        };

        handler.post(updateRunnable);
    }

    @Override
    public void onBackPressed() {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }
}
