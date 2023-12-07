package activity_portrait;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.stx_field_design.R;

import services_and_bluetooth.DataSaved;
import services_and_bluetooth.UpdateValues;
import utils.MyRW_IntMem;
import utils.Utils;

public class Antennas_Blade_Activity extends AppCompatActivity {
    TextView tv1,tv2,tv3;
    EditText et1,et2,et3;
    MyRW_IntMem myRW_intMem;

    int indexMeasure;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        init();
        onClick();
    }

    private void findView(){
        tv1=findViewById(R.id.txt_unit);
        tv2=findViewById(R.id.txt_unit2);
        tv3=findViewById(R.id.txt_unit3);
        et1=findViewById(R.id.et_z_h);
        et2=findViewById(R.id.et_left_edge);
        et3=findViewById(R.id.et_right_edge);


    }
    private void init(){
        et2.setAlpha(0.3f);
        et3.setAlpha(0.3f);
        tv2.setAlpha(0.3f);
        tv3.setAlpha(0.3f);
        et2.setEnabled(false);
        et3.setEnabled(false);
        myRW_intMem = new MyRW_IntMem();
        indexMeasure = Integer.parseInt(new MyRW_IntMem().MyRead("_unitofmeasure",this));
        String depth = Utils.readUnitOfMeasure(String.valueOf(DataSaved.D_AltezzaAnt), this);
        String left = Utils.readUnitOfMeasure(String.valueOf(DataSaved.D_Leftedge), this);
        String right = Utils.readUnitOfMeasure(String.valueOf(DataSaved.D_Rightedge), this);
        et1.setText(depth);
        et2.setText(left);
        et3.setText(right);
        tv1.setText(Utils.getMetriSimbol(Antennas_Blade_Activity.this));
        tv2.setText(Utils.getMetriSimbol(Antennas_Blade_Activity.this));
        tv3.setText(Utils.getMetriSimbol(Antennas_Blade_Activity.this));
    }

    private void onClick(){

    }

    public void saveData(){
        myRW_intMem.MyWrite("_altezzaantenna", Utils.writeMetri(et1.getText().toString(), Antennas_Blade_Activity.this), Antennas_Blade_Activity.this);
        DataSaved.D_AltezzaAnt = Double.parseDouble(myRW_intMem.MyRead("_altezzaantenna", Antennas_Blade_Activity.this)) ;
        myRW_intMem.MyWrite("_leftedge", Utils.writeMetri(et2.getText().toString(), Antennas_Blade_Activity.this), Antennas_Blade_Activity.this);
        DataSaved.D_Leftedge = Double.parseDouble(myRW_intMem.MyRead("_altezzaantenna", Antennas_Blade_Activity.this)) ;
        myRW_intMem.MyWrite("_rightedge", Utils.writeMetri(et3.getText().toString(), Antennas_Blade_Activity.this), Antennas_Blade_Activity.this);
        DataSaved.D_Rightedge = Double.parseDouble(myRW_intMem.MyRead("_altezzaantenna", Antennas_Blade_Activity.this)) ;
        startService(new Intent(Antennas_Blade_Activity.this, UpdateValues.class));
    }

    @Override
    public void onBackPressed() {

    }
}
