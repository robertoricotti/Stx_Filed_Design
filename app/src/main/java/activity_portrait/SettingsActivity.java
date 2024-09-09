package activity_portrait;

import static can.Can_Decoder.Deg_pitch;
import static can.Can_Decoder.Deg_roll;
import static can.Can_Decoder.correctPitch;
import static can.Can_Decoder.correctRoll;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import utils.Utils;

public class SettingsActivity extends AppCompatActivity {
    CheckBox cbMeters, cbUSF, cbDeg, cbPcent;
    ImageView img_connect, imgTest;
    TextView  txtsmootRmc, txt_tilt;
    TextView tv1;
    EditText et1;
    private Handler handler;
    EditText xyTol, zTol, tiltTol,hdtTol;
    private boolean mRunning = true;
    SeekBar seekRmc;
    CheckBox ckrmc, ckpos, ckhdt, usetilt, useCircle, useTriang;
    Button calib;
    int index;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findView();
        init();
        onClick();
        updateUI();
    }

    private void findView() {
        index = Integer.parseInt(new MyRW_IntMem().MyRead("_unitofmeasure", this));
        cbMeters = findViewById(R.id.ckMetri);
        cbUSF = findViewById(R.id.ckPiedi);
        cbDeg = findViewById(R.id.ckDeg);
        cbPcent = findViewById(R.id.ckPercent);
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
        hdtTol=findViewById(R.id.hdt_tol);
        tv1=findViewById(R.id.txt_unit);
        et1=findViewById(R.id.et_z_h);
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
        xyTol.setText(Utils.readUnitOfMeasure(String.valueOf(DataSaved.xy_tol),SettingsActivity.this).replace(",", "."));
        zTol.setText(Utils.readUnitOfMeasure(String.valueOf(DataSaved.z_tol),SettingsActivity.this).replace(",", "."));
        tiltTol.setText(String.format("%.1f", DataSaved.tilt_Tol).replace(",", "."));
        hdtTol.setText(String.format("%.1f", DataSaved.hdt_Tol).replace(",", "."));
        String depth = Utils.readUnitOfMeasure(String.valueOf(DataSaved.D_AltezzaAnt), this);
        et1.setText(depth);
        tv1.setText(Utils.getMetriSimbol(SettingsActivity.this));
        switch (index) {
            case 0:
                cbMeters.setChecked(true);
                cbUSF.setChecked(false);
                cbDeg.setChecked(true);
                cbPcent.setChecked(false);
                break;
            case 1:
                cbMeters.setChecked(true);
                cbUSF.setChecked(false);
                cbDeg.setChecked(false);
                cbPcent.setChecked(true);
                break;
            case 2:
                cbMeters.setChecked(false);
                cbUSF.setChecked(true);
                cbDeg.setChecked(true);
                cbPcent.setChecked(false);
                break;
            case 3:
                cbMeters.setChecked(false);
                cbUSF.setChecked(true);
                cbDeg.setChecked(false);
                cbPcent.setChecked(true);
                break;
        }
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
        cbMeters.setOnClickListener(view -> {
            cbMeters.setChecked(true);
            cbUSF.setChecked(false);
            updateCB();
        });
        cbUSF.setOnClickListener(view -> {
            cbUSF.setChecked(true);
            cbMeters.setChecked(false);
            updateCB();
        });
        cbDeg.setOnClickListener(view -> {
            cbDeg.setChecked(true);
            cbPcent.setChecked(false);
            updateCB();
        });
        cbPcent.setOnClickListener(view -> {
            cbPcent.setChecked(true);
            cbDeg.setChecked(false);
            updateCB();
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
                            if(DataSaved.useRmc==2){
                                seekRmc.setVisibility(View.GONE);
                                txtsmootRmc.setVisibility(View.GONE);
                            }else {
                                seekRmc.setVisibility(View.VISIBLE);
                                txtsmootRmc.setVisibility(View.VISIBLE);
                            }

                                txt_tilt.setText(String.valueOf("Pitch: " + String.format("%.2f", correctPitch) + "°       Roll: " + String.format("%.2f", correctRoll) + "°"));

                                txtsmootRmc.setText("BEARING SMOOTH: \t\t" + String.format("%.1f",(float)DataSaved.rmcSize/100));


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
        new MyRW_IntMem().MyWrite("_altezzaantenna", Utils.writeMetri(et1.getText().toString(), SettingsActivity.this), SettingsActivity.this);
        DataSaved.D_AltezzaAnt = Double.parseDouble(new MyRW_IntMem().MyRead("_altezzaantenna", SettingsActivity.this)) ;
        if (!xyTol.getText().toString().equals("")) {
            new MyRW_IntMem().MyWrite("xy_tol",Utils.writeMetri(xyTol.getText().toString(),this), this);
        }
        if (!zTol.getText().toString().equals("")) {
            new MyRW_IntMem().MyWrite("z_tol", Utils.writeMetri(zTol.getText().toString(),this), this);
        }
        if (!tiltTol.getText().toString().equals("")) {
            new MyRW_IntMem().MyWrite("tilt_tol", tiltTol.getText().toString(), this);
        }
        if (!hdtTol.getText().toString().equals("")) {
            new MyRW_IntMem().MyWrite("hdt_tol", hdtTol.getText().toString(), this);
        }
        startService(new Intent(SettingsActivity.this, UpdateValues.class));
        Toast.makeText(this, "SAVED!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
    private void updateCB() {
        int out = 0;
        if (cbMeters.isChecked() && !cbUSF.isChecked()) {
            if (cbDeg.isChecked() && !cbPcent.isChecked()) {
                out = 0;
            } else if (cbPcent.isChecked() && !cbDeg.isChecked()) {
                out = 1;
            }
        } else if (!cbMeters.isChecked() && cbUSF.isChecked()) {
            if (cbDeg.isChecked() && !cbPcent.isChecked()) {
                out = 2;
            } else if (cbPcent.isChecked() && !cbDeg.isChecked()) {
                out = 3;
            }
        }
        new MyRW_IntMem().MyWrite("_unitofmeasure", String.valueOf(out), SettingsActivity.this);

    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunning = false;
    }
}