package com.abhigam.www.foodspot;

/**
 * Created by sourabhzalke on 26/03/17.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentProfile extends Fragment {

    //Profile Image
    private CircleImageView mNetworkImageView;

    //real_layouts
    private LinearLayout real_number_posts,real_number_followers,real_number_following;


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
    private static final String PREF_BIO = "pref_bio";
    private static final String PREF_VERIFIED = "pref_verified";
    private static final String PREF_VERIFY_REQUESTED = "pref_verify_request";
    private static String username,email,gender,verified;
    private ImageView no_posts,no_internet;

    //for webview
    private static final String WEB_URL_INTENT = "web_url_intent_crony";
    private static final String crony_about_url = "https://www.crony.co.in";

    public static final String INTENT_USERNAME = "username_intent";
    public static final String INTENT_USER_ID = "user_id_intent";

    private int scroll_lock =0;


    private List<DataRecycler> data = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private AdapterUserFeed mAdapterUserFeed;
    private WrapContentLinearLayoutManager linearLayoutManager;
    private NestedScrollView mNestedScrollView;
    private ImageView edit_profile,back_arrow,verified_icon;
    private LinearLayout more_vert_linear,back_arrow_linear;

    private static String USER_FEED = "http://13.233.234.79/user_feed.php";

    //followers and followings
    private String user_fullname,number_posts,number_followers,number_following,user_id,bio_string;
    private static char[] c = new char[]{'k', 'm', 'b', 't'};
    long y=0,z=0;


    //URL's
    private static final String URL="http://13.233.234.79/user_id.php";
    private static String URL_N = "http://13.233.234.79/no_of_followers.php";
    private String URL_USER_DETAILS = "http://13.233.234.79/user_id.php";

    //TextViews
    private TextView posts,followers,following,full_n,enroll_n;


    //For Server Connections
    private StringRequest request;
    private RequestQueue mRequestQueue;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mNoteBook;

    //Tags for edit Fragment
    private static final String FULLNAME_TAG = "full_n";

    private Toolbar mToolbar;
    private TextView toolbar_title;
    private TextView no_of_coins,bio;
    private String no_coins_string;
    private LinearLayout wallet_linear;


    public static FragmentProfile newInstance(){
        FragmentProfile fragment = new FragmentProfile();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SharedPreferences prefs = SaveSharedPreference.getSharedPreferences(getActivity().getApplicationContext());
        username = prefs.getString(PREF_USER_NAME,"");
        user_fullname = prefs.getString(PREF_FULLNAME,"");
        user_id = prefs.getString(PREF_USER_ID,"");
        number_posts = prefs.getString(PREF_POSTS,"");
        email = prefs.getString(PREF_EMAIL,"");
        gender = prefs.getString(PREF_SEX,"");
        bio_string = prefs.getString(PREF_BIO,"");

        mRequestQueue = Volley.newRequestQueue(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.activity_fragment_user,container,false);


        //text_username = (TextView)v.findViewById(R.id.username);
        mNetworkImageView = v.findViewById(R.id.circular_profile);

        posts =  v.findViewById(R.id.number_posts);
        followers = v.findViewById(R.id.number_followers);
        following = v.findViewById(R.id.number_following);
        enroll_n = v.findViewById(R.id.enroll_n);
        full_n =  v.findViewById(R.id.full_n);
        mNestedScrollView =  v.findViewById(R.id.nestedScrollview);
        full_n.setText(user_fullname);
        enroll_n.setText(username);
        more_vert_linear = v.findViewById(R.id.more_vert);
        posts.setText(number_posts);  //setting text when getting number of posts
        back_arrow_linear = v.findViewById(R.id.back);
        back_arrow = v.findViewById(R.id.back_arrow_image);
        edit_profile = v.findViewById(R.id.edit_image);
        toolbar_title = v.findViewById(R.id.toolbar_title);
        bio = v.findViewById(R.id.bio);
        bio.setText(bio_string);
        no_posts = v.findViewById(R.id.no_posts);
        no_internet = v.findViewById(R.id.no_internet);
        verified_icon = v.findViewById(R.id.ic_verified);
        wallet_linear = v.findViewById(R.id.wallet_linear);
        wallet_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),CoinTransactions.class);
                getActivity().startActivity(i);
            }
        });

        Window window = getActivity().getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.purple));
        }

        real_number_followers = v.findViewById(R.id.real_number_followers);
        real_number_following = v.findViewById(R.id.real_number_following);
        real_number_posts = v.findViewById(R.id.real_number_posts);
        mProgressBar = v.findViewById(R.id.progress_bar_timeline);
        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.clearFocus();

                mProgressBar.setVisibility(View.VISIBLE);
                //Getting user feed
                data.clear();
                mRecyclerView.getRecycledViewPool().clear();
                getUserFeed(0);
                getFollowersAndFollowing();
                getUserIDFirst(username);
            }
        });
        mNoteBook = v.findViewById(R.id.notebook);

        mToolbar = v.findViewById(R.id.toolbar_top);
        no_of_coins = v.findViewById(R.id.no_of_coins);

        mNoteBook.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),Activities.class);
                startActivity(i);
            }
        });

        more_vert_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(getActivity(), v);
                menu.getMenu().add("Edit Profile");
                menu.getMenu().add("About");
                menu.getMenu().add("Contact Us");
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Edit Profile")){
                            Intent x = new Intent(getActivity(),EditProfile.class);
                            startActivity(x);
                        }else if(item.getTitle().equals("About")){
                            Intent i = new Intent(getActivity(),WebActivity.class);
                            i.putExtra(WEB_URL_INTENT,crony_about_url);
                            getActivity().startActivity(i);
                        }else if(item.getTitle().equals("Logout")){
                            Intent i = new Intent(getActivity(),WebActivity.class);
                            i.putExtra(WEB_URL_INTENT,crony_about_url);
                            getActivity().startActivity(i);
                        }else if(item.getTitle().equals("Contact Us")){
                            Intent i = new Intent(getActivity(),ContactDetails.class);
                            getActivity().startActivity(i);
                        }
                        return true;
                    }
                });
                menu.show();
            }
        });

        back_arrow_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        real_number_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent followers = new Intent(getActivity(),Followers.class);
                followers.putExtra(INTENT_USER_ID,user_id);
                followers.putExtra(INTENT_USERNAME,username);
                startActivity(followers);
            }
        });

        real_number_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int scrollTo = ((View) mRecyclerView.getParent().getParent()).getTop() + mRecyclerView.getTop();
                mNestedScrollView.smoothScrollTo(0, scrollTo);
            }
        });

        real_number_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),Following.class);
                i.putExtra(INTENT_USER_ID,user_id);
                i.putExtra(INTENT_USERNAME,username);
                startActivity(i);
            }
        });

        //ALL of Recyclerview
        mRecyclerView = v.findViewById(R.id.recycler_user_feed);
        mRecyclerView.setNestedScrollingEnabled(false);


        mAdapterUserFeed = new AdapterUserFeed(getActivity(),data);
        linearLayoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapterUserFeed);
        mRecyclerView.setItemAnimator(null);

        mProgressBar.setVisibility(View.VISIBLE);
        //Getting user feed
        data.clear();
        mRecyclerView.getRecycledViewPool().clear();
        getUserFeed(0);
        getFollowersAndFollowing();
        getUserIDFirst(username);

        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(v.getChildAt(v.getChildCount() - 1) != null) {

                    if (scrollY > oldScrollY) {
                        mToolbar.setBackgroundColor(getResources().getColor(R.color.white));
                        back_arrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                                android.graphics.PorterDuff.Mode.MULTIPLY);
                        edit_profile.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                                PorterDuff.Mode.SRC_IN);
                        toolbar_title.setVisibility(View.VISIBLE);
                    }else if(scrollY == 0){
                        mToolbar.setBackgroundColor(Color.TRANSPARENT);
                        back_arrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white),
                                android.graphics.PorterDuff.Mode.MULTIPLY);
                        edit_profile.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white),
                                PorterDuff.Mode.SRC_IN);
                        toolbar_title.setVisibility(View.GONE);
                    }
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
                                    mAdapterUserFeed.notifyItemInserted(data.size() - 1);
                                    getUserFeed(data.size());
                                }
                            }
//                        Load Your Data
                        }
                    }
                }
            }
        });


        /*
        //adding on scrollListener
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView
                                           recyclerView, int dx, int dy) {

                Log.d("sourabh",Integer.toString(linearLayoutManager.findLastCompletelyVisibleItemPosition()));

                if(linearLayoutManager.findLastCompletelyVisibleItemPosition()==data.size()-1){
                    if(data.size()>0) {
                        if (data.get(data.size() - 1) != null) {
                            data.add(null);
                            mAdapterUserFeed.notifyItemInserted(data.size() - 1);
                        }
                    }
                    getUserFeed(data.size());
                }
            }
        });
        */


        return v;
    }

    private void getUserIDFirst(final String enrollment){

        request = new StringRequest(Request.Method.POST, URL_USER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.names().get(0).toString().equals("error")){
                                SaveSharedPreference.clearUserName(getActivity().getApplicationContext());
                                Intent i = new Intent(getActivity(),FirstScreen.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                            no_coins_string = jsonObject.getString("coins");
                            no_of_coins.setText(" ₹"+no_coins_string);
                            verified = jsonObject.getString("verified");

                            if(verified.equals("1")){
                                verified_icon.setVisibility(View.VISIBLE);
                            }else{
                                verified_icon.setVisibility(View.GONE);
                            }
                            String verfiy_requested = jsonObject.getString("verify_requested");
                            if(verfiy_requested.equals("0")){
                                SharedPreferences.Editor editor = PreferenceManager
                                        .getDefaultSharedPreferences(getActivity()).edit();
                                editor.putString(PREF_VERIFY_REQUESTED,"0");
                                editor.putString(PREF_FULLNAME,jsonObject.getString("first_name")
                                +" "+jsonObject.getString("last_name"));
                                editor.putString(PREF_USER_NAME,jsonObject.getString("enrollment"));
                                editor.putString(PREF_BRANCH,jsonObject.getString("branch"));
                                editor.commit();
                            }else{
                                SharedPreferences.Editor editor = PreferenceManager
                                        .getDefaultSharedPreferences(getActivity()).edit();
                                editor.putString(PREF_VERIFY_REQUESTED,"1");
                                editor.putString(PREF_FULLNAME,jsonObject.getString("first_name")
                                        +" "+jsonObject.getString("last_name"));
                                editor.putString(PREF_USER_NAME,jsonObject.getString("enrollment"));
                                editor.putString(PREF_BRANCH,jsonObject.getString("branch"));
                                editor.commit();
                            }

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
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(FULLNAME_TAG);
        mRequestQueue.add(request);
    }


    @Override
    public void onPause(){
        super.onPause();
        mRequestQueue.cancelAll(FULLNAME_TAG);
    }

    @Override
    public void onResume() {
        super.onResume();

        //top_layout_update
        SharedPreferences prefs = SaveSharedPreference.getSharedPreferences(getActivity().getApplicationContext());
        username = prefs.getString(PREF_USER_NAME,"");
        user_fullname = prefs.getString(PREF_FULLNAME,"");
        bio_string = prefs.getString(PREF_BIO,"");
        bio.setText(bio_string);
        enroll_n.setText(username);
        full_n.setText(user_fullname);

        Glide
                .with(getActivity())
                .load("http://13.233.234.79/uploads/profile_pic/" +username+ "_profile.jpg")
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                .skipMemoryCache(true)
                .centerCrop()
                .into(new SimpleTarget<Bitmap>(300, 300) { //width and height
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        mNetworkImageView.setImageBitmap(resource);
                    }
                });
    }


  //  @Override
  //  public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
  //      switch (item.getItemId()) {
  //          case R.id.action_logout:
   //             SaveSharedPreference.clearUserName(getActivity().getApplicationContext());
    //            Intent i = new Intent(getActivity(),LoginActivity.class);
    //            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
     //           startActivity(i);
      //          return true;
      //      default:
       //         return super.onOptionsItemSelected(item);
       // }
    //}

    private static String coolFormat(double n, int iteration) {
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : coolFormat(d, iteration+1));

    }



    //to get feed of following people
    private void getUserFeed(final int size){

        if(haveNetworkConnection()) {

            request = new StringRequest(Request.Method.POST, USER_FEED, new Response.Listener<String>() {


                @Override
                public void onResponse(String response) {

                    if (response != null && response.length() > 0) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            mProgressBar.setVisibility(View.GONE);


                            no_posts.setVisibility(View.GONE);
                            no_internet.setVisibility(View.GONE);
                            // Stop refresh animation
                            mSwipeRefreshLayout.setRefreshing(false);

                            JSONArray jsonArray = jsonResponse.getJSONArray("posts");

                            if (data.size() > 0) {
                                //   remove progress item
                                if (data.get(data.size() - 1) == null) {  //condition for not removing item only remove progressBar
                                    data.remove(data.size() - 1);
                                    mAdapterUserFeed.notifyItemRemoved(data.size());
                                }
                            }

                            for (int i = 0; i < jsonArray.length(); ++i) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                DataRecycler post_data = new DataRecycler();
                                post_data.post_id = jsonObject.getString("post_id");
                                post_data.user_id = jsonObject.getString("user_id");
                                post_data.enrollment = jsonObject.getString("enrollment");
                                post_data.fullname = jsonObject.getString("first_name") + " " +
                                        jsonObject.getString("last_name");
                                post_data.time_ago = jsonObject.getString("time_ago");
                                post_data.caption = jsonObject.getString("caption");
                                post_data.is_image = jsonObject.getString("is_image");
                                post_data.is_liked = jsonObject.getString("is_liked");
                                post_data.likes_number = jsonObject.getString("likes_number");
                                post_data.views_number = jsonObject.getString("views");
                                post_data.verified = jsonObject.getString("verified");
                                post_data.is_saved = jsonObject.getString("is_saved");
                                post_data.comments_number = jsonObject.getString("@comments_number" +
                                        " := (SELECT COUNT(*) from post_comments where post_id = i.post_id)");
                                post_data.first_load = "1";
                                data.add(post_data);
                            }

                            mAdapterUserFeed.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mProgressBar.setVisibility(View.GONE);
                            // Stop refresh animation
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (data.size() == 0) {
                                no_posts.setVisibility(View.VISIBLE);
                                no_internet.setVisibility(View.GONE);
                                //condition for no posts
                            }
                            if (data.size() > 0) {
                                if (data.get(data.size() - 1) == null) {  //condition for not removing item only remove progressBar
                                    //remove progress item
                                    data.remove(data.size() - 1);
                                    mAdapterUserFeed.notifyItemRemoved(data.size());
                                }
                            }
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressBar.setVisibility(View.GONE);
                    if (data.size() > 0) {
                        if (data.get(data.size() - 1) == null) {  //condition for not removing item only remove progressBar
                            //remove progress item
                            data.remove(data.size() - 1);
                            mAdapterUserFeed.notifyItemRemoved(data.size());
                        }
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                    // Toast.makeText(mActivity,"Unable to connect",Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("user_id", user_id);
                    hashMap.put("post_id", Integer.toString(size));
                    return hashMap;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setTag(FULLNAME_TAG);
            mRequestQueue.add(request);
        }else{
            no_posts.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);

        }
    }


    private void getFollowersAndFollowing(){

        request = new StringRequest(Request.Method.POST, URL_N, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    y = Long.parseLong(jsonResponse.getString("following"));
                    z = Long.parseLong(jsonResponse.getString("followers"));

                    number_posts = jsonResponse.getString("num_posts");
                    posts.setText(number_posts);

                    if(y >=1000){
                    number_following = coolFormat(y,0);
                    number_followers = coolFormat(z,0);
                    }
                    else {
                        number_following = jsonResponse.getString("following");
                        number_followers = jsonResponse.getString("followers");
                        int x = Integer.parseInt(number_following);
                        int zak = Integer.parseInt(number_followers);
                        if(zak!=0){
                            zak=zak-1;
                        }
                        if(x!=0){
                            x = x-2;
                        }
                        if(zak<0)
                            zak=0;
                        if(x<0)
                            x=0;
                        number_followers = Integer.toString(zak);
                        number_following = Integer.toString(x);
                    }

                    followers.setText(number_followers);
                    following.setText(number_following);


                } catch (JSONException e) {
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
                hashMap.put("user_id",user_id);
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(FULLNAME_TAG);
        mRequestQueue.add(request);

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
