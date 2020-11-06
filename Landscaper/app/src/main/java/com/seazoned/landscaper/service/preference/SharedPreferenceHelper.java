package com.seazoned.landscaper.service.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.seazoned.landscaper.model.User;


/**
 * Created by root on 3/2/18.
 */

public class SharedPreferenceHelper {
    private static String SHARE_USER_INFO = "userInfo";
    private static SharedPreferenceHelper instance = null;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static String SHARE_KEY_UID = "userId";
    private static String SHARE_KEY_UTOKEN = "userToken";
    private static String SHARE_KEY_UFASTNAME = "userFastName";
    private static String SHARE_KEY_ULASTNAME = "userLastName";
    private static String SHARE_KEY_LOGINTYPE = "userLoginType";

    public static SharedPreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceHelper();
            preferences = context.getSharedPreferences(SHARE_USER_INFO, Context.MODE_PRIVATE);
            editor = preferences.edit();
        }
        return instance;
    }

    public void saveUserInfo(User user) {
        editor.putString(SHARE_KEY_UID, user.getUserId());
        editor.putString(SHARE_KEY_UTOKEN, user.getUserToken());
        editor.putString(SHARE_KEY_UFASTNAME, user.getUserFastName());
        editor.putString(SHARE_KEY_ULASTNAME, user.getUserLastName());
        editor.putString(SHARE_KEY_LOGINTYPE, user.getUserLoginType());
        editor.apply();
    }

    public String getUserId() {
        return preferences.getString(SHARE_KEY_UID, "");
    }

    public String getUserToken() {
        return preferences.getString(SHARE_KEY_UTOKEN, "");
    }

    public String getUserFastName() {
        return preferences.getString(SHARE_KEY_UFASTNAME, "");
    }

    public String getUserLastName() {
        return preferences.getString(SHARE_KEY_ULASTNAME, "");
    }

    public String getUserLoginType() {
        return preferences.getString(SHARE_KEY_LOGINTYPE, "");
    }

    public boolean clearData() {
        editor.clear().apply();
        return true;
    }
}
