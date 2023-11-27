package activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stx_field_design.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dialogs.CustomToast;
import project.LoadProject;
import project.PickProjectAdapter;
import utils.FullscreenActivity;


public class UsbActivity extends Activity {

    ImageView exit, export, inport, readusb;

    RecyclerView recyclerViewIN, recyclerViewOUT,recyclerViewProj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_inout);
        FullscreenActivity.setFullScreen(this);
        findview();
        onclick();


    }

    private void findview() {
        recyclerViewIN = findViewById(R.id.recyclerViewIn);
        recyclerViewOUT = findViewById(R.id.recyclerViewOut);
        recyclerViewProj=findViewById(R.id.recyclerViewProj);
        exit = findViewById(R.id.back);
        export = findViewById(R.id.copyToOUT);
        inport = findViewById(R.id.loadFromIN);
        readusb = findViewById(R.id.read);
    }

    private void onclick() {
        exit.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        readusb.setOnClickListener(view -> {
            try {
                readFromUSB_IN(getUsbFolderPath());
            } catch (Exception e) {
                new CustomToast(UsbActivity.this, "IN Folder Not Found").show();
            }
            try {
                readFromUSB_OUT(getUsbFolderPath());
            } catch (Exception e) {
                new CustomToast(UsbActivity.this, "OUT Folder Not Found").show();
            }
            try {
                loadFilesToRecyclerView();
            } catch (Exception e) {
                new CustomToast(UsbActivity.this, "No Projects Available").show();
            }
        });

        inport.setOnClickListener(view -> {
            //to do IMPORT from IN
        });

        export.setOnClickListener(view -> {
            //to do EXPORT to OUT
        });

    }


    private String getUsbFolderPath() {
        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);

        if (storageManager != null) {
            List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
            for (StorageVolume storageVolume : storageVolumes) {
                // Verifica se il volume è montato e se è rimovibile (USB, SD, ecc.)
                if (Environment.MEDIA_MOUNTED.equals(storageVolume.getState()) && storageVolume.isRemovable()) {
                    // Ottieni il percorso del volume
                    @SuppressLint({"NewApi", "LocalSuppress"}) File storageFile = storageVolume.getDirectory();
                    // Restituisci il percorso della cartella "IN" sulla USB stick
                    return new File(storageFile.getPath()).toString();
                }
            }
        }

        return null;

    }

    private void readFromUSB_IN(String usbFolderPath) {
        // Verifica se la cartella USB esiste
        File usbFolder = new File(usbFolderPath);

        if (usbFolder.exists() && usbFolder.isDirectory()) {
            // Percorso della cartella "IN" sulla USB stick
            File inFolder = new File(usbFolder, "IN");

            // Verifica se la cartella "IN" esiste
            if (inFolder.exists() && inFolder.isDirectory()) {
                // Ottieni la lista di file nella cartella "IN"
                File[] files = inFolder.listFiles();
                if (files != null) {

                    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                    recyclerViewIN.setLayoutManager(layoutManager);
                    PickProjectAdapter adapter = new PickProjectAdapter(getFileNames(files));

                    recyclerViewIN.setAdapter(adapter);
                }
            } else {
                String msg = ("Folder 'IN' not found on USB stick");
                new CustomToast(UsbActivity.this, msg).show();
            }
        } else {
            String msg = ("USB stick not found");
            new CustomToast(UsbActivity.this, msg).show();
        }
    }

    private void readFromUSB_OUT(String usbFolderPath) {
        // Verifica se la cartella USB esiste
        File usbFolder = new File(usbFolderPath);

        if (usbFolder.exists() && usbFolder.isDirectory()) {
            // Percorso della cartella "IN" sulla USB stick
            File inFolder = new File(usbFolder, "OUT");

            // Verifica se la cartella "IN" esiste
            if (inFolder.exists() && inFolder.isDirectory()) {
                // Ottieni la lista di file nella cartella "OUT"
                File[] files = inFolder.listFiles();
                if (files != null) {

                    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                    recyclerViewOUT.setLayoutManager(layoutManager);
                    PickProjectAdapter adapter = new PickProjectAdapter(getFileNames(files));

                    recyclerViewOUT.setAdapter(adapter);
                }
            } else {
                String msg = ("Folder 'OUT' not found on USB stick");
                new CustomToast(UsbActivity.this, msg).show();
            }
        } else {
            String msg = ("USB stick not found");
            new CustomToast(UsbActivity.this, msg).show();
        }
    }

    private void loadFilesToRecyclerView() {
        File dir1 = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getPath(), "Stx Field");
        String path = dir1.getAbsolutePath() + "/Projects/CSV/";
        // Verifica se la cartella specificata esiste
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            // Ottieni la lista di file nella cartella
            File[] files = dir.listFiles();
            if (files != null) {
                // Creare un nuovo adattatore con la lista di file
                PickProjectAdapter adapter = new PickProjectAdapter(getFileNames(files));

                // Ottieni la tua RecyclerView


                // Imposta l'adattatore sulla RecyclerView
                recyclerViewProj.setAdapter(adapter);
            } else {
                String msg = ("No files found in the specified directory");
                new CustomToast(UsbActivity.this, msg).show();
            }
        } else {
            String msg = ("Specified directory does not exist");
            new CustomToast(UsbActivity.this, msg).show();
        }
    }


    // Metodo per ottenere i nomi dei file dalla lista di File
    private ArrayList<String> getFileNames(File[] files) {
        ArrayList<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getName());

        }
        return fileNames;

    }


    @Override
    public void onBackPressed() {

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



}