package com.abhigam.www.foodspot;

/**
 * Created by sourabhzalke on 26/03/17.
 */
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;


public class FragmentHome extends Fragment {


    public Context mActivity;
    //URL's
    private static final String URL="http://13.233.234.79/user_id.php";
    private static final String URL_SEND_TOKEN = "http://13.233.234.79/update_user_id_fcm_token.php";
    //keys
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
    private static final String PREF_CHAT_BADGE = "pref_chat_badge";
    private static final String IS_CHAT = "http://13.233.234.79/is_chat.php";
    private static final String IS_BANNERS = "http://13.233.234.79/is_banners.php";


    private static String username,user_id,first_name,last_name,full_name,user_branch;
    private LinearLayout ic_inbox,ic_person_add,ic_bookmark;
    private ImageView no_posts,no_internet;
    private WrapContentLinearLayoutManager linearLayoutManager;
    //server_request
    private StringRequest request;
    private RequestQueue mRequestQueue;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AdapterHomeFeed mAdapterHomeFeed;
    private ProgressBar mProgressBar;
    private List<DataRecycler> data = new ArrayList<>();
    private NestedScrollView mNestedScrollView;
    CircleIndicator indicator;
    private String URL_USER_DETAILS = "http://13.233.234.79/user_id.php";

    private TextView app_name;
    private LinearLayout the_three;

    //banners
    ViewPager viewPager;
    List<DataRecycler> bannerDataRecycler;
    ViewPagerAdapter viewPagerAdapter;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
    private String URL_BANNERS = "http://13.233.234.79/get_banners.php";
    private int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 112;
    private Spinner mSpinner;
    private String[] filter =  new String[]{"Following"};
    private LinearLayout all_post_btn,my_branch_btn,following_btn;
    private TextView all_post_text,my_branch_text,following_text;
    private String URL_FOLLOW = "http://13.233.234.79/home_feed.php";
    private Toolbar mToolbar;

    private static final String REQUEST_TAG = "request_tag";

    private int is_home_feed =1; //variable for identifying if following feed or branch feed
                                 //and all posts feed

    private String is_blocked;
    private String is_chat_badge;
    private ImageView chat_image,animation;
    private FrameLayout banner_back;

    public static FragmentHome newInstance() {
        FragmentHome fragment = new FragmentHome();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("sourabh1","onCreate()");

        //initialising requestqueue
        mRequestQueue = Volley.newRequestQueue(mActivity);

        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            //hiding bottomsheet onclick
            // if(mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            //   mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else{
            external_runtime_permissions();
        }

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mActivity = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_fragment_home, container, false);

        //getting username and user_id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        username = prefs.getString(PREF_USER_NAME,"");
        user_id = prefs.getString(PREF_USER_ID,"");
        user_branch = prefs.getString(PREF_BRANCH,"");

        Window window = getActivity().getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        }

        all_post_btn = v.findViewById(R.id.all_posts_btn);
        my_branch_btn = v.findViewById(R.id.my_branch_btn);
        following_btn = v.findViewById(R.id.following_btn);
        all_post_text = v.findViewById(R.id.all_post_text);
        my_branch_text = v.findViewById(R.id.my_branch_text);
        following_text = v.findViewById(R.id.following_text);
        mToolbar = v.findViewById(R.id.toolbar_top);
        app_name = v.findViewById(R.id.app_name);
        banner_back = v.findViewById(R.id.banner_back);
        the_three = v.findViewById(R.id.the_three);


        if(user_branch.equals("Information Technology")){
            my_branch_text.setText("IT");
        }else if(user_branch.equals("Electronics and Telecommunications")){
            my_branch_text.setText("ET");
        }else if(user_branch.equals("Bio-Technology")){
            my_branch_text.setText("Bio Tech");
        }else if(user_branch.equals("Computer Science")){
            my_branch_text.setText("CSE");
        }else
        my_branch_text.setText(user_branch);

        app_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrolltoTop();
            }
        });

        all_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all_post_btn.setBackground(getActivity().getResources().
                        getDrawable(R.drawable.home_buttons));
                all_post_text.setTextColor(getActivity().getResources().getColor(R.color.white));
                following_btn.setBackground(getActivity().getResources().
                        getDrawable(R.drawable.home_buttons_1));
                following_text.setTextColor(getActivity().getResources().getColor(R.color.gray));
                my_branch_btn.setBackground(getActivity().getResources().
                        getDrawable(R.drawable.home_buttons_1));
                my_branch_text.setTextColor(getActivity().getResources().getColor(R.color.gray));
                URL_FOLLOW = "http://13.233.234.79/all_posts_home_feed.php";
                is_home_feed = 0;
                refreshLayout();
            }
        });

        following_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all_post_btn.setBackground(getActivity().getResources().
                          getDrawable(R.drawable.home_buttons_1));
                all_post_text.setTextColor(getActivity().getResources().getColor(R.color.gray));
                following_btn.setBackground(getActivity().getResources().
                        getDrawable(R.drawable.home_buttons));
                following_text.setTextColor(getActivity().getResources().getColor(R.color.white));
                my_branch_btn.setBackground(getActivity().getResources().
                        getDrawable(R.drawable.home_buttons_1));
                my_branch_text.setTextColor(getActivity().getResources().getColor(R.color.gray));
                URL_FOLLOW = "http://13.233.234.79/home_feed.php";
                is_home_feed = 1;
                refreshLayout();
            }
        });

        my_branch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all_post_btn.setBackground(getActivity().getResources().
                        getDrawable(R.drawable.home_buttons_1));
                all_post_text.setTextColor(getActivity().getResources().getColor(R.color.gray));
                following_btn.setBackground(getActivity().getResources().
                        getDrawable(R.drawable.home_buttons_1));
                following_text.setTextColor(getActivity().getResources().getColor(R.color.gray));
                my_branch_btn.setBackground(getActivity().getResources().
                        getDrawable(R.drawable.home_buttons));
                my_branch_text.setTextColor(getActivity().getResources().getColor(R.color.white));
                URL_FOLLOW = "http://13.233.234.79/my_branch_home_feed.php";
                is_home_feed = 0;
                refreshLayout();
            }
        });


        ic_person_add =v.findViewById(R.id.person_add);
        ic_person_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity,SuggestFriends.class);
                mActivity.startActivity(i);
            }
        });

        /*
        ic_bookmark = v.findViewById(R.id.ic_bookmark);
        ic_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),WishList.class);
                getActivity().startActivity(i);
            }
        });
        */

        ic_inbox = v.findViewById(R.id.inbox);
        ic_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(getActivity()).edit();
                editor.putString(PREF_CHAT_BADGE, "0");
                editor.commit();
                Intent i = new Intent(getActivity(),Vartalap.class);
                getActivity().startActivity(i);
            }
        });


        chat_image = v.findViewById(R.id.chat_image);


        no_posts = v.findViewById(R.id.no_posts);
        no_internet = v.findViewById(R.id.no_internet);
        mProgressBar = v.findViewById(R.id.progress_bar);
        mRecyclerView = v.findViewById(R.id.recycler_home_feed);
        mRecyclerView.setNestedScrollingEnabled(false);

        /*
        //toolbar bar icons
        ic_chatroom = v.findViewById(R.id.ic_chatroom);
        ic_chatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity,EnterChatRoom.class);
                getActivity().startActivity(i);
            }
        });
        */


        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!user_id.equals("")) {
                    //viewPager unwanted behaviour
                    if(timer!=null)
                    timer.cancel();
                    mRecyclerView.clearFocus();
                    mProgressBar.setVisibility(View.VISIBLE);
                    data.clear();
                    mRecyclerView.getRecycledViewPool().clear();
                    //causing duplicate in items
                    mAdapterHomeFeed.notifyDataSetChanged(); //important for IndexOutOfBoundException because clearing data and then instant Scrolling
                    getHomeFeed(0,user_branch);
                  //  getBanners();
                    is_banners();
                }else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        //sending data to Adapter
        mAdapterHomeFeed = new AdapterHomeFeed(getActivity(),data);
        //setting layout manager
        linearLayoutManager = new WrapContentLinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //setting the adapter
        mRecyclerView.setAdapter(mAdapterHomeFeed);
        mRecyclerView.setItemAnimator(null);

        mRecyclerView.clearFocus();
        mProgressBar.setVisibility(View.VISIBLE);
        data.clear();
        mRecyclerView.getRecycledViewPool().clear();
        //causing duplicate in items
        mAdapterHomeFeed.notifyDataSetChanged(); //important for IndexOutOfBoundException because clearing data and then instant Scrolling
        getHomeFeed(0,user_branch);
       // getBanners();
        is_banners();

        viewPager = v.findViewById(R.id.viewPager);
        indicator =  v.findViewById(R.id.indicator);
        mNestedScrollView = v.findViewById(R.id.nestedScrollview);

       // requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);

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

                if(v.getChildAt(v.getChildCount() - 1) != null) {
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
                                        mAdapterHomeFeed.notifyItemInserted(data.size() - 1);
                                        getHomeFeed(data.size(),user_branch);
                                    }
                                }
//                        Load Your Data
                            }
                    }
                }
            }
        });

        getUserIDFirst(username);

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

                boolean a = isLastItemDisplaying(mRecyclerView);
                if(a)
                {
                    if (data.size() > 0) {
                        if (data.get(data.size() - 1) != null) {
                            data.add(null);
                            Log.d("sourabh", Integer.toString(linearLayoutManager.findLastCompletelyVisibleItemPosition()));
                            mAdapterHomeFeed.notifyItemInserted(data.size() - 1);
                        }
                    }
                    getHomeFeed(data.size());
                }
            }
        });

        */

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return v;
    }

    private void getUserIDFirst(final String enrollment){

        request = new StringRequest(Request.Method.POST, URL_USER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            is_blocked = jsonObject.getString("block");
                            if(is_blocked.equals("1")) {
                                SaveSharedPreference.clearUserName(getActivity().getApplicationContext());
                                Intent i = new Intent(getActivity(), FirstScreen.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
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
        request.setTag(REQUEST_TAG);
        mRequestQueue.add(request);
    }


    private void external_runtime_permissions(){

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if(shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
                //set to never ask again
            }else{
                if(ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                }else{
                    //set to never ask again
                    //do something here
                }
            }

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique
        }
    }

    private void getBanners(){

        bannerDataRecycler = new ArrayList<>();

        request = new StringRequest(Request.Method.POST, URL_BANNERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("banners");

                    for(int i=0;i<jsonArray.length();++i){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        DataRecycler dataRecycler = new DataRecycler();
                        dataRecycler.setBannerUrl("http://13.233.234.79/banners/"
                                +jsonObject.getString("banner_name")
                                +".jpg");
                        bannerDataRecycler.add(dataRecycler);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                viewPagerAdapter = new ViewPagerAdapter(bannerDataRecycler, getActivity());
                viewPager.setAdapter(viewPagerAdapter);
                indicator.setViewPager(viewPager);
                viewPagerAdapter.registerDataSetObserver(indicator.getDataSetObserver());
                banner_back.setVisibility(View.VISIBLE);

                setTimer();



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(REQUEST_TAG);
        mRequestQueue.add(request);
    }


    public void setTimer(){
         /*After setting the adapter use the timer */
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == bannerDataRecycler.size()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    //to get feed of following people
    private void getHomeFeed(final int size,final String branch){

       if(haveNetworkConnection()) {
           request = new StringRequest(Request.Method.POST, URL_FOLLOW, new Response.Listener<String>() {


               @Override
               public void onResponse(String response) {

                   String private_account_check = "0", is_follow_check = "0";

                   try {
                       JSONObject jsonResponse = new JSONObject(response);
                       mProgressBar.setVisibility(View.GONE);
                       // Stop refresh animation
                       mSwipeRefreshLayout.setRefreshing(false);

                       no_posts.setVisibility(View.GONE);
                       no_internet.setVisibility(View.GONE);

                       JSONArray jsonArray = jsonResponse.getJSONArray("posts");

                       if (data.size() > 0) {
                           //remove progress item
                           if (data.get(data.size() - 1) == null) {  //condition for not removing item only remove progressBar
                               data.remove(data.size() - 1);
                               mAdapterHomeFeed.notifyItemRemoved(data.size());
                           }
                       }

                       for (int i = 0; i < jsonArray.length(); ++i) {
                           JSONObject jsonObject = jsonArray.getJSONObject(i);
                           DataRecycler post_data = new DataRecycler();
                           post_data.post_id = jsonObject.getString("post_id");
                           post_data.user_id = jsonObject.getString("user_id");
                           post_data.enrollment = jsonObject.getString("enrollment");
                           post_data.fullname = jsonObject.getString("first_name")
                                   + " " + jsonObject.getString("last_name");
                           post_data.time_ago = jsonObject.getString("time_ago");
                           post_data.caption = jsonObject.getString("caption");
                           post_data.is_liked = jsonObject.getString("is_liked");
                           post_data.likes_number = jsonObject.getString("likes_number");
                           post_data.views_number = jsonObject.getString("views");
                           post_data.is_image = jsonObject.getString("is_image");
                           post_data.verified = jsonObject.getString("verified");
                           post_data.is_saved = jsonObject.getString("is_saved");
                           post_data.comments_number = jsonObject.getString("@comments_number" +
                                   " := (SELECT COUNT(*) from post_comments where post_id = i.post_id)");
                           if (is_home_feed == 0) {
                               private_account_check = jsonObject.getString("private_ac");
                               is_follow_check = jsonObject.getString("is_follow");
                               if (private_account_check.equals("1") && is_follow_check.equals("1")
                                       || private_account_check.equals("0")) {
                                   post_data.first_load = "1";
                                   data.add(post_data);
                               }
                           } else {
                               post_data.first_load = "1";
                               data.add(post_data);
                           }
                       }

                       if (data.size() == 0) {
                           no_posts.setVisibility(View.VISIBLE);
                           no_internet.setVisibility(View.GONE);
                           //condition for no posts
                       }

                       mAdapterHomeFeed.notifyDataSetChanged();

                   } catch (JSONException e) {
                       e.printStackTrace();
                       mProgressBar.setVisibility(View.GONE);
                       //Stop refresh animation
                       mSwipeRefreshLayout.setRefreshing(false);
                       if (data.size() == 0) {
                           no_internet.setVisibility(View.GONE);
                           no_posts.setVisibility(View.VISIBLE);
                           //condition for no posts
                       }
                       if (data.size() > 0){
                           if (data.get(data.size() - 1) == null){ //condition for not removing item only remove progressBar
                               //remove progress item
                               data.remove(data.size() - 1);
                               mAdapterHomeFeed.notifyItemRemoved(data.size());
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
                           mAdapterHomeFeed.notifyItemRemoved(data.size());
                       }
                   }
                   // Stop refresh animation
                   mSwipeRefreshLayout.setRefreshing(false);

               }
           }) {
               @Override
               protected Map<String, String> getParams() throws AuthFailureError {
                   HashMap<String, String> hashMap = new HashMap<>();
                   hashMap.put("user_id", user_id);
                   hashMap.put("post_id", Integer.toString(size));
                   hashMap.put("branch", branch);
                   return hashMap;
                   //add null , so the adapter will check view_type and show progress bar at bottom
               }
           };
           request.setRetryPolicy(new DefaultRetryPolicy(5000,
                   DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                   DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
           request.setTag(REQUEST_TAG);
           mRequestQueue.add(request);
       }else{
           mProgressBar.setVisibility(View.GONE);
           mSwipeRefreshLayout.setRefreshing(false);
           no_posts.setVisibility(View.GONE);
           no_internet.setVisibility(View.VISIBLE);
       }
    }

    @Override
    public void onPause() {
        super.onPause();
        //find if chat notification is their
        if(timer !=null)
        timer.cancel();
        Log.i("sourabh","onPause()");
        mRequestQueue.cancelAll(REQUEST_TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll(REQUEST_TAG);
        Log.i("sourabhz","onDestroy()");

     //   if(mBroadcastReceiver != null){
       //     Intent i = new Intent(getActivity(),GPS_Service.class);
         //   getActivity().stopService(i);
         //   getActivity().unregisterReceiver(mBroadcastReceiver);
         //   mBroadcastReceiver = null;
      //  }
    }



    @Override
    public void onResume() {
        super.onResume();
        is_chat();
        //getting username and user_id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        is_chat_badge = prefs.getString(PREF_CHAT_BADGE,"");
        if(is_chat_badge.equals("1")){
            chat_image.setVisibility(View.VISIBLE);
        }else{
            chat_image.setVisibility(View.GONE);
        }
    }


    private void refreshLayout(){
        mRequestQueue.cancelAll(REQUEST_TAG);
        mRecyclerView.clearFocus();
        data.clear();
        mRecyclerView.getRecycledViewPool().clear();
        mAdapterHomeFeed.notifyDataSetChanged();
        mProgressBar.setVisibility(View.VISIBLE);
        getHomeFeed(0,user_branch);
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

    private void is_chat() {

        request = new StringRequest(Request.Method.POST, IS_CHAT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(getActivity()).edit();
                        editor.putString(PREF_CHAT_BADGE,"1");
                        editor.commit();
                        chat_image.setVisibility(View.VISIBLE);
                    }else{
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(getActivity()).edit();
                        editor.putString(PREF_CHAT_BADGE,"0");
                        editor.commit();
                        chat_image.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                chat_image.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_id", user_id);
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    private void is_banners() {

        request = new StringRequest(Request.Method.GET, IS_BANNERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
                        getBanners();
                    }else{
                        banner_back.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                chat_image.setVisibility(View.GONE);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }


}
