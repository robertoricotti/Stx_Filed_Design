package gnss;


import org.locationtech.proj4j.ProjCoordinate;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import coords_calc.GPS;
import services.DataSaved;
import services.UpdateValues;

public class Nmea_In {


    public String[] NmeaInput;
    CalculateXor8 calculateXor8;
    String myNmea;
    String mNmea1, mNmea2;
    Deg2UTM deg2UTM;
    GPS gps_cl;
    public static char mChar;
    public static int mZone;
    public static String VRMS_, HRMS_;
    public static double Nord1, Est1, Quota1, mLat_1, mLon_1, mSpeed_rmc, mBearing_rmc, tractorBearing,Crs_Nord,Crs_Est;
    public static String ggaNord, ggaEast, ggaNoS, ggaWoE, ggaZ1, ggaZ2, ggaSat, ggaDop, ggaQuality, ggaRtk, sMchOrient;//String data from  GPS1


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
                                mLat_1 = LatDec / 60 + LatInt;
                            } else if (ggaNoS.equals("S")) {
                                mLat_1 = LatDec / 60 + LatInt;
                                mLat_1 = mLat_1 * -1;
                            }

                            int LonInt = Integer.parseInt(ggaEast.substring(0, 3));
                            double LonDec = Double.parseDouble(ggaEast.substring(3, NmeaInput[2].length()));
                            if (ggaWoE.equals("E")) {
                                mLon_1 = LonDec / 60 + LonInt;
                            } else if (ggaWoE.equals("W")) {
                                mLon_1 = LonDec / 60 + LonInt;
                                mLon_1 = mLon_1 * -1;
                            }


                            deg2UTM = new Deg2UTM(mLat_1, mLon_1);

                            Nord1 = deg2UTM.getNorthing();
                            Est1 = deg2UTM.getEasting();
                            Quota1 = Double.parseDouble(ggaZ1.replace(",", ".")) + Double.parseDouble(ggaZ2.replace(",", "."));
                            Quota1 = Quota1 - DataSaved.D_AltezzaAnt;
                            mChar = deg2UTM.getLetter();
                            mZone = deg2UTM.getZone();
                            UpdateValues.wgsToUtm.transform(new ProjCoordinate(mLon_1, mLat_1), UpdateValues.result);
                            Crs_Est= UpdateValues.result.x;
                            Crs_Nord=UpdateValues.result.y;
                            break;
                        } catch (Exception e) {

                        }

                    case "$GPGST":
                    case "$GNGST":
                        try {
                            String LatCQ = NmeaInput[6];
                            String LonCQ = NmeaInput[7];
                            String HgtCQ = NmeaInput[8].substring(0, NmeaInput[8].indexOf("*"));
                            VRMS_ = String.format("%.3f", Float.parseFloat(HgtCQ));
                            HRMS_ = String.format("%.3f", 2 * Math.sqrt(0.5 * ((Math.pow(Double.parseDouble(LatCQ), 2) + Math.pow(Double.parseDouble(LonCQ), 2)) / 2)));

                            break;
                        } catch (Exception e) {
                            VRMS_ = "-0";
                            HRMS_ = "-0";

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
                tractorBearing = MachineBearing_fromRMC.machineBearing(mBearing_rmc, mSpeed_rmc, DataSaved.rmcSize);
            }else if(DataSaved.useRmc==1){

                GPSTracker.onLocationUpdate(mLat_1,mLon_1,DataSaved.rmcSize);
               tractorBearing= GPSTracker.getAverageBearing();
            }

        } catch (Exception e) {

        }
    }


}
