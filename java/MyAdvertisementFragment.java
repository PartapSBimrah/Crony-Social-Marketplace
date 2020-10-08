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
import java.util.jar.JarEntry;

/**
 * Created by sourabhzalke on 02/04/18.
 */

public class MyAdvertisementFragment extends Fragment {

    private String user_id,user,user_fullname,username;

    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME= "pref_username";
    private static final String URL = "http://13.233.234.79/get_advertisements.php";

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    private AdapterMyAdvertisement mAdapterMyAdvertisement;
    private List<DataRecycler> data = new ArrayList<>();
    private WrapContentLinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private ImageView no_posts,back,no_internet;
    private Toolbar mToolbar;
    private NestedScrollView mNestedScrollView;
    private TextView number_ad;
    private Button locate;
    private LinearLayout help;

    private static final String REQUEST_TAG = "request_tag";



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
        View v = inflater.inflate(R.layout.fragment_my_advertisement,container,false);

        mToolbar = v.findViewById(R.id.toolbar_top);

        no_posts = v.findViewById(R.id.no_posts);
        no_internet = v.findViewById(R.id.no_internet);
        number_ad = v.findViewById(R.id.number_ad);

        mRecyclerView = v.findViewById(R.id.recyclerview_advertisement);
        mRecyclerView.setNestedScrollingEnabled(false);
        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        mProgressBar = v.findViewById(R.id.progressBar);
        back = v.findViewById(R.id.back_arrow_image);
        mNestedScrollView = v.findViewById(R.id.nestedScrollview);
        locate = v.findViewById(R.id.locate);

        Window window = getActivity().getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.my_advertise));
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                no_internet.setVisibility(View.GONE);
                onResume();
            }
        });

        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //bahar hi rakhna
                if (scrollY > oldScrollY) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mToolbar.setElevation((float)8.0);
                    }
                }else if(scrollY == 0){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mToolbar.setElevation((float)0);
                    }
                }
            }
        });

        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),LocationWareHouse.class);
                getActivity().startActivity(i);
            }
        });

        help = v.findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),ContactDetails.class);
                getActivity().startActivity(i);
            }
        });

        mAdapterMyAdvertisement = new AdapterMyAdvertisement(getActivity(),data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapterMyAdvertisement);

        return v;
    }

    private void getAdvertisement(final int id){

        if(haveNetworkConnection()) {
            mStringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                    no_internet.setVisibility(View.GONE);
                    no_posts.setVisibility(View.GONE);


                    if (response.equals("\t\nno rows")) {
                        no_posts.setVisibility(View.VISIBLE);
                        no_internet.setVisibility(View.GONE);
                        number_ad.setText("0");
                    }else {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); ++i) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                DataRecycler item_data = new DataRecycler();
                                String image = jsonObject.getString("user_id") + "_" + jsonObject.getString("title") +
                                        "_" + jsonObject.getString("description").substring(0, 10);
                                item_data.image_name = image;
                                item_data.user_id = jsonObject.getString("user_id");
                                item_data.product_id = jsonObject.getString("id");
                                item_data.title = jsonObject.getString("title");
                                item_data.description = jsonObject.getString("description");
                                item_data.amount = jsonObject.getString("expected_price");
                                item_data.category = jsonObject.getString("category");
                                item_data.time_ago = jsonObject.getString("time_ago");
                                item_data.token = jsonObject.getString("token");
                                item_data.status = jsonObject.getString("status");
                                item_data.seller = jsonObject.getString("seller");
                                item_data.is_in_wishlist = jsonObject.getString("is_wishlist");
                                item_data.product_image_count = jsonObject.getString("image_count");
                                data.add(item_data);
                            }

                            number_ad.setText(Integer.toString(data.size()));

                            mAdapterMyAdvertisement.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            mProgressBar.setVisibility(View.GONE);
                            number_ad.setText("0");
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    no_internet.setVisibility(View.VISIBLE);
                    no_posts.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("user_id", user_id);
                    hashMap.put("post_id", "0");
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
            no_posts.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
            number_ad.setText("0");
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
        no_posts.setVisibility(View.GONE);
        no_internet.setVisibility(View.GONE);
        data.clear();
        mRecyclerView.getRecycledViewPool().clear();
        mAdapterMyAdvertisement.notifyDataSetChanged();
        mRequestQueue.cancelAll(REQUEST_TAG);
        mProgressBar.setVisibility(View.GONE);
        getAdvertisement(0);
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
