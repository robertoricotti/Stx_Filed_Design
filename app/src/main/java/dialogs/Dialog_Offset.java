package dialogs;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.stx_field_design.R;

import activity_portrait.ABProject;
import activity_portrait.AB_WorkActivity;
import activity_portrait.MyApp;
import activity_portrait.UsbActivity;
import services_and_bluetooth.DataSaved;
import services_and_bluetooth.UpdateValues;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;
import utils.Utils;

public class Dialog_Offset {

    Activity activity;
    AlertDialog alertDialog;
    Button yes, no,reset;
    EditText title;
    int flag;

    public Dialog_Offset(Activity activity, int flag) {
        this.activity = activity;
        this.flag = flag;

    }
    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.offset_dialog, null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
        FullscreenActivity.setFullScreen(alertDialog);
        findView();
        onClick();
    }

    private void findView() {
        title=alertDialog.findViewById(R.id.m_value);
        yes = alertDialog.findViewById(R.id._h_ok);
        no = alertDialog.findViewById(R.id._h_exit);
        reset=alertDialog.findViewById(R.id.bt_reset);
        title.setText(Utils.readUnitOfMeasure(new MyRW_IntMem().MyRead("_offset", MyApp.visibleActivity),MyApp.visibleActivity));

    }
    public void onClick(){
        no.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        reset.setOnClickListener(view -> {
            try {
                String s="0.0";
                DataSaved.D_Offset=Double.parseDouble(Utils.writeMetri(s,activity));
                new MyRW_IntMem().MyWrite("_offset",Utils.writeMetri(s,activity),activity);
                activity.startService(new Intent(activity, UpdateValues.class));
                if(activity instanceof AB_WorkActivity){
                    ((AB_WorkActivity)activity).updateOffset();
                }
            } catch (Exception e) {
                alertDialog.dismiss();

            }
            alertDialog.dismiss();
        });

        yes.setOnClickListener(view -> {
            try {
                String s=title.getText().toString();
                DataSaved.D_Offset=Double.parseDouble(Utils.writeMetri(s,activity));
                new MyRW_IntMem().MyWrite("_offset",Utils.writeMetri(s,activity),activity);
                activity.startService(new Intent(activity, UpdateValues.class));
                if(activity instanceof AB_WorkActivity){
                    ((AB_WorkActivity)activity).updateOffset();
                }
            } catch (Exception e) {
               alertDialog.dismiss();

            }
            alertDialog.dismiss();
        });

    }
}
