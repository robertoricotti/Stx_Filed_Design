package project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.stx_field_design.R;

import bluetooth.BT_Conn_GPS;
import coords_calc.DistToPoint;
import coords_calc.EasyPointCalculator;
import coords_calc.GPS;
import dialogs.Confirm_Dialog;
import dialogs.ConnectDialog;
import dialogs.MyEpsgDialog;
import dialogs.SaveFileDialog;
import gnss.My_LocationCalc;
import gnss.Nmea_In;
import services.DataSaved;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;
import utils.Utils;

public class ABProject extends AppCompatActivity {
    private boolean showCoord = false;
    TextView txtSat, txtFix, txtCq, txtHdt, txtAltezzaAnt, txtRtk;
    ImageView imgConnect, calc1, calc2, calc3, calc4, calc5, calc7, calc6;
    EditText et_zb, et_dst, et_slope, et_ltdst, et_ltslope, et_rtdst, et_rtslope;
    ConstraintLayout container_draw;
    ImageView back, pick, save;

    ImageButton center, zoomIn, zoomOut;
    ImageButton clear;
    Button crs;
    TextView textCoord;

    int pickIndex;
    boolean textCoordStatus = false;
    DataProjectSingleton dataProject;
    ABCanvas canvas;

    MyEpsgDialog myEpsgDialog;
    SaveFileDialog saveFileDialog;
    Handler handler;
    Runnable updateRunnable;
    double myZ, myLen, mySlope;

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
        calc1 = findViewById(R.id.btn_zb);
        calc2 = findViewById(R.id.btn_dst);
        calc3 = findViewById(R.id.btn_slope);
        calc4 = findViewById(R.id.btn_ltdst);
        calc5 = findViewById(R.id.btn_ltslope);
        calc6 = findViewById(R.id.btn_rtdst);
        calc7 = findViewById(R.id.btn_rtslope);
        et_zb = findViewById(R.id.et_zb);
        et_dst = findViewById(R.id.et_dst);
        et_slope = findViewById(R.id.et_slope);
        et_ltdst = findViewById(R.id.et_ltdst);
        et_ltslope = findViewById(R.id.et_ltslope);
        et_rtdst = findViewById(R.id.et_rtdst);
        et_rtslope = findViewById(R.id.et_rtslope);

    }

    private void init() {
        save.setVisibility(View.INVISIBLE);
        dataProject = DataProjectSingleton.getInstance();
        myEpsgDialog = new MyEpsgDialog(this);
        saveFileDialog = new SaveFileDialog(this);
        canvas = new ABCanvas(this);
        container_draw.addView(canvas);
        pickIndex = 0;
        dataProject.mScaleFactor = Float.parseFloat(new MyRW_IntMem().MyRead("zoomF", this));
    }

    private void onClick() {
        calc1.setOnClickListener(view -> {
            new Confirm_Dialog(this,1).show();
        });
        calc2.setOnClickListener(view -> {
            new Confirm_Dialog(this,2).show();
        });
        calc3.setOnClickListener(view -> {
            new Confirm_Dialog(this,3).show();
        });
        calc4.setOnClickListener(view -> {
            new Confirm_Dialog(this,4).show();
        });
        calc5.setOnClickListener(view -> {
            new Confirm_Dialog(this,5).show();
        });
        calc6.setOnClickListener(view -> {
            new Confirm_Dialog(this,6).show();
        });
        calc7.setOnClickListener(view -> {
            new Confirm_Dialog(this,7).show();
        });
        imgConnect.setOnClickListener(view -> {
            new ConnectDialog(this,1).show();

        });
        textCoord.setOnClickListener(view -> {
            showCoord = !showCoord;
        });
        back.setOnClickListener((View v) -> {
            startActivity(new Intent(this, MenuProject.class));

            finish();
        });

        save.setOnClickListener((View v) -> {
            new MyRW_IntMem().MyWrite("zoomF", String.valueOf(dataProject.mScaleFactor),this);

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
            dataProject.setmScaleFactor(1f);
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

        textCoord.setOnClickListener((View v) -> {
            textCoordStatus = !textCoordStatus;
        });
    }


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateUI() {
        handler = new Handler();
        updateRunnable = () -> {
            if (pickIndex >= 2) {
                pick.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
            } else {
                pick.setVisibility(View.VISIBLE);
            }

            crs.setText(dataProject.getEpsgCode() != null ? dataProject.getEpsgCode() : DataSaved.S_CRS);

            textCoord.setText(!BT_Conn_GPS.GNSSServiceState ? "DISCONNECTED" : "N: " + String.format("%.3f", Nmea_In.Nord1).replace(",", ".") + "\tE: " + String.format("%.3f", Nmea_In.Est1).replace(",", ".") + " Z: " + String.format("%.3f", Nmea_In.Quota1).replace(",", "."));

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


    public  void calcZ() {
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
    }
}
