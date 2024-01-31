package activity_portrait;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stx_field_design.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dialogs.Confirm_Dialog;
import dialogs.CustomToast;
import project.PickProjectAdapter;


public class UsbActivity extends AppCompatActivity {
    private boolean mRunning = true;
    private Handler handler;
    private static final int REQUEST_CODE_OPEN_DOCUMENT_TREE = 42;
    static String usbPath;
    TextView txt1, txt2;
    PickProjectAdapter adapter, adapterIN, adapterOUT;
    RecyclerView recyclerViewIN, recyclerViewOUT, recyclerViewProj;
    public static boolean enableBtn3,enableBtn4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findview();
        updateUI();


    }

    private void findview() {
        recyclerViewIN = findViewById(R.id.recyclerViewIn);
        recyclerViewOUT = findViewById(R.id.recyclerViewOut);
        recyclerViewProj = findViewById(R.id.recyclerViewProj);
        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        loadFilesToRecyclerView();

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
                            enableBtn3();
                            enableBtn4();
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
        if(adapterIN!=null){
        enableBtn3= adapterIN.getSelectedItem() > -1;}else {
            enableBtn3=false;
        }
    }

    public void enableBtn4() {
        if(adapter!=null){
       enableBtn4=adapter.getSelectedItem()>-1;}
        else {
            enableBtn4=false;
        }
    }



    public void exBtn1() {
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
            new CustomToast(UsbActivity.this, e.toString()).show();
        }

        // Verifica e crea la cartella "IN" se necessario
        File inFolder = new File(usbPath, "IN");
        if (!inFolder.exists()) {
            if (inFolder.mkdir()) {
                new CustomToast(UsbActivity.this, "USB:\nCreated IN Folder").show();
            } else {
                new CustomToast(UsbActivity.this, "USB:\nFailed to create IN Folder").show();
                return;
            }
        }

        // Verifica e crea la cartella "OUT" se necessario
        File outFolder = new File(usbPath, "OUT");
        if (!outFolder.exists()) {
            if (outFolder.mkdir()) {
                new CustomToast(UsbActivity.this, "USB:\nCreated OUT Folder").show();
            } else {
                new CustomToast(UsbActivity.this, "USB:\nFailed to create OUT Folder").show();
                return;
            }
        }

        try {
            readFromUSB_IN(usbPath);
        } catch (Exception e) {
            new CustomToast(UsbActivity.this, "USB:\nIN Folder Not Found").show();
        }

        try {
            readFromUSB_OUT(usbPath);
        } catch (Exception e) {
            new CustomToast(UsbActivity.this, "USB:\nOUT Folder Not Found").show();
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
        if (Build.VERSION.SDK_INT <= 29) {
            //new CustomToast(this, "Can't COPY TO USB Stick\nOn This Device").show();
            exportFilesToUsb();
        } else {
            exportFilesToUsb();
        }
        //to do EXPORT to OUT
    }

    public void exBtn5() {
        new Confirm_Dialog(UsbActivity.this, 255).show();
    }


    public void confirmDelete(boolean del) {
        if (del) {
            if (adapterOUT != null && adapterOUT.getSelectedItem() > -1) {
                File source = new File(getStoragePath(this, true), "OUT" + "/" + adapterOUT.getSelectedFilePath());
                try {
                    deleteFile(source.getPath());


                } catch (Exception e) {
                    new CustomToast(UsbActivity.this, "IMPOSSIBLE TO DELETE").show();
                }
                adapterOUT.removeItem(adapterOUT.getSelectedItem());
                adapterOUT.notifyDataSetChanged();

            } else {
                new CustomToast(UsbActivity.this, "Select a File to Delete").show();
            }


            if (adapterIN != null && adapterIN.getSelectedItem() > -1) {
                File source = new File(getStoragePath(this, true), "IN" + "/" + adapterIN.getSelectedFilePath());
                try {
                    deleteFile(source.getPath());


                } catch (Exception e) {
                    new CustomToast(UsbActivity.this, "IMPOSSIBLE TO DELETE").show();
                }
                adapterIN.removeItem(adapterIN.getSelectedItem());
                adapterIN.notifyDataSetChanged();

            } else {
                new CustomToast(UsbActivity.this, "Select a File to Delete").show();
            }


            if (adapter != null && adapter.getSelectedItem() > -1) {
                String appCsvFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Stx Field/Projects/";
                File appCsvDir = new File(appCsvFolder);

                try {
                    deleteFile(appCsvDir + "/" + adapter.getSelectedFilePath());


                } catch (Exception e) {
                    new CustomToast(UsbActivity.this, "IMPOSSIBLE TO DELETE").show();
                }
                adapter.removeItem(adapter.getSelectedItem());
                adapter.notifyDataSetChanged();

            } else {
                new CustomToast(UsbActivity.this, "Select a File to Delete").show();
            }
            del = false;
        } else {
            adapter.removeItem(-1);
            adapterIN.removeItem(-1);
            adapterOUT.removeItem(-1);
        }
    }


    private void readFromUSB_IN(String usbFolderPath) {
        // Verifica se la cartella USB esiste
        File usbFolder = new File(usbFolderPath);
        if (usbFolder.exists() && usbFolder.isDirectory()) {
            // Percorso della cartella "IN" sulla USB stick
            File inFolder = new File(usbFolder, "IN");
            // Verifica se la cartella "IN" esiste
            if (inFolder.exists() && inFolder.isDirectory()) {
                txt1.setText("USB: " + inFolder.getAbsolutePath());
                // Ottieni la lista di file nella cartella "IN"
                File[] files = inFolder.listFiles();
                if (files != null) {

                    adapterIN = new PickProjectAdapter(getFileNames(files));
                    recyclerViewIN.setAdapter(adapterIN);
                    recyclerViewIN.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

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
                txt2.setText("USB: " + inFolder.getAbsolutePath());
                // Ottieni la lista di file nella cartella "OUT"
                File[] files = inFolder.listFiles();
                if (files != null) {


                    adapterOUT = new PickProjectAdapter(getFileNames(files));
                    recyclerViewOUT.setAdapter(adapterOUT);
                    recyclerViewOUT.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


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
        String path = dir1.getAbsolutePath() + "/Projects/";
        // Verifica se la cartella specificata esiste
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            // Ottieni la lista di file nella cartella
            File[] files = dir.listFiles();
            if (files != null) {
                // Creare un nuovo adattatore con la lista di file
                adapter = new PickProjectAdapter(getFileNames(files));
                recyclerViewProj.setAdapter(adapter);
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
        File usbInFolder = new File(getStoragePath(this, true), "IN");

        // Verifica se la cartella "IN" esiste
        if (usbInFolder.exists() && usbInFolder.isDirectory()) {
            // Ottieni la lista di file nella cartella "IN"

            File[] filesToImport = usbInFolder.listFiles();

            // Verifica se ci sono file da importare
            if (filesToImport != null && filesToImport.length > 0) {
                // Percorso della cartella "CSV" dell'app
                String appCsvFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Stx Field/Projects/";
                if (adapterIN != null) {
                    if (adapterIN.getSelectedItem() > -1) {
                        File source = new File(getStoragePath(this, true), "IN" + "/" + adapterIN.getSelectedFilePath());

                        try {
                            FileUtils.copyFile(source, new File(appCsvFolder, adapterIN.getSelectedFilePath()));

                        } catch (IOException e) {

                        }
                        adapter.notifyDataSetChanged();
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
            new CustomToast(UsbActivity.this, "Folder 'IN' not found on USB stick").show();
        }
    }


    private void exportFilesToUsb() {
        // Verifica se la cartella USB è disponibile
        String usbFolderPath = getStoragePath(this, true);
        if (usbFolderPath != null) {
            // Percorso della cartella "OUT" sulla USB stick
            File outFolder = new File(usbFolderPath, "OUT");

            // Verifica se la cartella "OUT" esiste, altrimenti creala
            if (!outFolder.exists()) {
                if (!outFolder.mkdirs()) {
                    String msg = ("Failed to create 'OUT' folder on USB stick");
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
                    if (adapter != null) {
                        if (adapter.getSelectedItem() > -1) {
                            File source = new File(S_internalCsvFoldes + "/" + adapter.getSelectedFilePath());

                            try {
                                FileUtils.copyFile(source, new File(outFolder, adapter.getSelectedFilePath()));

                            } catch (IOException e) {

                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            new CustomToast(UsbActivity.this, "Select a File to export").show();

                        }
                    }

                    readFromUSB_OUT(usbFolderPath);
                    exBtn2();
                } else {
                    String msg = ("No files found in the internal folder");
                    new CustomToast(UsbActivity.this, msg).show();
                }
            } else {
                String msg = ("Internal  folder does not exist");
                new CustomToast(UsbActivity.this, msg).show();
            }
        } else {
            String msg = ("USB stick not found");
            new CustomToast(UsbActivity.this, msg).show();
        }
    }


    private void copyFile(File source, File destination) throws IOException {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            // Utilizza Files.copy per Android 11 e successive
            Files.copy(source.toPath(), destination.toPath());

        } else {
            // Utilizza FileChannel per versioni precedenti ad Android 11
            try (FileChannel sourceChannel = new FileInputStream(source).getChannel();
                 FileChannel destinationChannel = new FileOutputStream(destination).getChannel()) {
                destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
                destinationChannel.force(true);  // Flush dei dati
            } catch (IOException e) {
                new CustomToast(UsbActivity.this, e.toString()).show_long();
                Log.e("CopyFile", "Errore durante la copia del file", e);
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

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
                new CustomToast(UsbActivity.this, adapter.getSelectedFilePath() + "\n DELETED").show();
            } else {
                new CustomToast(UsbActivity.this, "IMPOSSIBLE TO DELETE").show();
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
                            //new CustomToast(UsbActivity.this, directory.getAbsolutePath()).show();
                            return directory.getAbsolutePath();
                        } else {
                            //new CustomToast(UsbActivity.this, "No USB Found").show();
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

    private void printUSBContents(String usbPath) {
        Log.e("MyUSB", "usbPath= " + usbPath);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT_TREE);
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