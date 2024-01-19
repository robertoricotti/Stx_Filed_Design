package activity_portrait;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.stx_field_design.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import eventbus.CanEvents;
import eventbus.GpsEvents;

public class Debug_Activity extends AppCompatActivity {
    ImageView playG, pauseG, clearG, playC, pauseC, clearC;
    private ListView listViewG, listViewC;
    private ArrayAdapter<String> adapterG, adapterC;
    private ArrayList<String> itemListG, itemListC;
    private boolean b_playG = true;

    private boolean b_playC = false;
    TextView txtG,txtC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        onClick();
        //updateC();
        updateG();
        txtC.setText("DEBUG CAN");
        txtG.setText("DEBUG NMEA");



    }
    private void updateC(){
        if(b_playC){
            //txtC.setText("DEBUG CAN PLAY");
            playC.setImageTintList(getColorStateList(R.color.blue));
            pauseC.setImageTintList(getColorStateList(R.color._____cancel_text));
        }else {
            //txtC.setText("DEBUG CAN STOP");
            playC.setImageTintList(getColorStateList(R.color._____cancel_text));
            pauseC.setImageTintList(getColorStateList(R.color.blue));
        }
    }
    private void updateG(){
        if(b_playG){
            //txtG.setText("DEBUG NMEA PLAY");
            playG.setImageTintList(getColorStateList(R.color.blue));
            pauseG.setImageTintList(getColorStateList(R.color._____cancel_text));
        }else {
            //txtG.setText("DEBUG NMEA STOP");
            playG.setImageTintList(getColorStateList(R.color._____cancel_text));
            pauseG.setImageTintList(getColorStateList(R.color.blue));
        }
    }

    private void findView() {
        txtC=findViewById(R.id.txtc);
        txtG=findViewById(R.id.txtg);
        playG = findViewById(R.id.play_gps);
        pauseG = findViewById(R.id.pause_gps);
        clearG = findViewById(R.id.clear_gps);
        playC = findViewById(R.id.play_can);
        pauseC = findViewById(R.id.pause_can);
        clearC = findViewById(R.id.clear_can);
        listViewG = findViewById(R.id.listView_GPS);
        listViewC = findViewById(R.id.listView_CAN);
        itemListG = new ArrayList<>();
        adapterG = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 , itemListG);
        listViewG.setAdapter(adapterG);

        itemListC = new ArrayList<>();
        adapterC = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 , itemListC);
        listViewC.setAdapter(adapterC);
    }

    private void onClick() {
        playG.setOnClickListener(view -> {
            b_playG = true;
            updateG();

        });
        pauseG.setOnClickListener(view -> {
            b_playG = false;
            updateG();

        });
        clearG.setOnClickListener(view -> {
            clearListG();
            updateG();

        });
        playC.setOnClickListener(view -> {
            //b_playC = true;
            //updateC();

        });
        pauseC.setOnClickListener(view -> {
            //b_playC = false;
           // updateC();

        });
        clearC.setOnClickListener(view -> {
            clearListC();
            //updateC();
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void GpsEvents(GpsEvents gpsEvents) {
        if (b_playG) {
            itemListG.add(gpsEvents.gpsdata);
            adapterG.notifyDataSetChanged();
            listViewG.smoothScrollToPositionFromTop(0, itemListG.size() - 1);
            if (adapterG.getCount() > 100) {
                clearListG();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void CanEvents(CanEvents canEvents) {
        if (b_playC) {
            itemListC.add(canEvents.candata);
            adapterC.notifyDataSetChanged();
            listViewC.smoothScrollToPositionFromTop(0, itemListC.size() - 1);
            if (adapterC.getCount() > 100) {
                clearListC();
            }
        }
    }

    public void clearListG() {
        adapterG.clear();
    }

    public void clearListC() {
        adapterC.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}