package project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.stx_field_design.R;

import java.util.Map;

import bluetooth.BT_Conn_GPS;
import coords_calc.GPS;
import dialogs.MyEpsgDialog;
import dialogs.PickProjectDialog;
import dialogs.SaveFileDialog;
import gnss.My_LocationCalc;
import gnss.Nmea_In;
import services.DataSaved;
import utils.FullscreenActivity;
import utils.Utils;

public class PlaneProject extends AppCompatActivity {
    boolean showCoord=false;

    ConstraintLayout container_draw;
    ImageView btnExit, btnSave, img_connect, btnPick, allPoints;
    TextView textCoord, txtSat, txtFix, txtCq, txtHdt, txtAltezzaAnt, txtRtk;
    ProjectCanvas canvas;
    ImageButton centerNavigator, zoomIn, zoomOut;
    Button btnCrs, btnDelaunay, btnLineRef, design, clear;
    MyEpsgDialog myEpsgDialog;
    SaveFileDialog saveFileDialog;
    PickProjectDialog pickProjectDialog;
    Handler handler;
    Runnable updateRunnable;
    DataProjectSingleton dataProject;
    int textCoordStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plane_project);
        FullscreenActivity.setFullScreen(this);
        findView();
        init();
        onClick();
        updateUI();
    }

    private void findView(){
        btnSave = findViewById(R.id.save);
        btnExit = findViewById(R.id.btn_exit);
        btnDelaunay = findViewById(R.id.delunay);
        btnCrs = findViewById(R.id.crs);
        btnLineRef = findViewById(R.id.line);
        btnPick = findViewById(R.id.pickPoint);
        allPoints = findViewById(R.id.go_to_list);
        centerNavigator = findViewById(R.id.centerNavigator);
        zoomIn = findViewById(R.id.zoomIn);
        zoomOut = findViewById(R.id.zoomOut);
        textCoord = findViewById(R.id.txt_coord);
        txtSat = findViewById(R.id.txt_satnr);
        txtFix = findViewById(R.id.txt_quality);
        txtCq = findViewById(R.id.txt_precision);
        txtHdt = findViewById(R.id.txt_hdt);
        txtAltezzaAnt = findViewById(R.id.txt_speed);
        txtRtk = findViewById(R.id.txt_rtk);
        img_connect = findViewById(R.id.img_connetti);
        container_draw = findViewById(R.id.container_draw);
        design = findViewById(R.id.design);
        clear = findViewById(R.id.clear);
    }

    private void init(){
        myEpsgDialog = new MyEpsgDialog(this);
        saveFileDialog = new SaveFileDialog(this);

        dataProject = DataProjectSingleton.getInstance();

        canvas = new ProjectCanvas(this);
        container_draw.addView(canvas);

        textCoordStatus = 0;
    }

    private void onClick(){
        btnPick.setOnClickListener((View v) -> {

            final String EPSG_CODE = "4326";

            if(dataProject.getEpsgCode() == null){
                dataProject.setEpsgCode(EPSG_CODE,PlaneProject.this);
            }

            if(dataProject.getSize() < 1){
                GPS gps = new GPS(Nmea_In.mLat_1, Nmea_In.mLon_1, Nmea_In.Quota1, dataProject.getEpsgCode());
                dataProject.addCoordinate(Utils.randomString(6), gps);
            }
            else {
                Toast.makeText(this, "Limit Exceed!", Toast.LENGTH_SHORT).show();
            }
        });

        clear.setOnClickListener((View v) -> {
            if(dataProject.getSize() > 0){
                dataProject.deleteAllCoordinate();
                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
            }
        });


        btnExit.setOnClickListener((View v) -> {
            startActivity(new Intent(this, MenuProject.class));

            finish();
        });

        btnCrs.setOnClickListener((View v) -> {
            if(!myEpsgDialog.dialog.isShowing())
                myEpsgDialog.show();
        });

        allPoints.setOnClickListener((View v) -> {
            if((dataProject.getSize() == 1) || (dataProject.getSize() >= 3)){

            }
            else {
                Toast.makeText(this, "Points not available!", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener((View v) -> {
            if(dataProject.getSize() == 1) {
                if(!saveFileDialog.dialog.isShowing())
                    saveFileDialog.show();
            }
            else {
                Toast.makeText(this, "Points not available!", Toast.LENGTH_SHORT).show();
            }
        });

        btnLineRef.setOnClickListener((View v) -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Choose ID");

            String[] items = new String[dataProject.getSize() + 1];
            items[0] = "NONE";

            int counter = 1;
            for (Map.Entry<String, GPS> entry : dataProject.getPoints().entrySet()) {
                items[counter++] = entry.getKey();
            }

            alertDialog.setSingleChoiceItems(items, -1, (dialog, which) -> {
                dataProject.setDistanceID(which <= 0 ? null : items[which]);
                dialog.dismiss();
            });

            alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(true);
            alert.show();
        });

        centerNavigator.setOnClickListener((View v) -> {
            dataProject.setOffsetX(0);
            dataProject.setOffsetY(0);
            dataProject.setmScaleFactor(1f);
            canvas.invalidate();
        });

        zoomIn.setOnClickListener((View v) -> {
            dataProject.mScaleFactor +=  0.05f;
            canvas.invalidate();
        });

        zoomOut.setOnClickListener((View v) -> {
            if(dataProject.mScaleFactor > 0.1f) {
                dataProject.mScaleFactor -= 0.05f;
                canvas.invalidate();
            }
        });

        textCoord.setOnClickListener(view -> {
            showCoord=!showCoord;
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateUI() {
        handler = new Handler();
        updateRunnable = () -> {

            btnCrs.setText(dataProject.getEpsgCode() != null ? dataProject.getEpsgCode() : "CRS");
            btnLineRef.setText(dataProject.getDistanceID() == null ? "LINE ID" : dataProject.getDistanceID());

            txtAltezzaAnt.setText(String.format("%.3f", DataSaved.D_AltezzaAnt).replace(",","."));
            if (BT_Conn_GPS.GNSSServiceState) {
                img_connect.setImageResource(R.drawable.btn_positionpage);

                if(showCoord){
                    textCoord.setText("Lat: " + My_LocationCalc.decimalToDMS(Nmea_In.mLat_1) + "\tLon: "
                            + My_LocationCalc.decimalToDMS(Nmea_In.mLon_1) + " Z: "
                            + String.format("%.3f", Nmea_In.Quota1).replace(",", "."));
                }else {
                    textCoord.setText("E: " + String.format("%.3f", Nmea_In.Crs_Est).replace(",", ".") + "\tN: "
                            + String.format("%.3f", Nmea_In.Crs_Nord).replace(",", ".") + " Z: "
                            + String.format("%.3f", Nmea_In.Quota1).replace(",", "."));
                }

                txtSat.setText("\t"+ Nmea_In.ggaSat);
                if(Nmea_In.ggaQuality!=null){
                    switch (Nmea_In.ggaQuality) {
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

            }
            else {
                img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                img_connect.setImageResource(R.drawable.btn_gpsoff);

                textCoord.setText("\tDISCONNECTED");
                txtSat.setText("--");
                txtFix.setText("---");
                txtCq.setText("H:---.-- V:---.--");
                txtHdt.setText("---.--");
                txtRtk.setText("----");
            }

            canvas.invalidate();

            handler.postDelayed(updateRunnable, 100);

        };
        handler.post(updateRunnable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
            dataProject.clearData();
        }
    }
}
