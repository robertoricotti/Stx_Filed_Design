package dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.core.content.ContextCompat;

import com.example.stx_field_design.R;

import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.ProjCoordinate;

import java.util.Arrays;

import can.Can_Decoder;
import services_and_bluetooth.AutoConnectionService;
import services_and_bluetooth.Bluetooth_CAN_Service;
import services_and_bluetooth.Bluetooth_GNSS_Service;
import services_and_bluetooth.DataSaved;
import utils.FullscreenActivity;

public class ConnectDialog {
    Activity activity;
    AlertDialog alertDialog;
    Button yes, exit;
    TextView textView;
    int flag;
    private boolean isUpdating = false;
    private Handler handler;

    public ConnectDialog(Activity activity, int flag) {
        this.activity = activity;
        this.flag = flag;
    }

    public void show() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.heading_dialog, null));
        alertDialog = builder.create();
        builder.setCancelable(false);
        FullscreenActivity.setFullScreen(alertDialog);
        alertDialog.show();
        findView();
        onClick();
        if(DataSaved.deviceType.equals("SRT8PROS")||DataSaved.deviceType.equals("SRT7PROS")){
        startUpdatingCoordinates();}
    }

    private void findView() {
        yes = alertDialog.findViewById(R.id._h_yes);
        exit = alertDialog.findViewById(R.id._h_exit);
        textView = alertDialog.findViewById(R.id.titleC);
        if (flag == 1) {
            if (Bluetooth_GNSS_Service.gpsIsConnected) {
                yes.setBackgroundTintList(ContextCompat.getColorStateList(activity.getApplicationContext(), R.color.bg_sfsred));

                textView.setText("GPS\nDisconnect From\n" + DataSaved.S_gpsname + "\n" + DataSaved.S_macAddres);
            } else {
                yes.setBackgroundTintList(ContextCompat.getColorStateList(activity.getApplicationContext(), R.color.colorStonexBlueLight1));

                textView.setText("GPS\nConnect To\n" + DataSaved.S_gpsname + "\n" + DataSaved.S_macAddres);
            }
        } else if (flag == 2) {
            if (Bluetooth_CAN_Service.canIsConnected) {
                yes.setBackgroundTintList(ContextCompat.getColorStateList(activity.getApplicationContext(), R.color.bg_sfsred));

                textView.setText("CAN\nDisconnect From\n" + DataSaved.S_can_name + "\n" + DataSaved.S_macAddress_CAN);
            } else {
                yes.setBackgroundTintList(ContextCompat.getColorStateList(activity.getApplicationContext(), R.color.colorStonexBlueLight1));

                textView.setText("CAN\nConnect To\n" + DataSaved.S_can_name + "\n" + DataSaved.S_macAddress_CAN);
            }
        }else if (flag==3) {

            textView.setText("ID: "+ Can_Decoder.mID+" "+ Arrays.toString(Can_Decoder.msgFrame)+"\n");
            yes.setVisibility(View.GONE);
            exit.setText("X");

        }
    }

    private void onClick() {
        yes.setOnClickListener((View v) -> {
            stopUpdatingCoordinates();
            if (flag == 1) {
                if (Bluetooth_GNSS_Service.gpsIsConnected) {

                    activity.stopService(new Intent(activity, AutoConnectionService.class));
                    activity.stopService(new Intent(activity, Bluetooth_GNSS_Service.class));
                    activity.startService(new Intent(activity, AutoConnectionService.class));
                } else {

                    activity.startService(new Intent(activity, Bluetooth_GNSS_Service.class));
                }

            } else if (flag == 2) {
                if (Bluetooth_CAN_Service.canIsConnected) {

                    activity.stopService(new Intent(activity, AutoConnectionService.class));
                    activity.stopService(new Intent(activity, Bluetooth_CAN_Service.class));
                    activity.startService(new Intent(activity, AutoConnectionService.class));
                    Log.d("Dialog: ", "mi disconnetto");
                } else {
                    if(DataSaved.deviceType.equals("SRT8PROS")||DataSaved.deviceType.equals("SRT7PROS")){

                    }else {
                        activity.startService(new Intent(activity, Bluetooth_CAN_Service.class));
                        Log.d("Dialog: ", "mi connetto");
                    }

                }

            }

            alertDialog.dismiss();
        });

        exit.setOnClickListener((View v) -> {
            stopUpdatingCoordinates();
            alertDialog.dismiss();
        });
    }


    private void startUpdatingCoordinates() {
        if (!isUpdating) {
            isUpdating = true;
            handler = new Handler();
            updateCoordinates();
        }
    }

    private void stopUpdatingCoordinates() {
        if (isUpdating) {
            isUpdating = false;
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
        }
    }

    private void updateCoordinates() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Update coord TextView with new coordinates

                textView.append("ID: "+ Can_Decoder.mID+" "+ Arrays.toString(Can_Decoder.msgFrame)+"\n");
               if(textView.getLineCount()>6){
                   textView.setText("");
               }



                if (isUpdating) {
                    updateCoordinates();
                }
            }
        }, 1000);
    }
}

