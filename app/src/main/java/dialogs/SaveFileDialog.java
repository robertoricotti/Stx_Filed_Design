package dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stx_field_design.R;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import activity_portrait.ABProject;
import activity_portrait.Create_1P;
import project.DataProjectSingleton;
import activity_portrait.MenuProject;
import utils.FullscreenActivity;

public class SaveFileDialog {
    Activity activity;
    public Dialog dialog;
    Button save, exit;
    EditText fileName;
    String tag,offset;

    public SaveFileDialog(Activity activity,String tag,String offset) {
        this.activity = activity;
        this.tag=tag;
        this.offset=offset;
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.save_file_dialog);

    }

    public void show() {
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        FullscreenActivity.setFullScreen(dialog);
        findView();
        onClick();
    }

    private void findView() {
        save = dialog.findViewById(R.id.save);
        exit = dialog.findViewById(R.id.exit);
        fileName = dialog.findViewById(R.id.fileName);
    }

    private void onClick(){

        save.setOnClickListener((View vw) -> {

            if(!fileName.getText().toString().equals("")){
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String currentDateTime = "";//sdf.format(new Date());
                DataProjectSingleton dataProject = DataProjectSingleton.getInstance();
                if(activity instanceof ABProject){
                    dataProject.saveProject(new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getPath(), "Stx Field").getAbsolutePath() + "/Projects/", "AB_"+fileName.getText().toString()+"_"+currentDateTime + ".pstx",tag,offset);

                }
                if(activity instanceof Create_1P){
                    dataProject.saveProject(new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getPath(), "Stx Field").getAbsolutePath() + "/Projects/", "1P_"+fileName.getText().toString()+"_"+currentDateTime + ".pstx",tag,offset);

                }
                //aggiungere  cross section


                Toast.makeText(activity, "File Saved!", Toast.LENGTH_SHORT).show();
                activity.startActivity(new Intent(activity, MenuProject.class));
                activity.finish();
                dialog.dismiss();
            }
            else {
                Toast.makeText(activity, "Missing name!", Toast.LENGTH_SHORT).show();
            }
        });

        exit.setOnClickListener((View vw) -> {
            dialog.dismiss();
        });

    }
}
