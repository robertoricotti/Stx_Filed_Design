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
import android.widget.Toast;

import com.example.stx_field_design.R;

import coords_calc.GPS;
import dialogs.SaveFileDialog;
import gnss.Nmea_In;
import project.DataProjectSingleton;
import services_and_bluetooth.DataSaved;

public class Create_1P extends AppCompatActivity {
    private boolean mRunning=true;
    private Handler handler;

    TextView txE,txN,txZ;
    SaveFileDialog saveFileDialog;
    DataProjectSingleton dataProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findView();
        onClick();
        updateUI();
    }

    private void findView(){
        dataProject = DataProjectSingleton.getInstance();
        saveFileDialog = new SaveFileDialog(this, "PLAN",String.valueOf(DataSaved.offset_Z_antenna));
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
    public void save1P(){
        GPS gps=new GPS(null,Nmea_In.Crs_Est,Nmea_In.Crs_Nord,Nmea_In.Quota1,Nmea_In.Band,Nmea_In.Zone);
        dataProject.addCoordinate("P",gps);
        if (dataProject.getSize() == 1) {

            if (!saveFileDialog.dialog.isShowing())
                saveFileDialog.show();
        } else {
            Toast.makeText(this, "Points not available!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataProject.clearData();
        mRunning=false;
    }
}