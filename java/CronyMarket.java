package com.abhigam.www.foodspot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sourabhzalke on 08/06/17.
 */

public class CronyMarket extends Fragment {


    private List<DataRecycler> data = new ArrayList<>();
    private List<DataRecycler> data_filter_category = new ArrayList<>();
    private List<DataRecycler> data_filter_sort = new ArrayList<>();

    private static final String URL_CRONY_MARKET = "http://13.233.234.79/crony_market_get_data.php";

    ExpandableHeightGridView gridView;
    private NestedScrollView mNestedScrollView;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private CronyMarketAdapter gridViewAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
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
    private String user_id;
    private ImageView no_internet;
    private LinearLayout ic_chatroom,ic_person_add,ic_bookmark;

    //lock
    private String loading="0";

    private int counter = 0;

    private ProgressBar mProgressBar,mGridLoader;
    private LinearLayout category,sort_by;
    private TextView category_text,sort_text;
    private ImageView cancel_1,cancel_2,category_icon,sort_by_icon;
    private Toolbar mToolbar;
    private ImageView no_posts;

    private static final String REQUEST_TAG = "request_tag";

    private Boolean canScroll = false;


    public static CronyMarket newInstance(){
        CronyMarket fragment = new CronyMarket();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mRequestQueue = Volley.newRequestQueue(getActivity());
        //getting username and user_id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        user_id = prefs.getString(PREF_USER_ID,"");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_crony_market, container, false);

        gridView = v.findViewById(R.id.gridView);
        category_text = v.findViewById(R.id.category_text);
        sort_text = v.findViewById(R.id.sort_text);
        cancel_1 =  v.findViewById(R.id.cancel_1);
        cancel_2 = v.findViewById(R.id.cancel_2);
        category_icon = v.findViewById(R.id.category_icon);
        sort_by_icon = v.findViewById(R.id.sort_by_icon);
        mNestedScrollView = v.findViewById(R.id.scroll_view);
        mNestedScrollView.setNestedScrollingEnabled(false);
        mToolbar = v.findViewById(R.id.toolbar_top);
        mGridLoader = v.findViewById(R.id.gridLoader);
        no_posts = v.findViewById(R.id.no_posts);

        /*
        //toolbar bar icons
        ic_chatroom = v.findViewById(R.id.ic_chatroom);
        ic_chatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),EnterChatRoom.class);
                getActivity().startActivity(i);
            }
        });
        */

        ic_bookmark = v.findViewById(R.id.ic_bookmark);
        ic_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),WishList.class);
                getActivity().startActivity(i);
            }
        });


        cancel_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel_1.setVisibility(View.GONE);
                category.setBackground(getActivity().getResources().getDrawable(R.drawable.home_buttons_1));
                category_icon.setVisibility(View.VISIBLE);
                category_text.setText(getActivity().getResources().getString(R.string.filter));
                category_text.setTextColor(getActivity().getResources().getColor(R.color.black));
                category.setEnabled(true);

                data_filter_category.clear();
                data_filter_sort.clear();
                data.clear();
                gridView.setAdapter(null);
                gridView.setAdapter(gridViewAdapter);
                gridView.setExpanded(true);
                gridView.setFocusable(false);
                mProgressBar.setVisibility(View.VISIBLE);
                counter = 0;
                getCronyMarketData(0);

            }
        });

        category = v.findViewById(R.id.category);
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new MaterialDialog.Builder(getActivity())
                        .title(R.string.filter)
                        .items(R.array.category_market)
                        .titleGravity(GravityEnum.CENTER)
                        .itemsGravity(GravityEnum.CENTER)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                category_text.setText(text);
                                category_text.setTextColor(Color.WHITE);
                                category.setBackground(getActivity().getResources().
                                        getDrawable(R.drawable.home_buttons));
                                category.setEnabled(false);

                                category_icon.setVisibility(View.GONE);
                                cancel_1.setVisibility(View.VISIBLE);
                                data_filter_category.clear();
                                data_filter_sort.clear();
                                data.clear();
                                gridView.setAdapter(null);
                                gridView.setAdapter(gridViewAdapter);
                                gridView.setExpanded(true);
                                gridView.setFocusable(false);
                                mProgressBar.setVisibility(View.VISIBLE);
                                counter = 0;
                                getCronyMarketData(0);
                            }
                        })
                        .show();
            }
        });


        cancel_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort_by.setBackground(getActivity().getResources().getDrawable(R.drawable.home_buttons_1));
                cancel_2.setVisibility(View.GONE);
                sort_by_icon.setVisibility(View.VISIBLE);
                sort_text.setTextColor(getActivity().getResources().getColor(R.color.black));
                sort_text.setText(getActivity().getResources().getString(R.string.sort));
                sort_by.setEnabled(true);

                data_filter_category.clear();
                data_filter_sort.clear();
                data.clear();
                gridView.setAdapter(null);
                gridView.setAdapter(gridViewAdapter);
                gridView.setExpanded(true);
                gridView.setFocusable(false);
                mProgressBar.setVisibility(View.VISIBLE);
                counter = 0;
                getCronyMarketData(0);
            }
        });


        no_internet = v.findViewById(R.id.no_internet);

        mProgressBar = v.findViewById(R.id.progress_bar_timeline);

        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onPause();
                data.clear();
                cancel_1.setVisibility(View.GONE);
                category.setBackground(getActivity().getResources().getDrawable(R.drawable.home_buttons_1));
                category_icon.setVisibility(View.VISIBLE);
                category_text.setText(getActivity().getResources().getString(R.string.filter));
                category_text.setTextColor(getActivity().getResources().getColor(R.color.black));
                category.setEnabled(true);

                sort_by.setBackground(getActivity().getResources().getDrawable(R.drawable.home_buttons_1));
                cancel_2.setVisibility(View.GONE);
                sort_by_icon.setVisibility(View.VISIBLE);
                sort_text.setTextColor(getActivity().getResources().getColor(R.color.black));
                sort_text.setText(getActivity().getResources().getString(R.string.sort));
                sort_by.setEnabled(true);

                data_filter_category.clear();
                data_filter_sort.clear();
                data.clear();
                gridView.setAdapter(null);
                gridView.setAdapter(gridViewAdapter);
                gridView.setExpanded(true);
                gridView.setFocusable(false);
                mProgressBar.setVisibility(View.VISIBLE);
                counter = 0;
                getCronyMarketData(0);
            }
        });
        sort_by = v.findViewById(R.id.sort_by);

        sort_by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new MaterialDialog.Builder(getActivity())
                        .title(R.string.sort)
                        .items(R.array.sort_market)
                        .titleGravity(GravityEnum.CENTER)
                        .itemsGravity(GravityEnum.CENTER)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                sort_text.setText(text);
                                sort_text.setTextColor(Color.WHITE);
                                sort_by.setBackground(getActivity().getResources().
                                        getDrawable(R.drawable.home_buttons));
                                sort_by.setEnabled(false);
                                sort_by_icon.setVisibility(View.GONE);
                                cancel_2.setVisibility(View.VISIBLE);

                                data_filter_category.clear();
                                data_filter_sort.clear();
                                data.clear();
                                gridView.setAdapter(null);
                                gridView.setAdapter(gridViewAdapter);
                                gridView.setExpanded(true);
                                gridView.setFocusable(false);
                                mProgressBar.setVisibility(View.VISIBLE);
                                counter = 0;
                                getCronyMarketData(0);
                            }
                        })
                        .show();
            }
        });
        //All of Crony Market Adapter
        //Creating GridViewAdapter Object
        data.clear();
        gridViewAdapter = new CronyMarketAdapter(getActivity(),data);
        gridView.setAdapter(gridViewAdapter);
        gridView.setExpanded(true);
        gridView.setFocusable(false);
        mProgressBar.setVisibility(View.VISIBLE);
        getCronyMarketData(0);


        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(v.getChildAt(v.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                            scrollY > oldScrollY) {

                               if(loading.equals("0") && data.size()>0) {
                                   mGridLoader.setVisibility(View.VISIBLE);
                                   getCronyMarketData(data.size()+counter);//for taking
                                   //care of sold and unverified items
                                   loading = "1";
                               }
                             //  Load Your Data
                    }
                }
            }
        });

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mRequestQueue.cancelAll(REQUEST_TAG);
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

        private void getCronyMarketData(final int post_id){
        final String sort_text_string;
        if(sort_text.getText().toString().equals("₹ Min to Max")){
            sort_text_string = "1";
        }else if(sort_text.getText().toString().equals("₹ Max to Min")){
            sort_text_string = "2";
        }else{
            sort_text_string = "0";
        }

        if(haveNetworkConnection()) {
            mStringRequest = new StringRequest(Request.Method.POST, URL_CRONY_MARKET, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    no_posts.setVisibility(View.GONE);

                    //lock
                    loading = "0";
                    mGridLoader.setVisibility(View.GONE);

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        mSwipeRefreshLayout.setRefreshing(false);
                        mProgressBar.setVisibility(View.GONE);
                        no_internet.setVisibility(View.GONE);

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
                            item_data.is_in_wishlist = jsonObject.getString("is_wishlist");
                            item_data.seller = jsonObject.getString("seller");
                            item_data.sold = jsonObject.getString("sold");

                            if (jsonObject.getString("verified").equals("1") && item_data.
                                    sold.equals("0")) {
                                data.add(item_data);
                                data_filter_category.add(item_data);
                            }else{
                                counter++;
                            }

                        }

                        if(data.size()==0){
                            no_posts.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setRefreshing(false);
                            mGridLoader.setVisibility(View.GONE);
                        }

                        gridViewAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mProgressBar.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setRefreshing(false);
                        mGridLoader.setVisibility(View.GONE);
                        if(data.size()==0)
                        no_posts.setVisibility(View.VISIBLE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mGridLoader.setVisibility(View.GONE);
                    loading = "0";
                    no_internet.setVisibility(View.GONE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("user_id", user_id);
                    hashMap.put("post_id",Integer.toString(post_id));
                    hashMap.put("category",category_text.getText().toString());
                    hashMap.put("sort_text",sort_text_string);
                    return hashMap;
                }
            };

            mStringRequest.setTag(REQUEST_TAG);
            mRequestQueue.add(mStringRequest);
        }else{

            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
            no_posts.setVisibility(View.GONE);

        }

    }

    public void scrolltoTop(){
        mNestedScrollView.fullScroll(View.FOCUS_UP);
        mNestedScrollView.scrollTo(0,0);
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
