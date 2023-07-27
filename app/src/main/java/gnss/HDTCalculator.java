package gnss;



public class HDTCalculator {

    // Constants
    private static final double EARTH_RADIUS = 6378137.0; // Earth's radius in meters??



    public static double calculateHDT(double startEast, double startNorth, double endEast, double endNorth) {


        double lat2 = Math.toRadians(startNorth / EARTH_RADIUS);
        double lon2 = Math.toRadians(startEast / EARTH_RADIUS);
        double lat1 = Math.toRadians(endNorth / EARTH_RADIUS);
        double lon1 = Math.toRadians(endEast / EARTH_RADIUS);


        double dLon = lon2 - lon1;
        double a = Math.pow(Math.cos(lat2) * Math.sin(dLon), 2) +
                Math.pow(Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon), 2);
        double b = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double c = Math.atan2(Math.sqrt(a), b);
        double distance = EARTH_RADIUS * c;



        double azimuth = Math.atan2(Math.sin(dLon) * Math.cos(lat2), Math.cos(lat1) * Math.sin(lat2) -
                Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon));
        azimuth = Math.toDegrees(azimuth);

        // Calcola HDT dall'azimuth
        double hdt = (azimuth + 180) % 360;

        return hdt;
    }
}
