package activity_portrait;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stx_field_design.R;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;

import dialogs.CustomToast;
import project.PickProjectAdapter;
import project.PickProjectAdapterUSB;

public class UsbActivity extends AppCompatActivity {

    private boolean mRunning = true;
    private Handler handler;
    private static final int REQUEST_CODE_OPEN_DOCUMENT_TREE = 42;
    static String usbPath;

    PickProjectAdapterUSB adapterPJ, adapterMC;
    RecyclerView recyclerViewProj, recyclerViewMC;
    public static boolean enableBtn3, enableBtn4;
    ImageView del, toleft, toright, refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findview();
        onClick();
        updateUI();
    }

    private void findview() {
        recyclerViewMC = findViewById(R.id.recycler_view_in);
        recyclerViewProj = findViewById(R.id.recycler_view_proj);
        del = findViewById(R.id.new_delete);
        toleft = findViewById(R.id.new_copy_from_usb);
        toright = findViewById(R.id.new_copy_to_usb);
        refresh = findViewById(R.id.new_Update);
        loadFilesToRecyclerView();
    }

    private void onClick() {
        del.setOnLongClickListener(view -> {
            confirmDelete(true);
            return true;
        });
        refresh.setOnClickListener(view -> exBtn2());
        toleft.setOnClickListener(view -> exBtn3());
        toright.setOnClickListener(view -> exBtn4());
    }

    private void updateUI() {
        handler = new Handler();
        new Thread(() -> {
            while (mRunning) {
                handler.post(() -> {
                    if (usbPath != null) {
                        enableBtn3();
                        enableBtn4();
                        toleft.setEnabled(enableBtn3);
                        toleft.setAlpha(enableBtn3 ? 1f : 0.3f);
                        toright.setEnabled(enableBtn4);
                        toright.setAlpha(enableBtn4 ? 1f : 0.3f);
                    } else {
                        toleft.setEnabled(false);
                        toleft.setAlpha(0.3f);
                        toright.setEnabled(false);
                        toright.setAlpha(0.3f);
                    }
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void enableBtn3() {
        enableBtn3 = adapterMC != null && adapterMC.getSelectedItem() > -1;
    }

    public void enableBtn4() {
        enableBtn4 = adapterPJ != null && adapterPJ.getSelectedItem() > -1;
    }

    public void exBtn1() {
        if (usbPath != null) {
            new CustomToast(this, "DO NOT REMOVE USB UNSAFETY. USE EJECT BUTTON IN ANDROID MENU").show();
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void exBtn2() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                usbPath = getUsbFolderPath();
            } else {
                usbPath = getStoragePath(UsbActivity.this, true).toString();
                usbPath = "/" + usbPath;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        File inFolder = new File(usbPath, "STX_MC");
        if (!inFolder.exists() && inFolder.mkdir()) {
            new CustomToast(UsbActivity.this, "USB:\nCreated STX_MC Folder").show();
        }

        try {
            readFromUSB_IN(usbPath);
        } catch (Exception e) {
            new CustomToast(UsbActivity.this, "USB:\nNot Found").show();
        }

        try {
            loadFilesToRecyclerView();
        } catch (Exception e) {
            new CustomToast(UsbActivity.this, "No Projects Available").show();
        }
    }

    public void exBtn3() {
        importFilesFromUsb();
    }

    public void exBtn4() {
        exportFilesToUsb();
    }

    public void confirmDelete(boolean del) {
        if (del) {
            if (adapterMC != null && adapterMC.getSelectedItem() > -1) {
                File source = new File(getStoragePath(this, true), "STX_MC" + "/" + adapterMC.getSelectedFilePath());
                if (deleteFile(source.getPath())) {
                    adapterMC.removeItem(adapterMC.getSelectedItem());
                }
            } else if (adapterPJ != null && adapterPJ.getSelectedItem() > -1) {
                String appCsvFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Stx Field/Projects/";
                File appCsvDir = new File(appCsvFolder);
                if (deleteFile(appCsvDir + "/" + adapterPJ.getSelectedFilePath())) {
                    adapterPJ.removeItem(adapterPJ.getSelectedItem());
                }
            } else {
                new CustomToast(UsbActivity.this, "Select a File to Delete").show();
            }
        } else {
            adapterPJ.removeItem(-1);
            adapterMC.removeItem(-1);
        }
    }

    private void readFromUSB_IN(String usbFolderPath) {
        File usbFolder = new File(usbFolderPath);
        if (usbFolder.exists() && usbFolder.isDirectory()) {
            File inFolder = new File(usbFolder, "STX_MC");
            if (inFolder.exists() && inFolder.isDirectory()) {
                File[] files = inFolder.listFiles();
                if (files != null) {
                    adapterMC = new PickProjectAdapterUSB(getFileNames(files));
                    recyclerViewMC.setAdapter(adapterMC);
                    recyclerViewMC.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
            } else {
                new CustomToast(UsbActivity.this, "Folder 'STX_MC' not found on USB stick").show();
            }
        } else {
            new CustomToast(UsbActivity.this, "USB stick not found").show();
        }
    }

    private void loadFilesToRecyclerView() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Stx Field/Projects/");
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                adapterPJ = new PickProjectAdapterUSB(getFileNames(files));
                recyclerViewProj.setAdapter(adapterPJ);
                recyclerViewProj.setLayoutManager(new LinearLayoutManager(this));
            } else {
                new CustomToast(UsbActivity.this, "No files found in the specified directory").show();
            }
        } else {
            new CustomToast(UsbActivity.this, "Specified directory does not exist").show();
        }
    }

    private ArrayList<String> getFileNames(File[] files) {
        ArrayList<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getAbsolutePath());
        }
        return fileNames;
    }

    private void importFilesFromUsb() {
        File usbInFolder = new File(getStoragePath(this, true), "STX_MC");
        if (usbInFolder.exists() && usbInFolder.isDirectory()) {
            File[] filesToImport = usbInFolder.listFiles();
            if (filesToImport != null && filesToImport.length > 0) {
                String appCsvFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Stx Field/Projects/";
                if (adapterMC != null && adapterMC.getSelectedItem() > -1) {
                    File source = new File(getStoragePath(this, true), "STX_MC" + "/" + adapterMC.getSelectedFilePath());
                    try {
                        FileUtils.copyFile(source, new File(appCsvFolder, adapterMC.getSelectedFilePath()));
                        adapterMC.notifyDataSetChanged();
                    } catch (IOException e) {
                        new CustomToast(UsbActivity.this, "Import failed").show();
                    }
                } else {
                    new CustomToast(UsbActivity.this, "Select a File to IMPORT").show();
                }
                loadFilesToRecyclerView();
                exBtn2();
            } else {
                new CustomToast(UsbActivity.this, "No files to import from USB").show();
            }
        } else {
            new CustomToast(UsbActivity.this, "USB stick not found").show();
        }
    }

    @SuppressLint("SetWorldWritable")
    private void exportFilesToUsb() {
        String usbFolderPath = getStoragePath(this, true);
        if (usbFolderPath != null) {
            File outFolder = new File(usbFolderPath, "STX_MC");
            if (!outFolder.exists() && !outFolder.mkdirs()) {
                new CustomToast(UsbActivity.this, "USB stick\nNot Found").show();
                return;
            }
            String appCsvFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Stx Field/Projects/";
            File internalCsvFolder = new File(appCsvFolder);
            if (internalCsvFolder.exists() && internalCsvFolder.isDirectory()) {
                File[] filesToExport = internalCsvFolder.listFiles();
                if (filesToExport != null && filesToExport.length > 0) {
                    if (adapterPJ != null && adapterPJ.getSelectedItem() > -1) {
                        File source = new File(appCsvFolder, adapterPJ.getSelectedFilePath());
                        File destFile = new File(outFolder, adapterPJ.getSelectedFilePath());
                        try {
                            FileUtils.copyFile(source, destFile);
                            adapterPJ.notifyDataSetChanged();
                        } catch (IOException e) {
                            new CustomToast(UsbActivity.this, "Export failed").show();
                        }
                        readFromUSB_IN(usbFolderPath);
                        exBtn2();
                    } else {
                        new CustomToast(UsbActivity.this, "Select a File to export").show();
                    }
                } else {
                    new CustomToast(UsbActivity.this, "No files found in the internal folder").show();
                }
            } else {
                new CustomToast(UsbActivity.this, "Internal folder does not exist").show();
            }
        } else {
            new CustomToast(UsbActivity.this, "USB stick not found").show();
        }
    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.delete();
    }


    @SuppressLint("PrivateApi")
    private String getStoragePath(Context context, boolean isUsb) {

        String path = "";

        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        Class<?> volumeInfoClazz;

        Class<?> diskInfoClaszz;

        try {

            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");

            diskInfoClaszz = Class.forName("android.os.storage.DiskInfo");

            Method StorageManager_getVolumes = Class.forName("android.os.storage.StorageManager").getMethod("getVolumes");

            Method VolumeInfo_GetDisk = volumeInfoClazz.getMethod("getDisk");

            Method VolumeInfo_GetPath = volumeInfoClazz.getMethod("getPath");

            Method DiskInfo_IsUsb = diskInfoClaszz.getMethod("isUsb");

            Method DiskInfo_IsSd = diskInfoClaszz.getMethod("isSd");

            List<Object> List_VolumeInfo = (List<Object>) StorageManager_getVolumes.invoke(mStorageManager);

            assert List_VolumeInfo != null;

            for (int i = 0; i < List_VolumeInfo.size(); i++) {

                Object volumeInfo = List_VolumeInfo.get(i);

                Object diskInfo = VolumeInfo_GetDisk.invoke(volumeInfo);

                if (diskInfo == null) continue;

                boolean sd = (boolean) DiskInfo_IsSd.invoke(diskInfo);

                boolean usb = (boolean) DiskInfo_IsUsb.invoke(diskInfo);

                File file = (File) VolumeInfo_GetPath.invoke(volumeInfo);

                if (isUsb == usb) {//usb

                    if (file != null) {

                        path = file.getAbsolutePath();
                    }

                } else if (!isUsb == sd) {//sd

                    if (file != null) {

                        path = file.getAbsolutePath();
                    }

                }

            }

        } catch (Exception e) {

            new CustomToast(UsbActivity.this, e.toString());

        }
        return path;
    }

    @SuppressLint("NewApi")
    private String getUsbFolderPath() {
        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);

        if (storageManager != null) {
            try {
                // Utilizza il nuovo metodo getDirectory() disponibile da Android 11 in poi
                List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
                for (StorageVolume storageVolume : storageVolumes) {
                    if (Environment.MEDIA_MOUNTED.equals(storageVolume.getState()) && storageVolume.isRemovable()) {
                        File directory = storageVolume.getDirectory();
                        if (directory != null) {

                            return directory.getAbsolutePath();
                        } else {

                            return null;
                        }
                    }
                }

            } catch (NoSuchMethodError e) {

                e.printStackTrace();
                new CustomToast(UsbActivity.this, "No USB Found").show();
            }
        }


        return null;
    }

}






/*
public class UsbActivity extends AppCompatActivity {

    private boolean mRunning = true;
    private Handler handler;
    private static final int REQUEST_CODE_OPEN_DOCUMENT_TREE = 42;
    static String usbPath;

    PickProjectAdapter adapterPJ, adapterMC;
    RecyclerView recyclerViewProj, recyclerViewMC;
    public static boolean enableBtn3, enableBtn4;
    ImageView del, toleft, toright, refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findview();
        onClick();
        updateUI();


    }


    private void findview() {
        recyclerViewMC = findViewById(R.id.recycler_view_in);
        recyclerViewProj = findViewById(R.id.recycler_view_proj);
        del = findViewById(R.id.new_delete);
        toleft = findViewById(R.id.new_copy_from_usb);
        toright = findViewById(R.id.new_copy_to_usb);
        refresh = findViewById(R.id.new_Update);
        loadFilesToRecyclerView();


    }

    private void onClick() {
        del.setOnLongClickListener(view -> {
            confirmDelete(true);
            return true;
        });
        refresh.setOnClickListener(view -> {
            exBtn2();
        });
        toleft.setOnClickListener(view -> {
            exBtn3();
        });
        toright.setOnClickListener(view -> {
            exBtn4();
        });
    }

    private void updateUI() {
        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRunning) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if(usbPath!=null) {
                                enableBtn3();
                                enableBtn4();
                                if (enableBtn3) {
                                    toleft.setEnabled(true);
                                    toleft.setAlpha(1f);
                                } else {
                                    toleft.setEnabled(false);
                                    toleft.setAlpha(0.3f);
                                }
                                if (enableBtn4) {
                                    toright.setEnabled(true);
                                    toright.setAlpha(1f);
                                } else {
                                    toright.setEnabled(false);
                                    toright.setAlpha(0.3f);
                                }
                            }else {
                                toleft.setEnabled(false);
                                toleft.setAlpha(0.3f);
                                toright.setEnabled(false);
                                toright.setAlpha(0.3f);
                            }


                        }
                    });
                    // sleep per intervallo update UI
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    public void enableBtn3() {
        if (adapterMC != null) {
            enableBtn3 = adapterMC.getSelectedItem() > -1;
        } else {
            enableBtn3 = false;
        }
    }

    public void enableBtn4() {
        if (adapterPJ != null) {
            enableBtn4 = adapterPJ.getSelectedItem() > -1;
        } else {
            enableBtn4 = false;
        }
    }


    public void exBtn1() {
        if (usbPath != null) {
          new CustomToast(this,"DO NOT REMOVE USB UNSAFETY. USE EJECT BUTTON IN ANDROID MENU").show();
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }

    public void exBtn2() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                usbPath = getUsbFolderPath();
            } else {
                usbPath = getStoragePath(UsbActivity.this, true).toString();
                usbPath = "/" + usbPath;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }

        // Verifica e crea la cartella "IN" se necessario
        File inFolder = new File(usbPath, "STX_MC");
        if (!inFolder.exists()) {
            if (inFolder.mkdir()) {
                new CustomToast(UsbActivity.this, "USB:\nCreated STX_MC Folder").show();
            } else {
                return;
            }
        }


        try {
            readFromUSB_IN(usbPath);
        } catch (Exception e) {
            new CustomToast(UsbActivity.this, "USB:\nNot Found").show();
        }


        try {
            loadFilesToRecyclerView();
        } catch (Exception e) {
            new CustomToast(UsbActivity.this, "No Projects Available").show();
        }
    }


    public void exBtn3() {
        importFilesFromUsb();
    }

    public void exBtn4() {
        exportFilesToUsb();
    }


    public void confirmDelete(boolean del) {
        if (del) {


            if (adapterMC != null && adapterMC.getSelectedItem() > -1) {
                File source = new File(getStoragePath(this, true), "STX_MC" + "/" + adapterMC.getSelectedFilePath());
                try {
                    deleteFile(source.getPath());


                } catch (Exception e) {
                   // new CustomToast(UsbActivity.this, "IMPOSSIBLE TO DELETE").show();
                }
                adapterMC.removeItem(adapterMC.getSelectedItem());
                adapterMC.notifyDataSetChanged();

            } else {
                new CustomToast(UsbActivity.this, "Select a File to Delete").show();
            }


            if (adapterPJ != null && adapterPJ.getSelectedItem() > -1) {
                String appCsvFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Stx Field/Projects/";
                File appCsvDir = new File(appCsvFolder);

                try {
                    deleteFile(appCsvDir + "/" + adapterPJ.getSelectedFilePath());


                } catch (Exception e) {
                   // new CustomToast(UsbActivity.this, "IMPOSSIBLE TO DELETE").show();
                }
                adapterPJ.removeItem(adapterPJ.getSelectedItem());
                adapterPJ.notifyDataSetChanged();

            } else {
                new CustomToast(UsbActivity.this, "Select a File to Delete").show();
            }
            del = false;
        } else {
            adapterPJ.removeItem(-1);
            adapterMC.removeItem(-1);

        }
    }


    private void readFromUSB_IN(String usbFolderPath) {
        // Verifica se la cartella USB esiste
        File usbFolder = new File(usbFolderPath);
        if (usbFolder.exists() && usbFolder.isDirectory()) {
            // Percorso della cartella "IN" sulla USB stick
            File inFolder = new File(usbFolder, "STX_MC");
            // Verifica se la cartella "IN" esiste
            if (inFolder.exists() && inFolder.isDirectory()) {
                // Ottieni la lista di file nella cartella "STX_MC"
                File[] files = inFolder.listFiles();
                if (files != null) {
                    adapterMC = new PickProjectAdapter(getFileNames(files));
                    recyclerViewMC.setAdapter(adapterMC);
                    recyclerViewMC.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
            } else {
                String msg = ("Folder 'STX_MC' not found on USB stick");
                //new CustomToast(UsbActivity.this, msg).show();
            }
        } else {
            String msg = ("USB stick not found");
            new CustomToast(UsbActivity.this, msg).show();
        }
    }


    private void loadFilesToRecyclerView() {
        File dir1 = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getPath(), "Stx Field");
        String path = dir1.getAbsolutePath() + "/Projects/";
        // Verifica se la cartella specificata esiste
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            // Ottieni la lista di file nella cartella
            File[] files = dir.listFiles();
            if (files != null) {
                // Creare un nuovo adattatore con la lista di file
                adapterPJ = new PickProjectAdapter(getFileNames(files));
                recyclerViewProj.setAdapter(adapterPJ);
                recyclerViewProj.setLayoutManager(new LinearLayoutManager(this));

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


    private void importFilesFromUsb() {

        // Ottieni la cartella "IN" sulla USB
        File usbInFolder = new File(getStoragePath(this, true), "STX_MC");

        // Verifica se la cartella "IN" esiste
        if (usbInFolder.exists() && usbInFolder.isDirectory()) {
            // Ottieni la lista di file nella cartella "IN"

            File[] filesToImport = usbInFolder.listFiles();

            // Verifica se ci sono file da importare
            if (filesToImport != null && filesToImport.length > 0) {
                // Percorso della cartella "CSV" dell'app
                String appCsvFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Stx Field/Projects/";
                if (adapterMC != null) {
                    if (adapterMC.getSelectedItem() > -1) {
                        File source = new File(getStoragePath(this, true), "STX_MC" + "/" + adapterMC.getSelectedFilePath());

                        try {
                            FileUtils.copyFile(source, new File(appCsvFolder, adapterMC.getSelectedFilePath()));

                        } catch (IOException e) {

                        }
                        adapterMC.notifyDataSetChanged();
                    } else {
                        new CustomToast(UsbActivity.this, "Select a File to IMPORT").show();

                    }
                }

                // Aggiorna la RecyclerView dopo l'importazione
                loadFilesToRecyclerView();
                exBtn2();
            } else {
                // Messaggio se non ci sono file da importare
                new CustomToast(UsbActivity.this, "No files to import from USB").show();
            }
        } else {
            // Messaggio se la cartella "IN" sulla USB non esiste
            new CustomToast(UsbActivity.this, "USB stick not found").show();
        }
    }


    @SuppressLint("SetWorldWritable")
    private void exportFilesToUsb() {
        String usbFolderPath = getStoragePath(this, true);
        if (usbFolderPath != null) {
            // Percorso della cartella "OUT" sulla USB stick
            File outFolder = new File(usbFolderPath, "STX_MC");
            // Verifica se la cartella "STX_MC" esiste, altrimenti creala
            if (!outFolder.exists()) {
                if (!outFolder.mkdirs()) {
                    String msg = ("USB stick\nNot Found");
                    new CustomToast(UsbActivity.this, msg).show();
                    return;
                }
            }
            // Percorso della cartella CSV interna dell'app
            String S_internalCsvFoldes = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Stx Field/Projects/";
            File internalCsvFolder = new File(S_internalCsvFoldes);

            // Verifica se la cartella CSV interna dell'app esiste
            if (internalCsvFolder.exists() && internalCsvFolder.isDirectory()) {
                //  lista di file nella cartella CSV interna dell'app
                File[] filesToExport = internalCsvFolder.listFiles();
                if (filesToExport != null && filesToExport.length > 0) {
                    if (adapterPJ != null) {
                        if (adapterPJ.getSelectedItem() > -1) {
                            File source = new File(S_internalCsvFoldes + "/" + adapterPJ.getSelectedFilePath());
                            File destFile = new File(outFolder, adapterPJ.getSelectedFilePath());
                            if (source.getTotalSpace() > 0) {
                                try {
                                    FileInputStream fis = new FileInputStream(source);
                                    InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                                    BufferedReader reader = new BufferedReader(isr);

                                    // Crea un nuovo file di destinazione


                                    // Crea un FileOutputStream per scrivere nel file di destinazione
                                    FileOutputStream fos = new FileOutputStream(destFile);

                                    // Leggi ogni riga dal file di origine e scrivi nel file di destinazione
                                    String line;

                                    while ((line = reader.readLine()) != null) {

                                        // Scrivi la riga nel file di destinazione, seguita da un carattere di nuova linea
                                        fos.write((line + "\n").getBytes(StandardCharsets.UTF_8));
                                        //Toast.makeText(this, line, Toast.LENGTH_SHORT).show();
                                    }
                                    fos.flush();

                                    // Chiudi gli stream
                                    reader.close();

                                    fos.close();


                                } catch (IOException e) {
                                    //new CustomToast(UsbActivity.this, e.toString()).show_long();
                                }

                            }

                        } else {
                            new CustomToast(UsbActivity.this, "Select a File to export").show();

                        }
                    }
                    assert adapterPJ != null;
                    adapterPJ.notifyDataSetChanged();
                    readFromUSB_IN(usbFolderPath);
                    exBtn2();
                } else {
                    String msg = ("No files found in the internal folder");
                    //new CustomToast(UsbActivity.this, msg).show();
                }
            } else {
                String msg = ("Internal  folder does not exist");
               // new CustomToast(UsbActivity.this, msg).show();
            }
        } else {
            String msg = ("USB stick not found");
            new CustomToast(UsbActivity.this, msg).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunning = false;


    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                if (adapterPJ.getSelectedItem() > -1) {
                    new CustomToast(UsbActivity.this, adapterPJ.getSelectedFilePath() + "\n DELETED").show();
                }
                if (adapterMC.getSelectedItem() > -1) {
                    new CustomToast(UsbActivity.this, adapterMC.getSelectedFilePath() + "\n DELETED").show();
                }
            }
        }
        return false;
    }


    @SuppressLint("PrivateApi")
    private String getStoragePath(Context context, boolean isUsb) {

        String path = "";

        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        Class<?> volumeInfoClazz;

        Class<?> diskInfoClaszz;

        try {

            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");

            diskInfoClaszz = Class.forName("android.os.storage.DiskInfo");

            Method StorageManager_getVolumes = Class.forName("android.os.storage.StorageManager").getMethod("getVolumes");

            Method VolumeInfo_GetDisk = volumeInfoClazz.getMethod("getDisk");

            Method VolumeInfo_GetPath = volumeInfoClazz.getMethod("getPath");

            Method DiskInfo_IsUsb = diskInfoClaszz.getMethod("isUsb");

            Method DiskInfo_IsSd = diskInfoClaszz.getMethod("isSd");

            List<Object> List_VolumeInfo = (List<Object>) StorageManager_getVolumes.invoke(mStorageManager);

            assert List_VolumeInfo != null;

            for (int i = 0; i < List_VolumeInfo.size(); i++) {

                Object volumeInfo = List_VolumeInfo.get(i);

                Object diskInfo = VolumeInfo_GetDisk.invoke(volumeInfo);

                if (diskInfo == null) continue;

                boolean sd = (boolean) DiskInfo_IsSd.invoke(diskInfo);

                boolean usb = (boolean) DiskInfo_IsUsb.invoke(diskInfo);

                File file = (File) VolumeInfo_GetPath.invoke(volumeInfo);

                if (isUsb == usb) {//usb

                    if (file != null) {

                        path = file.getAbsolutePath();
                    }

                } else if (!isUsb == sd) {//sd

                    if (file != null) {

                        path = file.getAbsolutePath();
                    }

                }

            }

        } catch (Exception e) {

            new CustomToast(UsbActivity.this, e.toString());

        }
        return path;
    }

    @SuppressLint("NewApi")
    private String getUsbFolderPath() {
        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);

        if (storageManager != null) {
            try {
                // Utilizza il nuovo metodo getDirectory() disponibile da Android 11 in poi
                List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
                for (StorageVolume storageVolume : storageVolumes) {
                    if (Environment.MEDIA_MOUNTED.equals(storageVolume.getState()) && storageVolume.isRemovable()) {
                        File directory = storageVolume.getDirectory();
                        if (directory != null) {

                            return directory.getAbsolutePath();
                        } else {

                            return null;
                        }
                    }
                }

            } catch (NoSuchMethodError e) {

                e.printStackTrace();
                new CustomToast(UsbActivity.this, "No USB Found").show();
            }
        }


        return null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_OPEN_DOCUMENT_TREE && resultCode == Activity.RESULT_OK) {
            Uri treeUri = data.getData();
            DocumentFile documentFile = DocumentFile.fromTreeUri(this, treeUri);

            if (documentFile != null && documentFile.isDirectory()) {
                DocumentFile[] files = documentFile.listFiles();

                if (files != null) {
                    for (DocumentFile file : files) {
                        if (file.isFile()) {
                            Log.e("MyUSB", "File: " + file.getName());
                        } else if (file.isDirectory()) {
                            Log.e("MyUSB", "Directory: " + file.getName());
                        }
                    }
                } else {
                    Log.e("MyUSB", "Nessun file nella cartella USB.");
                }
            } else {
                Log.e("MyUSB", "La cartella USB non esiste o non è una directory.");
            }
        }
    }


}


 */