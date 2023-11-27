package coords_calc;

import android.location.Location;

import org.locationtech.jts.geom.Coordinate;

import java.util.Map;

import gnss.Nmea_In;
import project.DataProjectSingleton;

public class Surface_Selector {
    private Surface_3pts surface3pts;
    private Surface_4ptsPlus surface4ptsPlus;
    private int size;

    public Surface_Selector(int mapSize) {
        this.size = mapSize;

        DataProjectSingleton dataProject = DataProjectSingleton.getInstance();

        Coordinate[] points;

        int counter = 0;

        switch (size) {
            case 1:
                break;

            case 3:
                surface3pts = new Surface_3pts();

                points = new Coordinate[3];

                for (Map.Entry<String, GPS> entry : dataProject.getPoints().entrySet()) {
                    GPS value = entry.getValue();
                    points[counter++] = new Coordinate(value.getX(), value.getY(), value.getZ());
                }

                surface3pts.updateData(points);
                break;

            case 6:
            case 4:

                surface4ptsPlus = new Surface_4ptsPlus();

                points = new Coordinate[size];


                for (Map.Entry<String, GPS> entry : dataProject.getPoints().entrySet()) {
                    GPS value = entry.getValue();
                    points[counter++] = new Coordinate(value.getX(), value.getY(), value.getZ());
                }

                surface4ptsPlus.setData(points);
                break;
        }
    }

    public double getAltitudeDifference(double myLat, double myLong, double myZ) {


        switch (size) {
            case 1:

                break;
            case 3:
                return surface3pts.getAltitudeDifference(Nmea_In.Crs_Est, Nmea_In.Crs_Nord, myZ);
            case 4:
            case 6:
                return surface4ptsPlus.getAltitudeDifference(Nmea_In.Crs_Est, Nmea_In.Crs_Nord, myZ);
        }

        return 0;
    }


    public double getDistance() {

        DataProjectSingleton dataProject = DataProjectSingleton.getInstance();

        GPS refPoint = dataProject.getSinglePoint();

        if (refPoint == null) {
            GPS myA = dataProject.getPoints().get("A");//coordinate misurate di A
            GPS myB = dataProject.getPoints().get("B");//coordinate misurate di B
            double diff = Math.abs(Nmea_In.tractorBearing - dataProject.abOrient());
            if (diff >= 90) {
                try {
                    return new DistToLine(Nmea_In.Crs_Est, Nmea_In.Crs_Nord, myB.getX(), myB.getY(), myA.getX(), myA.getY()).linedistance;

                } catch (Exception e) {
                    return 0;
                }

            } else {
                try {
                    return new DistToLine(Nmea_In.Crs_Est, Nmea_In.Crs_Nord, myA.getX(), myA.getY(), myB.getX(), myB.getY()).linedistance;

                } catch (Exception e) {
                  return 0;
                }
            }

        }

        float[] result = new float[3];

        Location.distanceBetween(Nmea_In.mLat_1, Nmea_In.mLon_1, refPoint.getLatitude(), refPoint.getLongitude(), result);


        return result[0];
    }

    public boolean isPointInsideSurface() {

        switch (size) {
            case 1:

                break;

            case 3:
                return surface3pts.isPointInsideTriangle(Nmea_In.Crs_Est, Nmea_In.Crs_Nord);


            case 4:
            case 6:
                return surface4ptsPlus.isPointInside1(Nmea_In.Crs_Est, Nmea_In.Crs_Nord) || surface4ptsPlus.isPointInside2(Nmea_In.Crs_Est, Nmea_In.Crs_Nord);

        }

        return false;
    }

    public boolean isSurfaceOK() {

        switch (size) {
            case 1:

                break;

            case 3:
                return surface3pts.isSurfOk();

            case 4:
            case 6:
                return surface4ptsPlus.isSurfOk();
        }

        return false;
    }
}
