package activity_portrait;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.stx_field_design.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import eventbus.CanEvents;

public class CAN_DebugActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> itemList;
    public static String s="";
    public boolean play=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView=findViewById(R.id.listview);
        itemList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.layout_custom_spinner, itemList);
        listView.setAdapter(adapter);

    }


    @Override
    protected void onStart() {
        super.onStart();
        //EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void CanEvents(CanEvents canEvents) {
        if(play) {
            itemList.add(canEvents.candata);
            adapter.notifyDataSetChanged();
            listView.smoothScrollToPositionFromTop(0,itemList.size() - 1);
            if(adapter.getCount()>100){
                clearList();
            }
        }
    }
    public void playpause(){
        play=!play;
    }
    public void clearList(){
        adapter.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //EventBus.getDefault().unregister(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }
}