package coords_calc;


import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

import services_and_bluetooth.DataSaved;

public class CoordsConverter {
    //static CRSFactory crsFactory = new CRSFactory();
    //static CoordinateReferenceSystem A = crsFactory.createFromName("epsg:" + "4326");
    //static CoordinateReferenceSystem B = crsFactory.createFromName("epsg:" + DataSaved.S_CRS);

   //static  CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
    //static CRSFactory crsFactory1 = new CRSFactory();
    //static CoordinateReferenceSystem A1 = crsFactory1.createFromName("epsg:" + DataSaved.S_CRS);
   // static CoordinateReferenceSystem B1 = crsFactory1.createFromName("epsg:" + "4326");

    //static CoordinateTransformFactory ctFactory1 = new CoordinateTransformFactory();


    public static double[] transformIntoCRS(String epsg, double latitude, double longitude){
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem A = crsFactory.createFromName("epsg:" + "4326");
        CoordinateReferenceSystem B = crsFactory.createFromName("epsg:" + DataSaved.S_CRS);
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform AB = ctFactory.createTransform(A, B);

        ProjCoordinate result = new ProjCoordinate();

        AB.transform(new ProjCoordinate(longitude, latitude), result);

        return new double[]{result.x, result.y};
    }



    public static double[] transformIntoWGS84(String epsg, double x, double y){
        CRSFactory crsFactory1 = new CRSFactory();
        CoordinateReferenceSystem A1 = crsFactory1.createFromName("epsg:" + DataSaved.S_CRS);
        CoordinateReferenceSystem B1 = crsFactory1.createFromName("epsg:" + "4326");
        CoordinateTransformFactory ctFactory1 = new CoordinateTransformFactory();
        CoordinateTransform AB = ctFactory1.createTransform(A1, B1);

        ProjCoordinate result = new ProjCoordinate();

        AB.transform(new ProjCoordinate(x, y), result);

        return new double[]{result.y, result.x};

        //result.y = latitudine
        //result.x = longitudine
    }

    public static String getInfoParams(String epsg){

        CRSFactory crsFactory = new CRSFactory();

        CoordinateReferenceSystem CRS = crsFactory.createFromName("epsg:" + epsg);

        return String.join(",", CRS.getParameters());
    }
}
