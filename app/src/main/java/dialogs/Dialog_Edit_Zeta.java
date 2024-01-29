package dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.stx_field_design.R;

import activity_portrait.ABProject;
import activity_portrait.Create_1P;
import gnss.Nmea_In;
import services_and_bluetooth.DataSaved;
import utils.FullscreenActivity;
import utils.Utils;

public class Dialog_Edit_Zeta {
    private boolean isSaving=false;
    Activity activity;
    public Dialog alertDialog;
    Button save;
    TextView est, nord,uom;
    EditText zeta;
    int index;

    public Dialog_Edit_Zeta(Activity activity, int index) {

        this.activity = activity;
        this.index=index;
    }

    public void show() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_modify_z, null));
        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        alertDialog.show();
        if (Build.BRAND.equals("SRT8PROS")) {
            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        } else {
            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        FullscreenActivity.setFullScreen(alertDialog);
        findView();
        init();
        onClick();
    }

    private void findView() {
        save = alertDialog.findViewById(R.id.ok);

        est=alertDialog.findViewById(R.id.txtest);
        nord=alertDialog.findViewById(R.id.txtnord);
        zeta=alertDialog.findViewById(R.id.etquota);
        uom=alertDialog.findViewById(R.id.txtuom);
        DataSaved.offset_Z_antenna=0;




    }
    private void init(){
        uom.setText("Z  "+ Utils.getMetriSimbol(activity)+" :");
        est.setText("E  "+Utils.getMetriSimbol(activity)+" :"+Utils.readSensorCalibration(String.valueOf(Nmea_In.Crs_Est),activity));
        nord.setText("N  "+Utils.getMetriSimbol(activity)+" :"+Utils.readSensorCalibration(String.valueOf(Nmea_In.Crs_Nord),activity));
        zeta.setText(Utils.readSensorCalibration(String.valueOf(Nmea_In.Quota1),activity));

    }
    private void pickA(){
        if(activity instanceof ABProject){
            ((ABProject) activity).metodoPick();
            alertDialog.dismiss();
        }
        if(activity instanceof Create_1P){
            ((Create_1P) activity).save1P();
            alertDialog.dismiss();
        }
    }

    private void onClick() {

        save.setOnClickListener(view -> {
            if(!isSaving) {
                isSaving=true;
                double value=Double.parseDouble(Utils.writeMetri(zeta.getText().toString(), activity));
                try {
                    DataSaved.offset_Z_antenna = (value-Nmea_In.Quota1);

                } catch (Exception e) {
                    DataSaved.offset_Z_antenna = 0;
                }
                (new Handler()).postDelayed(this::pickA, 1500);

            }

        });






    }
}
