package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCopyUtil {

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        // Verifica se il file sorgente esiste
        if (!sourceFile.exists()) {
            throw new IOException("Il file sorgente non esiste: " + sourceFile.getAbsolutePath());
        }

        // Crea il file di destinazione se non esiste
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;

        try {
            // Apre lo stream di input dal file sorgente
            inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
            // Apre lo stream di output per il file di destinazione
            outputStream = new BufferedOutputStream(new FileOutputStream(destFile));

            // Legge i byte dal file sorgente e li scrive nel file di destinazione
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Operazione di copia completata con successo
            System.out.println("File copiato con successo da " + sourceFile.getAbsolutePath() + " a " + destFile.getAbsolutePath());
        } finally {
            // Chiude gli stream di input e output
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}