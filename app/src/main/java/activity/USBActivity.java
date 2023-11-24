package activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stx_field_design.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class USBActivity extends AppCompatActivity {

    private TextView outputTextView;
    Button readButton ,writeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb);

        outputTextView = findViewById(R.id.outputTextView);

        readButton = findViewById(R.id.readButton);
        writeButton = findViewById(R.id.writeButton);

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFromUSB();
            }
        });

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeToUSB();
            }
        });
    }

    private void readFromUSB() {
        // Percorso della cartella "IN" sulla USB stick
        File inFolder = new File("/mnt/media_rw/USB_DISK0/IN");

        // Verifica se la cartella "IN" esiste
        if (inFolder.exists() && inFolder.isDirectory()) {
            // Ottieni la lista di file nella cartella "IN"
            File[] files = inFolder.listFiles();
            if (files != null) {
                // Stampa la lista dei file
                StringBuilder fileList = new StringBuilder("Files in 'IN' folder:\n");
                for (File file : files) {
                    fileList.append(file.getName()).append("\n");
                }
                outputTextView.setText(fileList.toString());
            }
        } else {
            outputTextView.setText("Folder 'IN' not found on USB stick");
        }
    }

    private void writeToUSB() {
        // Percorso della cartella "OUT" sulla USB stick
        File outFolder = new File("/mnt/media_rw/USB_DISK0/OUT");

        // Crea la cartella "OUT" se non esiste
        if (!outFolder.exists()) {
            outFolder.mkdirs();
        }

        // Crea un file nella cartella "OUT"
        File outFile = new File(outFolder, "output_file.txt");

        // Scrivi qualcosa nel file
        try {
            FileUtils.writeStringToFile(outFile, "Contenuto del file di output", "UTF-8");
            outputTextView.setText("File 'output_file.txt' scritto con successo nella cartella 'OUT'");
        } catch (IOException e) {
            e.printStackTrace();
            outputTextView.setText("Errore durante la scrittura del file nella cartella 'OUT'");
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}

