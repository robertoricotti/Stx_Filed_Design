package activity_portrait;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.icu.number.LocalizedNumberFormatter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.stx_field_design.R;

import coords_calc.GPS;
import dialogs.SaveFileDialog;
import gnss.Nmea_In;
import project.DataProjectSingleton;
import services_and_bluetooth.DataSaved;

public class Create_Area extends AppCompatActivity {
    private boolean mRunning=true;
    private Handler handler;
    SaveFileDialog saveFileDialog;
    DataProjectSingleton dataProject;
    public int pickIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataSaved.offset_Z_antenna=0;
        findView();
        updateUI();

    }
    private void findView(){
        pickIndex=0;
        dataProject = DataProjectSingleton.getInstance();
        saveFileDialog = new SaveFileDialog(this, "AREA",String.valueOf(DataSaved.offset_Z_antenna));




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

    public void addPoint(){
        if(Nmea_In.Crs_Est==0&&Nmea_In.Crs_Nord==0){
            Log.d("PuntiF", String.valueOf(dataProject.getPoints())+" "+dataProject.getSize());
            Toast.makeText(this,"Invalid GPS Position", Toast.LENGTH_LONG).show();
        }else {
            pickIndex++;
            GPS gps = new GPS(null, Nmea_In.Crs_Est, Nmea_In.Crs_Nord, Nmea_In.Quota1, Nmea_In.Band, Nmea_In.Zone);
            dataProject.addCoordinate("P"+pickIndex, gps);
            Log.d("PuntiF", String.valueOf(dataProject.getPoints()) + " " + dataProject.getSize());
        }
    }
    public void clearPoint(){

    }
    public void showList(){

    }

    public void saveProj(){
        if (dataProject.getSize() >=3) {

            if (!saveFileDialog.dialog.isShowing())
                saveFileDialog.show();
        } else {
            Toast.makeText(this, "Pick at least 3 points", Toast.LENGTH_LONG).show();
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