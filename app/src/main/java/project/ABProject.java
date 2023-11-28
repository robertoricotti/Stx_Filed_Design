package project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.stx_field_design.R;

import coords_calc.DistToPoint;
import coords_calc.EasyPointCalculator;
import coords_calc.GPS;
import dialogs.Confirm_Dialog;
import dialogs.ConnectDialog;
import dialogs.MyEpsgDialog;
import dialogs.SaveFileDialog;
import gnss.My_LocationCalc;
import gnss.Nmea_In;
import services_and_bluetooth.Bluetooth_GNSS_Service;
import services_and_bluetooth.DataSaved;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;
import utils.Utils;

public class ABProject extends AppCompatActivity {
    private boolean zommaIn = false;
    private boolean zommaOut = false;
    public ProgressBar progressBar;
    private static int passCode = -1;
    private boolean showCoord = false;
    TextView txtSat, txtFix, txtCq, txtHdt, txtAltezzaAnt, txtRtk;
    ImageView imgConnect;
    EditText et_zb, et_dst, et_slope, et_ltdst, et_ltslope, et_rtdst, et_rtslope;
    ConstraintLayout container_draw;
    public ImageView back, pick, save, calcola;

    ImageButton center, zoomIn, zoomOut;
    ImageButton clear;
    Button crs;
    TextView textCoord;

    int pickIndex;

    DataProjectSingleton dataProject;
    ABCanvas canvas;

    MyEpsgDialog myEpsgDialog;
    SaveFileDialog saveFileDialog;
    Handler handler;
    Runnable updateRunnable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ab_project);
        FullscreenActivity.setFullScreen(this);
        findView();
        init();
        onClick();
        updateUI();
    }

    private void findView() {
        back = findViewById(R.id.back);
        pick = findViewById(R.id.pickPoint);
        crs = findViewById(R.id.crs);
        textCoord = findViewById(R.id.txt_coord);
        container_draw = findViewById(R.id.container_draw);
        save = findViewById(R.id.save);
        center = findViewById(R.id.centerNavigator);
        zoomIn = findViewById(R.id.zoomIn);
        zoomOut = findViewById(R.id.zoomOut);
        clear = findViewById(R.id.delete);
        txtSat = findViewById(R.id.txt_satnr);
        txtFix = findViewById(R.id.txt_quality);
        txtCq = findViewById(R.id.txt_precision);
        txtHdt = findViewById(R.id.txt_hdt);
        txtAltezzaAnt = findViewById(R.id.txt_speed);
        txtRtk = findViewById(R.id.txt_rtk);
        imgConnect = findViewById(R.id.img_connetti);

        et_zb = findViewById(R.id.et_zb);
        et_dst = findViewById(R.id.et_dst);
        et_slope = findViewById(R.id.et_slope);
        et_ltdst = findViewById(R.id.et_ltdst);
        et_ltslope = findViewById(R.id.et_ltslope);
        et_rtdst = findViewById(R.id.et_rtdst);
        et_rtslope = findViewById(R.id.et_rtslope);
        calcola = findViewById(R.id.calcola);
        progressBar = findViewById(R.id.progressBar);


    }

    private void init() {
        save.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        dataProject = DataProjectSingleton.getInstance();
        myEpsgDialog = new MyEpsgDialog(this);
        saveFileDialog = new SaveFileDialog(this);
        canvas = new ABCanvas(this);
        container_draw.addView(canvas);
        pickIndex = 0;
        dataProject.mScaleFactor = Float.parseFloat(new MyRW_IntMem().MyRead("zoomF", this));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onClick() {
        calcola.setOnClickListener(view -> {
            new Confirm_Dialog(ABProject.this, -1).show();

        });
        et_zb.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                passCode = 1;
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                calcZ();
                return true;
            }
            return false;
        });
        et_dst.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                passCode = 2;
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                calc2();
                return true;
            }
            return false;
        });
        et_slope.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                passCode = 3;
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                calc3();
                return true;
            }
            return false;
        });
        et_ltdst.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                passCode = 4;
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                calc4();
                return true;
            }
            return false;
        });
        et_ltslope.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                passCode = 5;
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                calc5();
                return true;
            }
            return false;
        });
        et_rtdst.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                passCode = 6;
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                calc6();
                return true;
            }
            return false;
        });
        et_rtslope.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                passCode = 7;
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                calc7();
                return true;
            }
            return false;
        });


        imgConnect.setOnClickListener(view -> {
            new ConnectDialog(this, 1).show();

        });
        textCoord.setOnClickListener(view -> {
            showCoord = !showCoord;
        });
        back.setOnClickListener((View v) -> {
            new MyRW_IntMem().MyWrite("zoomF", String.valueOf(dataProject.mScaleFactor), this);
            startActivity(new Intent(this, MenuProject.class));

            finish();
        });

        save.setOnClickListener((View v) -> {

            if (dataProject.getSize() == 4 || dataProject.getSize() == 6) {

                if (!saveFileDialog.dialog.isShowing())
                    saveFileDialog.show();
            } else {
                Toast.makeText(this, "Points not available!", Toast.LENGTH_SHORT).show();
            }
        });

        pick.setOnClickListener((View v) -> {

            pickIndex++;

            dataProject.setEpsgCode(DataSaved.S_CRS, ABProject.this);


            if (dataProject.getSize() == 0 && pickIndex == 1) {
                GPS gps = new GPS(Nmea_In.mLat_1, Nmea_In.mLon_1, Nmea_In.Quota1, DataSaved.S_CRS);
                dataProject.addCoordinate("A", gps);

            }

            if (dataProject.getSize() == 1 && pickIndex == 2) {
                GPS gps = new GPS(Nmea_In.mLat_1, Nmea_In.mLon_1, Nmea_In.Quota1, DataSaved.S_CRS);
                dataProject.addCoordinate("B", gps);

                GPS a = dataProject.getPoints().get("A");

                GPS b = dataProject.getPoints().get("B");
                dataProject.setDistanceAB(new DistToPoint(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ()).getDist_to_point());
                double orientamento = My_LocationCalc.calcBearingXY(a.getX(), a.getY(), b.getX(), b.getY());

                double orRight = orientamento + 90;

                if (orRight < -180) {
                    orRight += 360;
                } else if (orRight > 180) {
                    orRight -= 360;
                }
                double orLeft = orientamento - 90;
                if (orLeft < -180) {
                    orLeft += 360;
                } else if (orLeft > 180) {
                    orLeft -= 360;
                }
                double slopeAB = Utils.slopeCalculator(a, b);
                double[] c = new EasyPointCalculator(new double[]{b.getX(), b.getY(), b.getZ()}).calculateEndPoint(dataProject.getRtSlope(), orRight, dataProject.getRtLength());

                double[] d = new EasyPointCalculator(new double[]{a.getX(), a.getY(), a.getZ()}).calculateEndPoint(dataProject.getRtSlope(), orRight, dataProject.getRtLength());

                double[] e = new EasyPointCalculator(new double[]{b.getX(), b.getY(), b.getZ()}).calculateEndPoint(dataProject.getLtSlope(), orLeft, dataProject.getLtLength());

                double[] f = new EasyPointCalculator(new double[]{a.getX(), a.getY(), a.getZ()}).calculateEndPoint(dataProject.getLtSlope(), orLeft, dataProject.getLtLength());

                dataProject.addCoordinate("C", new GPS(dataProject.getEpsgCode(), c[0], c[1], c[2]));
                dataProject.addCoordinate("D", new GPS(dataProject.getEpsgCode(), d[0], d[1], d[2]));
                dataProject.addCoordinate("E", new GPS(dataProject.getEpsgCode(), e[0], e[1], e[2]));
                dataProject.addCoordinate("F", new GPS(dataProject.getEpsgCode(), f[0], f[1], f[2]));
                dataProject.setzB(b.getZ());
                dataProject.setSlopeAB(slopeAB);

                updateAll();


            }

            if (pickIndex >= 3) {
                Toast.makeText(this, "Limit Exceed!", Toast.LENGTH_SHORT).show();
                pickIndex--;
            }


        });

        clear.setOnLongClickListener((View v) -> {
            if (dataProject.getSize() > 0) {
                if (dataProject.getSize() == 1) {
                    dataProject.deleteAllCoordinate();
                    pickIndex = 0;
                    Toast.makeText(this, dataProject.getSize() + " Nr of Points", Toast.LENGTH_SHORT).show();
                } else if (dataProject.getSize() == 2) {
                    dataProject.deleteCoordinate("B");
                    pickIndex = 1;
                    Toast.makeText(this, dataProject.getSize() + " Nr of Points", Toast.LENGTH_SHORT).show();
                } else if (dataProject.getSize() == 6) {
                    dataProject.deleteCoordinate("C");
                    dataProject.deleteCoordinate("D");
                    dataProject.deleteCoordinate("E");
                    dataProject.deleteCoordinate("F");
                    pickIndex = 2;
                    Toast.makeText(this, dataProject.getSize() + " Nr of Points", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, dataProject.getSize() + " Nr of Points", Toast.LENGTH_SHORT).show();
            }
            return false;
        });


        crs.setOnClickListener((View v) -> {
            if (!myEpsgDialog.dialog.isShowing())
                myEpsgDialog.show();
        });

        center.setOnClickListener((View v) -> {
            dataProject.setOffsetX(0);
            dataProject.setOffsetY(0);
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



        textCoord.setOnClickListener((View v) -> {
            showCoord = !showCoord;
        });
    }


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateUI() {
        handler = new Handler();
        updateRunnable = () -> {
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
            if (pickIndex < 2) {
                pick.setVisibility(View.VISIBLE);
                save.setVisibility(View.INVISIBLE);
            }
            crs.setText(dataProject.getEpsgCode() != null ? dataProject.getEpsgCode() : DataSaved.S_CRS);


            canvas.invalidate();
            aggiornaCoordinate();

            handler.postDelayed(updateRunnable, 100);

        };
        handler.post(updateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
            dataProject.clearData();
        }
    }


    public void calcZ() {
        String s = "0";
        double d = 0;
        if (!(et_zb.getText().toString() == null) && !et_zb.getText().toString().equals("")) {
            s = et_zb.getText().toString();
            d = Double.parseDouble(s);
            dataProject.setzB(d);
        }
        if (dataProject.getSize() >= 2) {
            GPS myA = dataProject.getPoints().get("A");//coordinate misurate di A
            GPS myB = dataProject.getPoints().get("B");//coordinate misurate di B
            dataProject.updateCoordinate("B", new GPS(DataSaved.S_CRS, myB.getX(), myB.getY(), dataProject.getzB()));
            GPS my_new_B = dataProject.getPoints().get("B");//coordinate misurate di B
            double ab = new DistToPoint(myA.getX(), myA.getY(), myA.getZ(), my_new_B.getX(), my_new_B.getY(), my_new_B.getZ()).getDist_to_point();
            dataProject.setDistanceAB(ab);
            dataProject.setSlopeAB(Utils.slopeCalculator(myA, my_new_B));
            updateAll();


        }


    }

    public void calc2() {
        String s = "0";
        double d = 0;
        if (!(et_dst.getText().toString() == null) && !et_dst.getText().toString().equals("")) {
            s = et_dst.getText().toString();
            d = Double.parseDouble(s);
            dataProject.setDistanceAB(d);
        }
        if (dataProject.getSize() >= 2) {
            GPS myA = dataProject.getPoints().get("A");//coordinate misurate di A
            GPS myB = dataProject.getPoints().get("B");//coordinate misurate di B
            double[] np = new EasyPointCalculator(new double[]{myA.getX(), myA.getY(), myA.getZ()}).calculateEndPoint(dataProject.getSlopeAB(), dataProject.abOrient(), d);
            dataProject.updateCoordinate("B", new GPS(DataSaved.S_CRS, np[0], np[1], np[2]));
            updateAll();

        }


    }

    public void calc3() {
        String s = "0";
        double d = 0;
        if (!(et_slope.getText().toString() == null) && !et_slope.getText().toString().equals("")) {
            s = et_slope.getText().toString();
            d = Double.parseDouble(s);
            dataProject.setSlopeAB(d);
        }
        if (dataProject.getSize() >= 2) {
            GPS myA = dataProject.getPoints().get("A");//coordinate misurate di A
            GPS myB = dataProject.getPoints().get("B");//coordinate misurate di B
            double[] np = new EasyPointCalculator(new double[]{myA.getX(), myA.getY(), myA.getZ()}).calculateEndPoint(dataProject.getSlopeAB(), dataProject.abOrient(), dataProject.getDistanceAB());
            dataProject.updateCoordinate("B", new GPS(DataSaved.S_CRS, np[0], np[1], np[2]));
            updateAll();

        }


    }

    public void calc4() {
        String s = "0";
        double d = 0;
        if (!(et_ltdst.getText().toString() == null) && !et_ltdst.getText().toString().equals("")) {
            s = et_ltdst.getText().toString();
            d = Double.parseDouble(s);
            dataProject.setLtLength(d);
        }
        if (dataProject.getSize() >= 2) {
            falde();
        }

    }

    public void calc5() {
        String s = "0";
        double d = 0;
        if (!(et_ltslope.getText().toString() == null) && !et_ltslope.getText().toString().equals("")) {
            s = et_ltslope.getText().toString();
            d = Double.parseDouble(s);
            dataProject.setLtSlope(d);
        }
        if (dataProject.getSize() >= 2) {
            falde();
        }

    }

    public void calc6() {
        String s = "0";
        double d = 0;
        if (!(et_rtdst.getText().toString() == null) && !et_rtdst.getText().toString().equals("")) {
            s = et_rtdst.getText().toString();
            d = Double.parseDouble(s);
            dataProject.setRtLength(d);
        }
        if (dataProject.getSize() >= 2) {
            falde();
        }
    }

    public void calc7() {
        String s = "0";
        double d = 0;
        if (!(et_rtslope.getText().toString() == null) && !et_rtslope.getText().toString().equals("")) {
            s = et_rtslope.getText().toString();
            d = Double.parseDouble(s);
            dataProject.setRtSlope(d);
        }
        if (dataProject.getSize() >= 2) {
            falde();
        }
    }

    public void falde() {
        if (Double.parseDouble(et_ltdst.getText().toString()) == 0) {
            //se la larghezza sinistra è 0 i punti E ed F vengono sovrapposti rispettivamente ad B e A
            dataProject.setLtLength(0);
            dataProject.setLtSlope(0);
            dataProject.points.put("F", dataProject.getPoints().get("A"));
            dataProject.points.put("E", dataProject.getPoints().get("B"));
        } else {
            GPS a = dataProject.getPoints().get("A");
            GPS b = dataProject.getPoints().get("B");

            dataProject.setLtLength(Double.parseDouble(et_ltdst.getText().toString()));
            dataProject.setLtSlope(Double.parseDouble(et_ltslope.getText().toString()));

            double orientamento1 = My_LocationCalc.calcBearingXY(a.getX(), a.getY(), b.getX(), b.getY());
            double orLeft = orientamento1 - 90;
            if (orLeft < -180) {
                orLeft += 360;
            } else if (orLeft > 180) {
                orLeft -= 360;
            }

            double[] e = new EasyPointCalculator(new double[]{b.getX(), b.getY(), b.getZ()}).calculateEndPoint(dataProject.getLtSlope(), orLeft, dataProject.getLtLength());
            double[] f = new EasyPointCalculator(new double[]{a.getX(), a.getY(), a.getZ()}).calculateEndPoint(dataProject.getLtSlope(), orLeft, dataProject.getLtLength());
            dataProject.updateCoordinate("E", new GPS(dataProject.getEpsgCode(), e[0], e[1], e[2]));
            dataProject.updateCoordinate("F", new GPS(dataProject.getEpsgCode(), f[0], f[1], f[2]));
        }


        if (Double.parseDouble(et_rtdst.getText().toString()) == 0) {
            //se la larghezza destra è 0 i punti  C e D vengono sovrapposti rispettivamente ad B e A
            dataProject.setRtLength(0);
            dataProject.setRtSlope(0);
            dataProject.points.put("C", dataProject.getPoints().get("B"));
            dataProject.points.put("D", dataProject.getPoints().get("A"));

        } else {
            GPS a = dataProject.getPoints().get("A");
            GPS b = dataProject.getPoints().get("B");

            dataProject.setRtLength(Double.parseDouble(et_rtdst.getText().toString()));
            dataProject.setRtSlope(Double.parseDouble(et_rtslope.getText().toString()));

            double orientamento = My_LocationCalc.calcBearingXY(a.getX(), a.getY(), b.getX(), b.getY());

            double orRight = orientamento + 90;

            if (orRight < -180) {
                orRight += 360;
            } else if (orRight > 180) {
                orRight -= 360;
            }

            double[] c = new EasyPointCalculator(new double[]{b.getX(), b.getY(), b.getZ()}).calculateEndPoint(dataProject.getRtSlope(), orRight, dataProject.getRtLength());
            double[] d = new EasyPointCalculator(new double[]{a.getX(), a.getY(), a.getZ()}).calculateEndPoint(dataProject.getRtSlope(), orRight, dataProject.getRtLength());
            dataProject.updateCoordinate("C", new GPS(dataProject.getEpsgCode(), c[0], c[1], c[2]));
            dataProject.updateCoordinate("D", new GPS(dataProject.getEpsgCode(), d[0], d[1], d[2]));
        }
    }

    private void updateAll() {
        GPS b = dataProject.getPoints().get("B");
        et_zb.setText(String.format("%.3f", b.getZ()));
        et_dst.setText(String.format("%.3f", dataProject.getDistanceAB()));
        et_slope.setText(String.format("%.1f", dataProject.getSlopeAB()));
        et_ltdst.setText(String.format("%.3f", dataProject.getLtLength()));
        et_ltslope.setText(String.format("%.1f", dataProject.getLtSlope()));
        et_rtdst.setText(String.format("%.3f", dataProject.getRtLength()));
        et_rtslope.setText(String.format("%.1f", dataProject.getRtSlope()));
    }

    private void aggiornaCoordinate() {
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
    }
}
