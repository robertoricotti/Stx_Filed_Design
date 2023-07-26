package dialogs;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.example.stx_field_design.R;

import utils.FullscreenActivity;

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

            alertDialog.dismiss();

            activity.finishAndRemoveTask();
            System.exit(0);
        });

        no.setOnClickListener((View v) -> {
            alertDialog.dismiss();
        });
    }
}
