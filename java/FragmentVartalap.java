package com.abhigam.www.foodspot;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sourabhzalke on 21/06/18.
 */

public class FragmentVartalap extends Fragment {


    private ImageView back;

    private List<DataItem> data1 = new ArrayList<>();
    private List<DataItem> data2 = new ArrayList<>();
    private List<DataRecycler> live_data = new ArrayList<>();
    private LinearLayoutManager liveLinearLayoutManager;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    //prefs
    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME= "pref_username";
    private static final String PREF_PHONE_NUMBER = "pref_phone_number";
    private static final String UPDATE_BADGE = "http://13.233.234.79/update_chat_badge.php";

    private RecyclerView mSuggestRecyclerview,mRecyclerView;
    private AdapterSuggestInbox mAdapterSuggestInbox;
    private AdapterChatInbox mAdapterChatInbox;

    private String username,user_id;
    private NestedScrollView mNestedScrollView;
    private ProgressBar mProgressBar;
    private TextView connections,suggested;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView no_internet;

    private String URL = "http://13.233.234.79/suggested_friends.php";
    private String URL_CONNECTIONS = "http://13.233.234.79/get_connections.php";
    private String URL_LIVE_NOW = "http://13.233.234.79/get_live_now.php";

    private static final String REQUEST_TAG = "request_tag1";

    private static RecyclerView mLiveRecyclerView;
    private static AdapterLiveNow mAdapterLiveNow;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(getActivity());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        username = prefs.getString(PREF_USER_NAME,"");
        user_id = prefs.getString(PREF_USER_ID,"");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vartalap,container,false);

        Window window = getActivity().getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        }

        mNestedScrollView = v.findViewById(R.id.nestedScrollview);
        mProgressBar = v.findViewById(R.id.progressBar1);
        connections = v.findViewById(R.id.text_con);
        suggested = v.findViewById(R.id.text_suggest);
        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        no_internet = v.findViewById(R.id.no_internet);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(haveNetworkConnection()) {
                    mRequestQueue.cancelAll(REQUEST_TAG);
                    mRecyclerView.clearFocus();
                    mSuggestRecyclerview.clearFocus();
                    connections.setVisibility(View.GONE);
                    suggested.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    //Getting user feed
                    data1.clear();
                    mRecyclerView.getRecycledViewPool().clear();
                    data2.clear();
                    mSuggestRecyclerview.getRecycledViewPool().clear();
                    mAdapterChatInbox.notifyDataSetChanged();
                    mAdapterSuggestInbox.notifyDataSetChanged();
                    live_data.clear();
                    mLiveRecyclerView.clearFocus();
                    mLiveRecyclerView.getRecycledViewPool().clear();
                    mAdapterChatInbox.notifyDataSetChanged();
                    //getLiveNow();
                    getConnections();
                    suggested_users();
                }else{
                    mSwipeRefreshLayout.setRefreshing(false);
                    no_internet.setVisibility(View.VISIBLE);
                }
            }
        });

        back = v.findViewById(R.id.back_arrow_image);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        //first
        mRecyclerView = v.findViewById(R.id.recycler_chats);
        mAdapterChatInbox = new AdapterChatInbox(getActivity(),data1);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager1);
        mRecyclerView.setAdapter(mAdapterChatInbox);
        mRecyclerView.setNestedScrollingEnabled(false);

        //second
        mSuggestRecyclerview = v.findViewById(R.id.recycler_suggested);
        mAdapterSuggestInbox = new AdapterSuggestInbox(getActivity(),data2);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        mSuggestRecyclerview.setLayoutManager(linearLayoutManager2);
        mSuggestRecyclerview.setAdapter(mAdapterSuggestInbox);
        mSuggestRecyclerview.setNestedScrollingEnabled(false);

        mLiveRecyclerView = v.findViewById(R.id.live_now);

        //ALL of Recyclerview
        mLiveRecyclerView.setNestedScrollingEnabled(false);
        mAdapterLiveNow = new AdapterLiveNow(getActivity(),live_data);
        liveLinearLayoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        mLiveRecyclerView.setLayoutManager(liveLinearLayoutManager);
        mLiveRecyclerView.setAdapter(mAdapterLiveNow);

        update_badge();

        return v;
    }

    private void suggested_users(){

        mStringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

               try{

                   mProgressBar.setVisibility(View.GONE);
                   suggested.setVisibility(View.VISIBLE);
                   mSwipeRefreshLayout.setRefreshing(false);

                   JSONObject jsonObject = new JSONObject(response);

                   JSONArray jsonArray = jsonObject.getJSONArray("users");

                   for(int i=0;i<jsonArray.length();++i){
                       JSONObject json_data = jsonArray.getJSONObject(i);
                       DataItem itemData = new DataItem();
                       itemData.username = json_data.getString("enrollment");
                       if(!json_data.getString("first_name").equals("null") && !json_data.getString("last_name").equals("null")) {
                           itemData.full_name = json_data.getString("first_name") + " " + json_data.getString("last_name");
                       }else
                           itemData.full_name = "0";
                       itemData.user_id = json_data.getString("id");
                       itemData.profilepicName = json_data.getString("profilepicname");
                       itemData.verified = json_data.getString("verified");
                       itemData.private_ac = json_data.getString("private_ac");
                       if(!json_data.getString("enrollment").equals(username))
                           data2.add(itemData);
                   }

                   if(data2.size()==0){
                       suggested.setVisibility(View.GONE);
                   }

                    mAdapterSuggestInbox.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                   mProgressBar.setVisibility(View.GONE);
                   mSwipeRefreshLayout.setRefreshing(false);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                return hashMap;
            }
        };
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mStringRequest.setTag(REQUEST_TAG);
        mRequestQueue.add(mStringRequest);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(haveNetworkConnection()) {
            mRequestQueue.cancelAll(REQUEST_TAG);
            mRecyclerView.clearFocus();
            mSuggestRecyclerview.clearFocus();
            connections.setVisibility(View.GONE);
            suggested.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            //Getting user feed
            data1.clear();
            mRecyclerView.getRecycledViewPool().clear();
            data2.clear();
            mLiveRecyclerView.clearFocus();
            live_data.clear();
            mLiveRecyclerView.getRecycledViewPool().clear();
            mAdapterChatInbox.notifyDataSetChanged();
            mSuggestRecyclerview.getRecycledViewPool().clear();

            getConnections();
        //    getLiveNow();
            suggested_users();
        }else{
            no_internet.setVisibility(View.VISIBLE);
        }
    }

    private void getConnections(){

        mStringRequest = new StringRequest(Request.Method.POST, URL_CONNECTIONS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                connections.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); ++i) {
                                JSONObject json_data = jsonArray.getJSONObject(i);
                                DataItem itemData = new DataItem();
                                itemData.username = json_data.getString("enrollment");
                                if(!json_data.getString("first_name").equals("null") && !json_data.getString("last_name").equals("null")) {
                                    itemData.full_name = json_data.getString("first_name") + " " + json_data.getString("last_name");
                                }else
                                    itemData.full_name = "0";
                                itemData.user_id = json_data.getString("id");
                                itemData.profilepicName = json_data.getString("profilepicname");
                                itemData.verified = json_data.getString("verified");
                                itemData.private_ac = json_data.getString("private_ac");
                                itemData.highlight = json_data.getString("highlight");
                                if(!json_data.getString("msg").equals("null")
                                        && !json_data.getString("time").equals("null")) {
                                    itemData.msg = json_data.getString("msg");
                                    Long timeNow = new Date().getTime();
                                    String time = convertLongDateToAgoString(
                                            Long.parseLong(json_data.getString("time")),timeNow);
                                    itemData.time_ago = time;
                                }else{
                                    itemData.msg = "Start Conversation";
                                    itemData.time_ago = "";
                                }
                                if(!json_data.getString("enrollment").equals(username))
                                    data1.add(itemData);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mSwipeRefreshLayout.setRefreshing(false);
                            mProgressBar.setVisibility(View.GONE);
                        }

                if(data1.size()==0){
                    connections.setVisibility(View.GONE);
                }


                mAdapterChatInbox.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mStringRequest.setTag(REQUEST_TAG);
        mRequestQueue.add(mStringRequest);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("sourabh","onPause()");
        mRequestQueue.cancelAll(REQUEST_TAG);
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static String convertLongDateToAgoString (Long createdDate, Long timeNow){
        Long timeElapsed = timeNow - createdDate;

        // For logging in Android for testing purposes
        /*
        Date dateCreatedFriendly = new Date(createdDate);
        Log.d("MicroR", "dateCreatedFriendly: " + dateCreatedFriendly.toString());
        Log.d("MicroR", "timeNow: " + timeNow.toString());
        Log.d("MicroR", "timeElapsed: " + timeElapsed.toString());*/

        // Lengths of respective time durations in Long format.
        Long oneMin = 60000L;
        Long oneHour = 3600000L;
        Long oneDay = 86400000L;
        Long oneWeek = 604800000L;

        String finalString = "0sec";
        String unit;

        if (timeElapsed < oneMin){
            // Convert milliseconds to seconds.
            double seconds = (double) ((timeElapsed / 1000));
            // Round up
            seconds = Math.round(seconds);
            // Generate the friendly unit of the ago time
            if (seconds == 1) {
                unit = "s";
            } else {
                unit = "s";
            }
            finalString = String.format("%.0f", seconds) + unit;
        } else if (timeElapsed < oneHour) {
            double minutes = (double) ((timeElapsed / 1000) / 60);
            minutes = Math.round(minutes);
            if (minutes == 1) {
                unit = "m";
            } else {
                unit = "m";
            }
            finalString = String.format("%.0f", minutes) + unit;
        } else if (timeElapsed < oneDay) {
            double hours   = (double) ((timeElapsed / 1000) / 60 / 60);
            hours = Math.round(hours);
            if (hours == 1) {
                unit = "h";
            } else {
                unit = "h";
            }
            finalString = String.format("%.0f", hours) + unit;
        } else if (timeElapsed < oneWeek) {
            double days   = (double) ((timeElapsed / 1000) / 60 / 60 / 24);
            days = Math.round(days);
            if (days == 1) {
                unit = "d";
            } else {
                unit = "d";
            }
            finalString = String.format("%.0f", days) + unit;
        } else if (timeElapsed > oneWeek) {
            double weeks = (double) ((timeElapsed / 1000) / 60 / 60 / 24 / 7);
            weeks = Math.round(weeks);
            if (weeks == 1) {
                unit = "w";
            } else {
                unit = "w";
            }
            finalString = String.format("%.0f", weeks) + unit;
        }
        return finalString;
    }

    private void update_badge() {

        mStringRequest = new StringRequest(Request.Method.POST, UPDATE_BADGE, new Response.Listener<String>() {
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

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void getLiveNow(){

        mStringRequest = new StringRequest(Request.Method.POST, URL_LIVE_NOW, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json_data = jsonArray.getJSONObject(i);
                        DataRecycler itemData = new DataRecycler();
                        itemData.enrollment = json_data.getString("enrollment");
                        itemData.first_name = json_data.getString("first_name");
                        itemData.fullname = json_data.getString("first_name")+" "+
                                json_data.getString("last_name");
                        itemData.user_id = json_data.getString("id");
                        itemData.chat = json_data.getString("num_connect");
                        if(!json_data.getString("enrollment").equals(username))
                            live_data.add(itemData);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

                mAdapterLiveNow.notifyDataSetChanged();

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
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mStringRequest.setTag(REQUEST_TAG);
        mRequestQueue.add(mStringRequest);

    }


}
