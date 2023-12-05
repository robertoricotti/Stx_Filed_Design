package activity_portrait;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.stx_field_design.R;

import coords_calc.DistToPoint;
import gnss.My_LocationCalc;
import project.UOM_Canvas;
import utils.MyRW_IntMem;
import utils.Utils;

public class UOM_Activity extends AppCompatActivity {
    private boolean zommaIn = false;
    private boolean zommaOut = false;
    double dist_3d, dist_2d, deltaz, slope, bearing;
    CheckBox cbMeters, cbUSF, cbDeg, cbPcent;
    TextView result,tv_zoom;
    int index;
    private boolean mRunning = true;
    private Handler mHandler;
    UOM_Canvas canvas;
    ConstraintLayout container_draw;
    public double[] A_coord, B_coord;
    public int size = 0;
    String res1, res2, res3, res4, res5;
    ImageButton center, zoomIn, zoomOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        init();
        onClick();
        updateUI();


    }

    private void findView() {
        index = Integer.parseInt(new MyRW_IntMem().MyRead("_unitofmeasure", this));
        cbMeters = findViewById(R.id.ckMetri);
        cbUSF = findViewById(R.id.ckPiedi);
        cbDeg = findViewById(R.id.ckDeg);
        cbPcent = findViewById(R.id.ckPercent);
        container_draw = findViewById(R.id.container_draw_uom);
        result = findViewById(R.id.txt_result);
        center=findViewById(R.id.myCenterNav);
        zoomIn=findViewById(R.id.myZoomIn);
        zoomOut=findViewById(R.id.myZoomOut);
        tv_zoom=findViewById(R.id.tv_scalef);


    }

    private void init() {
        canvas = new UOM_Canvas(this);
        container_draw.addView(canvas);
        result.setText("");
        switch (index) {
            case 0:
                cbMeters.setChecked(true);
                cbUSF.setChecked(false);
                cbDeg.setChecked(true);
                cbPcent.setChecked(false);
                break;
            case 1:
                cbMeters.setChecked(true);
                cbUSF.setChecked(false);
                cbDeg.setChecked(false);
                cbPcent.setChecked(true);
                break;
            case 2:
                cbMeters.setChecked(false);
                cbUSF.setChecked(true);
                cbDeg.setChecked(true);
                cbPcent.setChecked(false);
                break;
            case 3:
                cbMeters.setChecked(false);
                cbUSF.setChecked(true);
                cbDeg.setChecked(false);
                cbPcent.setChecked(true);
                break;
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    private void onClick() {
        cbMeters.setOnClickListener(view -> {
            cbMeters.setChecked(true);
            cbUSF.setChecked(false);
            updateCB();
        });
        cbUSF.setOnClickListener(view -> {
            cbUSF.setChecked(true);
            cbMeters.setChecked(false);
            updateCB();
        });
        cbDeg.setOnClickListener(view -> {
            cbDeg.setChecked(true);
            cbPcent.setChecked(false);
            updateCB();
        });
        cbPcent.setOnClickListener(view -> {
            cbPcent.setChecked(true);
            cbDeg.setChecked(false);
            updateCB();
        });

        center.setOnClickListener((View v) -> {
            canvas.offsetX=0;
            canvas.offsetY=0;
            canvas.invalidate();
        });
        center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                canvas.offsetX = 0;
                canvas.offsetY = 0;
                canvas.mScaleFactor=0.5f;
                canvas.invalidate();
            }
        });



        zoomIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    zommaIn = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    zommaIn = false;
                }
                return false;  // Torna 'false' per continuare a ricevere gli eventi di clic
            }
        });



        zoomOut.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    zommaOut = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    zommaOut = false;
                }
                return false;  // Torna 'false' per continuare a ricevere gli eventi di clic
            }
        });
    }

    private void updateCB() {
        int out = 0;
        if (cbMeters.isChecked() && !cbUSF.isChecked()) {
            if (cbDeg.isChecked() && !cbPcent.isChecked()) {
                out = 0;
            } else if (cbPcent.isChecked() && !cbDeg.isChecked()) {
                out = 1;
            }
        } else if (!cbMeters.isChecked() && cbUSF.isChecked()) {
            if (cbDeg.isChecked() && !cbPcent.isChecked()) {
                out = 2;
            } else if (cbPcent.isChecked() && !cbDeg.isChecked()) {
                out = 3;
            }
        }
        new MyRW_IntMem().MyWrite("_unitofmeasure", String.valueOf(out), UOM_Activity.this);

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateUI() {

        mHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRunning) {

                    //non UI

                    mHandler.post(new Runnable() {
                        @SuppressLint("NewApi")
                        @Override
                        public void run() {

                            //UI
                            tv_zoom.setText(String.format("%.2f",canvas.mScaleFactor)+" x");
                            if (zommaOut) {
                                zommaIn = false;
                                if (canvas.mScaleFactor > 0.04f) {
                                    canvas.mScaleFactor -= 0.01f;

                                }
                            }
                            if (zommaIn) {
                                zommaOut = false;
                                canvas.mScaleFactor += 0.01f;

                            }
                            if (size == 2) {
                                dist_3d = new DistToPoint(A_coord[0], A_coord[1], A_coord[2], B_coord[0], B_coord[1], B_coord[2]).getDist_to_point();
                                dist_2d = new DistToPoint(A_coord[0], A_coord[1], 0, B_coord[0], B_coord[1], 0).getDist_to_point();
                                deltaz = A_coord[2] - B_coord[2];
                                bearing = My_LocationCalc.calcBearingXY(A_coord[0], A_coord[1], B_coord[0], B_coord[1]);
                                if (bearing < 0) {
                                    bearing += 360;
                                }
                                if (bearing > 360) {
                                    bearing -= 3650;
                                }
                                try {
                                    slope = Utils.slopeCalculator_primitive(A_coord, B_coord);
                                    res1 = Utils.readUnitOfMeasure(String.valueOf(dist_3d), UOM_Activity.this) + " " + Utils.getMetriSimbol(UOM_Activity.this);
                                    res2 = Utils.readUnitOfMeasure(String.valueOf(dist_2d), UOM_Activity.this) + " " + Utils.getMetriSimbol(UOM_Activity.this);
                                    res3 =Utils.readUnitOfMeasure(String.valueOf(deltaz),UOM_Activity.this)+" "+Utils.getMetriSimbol(UOM_Activity.this);
                                    res4=String.format("%.2f",bearing).replace(",",".");
                                    res5=Utils.readAngolo(String.valueOf(slope),UOM_Activity.this)+" "+Utils.getGradiSimbol(UOM_Activity.this);


                                } catch (Exception e) {
                                    res1=e.toString();
                                }

                            }
                            if (size == 2) {
                                try {
                                    result.setText("3D Dist: "+res1+"\n"+
                                            "2D Dist: "+res2+"\n"+
                                            "Delta Z: "+res3+"\n"+
                                            "HDT: "+res4+" °"+"\n"+
                                            "Slope: "+res5);
                                } catch (Exception e) {
                                   result.setText("");
                                }

                            }
                            canvas.A_coord = A_coord;
                            canvas.B_coord = B_coord;
                            canvas.pNumber = size;
                            canvas.invalidate();
                        }
                    });
                    // sleep per intervallo update UI
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.println(e.toString());
                    }
                }
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunning = false;
        size = 0;
    }

    @Override
    public void onBackPressed() {

    }

    public void setA_coord(double[] coord) {
        A_coord = coord;
    }

    public void setB_coord(double[] coord) {
        B_coord = coord;
    }
}