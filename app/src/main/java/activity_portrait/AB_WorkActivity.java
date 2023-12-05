package activity_portrait;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;

import com.example.stx_field_design.R;

import java.util.Map;

import can.PLC_DataTypes_BigEndian;
import coords_calc.GPS;
import coords_calc.Surface_Selector;
import dialogs.CoordsGNSSInfo;
import gnss.Nmea_In;
import project.DataProjectSingleton;
import project.ProjectCanvas;
import services_and_bluetooth.AutoConnectionService;
import services_and_bluetooth.Bluetooth_CAN_Service;
import services_and_bluetooth.DataSaved;
import utils.MyRW_IntMem;
import utils.Utils;

public class AB_WorkActivity extends AppCompatActivity {
    private boolean mRunning = true;

    public static boolean auto;
    Guideline guideline;
    public static byte page = 0;
    public static byte[] quota;

    ImageView  lineID;
    TextView altitude, distance, fileName;

    ConstraintLayout container;
    ProjectCanvas canvas;
    ImageButton center, zoomIn, zoomOut, rotateLeft, rotateRight, autorotate;
    Button crs, delaunay, surfaceStatus, surfaceOK;
    DataProjectSingleton dataProject;
    CoordsGNSSInfo coordsGNSSInfo;


    Handler handler;

    Surface_Selector surfaceSelector;
    boolean rotLeft = false;
    boolean rotRight = false;
    boolean zommaIn = false;
    boolean zommaOut = false;
    static int idData = 0x6FA;//pacchetto dati


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        init();
        onClick();
        updateUI();

    }

    private void findView() {



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

        surfaceOK = findViewById(R.id.surfaceOK);
        fileName = findViewById(R.id.fileName);

        rotateLeft = findViewById(R.id.rotateLeft);
        rotateRight = findViewById(R.id.rotateRight);
        autorotate = findViewById(R.id.autorotate);


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

        autorotate.setOnClickListener(v -> {
            auto = !auto;
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



        delaunay.setOnClickListener((View v) -> {
            dataProject.toggleDelaunay();
        });



    }
    public void metodoLineId(){
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
    }
    public void metodoOpenList(){
        if (!coordsGNSSInfo.dialog.isShowing())
            coordsGNSSInfo.show();
    }
    public void metodoBack(){
        new MyRW_IntMem().MyWrite("zoomF", String.valueOf(dataProject.mScaleFactor), this);
        new MyRW_IntMem().MyWrite("rot", String.valueOf(dataProject.rotate), this);
        if (auto) {
            new MyRW_IntMem().MyWrite("_maprotmode", "1", this);
        } else {
            new MyRW_IntMem().MyWrite("_maprotmode", "0", this);
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateUI() {

        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRunning) {

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
                        @SuppressLint("NewApi")
                        @Override
                        public void run() {







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




                            }


                            String strDistance = "DIST: " + Utils.readUnitOfMeasure(String.valueOf(surfaceSelector.getDistance()),AB_WorkActivity.this).replace(",", ".") +" "+Utils.getMetriSimbol(AB_WorkActivity.this);
                            if (Math.abs(surfaceSelector.getDistance()) <= DataSaved.xy_tol) {
                                distance.setBackgroundColor(getColor(R.color.green));
                                distance.setTextColor(getColor(R.color._____cancel_text));
                            } else {
                                distance.setBackgroundColor(getColor(R.color._____cancel_text));
                                distance.setTextColor(getColor(R.color.white));
                            }

                            if (surfaceSelector.isPointInsideSurface()) {
                                if (Math.abs(v) <= DataSaved.z_tol) {
                                    altitude.setText("⧗ " + Utils.readUnitOfMeasure(String.valueOf(v),AB_WorkActivity.this).replace(",", ".")+" "+Utils.getMetriSimbol(AB_WorkActivity.this));
                                    altitude.setBackgroundColor(getColor(R.color.green));
                                    altitude.setTextColor(getColor(R.color._____cancel_text));
                                } else if (v < -(DataSaved.z_tol + 0.001)) {
                                    altitude.setText("▲ " + Utils.readUnitOfMeasure(String.valueOf(v),AB_WorkActivity.this).replace(",", ".")+" "+Utils.getMetriSimbol(AB_WorkActivity.this));
                                    altitude.setBackgroundColor(getColor(R.color.red));
                                    altitude.setTextColor(getColor(R.color.white));
                                } else if (v > DataSaved.z_tol + 0.001) {
                                    altitude.setText("▼ " + Utils.readUnitOfMeasure(String.valueOf(v),AB_WorkActivity.this).replace(",", ".")+" "+Utils.getMetriSimbol(AB_WorkActivity.this));
                                    altitude.setBackgroundColor(getColor(R.color.blue));
                                    altitude.setTextColor(getColor(R.color.white));
                                }
                            } else {
                                altitude.setText("OFF GRID");
                                altitude.setTextColor(getColor(R.color.white));
                                altitude.setBackgroundColor(getColor(R.color._____cancel_text));
                            }

                            distance.setText(strDistance);



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




    @Override
    protected void onDestroy() {
        mRunning = false;
        page = 0;
        super.onDestroy();
        dataProject.clearData();


    }
}
