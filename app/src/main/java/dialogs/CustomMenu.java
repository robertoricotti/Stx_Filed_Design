package dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.example.stx_field_design.R;

import java.util.List;

import utils.FullscreenActivity;

public class CustomMenu {
    private Activity activity;
    private TextView title;
    private ListView listView;
    private String whoCall;
    Button close;
    private ArrayAdapter<String> listAdapter;

    private Dialog alertDialog;

    public CustomMenu(Activity activity, String titolo) {
        this.activity = activity;
        alertDialog = new Dialog(activity, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        listAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1);
        whoCall = titolo;
    }

    public void show(List<String> listItems, final OnItemSelectedListener itemSelectedListener) {

        alertDialog.create();
        alertDialog.setContentView(R.layout.layout_custom_menu);
        alertDialog.setCancelable(false);

        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        FullscreenActivity.setFullScreen(alertDialog);
        alertDialog.show();
        findView();
        setupListView(listItems, itemSelectedListener);
    }

    private void findView() {
        title = alertDialog.findViewById(R.id.menuTitle);
        listView = alertDialog.findViewById(R.id.listView);
        close=alertDialog.findViewById(R.id.chiudi);
        title.setText(whoCall);
        close.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
    }

    private void setupListView(List<String> listItems, final OnItemSelectedListener itemSelectedListener) {
        listView.setAdapter(listAdapter);
        listAdapter.clear();
        listAdapter.addAll(listItems);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
                String selectedItem = listAdapter.getItem(position);
                itemSelectedListener.onItemSelected(selectedItem); // Restituisci il valore selezionato
                alertDialog.dismiss(); // Chiudi la dialog
            }
        });
    }

    public interface OnItemSelectedListener {
        void onItemSelected(String selectedItem);
    }
}
