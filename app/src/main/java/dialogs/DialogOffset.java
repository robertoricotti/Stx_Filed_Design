package dialogs;


import static utils.Utils.isNumeric;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;


import com.example.stx_field_design.R;

import services_and_bluetooth.DataSaved;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;
import utils.Utils;

public class DialogOffset {
    Activity activity;
    Dialog dialog;
    Button canc, save,  reverse;
    EditText value, value_ft;
    TextView  measure, measure_ft;
    Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b0, bdot, bcanc, bdel;



    boolean c = true;

    MyRW_IntMem myRW_intMem;

    int indexMeasure;
    int indexFtIn;


    public DialogOffset(Activity activity) {
        this.activity = activity;
        dialog = new Dialog(activity, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
    }

    public void show(){
        dialog.create();
        dialog.setContentView(R.layout.offset);
        dialog.setCancelable(false);
        dialog.show();
        FullscreenActivity.setFullScreen(dialog);
        findView();
        init();
        onClick();
    }

    private void init(){
        myRW_intMem = new MyRW_IntMem();
        indexMeasure = DataSaved.I_UnitOfMeasure;
        if(indexMeasure >0){
            measure.setVisibility(View.GONE);
            value.setVisibility(View.GONE);

            value_ft.setVisibility(View.VISIBLE);
            measure_ft.setVisibility(View.VISIBLE);
            indexFtIn = 0;
            value_ft.setBackgroundColor(ContextCompat.getColor(activity, R.color.light_yellow));
            String depth = Utils.readUnitOfMeasure(String.valueOf(DataSaved.D_AltezzaAnt), activity);

            value_ft.setText(depth.split("'")[0].trim());
        }
        else {
            indexFtIn = 0;
            measure.setVisibility(View.VISIBLE);
            value.setVisibility(View.VISIBLE);
            value_ft.setVisibility(View.GONE);
            measure_ft.setVisibility(View.GONE);

            value.setText(Utils.readUnitOfMeasure(String.valueOf(DataSaved.D_AltezzaAnt),activity));
            measure.setText(Utils.getMetriSimbol(activity));
        }

    }

    private void findView(){

        canc = dialog.findViewById(R.id.exit);
        save = dialog.findViewById(R.id.save);
        value = dialog.findViewById(R.id.value);

        reverse = dialog.findViewById(R.id.inverted);
        b1 = dialog.findViewById(R.id.b1);
        b2 = dialog.findViewById(R.id.b2);
        b3 = dialog.findViewById(R.id.b3);
        b4 = dialog.findViewById(R.id.b4);
        b5 = dialog.findViewById(R.id.b5);
        b6 = dialog.findViewById(R.id.b6);
        b7 = dialog.findViewById(R.id.b7);
        b8 = dialog.findViewById(R.id.b8);
        b9 = dialog.findViewById(R.id.b9);
        b0 = dialog.findViewById(R.id.b0);
        bdot = dialog.findViewById(R.id.bdot);
        bcanc = dialog.findViewById(R.id.bc);
        measure = dialog.findViewById(R.id.unitOfMeasure);

        bdel = dialog.findViewById(R.id.bdel);
        measure_ft = dialog.findViewById(R.id.measure_ft);

        value_ft = dialog.findViewById(R.id.value_ft);

    }

    private void onClick() {
        save.setOnClickListener((View v) -> {
            if(indexMeasure >0){
                if(isNumeric(value_ft.getText().toString()) ){
                    DataSaved.D_AltezzaAnt = Double.parseDouble(myRW_intMem.MyRead("_altezzaantenna", activity)) ;
                    dialog.cancel();
                }
                else{
                    new CustomToast(activity, "Error INPUT!").show();
                }
            }
            else{
                if(isNumeric(value.getText().toString())){
                    myRW_intMem.MyWrite("_altezzaantenna", Utils.writeMetri(value.getText().toString(), activity), activity);
                    DataSaved.D_AltezzaAnt = Double.parseDouble(myRW_intMem.MyRead("_altezzaantenna", activity)) ;
                    dialog.cancel();
                }
                else{
                    new CustomToast(activity, "Error INPUT!").show();
                }
            }
        });


        canc.setOnClickListener((View v) -> {
            dialog.cancel();
        });





        reverse.setOnClickListener((View v) -> {
            if(indexMeasure >0){
                if(!value_ft.getText().toString().equals("")){
                    if(!value_ft.getText().toString().contains("-")){
                        value_ft.setText(value_ft.getText().insert(0, "-"));
                    }
                    else {
                        value_ft.setText(value_ft.getText().toString().replace("-", ""));
                    }
                }
            }
            else {
                if(!value.getText().toString().equals("")){
                    if(!value.getText().toString().contains("-")){
                        value.setText(value.getText().insert(0, "-"));
                    }
                    else {
                        value.setText(value.getText().toString().replace("-", ""));
                    }
                }
            }
        });

        value_ft.setOnClickListener((View v) -> {
            indexFtIn = 0;
            c = true;
            value_ft.setBackgroundColor(ContextCompat.getColor(activity, R.color.light_yellow));
        });





        b1.setOnClickListener((View v) -> {
            if(indexMeasure >0){
                if(c){
                    if(indexFtIn == 0){
                        value_ft.setText("");
                    }
                    else {
                    }
                    c = false;
                }
                if(indexFtIn == 0){
                    value_ft.setText(value_ft.getText().toString().concat("1"));
                }
                else {
                }
            }
            else{
                if(c){
                    value.setText("");
                    c = false;
                }
                value.setText(value.getText().toString().concat("1"));
            }
        });

        b2.setOnClickListener((View v) -> {
            if(indexMeasure>0){
                if(c){
                    if(indexFtIn == 0){
                        value_ft.setText("");
                    }
                    else {
                    }
                    c = false;
                }
                if(indexFtIn == 0){
                    value_ft.setText(value_ft.getText().toString().concat("2"));
                }
                else {
                }
            }
            else{
                if(c){
                    value.setText("");
                    c = false;
                }
                value.setText(value.getText().toString().concat("2"));
            }
        });

        b3.setOnClickListener((View v) -> {
            if(indexMeasure >0){
                if(c){
                    if(indexFtIn == 0){
                        value_ft.setText("");
                    }
                    else {
                    }
                    c = false;
                }
                if(indexFtIn == 0){
                    value_ft.setText(value_ft.getText().toString().concat("3"));
                }
                else {
                }
            }
            else{
                if(c){
                    value.setText("");
                    c = false;
                }
                value.setText(value.getText().toString().concat("3"));
            }
        });

        b4.setOnClickListener((View v) -> {
            if(indexMeasure>0){
                if(c){
                    if(indexFtIn == 0){
                        value_ft.setText("");
                    }
                    else {
                    }
                    c = false;
                }
                if(indexFtIn == 0){
                    value_ft.setText(value_ft.getText().toString().concat("4"));
                }
                else {
                }
            }
            else{
                if(c){
                    value.setText("");
                    c = false;
                }
                value.setText(value.getText().toString().concat("4"));
            }
        });

        b5.setOnClickListener((View v) -> {
            if(indexMeasure>0){
                if(c){
                    if(indexFtIn == 0){
                        value_ft.setText("");
                    }
                    else {
                    }
                    c = false;
                }
                if(indexFtIn == 0){
                    value_ft.setText(value_ft.getText().toString().concat("5"));
                }
                else {
                }
            }
            else{
                if(c){
                    value.setText("");
                    c = false;
                }
                value.setText(value.getText().toString().concat("5"));
            }
        });

        b6.setOnClickListener((View v) -> {
            if(indexMeasure>0){
                if(c){
                    if(indexFtIn == 0){
                        value_ft.setText("");
                    }
                    else {
                    }
                    c = false;
                }
                if(indexFtIn == 0){
                    value_ft.setText(value_ft.getText().toString().concat("6"));
                }
                else {
                }
            }
            else{
                if(c){
                    value.setText("");
                    c = false;
                }
                value.setText(value.getText().toString().concat("6"));
            }
        });

        b7.setOnClickListener((View v) -> {
            if(indexMeasure >0){
                if(c){
                    if(indexFtIn == 0){
                        value_ft.setText("");
                    }
                    else {
                    }
                    c = false;
                }
                if(indexFtIn == 0){
                    value_ft.setText(value_ft.getText().toString().concat("7"));
                }
                else {
                }
            }
            else{
                if(c){
                    value.setText("");
                    c = false;
                }
                value.setText(value.getText().toString().concat("7"));
            }

        });

        b8.setOnClickListener((View v) -> {
            if(indexMeasure >0){
                if(c){
                    if(indexFtIn == 0){
                        value_ft.setText("");
                    }
                    else {
                    }
                    c = false;
                }
                if(indexFtIn == 0){
                    value_ft.setText(value_ft.getText().toString().concat("8"));
                }
                else {
                }
            }
            else{
                if(c){
                    value.setText("");
                    c = false;
                }
                value.setText(value.getText().toString().concat("8"));
            }

        });

        b9.setOnClickListener((View v) -> {
            if(indexMeasure >0){
                if(c){
                    if(indexFtIn == 0){
                        value_ft.setText("");
                    }
                    else {
                    }
                    c = false;
                }
                if(indexFtIn == 0){
                    value_ft.setText(value_ft.getText().toString().concat("9"));
                }
                else {
                }
            }
            else{
                if(c){
                    value.setText("");
                    c = false;
                }
                value.setText(value.getText().toString().concat("9"));
            }

        });

        b0.setOnClickListener((View v) -> {
            if(indexMeasure >0){
                if(c){
                    if(indexFtIn == 0){
                        value_ft.setText("");
                    }
                    else {
                    }
                    c = false;
                }
                if(indexFtIn == 0){
                    value_ft.setText(value_ft.getText().toString().concat("0"));
                }
                else {
                }
            }
            else{
                if(c){
                    value.setText("");
                    c = false;
                }
                value.setText(value.getText().toString().concat("0"));
            }

        });

        bdot.setOnClickListener((View v) -> {
            if(indexMeasure >0){
                if(c){
                    if(indexFtIn == 0){
                        value_ft.setText("");
                    }
                    else {
                    }
                    c = false;
                }
                if(indexFtIn == 0){
                    value_ft.setText(value_ft.getText().toString().concat("."));
                }
                else {
                }
            }
            else{
                if(c){
                    value.setText("");
                    c = false;
                }
                value.setText(value.getText().toString().concat("."));
            }

        });

        bcanc.setOnClickListener((View v) -> {
            if(indexMeasure >0){
                if(indexFtIn == 0){
                    value_ft.setText("0");
                }
                else{
                }
            }
            else {
                value.setText("0");
            }
            c = true;

        });

        bdel.setOnClickListener((View v) -> {
            if(indexMeasure >0){
                if(indexFtIn == 0){
                    if(value_ft.getText().toString().length() > 0)
                        value_ft.setText(value_ft.getText().toString().substring(0, value_ft.getText().toString().length() - 1));
                }

            }
            else {
                if(value.getText().toString().length() > 0)
                    value.setText(value.getText().toString().substring(0, value.getText().toString().length() - 1));
            }

        });
    }


}


