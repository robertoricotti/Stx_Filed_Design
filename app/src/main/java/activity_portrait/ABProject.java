package activity_portrait;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.stx_field_design.R;

import coords_calc.DistToPoint;
import coords_calc.EasyPointCalculator;
import coords_calc.GPS;
import dialogs.Confirm_Dialog;
import dialogs.MyEpsgDialog;
import dialogs.SaveFileDialog;
import gnss.My_LocationCalc;
import gnss.Nmea_In;
import project.ABCanvas;
import project.DataProjectSingleton;
import services_and_bluetooth.DataSaved;
import utils.MyRW_IntMem;
import utils.Utils;

public class ABProject extends AppCompatActivity {
    private boolean zommaIn = false;
    private boolean zommaOut = false;
    public ProgressBar progressBar;
    private static int passCode = -1;

    TextView um1, um2, um3, um4, um5, um6, um7;

    EditText et_zb, et_dst, et_slope, et_ltdst, et_ltslope, et_rtdst, et_rtslope;
    ConstraintLayout container_draw;


    ImageButton center, zoomIn, zoomOut, imgCrs;
    ImageButton clear;
    TextView crs;
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

        findView();
        init();
        onClick();
        updateUI();
    }

    private void findView() {

        crs = findViewById(R.id.crs);
        textCoord = findViewById(R.id.txt_coord);
        container_draw = findViewById(R.id.container_draw);
        imgCrs = findViewById(R.id.img_crs);
        center = findViewById(R.id.myCenterNav);
        zoomIn = findViewById(R.id.myZoomIn);
        zoomOut = findViewById(R.id.myZoomOut);
        clear = findViewById(R.id.delete);


        et_zb = findViewById(R.id.et_zb);
        et_dst = findViewById(R.id.et_dst);
        et_slope = findViewById(R.id.et_slope);
        et_ltdst = findViewById(R.id.et_ltdst);
        et_ltslope = findViewById(R.id.et_ltslope);
        et_rtdst = findViewById(R.id.et_rtdst);
        et_rtslope = findViewById(R.id.et_rtslope);

        progressBar = findViewById(R.id.progressBar);
        um1 = findViewById(R.id.um_1);
        um2 = findViewById(R.id.um_2);
        um3 = findViewById(R.id.um_3);
        um4 = findViewById(R.id.um_4);
        um5 = findViewById(R.id.um_5);
        um6 = findViewById(R.id.um_6);
        um7 = findViewById(R.id.um_7);


    }

    private void init() {

        progressBar.setVisibility(View.INVISIBLE);
        dataProject = DataProjectSingleton.getInstance();
        myEpsgDialog = new MyEpsgDialog(this);
        saveFileDialog = new SaveFileDialog(this, "AB");
        canvas = new ABCanvas(this);
        container_draw.addView(canvas);
        pickIndex = 0;
        dataProject.mScaleFactor = Float.parseFloat(new MyRW_IntMem().MyRead("zoomF", this));
        um1.setText(Utils.getMetriSimbol(ABProject.this));
        um2.setText(Utils.getMetriSimbol(ABProject.this));
        um3.setText(Utils.getGradiSimbol(ABProject.this));
        um4.setText(Utils.getMetriSimbol(ABProject.this));
        um5.setText(Utils.getGradiSimbol(ABProject.this));
        um6.setText(Utils.getMetriSimbol(ABProject.this));
        um7.setText(Utils.getGradiSimbol(ABProject.this));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onClick() {

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


        imgCrs.setOnClickListener((View v) -> {
          /*  if (!myEpsgDialog.dialog.isShowing())
                myEpsgDialog.show();*/
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


    }


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateUI() {
        handler = new Handler();
        updateRunnable = () -> {

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


            crs.setText("UTM");

            canvas.invalidate();

            handler.postDelayed(updateRunnable, 100);

        };
        handler.post(updateRunnable);
    }

    @Override
    protected void onDestroy() {
        dataProject.clearData();
        super.onDestroy();
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);

        }
    }

    public void metodoCalcola() {
        new Confirm_Dialog(ABProject.this, -1).show();
    }

    public void metodoSave() {
        progressBar.setVisibility(View.VISIBLE);
        (new Handler()).postDelayed(this::calcZ, 500);
        (new Handler()).postDelayed(this::calc2, 100);
        (new Handler()).postDelayed(this::calc3, 100);
        (new Handler()).postDelayed(this::calc4, 100);
        (new Handler()).postDelayed(this::calc5, 100);
        (new Handler()).postDelayed(this::calc6, 100);
        (new Handler()).postDelayed(this::calc7, 100);
        (new Handler()).postDelayed(this::updateAll, 100);
        (new Handler()).postDelayed(this::salvatutto, 100);

    }

    private void salvatutto() {
        progressBar.setVisibility(View.INVISIBLE);
        if (dataProject.getSize() == 4 || dataProject.getSize() == 6) {

            if (!saveFileDialog.dialog.isShowing())
                saveFileDialog.show();
        } else {
            Toast.makeText(this, "Points not available!", Toast.LENGTH_SHORT).show();
        }
    }

    public void metodoBack() {
        new MyRW_IntMem().MyWrite("zoomF", String.valueOf(dataProject.mScaleFactor), this);
    }

    public void metodoPick() {

        pickIndex++;




        if (dataProject.getSize() == 0 && pickIndex == 1) {
            GPS gps = new GPS(Nmea_In.mLat_1, Nmea_In.mLon_1, Nmea_In.Quota1, null);
            dataProject.addCoordinate("A", gps);

        }

        if (dataProject.getSize() == 1 && pickIndex == 2) {
            GPS gps = new GPS(Nmea_In.mLat_1, Nmea_In.mLon_1, Nmea_In.Quota1, null);
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

            dataProject.addCoordinate("C", new GPS(null, c[0], c[1], c[2],Nmea_In.Band,Nmea_In.Zone));
            dataProject.addCoordinate("D", new GPS(null, d[0], d[1], d[2],Nmea_In.Band,Nmea_In.Zone));
            dataProject.addCoordinate("E", new GPS(null, e[0], e[1], e[2],Nmea_In.Band,Nmea_In.Zone));
            dataProject.addCoordinate("F", new GPS(null, f[0], f[1], f[2],Nmea_In.Band,Nmea_In.Zone));
            dataProject.setzB(b.getZ());
            dataProject.setSlopeAB(slopeAB);

            updateAll();


        }

        if (pickIndex >= 3) {
            Toast.makeText(this, "Limit Exceed!", Toast.LENGTH_SHORT).show();
            pickIndex--;
        }

    }


    public void calcZ() {
        String s = "0";
        double d = 0;
        if (!(et_zb.getText().toString() == null) && !et_zb.getText().toString().equals("")) {
            GPS a = dataProject.getPoints().get("A");
            s = Utils.writeMetri(et_zb.getText().toString(), this);
            d = Double.parseDouble(s) + a.getZ();
            dataProject.setzB(d);
        }
        if (dataProject.getSize() >= 2) {
            GPS myA = dataProject.getPoints().get("A");//coordinate misurate di A
            GPS myB = dataProject.getPoints().get("B");//coordinate misurate di B
            dataProject.updateCoordinate("B", new GPS(null, myB.getX(), myB.getY(), dataProject.getzB(),Nmea_In.Band,Nmea_In.Zone));
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
            s = Utils.writeMetri(et_dst.getText().toString(), this);
            d = Double.parseDouble(s);
            dataProject.setDistanceAB(d);
        }
        if (dataProject.getSize() >= 2) {
            GPS myA = dataProject.getPoints().get("A");//coordinate misurate di A
            GPS myB = dataProject.getPoints().get("B");//coordinate misurate di B
            double[] np = new EasyPointCalculator(new double[]{myA.getX(), myA.getY(), myA.getZ()}).calculateEndPoint(dataProject.getSlopeAB(), dataProject.abOrient(), d);
            dataProject.updateCoordinate("B", new GPS(null, np[0], np[1], np[2],Nmea_In.Band,Nmea_In.Zone));
            updateAll();

        }


    }

    public void calc3() {
        String s = "0";
        double d = 0;
        if (!(et_slope.getText().toString() == null) && !et_slope.getText().toString().equals("")) {
            s = Utils.writeGradi(et_slope.getText().toString(), this);
            d = Double.parseDouble(s);
            dataProject.setSlopeAB(d);
        }
        if (dataProject.getSize() >= 2) {
            GPS myA = dataProject.getPoints().get("A");//coordinate misurate di A
            GPS myB = dataProject.getPoints().get("B");//coordinate misurate di B
            double[] np = new EasyPointCalculator(new double[]{myA.getX(), myA.getY(), myA.getZ()}).calculateEndPoint(dataProject.getSlopeAB(), dataProject.abOrient(), dataProject.getDistanceAB());
            dataProject.updateCoordinate("B", new GPS(null, np[0], np[1], np[2],Nmea_In.Band,Nmea_In.Zone));
            updateAll();

        }


    }

    public void calc4() {
        String s = "0";
        double d = 0;
        if (!(et_ltdst.getText().toString() == null) && !et_ltdst.getText().toString().equals("")) {
            s = Utils.writeMetri(et_ltdst.getText().toString(), this);
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
            s = Utils.writeGradi(et_ltslope.getText().toString(), this);
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
            s = Utils.writeMetri(et_rtdst.getText().toString(), this);
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
            s = Utils.writeGradi(et_rtslope.getText().toString(), this);
            ;
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

            dataProject.setLtLength(Double.parseDouble(Utils.writeMetri(et_ltdst.getText().toString(), ABProject.this)));
            dataProject.setLtSlope(Double.parseDouble(Utils.writeGradi(et_ltslope.getText().toString(), ABProject.this)));

            double orientamento1 = My_LocationCalc.calcBearingXY(a.getX(), a.getY(), b.getX(), b.getY());
            double orLeft = orientamento1 - 90;
            if (orLeft < -180) {
                orLeft += 360;
            } else if (orLeft > 180) {
                orLeft -= 360;
            }

            double[] e = new EasyPointCalculator(new double[]{b.getX(), b.getY(), b.getZ()}).calculateEndPoint(dataProject.getLtSlope(), orLeft, dataProject.getLtLength());
            double[] f = new EasyPointCalculator(new double[]{a.getX(), a.getY(), a.getZ()}).calculateEndPoint(dataProject.getLtSlope(), orLeft, dataProject.getLtLength());
            dataProject.updateCoordinate("E", new GPS(null, e[0], e[1], e[2],Nmea_In.Band,Nmea_In.Zone));
            dataProject.updateCoordinate("F", new GPS(null, f[0], f[1], f[2],Nmea_In.Band,Nmea_In.Zone));
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

            dataProject.setRtLength(Double.parseDouble(Utils.writeMetri(et_rtdst.getText().toString(), ABProject.this)));
            dataProject.setRtSlope(Double.parseDouble(Utils.writeGradi(et_rtslope.getText().toString(), ABProject.this)));

            double orientamento = My_LocationCalc.calcBearingXY(a.getX(), a.getY(), b.getX(), b.getY());

            double orRight = orientamento + 90;

            if (orRight < -180) {
                orRight += 360;
            } else if (orRight > 180) {
                orRight -= 360;
            }

            double[] c = new EasyPointCalculator(new double[]{b.getX(), b.getY(), b.getZ()}).calculateEndPoint(dataProject.getRtSlope(), orRight, dataProject.getRtLength());
            double[] d = new EasyPointCalculator(new double[]{a.getX(), a.getY(), a.getZ()}).calculateEndPoint(dataProject.getRtSlope(), orRight, dataProject.getRtLength());
            dataProject.updateCoordinate("C", new GPS(null, c[0], c[1], c[2],Nmea_In.Band,Nmea_In.Zone));
            dataProject.updateCoordinate("D", new GPS(null, d[0], d[1], d[2],Nmea_In.Band,Nmea_In.Zone));
        }
    }

    private void updateAll() {
        GPS a = dataProject.getPoints().get("A");
        GPS b = dataProject.getPoints().get("B");
        et_zb.setText(Utils.readUnitOfMeasure(String.valueOf(b.getZ() - a.getZ()), ABProject.this));
        et_dst.setText(Utils.readUnitOfMeasure(String.valueOf(dataProject.getDistanceAB()), ABProject.this));
        et_slope.setText(Utils.readAngolo(String.valueOf(dataProject.getSlopeAB()), ABProject.this));
        et_ltdst.setText(Utils.readUnitOfMeasure(String.valueOf(dataProject.getLtLength()), ABProject.this));
        et_ltslope.setText(Utils.readAngolo(String.valueOf(dataProject.getLtSlope()), ABProject.this));
        et_rtdst.setText(Utils.readUnitOfMeasure(String.valueOf(dataProject.getRtLength()), ABProject.this));
        et_rtslope.setText(Utils.readAngolo(String.valueOf(dataProject.getRtSlope()), ABProject.this));
    }


}
