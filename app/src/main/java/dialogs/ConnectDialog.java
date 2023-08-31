package dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.stx_field_design.R;

import activity.MainActivity;
import bluetooth.BT_Conn;
import services.DataSaved;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;

public class ConnectDialog {
    Activity activity;
    AlertDialog alertDialog;
    Button yes, exit;
    TextView textView;

    public ConnectDialog(Activity activity) {
        this.activity = activity;
    }

    public void show(){

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

    private void findView(){
        yes = alertDialog.findViewById(R.id._h_yes);
        exit = alertDialog.findViewById(R.id._h_exit);
        textView=alertDialog.findViewById(R.id.titleC);
        if(BT_Conn.GNSSServiceState){
            textView.setText("Disconnect From\n"+DataSaved.S_gpsname+"\n"+DataSaved.S_macAddres);
        }else{
            textView.setText("Connect To\n"+DataSaved.S_gpsname+"\n"+DataSaved.S_macAddres);
        }
    }

    private void onClick(){
        yes.setOnClickListener((View v) -> {
            new BT_Conn().GNSS_Connection(activity, !BT_Conn.GNSSServiceState, DataSaved.S_macAddres);
            alertDialog.dismiss();
        });

        exit.setOnClickListener((View v) -> {
            alertDialog.dismiss();
        });
    }
}

