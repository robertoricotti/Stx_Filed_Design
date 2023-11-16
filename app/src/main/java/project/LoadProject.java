package project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.stx_field_design.R;

import java.util.Map;

import activity.MainActivity;
import bluetooth.BT_Conn_GPS;
import coords_calc.GPS;
import coords_calc.Surface_Selector;
import dialogs.ConnectDialog;
import dialogs.CoordsGNSSInfo;
import dialogs.PickProjectDialog;
import gnss.My_LocationCalc;
import gnss.Nmea_In;
import services.DataSaved;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;

public class LoadProject extends AppCompatActivity {
    boolean showCoord = false;
    public static boolean auto;


    TextView textCoord, txtSat, txtFix, txtCq, txtHdt, txtAltezzaAnt, txtRtk;
    ImageView back, openList, imgConnect,lineID;
    TextView altitude, distance, fileName;
    Button loadProject;
    ConstraintLayout container;
    ProjectCanvas canvas;
    ImageButton center, zoomIn, zoomOut, rotateLeft, rotateRight, autorotate;
    Button crs, delaunay, surfaceStatus, surfaceOK;
    DataProjectSingleton dataProject;
    CoordsGNSSInfo coordsGNSSInfo;
    PickProjectDialog pickProjectDialog;
    Handler handler;
    Runnable updateRunnable;
    Surface_Selector surfaceSelector;
    boolean rotLeft=false;
    boolean rotRight=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_project);
        FullscreenActivity.setFullScreen(this);
        findView();
        init();
        onClick();
        updateUI();

    }

    private void findView() {

        back = findViewById(R.id.btn_exit);
        openList = findViewById(R.id.go_to_list);
        container = findViewById(R.id.container_draw);
        center = findViewById(R.id.centerNavigator);
        zoomIn = findViewById(R.id.zoomIn);
        zoomOut = findViewById(R.id.zoomOut);
        surfaceStatus = findViewById(R.id.surfaceStatus);
        crs = findViewById(R.id.crs);
        delaunay = findViewById(R.id.delunay);
        lineID = findViewById(R.id.pickPoint);
        altitude = findViewById(R.id.quota);
        distance = findViewById(R.id.distance);
        textCoord = findViewById(R.id.txt_coord);
        txtSat = findViewById(R.id.txt_satnr);
        txtFix = findViewById(R.id.txt_quality);
        txtCq = findViewById(R.id.txt_precision);
        txtHdt = findViewById(R.id.txt_hdt);
        txtAltezzaAnt = findViewById(R.id.txt_speed);
        txtRtk = findViewById(R.id.txt_rtk);
        imgConnect = findViewById(R.id.img_connetti);
        surfaceOK = findViewById(R.id.surfaceOK);
        fileName = findViewById(R.id.fileName);
        loadProject = findViewById(R.id.loadProject);
        rotateLeft = findViewById(R.id.rotateLeft);
        rotateRight = findViewById(R.id.rotateRight);
        autorotate = findViewById(R.id.autorotate);

    }

    private void init() {

        delaunay.setVisibility(View.INVISIBLE);
        dataProject = DataProjectSingleton.getInstance();

        surfaceSelector = new Surface_Selector(dataProject.getSize());

        pickProjectDialog = new PickProjectDialog(this);

        canvas = new ProjectCanvas(this);
        container.addView(canvas);

        coordsGNSSInfo = new CoordsGNSSInfo(this);
        if (dataProject.getSize() > 1) {
            dataProject.toggleDelaunay();
        }
        dataProject.mScaleFactor = Float.parseFloat(new MyRW_IntMem().MyRead("zoomF", this));
        dataProject.rotate = (float) Double.parseDouble(new MyRW_IntMem().MyRead("rot", this));

    }

    private void onClick() {
        autorotate.setOnClickListener(v -> {
            auto = !auto;
        });
        imgConnect.setOnClickListener(view -> {
            new ConnectDialog(this,1).show();

        });
        back.setOnClickListener((View v) -> {
            new MyRW_IntMem().MyWrite("zoomF", String.valueOf(dataProject.mScaleFactor), this);
            new MyRW_IntMem().MyWrite("rot", String.valueOf(dataProject.rotate), this);
            startActivity(new Intent(this, MainActivity.class));

            finish();
        });


        openList.setOnClickListener((View v) -> {
            if (!coordsGNSSInfo.dialog.isShowing())
                coordsGNSSInfo.show();
        });


        center.setOnClickListener((View v) -> {
            dataProject.setOffsetX(0);
            dataProject.setOffsetY(0);
            dataProject.setRotate(0f);
            //dataProject.setmScaleFactor(1f);
            canvas.invalidate();
        });

        zoomIn.setOnClickListener((View v) -> {
            dataProject.mScaleFactor += 0.05f;
            canvas.invalidate();
        });

        zoomOut.setOnClickListener((View v) -> {
            if (dataProject.mScaleFactor > 0.1f) {
                dataProject.mScaleFactor -= 0.05f;
                canvas.invalidate();
            }
        });

       rotateRight.setOnTouchListener((view, motionEvent) -> {

           if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
              rotRight=true;
           }
           if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
               rotRight=false;
           }

           return false;
       });
        rotateLeft.setOnTouchListener((view, motionEvent) -> {

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
               rotLeft=true;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                rotLeft=false;
            }

            return false;
        });

        lineID.setOnClickListener((View v) -> {
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

        delaunay.setOnClickListener((View v) -> {
            dataProject.toggleDelaunay();
        });

        loadProject.setOnClickListener((View v) -> {
            if (!pickProjectDialog.dialog.isShowing())
                pickProjectDialog.show();
        });

        textCoord.setOnClickListener(view -> {
            showCoord = !showCoord;
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateUI() {
        handler = new Handler();
        updateRunnable = () -> {
            if(rotRight){
                rotLeft=false;
                if (dataProject.rotate <= 360f) {
                    dataProject.rotate += 2f;

                } else {
                    dataProject.rotate = 0f;
                }
            }
            if(rotLeft){
                rotRight=false;
                if (dataProject.rotate >= 1f) {
                    dataProject.rotate -= 2f;

                } else {
                    dataProject.rotate = 360f;

                }

            }

            if (auto) {
                rotateLeft.setEnabled(false);
                rotateRight.setEnabled(false);
                rotateLeft.setAlpha(0.4f);
                rotateRight.setAlpha(0.4f);
                autorotate.setAlpha(1.0f);
            } else {
                rotateLeft.setEnabled(true);
                rotateRight.setEnabled(true);
                rotateLeft.setAlpha(1f);
                rotateRight.setAlpha(1f);
                autorotate.setAlpha(0.4f);
            }

            fileName.setText(dataProject.getProjectName().replace(".csv",""));


            surfaceStatus.setText(surfaceSelector.isPointInsideSurface() ? "IN" : "OUT");
            surfaceOK.setText(surfaceSelector.isSurfaceOK() ? "YES" : "NO");
            crs.setText(dataProject.getEpsgCode());


            delaunay.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), dataProject.isDelaunay() ? R.color.pure_green : R.color._____cancel_text));
            surfaceStatus.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), surfaceSelector.isPointInsideSurface() ? R.color.pure_green : R.color.red));
            surfaceOK.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), surfaceSelector.isSurfaceOK() ? R.color.pure_green : R.color.red));
            double v = 0;
            String strAltitude = "Z: " + String.format("%.3f", surfaceSelector.getAltitudeDifference(Nmea_In.mLat_1, Nmea_In.mLon_1, Nmea_In.Quota1)).replace(",", ".") + " m";
            v = surfaceSelector.getAltitudeDifference(Nmea_In.mLat_1, Nmea_In.mLon_1, Nmea_In.Quota1);
            if (Double.isNaN(v)) v = 0;

            String strDistance = "DIST: " + String.format("%.3f", surfaceSelector.getDistance(Nmea_In.mLat_1, Nmea_In.mLon_1)).replace(",", ".") + " m";


            if (surfaceSelector.isPointInsideSurface()) {
                if (Math.abs(v) <= DataSaved.z_tol) {
                    altitude.setText("⧗ "+String.format("%.3f",v));
                    altitude.setBackgroundColor(getColor(R.color.green));
                    altitude.setTextColor(getColor(R.color._____cancel_text));
                } else if (v < -(DataSaved.z_tol+0.001)) {
                    altitude.setText("▲ " +String.format("%.3f",v));
                    altitude.setBackgroundColor(getColor(R.color.red));
                    altitude.setTextColor(getColor(R.color.white));
                } else if (v > DataSaved.z_tol+0.001) {
                    altitude.setText("▼ " +String.format("%.3f",v));
                    altitude.setBackgroundColor(getColor(R.color.blue));
                    altitude.setTextColor(getColor(R.color.white));
                }
            } else {
                altitude.setText("OFF GRID");
                altitude.setTextColor(getColor(R.color.white));
                altitude.setBackgroundColor(getColor(R.color._____cancel_text));
            }

            distance.setText(strDistance);

            txtAltezzaAnt.setText(String.format("%.3f", DataSaved.D_AltezzaAnt).replace(",", "."));

            if (BT_Conn_GPS.GNSSServiceState) {
                imgConnect.setImageResource(R.drawable.btn_positionpage);
                if (showCoord) {
                    textCoord.setText("Lat: " + My_LocationCalc.decimalToDMS(Nmea_In.mLat_1) + "\tLon: "
                            + My_LocationCalc.decimalToDMS(Nmea_In.mLon_1) + " Z: "
                            + String.format("%.3f", Nmea_In.Quota1).replace(",", "."));
                } else {
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
                } else {
                    txtCq.setText("H:---.-- V:---.--");
                }
                txtHdt.setText("\t" + String.format("%.2f", Nmea_In.tractorBearing).replace(",", "."));
                txtRtk.setText("\t" + Nmea_In.ggaRtk);

            } else {
                imgConnect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                imgConnect.setImageResource(R.drawable.btn_gpsoff);
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
