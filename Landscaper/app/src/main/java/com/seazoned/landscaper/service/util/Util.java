package com.seazoned.landscaper.service.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.seazoned.landscaper.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by root on 1/2/18.
 */

public class Util {
    private static NetworkInfo networkInfo;
    private static int countryCode;
    private static Context c = null;

    public static void setPadding(Context context, View view) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window window = ((Activity) context).getWindow(); // in Activity's onCreate() for instance
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(((Activity) context));
            tintManager.setStatusBarTintEnabled(true);

            tintManager.setTintColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }


        /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Window w = ((Activity)context).getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            int result = 0;
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }
            view.setPadding(0, result, 0, 0);
        }*/


    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // test for connection for WIFI
        if (networkInfo != null
                && networkInfo.isAvailable()
                && networkInfo.isConnected()) {
            return true;
        }

        networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // test for connection for Mobile
        return networkInfo != null
                && networkInfo.isAvailable()
                && networkInfo.isConnected();

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /*public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }*/

    public static JSONObject getjsonobject(String responce) {
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(responce);
        } catch (Exception e) {

        }
        return jobj;
    }

    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target.length() != 10) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    public static String changeAnyDateFormat(String reqdate, String dateformat, String reqformat) {
        //String	date1=reqdate;

        if (reqdate.equalsIgnoreCase("") || dateformat.equalsIgnoreCase("") || reqformat.equalsIgnoreCase(""))
            return "";
        SimpleDateFormat format = new SimpleDateFormat(dateformat);
        String changedate = "";
        Date dt = null;
        if (!reqdate.equals("") && !reqdate.equals("null")) {
            try {
                dt = format.parse(reqdate);
                SimpleDateFormat your_format = new SimpleDateFormat(reqformat);
                changedate = your_format.format(dt);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //SimpleDateFormat your_format = new SimpleDateFormat("dd-MMM-yyyy");

        }
        return changedate;
    }

    public static void showSnakBar(final Context context, String message) {
        //if (c!=context) {
        Snackbar.with(context) // context
                .text(message) // text to display
                .actionLabel("Try Again") // action button label
                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                .animation(true)
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        Activity a = (Activity) context;
                        Intent i = a.getIntent();
                        a.overridePendingTransition(0, 0);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        a.finish();
                        a.overridePendingTransition(0, 0);
                        a.startActivity(i);

                    }
                })
                .show((Activity) context);
        /*}
        c=context;*/

    }

    public static void showSnakBar(final Context context, String message, final View view) {
        //if (c!=context) {

        Snackbar.with(context) // context
                .text(message) // text to display
                .actionLabel("Try Again") // action button label
                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                .animation(true)
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        view.performClick();

                    }
                })
                .show((Activity) context);
        /*}
        c=context;*/
    }


    public static void openKeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void closeKeyBoard(Context context) {
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*public static void addRippleEffect(View v) {
        MaterialRippleLayout.on(v).rippleColor(Color.parseColor("#3e948888")).rippleAlpha(0.1f).rippleHover(true).create();
    }*/

    public static String encode(String value) {
        try {
            value = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static boolean isTimeAfter(Date startTime, Date endTime) {
        if (endTime.before(startTime)) { //Same way you can check with after() method also.
            return false;
        } else {
            return true;
        }
    }

    public static boolean isTimeBefore(Date startTime, Date endTime) {
        if (startTime.after(endTime)) { //Same way you can check with after() method also.
            return false;
        } else {
            return true;
        }
    }

    public static boolean nullChecker(String value) {
        return (value.equals("") || value.equals("null") || value.isEmpty() || value == null) ? true : false;
    }

    public static String getFreshValue(String value) {
        return (value.equals("") || value.equals("null") || value.isEmpty() || value == null) ? "" : value;
    }

    public static String getDecimalTwoPoint(double d) {
        String s = "" + d;
        if (s.equalsIgnoreCase("")) {
            return s;
        }
        try {
//            s=new DecimalFormat("##.##").format(d);
            NumberFormat formatter = NumberFormat.getNumberInstance();
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
            s = formatter.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }


    public static PopupWindow getPopUpWindow(Context context, String image) {
        View v = LayoutInflater.from(context).inflate(R.layout.image_view_row, null);
        ImageView imageView = v.findViewById(R.id.ivImageRow);
        ImageView ivCancel = v.findViewById(R.id.ivCancel);

        final PopupWindow popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        Picasso.with(context).load(image).into(imageView);


        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        return popupWindow;
    }

    public static String getSystemDateTime() {
        String currentDateTime = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            currentDateTime = sdf.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentDateTime;
    }
    public static String getFileName(String path) {
        String name="";
        try {
            int cut = path.lastIndexOf('/');
            if (cut != -1) {
                name = path.substring(cut + 1);
            }
        }
        catch (Exception e){
            name="";
            e.printStackTrace();
        }
        return name;
    }
    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
