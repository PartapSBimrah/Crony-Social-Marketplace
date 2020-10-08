package com.abhigam.www.foodspot;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sourabhzalke on 09/05/18.
 */

public class CronyApp extends Application {

    private final static String URL_NO_ONLINE_BACK = "http://13.233.234.79/no_online_back.php";
    private final static String URL_GET_ONLINE = "http://13.233.234.79/get_online.php";
    private StringRequest mStringRequest;
    private static final String PREF_USER_ID = "pref_user_id";
    private RequestQueue mRequestQueue;
    private String user_id;
    private ActivityLifecycleCallbacks mActivityLifecycleCallbacks;
    private  int numStarted=0;

    @Override
    public void onCreate() {
        super.onCreate();
        //getting username and user_id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        user_id = prefs.getString(PREF_USER_ID, "");
        //Parse SDK stuff goes here
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(getBaseContext());
        mActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if(numStarted == 0) {
                    // app went to foreground
                    get_online();
                }
                numStarted++;

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                numStarted--;
                if (numStarted == 0) {
                    // app went to background
                    get_offline();
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        };
    }




    private void get_offline(){

        mStringRequest = new StringRequest(Request.Method.POST, URL_NO_ONLINE_BACK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_id", user_id);   //making user online
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                return hashMap;

            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void get_online(){

        mStringRequest = new StringRequest(Request.Method.POST, URL_GET_ONLINE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_id", user_id);   //making user online
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                return hashMap;

            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

}
