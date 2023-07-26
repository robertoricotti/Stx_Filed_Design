package utils;

import android.app.Activity;
import android.content.Context;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
public class MyRW_IntMem extends Activity {
    public final int READ_BLOCK_SIZE = 150;

    public void MyWrite(String DataName, String Data2Save, Context context){
        try {
            OutputStreamWriter osw=new OutputStreamWriter(context.openFileOutput(DataName, MODE_PRIVATE));
            osw.write(Data2Save);
            osw.flush( );
            osw.close( );
        } catch (IOException |NullPointerException ignored){}
    }

    public String MyRead(String DataName, Context context){
        String DataSaved = null;
        try {
            FileInputStream fIn = context.openFileInput(DataName);
            InputStreamReader isr = new InputStreamReader(fIn);
            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            StringBuilder s = new StringBuilder();
            int charRead;
            while ((charRead = isr.read(inputBuffer)) > 0) {
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                s.append(readString);
                inputBuffer = new char[READ_BLOCK_SIZE];
            }
            DataSaved = s.toString();
        } catch (IOException | NullPointerException ignored) {}
        return DataSaved;
    }
}
