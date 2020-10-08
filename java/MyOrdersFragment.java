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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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

public class MyOrdersFragment extends Fragment {

    private String user_id,user,user_fullname,username;

    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME= "pref_username";
    private static final String URL = "http://13.233.234.79/get_my_orders.php";

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    private ImageView no_orders,no_internet;
    private AdapterOrders mAdapterOrders;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private NestedScrollView mNestedScrollView;
    private Button locate_warehouse;

    private static final String REQUEST_TAG = "request_tag";
    private Toolbar mToolbar;
    private TextView number_orders;
    private LinearLayout help;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        View v = inflater.inflate(R.layout.fragment_my_orders,container,false);

        mToolbar = v.findViewById(R.id.toolbar_top);
        if (mToolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Window window = getActivity().getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.my_orders));
        }

        no_orders = v.findViewById(R.id.no_orders);
        no_internet = v.findViewById(R.id.no_internet);
        number_orders = v.findViewById(R.id.number_orders);
        help = v.findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),ContactDetails.class);
                getActivity().startActivity(i);
            }
        });

        locate_warehouse = v.findViewById(R.id.locate);
        locate_warehouse.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),LocationWareHouse.class);
                getActivity().startActivity(i);
            }
        });

        mRecyclerView = v.findViewById(R.id.recyclerview_my_orders);
        mRecyclerView.setNestedScrollingEnabled(false);

        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        mProgressBar = v.findViewById(R.id.progressBar);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                no_orders.setVisibility(View.GONE);
                no_internet.setVisibility(View.GONE);
                getOrders(0);
            }
        });
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
            }
        });

        return v;
    }

    private void getOrders(final int id){

        if(haveNetworkConnection()) {
            mStringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    //this method will be running on UI thread
                    List<DataRecycler> data = new ArrayList<>();
                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);

                    if (response.equals("no rows")) {
                        no_orders.setVisibility(View.VISIBLE);
                        no_internet.setVisibility(View.GONE);
                        number_orders.setText("0");
                    } else {

                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jArray = jsonResponse.getJSONArray("my_orders");

                            // Extract data from json and store into ArrayList as class objects
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject json_data = jArray.getJSONObject(i);
                                DataRecycler itemData = new DataRecycler();
                                itemData.product_id = json_data.getString("item_id");
                                itemData.title = json_data.getString("title");
                                itemData.description = json_data.getString("description");
                                itemData.time_ago = json_data.getString("order_time");
                                itemData.status = json_data.getString("order_status");
                                String title = json_data.getString("title");
                                String description = json_data.getString("description");
                                String image = json_data.getString("user_id") + "_" +
                                        title +
                                        "_" + description.substring(0, 10);
                                itemData.image_name = image;
                                itemData.price = json_data.getString("expected_price");
                                itemData.category = json_data.getString("category");
                                itemData.product_image_count = json_data.getString("image_count");
                                itemData.seller = json_data.getString("seller");
                                itemData.is_in_wishlist = json_data.getString("is_wishlist");
                                itemData.verified = json_data.getString("verified");
                                itemData.token = json_data.getString("order_token");
                                data.add(itemData);
                            }

                            number_orders.setText(Integer.toString(data.size()));


                            mAdapterOrders = new AdapterOrders(getActivity(), data);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(mAdapterOrders);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            // You to understand what actually error is and handle it appropriately
                            if (data.size() == 0) {
                                no_orders.setVisibility(View.VISIBLE);
                                no_internet.setVisibility(View.GONE);
                                mProgressBar.setVisibility(View.GONE);
                                number_orders.setText("0");
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

            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mStringRequest.setTag(REQUEST_TAG);
            mRequestQueue.add(mStringRequest);
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
            no_orders.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
            number_orders.setText("0");
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
        mProgressBar.setVisibility(View.VISIBLE);
        no_internet.setVisibility(View.GONE);
        no_orders.setVisibility(View.GONE);
        getOrders(0);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
