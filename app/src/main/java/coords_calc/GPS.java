package coords_calc;

public class GPS {

    private double x;
    private double y;
    private double latitude;
    private double longitude;
    private double z;

    public GPS(String epsg, double x, double y, double z) {
        double[] res = CoordsConverter.transformIntoWGS84(epsg, x, y);
        this.x = x;
        this.y = y;
        this.latitude = res[0];
        this.longitude = res[1];
        this.z = z;
    }

    public GPS(double latitude, double longitude, double z, String epsg) {
        double[] res = CoordsConverter.transformIntoCRS(epsg, latitude, longitude);
        this.latitude = latitude;
        this.longitude = longitude;
        this.x = res[0];
        this.y = res[1];
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "GPS{" +
                "x=" + x +
                ", y=" + y +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", z=" + z +
                '}';
    }
}
