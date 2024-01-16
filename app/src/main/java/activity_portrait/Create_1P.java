package activity_portrait;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stx_field_design.R;

import gnss.Nmea_In;

public class Create_1P extends AppCompatActivity {
    private boolean mRunning=true;
    private Handler handler;

    TextView txE,txN,txZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findView();
        onClick();
        updateUI();
    }

    private void findView(){
        txE=findViewById(R.id.p_coord_est);
        txN=findViewById(R.id.p_coord_nord);
        txZ=findViewById(R.id.p_coord_z);



    }
    private void onClick(){


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
                           txE.setText("E: "+ String.format("%.3f",Nmea_In.Crs_Est));
                            txN.setText("N: "+ String.format("%.3f",Nmea_In.Crs_Nord));
                            txZ.setText("Z: "+ String.format("%.3f",Nmea_In.Quota1));


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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunning=false;
    }
}