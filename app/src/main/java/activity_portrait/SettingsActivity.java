package activity_portrait;

import static can.Can_Decoder.Deg_pitch;
import static can.Can_Decoder.Deg_roll;
import static can.Can_Decoder.correctPitch;
import static can.Can_Decoder.correctRoll;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stx_field_design.R;

import services_and_bluetooth.DataSaved;
import services_and_bluetooth.UpdateValues;
import utils.MyRW_IntMem;

public class SettingsActivity extends AppCompatActivity {

    ImageView img_connect, imgTest;
    TextView  txtsmootRmc, txt_tilt;
    private Handler handler;
    EditText xyTol, zTol, tiltTol;
    private boolean mRunning = true;
    SeekBar seekRmc;
    CheckBox ckrmc, ckpos, ckhdt, usetilt, useCircle, useTriang;
    Button calib;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findView();
        init();
        onClick();
        updateUI();
    }

    private void findView() {

        img_connect = findViewById(R.id.img_connetti);
        seekRmc = findViewById(R.id.seekRmc);
        txtsmootRmc = findViewById(R.id.txtOrSmooth);
        seekRmc.setProgress(DataSaved.rmcSize);
        ckrmc = findViewById(R.id.ckRMC);
        ckpos = findViewById(R.id.ckPos);
        imgTest = findViewById(R.id.imgTest);
        xyTol = findViewById(R.id.xy_tol);
        zTol = findViewById(R.id.z_tol);
        ckhdt = findViewById(R.id.ckhdt);
        txt_tilt = findViewById(R.id.txt_tilt);
        calib = findViewById(R.id.calibrateTilt);
        usetilt = findViewById(R.id.ckUseTilt);
        tiltTol = findViewById(R.id.tilt_tol);
        useCircle = findViewById(R.id.ckPalina);
        useTriang = findViewById(R.id.ckNav);
        if (DataSaved.useTilt == 0) {
            usetilt.setChecked(false);
        } else if (DataSaved.useTilt == 1) {
            usetilt.setChecked(true);
        }

        if (DataSaved.useRmc == 0) {
            ckrmc.setChecked(true);
            ckpos.setChecked(false);
            ckhdt.setChecked(false);
        } else if (DataSaved.useRmc == 1) {
            ckpos.setChecked(true);
            ckrmc.setChecked(false);
            ckhdt.setChecked(false);
        } else if (DataSaved.useRmc == 2) {
            ckpos.setChecked(false);
            ckrmc.setChecked(false);
            ckhdt.setChecked(true);
        }
        if (DataSaved.imgMode == 0) {
            useCircle.setChecked(true);
            useTriang.setChecked(false);
        } else {
            useCircle.setChecked(false);
            useTriang.setChecked(true);
        }


    }

    private void init() {
        xyTol.setText(String.format("%.3f", DataSaved.xy_tol).replace(",", "."));
        zTol.setText(String.format("%.3f", DataSaved.z_tol).replace(",", "."));
        tiltTol.setText(String.format("%.1f", DataSaved.tilt_Tol).replace(",", "."));
    }

    private void onClick() {
        calib.setOnClickListener(view -> {
            DataSaved.offsetRoll = Deg_roll;
            DataSaved.offsetPitch = Deg_pitch;
            new MyRW_IntMem().MyWrite("_offsetpitch", String.valueOf(DataSaved.offsetPitch), SettingsActivity.this);
            new MyRW_IntMem().MyWrite("_offsetroll", String.valueOf(DataSaved.offsetRoll), SettingsActivity.this);
            Log.d("calibraz", "offp: " + DataSaved.offsetPitch + "  offroll:  " + DataSaved.offsetRoll);
        });

        usetilt.setOnClickListener(view -> {
            if (usetilt.isChecked()) {
                DataSaved.useTilt = 1;
            } else {
                DataSaved.useTilt = 0;
            }
            new MyRW_IntMem().MyWrite("_usetilt", String.valueOf(DataSaved.useTilt), SettingsActivity.this);
        });

        ckrmc.setOnClickListener(view -> {
            ckpos.setChecked(false);
            ckrmc.setChecked(true);
            ckhdt.setChecked(false);
            DataSaved.useRmc = 0;
            new MyRW_IntMem().MyWrite("useRmc", String.valueOf(DataSaved.useRmc), SettingsActivity.this);
        });
        ckpos.setOnClickListener(view -> {
            ckpos.setChecked(true);
            ckrmc.setChecked(false);
            ckhdt.setChecked(false);
            DataSaved.useRmc = 1;
            new MyRW_IntMem().MyWrite("useRmc", String.valueOf(DataSaved.useRmc), SettingsActivity.this);
        });
        ckhdt.setOnClickListener(view -> {
            ckpos.setChecked(false);
            ckrmc.setChecked(false);
            ckhdt.setChecked(true);
            DataSaved.useRmc = 2;
            new MyRW_IntMem().MyWrite("useRmc", String.valueOf(DataSaved.useRmc), SettingsActivity.this);
        });
        useCircle.setOnClickListener(view -> {
            useCircle.setChecked(true);
            useTriang.setChecked(false);
            DataSaved.imgMode = 0;
            new MyRW_IntMem().MyWrite("imgMode", String.valueOf(DataSaved.imgMode), SettingsActivity.this);
        });
        useTriang.setOnClickListener(view -> {
            useTriang.setChecked(true);
            useCircle.setChecked(false);
            DataSaved.imgMode = 1;
            new MyRW_IntMem().MyWrite("imgMode", String.valueOf(DataSaved.imgMode), SettingsActivity.this);
        });


        seekRmc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                DataSaved.rmcSize = i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                new MyRW_IntMem().MyWrite("rmcSize", String.valueOf(DataSaved.rmcSize), SettingsActivity.this);

            }
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

                                txt_tilt.setText(String.valueOf("Pitch: " + String.format("%.2f", correctPitch) + "°       Roll: " + String.format("%.2f", correctRoll) + "°"));

                                txtsmootRmc.setText("Bearing min Dist: \t\t" + String.format("%.1f",(float)DataSaved.rmcSize/100));


                            if (DataSaved.imgMode == 0) {
                                imgTest.setImageResource(R.drawable.circle_96);
                            } else {
                                imgTest.setImageResource(R.drawable.play_96);
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

    public void metodoSave() {
        if (!xyTol.getText().toString().equals("")) {
            new MyRW_IntMem().MyWrite("xy_tol", xyTol.getText().toString(), this);
        }
        if (!zTol.getText().toString().equals("")) {
            new MyRW_IntMem().MyWrite("z_tol", zTol.getText().toString(), this);
        }
        if (!tiltTol.getText().toString().equals("")) {
            new MyRW_IntMem().MyWrite("tilt_tol", tiltTol.getText().toString(), this);
        }
        startService(new Intent(SettingsActivity.this, UpdateValues.class));
        Toast.makeText(this, "SAVED!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunning = false;
    }
}