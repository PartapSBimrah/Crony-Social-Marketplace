package com.abhigam.www.foodspot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sourabhzalke on 04/03/18.
 */

public class FragmentNotifications extends Fragment {


    private LinearLayout chatroom,ic_person_add,ic_bookmark;
    private RecyclerView mRecyclerView;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME= "pref_username";
    private static final String PREF_PHONE_NUMBER = "pref_phone_number";
    private static final String PREF_EMAIL = "pref_email";
    private static final String PREF_BIRTHDAY = "pref_birthday";
    private static final String PREF_SEX= "pref_sex";
    private static final String PREF_BRANCH = "pref_branch";
    private static final String PREF_YEAR_ADM = "pref_year_adm";
    private static final String PREF_ABOUT_ME = "pref_about_me";
    private static final String PREF_POSTS = "pref_posts";
    private String username,user_id;
    private String URL = "http://13.233.234.79/get_notifications.php";
    private AdapterNotifications mAdapterNotifications;
    private List<DataItem> data = new ArrayList<>();
    private WrapContentLinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView no_notifications,no_internet;

    private ProgressBar mProgressBar;

    private static final String REQUEST_TAG = "request_tag";


    public static FragmentNotifications newInstance(){
        FragmentNotifications fragment = new FragmentNotifications();
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getting username and user_id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        username = prefs.getString(PREF_USER_NAME,"");
        user_id = prefs.getString(PREF_USER_ID,"");

        mRequestQueue = Volley.newRequestQueue(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications,container,false);

        //images
        no_notifications = v.findViewById(R.id.no_notifications);
        no_internet = v.findViewById(R.id.no_internet);

        mProgressBar = v.findViewById(R.id.progress_bar_timeline);
        /*
        chatroom = v.findViewById(R.id.ic_chatroom);
        chatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),EnterChatRoom.class);
                getActivity().startActivity(i);
            }
        });
        */

        mRecyclerView = v.findViewById(R.id.recyclerview_notifications);
        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.clearFocus();
                mProgressBar.setVisibility(View.VISIBLE);
                data.clear();
                mRecyclerView.getRecycledViewPool().clear();
                //causing duplicate in items
                mAdapterNotifications.notifyDataSetChanged(); //important for IndexOutOfBoundException because clearing data and then instant Scrolling
                no_notifications.setVisibility(View.GONE);
                getNotificaions(0);
            }
        });

        mAdapterNotifications = new AdapterNotifications(getActivity(), data);
       final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapterNotifications);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView
                                           recyclerView, int dx, int dy) {

                boolean a = isLastItemDisplaying(mRecyclerView);
                if(a)
                {
                    if (data.size() > 0) {
                        if (data.get(data.size() - 1) != null) {
                            data.add(null);
                            Log.d("sourabh", Integer.toString(linearLayoutManager.findLastCompletelyVisibleItemPosition()));
                            mAdapterNotifications.notifyItemInserted(data.size() - 1);
                            getNotificaions(data.size());
                        }
                    }
                }
            }
        });

        return v;
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    private void getNotificaions(final int post_id){


        if(haveNetworkConnection()) {
            mStringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                    no_internet.setVisibility(View.GONE);

                    if (data.size() > 0) {
                        //remove progress item
                        if (data.get(data.size() - 1) == null) {  //condition for not removing item only remove progressBar
                            data.remove(data.size() - 1);
                            mAdapterNotifications.notifyItemRemoved(data.size());
                        }
                    }

                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jArray = jsonResponse.getJSONArray("notifications");

                            // Extract data from json and store into ArrayList as class objects
                            for (int i = 0; i < jArray.length(); i++){
                                JSONObject json_data = jArray.getJSONObject(i);
                                DataItem itemData = new DataItem();
                                itemData.image_name = json_data.getString("enrollment");
                                itemData.username = json_data.getString("first_name") + " " +
                                        json_data.getString("last_name");
                                itemData.time_ago = json_data.getString("time_ago");
                                itemData.user_id = json_data.getString(
                                        "@not_id := (SELECT id from users where enrollment =" +
                                                " i.enrollment)");
                                itemData.notification_id = json_data.getString("id");
                                itemData.first_load = "1";
                                itemData.notification_type = json_data.getString("type");
                                itemData.thing = json_data.getString("thing");
                                itemData.post_id = json_data.getString("post_id");
                                data.add(itemData);
                            }

                            mAdapterNotifications.notifyDataSetChanged();

                            if (data.size() == 0) {
                                no_notifications.setVisibility(View.VISIBLE);
                                no_internet.setVisibility(View.GONE);
                                //condition for no posts
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            // You to understand what actually error is and handle it appropriately
                            if (data.size() == 0) {
                                no_notifications.setVisibility(View.VISIBLE);
                                no_internet.setVisibility(View.GONE);
                                mProgressBar.setVisibility(View.GONE);
                            }
                        }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("user_id", user_id);
                    hashMap.put("post_id", Integer.toString(post_id));
                    return hashMap;
                }
            };

            mStringRequest.setTag(REQUEST_TAG);
            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mRequestQueue.add(mStringRequest);
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
            no_notifications.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mRequestQueue.cancelAll(REQUEST_TAG);
    }

    @Override
    public void onResume(){
        super.onResume();
        mRecyclerView.clearFocus();
        mProgressBar.setVisibility(View.VISIBLE);
        data.clear();
        mRecyclerView.getRecycledViewPool().clear();
        //causing duplicate in items
        mAdapterNotifications.notifyDataSetChanged(); //important for IndexOutOfBoundException because clearing data and then instant Scrolling
        no_notifications.setVisibility(View.GONE);
        getNotificaions(0);
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

}
