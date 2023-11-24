package dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stx_field_design.R;

import java.io.File;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.Comparator;

import activity.MainActivity;
import activity.MyApp;
import project.DataProjectSingleton;
import project.LoadProject;
import project.PickProjectAdapter;
import services_and_bluetooth.UpdateValues;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;

public class PickProjectDialog {

    public Dialog dialog;
    private RecyclerView recyclerView;
    private Button select, exit;
    private PickProjectAdapter pickProjectAdapter;
    private ArrayList<String> arrayFiles;

    public PickProjectDialog(Activity activity) {
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.pick_project);

        select = dialog.findViewById(R.id.select);
        exit = dialog.findViewById(R.id.exit);
        recyclerView = dialog.findViewById(R.id.recycler);

        arrayFiles = new ArrayList<>();
        File dir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getPath(), "Stx Field");
        String path = dir.getAbsolutePath() + "/Projects/CSV/";


        File directory = new File(path);
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            arrayFiles.add(file.getName());
        }

        arrayFiles.sort(Comparator.naturalOrder());

        pickProjectAdapter = new PickProjectAdapter(arrayFiles);
        recyclerView.setAdapter(pickProjectAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setItemViewCacheSize(pickProjectAdapter.getItemCount());
        onClick(activity);
        setupDialog();
    }

    public void show() {
        dialog.show();
        dialog.setCancelable(true);
        FullscreenActivity.setFullScreen(dialog);
    }
    private void setupDialog(){
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        DisplayMetrics metrics = dialog.getContext().getResources().getDisplayMetrics();
        int parentHeight = (int) (metrics.heightPixels * 0.8);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, parentHeight);
    }
    private void onClick(Activity activity){

        select.setOnClickListener((View v) -> {
            if (pickProjectAdapter.getSelectedItem() == -1) {
                Toast.makeText(activity, "SELECT FILE", Toast.LENGTH_SHORT).show();
            }
            else {
                DataProjectSingleton dataProject = DataProjectSingleton.getInstance();

                String path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Stx Field").getAbsolutePath() + "/Projects/CSV/" + arrayFiles.get(pickProjectAdapter.getSelectedItem());

                dataProject.readProject(path);

                if(!(activity instanceof LoadProject)){

                    activity.startActivity(new Intent(activity, MainActivity.class));

                    activity.finish();
                }
                dialog.dismiss();

            }
        });

        exit.setOnClickListener((View v) -> {
            dialog.dismiss();
        });
    }
}
