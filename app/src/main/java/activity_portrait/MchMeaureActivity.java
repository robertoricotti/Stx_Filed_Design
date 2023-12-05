package activity_portrait;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stx_field_design.R;

import java.util.Arrays;

import dialogs.ConnectDialog;
import gnss.Nmea_In;
import services_and_bluetooth.Bluetooth_GNSS_Service;
import services_and_bluetooth.DataSaved;
import utils.CircumferenceCenterCalculator;
import utils.MyRW_IntMem;

public class MchMeaureActivity extends AppCompatActivity {

    Button btn_calc;
    EditText etX1, etX2, etX3, etY1, etY2, etY3;

    TextView  res_m, res_ft, res_deg;
    private Handler handler;
    private boolean mRunning = true;
    boolean isNumber1 = false, isNumber2 = false, isNumber3 = false, isNumber4 = false, isNumber5 = false, isNumber6 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findView();
        init();
        onClick();

    }

    private void findView() {

        etX1 = findViewById(R.id.et_x1);
        etX2 = findViewById(R.id.et_x2);
        etX3 = findViewById(R.id.et_x3);
        etY1 = findViewById(R.id.et_y1);
        etY2 = findViewById(R.id.et_y2);
        etY3 = findViewById(R.id.et_y3);

        btn_calc = findViewById(R.id.btn_calc);
        res_m = findViewById(R.id.txt_result_m);
        res_ft = findViewById(R.id.txt_result_ft);
        res_deg = findViewById(R.id.txt_result_deg);

    }

    private void onClick() {
        btn_calc.setOnClickListener(view -> {
            isNumber1 = isDecimalNumber(etX1.getText().toString());
            isNumber2 = isDecimalNumber(etX2.getText().toString());
            isNumber3 = isDecimalNumber(etX3.getText().toString());
            isNumber4 = isDecimalNumber(etY1.getText().toString());
            isNumber5 = isDecimalNumber(etY2.getText().toString());
            isNumber6 = isDecimalNumber(etY3.getText().toString());

            if (isNumber1 && isNumber2 && isNumber3 && isNumber4 && isNumber5 && isNumber6) {
                double x1 = Double.parseDouble(etX1.getText().toString());
                double x2 = Double.parseDouble(etX2.getText().toString());
                double x3 = Double.parseDouble(etX3.getText().toString());
                double y1 = Double.parseDouble(etY1.getText().toString());
                double y2 = Double.parseDouble(etY2.getText().toString());
                double y3 = Double.parseDouble(etY3.getText().toString());
                double[] result = CircumferenceCenterCalculator.findCircumferenceCenter(x1, y1, x2, y2, x3, y3);
                res_m.setText(String.format("%.3f", result[2]).replace(",",".")+" m");
                res_deg.setText(String.format("%.2f", result[3]).replace(",",".")+" °");
                res_ft.setText(String.format("%.4f", (result[2] * 3.28083333333)).replace(",",".")+" ft");
                String[] resultArray=new String[]{res_m.getText().toString().replace(" m",""),res_deg.getText().toString().replace(" °",""),res_ft.getText().toString().replace(" ft","")};
                new MyRW_IntMem().MyWrite("boomresult", Arrays.toString(resultArray),MchMeaureActivity.this);

            } else {
                res_m.setText("Input Err");
                res_ft.setText("Input Err");
                res_deg.setText("Input Err");
            }
        });




    }


    public boolean isDecimalNumber(String input) {
        // Definisci il pattern di un numero decimale
        String decimalPattern = "^[-+]?\\d*\\.?\\d+$";

        // Verifica se la stringa corrisponde al pattern
        return input.matches(decimalPattern);
    }
    private void init(){
        String [] a=new MyRW_IntMem().MyRead("boomresult",MchMeaureActivity.this).split(",");
        res_m.setText(a[0].replace("[","")+" m");
        res_deg.setText(a[1]+" °");
        res_ft.setText(a[2].replace("]","")+" ft");
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