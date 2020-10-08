package com.abhigam.www.foodspot;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sourabhzalke on 26/03/17.
 */

public class SaveSharedPreference
{

    //Tags
    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME= "pref_username";
    private static final String PREF_PHONE_NUMBER = "pref_phone_number";
    private static final String PREF_EMAIL = "pref_email";
    private static final String PREF_BIRTHDAY = "pref_birthday";
    private static final String PREF_SEX= "pref_sex";
    private static final String PREF_BRANCH = "pref_branch";
    private static final String PREF_YEAR_ADM = "pref_full_name";
    private static final String PREF_BIO = "pref_bio";
    private static final String PREF_VERIFY_REQUESTED = "pref_verify_request";
    private static final String PREF_VERIFIED = "pref_verified";

    public static SharedPreferences getSharedPreferences(Context ctx) {
            return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.apply();
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static void clearUserName(Context ctx){
        SharedPreferences preferences = getSharedPreferences(ctx);
        preferences.edit().remove(PREF_USER_NAME).apply();
        preferences.edit().remove(PREF_FULLNAME).apply();
        preferences.edit().remove(PREF_USER_ID).apply();
        preferences.edit().remove(PREF_PHONE_NUMBER).apply();
        preferences.edit().remove(PREF_EMAIL).apply();
        preferences.edit().remove(PREF_BIRTHDAY).apply();
        preferences.edit().remove(PREF_SEX).apply();
        preferences.edit().remove(PREF_BRANCH).apply();
        preferences.edit().remove(PREF_YEAR_ADM).apply();
        preferences.edit().remove(PREF_BIO).apply();
        preferences.edit().remove(PREF_VERIFIED).apply();
        preferences.edit().remove(PREF_VERIFY_REQUESTED).apply();

    }
}
