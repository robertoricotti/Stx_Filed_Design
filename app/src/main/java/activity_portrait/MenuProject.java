package activity_portrait;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stx_field_design.R;

import dialogs.CustomToast;
import dialogs.PickProjectDialog;

public class MenuProject extends AppCompatActivity {

    ImageButton cross_sect, ab, flat_surf, create_area;
    PickProjectDialog pickProjectDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        init();
        onClick();

    }

    private void findView(){
        cross_sect = findViewById(R.id.plane);
        ab = findViewById(R.id.ab);
        flat_surf = findViewById(R.id.delaunay);
        create_area =findViewById(R.id.imagepathfollower);


    }

    private void init(){
        pickProjectDialog = new PickProjectDialog(this);
    }

    private void onClick(){

        cross_sect.setOnClickListener((View v) -> {
            new CustomToast(this,"Not Implemented").show();
        });

        ab.setOnClickListener((View v) -> {
            startActivity(new Intent(this, ABProject.class));

            finish();
        });

        flat_surf.setOnClickListener((View v) -> {
            startActivity(new Intent(this, Create_1P.class));

            finish();

        });
        create_area.setOnClickListener(view -> {
            new CustomToast(this,"Not Implemented").show();
         /*   startActivity(new Intent(this, Create_Area.class));

            finish();*/

        });





    }



    public void metodoLoadProject(){
        if(!pickProjectDialog.dialog.isShowing())
            pickProjectDialog.show();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {}

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
