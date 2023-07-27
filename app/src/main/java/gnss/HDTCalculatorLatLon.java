package gnss;

public class HDTCalculatorLatLon {
    public String s_bearing;
    public double bearing;

    public HDTCalculatorLatLon(double Lon1,double Lat1,double Lon2,double Lat2){
        double lat1=Lat1;
        double lon1=Lon1;
        double lat2=Lat2;
        double lon2=Lon2;
        double dLon = lon2 - lon1;

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);

        bearing = Math.atan2(y, x);

        bearing = Math.toDegrees(bearing);
        bearing = (bearing + 360d) % 360;
        s_bearing=String.format("%.4f",bearing);
    }

    public double getBearing() {
        return bearing;
    }

    public String getS_bearing() {
        return s_bearing;
    }
}