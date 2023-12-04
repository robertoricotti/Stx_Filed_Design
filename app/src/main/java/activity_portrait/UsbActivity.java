package activity_portrait;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stx_field_design.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import dialogs.Confirm_Dialog;
import dialogs.CustomToast;
import project.PickProjectAdapter;


public class UsbActivity extends AppCompatActivity {

    PickProjectAdapter adapter;

    ImageView exit, export, prendi, readusb, delete;

    RecyclerView recyclerViewIN, recyclerViewOUT, recyclerViewProj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findview();
        onclick();


    }

    private void findview() {
        recyclerViewIN = findViewById(R.id.recyclerViewIn);
        recyclerViewOUT = findViewById(R.id.recyclerViewOut);
        recyclerViewProj = findViewById(R.id.recyclerViewProj);
        exit = findViewById(R.id.back);
        export = findViewById(R.id.copyToOUT);
        prendi = findViewById(R.id.loadFromIN);
        readusb = findViewById(R.id.read);
        delete = findViewById(R.id.deletSelection);
        loadFilesToRecyclerView();

    }


    private void onclick() {
        delete.setOnClickListener(view -> {
            new Confirm_Dialog(UsbActivity.this, 255).show();

        });
        exit.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        readusb.setOnClickListener(view -> {
new CustomToast(this,getUSB()).show_long();
            try {
                readFromUSB_IN(getUsbFolderPath());
            } catch (Exception e) {
                new CustomToast(UsbActivity.this, "USB:\nIN Folder Not Found").show();
            }
            try {
                readFromUSB_OUT(getUsbFolderPath());
            } catch (Exception e) {
                new CustomToast(UsbActivity.this, "USB:\nOUT Folder Not Found").show();
            }
            try {
                loadFilesToRecyclerView();
            } catch (Exception e) {
                new CustomToast(UsbActivity.this, "No Projects Available").show();
            }
        });

        prendi.setOnClickListener(view -> {
            //to do IMPORT from IN
            importFilesFromUsb();
        });

        export.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT <= 29) {
                new CustomToast(this, "Can't COPY TO USB Stick\nOn This Device").show();
                exportFilesToUsb();
            } else {
                exportFilesToUsb();
            }
            //to do EXPORT to OUT

        });

    }



    public void confirmDelete(boolean del) {
        if (del) {
            if (adapter.getSelectedItem() > -1) {
                String appCsvFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Stx Field/Projects/";
                File appCsvDir = new File(appCsvFolder);
                Log.d("file selezionato del", appCsvDir + "/" + adapter.getSelectedFilePath());
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
        }
    }


    @SuppressLint("NewApi")
    private String getUsbFolderPath() {
        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);

        if (storageManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
                } else {

                    return getUSB();

                }
            } catch (NoSuchMethodError e) {

                e.printStackTrace();
                new CustomToast(UsbActivity.this, "No USB Found").show();
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

                    adapter = new PickProjectAdapter(getFileNames(files));
                    recyclerViewIN.setAdapter(adapter);
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
                // Ottieni la lista di file nella cartella "OUT"
                File[] files = inFolder.listFiles();
                if (files != null) {


                    adapter = new PickProjectAdapter(getFileNames(files));
                    recyclerViewOUT.setAdapter(adapter);
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
        File usbInFolder = new File(getUsbFolderPath(), "IN");

        // Verifica se la cartella "IN" esiste
        if (usbInFolder.exists() && usbInFolder.isDirectory()) {
            // Ottieni la lista di file nella cartella "IN"

            File[] filesToImport = usbInFolder.listFiles();

            // Verifica se ci sono file da importare
            if (filesToImport != null && filesToImport.length > 0) {
                // Percorso della cartella "CSV" dell'app
                String appCsvFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Stx Field/Projects/";

                // Verifica se la cartella "CSV" dell'app esiste, altrimenti creala
                File appCsvDir = new File(appCsvFolder);
                if (!appCsvDir.exists()) {
                    appCsvDir.mkdirs();
                }

                // Copia i file che non esistono nella cartella "CSV" dell'app
                for (File file : filesToImport) {
                    String fileName = file.getName();
                    File destinationFile = new File(appCsvFolder, fileName);

                    if (file.exists() && file.isFile()) {
                        // Verifica se il file sorgente esiste ed è un file valido
                        if (!destinationFile.exists()) {
                            // Copia il file solo se non esiste già
                            try {
                                FileUtils.copyFile(file, destinationFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                                // Gestisci l'eccezione di copia del file, se necessario
                            }
                        }
                    } else {
                        // Il file sorgente non esiste o non è un file valido
                        Log.e("UsbActivity", "Source file not found or is not a valid file: " + file.getAbsolutePath());
                    }
                }

                // Aggiorna la RecyclerView dopo l'importazione
                loadFilesToRecyclerView();
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
        String usbFolderPath = getUsbFolderPath();
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
                if (filesToExport != null) {
                    // Copia il contenuto dei file nella cartella "OUT" sulla USB
                    for (File file : filesToExport) {
                        File outFile = new File(outFolder, file.getName());
                        try {

                            copyFile(file, outFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                            // errore di copia del file
                            String msg = ("Failed to copy file to USB stick");
                            new CustomToast(UsbActivity.this, msg).show();
                        }
                    }
                    // Aggiorna la RecyclerView della cartella "OUT"
                    readFromUSB_OUT(usbFolderPath);
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
            Files.copy(source.toPath(),destination.toPath());

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

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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


    //metodo funzionante per Android 9,10,11 https://stackoverflow.com/questions/51855429/how-to-get-external-usb-mass-storage-path-in-android
    public static String getUSB() {
        File storageDirectory = new File("/mnt");
        if (!storageDirectory.exists()) {
            Log.e("mUSB", "getUSB: '/storage' does not exist on this device");
            return "";
        }

        File[] files = storageDirectory.listFiles();
        if (files == null) {
            Log.e("mUSB", "getUSB: Null when requesting directories inside '/storage'");
            return "";
        }

        List<String> possibleUSBStorageMounts = new ArrayList<>();
        for (File file : files) {
            String path = file.getPath();
            if (path.contains("emulated") ||
                    path.contains("sdcard") ||
                    path.contains("self")) {
                Log.d("mUSB", "getUSB: Found '" + path + "' - not USB");
            } else {
                possibleUSBStorageMounts.add(path);
            }
        }

        if (possibleUSBStorageMounts.size() == 0) {
            Log.e("mUSB", "getUSB: Did not find any possible USB mounts");
            return "";
        }
        if (possibleUSBStorageMounts.size() > 1) {
            Log.d("mUSB", "getUSB: Found multiple possible USB mount points, choosing the first one");
        }

        return possibleUSBStorageMounts.get(0);

    }







}