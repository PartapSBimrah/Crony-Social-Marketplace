package com.abhigam.www.foodspot;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sourabhzalke on 16/03/18.
 */

public class LikedUsersFragment extends Fragment {

    private List<DataItem> data = new ArrayList<>();


    //intent_tags
    private static final String INTENT_POST_ID = "com.abhigam.www.foodspot.POST_ID";
    private String post_id,user_id;
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
    private String URL_LIKED_USERS = "http://13.233.234.79/get_liked_users_un.php";
    int is_follow;
    int is_request;
    private String instance_user_id;

    private RecyclerView mRecyclerView;
    private AdapterSuggestedUsers mAdapterSuggestedUsers;
    private ImageView no_likes,no_internet;
    private ProgressBar mProgressBar;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SharedPreferences prefs = SaveSharedPreference.getSharedPreferences(getActivity().getApplicationContext());
        user_id = prefs.getString(PREF_USER_ID,"");
        mRequestQueue = Volley.newRequestQueue(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_liked_users,container,false);

        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        if (toolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        post_id = getActivity().getIntent().getStringExtra(INTENT_POST_ID);
        mRecyclerView = v.findViewById(R.id.recycler_view_for_liked_users);
        no_likes = v.findViewById(R.id.no_likes);
        no_internet = v.findViewById(R.id.no_internet);
        mProgressBar = v.findViewById(R.id.progress_bar_timeline);

        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                data.clear();
                getLikedUsers(Integer.toString(0));
            }
        });

        mAdapterSuggestedUsers = new AdapterSuggestedUsers(getActivity(), data);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapterSuggestedUsers);

        mProgressBar.setVisibility(View.VISIBLE);
        getLikedUsers(Integer.toString(0));

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
                            getLikedUsers(Integer.toString(data.size()));
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


    public void getLikedUsers(final String last){

        if(haveNetworkConnection()) {

            mStringRequest = new StringRequest(Request.Method.POST, URL_LIKED_USERS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    mSwipeRefreshLayout.setRefreshing(false);
                    no_likes.setVisibility(View.GONE);
                    no_internet.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);

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

                            is_follow = json_data.getInt("@is_follow := (SELECT COUNT(*) " +
                                    "from follows where user_id = '" + user_id + "' and following_id = j.id)");
                            is_request = json_data.getInt("@is_request := (SELECT COUNT(*) from follows where user_id = '" +
                                    user_id + "' and following_id = j.id and accepted = '0')");

                            itemData.user_id = json_data.getString("id");
                            instance_user_id = json_data.getString("id");
                            if (instance_user_id.equals(user_id))
                                itemData.followed = 2;
                            else if (is_follow == 1)
                                itemData.followed = 1;
                            else if (is_request == 1)
                                itemData.followed = 5;
                            else
                                itemData.followed = 0;
                            itemData.verified = json_data.getString("verified");
                            itemData.private_ac = json_data.getString("private_ac");

                            itemData.profilepicName = json_data.getString("profilepicname");
                            data.add(itemData);
                        }

                        if (data.size() == 0) {
                            no_likes.setVisibility(View.VISIBLE);
                        }
                        mAdapterSuggestedUsers.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (data.size() == 0){
                            no_likes.setVisibility(View.VISIBLE);
                            no_internet.setVisibility(View.GONE);
                        }
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("post_id", post_id);
                    hashMap.put("user_id", user_id);
                    hashMap.put("number",last);
                    return hashMap;
                }
            };

            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mRequestQueue.add(mStringRequest);
        }else{
            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
            no_likes.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
        }

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
