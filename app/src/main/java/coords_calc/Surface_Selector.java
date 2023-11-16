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
        DataProjectSingleton dataProject = DataProjectSingleton.getInstance();

        double[] myCoords = CoordsConverter.transformIntoCRS(dataProject.getEpsgCode(), myLat, myLong);

        switch (size) {
            case 1:

                break;
            case 3:
                return surface3pts.getAltitudeDifference(myCoords[0], myCoords[1], myZ);
            case 4:
            case 6:
                return surface4ptsPlus.getAltitudeDifference(myCoords[0], myCoords[1], myZ);
        }

        return 0;
    }

    public double getDistance(double myLat, double myLong) {

        DataProjectSingleton dataProject = DataProjectSingleton.getInstance();

        GPS refPoint = dataProject.getSinglePoint();

        if (refPoint == null) {
            return 0;
        }

        float[] result = new float[3];

        Location.distanceBetween(myLat, myLong, refPoint.getLatitude(), refPoint.getLongitude(), result);

        return result[0];
    }

    public boolean isPointInsideSurface() {
        DataProjectSingleton dataProject = DataProjectSingleton.getInstance();

        double[] myCoords = CoordsConverter.transformIntoCRS(dataProject.getEpsgCode(), Nmea_In.mLat_1, Nmea_In.mLon_1);

        switch (size) {
            case 1:

                break;

            case 3:
                return surface3pts.isPointInsideTriangle(myCoords[0], myCoords[1]);


            case 4:
            case 6:
                return surface4ptsPlus.isPointInside1(myCoords[0], myCoords[1]) || surface4ptsPlus.isPointInside2(myCoords[0], myCoords[1]);

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
