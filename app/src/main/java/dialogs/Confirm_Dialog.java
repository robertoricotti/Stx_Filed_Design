package dialogs;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.example.stx_field_design.R;

import project.ABProject;
import utils.FullscreenActivity;

public class Confirm_Dialog {
    Activity activity;
    AlertDialog alertDialog;
    Button yes, no;
    int flag;


    public Confirm_Dialog(Activity activity, int flag) {
        this.activity = activity;
        this.flag = flag;

    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.modify_confirm_dialog, null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
        FullscreenActivity.setFullScreen(alertDialog);
        findView();
        onClick();
    }

    private void findView() {
        yes = alertDialog.findViewById(R.id.yes);
        no = alertDialog.findViewById(R.id.no);
    }

    private void onClick() {


        yes.setOnClickListener((View v) -> {
            if (activity instanceof ABProject) {
                switch (flag) {
                    case 0:
                        alertDialog.dismiss();
                        break;
                    case 1:
                        ((ABProject) activity).calcZ();
                        break;
                    case 2:
                        ((ABProject) activity).calc2();
                        break;
                    case 3:
                        ((ABProject) activity).calc3();
                        break;
                    case 4:
                        ((ABProject) activity).calc4();
                        break;
                    case 5:
                        ((ABProject) activity).calc5();
                        break;
                    case 6:
                        ((ABProject) activity).calc6();
                        break;
                    case 7:
                        ((ABProject) activity).calc7();
                        break;
                }

            }

            alertDialog.dismiss();


        });

        no.setOnClickListener((View v) -> {
            flag=0;
            alertDialog.dismiss();
        });
    }


}
