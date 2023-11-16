package dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.stx_field_design.R;

import java.util.ArrayList;
import java.util.Map;

import coords_calc.GPS;
import project.DataProjectSingleton;
import utils.FullscreenActivity;

public class CoordsGNSSInfo {
    public final Dialog dialog;
    private final ListView listView;
    private final ArrayList<String> coordsList;
    private final ArrayAdapter<String> arrayAdapter;

    public CoordsGNSSInfo(Activity activity) {
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_gnss_coordinates);
        listView = dialog.findViewById(R.id.listCoordinates);
        coordsList = new ArrayList<>();
        DataProjectSingleton dataProject = DataProjectSingleton.getInstance();
        for (Map.Entry<String, GPS> entry : dataProject.getPoints().entrySet()) {
            String id = "ID: " + entry.getKey() + "    CRS: "+dataProject.getEpsgCode()+"\n";
            String x = "X: " + entry.getValue().getX() + "\n";
            String y = "Y: " + entry.getValue().getY() + "\n";
            String z = "Z: " + entry.getValue().getZ();
            String strRes = id + x + y + z;
            coordsList.add(strRes);
        }
        arrayAdapter = new ArrayAdapter<>(activity, R.layout.text_row, R.id.my_text_row, coordsList);
        listView.setAdapter(arrayAdapter);
        setupDialog();
    }

    public void show() {
        dialog.show();
        dialog.setCancelable(true);
        FullscreenActivity.setFullScreen(dialog);
    }

    private void setupDialog() {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        DisplayMetrics metrics = dialog.getContext().getResources().getDisplayMetrics();
        int parentHeight = (int) (metrics.heightPixels * 0.8);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, parentHeight);
    }

}