package com.seazoned.landscaper.app;

import android.graphics.Bitmap;
import android.net.Uri;

import com.seazoned.landscaper.model.User;

/**
 * Created by root on 3/2/18.
 */

public class AppData {
    public static String sToken="";
    public static String sUserId="";
    public static User user=null;

    public static String sBookingId="";
    public static Bitmap sCompleteImage=null;
    public static double lat=0.0;
    public static double lang=0.0;
    public static String address="";
    public static String type="";
    public static String sServiceHistoryFlag="";
    public static String sAddress="";
    public static double sSearchLatitiude=0.0;
    public static double sSearchLongitiude=0.0;
    public static int sCompletejobFlag=0;
    public static String sTaxRate="20";
    public static Uri selectedFileUri=null;
}
