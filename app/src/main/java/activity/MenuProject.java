package activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.stx_field_design.R;

import dialogs.ConnectDialog;
import dialogs.CustomToast;
import dialogs.PickProjectDialog;
import gnss.My_LocationCalc;
import gnss.Nmea_In;
import services_and_bluetooth.Bluetooth_GNSS_Service;
import services_and_bluetooth.DataSaved;
import utils.FullscreenActivity;

public class MenuProject extends AppCompatActivity {

    ImageButton plane, ab, delaunay;
    PickProjectDialog pickProjectDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        init();
        onClick();

    }

    private void findView(){
        plane = findViewById(R.id.plane);
        ab = findViewById(R.id.ab);
        delaunay = findViewById(R.id.delaunay);


    }

    private void init(){
        pickProjectDialog = new PickProjectDialog(this);
    }

    private void onClick(){

        plane.setOnClickListener((View v) -> {
        new CustomToast(this,"Not Implemented").show();
        });

        ab.setOnClickListener((View v) -> {
            startActivity(new Intent(this, ABProject.class));

            finish();
        });

        delaunay.setOnClickListener((View v) -> {
            new CustomToast(this,"Not Implemented").show();
        });





    }



    public void metodoLoadProject(){
        if(!pickProjectDialog.dialog.isShowing())
            pickProjectDialog.show();
    }

    @Override
    public void onBackPressed() {}

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
