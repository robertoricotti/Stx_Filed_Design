package activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stx_field_design.R;

import java.util.Arrays;

import dialogs.CustomToast;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;

public class ExcavatorMeasureXYZ extends AppCompatActivity {
    public static String[] pointSaved = new String[11];
    ImageView exit,calcola;
    EditText x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11;
    EditText y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11;
    EditText z1,z2,z3,z4,z5,z6,z7,z8,z9,z10,z11;

    double d_x1, d_x2, d_x3, d_x4, d_x5, d_x6, d_x7, d_x8, d_x9, d_x10, d_x11;
    double d_y1, d_y2, d_y3, d_y4, d_y5, d_y6, d_y7, d_y8, d_y9, d_y10, d_y11;
    double d_z1, d_z2, d_z3, d_z4, d_z5, d_z6, d_z7, d_z8, d_z9, d_z10, d_z11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excavator_measure_xyz);
        FullscreenActivity.setFullScreen(this);
        findView();
        init();
        onClick();


    }

    private void findView(){
        exit=findViewById(R.id.btn_exit);
        calcola=findViewById(R.id.btn_tognss);
        x1=findViewById(R.id.p1x);
        x2=findViewById(R.id.p2x);
        x3=findViewById(R.id.p3x);
        x4=findViewById(R.id.p4x);
        x5=findViewById(R.id.p5x);
        x6=findViewById(R.id.p6x);
        x7=findViewById(R.id.p7x);
        x8=findViewById(R.id.p8x);
        x9=findViewById(R.id.p9x);
        x10=findViewById(R.id.p10x);
        x11=findViewById(R.id.p11x);

        y1=findViewById(R.id.p1y);
        y2=findViewById(R.id.p2y);
        y3=findViewById(R.id.p3y);
        y4=findViewById(R.id.p4y);
        y5=findViewById(R.id.p5y);
        y6=findViewById(R.id.p6y);
        y7=findViewById(R.id.p7y);
        y8=findViewById(R.id.p8y);
        y9=findViewById(R.id.p9y);
        y10=findViewById(R.id.p10y);
        y11=findViewById(R.id.p11y);

        z1=findViewById(R.id.p1z);
        z2=findViewById(R.id.p2z);
        z3=findViewById(R.id.p3z);
        z4=findViewById(R.id.p4z);
        z5=findViewById(R.id.p5z);
        z6=findViewById(R.id.p6z);
        z7=findViewById(R.id.p7z);
        z8=findViewById(R.id.p8z);
        z9=findViewById(R.id.p9z);
        z10=findViewById(R.id.p10z);
        z11=findViewById(R.id.p11z);


    }
    private void onClick(){
        exit.setOnClickListener(view -> {
            startActivity(new Intent(ExcavatorMeasureXYZ.this, AntennaMeasure.class));

            finish();
        });
        calcola.setOnClickListener(view -> {
            makeCalc();
        });

    }
    private void makeCalc(){
        try {
            d_x1 = parseDoubleOrZero(x1.getText().toString());
            d_y1 = parseDoubleOrZero(y1.getText().toString());
            d_z1 = parseDoubleOrZero(z1.getText().toString());

            d_x2 = parseDoubleOrZero(x2.getText().toString());
            d_y2 = parseDoubleOrZero(y2.getText().toString());
            d_z2 = parseDoubleOrZero(z2.getText().toString());

            d_x3 = parseDoubleOrZero(x3.getText().toString());
            d_y3 = parseDoubleOrZero(y3.getText().toString());
            d_z3 = parseDoubleOrZero(z3.getText().toString());

            d_x4 = parseDoubleOrZero(x4.getText().toString());
            d_y4 = parseDoubleOrZero(y4.getText().toString());
            d_z4 = parseDoubleOrZero(z4.getText().toString());

            d_x5 = parseDoubleOrZero(x5.getText().toString());
            d_y5 = parseDoubleOrZero(y5.getText().toString());
            d_z5 = parseDoubleOrZero(z5.getText().toString());

            d_x6 = parseDoubleOrZero(x6.getText().toString());
            d_y6 = parseDoubleOrZero(y6.getText().toString());
            d_z6 = parseDoubleOrZero(z6.getText().toString());

            d_x7 = parseDoubleOrZero(x7.getText().toString());
            d_y7 = parseDoubleOrZero(y7.getText().toString());
            d_z7 = parseDoubleOrZero(z7.getText().toString());

            d_x8 = parseDoubleOrZero(x8.getText().toString());
            d_y8 = parseDoubleOrZero(y8.getText().toString());
            d_z8 = parseDoubleOrZero(z8.getText().toString());

            d_x9 = parseDoubleOrZero(x9.getText().toString());
            d_y9 = parseDoubleOrZero(y9.getText().toString());
            d_z9 = parseDoubleOrZero(z9.getText().toString());

            d_x10 = parseDoubleOrZero(x10.getText().toString());
            d_y10 = parseDoubleOrZero(y10.getText().toString());
            d_z10 = parseDoubleOrZero(z10.getText().toString());

            d_x11 = parseDoubleOrZero(x11.getText().toString());
            d_y11 = parseDoubleOrZero(y11.getText().toString());
            d_z11 = parseDoubleOrZero(z11.getText().toString());

            updateUI();
            savePoints();
            new MyRW_IntMem().MyWrite("pointssaved", Arrays.toString(pointSaved),ExcavatorMeasureXYZ.this);


        } catch (Exception e) {
            new CustomToast(ExcavatorMeasureXYZ.this,e.toString()).show();
        }

    }
    private void updateUI(){
        x1.setText(String.valueOf(d_x1));
        y1.setText(String.valueOf(d_y1));
        z1.setText(String.valueOf(d_z1));

        x2.setText(String.valueOf(d_x2));
        y2.setText(String.valueOf(d_y2));
        z2.setText(String.valueOf(d_z2));

        x3.setText(String.valueOf(d_x3));
        y3.setText(String.valueOf(d_y3));
        z3.setText(String.valueOf(d_z3));

        x4.setText(String.valueOf(d_x4));
        y4.setText(String.valueOf(d_y4));
        z4.setText(String.valueOf(d_z4));

        x5.setText(String.valueOf(d_x5));
        y5.setText(String.valueOf(d_y5));
        z5.setText(String.valueOf(d_z5));

        x6.setText(String.valueOf(d_x6));
        y6.setText(String.valueOf(d_y6));
        z6.setText(String.valueOf(d_z6));

        x7.setText(String.valueOf(d_x7));
        y7.setText(String.valueOf(d_y7));
        z7.setText(String.valueOf(d_z7));

        x8.setText(String.valueOf(d_x8));
        y8.setText(String.valueOf(d_y8));
        z8.setText(String.valueOf(d_z8));

        x9.setText(String.valueOf(d_x9));
        y9.setText(String.valueOf(d_y9));
        z9.setText(String.valueOf(d_z9));

        x10.setText(String.valueOf(d_x10));
        y10.setText(String.valueOf(d_y10));
        z10.setText(String.valueOf(d_z10));

        x11.setText(String.valueOf(d_x11));
        y11.setText(String.valueOf(d_y11));
        z11.setText(String.valueOf(d_z11));

    }
    private void savePoints() {
        pointSaved[0] = String.format("%.3f, %.3f, %.3f", d_x1, d_y1, d_z1);
        pointSaved[1] = String.format("%.3f, %.3f, %.3f", d_x2, d_y2, d_z2);
        pointSaved[2] = String.format("%.3f, %.3f, %.3f", d_x3, d_y3, d_z3);
        pointSaved[3] = String.format("%.3f, %.3f, %.3f", d_x4, d_y4, d_z4);
        pointSaved[4] = String.format("%.3f, %.3f, %.3f", d_x5, d_y5, d_z5);
        pointSaved[5] = String.format("%.3f, %.3f, %.3f", d_x6, d_y6, d_z6);
        pointSaved[6] = String.format("%.3f, %.3f, %.3f", d_x7, d_y7, d_z7);
        pointSaved[7] = String.format("%.3f, %.3f, %.3f", d_x8, d_y8, d_z8);
        pointSaved[8] = String.format("%.3f, %.3f, %.3f", d_x9, d_y9, d_z9);
        pointSaved[9] = String.format("%.3f, %.3f, %.3f", d_x10, d_y10, d_z10);
        pointSaved[10] = String.format("%.3f, %.3f, %.3f", d_x11, d_y11, d_z11);
    }
    private void init(){
        String [] str=new MyRW_IntMem().MyRead("pointssaved",ExcavatorMeasureXYZ.this).split(",");
        x1.setText(str[0]);
        y1.setText(str[1]);
        z1.setText(str[2]);
        x2.setText(str[3]);
        y2.setText(str[4]);
        z2.setText(str[5]);
        x3.setText(str[6]);
        y3.setText(str[7]);
        z3.setText(str[8]);
        x4.setText(str[9]);
        y4.setText(str[10]);
        z4.setText(str[11]);
        x5.setText(str[12]);
        y5.setText(str[13]);
        z5.setText(str[14]);
        x6.setText(str[15]);
        y6.setText(str[16]);
        z6.setText(str[17]);
        x7.setText(str[18]);
        y7.setText(str[19]);
        z7.setText(str[20]);
        x8.setText(str[21]);
        y8.setText(str[22]);
        z8.setText(str[23]);
        x9.setText(str[24]);
        y9.setText(str[25]);
        z9.setText(str[26]);
        x10.setText(str[27]);
        y10.setText(str[28]);
        z10.setText(str[29]);
        x11.setText(str[30]);
        y11.setText(str[31]);
        z11.setText(str[32]);


    }








    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onBackPressed() {

    }
    private double parseDoubleOrZero(String input) {
        try {
            if (input != null && !input.isEmpty()) {
                return Double.parseDouble(input);
            } else {
                return 0.0;
            }
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}