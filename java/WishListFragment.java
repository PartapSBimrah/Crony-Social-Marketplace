package com.abhigam.www.foodspot;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class WishListFragment extends Fragment implements InterfaceForAdapter{

    //prefs
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

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private WishListAdapter gridViewAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    ExpandableHeightGridView gridView;
    private List<DataRecycler> data = new ArrayList<>();
    private String URL_WISHLIST_DATA = "http://13.233.234.79/wishlist_data.php";
    private String user_id;
    private TextView num_items;
    int x=0;
    private String server_request_tag = "server_request_tag_suggest_friends";
    private LinearLayout back;
    private NestedScrollView mNestedScrollView;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private ImageView no_posts,no_internet;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        SharedPreferences prefs = SaveSharedPreference.getSharedPreferences(getActivity().getApplicationContext());
        user_id = prefs.getString(PREF_USER_ID,"");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wish_list,container,false);

        gridView = v.findViewById(R.id.gridView);

        Window window = getActivity().getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.my_wishlist));
        }

        mNestedScrollView = v.findViewById(R.id.nestedScrollview);
        mProgressBar = v.findViewById(R.id.progressBar);

        mToolbar = v.findViewById(R.id.toolbar_top);
        back = v.findViewById(R.id.back);
        num_items = v.findViewById(R.id.num_items);
        no_posts = v.findViewById(R.id.no_posts);
        no_internet = v.findViewById(R.id.no_internet);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
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

        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onPause();
                onResume();
            }
        });
        //All of Crony Market Adapter
        //Creating GridViewAdapter Object
        gridViewAdapter = new WishListAdapter(getActivity(),data);

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        gridView.setAdapter(gridViewAdapter);
        gridView.setExpanded(true);
        gridView.setFocusable(false);
        mRequestQueue.cancelAll(server_request_tag);
        mProgressBar.setVisibility(View.VISIBLE);
        getWishlistData();

    }

    @Override
    public void onPause() {
        super.onPause();
        mRequestQueue.cancelAll(server_request_tag);
        data.clear();
        gridView.setAdapter(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll(server_request_tag);
    }

    private void getWishlistData(){

        if(haveNetworkConnection()) {
            mStringRequest = new StringRequest(Request.Method.POST, URL_WISHLIST_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    mProgressBar.setVisibility(View.GONE);
                    no_internet.setVisibility(View.GONE);
                    no_posts.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);

                    if (response.equals("no rows")) {
                        no_posts.setVisibility(View.VISIBLE);
                        num_items.setText("0");
                    } else {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            mSwipeRefreshLayout.setRefreshing(false);

                            JSONArray jsonArray = jsonResponse.getJSONArray("items");

                            for (int i = 0; i < jsonArray.length(); ++i) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                DataRecycler item_data = new DataRecycler();
                                String title = jsonObject.getString("title");
                                String description = jsonObject.getString("description");
                                String image = jsonObject.getString("user_id") + "_" +
                                        title +
                                        "_" + description.substring(0, 10);
                                item_data.image_name = image;
                                item_data.user_id = jsonObject.getString("user_id");
                                item_data.product_id = jsonObject.getString("id");
                                item_data.title = jsonObject.getString("title");
                                item_data.description = jsonObject.getString("description");
                                item_data.price = jsonObject.getString("expected_price");
                                item_data.category = jsonObject.getString("category");
                                item_data.product_image_count = jsonObject.getString("image_count");
                                item_data.seller = jsonObject.getString("seller");
                                if (jsonObject.getString("verified").equals("1"))
                                    data.add(item_data);
                            }

                            gridViewAdapter.notifyDataSetChanged();
                            num_items.setText(Integer.toString(gridViewAdapter.getCount()));
                            x = gridViewAdapter.getCount();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    no_internet.setVisibility(View.VISIBLE);
                    no_posts.setVisibility(View.GONE);
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
            mStringRequest.setTag(server_request_tag);
            mRequestQueue.add(mStringRequest);
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
            no_posts.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
            num_items.setText("0");
        }
    }

    @Override
    public void onRemoved(){
        num_items.setText(Integer.toString(gridViewAdapter.getCount()));
        if(gridViewAdapter.getCount()<=0){
            no_posts.setVisibility(View.VISIBLE);
        }
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
