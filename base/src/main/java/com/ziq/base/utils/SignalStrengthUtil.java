package com.ziq.base.utils;

import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import java.lang.reflect.Field;

/**
 * @author john.
 * @since 2018/5/3.
 * Des: 提取Android 源码中的SignalStrength 不公开的方法。获得网络制式，信号强度
 */

public final class SignalStrengthUtil {

    public static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    public static final int SIGNAL_STRENGTH_POOR = 1;
    public static final int SIGNAL_STRENGTH_MODERATE = 2;
    public static final int SIGNAL_STRENGTH_GOOD = 3;
    public static final int SIGNAL_STRENGTH_GREAT = 4;
    public static final int NETWORK_TYPE_LTE_CA = 19;
    private static final String TAG = "SignalStrengthUtil";
    private static final int RSRP_THRESH_TYPE_STRICT = 0;
    private static final int[] RSRP_THRESH_STRICT = new int[]{-140, -115, -105, -95, -85, -44};
    private static final int[] RSRP_THRESH_LENIENT = new int[]{-140, -128, -118, -108, -98, -44};


    private SignalStrengthUtil() {
    }

    /**
     * @param networkType 网络类型
     * @return 网络制式
     */
    public static String getNetworkClass(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "E";
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "H";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "H+";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
            case TelephonyManager.NETWORK_TYPE_IWLAN:
            case NETWORK_TYPE_LTE_CA:
                return "4G";
            default:
                return "";
        }
    }

    /**
     * @param signalStrength 信号
     * @return 信号级别
     */
    public static int getLevel(SignalStrength signalStrength, String[] arraySignalStrength) {
        int level = 0;
        if (signalStrength.isGsm()) {
            int mLteRsrp = 0;
            int mLteRssnr = 0;
            int mLteSignalStrength = 99;
            Class signalStrengthClass = signalStrength.getClass();
            try {
                Field mLteRsrpField = signalStrengthClass.getDeclaredField("mLteRsrp");
                mLteRsrpField.setAccessible(true);
                mLteRsrp = (int) mLteRsrpField.get(signalStrength);

                Field mLteRssnrField = signalStrengthClass.getDeclaredField("mLteRssnr");
                mLteRssnrField.setAccessible(true);
                mLteRssnr = (int) mLteRssnrField.get(signalStrength);

                Field mLteSignalStrengthField = signalStrengthClass.getDeclaredField("mLteSignalStrength");
                mLteSignalStrengthField.setAccessible(true);
                mLteSignalStrength = (int) mLteSignalStrengthField.get(signalStrength);

            } catch (Exception e) {
            }

            level = getLteLevel(mLteRsrp, mLteRssnr, mLteSignalStrength);
            arraySignalStrength[0] = String.valueOf(mLteRsrp);
            if (level == SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
//                level = getTdScdmaLevel();
                if (level == SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                    int dbm = -113 + 2 * signalStrength.getGsmSignalStrength();
                    arraySignalStrength[0] = String.valueOf(dbm);
                    level = getGsmLevel(signalStrength.getGsmSignalStrength());
                }
            }
        } else {
            int cdmaLevel = getCdmaLevel(signalStrength.getCdmaDbm(), signalStrength.getCdmaEcio());
            int evdoLevel = getEvdoLevel(signalStrength.getEvdoDbm(), signalStrength.getEvdoSnr());
            if (evdoLevel == SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                /* We don't know evdo, use cdma */
                level = cdmaLevel;
                //为了对arraySignalStrength进行赋值，拿db强度
                arraySignalStrength[0] = String.valueOf(signalStrength.getCdmaDbm());
            } else if (cdmaLevel == SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                /* We don't know cdma, use evdo */
                level = evdoLevel;
                //为了对arraySignalStrength进行赋值，拿db强度
                arraySignalStrength[0] = String.valueOf(signalStrength.getEvdoDbm());
            } else {
                /* We know both, use the lowest level */
                level = cdmaLevel < evdoLevel ? cdmaLevel : evdoLevel;
                if (cdmaLevel < evdoLevel) {
                    arraySignalStrength[0] = String.valueOf(signalStrength.getCdmaDbm());
                } else {
                    arraySignalStrength[0] = String.valueOf(signalStrength.getEvdoDbm());
                }
            }
        }
        return level;
    }

    /**
     * @param mLteRsrp           参考信号接收功率
     * @param mLteRssnr          信噪比
     * @param mLteSignalStrength 信号
     * @return lte信号级别
     */
    public static int getLteLevel(int mLteRsrp, int mLteRssnr, int mLteSignalStrength) {
        /*
         * TS 36.214 Physical Layer Section 5.1.3 TS 36.331 RRC RSSI = received
         * signal + noise RSRP = reference signal dBm RSRQ = quality of signal
         * dB= Number of Resource blocksxRSRP/RSSI SNR = gain=signal/noise ratio
         * = -10log P1/P2 dB
         */
        int rssiIconLevel = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        int rsrpIconLevel = -1;
        int snrIconLevel = -1;

        int[] threshRsrp = RSRP_THRESH_LENIENT;

        if (mLteRsrp > threshRsrp[5]) {
            rsrpIconLevel = -1;
        } else if (mLteRsrp >= threshRsrp[4]) {
            rsrpIconLevel = SIGNAL_STRENGTH_GREAT;
        } else if (mLteRsrp >= threshRsrp[3]) {
            rsrpIconLevel = SIGNAL_STRENGTH_GOOD;
        } else if (mLteRsrp >= threshRsrp[2]) {
            rsrpIconLevel = SIGNAL_STRENGTH_MODERATE;
        } else if (mLteRsrp >= threshRsrp[1]) {
            rsrpIconLevel = SIGNAL_STRENGTH_POOR;
        } else if (mLteRsrp >= threshRsrp[0]) {
            rsrpIconLevel = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        }

        /*
         * Values are -200 dB to +300 (SNR*10dB) RS_SNR >= 13.0 dB =>4 bars 4.5
         * dB <= RS_SNR < 13.0 dB => 3 bars 1.0 dB <= RS_SNR < 4.5 dB => 2 bars
         * -3.0 dB <= RS_SNR < 1.0 dB 1 bar RS_SNR < -3.0 dB/No Service Antenna
         * Icon Only
         */
        if (mLteRssnr > 300) {
            snrIconLevel = -1;
        } else if (mLteRssnr >= 130) {
            snrIconLevel = SIGNAL_STRENGTH_GREAT;
        } else if (mLteRssnr >= 45) {
            snrIconLevel = SIGNAL_STRENGTH_GOOD;
        } else if (mLteRssnr >= 10) {
            snrIconLevel = SIGNAL_STRENGTH_MODERATE;
        } else if (mLteRssnr >= -30) {
            snrIconLevel = SIGNAL_STRENGTH_POOR;
        } else if (mLteRssnr >= -200) {
            snrIconLevel = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        }

        if (snrIconLevel != -1 && rsrpIconLevel != -1) {
            return (rsrpIconLevel < snrIconLevel ? rsrpIconLevel : snrIconLevel);
        }

        if (snrIconLevel != -1) {
            return snrIconLevel;
        }

        if (rsrpIconLevel != -1) {
            return rsrpIconLevel;
        }

        /* Valid values are (0-63, 99) as defined in TS 36.331 */
        if (mLteSignalStrength > 63) {
            rssiIconLevel = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        } else if (mLteSignalStrength >= 12) {
            rssiIconLevel = SIGNAL_STRENGTH_GREAT;
        } else if (mLteSignalStrength >= 8) {
            rssiIconLevel = SIGNAL_STRENGTH_GOOD;
        } else if (mLteSignalStrength >= 5) {
            rssiIconLevel = SIGNAL_STRENGTH_MODERATE;
        } else if (mLteSignalStrength >= 0) {
            rssiIconLevel = SIGNAL_STRENGTH_POOR;
        }
        return rssiIconLevel;

    }

    /**
     * @param asu
     * @return
     */
    public static int getGsmLevel(int asu) {
        int level;

        // ASU ranges from 0 to 31 - TS 27.007 Sec 8.5
        // asu = 0 (-113dB or less) is very weak
        // signal, its better to show 0 bars to the user in such cases.
        // asu = 99 is a special case, where the signal strength is unknown.
        if (asu <= 2 || asu == 99) {
            level = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        } else if (asu >= 12) {
            level = SIGNAL_STRENGTH_GREAT;
        } else if (asu >= 8) {
            level = SIGNAL_STRENGTH_GOOD;
        } else if (asu >= 5) {
            level = SIGNAL_STRENGTH_MODERATE;
        } else {
            level = SIGNAL_STRENGTH_POOR;
        }
        return level;
    }


    /**
     * @param cdmaDbm  信号强度
     * @param cdmaEcio “载干比”，它是指空中模拟电波中的信号与噪声的比值 似于信噪比
     * @return
     */
    public static int getCdmaLevel(int cdmaDbm, int cdmaEcio) {
        int levelDbm;
        int levelEcio;

        if (cdmaDbm >= -75) {
            levelDbm = SIGNAL_STRENGTH_GREAT;
        } else if (cdmaDbm >= -85) {
            levelDbm = SIGNAL_STRENGTH_GOOD;
        } else if (cdmaDbm >= -95) {
            levelDbm = SIGNAL_STRENGTH_MODERATE;
        } else if (cdmaDbm >= -100) {
            levelDbm = SIGNAL_STRENGTH_POOR;
        } else {
            levelDbm = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        }

        // Ec/Io are in dB*10
        if (cdmaEcio >= -90) {
            levelEcio = SIGNAL_STRENGTH_GREAT;
        } else if (cdmaEcio >= -110) {
            levelEcio = SIGNAL_STRENGTH_GOOD;
        } else if (cdmaEcio >= -130) {
            levelEcio = SIGNAL_STRENGTH_MODERATE;
        } else if (cdmaEcio >= -150) {
            levelEcio = SIGNAL_STRENGTH_POOR;
        } else {
            levelEcio = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        }
        int level = (levelDbm < levelEcio) ? levelDbm : levelEcio;
        return level;
    }

    /**
     * @param evdoDbm evdoDbm
     * @param evdoSnr evdoSnr 信噪比
     * @return 信号等级
     */
    public static int getEvdoLevel(int evdoDbm, int evdoSnr) {
        int levelEvdoDbm;
        int levelEvdoSnr;

        if (evdoDbm >= -65) {
            levelEvdoDbm = SIGNAL_STRENGTH_GREAT;
        } else if (evdoDbm >= -75) {
            levelEvdoDbm = SIGNAL_STRENGTH_GOOD;
        } else if (evdoDbm >= -90) {
            levelEvdoDbm = SIGNAL_STRENGTH_MODERATE;
        } else if (evdoDbm >= -105) {
            levelEvdoDbm = SIGNAL_STRENGTH_POOR;
        } else {
            levelEvdoDbm = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        }

        if (evdoSnr >= 7) {
            levelEvdoSnr = SIGNAL_STRENGTH_GREAT;
        } else if (evdoSnr >= 5) {
            levelEvdoSnr = SIGNAL_STRENGTH_GOOD;
        } else if (evdoSnr >= 3) {
            levelEvdoSnr = SIGNAL_STRENGTH_MODERATE;
        } else if (evdoSnr >= 1) {
            levelEvdoSnr = SIGNAL_STRENGTH_POOR;
        } else {
            levelEvdoSnr = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        }

        int level = (levelEvdoDbm < levelEvdoSnr) ? levelEvdoDbm : levelEvdoSnr;
        return level;
    }

}
