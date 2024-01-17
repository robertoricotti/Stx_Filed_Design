package activity_portrait;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stx_field_design.R;

import coords_calc.GPS;
import dialogs.Dialog_Offset;
import gnss.Nmea_In;
import project.DataProjectSingleton;
import services_and_bluetooth.DataSaved;
import utils.Utils;

public class P_WorkActivity extends AppCompatActivity {
    TextView freccia, quota,offset;
    LinearLayout linearLayout;
    private boolean mRunning = true;
    private Handler handler;
    DataProjectSingleton dataProject;
    static double quots=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        updateUI();
    }

    private void findView() {
        freccia = findViewById(R.id.statusSurf);
        quota = findViewById(R.id.distSurf);
        linearLayout = findViewById(R.id.backLayour);
        dataProject = DataProjectSingleton.getInstance();
        offset=findViewById(R.id.txtoff);

        offset.setOnClickListener(view -> {
            new Dialog_Offset(this,quots).show();
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
                            offset.setText("Offset: "+Utils.readUnitOfMeasure(String.valueOf(DataSaved.D_Offset),P_WorkActivity.this));
                            GPS myP = dataProject.getPoints().get("P");//coordinate misurate di A
                            double d = myP.getZ();

                            quots = Nmea_In.Quota1-d - DataSaved.D_Offset;
                            quota.setText(Utils.readUnitOfMeasure(String.valueOf(quots), P_WorkActivity.this));
                            if (Math.abs(quots) <= DataSaved.z_tol) {
                                linearLayout.setBackgroundColor(Color.GREEN);

                                quota.setTextColor(Color.DKGRAY);
                                freccia.setTextColor(Color.DKGRAY);
                                offset.setTextColor(Color.DKGRAY);
                                freccia.setText("=");
                            }
                            if (quots > (DataSaved.z_tol + 0.001)) {
                                //linearLayout.setBackgroundColor(Color.BLUE);
                                linearLayout.setBackground(getDrawable(R.drawable.custom_background_transp));
                                quota.setTextColor(Color.BLUE);
                                freccia.setTextColor(Color.BLUE);
                                offset.setTextColor(Color.BLUE);
                                freccia.setText("▼");
                            }
                            if (quots < -(DataSaved.z_tol + 0.001)) {
                                //linearLayout.setBackgroundColor(Color.RED);
                                linearLayout.setBackground(getDrawable(R.drawable.custom_background_transp));
                                quota.setTextColor(Color.RED);
                                freccia.setTextColor(Color.RED);
                                offset.setTextColor(Color.RED);
                                freccia.setText("▲");
                            }


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
}