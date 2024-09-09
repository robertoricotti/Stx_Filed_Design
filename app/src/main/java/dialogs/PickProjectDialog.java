package dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stx_field_design.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import activity_portrait.MainActivity;
import activity_portrait.MenuProject;
import project.DataProjectSingleton;
import project.PickProjectAdapter;
import utils.FullscreenActivity;

public class PickProjectDialog {

    ImageView ritorna;
    public Dialog dialog;
    RecyclerView recyclerView;
    Button select, exit;
    PickProjectAdapter pickProjectAdapter;
    ArrayList<String> arrayFiles;
    File currentDirectory = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getPath(), "Stx Field/Projects");  // Aggiunta per gestire la navigazione nelle cartelle

    public PickProjectDialog(Activity activity) {
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.pick_project);
        ritorna = dialog.findViewById(R.id.ritorna);
        select = dialog.findViewById(R.id.select);
        exit = dialog.findViewById(R.id.exit);
        recyclerView = dialog.findViewById(R.id.recycler);

        arrayFiles = new ArrayList<>();

        loadDirectory(currentDirectory);  // Carica il contenuto della directory iniziale

        pickProjectAdapter = new PickProjectAdapter(arrayFiles);
        recyclerView.setAdapter(pickProjectAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setItemViewCacheSize(pickProjectAdapter.getItemCount());
        onClick(activity);
        setupDialog();
    }

    private void loadDirectory(File directory) {
        // Carica i file e le cartelle dalla directory specificata
        arrayFiles.clear();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                arrayFiles.add(file.getAbsolutePath());  // Aggiungi il percorso completo
            }
        }
        arrayFiles.sort(Comparator.naturalOrder());
        currentDirectory = directory;  // Aggiorna la directory corrente
        if (pickProjectAdapter != null) {
            pickProjectAdapter.notifyDataSetChanged();  // Aggiorna l'adapter se già inizializzato
        }
    }

    public void show() {
        dialog.show();
        dialog.setCancelable(true);
        FullscreenActivity.setFullScreen(dialog);
    }

    private void setupDialog() {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        DisplayMetrics metrics = dialog.getContext().getResources().getDisplayMetrics();
        int parentHeight = (int) (metrics.heightPixels * 0.8);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, parentHeight);
    }

    private void onClick(Activity activity) {
        ritorna.setOnClickListener(view -> {

            loadDirectory(new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getPath(), "Stx Field/Projects"));

        });
        select.setOnClickListener((View v) -> {
            int selectedItemIndex = pickProjectAdapter.getSelectedItem();
            if (selectedItemIndex == -1) {
                Toast.makeText(activity, "SELECT FILE OR FOLDER", Toast.LENGTH_SHORT).show();
            } else {
                File selectedFile = new File(arrayFiles.get(selectedItemIndex));
                if (selectedFile.isDirectory()) {
                    // Se è una cartella, apri il contenuto della cartella
                    loadDirectory(selectedFile);
                } else if (selectedFile.isFile() && getFileExtension(selectedFile.getName()).equals("pstx")) {
                    // Se è un file .pstx, mantieni il comportamento attuale
                    DataProjectSingleton dataProject = DataProjectSingleton.getInstance();
                    dataProject.readProject(selectedFile.getAbsolutePath());

                    if ((activity instanceof MenuProject)) {
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        activity.finish();
                    }

                    dialog.dismiss();
                } else {
                    Toast.makeText(activity, "Invalid File Selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        exit.setOnClickListener((View v) -> {
            dialog.dismiss();
        });
    }

    // Funzione per ottenere l'estensione del file
    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
}
