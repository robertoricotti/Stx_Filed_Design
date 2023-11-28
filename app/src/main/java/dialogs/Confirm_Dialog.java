package dialogs;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.stx_field_design.R;

import activity.UsbActivity;
import project.ABProject;
import utils.FullscreenActivity;

public class Confirm_Dialog {
    Activity activity;
    AlertDialog alertDialog;
    Button yes, no;
    TextView title;
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
        title=alertDialog.findViewById(R.id.title);
        yes = alertDialog.findViewById(R.id.yes);
        no = alertDialog.findViewById(R.id.no);
        if(activity instanceof ABProject){
            title.setText("UPLOAD\nDATA?");
        }
        if(activity instanceof UsbActivity){
            title.setText("DELETE\nSELECTED FILE?");
        }
    }

    private void onClick() {


        yes.setOnClickListener((View v) -> {
            if (activity instanceof ABProject) {
                switch (flag) {
                    case -1:
                        if(((ABProject)activity).progressBar.getVisibility()==View.INVISIBLE){
                            visu();
                            (new Handler()).postDelayed(this:: calcZ, 500);
                            (new Handler()).postDelayed(this:: calc2, 100);
                            (new Handler()).postDelayed(this:: calc3, 100);
                            (new Handler()).postDelayed(this:: calc4, 100);
                            (new Handler()).postDelayed(this:: calc5, 100);
                            (new Handler()).postDelayed(this:: calc6, 100);
                            (new Handler()).postDelayed(this:: calc7, 100);
                            (new Handler()).postDelayed(this:: invisu, 100);
                            if( ((ABProject)activity).save.getVisibility()==View.INVISIBLE)
                                ((ABProject)activity).save.setVisibility(View.VISIBLE);

                        }

                        break;
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
            if(activity instanceof UsbActivity){
                ((UsbActivity)activity).confirmDelete(true);
            }


            alertDialog.dismiss();


        });

        no.setOnClickListener((View v) -> {
            flag=0;
            if(activity instanceof UsbActivity){
                ((UsbActivity)activity).confirmDelete(false);
            }
            alertDialog.dismiss();
        });
    }

    private void visu() {
        ((ABProject)activity).progressBar.setVisibility(View.VISIBLE);
    }
    private void invisu() {
        ((ABProject)activity).progressBar.setVisibility(View.INVISIBLE);
    }

    private void calc7() {
        ((ABProject) activity).calc7();
    }
    private void calc6() {
        ((ABProject) activity).calc6();
    }
    private void calc5() {
        ((ABProject) activity).calc5();
    }
    private void calc4() {
        ((ABProject) activity).calc4();
    }
    private void calc3() {
        ((ABProject) activity).calc3();
    }

    private void calc2() {
        ((ABProject) activity).calc2();
    }

    private void calcZ() {
        ((ABProject) activity).calcZ();
    }


}
