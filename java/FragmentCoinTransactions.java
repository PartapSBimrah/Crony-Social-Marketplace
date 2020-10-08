package com.abhigam.www.foodspot;

import android.content.Context;
import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sourabhzalke on 02/04/18.
 */

public class FragmentCoinTransactions extends Fragment {

    private String user_id,user,user_fullname,username;

    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME= "pref_username";
    private static final String URL = "http://13.233.234.79/get_transactions_un.php";

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    private AdapterCoinTransactions mAdapterCoinTransactions;
    private List<DataRecycler> data = new ArrayList<>();
    private WrapContentLinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView no_internet,no_posts;

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private NestedScrollView mNestedScrollView;
    private LinearLayout back;
    private String no_coins_string;
    private TextView wallet_balance;

    private static final String REQUEST_TAG = "request_tag";
    private String URL_USER_DETAILS = "http://13.233.234.79/user_id.php";



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        user_id = prefs.getString(PREF_USER_ID,"");
        username = prefs.getString(PREF_USER_NAME,"");
        user_fullname = prefs.getString(PREF_FULLNAME,"");
        mRequestQueue = Volley.newRequestQueue(getActivity());

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_coin_transactions,container,false);

        mToolbar = v.findViewById(R.id.toolbar_top);

        no_internet = v.findViewById(R.id.no_internet);
        no_posts = v.findViewById(R.id.no_posts);
        mRecyclerView = v.findViewById(R.id.recyclerview_transactions);
        mRecyclerView.setNestedScrollingEnabled(false);
        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        mProgressBar = v.findViewById(R.id.progressBar);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.clearFocus();
                mProgressBar.setVisibility(View.VISIBLE);
                data.clear();
                mRecyclerView.getRecycledViewPool().clear();
                no_internet.setVisibility(View.GONE);
                no_posts.setVisibility(View.GONE);
                getTransactions(0);
                getUserIDFirst(username);
            }
        });
        back = v.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        wallet_balance = v.findViewById(R.id.wallet);

        Window window = getActivity().getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.my_wallet));
        }

        mAdapterCoinTransactions = new AdapterCoinTransactions(getActivity(), data);
        linearLayoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapterCoinTransactions);

        mNestedScrollView = v.findViewById(R.id.nestedScrollview);

        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //bahar hi rakhna

                if (scrollY > oldScrollY){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        mToolbar.setElevation((float)8.0);
                    }
                }else if(scrollY == 0){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        mToolbar.setElevation((float)0);
                    }
                }
                if(v.getChildAt(v.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                            scrollY > oldScrollY) {

                        int visibleItemCount;
                        int totalItemCount;
                        int pastVisiblesItems;
                        visibleItemCount = linearLayoutManager.getChildCount();
                        totalItemCount = linearLayoutManager.getItemCount();
                        pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();


                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {

                            if (data.size() > 0) {
                                if (data.get(data.size() - 1) != null) {
                                    data.add(null);
                                    Log.d("sourabh", Integer.toString(linearLayoutManager.findLastCompletelyVisibleItemPosition()));
                                    mAdapterCoinTransactions.notifyItemInserted(data.size() - 1);
                                    getTransactions(data.size());
                                }
                            }
//                        Load Your Data
                        }
                    }
                }
            }
        });

        no_internet.setVisibility(View.GONE);
        no_posts.setVisibility(View.GONE);
        getTransactions(0);
        getUserIDFirst(username);

        return v;
    }

    private void getTransactions(final int id){

        if(haveNetworkConnection()) {
            mStringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    //this method will be running on UI thread
                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                    no_internet.setVisibility(View.GONE);
                    no_posts.setVisibility(View.GONE);

                    if (data.size() > 0) {
                        //remove progress item
                        if (data.get(data.size() - 1) == null) {  //condition for not removing item only remove progressBar
                            data.remove(data.size() - 1);
                            mAdapterCoinTransactions.notifyItemRemoved(data.size());
                        }
                    }

                    if (response.equals("no rows") && data.size()==0){
                        no_internet.setVisibility(View.GONE);
                        no_posts.setVisibility(View.VISIBLE);
                    } else {

                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jArray = jsonResponse.getJSONArray("transactions");

                            // Extract data from json and store into ArrayList as class objects
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject json_data = jArray.getJSONObject(i);
                                DataRecycler itemData = new DataRecycler();
                                itemData.enrollment = json_data.getString("enrollment");
                                itemData.time_ago = json_data.getString("time_ago");
                                itemData.type = json_data.getString("type");
                                itemData.user_id =json_data.getString("user_id");
                                itemData.sender_user_id = json_data.getString("sender_user_id");
                                itemData.store_name = json_data.getString("store_name");
                                itemData.txnId = json_data.getString("txnId");
                                if(itemData.store_name.equals("Sign Up Bonus") || itemData.store_name.equals("Rewards")){
                                    itemData.fullname = itemData.store_name;
                                }
                                else if(!itemData.store_name.equals("x")){
                                    itemData.fullname = "To "+itemData.store_name;
                                }
                                else if (itemData.enrollment.equals(username) && itemData.type.equals("1")) {
                                    itemData.fullname = "Added to Wallet";
                                }else if (itemData.enrollment.equals(username) && itemData.type.equals("2")) {
                                    itemData.fullname = "To Merchant";
                                }else{
                                    itemData.fullname = json_data.getString("first_name")
                                            + " " + json_data.getString("last_name");
                                }
                                itemData.amount = json_data.getString("amount");
                                data.add(itemData);
                            }

                            mAdapterCoinTransactions.notifyDataSetChanged();



                        } catch (JSONException e) {
                            e.printStackTrace();
                            // You to understand what actually error is and handle it appropriately
                            if (data.size() == 0) {
                                no_posts.setVisibility(View.VISIBLE);
                                no_internet.setVisibility(View.GONE);
                                mSwipeRefreshLayout.setRefreshing(false);
                                mProgressBar.setVisibility(View.GONE);
                            }
                            if (data.size() > 0){
                                if (data.get(data.size() - 1) == null){ //condition for not removing item only remove progressBar
                                    //remove progress item
                                    data.remove(data.size() - 1);
                                    mAdapterCoinTransactions.notifyItemRemoved(data.size());
                                }
                            }
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("user_id", user_id);
                    hashMap.put("post_id", Integer.toString(id));
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
            no_posts.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
            wallet_balance.setText("0");
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mRequestQueue.cancelAll(REQUEST_TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll(REQUEST_TAG);
    }

    @Override
    public void onResume(){
        super.onResume();
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

    private void getUserIDFirst(final String enrollment){

        mStringRequest = new StringRequest(Request.Method.POST, URL_USER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            no_coins_string = jsonObject.getString("coins");
                            wallet_balance.setText("₹"+no_coins_string);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("enrollment",enrollment);
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
