package gnss;


import android.annotation.SuppressLint;

import org.locationtech.proj4j.ProjCoordinate;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import can.Can_Decoder;
import coords_calc.CoordsConverter;
import services_and_bluetooth.DataSaved;
import services_and_bluetooth.UpdateValues;

public class Nmea_In {
    public String[] NmeaInput;
    CalculateXor8 calculateXor8;
    String myNmea;
    String mNmea1, mNmea2;
    private double lat1, lon1, tempq;
    public static int Zone;
    public static char Band;
    public static String VRMS_, HRMS_, CQ_Tot;
    public static double Nord1, Est1, Quota1, mLat_1, mLon_1, mSpeed_rmc, mBearing_rmc, tractorBearing, Crs_Nord, Crs_Est, mch_Hdt;
    public static String ggaNord, ggaEast, ggaNoS, ggaWoE, ggaZ1, ggaZ2, ggaSat, ggaDop, ggaQuality, ggaRtk;//String data from  GPS1


    @SuppressLint("NewApi")
    public Nmea_In(String NmeaGGAH) {

        try {
            myNmea = NmeaGGAH.substring(NmeaGGAH.indexOf("$") + 1, NmeaGGAH.indexOf("*"));
            calculateXor8 = new CalculateXor8(myNmea.getBytes(StandardCharsets.UTF_8));
            mNmea1 = NmeaGGAH.substring(NmeaGGAH.indexOf("*") + 1);
            mNmea2 = Integer.toHexString(calculateXor8.xor).toUpperCase(Locale.ROOT).toString();

            if (mNmea1.contains(mNmea2)) {
                NmeaInput = NmeaGGAH.split(",");


                switch (NmeaInput[0]) {

                    case "$GLGGA":
                    case "$GNGGA":
                    case "$GPGGA":
                        try {
                            ggaNord = NmeaInput[2];//Latitudine
                            ggaNoS = NmeaInput[3];
                            ggaEast = NmeaInput[4];//Longitudine
                            ggaWoE = NmeaInput[5];
                            ggaQuality = NmeaInput[6];
                            ggaSat = NmeaInput[7];
                            ggaDop = NmeaInput[8];
                            ggaZ1 = NmeaInput[9];
                            ggaZ2 = NmeaInput[11];
                            ggaRtk = NmeaInput[13];

                            int LatInt = Integer.parseInt(ggaNord.substring(0, 2));
                            double LatDec = Double.parseDouble(ggaNord.substring(2, NmeaInput[2].length()));
                            if (ggaNoS.equals("N")) {
                                lat1 = LatDec / 60 + LatInt;
                            } else if (ggaNoS.equals("S")) {
                                lat1 = LatDec / 60 + LatInt;
                                lat1 = lat1 * -1;
                            }

                            int LonInt = Integer.parseInt(ggaEast.substring(0, 3));
                            double LonDec = Double.parseDouble(ggaEast.substring(3, NmeaInput[2].length()));
                            if (ggaWoE.equals("E")) {
                                lon1 = LonDec / 60 + LonInt;
                            } else if (ggaWoE.equals("W")) {
                                lon1 = LonDec / 60 + LonInt;
                                lon1 = lon1 * -1;
                            }
                            tempq = DataSaved.offset_Z_antenna+Double.parseDouble(ggaZ1.replace(",", ".")) + Double.parseDouble(ggaZ2.replace(",", "."));
                            Quota1 = tempq - DataSaved.D_AltezzaAnt;
                            if (DataSaved.useTilt == 0) {
                                mLat_1 = lat1;
                                mLon_1 = lon1;
                                Deg2UTM deg2UTM = new Deg2UTM(lat1, lon1);
                                Crs_Est = deg2UTM.getEasting();//UpdateValues.result.x;
                                Crs_Nord = deg2UTM.getNorthing(); //UpdateValues.result.y;
                                Band =deg2UTM.getLetter();
                                Zone = deg2UTM.getZone();

                            } else if (DataSaved.useTilt == 1) {

                                double x = new Deg2UTM(lat1, lon1).Easting;
                                double y = new Deg2UTM(lat1, lon1).Northing;
                                char band = new Deg2UTM(lat1, lon1).Letter;
                                int zone = new Deg2UTM(lat1, lon1).Zone;
                                double[] end = Exca_Quaternion.endPoint(new double[]{x, y, tempq}, Can_Decoder.correctPitch - 90, Can_Decoder.correctRoll, DataSaved.D_AltezzaAnt, tractorBearing);
                                Crs_Est = end[0];
                                Crs_Nord = end[1];
                                Quota1 = end[2];
                                double[] latlon = new UTM2Deg(zone, band, x, y).getLatLon(); //CoordsConverter.transformIntoWGS84(DataSaved.S_CRS,end[0],end[1]);
                                mLon_1 = latlon[1];
                                mLat_1 = latlon[0];
                                Band = new Deg2UTM(mLat_1, mLon_1).getLetter();
                                Zone = new Deg2UTM(mLat_1, mLon_1).getZone();


                            }
                            break;
                        } catch (Exception e) {

                        }
                    case "$GPHDT":
                    case "$GNHDT":
                    case "$HCHDT":

                        try {
                            mch_Hdt = Double.parseDouble(NmeaInput[1]);

                            if (NmeaInput[1].equals("0.0000") || NmeaInput[1].equals("")) {
                                mch_Hdt = 999;
                            }

                            break;

                        } catch (Exception e) {
                            mch_Hdt = 0;

                        }

                    case "$GPGST":
                    case "$GNGST":
                        try {
                            String LatCQ = NmeaInput[6];
                            String LonCQ = NmeaInput[7];
                            String HgtCQ = NmeaInput[8].substring(0, NmeaInput[8].indexOf("*"));
                            VRMS_ = String.format("%.3f", Float.parseFloat(HgtCQ));
                            HRMS_ = String.format("%.3f", 2 * Math.sqrt(0.5 * ((Math.pow(Double.parseDouble(LatCQ), 2) + Math.pow(Double.parseDouble(LonCQ), 2)) / 2)));
                            CQ_Tot = String.format("%.3f", Math.abs(Double.parseDouble(VRMS_) + Double.parseDouble(HRMS_) / 2));
                            break;
                        } catch (Exception e) {
                            VRMS_ = "_";
                            HRMS_ = "_";
                            CQ_Tot = "_";

                        }
                    case "$GPRMC":
                    case "$GNRMC":
                        try {
                            mSpeed_rmc = Double.parseDouble(NmeaInput[7]);
                            mBearing_rmc = Double.parseDouble(NmeaInput[8]);


                        } catch (Exception e) {
                            mSpeed_rmc = 0;
                            mBearing_rmc = 0;
                        }
                        break;

                }
            }

            if (DataSaved.useRmc == 0) {
                tractorBearing = MachineBearing_from_RMC.machineBearing(mBearing_rmc, mSpeed_rmc, DataSaved.rmcSize);
            } else if (DataSaved.useRmc == 1) {

                MachineBearing_from_POSITIONS.onLocationUpdate(mLat_1, mLon_1, DataSaved.rmcSize);
                tractorBearing = MachineBearing_from_POSITIONS.getAverageBearing();
            } else if (DataSaved.useRmc == 2) {
                tractorBearing = mch_Hdt;
            }

        } catch (Exception e) {

        }
    }


}
