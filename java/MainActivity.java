package com.abhigam.www.foodspot;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {


    private BottomSheetBehavior mBottomSheetBehavior;
    private LinearLayout mBottomSheet, post_info, post_ad, transfer_money;

    private static final String URL = "http://13.233.234.79/user_id.php";
    private static final String NOTIFICATION_BADGE = "com.abhigam.www.foodspot.pref";
    private static final String IS_BADGE = "http://13.233.234.79/is_badge.php";
    private static final String UPDATE_BADGE = "http://13.233.234.79/update_badge.php";

    private static String username, user_id, first_name, last_name, full_name, user_branch, notification_badge;

    //prefs
    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME = "pref_username";
    private static final String PREF_PHONE_NUMBER = "pref_phone_number";
    private static final String PREF_EMAIL = "pref_email";
    private static final String PREF_BIRTHDAY = "pref_birthday";
    private static final String PREF_SEX = "pref_sex";
    private static final String PREF_BRANCH = "pref_branch";
    private static final String PREF_YEAR_ADM = "pref_year_adm";
    private static final String PREF_ABOUT_ME = "pref_about_me";
    private static final String PREF_POSTS = "pref_posts";
    private static final String PREF_CHAT_BADGE = "pref_chat_badge";

    //TAGS
    private final static String TAG_HOME = "home_tag";
    private final static String TAG_SHOP = "shop_tag";
    private final static String TAG_POST = "activity_tag";
    private final static String TAG_NOTIFY = "notify_tag";

    private String PAYU_HASH;

    private String ULTIMATE_TAG;
    private View transparent;

    //server_request
    private StringRequest request;
    private RequestQueue mRequestQueue;
    private String fcm;

    private final static String latitude_s = "latitude_coordinate";
    private final static String longitude_s = "longitude_coordinate";
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;
    private static final String URL_SEND_TOKEN = "http://13.233.234.79/update_user_id_fcm_token.php";
    private static final String URL_SEND_DUPLICATE_TOKEN = "http://13.233.234.79/fcm_token.php";
    private int id, id_for_check;
    private BottomNavigationView bottomNavigationView;
    boolean doubleBackToExitPressedOnce = false;
    BottomNavigationItemView itemViewBadge;
    View badge;
    View notif_badge_view;
    BottomNavigationMenuView menuView;
    int x = 0;

    private static final String URL_PAYU = "http://13.233.234.79/payu_hash.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        //getting username and user_id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        username = prefs.getString(PREF_USER_NAME, "");
        fcm = prefs.getString(getResources().getString(R.string.FCM_TOKEN), "");
        user_id = prefs.getString(PREF_USER_ID, "");
        full_name = prefs.getString(PREF_FULLNAME, "");
        user_branch = prefs.getString(PREF_BRANCH, "");
        notification_badge = prefs.getString(NOTIFICATION_BADGE, "");
        send_fcm_token(user_id, fcm);

        mBottomSheet = findViewById(R.id.bottom_sheet);
        post_info = findViewById(R.id.post_info);
        transfer_money = findViewById(R.id.transfer_money);

        transfer_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PayOrTransfer.class);
                startActivity(i);
            }
        });

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        transparent = findViewById(R.id.transparent);
        transparent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                transparent.setVisibility(View.GONE);
                if (id == R.id.action_post) {
                    View view = bottomNavigationView.findViewById(id_for_check);
                    view.performClick();
                }
            }
        });

        //Create Post Activity
        post_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreatePost.class);
                startActivity(i);
            }
        });

/*
        //Create Ad Activity
        post_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(getApplicationContext(), CreateAd.class);
                    startActivity(i);
                    //hiding bottomsheet onclick
                    // if(mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    //   mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    external_runtime_permissions();
                }
            }
        });
        */


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, displayMetrics);
                layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }


        if (notification_badge.equals("1")) {
            notif_badge_view = menuView.getChildAt(3);
            itemViewBadge = (BottomNavigationItemView) notif_badge_view;

            badge = LayoutInflater.from(this)
                    .inflate(R.layout.notification_badge, menuView, false);
            x = 1;

            itemViewBadge.addView(badge);
        }

        is_badge();


        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                id = item.getItemId();
                                id_for_check = item.getItemId();
                                selectedFragment = FragmentHome.newInstance();
                                ULTIMATE_TAG = TAG_HOME;
                                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    transparent.setVisibility(View.GONE);
                                }
                                break;
                            case R.id.action_post:
                                id = item.getItemId();
                                ULTIMATE_TAG = TAG_POST;
                                transparent.setVisibility(View.VISIBLE);
                                break;
                            case R.id.action_notify:
                                id = item.getItemId();
                                id_for_check = item.getItemId();
                                update_badge();
                                if (x == 1){
                                    SharedPreferences.Editor editor = PreferenceManager
                                            .getDefaultSharedPreferences(getApplicationContext()).edit();
                                    editor.putString(NOTIFICATION_BADGE, "0");
                                    editor.commit();
                                    itemViewBadge.removeView(badge);
                                }
                                selectedFragment = FragmentNotifications.newInstance();
                                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    transparent.setVisibility(View.GONE);
                                }
                                ULTIMATE_TAG = TAG_NOTIFY;
                                break;
                            case R.id.action_user:
                                id = item.getItemId();
                                ULTIMATE_TAG = "right_slide_in";
                                Intent i = new Intent(getApplicationContext(), Profile.class);
                                startActivity(i);
                                break;
                        }

                        //getting only currently visible fragment on activity main don't want to iterate overall
                        Fragment f = getSupportFragmentManager().findFragmentById(R.id.activity_main);

                        if (f.getTag().equals(ULTIMATE_TAG) && !ULTIMATE_TAG.equals(TAG_POST)) {
                            if (ULTIMATE_TAG.equals(TAG_HOME)) {
                                if(f instanceof FragmentHome)
                                    // do something with f
                                    ((FragmentHome) f).scrolltoTop();
                            }else if(ULTIMATE_TAG.equals(TAG_SHOP)){
                                if(f instanceof CronyMarket)
                                    // do something with f
                                    ((CronyMarket) f).scrolltoTop();
                            }
                            return true;
                        }
                        else if (ULTIMATE_TAG.equals(TAG_POST)) {
                            toggleBottomSheet();
                            return true;
                        } else if (ULTIMATE_TAG.equals("right_slide_in")) {
                            return true;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.activity_main, selectedFragment, ULTIMATE_TAG);
                        transaction.commit();
                        return true;
                    }
                });


        //Manually displaying the first fragment - one time only

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_main, FragmentHome.newInstance(), TAG_HOME);
        ULTIMATE_TAG = TAG_HOME;
        transaction.commit();

        id = R.id.action_home;
        id_for_check = R.id.action_home;
        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);

      /*  if(!runtime_permissions()){
            enable_buttons();
        }
        */


        //PollService.setServiceAlarm(this,false);

    }


    //for location service

    private void external_runtime_permissions() {

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
                //set to never ask again
            } else {
                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    //allowed
               //     Intent i = new Intent(getApplicationContext(), CreateAd.class);
                //    startActivity(i);
                } else {
                    //set to never ask again
                    //do something here
                }
            }

            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique
        }
    }

    public void createAlertDialog() {
        android.support.v7.app.AlertDialog.Builder builder;
        builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Permission Required")
                .setMessage(R.string.post_ad_permission)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing

                    }
                })
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(getApplicationContext(), CreateAd.class);
                startActivity(i);
            } else {
                createAlertDialog();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            transparent.setVisibility(View.GONE);
        }
        if (id == R.id.action_user || id == R.id.action_post) {
            View view = bottomNavigationView.findViewById(id_for_check);
            view.performClick();
        }
    }


    public void toggleBottomSheet() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            transparent.setVisibility(View.VISIBLE);
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            transparent.setVisibility(View.GONE);
            if (id == R.id.action_post) {
                View view = bottomNavigationView.findViewById(id_for_check);
                view.performClick();
            }
        }
    }

    public void send_fcm_token(final String user_id, final String fcm_token) {
        request = new StringRequest(Request.Method.POST, URL_SEND_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_id", user_id);
                hashMap.put("fcm_token", fcm_token);
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 5000);
    }

    private void is_badge() {

        request = new StringRequest(Request.Method.POST, IS_BADGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
                        if (x == 0) {
                            notif_badge_view = menuView.getChildAt(3);
                            itemViewBadge = (BottomNavigationItemView) notif_badge_view;

                            badge = LayoutInflater.from(MainActivity.this)
                                    .inflate(R.layout.notification_badge, menuView, false);
                            x = 1;

                            itemViewBadge.addView(badge);
                        }
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_id", user_id);
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    private void update_badge() {

        request = new StringRequest(Request.Method.POST, UPDATE_BADGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_id", user_id);
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

}



    /*

    public void send_token(final String user_id,final String fcm_token){
        request = new StringRequest(Request.Method.POST, URL_SEND_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

        mRequestQueue.add(request);
    }
}
    */
