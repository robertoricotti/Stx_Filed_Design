package activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.stx_field_design.R;

import dialogs.CloseAppDialog;
import services.DataSaved;
import utils.FullscreenActivity;

public class MainActivity extends AppCompatActivity {
    ImageView btn_exit, btn_tognss, to_bt, to_files, to_new, to_settings, to_stakeout;
    private Handler handler;
    private boolean mRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FullscreenActivity.setFullScreen(this);
        findView();
        onClick();
        updateUI();

    }

    private void findView() {
        btn_exit = findViewById(R.id.btn_exit);
        btn_tognss = findViewById(R.id.btn_tognss);
        to_bt = findViewById(R.id.img1);
        to_files = findViewById(R.id.img2);
        to_new = findViewById(R.id.img3);
        to_settings = findViewById(R.id.img4);
        to_stakeout = findViewById(R.id.img7);
    }

    @SuppressLint("NewApi")
    private void onClick() {
        btn_exit.setOnClickListener(view -> {
            new CloseAppDialog(this).show();
        });
        to_bt.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, BT_DevicesActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        to_files.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, FilesActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        to_new.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, NewProjectActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        to_settings.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, NewProjectActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        to_stakeout.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, StakeOutactivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private void updateUI() {

        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRunning) {


                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                        }
                    });
                    // sleep per intervallo update UI
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunning = false;
    }

    @Override
    public void onBackPressed() {

    }
}