package dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.core.content.ContextCompat;

import com.example.stx_field_design.R;

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
        }
    }

    private void onClick() {
        yes.setOnClickListener((View v) -> {
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

                    activity.startService(new Intent(activity, Bluetooth_CAN_Service.class));
                    Log.d("Dialog: ", "mi connetto");
                }

            }

            alertDialog.dismiss();
        });

        exit.setOnClickListener((View v) -> {
            alertDialog.dismiss();
        });
    }
}

