package com.abhigam.www.foodspot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sourabhzalke on 29/06/17.
 */

public class VisitorProfileFragment extends Fragment {

    private CircleImageView mNetworkImageView;

    private Long timeInMillis;

    private String URL_FOLLOW = "http://13.233.234.79/follow_people.php";
    private String IS_FOLLOW = "http://13.233.234.79/is_follow.php";
    private final static String URL_SEND_NOTIFICATION = "http://13.233.234.79/send_notification.php";

    private LinearLayout real_number_posts,real_number_followers,real_number_following,
                         private_account_view;

    private String server_request_tag = "server_request_tag_suggest_friends";

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
    private static final String USERNAME_VISITOR = "visitor_username";
    private static final String PREF_BIO = "pref_bio";

    private static String username_visitor;

    //Intent's
    public static final String INTENT_USERNAME = "username_intent";
    public static final String INTENT_USER_ID = "user_id_intent";

    private static final String INTENT_FULLNAME_1 = "intent_fullname";
    private static final String INTENT_OTHER_ID_1 = "intent_other_id";
    private static final String INTENT_USERNAME_1 = "intent_username";

    //Buttons
    private Button follow_as_visitor,following_as_visitor,request_as_visitor;


    //followers,following and post
    private String number_posts,number_followers="0",
            number_following="0",user_id,user_check_id,user_full_name,user_enroll_no,user_real_fullname;
    private ImageView edit_profile,back_arrow;
    private static char[] c = new char[]{'k', 'm', 'b', 't'};
    long y=0,z=0;


    //Server connection
    private StringRequest request;
    private RequestQueue mRequestQueue;
    private ProgressBar mProgressBar;
    private NestedScrollView mNestedScrollView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView bio_textview;
    private String bio_string,verified;
    private ImageView no_posts,no_internet,chat_icon;

    private List<DataRecycler> data = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private AdapterUserFeed mAdapterUserFeed;
    private WrapContentLinearLayoutManager linearLayoutManager;
    private String private_string,num_chat="0",num_connect="0";

    private static String USER_FEED = "http://13.233.234.79/user_feed.php";

    //URL's
    private static final String URL="http://13.233.234.79/user_id.php";
    private static String URL_N = "http://13.233.234.79/no_of_followers.php";
    private String CONNECT_CHAT = "http://13.233.234.79/connect_chat.php";


    private static final String I_AM = "i_am";
    private static  String username;
    private int is_follow = 0;
    private LinearLayout back_arrow_linear,chat_box;
    private ImageView verified_icon;
    private TextView mProfileText;


    //TextViews
    private TextView posts,followers,following,fullname_user,enrollment_user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //getting username and user_id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        user_check_id = prefs.getString(PREF_USER_ID,"");
        username = prefs.getString(PREF_USER_NAME,"");
        user_real_fullname = prefs.getString(PREF_FULLNAME,"");


        //getting username
        username_visitor = getActivity().getIntent().getStringExtra(USERNAME_VISITOR);

        mRequestQueue = Volley.newRequestQueue(getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();
        //removing duplicacy
        follow_as_visitor.setVisibility(View.GONE);
        following_as_visitor.setVisibility(View.GONE);
        request_as_visitor.setVisibility(View.GONE);
        getFollowersAndFollowing();

        is_follow();

        //getting username
        username_visitor = getActivity().getIntent().getStringExtra(USERNAME_VISITOR);

        Glide
                .with(getActivity())
                .load("http://13.233.234.79/uploads/profile_pic/" +username_visitor+ "_profile.jpg")
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                .skipMemoryCache(true)
                .centerCrop()
                .into(new SimpleTarget<Bitmap>(300,300) { //width and height
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        mNetworkImageView.setImageBitmap(resource);
                    }
                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.activity_visitor_profile,container,false);

        Window window = getActivity().getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.purple));
        }


        //Wiring to ids
        final Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        real_number_followers = v.findViewById(R.id.real_number_followers);
        real_number_following = v.findViewById(R.id.real_number_following);
        real_number_posts = v.findViewById(R.id.real_number_posts);
        //follow_button
        follow_as_visitor = v.findViewById(R.id.follow);
        following_as_visitor = v.findViewById(R.id.following);
        //textviews
        posts =v.findViewById(R.id.number_posts);
        followers = v.findViewById(R.id.number_followers);
        following = v.findViewById(R.id.number_following);
        fullname_user = v.findViewById(R.id.full_n);
        enrollment_user = v.findViewById(R.id.enroll_n);
        mNestedScrollView = v.findViewById(R.id.nestedScrollview);
        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.clearFocus();
                mProgressBar.setVisibility(View.VISIBLE);
                data.clear();
                mRecyclerView.getRecycledViewPool().clear();
                mRequestQueue.cancelAll(server_request_tag);
                no_posts.setVisibility(View.GONE);
                no_internet.setVisibility(View.GONE);
                getUserID();
            }
        });
        private_account_view = v.findViewById(R.id.private_account_view);
        back_arrow_linear = v.findViewById(R.id.back);
        back_arrow = v.findViewById(R.id.back_arrow_image);
        verified_icon = v.findViewById(R.id.ic_verified);
        no_internet = v.findViewById(R.id.no_internet);
        no_posts = v.findViewById(R.id.no_posts);
        mProfileText = v.findViewById(R.id.profile);

        //CHATBOX
        chat_box = v.findViewById(R.id.chatbox);
        chat_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(num_connect.equals("1")){
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Connect to "+user_full_name)
                            .setMessage("Do you want to start chat with "+user_full_name+"?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final ProgressDialog progress = new ProgressDialog(getActivity());
                                    progress.setMessage("Connecting :) ");
                                    progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    progress.setIndeterminate(true);
                                    timeInMillis = Calendar.getInstance().getTimeInMillis();

                                    request = new StringRequest(Request.Method.POST,
                                            CONNECT_CHAT, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {


                                            try {
                                                JSONObject jsonObject = new JSONObject(response);

                                                if(jsonObject.names().get(0).equals("success")){
                                                    progress.cancel();
                                                    send_conv_notification();
                                                    Intent i = new Intent(getActivity(),EnterChatRoom.class);
                                                    i.putExtra(INTENT_FULLNAME_1,user_full_name);
                                                    i.putExtra(INTENT_OTHER_ID_1,user_id);
                                                    i.putExtra(INTENT_USERNAME_1,username_visitor);
                                                    getActivity().startActivity(i);
                                                }else{
                                                    Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                                                    progress.cancel();
                                                }
                                            }catch(JSONException e) {
                                                e.printStackTrace();
                                                progress.cancel();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            progress.cancel();
                                            Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                                        }
                                    }){
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            HashMap<String,String> hashMap = new HashMap<>();
                                            hashMap.put("user1",user_check_id);
                                            hashMap.put("user2",user_id);
                                            return hashMap;
                                        }
                                    };

                                    request.setRetryPolicy(new DefaultRetryPolicy(5000,
                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                    mRequestQueue.add(request);

                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .show();
                }
                else if(num_chat.equals("1")){
                    Intent i = new Intent(getActivity(),EnterChatRoom.class);
                    i.putExtra(INTENT_FULLNAME_1,user_full_name);
                    i.putExtra(INTENT_OTHER_ID_1,user_id);
                    i.putExtra(INTENT_USERNAME_1,username_visitor);
                    getActivity().startActivity(i);
                }
            }
        });

        //CHAT ICON
        chat_icon = v.findViewById(R.id.chat_icon);
        chat_icon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white),
                PorterDuff.Mode.SRC_IN);


        mProgressBar = v.findViewById(R.id.progress_bar_timeline);
        request_as_visitor = v.findViewById(R.id.request);

        //text_username = (TextView)v.findViewById(R.id.username);
        mNetworkImageView = v.findViewById(R.id.circular_profile);
        bio_textview = v.findViewById(R.id.bio);


        real_number_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!number_followers.equals("0")) {   //was an error
                    Intent followers = new Intent(getActivity(), Followers.class);
                    followers.putExtra(INTENT_USER_ID, user_id);
                    followers.putExtra(INTENT_USERNAME, username_visitor);
                    followers.putExtra(I_AM, "1");
                    startActivity(followers);
                }
            }
        });

        back_arrow_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        real_number_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!number_following.equals("0")){
                    Intent i = new Intent(getActivity(), Following.class);
                    i.putExtra(INTENT_USER_ID, user_id);
                    i.putExtra(INTENT_USERNAME, username_visitor);
                    i.putExtra(I_AM, "1");       //for adaptersuggetfriends
                    startActivity(i);
                }
            }
        });

        real_number_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int scrollTo = ((View) mRecyclerView.getParent().getParent()).getTop() + mRecyclerView.getTop();
                mNestedScrollView.smoothScrollTo(0, scrollTo);
            }
        });


        follow_as_visitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    follow_people();
            }
        });

        following_as_visitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    unfollow_dialog();
            }
        });

        request_as_visitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               request_dialog();
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
        data.clear();
        mRecyclerView.getRecycledViewPool().clear();
        mRequestQueue.cancelAll(server_request_tag);
        no_posts.setVisibility(View.GONE);
        no_internet.setVisibility(View.GONE);
        getUserID();


        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(v.getChildAt(v.getChildCount() - 1) != null) {

                    if (scrollY > oldScrollY) {
                        toolbar.setBackgroundColor(getResources().getColor(R.color.white));
                        back_arrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                                android.graphics.PorterDuff.Mode.MULTIPLY);
                        chat_icon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                                PorterDuff.Mode.SRC_IN);
                        mProfileText.setVisibility(View.VISIBLE);
                    }else if(scrollY == 0){
                        toolbar.setBackgroundColor(Color.TRANSPARENT);
                        back_arrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white),
                                android.graphics.PorterDuff.Mode.MULTIPLY);
                        chat_icon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white),
                                PorterDuff.Mode.SRC_IN);
                        mProfileText.setVisibility(View.GONE);
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




    private static String coolFormat(double n, int iteration) {
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : coolFormat(d, iteration+1));

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



    private void getUserID(){

        if(haveNetworkConnection()) {
            request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        user_id = jsonObject.getString("id");
                        user_full_name = jsonObject.getString("first_name") + " " + jsonObject.
                                getString("last_name");
                        user_enroll_no = jsonObject.getString("enrollment");
                        private_string = jsonObject.getString("private_ac");
                        bio_string = jsonObject.getString("bio");
                        verified = jsonObject.getString("verified");

                        if (verified.equals("1"))
                            verified_icon.setVisibility(View.VISIBLE);
                        else
                            verified_icon.setVisibility(View.GONE);

                        fullname_user.setText(user_full_name);
                        enrollment_user.setText(user_enroll_no);
                        bio_textview.setText(bio_string);

                        getFollowersAndFollowing();
                        is_follow();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "No user_id", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("enrollment", username_visitor);
                    return hashMap;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setTag(server_request_tag);
            mRequestQueue.add(request);
        }else{
            no_posts.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
        }
    }


    private void send_notification(){

        request  = new StringRequest(Request.Method.POST, URL_SEND_NOTIFICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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
                hashMap.put("title",username);
                if(private_string.equals("1")) {
                    hashMap.put("message", user_real_fullname + " has requested to follow you.");
                }else{
                    hashMap.put("message",user_real_fullname + " started following you.");
                }
                return hashMap;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    private void send_conv_notification(){

        request  = new StringRequest(Request.Method.POST, URL_SEND_NOTIFICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                hashMap.put("title",username);
                hashMap.put("message",user_real_fullname+" started conversation with you.");
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }



    //to get feed of following people
    private void getUserFeed(final int size){

            request = new StringRequest(Request.Method.POST, USER_FEED, new Response.Listener<String>() {

                @Override
                public void onResponse(String response){

                    no_internet.setVisibility(View.GONE);
                    no_posts.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);

                    if (response != null && response.length() > 0){
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

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
                    no_internet.setVisibility(View.VISIBLE);
                    no_posts.setVisibility(View.GONE);
                    // Stop refresh animation
                    mSwipeRefreshLayout.setRefreshing(false);
                    // Toast.makeText(mActivity,"Unable to connect",Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("user_id", user_id);
                    hashMap.put("post_id", Integer.toString(size));
                    hashMap.put("visitor_id", user_check_id);
                    return hashMap;
                }
            };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mRequestQueue.add(request);
    }



    private void getFollowersAndFollowing(){

        request = new StringRequest(Request.Method.POST, URL_N, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    y =Long.parseLong(jsonResponse.getString("following"));
                    z = Long.parseLong(jsonResponse.getString("followers"));

                    /*
                    num_chat = jsonResponse.getString("num_chat");
                    num_connect = jsonResponse.getString("num_connect");

                    if(num_chat.equals("1")  &&
                            !username.equals(username_visitor)|| num_connect.equals("1") &&
                            !username.equals(username_visitor)){
                        chat_icon.setVisibility(View.VISIBLE);
                    }
                    */

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
                hashMap.put("user_check_id",user_check_id);
                return hashMap;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);


    }

    private void is_follow(){

        request = new StringRequest(Request.Method.POST, IS_FOLLOW, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(user_check_id.equals(user_id)){
                        follow_as_visitor.setVisibility(View.GONE);
                        following_as_visitor.setVisibility(View.GONE);
                        request_as_visitor.setVisibility(View.GONE);
                        is_follow = 0;
                    }
                    else if(jsonObject.names().get(0).equals("success")){
                        follow_as_visitor.setVisibility(View.GONE);
                        following_as_visitor.setVisibility(View.VISIBLE);
                        request_as_visitor.setVisibility(View.GONE);
                        is_follow = 1;
                    }else if(jsonObject.names().get(0).equals("request")){
                        follow_as_visitor.setVisibility(View.GONE);
                        following_as_visitor.setVisibility(View.GONE);
                        request_as_visitor.setVisibility(View.VISIBLE);
                        is_follow =0;
                    }
                    else{
                        follow_as_visitor.setVisibility(View.VISIBLE);
                        following_as_visitor.setVisibility(View.GONE);
                        request_as_visitor.setVisibility(View.GONE);
                        is_follow = 0;
                    }

                    if(is_follow==1 && private_string.equals("1")
                            || username.equals(username_visitor)|| private_string.equals("0")) {
                        //Getting user feed
                        data.clear();
                        mRecyclerView.getRecycledViewPool().clear();
                        getUserFeed(0);
                        real_number_posts.setClickable(true);
                        real_number_following.setClickable(true);
                        real_number_followers.setClickable(true);
                        private_account_view.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }else{
                        real_number_posts.setClickable(false);
                        real_number_following.setClickable(false);
                        real_number_followers.setClickable(false);
                        mProgressBar.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setRefreshing(false);
                        private_account_view.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_check_id);
                hashMap.put("user_check_id",user_id);
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    private void follow_people(){

        timeInMillis = Calendar.getInstance().getTimeInMillis();

        request = new StringRequest(Request.Method.POST, URL_FOLLOW, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try{
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.names().get(0).equals("success")){
                        if(private_string.equals("0")){
                            follow_as_visitor.setVisibility(View.GONE);
                            following_as_visitor.setVisibility(View.VISIBLE);
                            request_as_visitor.setVisibility(View.GONE);
                        }else{
                            follow_as_visitor.setVisibility(View.GONE);
                            following_as_visitor.setVisibility(View.GONE);
                            request_as_visitor.setVisibility(View.VISIBLE);
                        }
                        send_notification();
                    }else{
                        Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                    }
                }catch(JSONException e) {
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
                hashMap.put("user_id",user_check_id);
                hashMap.put("following_id",user_id);
                hashMap.put("enrollment",username);
                hashMap.put("fullname",user_real_fullname);
                hashMap.put("time_ago",Long.toString(timeInMillis));
                if(private_string.equals("0")){
                    hashMap.put("private_ac","0");
                }
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);

    }

    private void request_dialog(){

        final String URL_UNFOLLOW = "http://13.233.234.79/unfollow_people.php";

        final SpannableStringBuilder sb = new SpannableStringBuilder("Are you sure " +
                "you want to cancel request to follow "+user_full_name+" ?");

        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sb.setSpan(bss, 50, sb.length()-2, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // making fullname characters Bold


        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cancel request")
                .setMessage(sb)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        request = new StringRequest(Request.Method.POST, URL_UNFOLLOW, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                request_as_visitor.setVisibility(View.GONE);
                                following_as_visitor.setVisibility(View.GONE);
                                follow_as_visitor.setVisibility(View.VISIBLE);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String,String> hashMap = new HashMap<>();
                                hashMap.put("user_id",user_check_id);
                                hashMap.put("following_id",user_id);
                                hashMap.put("username",username);
                                return hashMap;
                            }
                        };
                        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        mRequestQueue.add(request);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void unfollow_dialog(){

        final String URL_UNFOLLOW = "http://13.233.234.79/unfollow_people.php";

        final SpannableStringBuilder sb = new SpannableStringBuilder("Are you sure " +
                "you want to unfollow "+user_full_name+" ?");

        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sb.setSpan(bss, 34, sb.length()-2, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // making fullname characters Bold


        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Unfollow")
                .setMessage(sb)
                .setPositiveButton(R.string.unfollow, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        request = new StringRequest(Request.Method.POST, URL_UNFOLLOW, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                onResume();

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String,String> hashMap = new HashMap<>();
                                hashMap.put("user_id",user_check_id);
                                hashMap.put("following_id",user_id);
                                hashMap.put("username",username);
                                return hashMap;
                            }
                        };
                        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        mRequestQueue.add(request);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
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
