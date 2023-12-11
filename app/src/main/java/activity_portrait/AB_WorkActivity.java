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

import can.Can_Decoder;
import can.PLC_DataTypes_BigEndian;
import coords_calc.GPS;
import coords_calc.Surface_Selector;
import dialogs.CoordsGNSSInfo;
import dialogs.Dialog_Offset;
import gnss.Nmea_In;
import project.DataProjectSingleton;
import project.ProjectCanvas;
import services_and_bluetooth.AutoConnectionService;
import services_and_bluetooth.Bluetooth_CAN_Service;
import services_and_bluetooth.DataSaved;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;
import utils.Utils;

public class AB_WorkActivity extends AppCompatActivity {
    private boolean mRunning = true;

    public static boolean auto;
    Guideline guideline;
    public static byte page = 0;
    public static byte[] quota;

    ImageView lineID;
    TextView altitude, distance, fileName, setOffset, offsetUnit;

    ConstraintLayout container;
    ProjectCanvas canvas;
    ImageButton center, zoomIn, zoomOut, rotateLeft, rotateRight, autorotate;
    Button crs, surfaceStatus, surfaceOK;
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
        dataProject.setDistanceID(new MyRW_IntMem().MyRead("_pointselected",this));

    }

    private void findView() {


        container = findViewById(R.id.container_draw);
        center = findViewById(R.id.myCenterNav);
        zoomIn = findViewById(R.id.myZoomIn);
        zoomOut = findViewById(R.id.myZoomOut);
        surfaceStatus = findViewById(R.id.surfaceStatus);
        crs = findViewById(R.id.img_crs);

        lineID = findViewById(R.id.pickPoint);
        altitude = findViewById(R.id.quota);
        distance = findViewById(R.id.distance);

        surfaceOK = findViewById(R.id.surfaceOK);
        fileName = findViewById(R.id.fileName);

        rotateLeft = findViewById(R.id.rotateLeft);
        rotateRight = findViewById(R.id.rotateRight);
        autorotate = findViewById(R.id.autorotate);
        setOffset = findViewById(R.id.set_offset);
        offsetUnit = findViewById(R.id.txt_unitaoffset);

    }

    private void init() {
        if (MyApp.screenWidth > 400) {
            altitude.setTextSize(26f);
            distance.setTextSize(26f);
        }


        dataProject = DataProjectSingleton.getInstance();
        surfaceStatus.setClickable(false);
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
        updateOffset();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onClick() {

        setOffset.setOnClickListener(view -> {
            new Dialog_Offset(this, 0).show();

        });

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


    }

    public void metodoLineId() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Choose ID");
        String[] items = new String[dataProject.getSize() + 1];
        items[0] = "AB Line";
        int counter = 1;
        for (Map.Entry<String, GPS> entry : dataProject.getPoints().entrySet()) {
            items[counter++] = entry.getKey();
        }
        int selected=-1;
        switch (dataProject.getDistanceID()){
            case "AB Line":
                selected=0;
                break;
            case "A":
                selected=1;
                break;
            case "B":
                selected=2;
                break;
            case "C":
                selected=3;
                break;
            case "D":
                selected=4;
                break;
            case "E":
                selected=5;
                break;
            case "F":
                selected=6;
                break;
            default:
                selected=-1;
                break;
        }



        alertDialog.setSingleChoiceItems(items, selected, (dialog, which) -> {
            dataProject.setDistanceID(which <= 0 ? null : items[which]);
            new MyRW_IntMem().MyWrite("_pointselected",items[which],this);
            dialog.dismiss();
        });

        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();

    }

    public void metodoOpenList() {
        if (!coordsGNSSInfo.dialog.isShowing())
            coordsGNSSInfo.show();
    }

    public void metodoBack() {
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



                            surfaceOK.setText(surfaceSelector.isSurfaceOK() ? "YES" : "NO");
                            crs.setText(dataProject.getEpsgCode());


                            surfaceStatus.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), Can_Decoder.auto==1 ? R.color.pure_green : R.color.transparent));
                            surfaceOK.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), surfaceSelector.isSurfaceOK() ? R.color.pure_green : R.color.red));
                            double v = 0;
                            double v2 = 0;
                            v = surfaceSelector.getAltitudeDifference(Nmea_In.mLat_1, Nmea_In.mLon_1, Nmea_In.Quota1)-DataSaved.D_Offset;
                            v2 = surfaceSelector.getDistance();
                            if (Double.isNaN(v)) v = 0;
                            if (Bluetooth_CAN_Service.canIsConnected) {
                                short dataOut = (short) (v * 1000);
                                short dataOut_2 = (short) (v2 * 1000);
                                byte[] data = PLC_DataTypes_BigEndian.S16_to_bytes_be(dataOut);
                                byte[] data2 = PLC_DataTypes_BigEndian.S16_to_bytes_be(dataOut_2);
                                AutoConnectionService.data_6FA = data;
                                AutoConnectionService.data_6FA_2nd = data2;
                                page = 1;


                            }


                            String strDistance = "DIST: " + Utils.readUnitOfMeasure(String.valueOf(surfaceSelector.getDistance()), AB_WorkActivity.this).replace(",", ".") + " " + Utils.getMetriSimbol(AB_WorkActivity.this);
                            if (Math.abs(surfaceSelector.getDistance()) <= DataSaved.xy_tol) {
                                distance.setBackgroundColor(getColor(R.color.green));
                                distance.setTextColor(getColor(R.color._____cancel_text));
                            } else {
                                distance.setBackgroundColor(getColor(R.color._____cancel_text));
                                distance.setTextColor(getColor(R.color.white));
                            }

                            if (surfaceSelector.isPointInsideSurface()) {
                                if (Math.abs(v) <= DataSaved.z_tol) {
                                    altitude.setText("⧗ " + Utils.readUnitOfMeasure(String.valueOf(v), AB_WorkActivity.this).replace(",", ".") + " " + Utils.getMetriSimbol(AB_WorkActivity.this));
                                    altitude.setBackgroundColor(getColor(R.color.green));
                                    altitude.setTextColor(getColor(R.color._____cancel_text));
                                } else if (v < -(DataSaved.z_tol + 0.001)) {
                                    altitude.setText("▲ " + Utils.readUnitOfMeasure(String.valueOf(v), AB_WorkActivity.this).replace(",", ".") + " " + Utils.getMetriSimbol(AB_WorkActivity.this));
                                    altitude.setBackgroundColor(getColor(R.color.red));
                                    altitude.setTextColor(getColor(R.color.white));
                                } else if (v > DataSaved.z_tol + 0.001) {
                                    altitude.setText("▼ " + Utils.readUnitOfMeasure(String.valueOf(v), AB_WorkActivity.this).replace(",", ".") + " " + Utils.getMetriSimbol(AB_WorkActivity.this));
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

    public void updateOffset() {
        offsetUnit.setText(Utils.getMetriSimbol(this));
        setOffset.setText(Utils.readUnitOfMeasure(String.valueOf(DataSaved.D_Offset), this));
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
