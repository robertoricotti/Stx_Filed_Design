package activity_portrait;

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

    ImageButton plane, ab, delaunay,path;
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
        path=findViewById(R.id.imagepathfollower);


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
        path.setOnClickListener(view -> {
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
