package activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stx_field_design.R;

import java.util.Arrays;

import bluetooth.BT_Conn_GPS;
import dialogs.ConnectDialog;
import gnss.Nmea_In;
import services.DataSaved;
import utils.CircumferenceCenterCalculator;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;

public class MchMeaureActivity extends AppCompatActivity {

    Button btn_calc;
    EditText etX1, etX2, etX3, etY1, etY2, etY3;
    ImageView btn_exit, img_connect,img_tognss;
    TextView textCoord, txtSat, txtFix, txtCq, txtHdt, txtAltezzaAnt, txtRtk, res_m, res_ft, res_deg;
    private Handler handler;
    private boolean mRunning = true;
    boolean isNumber1 = false, isNumber2 = false, isNumber3 = false, isNumber4 = false, isNumber5 = false, isNumber6 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mch_meaure);
        FullscreenActivity.setFullScreen(this);
        findView();
        init();
        onClick();
        updateUI();
    }

    private void findView() {
        btn_exit = findViewById(R.id.btn_exit);
        textCoord = findViewById(R.id.txt_coord);
        txtSat = findViewById(R.id.txt_satnr);
        txtFix = findViewById(R.id.txt_quality);
        txtCq = findViewById(R.id.txt_precision);
        txtHdt = findViewById(R.id.txt_hdt);
        txtAltezzaAnt = findViewById(R.id.txt_speed);
        txtRtk = findViewById(R.id.txt_rtk);
        img_connect = findViewById(R.id.img_connetti);
        etX1 = findViewById(R.id.et_x1);
        etX2 = findViewById(R.id.et_x2);
        etX3 = findViewById(R.id.et_x3);
        etY1 = findViewById(R.id.et_y1);
        etY2 = findViewById(R.id.et_y2);
        etY3 = findViewById(R.id.et_y3);

        btn_calc = findViewById(R.id.btn_calc);
        res_m = findViewById(R.id.txt_result_m);
        res_ft = findViewById(R.id.txt_result_ft);
        res_deg = findViewById(R.id.txt_result_deg);
        img_tognss=findViewById(R.id.btn_tognss);
    }

    private void onClick() {
        btn_calc.setOnClickListener(view -> {
            isNumber1 = isDecimalNumber(etX1.getText().toString());
            isNumber2 = isDecimalNumber(etX2.getText().toString());
            isNumber3 = isDecimalNumber(etX3.getText().toString());
            isNumber4 = isDecimalNumber(etY1.getText().toString());
            isNumber5 = isDecimalNumber(etY2.getText().toString());
            isNumber6 = isDecimalNumber(etY3.getText().toString());

            if (isNumber1 && isNumber2 && isNumber3 && isNumber4 && isNumber5 && isNumber6) {
                double x1 = Double.parseDouble(etX1.getText().toString());
                double x2 = Double.parseDouble(etX2.getText().toString());
                double x3 = Double.parseDouble(etX3.getText().toString());
                double y1 = Double.parseDouble(etY1.getText().toString());
                double y2 = Double.parseDouble(etY2.getText().toString());
                double y3 = Double.parseDouble(etY3.getText().toString());
                double[] result = CircumferenceCenterCalculator.findCircumferenceCenter(x1, y1, x2, y2, x3, y3);
                res_m.setText(String.format("%.3f", result[2]).replace(",",".")+" m");
                res_deg.setText(String.format("%.2f", result[3]).replace(",",".")+" °");
                res_ft.setText(String.format("%.4f", (result[2] * 3.28083333333)).replace(",",".")+" ft");
                String[] resultArray=new String[]{res_m.getText().toString().replace(" m",""),res_deg.getText().toString().replace(" °",""),res_ft.getText().toString().replace(" ft","")};
                new MyRW_IntMem().MyWrite("boomresult", Arrays.toString(resultArray),MchMeaureActivity.this);

            } else {
                res_m.setText("Input Err");
                res_ft.setText("Input Err");
                res_deg.setText("Input Err");
            }
        });


        img_connect.setOnClickListener(view -> {
            new ConnectDialog(this,1).show();

        });
        btn_exit.setOnClickListener(view -> {
            startActivity(new Intent(MchMeaureActivity.this, MainActivity.class));
            finish();
        });
        img_tognss.setOnClickListener(view -> {
            startActivity(new Intent(MchMeaureActivity.this, AntennaMeasure.class));
            finish();
        });

    }

    private void updateUI() {

        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRunning) {


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            txtAltezzaAnt.setText(String.format("%.3f", DataSaved.D_AltezzaAnt).replace(",", "."));
                            if (BT_Conn_GPS.GNSSServiceState) {
                                img_connect.setImageResource(R.drawable.btn_positionpage);

                                textCoord.setText("N: " + String.format("%.3f", Nmea_In.Nord1).replace(",", ".") + "\tE: " + String.format("%.3f", Nmea_In.Est1).replace(",", ".") + " Z: " + String.format("%.3f", Nmea_In.Quota1).replace(",", "."));
                                txtSat.setText("\t" + Nmea_In.ggaSat);
                                if (Nmea_In.ggaQuality != null) {
                                    switch (Nmea_In.ggaQuality) {
                                        case "":
                                        case "0":
                                        case "1":
                                            txtFix.setText("\tAUTONOMOUS");
                                            img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                                            break;
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
                                        default:
                                            txtFix.setText("\tAUTONOMOUS");
                                            img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                                            break;
                                    }
                                }
                                if (Nmea_In.VRMS_ != null) {
                                    txtCq.setText("\tH: " + Nmea_In.HRMS_.replace(",", ".") + "\tV: " + Nmea_In.VRMS_.replace(",", "."));
                                } else {
                                    txtCq.setText("H:---.-- V:---.--");
                                }
                                txtHdt.setText("\t" + String.format("%.2f", Nmea_In.tractorBearing).replace(",","."));
                                txtRtk.setText("\t" + Nmea_In.ggaRtk);

                            } else {
                                img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                                img_connect.setImageResource(R.drawable.btn_gpsoff);

                                textCoord.setText("\tDISCONNECTED");
                                txtSat.setText("--");
                                txtFix.setText("---");
                                txtCq.setText("H:---.-- V:---.--");
                                txtHdt.setText("---.--");
                                txtRtk.setText("----");
                            }


                        }
                    });
                    // sleep per intervallo update UI
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
    public boolean isDecimalNumber(String input) {
        // Definisci il pattern di un numero decimale
        String decimalPattern = "^[-+]?\\d*\\.?\\d+$";

        // Verifica se la stringa corrisponde al pattern
        return input.matches(decimalPattern);
    }
    private void init(){
        String [] a=new MyRW_IntMem().MyRead("boomresult",MchMeaureActivity.this).split(",");
        res_m.setText(a[0].replace("[","")+" m");
        res_deg.setText(a[1]+" °");
        res_ft.setText(a[2].replace("]","")+" ft");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunning = false;
    }

    @Override
    public void onBackPressed() {

    }
}