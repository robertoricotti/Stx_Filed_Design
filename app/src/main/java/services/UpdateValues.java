package services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import utils.MyRW_IntMem;

public class UpdateValues extends Service {
    MyRW_IntMem myRW_intMem;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        myRW_intMem = new MyRW_IntMem();

        String macaddress = myRW_intMem.MyRead("_macaddress", this);
        String gpsname = myRW_intMem.MyRead("_gpsname", this);
        String crs = myRW_intMem.MyRead("_crs", this);
        String altezzaAnt=myRW_intMem.MyRead("_altezzaantenna",this);
        String unitOfMeasure = myRW_intMem.MyRead("_unitofmeasure", this);

        if(macaddress==null){
            myRW_intMem.MyWrite("_macaddress","00:00:00:00:00:00",this);
        }
        if(gpsname==null){
            myRW_intMem.MyWrite("_gpsname","UNKNOWN",this);
        }

        if(crs==null){
            myRW_intMem.MyWrite("_crs","3004",this);
        }
        if(altezzaAnt==null){
            myRW_intMem.MyWrite("_altezzaantenna","2",this);
        }
        if (unitOfMeasure == null) {
            myRW_intMem.MyWrite("_unitofmeasure", "0", this);
        }






        DataSaved.S_macAddres=myRW_intMem.MyRead("_macaddress",this);
        DataSaved.S_gpsname=myRW_intMem.MyRead("_gpsname",this);
        DataSaved.S_CRS=myRW_intMem.MyRead("_crs",this);
        DataSaved.D_AltezzaAnt=Double.parseDouble(myRW_intMem.MyRead("_altezzaantenna",this).replace(",","."));
        DataSaved.I_UnitOfMeasure=Integer.parseInt(myRW_intMem.MyRead("_unitofmeasure",this));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.stopSelf();
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}