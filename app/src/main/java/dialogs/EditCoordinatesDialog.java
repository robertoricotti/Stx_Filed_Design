package dialogs;

import static android.os.Looper.getMainLooper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.stx_field_design.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


import project.DataProjectSingleton;
import utils.FullscreenActivity;

public class EditCoordinatesDialog {
    Activity activity;

    public Dialog dialog;

    LinearLayout layout;

    Button ok;

    ArrayList<Button> pickButtons;

    ArrayList<EditText> idPoints;

    TextView caricamento;

    Executor executor = Executors.newSingleThreadExecutor();


    public EditCoordinatesDialog(Activity activity){
        this.activity = activity;
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.edit_coordinates_dialog);
    }

    public void show(){
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        dialog.show();
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int parentHeight = (int) (metrics.heightPixels * 0.8);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, parentHeight);
        dialog.setCancelable(true);
        FullscreenActivity.setFullScreen(dialog);
        findView();
        init();
    }

    private void findView(){
        layout = dialog.findViewById(R.id.coordinates_container);
        ok = dialog.findViewById(R.id.ok);
        caricamento = dialog.findViewById(R.id.caricamento);
    }

    private void onClick(){

        ok.setOnClickListener((View v) -> {
            dialog.dismiss();
        });


    }

    private void init(){
        idPoints = new ArrayList<>();

        pickButtons = new ArrayList<>();

        executor.execute(new ReadAndCreateView());
    }

    private void addCoordinate(String strID) {

        LinearLayout linearLayout = new LinearLayout(activity);

        LinearLayout.LayoutParams paramsLinearLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        paramsLinearLayout.setMargins(convertDpToPixel(10, activity), 0, convertDpToPixel(10, activity), 0);

        linearLayout.setLayoutParams(paramsLinearLayout);

        LinearLayout.LayoutParams paramsButton = new LinearLayout.LayoutParams(convertDpToPixel(150, activity), ViewGroup.LayoutParams.WRAP_CONTENT);

        paramsButton.setMargins(0, 0, convertDpToPixel(10, activity), 0);

        Button button = new Button(activity);
        button.setText("POINT");
        button.setLayoutParams(paramsButton);
        button.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.yellow));

        pickButtons.add(button);

        LinearLayout.LayoutParams paramsEditText = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        paramsLinearLayout.setMargins(convertDpToPixel(10, activity), 0, 0, 0);

        EditText editText = new EditText(activity);
        editText.setGravity(Gravity.CENTER);
        editText.setEms(10);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setHint("ID POINT");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setLayoutParams(paramsEditText);
        editText.setText(strID);
        editText.setFocusable(false);
        idPoints.add(editText);

        linearLayout.addView(button);
        linearLayout.addView(editText);

        layout.addView(linearLayout);

    }

    private void updateClickLister(){
        for (int i = 0; i < pickButtons.size(); i++) {
            final int index = i; // Crea una variabile finale per l'indice corrente

            pickButtons.get(index).setOnClickListener(v -> {
                infoDialogShow(index, idPoints.get(index).getText().toString());
            });
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void infoDialogShow(int index, String id){
        Dialog infoDialog = new Dialog(activity);
        infoDialog.setContentView(R.layout.pick_info_dialog);
        infoDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoDialog.setCancelable(false);

        Button delete, okay;

        EditText x, y, z;

        EditText idPoint;

        delete = infoDialog.findViewById(R.id.delete);

        okay = infoDialog.findViewById(R.id.okay_text);

        idPoint = infoDialog.findViewById(R.id.id_point);

        x = infoDialog.findViewById(R.id.valueLat);

        y = infoDialog.findViewById(R.id.valueLong);

        z = infoDialog.findViewById(R.id.valueZ);

        idPoint.setText(idPoints.get(index).getText().toString());


       /* x.setText(String.format("%.6f", DataProjectSingleton.getInstance().points.get(id).getLatitude()));
        y.setText(String.format("%.6f", DataProjectSingleton.getInstance().points.get(id).getLongitude()));
        z.setText(String.format("%.6f", DataProjectSingleton.getInstance().points.get(id).getQuota()));*/

        delete.setOnClickListener((View v) -> {
            pickButtons.remove(index);
            idPoints.remove(index);
            DataProjectSingleton.getInstance().deleteCoordinate(id);
            layout.removeViewAt(index);
            updateClickLister();
            infoDialog.dismiss();
        });

        okay.setOnClickListener((View v) ->{

           /* String currentID = idPoint.getText().toString();

            if(!idPoints.get(index).getText().toString().equals(currentID)){
                CreateNewProject.map.put(currentID, DataProjectSingleton.getInstance().points.remove(id));
                idPoints.get(index).setText(currentID);
            }

            MyGPS pos = new MyGPS();

            pos.setLatitude(Double.parseDouble(x.getText().toString()));
            pos.setLongitude(Double.parseDouble(y.getText().toString()));
            pos.setQuota(DataProjectSingleton.getInstance().points.get(currentID).getQuota());

            DataProjectSingleton.getInstance().updateCoordinate(currentID, pos);

            infoDialog.dismiss();*/

        });

        infoDialog.show();
    }


    public static int convertDpToPixel(float dp, Context context){
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    private class ReadAndCreateView implements Runnable {

        //LinkedHashMap<String, GPS_WGS84> datas;

        ReadAndCreateView() {
            //datas = DataProjectSingleton.getInstance().points;
        }

        @Override
        public void run() {
            new Handler(getMainLooper()).post(() -> {
               /* if (!datas.isEmpty()) {
                    for (Map.Entry<String, GPS_WGS84> entry : datas.entrySet()) {
                        String key = entry.getKey();
                        addCoordinate(key);
                    }
                }*/
                updateClickLister();
                caricamento.setVisibility(View.GONE);
                onClick();
            });
        }
    }
}
