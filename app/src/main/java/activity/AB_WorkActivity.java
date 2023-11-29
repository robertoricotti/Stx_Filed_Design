package activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;

import com.example.stx_field_design.R;

import java.util.Map;

import can.Can_Decoder;
import can.PLC_DataTypes_BigEndian;
import coords_calc.GPS;
import coords_calc.Surface_Selector;
import dialogs.ConnectDialog;
import dialogs.CoordsGNSSInfo;
import gnss.My_LocationCalc;
import gnss.Nmea_In;
import project.DataProjectSingleton;
import project.ProjectCanvas;
import services_and_bluetooth.AutoConnectionService;
import services_and_bluetooth.Bluetooth_CAN_Service;
import services_and_bluetooth.Bluetooth_GNSS_Service;
import services_and_bluetooth.DataSaved;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;

public class AB_WorkActivity extends AppCompatActivity {
    private boolean mRunning = true;
    boolean showCoord = false;
    public static boolean auto;
    Guideline guideline;
    public static byte page = 0;
    public static byte[] quota;
    TextView textCoord, txtSat, txtFix, txtCq, txtHdt, txtAltezzaAnt, txtRtk, txt_incl;
    ImageView back, openList, imgConnect, lineID, canconnect, imgPick, imgSign, imgSat, imgHdt;
    TextView altitude, distance, fileName;

    ConstraintLayout container;
    ProjectCanvas canvas;
    ImageButton center, zoomIn, zoomOut, rotateLeft, rotateRight, autorotate;
    Button crs, delaunay, surfaceStatus, surfaceOK;
    DataProjectSingleton dataProject;
    CoordsGNSSInfo coordsGNSSInfo;


    Handler handler;
    Runnable updateRunnable;
    Surface_Selector surfaceSelector;
    boolean rotLeft = false;
    boolean rotRight = false;
    boolean zommaIn = false;
    boolean zommaOut = false;
    static int idData = 0x6FA;//pacchetto dati


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

        rotateLeft = findViewById(R.id.rotateLeft);
        rotateRight = findViewById(R.id.rotateRight);
        autorotate = findViewById(R.id.autorotate);
        txt_incl = findViewById(R.id.txt_incl);
        canconnect = findViewById(R.id.canconnect);
        imgPick = findViewById(R.id.imgPick);
        imgSign = findViewById(R.id.imgsign);
        imgSat = findViewById(R.id.imgSat);
        imgHdt = findViewById(R.id.imgHdt);
        guideline = findViewById(R.id.H_top_10);

    }

    private void init() {

        delaunay.setVisibility(View.INVISIBLE);
        dataProject = DataProjectSingleton.getInstance();

        surfaceSelector = new Surface_Selector(dataProject.getSize());


        canvas = new ProjectCanvas(this);
        container.addView(canvas);

        coordsGNSSInfo = new CoordsGNSSInfo(this);
        if (dataProject.getSize() > 1) {
            dataProject.toggleDelaunay();
        }
        dataProject.mScaleFactor = Float.parseFloat(new MyRW_IntMem().MyRead("zoomF", this));
        dataProject.rotate = (float) Double.parseDouble(new MyRW_IntMem().MyRead("rot", this));
        if (new MyRW_IntMem().MyRead("_maprotmode", this).equals("0")) {
            auto = false;
        } else if (new MyRW_IntMem().MyRead("_maprotmode", this).equals("1")) {
            auto = true;
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void onClick() {
        canconnect.setOnClickListener(view -> {
            new ConnectDialog(this, 2).show();
        });
        autorotate.setOnClickListener(v -> {
            auto = !auto;
        });
        imgConnect.setOnClickListener(view -> {
            new ConnectDialog(this, 1).show();

        });
        back.setOnClickListener((View v) -> {
            new MyRW_IntMem().MyWrite("zoomF", String.valueOf(dataProject.mScaleFactor), this);
            new MyRW_IntMem().MyWrite("rot", String.valueOf(dataProject.rotate), this);
            if (auto) {
                new MyRW_IntMem().MyWrite("_maprotmode", "1", this);
            } else {
                new MyRW_IntMem().MyWrite("_maprotmode", "0", this);
            }
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
            //dataProject.setmScaleFactor(0.5f);
            canvas.invalidate();
        });


        zoomIn.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                zommaIn = true;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                zommaIn = false;
            }
            return true;
        });
        zoomOut.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                zommaOut = true;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                zommaOut = false;
            }
            return true;
        });


        rotateRight.setOnTouchListener((view, motionEvent) -> {

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                rotRight = true;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                rotRight = false;
            }

            return false;
        });
        rotateLeft.setOnTouchListener((view, motionEvent) -> {

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                rotLeft = true;
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                rotLeft = false;
            }

            return false;
        });

        lineID.setOnClickListener((View v) -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Choose ID");

            String[] items = new String[dataProject.getSize() + 1];
            items[0] = "AB Line";

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


        textCoord.setOnClickListener(view -> {
            showCoord = !showCoord;
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateUI() {

        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRunning) {
                    Log.d("ZOMMA", String.valueOf(dataProject.mScaleFactor));
                    if (zommaOut) {
                        zommaIn = false;
                        if (dataProject.mScaleFactor > 0.04f) {
                            dataProject.mScaleFactor -= 0.01f;

                        }
                    }
                    if (zommaIn) {
                        zommaOut = false;
                        dataProject.mScaleFactor += 0.01f;

                    }
                    if (rotRight) {
                        rotLeft = false;
                        if (dataProject.rotate <= 360f) {
                            dataProject.rotate += 2f;

                        } else {
                            dataProject.rotate = 0f;
                        }
                    }
                    if (rotLeft) {
                        rotRight = false;
                        if (dataProject.rotate >= 1f) {
                            dataProject.rotate -= 2f;

                        } else {
                            dataProject.rotate = 360f;

                        }

                    }


                    handler.post(new Runnable() {
                        @Override
                        public void run() {

//////////////////////////////////


                            Log.d("DataScale", String.valueOf(dataProject.mScaleFactor));


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
                            try {
                                fileName.setText(dataProject.getProjectName().replace(".csv", ""));

                            } catch (Exception e) {
                                fileName.setText(" ");
                            }


                            surfaceStatus.setText(surfaceSelector.isPointInsideSurface() ? "IN" : "OUT");
                            surfaceOK.setText(surfaceSelector.isSurfaceOK() ? "YES" : "NO");
                            crs.setText(dataProject.getEpsgCode());


                            delaunay.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), dataProject.isDelaunay() ? R.color.pure_green : R.color._____cancel_text));
                            surfaceStatus.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), surfaceSelector.isPointInsideSurface() ? R.color.pure_green : R.color.red));
                            surfaceOK.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), surfaceSelector.isSurfaceOK() ? R.color.pure_green : R.color.red));
                            double v = 0;
                            v = surfaceSelector.getAltitudeDifference(Nmea_In.mLat_1, Nmea_In.mLon_1, Nmea_In.Quota1);
                            if (Double.isNaN(v)) v = 0;
                            if (Bluetooth_CAN_Service.canIsConnected) {
                                int dataOut = (int) (v * 1000);
                                byte[] data = PLC_DataTypes_BigEndian.S32_to_bytes_be(dataOut);
                                AutoConnectionService.data_6FA = data;
                                page = 1;

                                txt_incl.setText(String.valueOf("Pitch: " + String.format("%.2f", Can_Decoder.correctPitch).replace(",", ".") + "°       Roll: " + String.format("%.2f", Can_Decoder.correctRoll).replace(",", ".") + "°"));
                                canconnect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.green));
                                canconnect.setImageResource(R.drawable.btn_ecu_connect);
                                if (Math.abs(Can_Decoder.correctPitch) <= DataSaved.tilt_Tol && Math.abs(Can_Decoder.correctRoll) <= DataSaved.tilt_Tol) {
                                    txt_incl.setTextColor(Color.parseColor("#008000"));
                                } else {
                                    txt_incl.setTextColor(Color.BLACK);
                                }

                            } else {
                                txt_incl.setText(String.valueOf("CAN DISCONNECTED"));
                                canconnect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color._____cancel_text));
                                canconnect.setImageResource(R.drawable.btn_can_disconn);
                                txt_incl.setTextColor(Color.RED);
                            }


                            String strDistance = "DIST: " + String.format("%.3f", surfaceSelector.getDistance()).replace(",", ".") + " m";
                            if (Math.abs(surfaceSelector.getDistance()) <= DataSaved.xy_tol) {
                                distance.setBackgroundColor(getColor(R.color.green));
                                distance.setTextColor(getColor(R.color._____cancel_text));
                            } else {
                                distance.setBackgroundColor(getColor(R.color._____cancel_text));
                                distance.setTextColor(getColor(R.color.white));
                            }

                            if (surfaceSelector.isPointInsideSurface()) {
                                if (Math.abs(v) <= DataSaved.z_tol) {
                                    altitude.setText("⧗ " + String.format("%.3f", v).replace(",", "."));
                                    altitude.setBackgroundColor(getColor(R.color.green));
                                    altitude.setTextColor(getColor(R.color._____cancel_text));
                                } else if (v < -(DataSaved.z_tol + 0.001)) {
                                    altitude.setText("▲ " + String.format("%.3f", v).replace(",", "."));
                                    altitude.setBackgroundColor(getColor(R.color.red));
                                    altitude.setTextColor(getColor(R.color.white));
                                } else if (v > DataSaved.z_tol + 0.001) {
                                    altitude.setText("▼ " + String.format("%.3f", v).replace(",", "."));
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
                                imgConnect.setImageResource(R.drawable.btn_positionpage);


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
                                            imgConnect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color._____cancel_text));
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
                                imgConnect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color._____cancel_text));
                                imgConnect.setImageResource(R.drawable.btn_gpsoff);
                                textCoord.setTextColor(Color.RED);
                                txtSat.setText("\t" + Nmea_In.ggaSat);
                                txtFix.setText("---");
                                txtCq.setText("H:---.-- V:---.--");
                                txtHdt.setText("---.--");
                                txtRtk.setText("----");
                            }

                            canvas.invalidate();


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


    @Override
    public void onBackPressed() {
    }

    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Qui puoi eseguire le azioni necessarie quando avviene una rotazione
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imgConnect.setPadding(0, 15, 0, 15);
            txtRtk.setVisibility(View.GONE);
            txtCq.setVisibility(View.GONE);
            imgSign.setVisibility(View.GONE);
            imgPick.setVisibility(View.GONE);
            imgSat.setVisibility(View.GONE);
            imgHdt.setVisibility(View.GONE);
            txtSat.setVisibility(View.GONE);
            txtHdt.setVisibility(View.GONE);
            guideline.setGuidelinePercent(0.08f);
            // Esegui azioni per l'orientamento orizzontale
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Esegui azioni per l'orientamento verticale
            imgConnect.setPadding(0, 35, 0, 35);
            txtRtk.setVisibility(View.VISIBLE);
            txtCq.setVisibility(View.VISIBLE);
            imgSign.setVisibility(View.VISIBLE);
            imgPick.setVisibility(View.VISIBLE);
            imgSat.setVisibility(View.VISIBLE);
            imgHdt.setVisibility(View.VISIBLE);
            txtSat.setVisibility(View.VISIBLE);
            txtHdt.setVisibility(View.VISIBLE);
            guideline.setGuidelinePercent(0.11f);

        }
    }

    @Override
    protected void onDestroy() {
        mRunning = false;
        page = 0;
        super.onDestroy();
        dataProject.clearData();


    }
}
