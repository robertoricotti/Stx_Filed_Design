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

import bluetooth.BT_Conn;
import dialogs.ConnectDialog;
import dialogs.CustomToast;
import gnss.NmeaListenerGGAH;
import services.DataSaved;
import utils.CircumferenceCenterCalculator;
import utils.FullscreenActivity;

public class AntennaMeasure extends AppCompatActivity {
    ImageView btn_exit;
    Button btn_calc;
    EditText p1L, p1S, p2L, p2S, p3L, p3S, p4L, p4S;
    TextView result1, result2, result3, result4, result5, result6;
    boolean D26, D27, E27;

    private Handler handler;
    private boolean mRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antenna_measure);
        FullscreenActivity.setFullScreen(this);
        findView();
        onClick();
        updateUI();

    }

    private void findView() {
        btn_exit = findViewById(R.id.btn_exit);
        btn_calc = findViewById(R.id.btn_calcola);
        p1L = findViewById(R.id.etTarget_L);
        p1S = findViewById(R.id.etTarget_S);
        p2L = findViewById(R.id.etGPS1_L);
        p2S = findViewById(R.id.etGPS1_S);
        p3L = findViewById(R.id.etParallel_L);
        p3S = findViewById(R.id.etParallel_S);
        p4L = findViewById(R.id.etGPS2_L);
        p4S = findViewById(R.id.etGPS2_S);
        result1 = findViewById(R.id.txtAccuracy);
        result2 = findViewById(R.id.txtxdev);
        result3 = findViewById(R.id.txtydev);
        result4 = findViewById(R.id.txtxreach);
        result5 = findViewById(R.id.txtpitch);
        result6 = findViewById(R.id.txtroll);

    }

    private void onClick() {


        btn_exit.setOnClickListener(view -> {
            startActivity(new Intent(AntennaMeasure.this, MchMeaureActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        btn_calc.setOnClickListener(view -> {
            try {
                calcola();
            } catch (Exception e) {
               new CustomToast(this,"ERROR Check Values").show();
            }

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

    private void calcola() {
        double dP1L = Double.parseDouble(p1L.getText().toString());
        double dP1S = Double.parseDouble(p1S.getText().toString());
        double dP2L = Double.parseDouble(p2L.getText().toString());
        double dP2S = Double.parseDouble(p2S.getText().toString());
        double dP3L = Double.parseDouble(p3L.getText().toString());
        double dP3S = Double.parseDouble(p3S.getText().toString());
        double dP4L = Double.parseDouble(p4L.getText().toString());
        double dP4S = Double.parseDouble(p4S.getText().toString());
        double res2 = ((dP2L - dP4L) / 2) * -1;
        double roll1 = ((dP3L + dP3S) / 2) / 2;//=((F16+F18)/2)/2
        double roll2 = ((dP2S * (-1) - dP4S) / 2) * -1;
        double roll = (roll1 + roll2) / 2;
        D26 = dP2S > roll1;
        int E26 = D26 ? 1 : -1;
        double y1 = 0;
        if (D26) {
            y1 = (dP2S + roll1) * (-1) * E26;
        } else {
            y1 = (dP2S * (-1) + roll1) * E26;
        }
        double y2 = 0;
        y2 = (D26 ? (dP4S * (-1) + roll1) : (dP4S - roll1)) * E26;

        double y = (y1 + y2) / 2;
        double num = Math.pow(res2, 2) + Math.pow(y, 2);
        double reach = Math.sqrt(num);
        double pitch = dP2L + res2 - dP1L;
        double accuracyY = Math.round(((y1 - y) * E26) * 1000.0) / 1000.0;
        double accuracyRoll = Math.round(((roll1 - roll) * 1) * 1000.0) / 1000.0;

        result1.setText("\tAcc Y: " + String.format("%.3f", accuracyY).replace(",",".") + "\t" + "Acc Roll: " + String.format("%.3f", accuracyRoll).replace(",","."));
        result2.setText("\tX: " + String.format("%.3f", res2).replace(",","."));
        result3.setText("\tY: " + String.format("%.3f", y).replace(",","."));
        result4.setText("\tr: " + String.format("%.3f", reach).replace(",","."));
        result5.setText("\tPitch: " + String.format("%.3f", pitch).replace(",","."));
        result6.setText("\tRoll: " + String.format("%.3f", roll).replace(",","."));
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