package gnss;


import java.nio.charset.StandardCharsets;
import java.util.Locale;

import services.DataSaved;

public class NmeaListenerGGAH {
    public String[] NmeaInput;
    CalculateXor8 calculateXor8;
    String myNmea;
    String mNmea1, mNmea2;
    Deg2UTM deg2UTM;
    public static char mChar;
    public static int mZone;
    public static String VRMS_, HRMS_, sHDT, _3DRMS;
    public static double mLat_1, mLon_1, mLat_2, mLon_2;
    public static double Nord1, Est1, Quota1, Nord2, Est2, Quota2, mch_Orientation, mch_Hdt, mchBaseline;
    public static String ggaNord, ggaEast, ggaNoS, ggaWoE, ggaZ1, ggaZ2, ggaSat, ggaDop, ggaQuality, ggaRtk, sMchOrient;//String data from  GPS1
    public static String gga_H_Nord, gga_H_East, gga_H_NoS, gga_H_WoE, gga_H_Z1, gga_H_Z2, gga_H_Sat, gga_H_Dop, gga_H_Quality, gga_H_Rtk;//String data from  GPS1

    public NmeaListenerGGAH(String NmeaGGAH) {


        try {
            myNmea = NmeaGGAH.substring(NmeaGGAH.indexOf("$") + 1, NmeaGGAH.indexOf("*"));
            calculateXor8 = new CalculateXor8(myNmea.getBytes(StandardCharsets.UTF_8));
            mNmea1 = NmeaGGAH.substring(NmeaGGAH.indexOf("*") + 1);
            mNmea2 = Integer.toHexString(calculateXor8.xor).toUpperCase(Locale.ROOT).toString();

            if (mNmea1.contains(mNmea2)) {
                NmeaInput = NmeaGGAH.split(",");


                switch (NmeaInput[0]) {

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

                            /*

                             */
                            int LonInt = Integer.parseInt(ggaEast.substring(0, 3));
                            double LonDec = Double.parseDouble(ggaEast.substring(3, NmeaInput[2].length()));
                            if (ggaWoE.equals("E")) {
                                mLon_1 = LonDec / 60 + LonInt;
                            } else if (ggaWoE.equals("W")) {
                                mLon_1 =  LonDec / 60 + LonInt;
                                mLon_1 = mLon_1 * -1;
                            }
                            /*

                             */

                            deg2UTM = new Deg2UTM(mLat_1, mLon_1);
                            Nord1 = deg2UTM.getNorthing();
                            Est1 = deg2UTM.getEasting();
                            Quota1 = Double.parseDouble(ggaZ1.replace(",", ".")) + Double.parseDouble(ggaZ2.replace(",", "."));
                            Quota1=Quota1- DataSaved.D_AltezzaAnt;
                            mChar = deg2UTM.getLetter();
                            mZone = deg2UTM.getZone();


                            break;
                        } catch (Exception e) {

                        }
                    case "$GNGGAH":
                    case "$GPGGAH":
                        try {
                            gga_H_Nord = NmeaInput[2];//Latitudine
                            gga_H_NoS = NmeaInput[3];
                            gga_H_East = NmeaInput[4];//Longitudine
                            gga_H_WoE = NmeaInput[5];
                            gga_H_Quality = NmeaInput[6];
                            gga_H_Sat = NmeaInput[7];
                            gga_H_Dop = NmeaInput[8];
                            gga_H_Z1 = NmeaInput[9];
                            gga_H_Z2 = NmeaInput[11];
                            gga_H_Rtk = NmeaInput[13];

                            int LatIntH = Integer.parseInt(gga_H_Nord.substring(0, 2));
                            double LatDecH = Double.parseDouble(gga_H_Nord.substring(2, NmeaInput[2].length()));
                            if (gga_H_NoS.equals("N")) {
                                mLat_2 = LatDecH / 60 + LatIntH;
                            } else if (gga_H_NoS.equals("S")) {
                                mLat_2 =  LatDecH / 60 + LatIntH;
                                mLat_2 = mLat_2 * -1;
                            }
                            /*

                             */
                            int LonIntH = Integer.parseInt(gga_H_East.substring(0, 3));
                            double LonDecH = Double.parseDouble(gga_H_East.substring(3, NmeaInput[4].length()));
                            if (ggaWoE.equals("E")) {
                                mLon_2 = LonDecH / 60 + LonIntH;
                            } else if (ggaWoE.equals("W")) {
                                mLon_2 = LonDecH / 60 + LonIntH;
                                mLon_2 = mLon_2 * -1;
                            }
                            /*

                             */
                            Nord2 = new Deg2UTM(mLat_2,mLon_2).getNorthing();
                            Est2 = new Deg2UTM(mLat_2,mLon_2).getEasting();
                            Quota2 = Double.parseDouble(gga_H_Z1.replace(",", ".")) + Double.parseDouble(gga_H_Z2.replace(",", "."));
                            break;
                        } catch (Exception e) {

                        }
                    case "$GPHDT":
                    case "$GNHDT":
                    case "$HCHDT":

                        try {
                            mch_Hdt = Double.parseDouble(NmeaInput[1]);
                            if (NmeaInput[1].equals("0.0000") || NmeaInput[1].equals("")) {
                                mch_Hdt = 0;
                            }
                            sHDT = String.format("%.3f", mch_Hdt);
                            break;

                        } catch (Exception e) {
                            mch_Hdt = 0;
                            sHDT = String.format("%.3f", mch_Hdt);
                        }


                    case "$GPGST":
                    case "$GNGST":
                        try {
                            String LatCQ = NmeaInput[6];
                            String LonCQ = NmeaInput[7];
                            String HgtCQ = NmeaInput[8].substring(0, NmeaInput[8].indexOf("*"));
                            VRMS_ = String.format("%.3f", Float.parseFloat(HgtCQ));
                            HRMS_ = String.format("%.3f",2 * Math.sqrt(0.5 * ((Math.pow(Double.parseDouble(LatCQ), 2) + Math.pow(Double.parseDouble(LonCQ), 2)) / 2)));
                            _3DRMS = String.format("%.3f",3 * Math.sqrt(((Math.pow(Double.parseDouble(LatCQ), 2) + Math.pow(Double.parseDouble(LonCQ), 2)) / 2)));
                            break;
                        } catch (Exception e) {
                            VRMS_ = "-0";
                            HRMS_ = "-0";
                            _3DRMS = "-0";
                        }
                    case "$GPRMC":
                    case "$GNRMC":
                        try {

                            break;
                        } catch (Exception e) {

                        }
                }
            }
            try {
                if (gga_H_Quality.equals("4") && ggaQuality.equals("4")) {
                    mch_Orientation = HDTCalculator.calculateHDT(Est1, Nord1, Est2, Nord2);

                } else {
                    mch_Orientation = mch_Hdt;
                }
                sMchOrient = String.format("%.3f", mch_Orientation);
                mchBaseline = new Gps1Gps2_Baseline(Est1, Nord1, Quota1, Est2, Nord2, Quota2).getBaseline();
            } catch (Exception e) {

                mch_Orientation = 0.000;
            }


        } catch (Exception e) {

        }
    }


}
