package com.abhigam.www.foodspot;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sourabhzalke on 23/06/17.
 */

public class FollowersFragment extends Fragment {

    private List<DataItem> data = new ArrayList<>();

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String username,user_id,user_check_id,instance_user_id,i_am;

    public static final String INTENT_USERNAME = "username_intent";
    public static final String INTENT_USER_ID = "user_id_intent";

    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME= "pref_username";
    private static final String PREF_PHONE_NUMBER = "pref_phone_number";
    private static final String PREF_EMAIL = "pref_email";
    private static final String PREF_BIRTHDAY = "pref_birthday";
    private static final String PREF_SEX= "pref_sex";
    private static final String PREF_BRANCH = "pref_branch";
    private static final String PREF_YEAR_ADM = "pref_year_adm";
    private static final String PREF_POSTS = "pref_posts";

    private TextView toolbar_title;
    private int is_follow,is_request;

    private RecyclerView mRecyclerView;
    private AdapterSuggestedUsers mAdapterSuggestedUsers;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private ImageView no_followers,no_internet;
    private static final String REQUEST_TAG = "request_tag";

    private static String URL = "http://13.233.234.79/visitor_followers_number_un.php";
    private static final String I_AM = "i_am";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mRequestQueue = Volley.newRequestQueue(getActivity());

        username = getActivity().getIntent().getStringExtra(INTENT_USERNAME);
        user_id = getActivity().getIntent().getStringExtra(INTENT_USER_ID);
        i_am = getActivity().getIntent().getStringExtra(I_AM);

        SharedPreferences prefs = SaveSharedPreference.getSharedPreferences(getActivity().getApplicationContext());
        user_check_id = prefs.getString(PREF_USER_ID,"");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_followers,container,false);

        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        if (toolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbar_title = v.findViewById(R.id.main_toolbar_title);
        toolbar_title.setText(getResources().getString(R.string.followers_number));

        mRecyclerView = v.findViewById(R.id.recycler_view_for_suggested_users);

        no_followers = v.findViewById(R.id.no_followers);
        no_internet = v.findViewById(R.id.no_internet);
        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                data.clear();
                followers_users(0);
            }
        });
        mProgressBar = v.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        mAdapterSuggestedUsers = new AdapterSuggestedUsers(getActivity(), data);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapterSuggestedUsers);

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
                            mAdapterSuggestedUsers.notifyItemInserted(data.size() - 1);
                            followers_users(data.size());
                        }
                    }
                }
            }
        });

        data.clear();
        followers_users(0);

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

    private void followers_users(final int last){

        if(haveNetworkConnection()) {
            mStringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    no_internet.setVisibility(View.GONE);
                    no_followers.setVisibility(View.GONE);

                    if (data.size() > 0) {
                        //remove progress item
                        if (data.get(data.size() - 1) == null) {  //condition for not removing item only remove progressBar
                            data.remove(data.size() - 1);
                            mAdapterSuggestedUsers.notifyItemRemoved(data.size());
                        }
                    }

                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = 0; i < jsonArray.length(); ++i) {
                            JSONObject json_data = jsonArray.getJSONObject(i);
                            DataItem itemData = new DataItem();
                            itemData.username = json_data.getString("enrollment");
                            if (!json_data.getString("first_name").equals("null") && !json_data.getString("last_name").equals("null")) {
                                itemData.full_name = json_data.getString("first_name") + " " + json_data.getString("last_name");
                            } else
                                itemData.full_name = "0";
                            //for visitor fragment user_id problem
                            if (i_am != null) {
                                is_follow = json_data.getInt("@is_follow := (SELECT COUNT(*) from follows where user_id = " +
                                        user_check_id + " and following_id = users.id and accepted = '1')");
                                is_request = json_data.getInt("@is_request := (SELECT COUNT(*) from follows where user_id = " +
                                        user_check_id + " and following_id = users.id and accepted = '0')");

                            } else {
                                is_follow = json_data.getInt("@is_follow := (SELECT COUNT(*) from follows where user_id = " +
                                        user_id + " and following_id = users.id and accepted = '1')");
                                is_request = json_data.getInt("@is_request := (SELECT COUNT(*) from follows where user_id = " +
                                        user_id + " and following_id = users.id and accepted = '0')");
                            }
                            itemData.user_id = json_data.getString("id");
                            itemData.verified = json_data.getString("verified");
                            instance_user_id = json_data.getString("id");
                            if (instance_user_id.equals(user_check_id))
                                itemData.followed = 2;
                            else if (is_follow == 1)
                                itemData.followed = 1;
                            else if (is_request == 1)
                                itemData.followed = 5;
                            else
                                itemData.followed = 0;
                            itemData.profilepicName = json_data.getString("profilepicname");
                            itemData.private_ac = json_data.getString("private_ac");
                            if (!json_data.getString("enrollment").equals(username) &&
                                    !json_data.getString("enrollment").equals("support@crony.co.in"))
                                data.add(itemData);
                        }

                        if (data.size() == 0) {
                            no_internet.setVisibility(View.GONE);
                            no_followers.setVisibility(View.VISIBLE);
                        }

                        mAdapterSuggestedUsers.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (data.size() == 0) {
                            no_internet.setVisibility(View.VISIBLE);
                            no_followers.setVisibility(View.GONE);
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    no_followers.setVisibility(View.GONE);
                    no_internet.setVisibility(View.VISIBLE);

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("user_id", user_id);
                    hashMap.put("user_check_id", user_check_id);
                    hashMap.put("number",Integer.toString(last));
                    return hashMap;
                }
            };

            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mStringRequest.setTag(REQUEST_TAG);
            mRequestQueue.add(mStringRequest);
        }else{
            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
            no_followers.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
        }

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
