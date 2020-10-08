package com.abhigam.www.foodspot;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sourabhzalke on 07/03/18.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final  String REG_TOKEN = "REG_TOKEN";
    private static final String URL_SEND_TOKEN = "http://13.233.234.79/fcm_token.php";
    private final static String PREF_USER_ID = "user_id";
    private StringRequest mStringRequest;
    private RequestQueue mRequestQueue;
    private String user_id;
    String recent_token;

    @Override
    public void onCreate(){
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        SharedPreferences prefs = SaveSharedPreference.getSharedPreferences(getApplicationContext());
        user_id = prefs.getString(PREF_USER_ID,"");
    }

    @Override
    public void onTokenRefresh() {

        recent_token = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString(getString(R.string.FCM_TOKEN),recent_token);
        editor.commit();

        send_token(user_id,recent_token);

        Log.d(REG_TOKEN,recent_token);

    }

    public void send_token(final String user_id,final String fcm_token){
        mStringRequest = new StringRequest(Request.Method.POST, URL_SEND_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                 onTokenRefresh();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("fcm_token",fcm_token);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }
}
