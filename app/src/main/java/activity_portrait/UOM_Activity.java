package activity_portrait;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;

import com.example.stx_field_design.R;

import utils.MyRW_IntMem;

public class UOM_Activity extends AppCompatActivity {
    CheckBox cbMeters,cbUSF,cbDeg,cbPcent;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        init();
        onClick();


    }

    private void findView(){
        index=Integer.parseInt(new MyRW_IntMem().MyRead("_unitofmeasure",this));
        cbMeters=findViewById(R.id.ckMetri);
        cbUSF=findViewById(R.id.ckPiedi);
        cbDeg=findViewById(R.id.ckDeg);
        cbPcent=findViewById(R.id.ckPercent);
    }
    private void init(){
        switch (index){
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

    private void onClick(){
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
    private void updateCB(){
        int out=0;
        if(cbMeters.isChecked()&&!cbUSF.isChecked()){
            if(cbDeg.isChecked()&&!cbPcent.isChecked()){
                out= 0;
            }else if(cbPcent.isChecked()&&!cbDeg.isChecked()){
                out= 1;
            }
        }else if(!cbMeters.isChecked()&&cbUSF.isChecked()){
            if(cbDeg.isChecked()&&!cbPcent.isChecked()){
                out= 2;
            }else if(cbPcent.isChecked()&&!cbDeg.isChecked()){
                out= 3;
            }
        }
        Log.d("UPDATE_CB", String.valueOf(out));
        new MyRW_IntMem().MyWrite("_unitofmeasure", String.valueOf(out),UOM_Activity.this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}