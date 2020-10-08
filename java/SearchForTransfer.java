package com.abhigam.www.foodspot;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class SearchForTransfer extends AppCompatActivity {

    private List<DataItem> data = new ArrayList<>();


    SearchView searchView = null;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
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
    private String username,user_id,amount,sender_money;
    private TextView no_suggestions;
    private String server_request_tag = "server_request_tag_suggest_friends";
    private String INTENT_ENROLLMENT = "com.abhigam.www.foodspot.enrollment";
    private String INTENT_FULLNAME = "com.abhigam.www.foodspot.fullname";
    private String INTENT_USER_ID = "com.abhigam.www.foodspot.user_id";
    private String RECIEVER_INTENT_COINS = "com.abhigam.www.foodspot.reciever_intent_coins";
    private String SENDER_INTENT_COINS = "com.abhigam.www.foodspot.sender_intent_coins";
    private String INTENT_COINS = "com.abhigam.www.foodspot.intent_coins";
    private String INTENT_LINEAR = "com.abhigam.www.foodspot.linear";


    private RecyclerView mRecyclerView;
    private AdapterTransfer mAdapterTransfer;


    //URLS
    private static String URL_SEARCH = "http://13.233.234.79/users_search_auto.php";

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        this.finish();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        setContentView(R.layout.activity_search_for_transfer);

        mRequestQueue = Volley.newRequestQueue(this);

        no_suggestions = findViewById(R.id.no_suggestions);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        username = prefs.getString(PREF_USER_NAME,"");
        user_id = prefs.getString(PREF_USER_ID,"");

        amount = getIntent().getStringExtra(INTENT_COINS);
        sender_money = getIntent().getStringExtra(SENDER_INTENT_COINS);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.text_search));
        }

        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapterTransfer = new AdapterTransfer(SearchForTransfer.this,data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchForTransfer.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapterTransfer);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("sourbh","onCreateOptionsMenu()");
        // adds item to action bar
        getMenuInflater().inflate(R.menu.search_main, menu);

        //Checking intent from item_fragment and querying searchView

        // Get Search item from action bar and Get Search service
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) SearchForTransfer.this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView)searchItem.getActionView();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                searchView.setTransitionName("searchbar");
            }
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchForTransfer.this.getComponentName()));
            searchView.setIconified(false); //not showing search icon
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.e("onQueryTextChange", "called");
                data.clear();
                mRecyclerView.getRecycledViewPool().clear();
                mAdapterTransfer.notifyDataSetChanged();
                mRequestQueue.cancelAll(server_request_tag);
                search_users(newText);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {


                // Do your task here

                return false;
            }

        });

        return true;
    }


    // Every time when you press search button on keypad an Activity is recreated which in turn calls this function
    @Override
    protected void onNewIntent(Intent intent) {
        // Get search query and create object of class AsyncFetch
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null) {
                searchView.clearFocus();
            }
            data.clear();
            mRecyclerView.getRecycledViewPool().clear();
            search_users(query);
        }
    }

    private void search_users(final String searchQuery){

        mStringRequest = new StringRequest(Request.Method.POST, URL_SEARCH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("items");

                    for(int i=0;i<jsonArray.length();++i){
                        JSONObject json_data = jsonArray.getJSONObject(i);
                        DataItem itemData = new DataItem();
                        itemData.username = json_data.getString("enrollment");
                        if(!json_data.getString("first_name").equals("null") && !json_data.getString("last_name").equals("null")) {
                            itemData.full_name = json_data.getString("first_name") + " " + json_data.getString("last_name");
                        }else
                            itemData.full_name = "0";
                        itemData.user_id = json_data.getString("id");
                        itemData.verified = json_data.getString("verified");
                        itemData.price = json_data.getString("coins");
                        itemData.amount = amount;
                        itemData.sender_money = sender_money;
                        itemData.profilepicName = json_data.getString("profilepicname");
                        if(!itemData.user_id.equals(user_id))
                        data.add(itemData);
                    }

                    if(data.size() == 0){
                        no_suggestions.setVisibility(View.VISIBLE);
                    }else{
                        no_suggestions.setVisibility(View.GONE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mAdapterTransfer.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("searchQuery",searchQuery);
                return hashMap;
            }
        };
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mStringRequest.setTag(server_request_tag);
        mRequestQueue.add(mStringRequest);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
