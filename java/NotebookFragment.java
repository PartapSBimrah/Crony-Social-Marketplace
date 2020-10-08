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

public class NotebookFragment extends Fragment{

    private String URL_NOTEBOOK_DATA = "http://13.233.234.79/notebook_data.php";
    private String user_id;
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
    private ImageView no_posts,no_internet;
    int x=0;
    private WrapContentLinearLayoutManager linearLayoutManager;
    //server_request
    private StringRequest mStringRequest;
    private RequestQueue mRequestQueue;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AdapterNotebook mAdapterNotebook;
    private ProgressBar mProgressBar;
    private List<DataRecycler> data = new ArrayList<>();
    private String server_request_tag = "server_request_tag_suggest_friends";
    private LinearLayout back;
    private NestedScrollView mNestedScrollView;
    private Toolbar mToolbar;
    private TextView number_saved;
    private static final String REQUEST_TAG = "request_tag";

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
        View v = inflater.inflate(R.layout.fragment_notebook,container,false);

        mToolbar = v.findViewById(R.id.toolbar_top);

        Window window = getActivity().getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.my_notebook));
        }


        no_posts = v.findViewById(R.id.no_posts);
        no_internet = v.findViewById(R.id.no_internet);
        back = v.findViewById(R.id.back);
        mNestedScrollView = v.findViewById(R.id.nestedScrollview);
        number_saved = v.findViewById(R.id.number_saved);

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

        mProgressBar = v.findViewById(R.id.progressBar);
        mRecyclerView = v.findViewById(R.id.recycler);
        mRecyclerView.setNestedScrollingEnabled(false);

        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
            }
        });

        //sending data to Adapter
        mAdapterNotebook = new AdapterNotebook(getActivity(),data);
        //setting layout manager
        linearLayoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //setting the adapter
        mRecyclerView.setAdapter(mAdapterNotebook);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.clearFocus();
        data.clear();
        mRecyclerView.getRecycledViewPool().clear();
        mAdapterNotebook.notifyDataSetChanged();
        mRequestQueue.cancelAll(server_request_tag);
        mProgressBar.setVisibility(View.VISIBLE);
      //  mProgressBar.setVisibility(View.VISIBLE);
        getNotebookData();
    }

    @Override
    public void onPause() {
        super.onPause();
        mRequestQueue.cancelAll(server_request_tag);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll(server_request_tag);
    }

    private void getNotebookData(){

        if(haveNetworkConnection()) {
            mStringRequest = new StringRequest(Request.Method.POST, URL_NOTEBOOK_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    no_internet.setVisibility(View.GONE);
                    no_posts.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);

                    if (response.equals("no rows")) {
                        no_posts.setVisibility(View.VISIBLE);
                        no_internet.setVisibility(View.GONE);
                        number_saved.setText("0");
                    }else {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            mSwipeRefreshLayout.setRefreshing(false);

                            JSONArray jsonArray = jsonResponse.getJSONArray("items");

                            for (int i = 0; i < jsonArray.length(); ++i) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                DataRecycler item_data = new DataRecycler();
                                item_data.post_id = jsonObject.getString("post_id");
                                item_data.user_id = jsonObject.getString("user_id");
                                item_data.enrollment = jsonObject.getString("enrollment");
                                item_data.fullname = jsonObject.getString("first_name") + " "
                                        + jsonObject.getString("last_name");
                                item_data.time_ago = jsonObject.getString("time_ago");
                                item_data.caption = jsonObject.getString("caption");
                                item_data.is_liked = jsonObject.getString("is_liked");
                                item_data.likes_number = jsonObject.getString("likes_number");
                                item_data.views_number = jsonObject.getString("views");
                                item_data.is_image = jsonObject.getString("is_image");
                                item_data.verified = jsonObject.getString("verified");
                                item_data.comments_number = jsonObject.getString("@comments_number" +
                                        " := (SELECT COUNT(*) from post_comments where post_id = i.post_id)");
                                item_data.first_load = "1";
                                data.add(item_data);
                            }

                            number_saved.setText(Integer.toString(data.size()));
                            mAdapterNotebook.notifyDataSetChanged();

                            x = mAdapterNotebook.getItemCount();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mSwipeRefreshLayout.setRefreshing(false);
                            if(data.size()==0) {
                                number_saved.setText("0");
                            }
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
            number_saved.setText("0");
        }
    }

    public void onRemoved(){
        number_saved.setText(Integer.toString(data.size()));
        if(data.size()==0){
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
