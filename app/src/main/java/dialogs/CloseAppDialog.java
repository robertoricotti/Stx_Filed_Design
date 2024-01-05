package dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.example.stx_field_design.R;
import com.van.jni.VanCmd;


import services_and_bluetooth.AutoConnectionService;
import services_and_bluetooth.Bluetooth_CAN_Service;
import services_and_bluetooth.Bluetooth_GNSS_Service;
import services_and_bluetooth.CanService;
import services_and_bluetooth.DataSaved;
import services_and_bluetooth.UpdateValues;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;

public class CloseAppDialog {
    Activity activity;
    AlertDialog alertDialog;
    Button yes, no;



    public CloseAppDialog(Activity activity) {
        this.activity = activity;

    }

    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.close_app, null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
        FullscreenActivity.setFullScreen(alertDialog);
        findView();
        onClick();
    }

    private void findView(){
        yes = alertDialog.findViewById(R.id.yes);
        no = alertDialog.findViewById(R.id.no);
    }

    private void onClick() {

        yes.setOnClickListener((View v) -> {
            try {
                activity.stopService(new Intent(activity, AutoConnectionService.class));
                activity.stopService((new Intent(activity, Bluetooth_GNSS_Service.class)));
                if(DataSaved.deviceType.equals("SRT8PROS")||DataSaved.deviceType.equals("SRT7PROS")){
                    activity.stopService((new Intent(activity, CanService.class)));
                }else {
                activity.stopService((new Intent(activity, Bluetooth_CAN_Service.class)));}
            } catch (Exception e) {

            }
            if(DataSaved.deviceType.equals("SRT8PROS")||DataSaved.deviceType.equals("SRT7PROS")){
                VanCmd.exec("wm overscan 0,0,0,0", 10);
            }

            alertDialog.dismiss();

            activity.finishAndRemoveTask();

            System.exit(0);
        });

        no.setOnClickListener((View v) -> {
            alertDialog.dismiss();
        });
    }

}
