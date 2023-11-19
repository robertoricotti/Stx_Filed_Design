package gnss;

import java.util.Queue;
import java.util.LinkedList;

import services_and_bluetooth.DataSaved;

public class GPSTracker {


    static Queue<Double> bearingQueue = new LinkedList<>();
    static Queue<Position> positionQueue = new LinkedList<>();

    public static void onLocationUpdate(double newLat, double newLon, int size) {

        positionQueue.offer(new Position(newLat, newLon));


        if (positionQueue.size() > 5) {

            positionQueue.poll();
        }


        if (positionQueue.size() >= size) {
            Position lastPosition = positionQueue.poll();
            Position secondLastPosition = positionQueue.peek();


            double distance = calculateDistance(secondLastPosition.lat, secondLastPosition.lon, newLat, newLon);
            if (distance > 0.2) {//dist 20cm

                double bearing = calculateBearing(secondLastPosition.lat, secondLastPosition.lon, newLat, newLon);


                bearingQueue.offer(bearing);

                if (bearingQueue.size() > DataSaved.rmcSize) {
                    bearingQueue.poll();
                }
            }

        }

        positionQueue.offer(new Position(newLat, newLon));
    }

    public static double getAverageBearing() {
        if (bearingQueue.isEmpty()) {
            return 0.0; // Ritorna 0.0 se la coda è vuota
        }

        double sum = 0.0;
        for (Double bearing : bearingQueue) {
            sum += bearing;
        }
        double res = sum / bearingQueue.size();
        if (res > 180) {
            res -= 360;
        } else if (res < 0) {
            res += 360;
        }
        return res;
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {


        final double R = 6371.0; // Raggio medio della Terra in km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000; // Distanza in metri
    }

    private static double calculateBearing(double lat1, double lon1, double lat2, double lon2) {


        double dLon = Math.toRadians(lon2 - lon1);

        double y = Math.sin(dLon) * Math.cos(Math.toRadians(lat2));
        double x = Math.cos(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) -
                Math.sin(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(dLon);

        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (bearing + 360) % 360;
    }

    private static class Position {
        double lat;
        double lon;

        Position(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }
}