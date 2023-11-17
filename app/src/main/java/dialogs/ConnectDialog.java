package dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.stx_field_design.R;

import bluetooth.BT_Conn_CAN;
import bluetooth.BT_Conn_GPS;
import services.AutoConnectionService;
import services.DataSaved;
import utils.FullscreenActivity;

public class ConnectDialog {
    Activity activity;
    AlertDialog alertDialog;
    Button yes, exit;
    TextView textView;
    int flag;

    public ConnectDialog(Activity activity, int flag) {
        this.activity = activity;
        this.flag=flag;
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
        if(flag==1) {
            if (BT_Conn_GPS.GNSSServiceState) {
                textView.setText("GPS\nDisconnect From\n" + DataSaved.S_gpsname + "\n" + DataSaved.S_macAddres);
            } else {
                textView.setText("GPS\nConnect To\n" + DataSaved.S_gpsname + "\n" + DataSaved.S_macAddres);
            }
        }else if(flag==2){
            if (BT_Conn_CAN.CANerviceState) {
                textView.setText("CAN\nDisconnect From\n" + DataSaved.S_can_name + "\n" + DataSaved.S_macAddress_CAN);
            } else {
                textView.setText("CAN\nConnect To\n" + DataSaved.S_can_name + "\n" + DataSaved.S_macAddress_CAN);
            }
        }
    }

    private void onClick() {
        yes.setOnClickListener((View v) -> {
            if(flag==1) {
                new BT_Conn_GPS().GNSS_Connection(activity, !BT_Conn_GPS.GNSSServiceState);
            }else if(flag==2){
                new BT_Conn_CAN().CAN_Connection(activity,!BT_Conn_CAN.CANerviceState);
            }

            alertDialog.dismiss();
        });

        exit.setOnClickListener((View v) -> {
            alertDialog.dismiss();
        });
    }
}

