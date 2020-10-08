package com.abhigam.www.foodspot;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.Zoomy;
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
import com.stfalcon.chatkit.messages.MessageInput;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sourabhzalke on 12/03/18.
 */

public class PostDetailsFragment extends Fragment implements InterfaceComment{



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
    private String username,user_id,user_fullname;
    private String post_id,post_user_id,post_enrollment,post_fullname,post_timeago,
            post_caption,likes_n,views_n,is_liked,is_image,is_verfied,is_saved;
    private int x =0;
    private int interface_postion=-1;
    private String interface_comment_id="";

    //intent_tags
    private static final String INTENT_POST_ID = "com.abhigam.www.foodspot.POST_ID";
    private static final String INTENT_USER_ID = "com.abhigam.www.foodspot.USER_ID";
    private static final String INTENT_ENROLLMENT = "com.abhigam.www.foodspot.ENROLLMENT";
    private static final String INTENT_FULLNAME = "com.abhigam.www.foodspot.FULLNAME";
    private static final String INTENT_TIMEAGO = "com.abhigam.www.foodspot.TIMEAGO";
    private static final String INTENT_CAPTION = "com.abhigam.www.foodspot.CAPTION";
    private static final String INTENT_IS_LIKED = "com.abhigam.www.foodspot.IS_LIKED";
    private static final String INTENT_VIEWS_NUMBER = "com.abhigam.www.foodspot.VIEWS_NUMBER";
    private static final String INTENT_LIKES_NUMBER = "com.abhigam.www.foodspot.LIKES_NUMBER";
    private static final String INTENT_IS_IMAGE = "com.abhigam.www.foodspot.IS_IMAGE";
    private static final String INTENT_IS_VERIFIED = "com.abhigam.www.foodspot.IS_VERIFIED";
    private static final String INTENT_IS_SAVED = "com.abhigam.www.foodspot.IS_SAVED";

    private String URL_ADD_TO_LIKED = "http://13.233.234.79/add_to_liked.php";
    private String URL_REMOVE_FROM_LIKED = "http://13.233.234.79/remove_from_liked.php";

    private CircleImageView mCircleImageView;
    private TextView fullname_view,enrollment_view,time_ago_view,full_n_view,caption,
            likes_number_view,views_number_view,like_text_view;
    private ImageView mPostImage,LikeImage,ic_more_vert,is_verified_icon;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String URL_INCREASE_VIEWS = "http://13.233.234.79/increase_views.php";
    private final static String URL_SEND_NOTIFICATION = "http://13.233.234.79/send_notification.php";
    private final static String URL_COMMENT_NOTIFICATION = "http://13.233.234.79/comment_notification.php";
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private LinearLayout likes_view,save_view,comment_view;
    MessageInput mMessageInput;
    private TextView add_comment;
    private View background;
    private String inputEditText;
    private View viewParent;

    private String URL_INSERT_REVIEW= "http://13.233.234.79/insert_reviews.php";
    private String URL_GET_REVIEWS = "http://13.233.234.79/get_reviews.php";
    private String URL_UPDATE_COMMENT = "http://13.233.234.79/update_comment.php";
    private List<DataRecycler> data = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private AdapterComments mAdapterComments;
    private TextView num_comments;
    private String open_comment ="0";
    private NestedScrollView mNestedScrollView;
    private static final String INTENT_OPEN_COMMENT = "com.abhigam.www.foodspot.OPEN_COMMENT";
    LinearLayoutManager linearLayoutManager;
    private String INTENT_URL_IMAGE = "intent_url_post_image";
    private String INTENT_POST_FULLNAME = "intent_post_full_name";
    private static final String URL_LIKED_NOTIFICATION = "http://13.233.234.79/liked_notification_inside.php";

    private static final String USERNAME_VISITOR = "visitor_username";

    private String URL_ADD_TO_NOTEBOOK = "http://13.233.234.79/add_to_notebook.php";
    private String URL_DELETE_POST = "http://13.233.234.79/delete_posts.php";
    private static final String URL_REMOVE_FROM_NOTEBOOK = "http://13.233.234.79/remove_from_notebook.php";
    private ImageView love_lined,love_filled;
    private TextView text_save;

    DataRecycler mDataRecycler = new DataRecycler();
    private static final String REQUEST_TAG = "request_tag";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //getting username and user_id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        username = prefs.getString(PREF_USER_NAME,"");
        user_id = prefs.getString(PREF_USER_ID,"");
        user_fullname = prefs.getString(PREF_FULLNAME,"");
        mRequestQueue = Volley.newRequestQueue(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_post_details, container, false);

        viewParent = v;
        mCircleImageView = v.findViewById(R.id.circular_profile);
        fullname_view = v.findViewById(R.id.fullname);
        enrollment_view = v.findViewById(R.id.enroll);
        time_ago_view = v.findViewById(R.id.time_ago);
        full_n_view = v.findViewById(R.id.full_n);
        mPostImage = v.findViewById(R.id.post_image);
        caption = v.findViewById(R.id.caption);
        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        views_number_view = v.findViewById(R.id.views_number);
        likes_number_view = v.findViewById(R.id.likes_number);
        like_text_view = v.findViewById(R.id.like_text);
        LikeImage = v.findViewById(R.id.like_image);
        likes_view = v.findViewById(R.id.likes_view);
        save_view = v.findViewById(R.id.save_it);
        mMessageInput = v.findViewById(R.id.input);
        is_verified_icon = v.findViewById(R.id.ic_verified);
        comment_view = v.findViewById(R.id.comment_it);
        text_save = v.findViewById(R.id.text_save);
        love_filled = v.findViewById(R.id.love_filled);
        love_lined = v.findViewById(R.id.love_lined);


        comment_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // background.setVisibility(View.VISIBLE);
                mMessageInput.getInputEditText().requestFocus();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                assert inputMethodManager != null;
                inputMethodManager.toggleSoftInputFromWindow(
                        mMessageInput.getInputEditText().getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);
            }
        });


        // add_comment = v.findViewById(R.id.add_comment);
        //   background = v.findViewById(R.id.background);
        num_comments = v.findViewById(R.id.num_comments);
        mNestedScrollView = v.findViewById(R.id.nestedScrollview);
        ic_more_vert = v.findViewById(R.id.more_vert);
        ic_more_vert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(getActivity(), v);
                menu.getMenu().add("Share");
                if(post_user_id.equals(user_id))
                    menu.getMenu().add("Delete");
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Report")){

                        }else if(item.getTitle().equals("Share")){
                            if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(),
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                if (is_image.equals("1")) {
                                    Uri share_uri = getImageUri(getActivity(), mDataRecycler.getBitmap());
                                    String text_share = post_caption +
                                            "\n- " + post_fullname +
                                            " For more visit: www.crony.co.in";
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, text_share);
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, share_uri);
                                    shareIntent.setType("image/jpg");
                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    getActivity().startActivity(Intent.createChooser(shareIntent, "Share"));


                                } else {
                                    String text_share = post_caption+
                                            "\n- " + post_fullname +
                                            "\n For more visit: www.crony.co.in";
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, text_share);
                                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Crony Post");
                                    shareIntent.setType("text/plain");
                                    getActivity().startActivity(Intent.createChooser(shareIntent, "Share"));
                                }

                            } else {

                                AlertDialog.Builder builder;
                                builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Permission Required")
                                        .setMessage("To use this feature we require permission to write in your " +
                                                "External storage. Go to settings to change it.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();
                            }
                        }else{

                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Delete post")
                                    .setMessage("Are you sure you want to delete this post?")
                                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            delete_post(post_id);
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .show();
                        }
                        return true;
                    }
                });
                menu.show();
            }
        });

        Zoomy.Builder builder = new Zoomy.Builder(getActivity());

        builder.target(mPostImage)
                .tapListener(new TapListener() {
                    @Override
                    public void onTap(View v) {
                        Intent i = new Intent(getActivity(),ImageDownloadActivity.class);
                        i.putExtra(INTENT_POST_FULLNAME,post_fullname);
                        i.putExtra(INTENT_URL_IMAGE,"http://13.233.234.79/uploads/post_images/"
                                +post_user_id
                                +"_"+post_timeago+".jpg");
                        getActivity().startActivity(i);
                    }
                }).register();

        post_id = getActivity().getIntent().getStringExtra(INTENT_POST_ID);
        post_user_id = getActivity().getIntent().getStringExtra(INTENT_USER_ID);
        post_enrollment = getActivity().getIntent().getStringExtra(INTENT_ENROLLMENT);
        post_fullname = getActivity().getIntent().getStringExtra(INTENT_FULLNAME);
        post_timeago = getActivity().getIntent().getStringExtra(INTENT_TIMEAGO);
        post_caption = getActivity().getIntent().getStringExtra(INTENT_CAPTION);
        likes_n = getActivity().getIntent().getStringExtra(INTENT_LIKES_NUMBER);
        views_n = getActivity().getIntent().getStringExtra(INTENT_VIEWS_NUMBER);
        is_liked = getActivity().getIntent().getStringExtra(INTENT_IS_LIKED);
        is_image = getActivity().getIntent().getStringExtra(INTENT_IS_IMAGE);
        is_verfied = getActivity().getIntent().getStringExtra(INTENT_IS_VERIFIED);
        is_saved = getActivity().getIntent().getStringExtra(INTENT_IS_SAVED);


        likes_number_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),LikedUsers.class);
                i.putExtra(INTENT_POST_ID,post_id);
                getActivity().startActivity(i);
            }
        });

        //called twice
        increase_views();

        if(is_verfied.equals("1")){
            is_verified_icon.setVisibility(View.VISIBLE);
        }else{
            is_verified_icon.setVisibility(View.GONE);
        }

        mRecyclerView = v.findViewById(R.id.recycler_comments);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapterComments = new AdapterComments(getActivity(), data);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapterComments);

        /*
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View x) {
                background.setVisibility(View.GONE);
                mCircleImageView.requestFocus();
                hideSoftKeyboard(getActivity(),v);
                mMessageInput.getInputEditText().clearFocus();
                mNestedScrollView.requestFocus();
            }
        });
        */

        open_comment = getActivity().getIntent().getStringExtra(INTENT_OPEN_COMMENT);
        if (open_comment.equals("1")) {
            // background.setVisibility(View.VISIBLE);
            mMessageInput.getInputEditText().requestFocus();
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.toggleSoftInputFromWindow(
                    mMessageInput.getInputEditText().getApplicationWindowToken(),
                    InputMethodManager.SHOW_FORCED, 0);
        }


        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), VisitorProfile.class);
                i.putExtra(USERNAME_VISITOR, post_enrollment);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                getActivity().startActivity(i);
            }
        });

        save_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(is_saved)==0) {
                    add_to_notebook(user_id, post_id);
                }else{
                    remove_from_notebook(user_id, post_id);
                }

            }
        });


        fullname_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), VisitorProfile.class);
                i.putExtra(USERNAME_VISITOR, post_enrollment);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                getActivity().startActivity(i);
            }
        });

        enrollment_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), VisitorProfile.class);
                i.putExtra(USERNAME_VISITOR, post_enrollment);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                getActivity().startActivity(i);
            }
        });


/*
        add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background.setVisibility(View.VISIBLE);
                mMessageInput.setVisibility(View.VISIBLE);
                mMessageInput.getInputEditText().requestFocus();
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                assert inputMethodManager != null;
                inputMethodManager.toggleSoftInputFromWindow(
                        mMessageInput.getInputEditText().getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);
            }
        });
        */

        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        mMessageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                inputEditText = mMessageInput.getInputEditText().getText().toString();
                if(x==0)
                insert_review(user_id,post_id,inputEditText,Long.toString(Calendar.getInstance().getTimeInMillis()));
                else{
                    editComment(interface_comment_id,mMessageInput.getInputEditText().getText().toString(),
                            interface_postion);
                    x=0;
                }
                return true;
            }
        });

        mMessageInput.getInputEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        if (is_image.equals("1")) {
            mPostImage.setBackground(getActivity().getResources().getDrawable(R.drawable.dark_gradient));
            mPostImage.setVisibility(View.VISIBLE);
        }else {
            mPostImage.setVisibility(View.GONE);
        }


        long time = Long.parseLong(post_timeago);
        long now = System.currentTimeMillis();

        CharSequence min = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS);
        time_ago_view.setText(min);

        fullname_view.setText(post_fullname);
        enrollment_view.setText(post_enrollment);
        full_n_view.setText(post_fullname+"'s Post");
        caption.setText(post_caption);
        likes_number_view.setText(likes_n+" likes ∙ ");
        views_number_view.setText(views_n+" views");

        if(Integer.parseInt(is_liked)==0){
            LikeImage.setColorFilter(ContextCompat.getColor(getActivity(), R.color.gray),
                    PorterDuff.Mode.SRC_IN);
            like_text_view.setTextColor(getActivity().getResources().getColor(R.color.gray));
            like_text_view.setText("Like");
        }else{
            LikeImage.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            like_text_view.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            like_text_view.setText("Liked");
          }

        if (Integer.parseInt(is_saved) == 0) {
            love_lined.setVisibility(View.VISIBLE);
            love_filled.setVisibility(View.GONE);
            text_save.setTextColor(getActivity().getResources().getColor(R.color.gray));
            text_save.setText("Save");
        } else {
            love_filled.setVisibility(View.VISIBLE);
            love_lined.setVisibility(View.GONE);
            text_save.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            text_save.setText("Saved");
        }


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
            }
        });

        likes_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(is_liked)==0) {
                    add_to_liked(user_id, post_id);
                }else{
                    remove_from_liked(user_id, post_id);
                }
            }
        });

        return v;
    }

    private void send_notification(final String inputEditText){

        mStringRequest  = new StringRequest(Request.Method.POST, URL_SEND_NOTIFICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",post_user_id);
                hashMap.put("title",username);
                hashMap.put("message",user_fullname+" has commented on your post, '"+inputEditText+"...'.");
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void send_comment_notification(final String inputEditText){

        mStringRequest  = new StringRequest(Request.Method.POST, URL_COMMENT_NOTIFICATION,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response){

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",post_user_id);
                hashMap.put("sender_id",user_id);
                hashMap.put("thing",inputEditText);
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                hashMap.put("post_id",post_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, UUID.randomUUID().toString() + ".jpg", "drawing");
        return Uri.parse(path);
    }

    public void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mCircleImageView.setImageResource(R.drawable.gray);
        Glide
                .with(getActivity())
                .load("http://13.233.234.79/uploads/profile_pic/" +post_enrollment+ "_profile.jpg")
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                .skipMemoryCache(true)
                .centerCrop()
                .into(new SimpleTarget<Bitmap>(300, 300) { //width and height
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        mCircleImageView.setImageBitmap(resource);
                    }
                });

        final String URL = "http://13.233.234.79/uploads/post_images/"
                +post_user_id
                +"_"+post_timeago+".jpg";

        mPostImage.setImageResource(R.drawable.gray);

        Glide
                .with(getActivity())
                .load(URL)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(mPostImage.getMaxWidth(),
                        mPostImage.getMaxHeight()) { //width and height
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        mPostImage.setImageBitmap(resource);
                        mDataRecycler.setBitmap(resource);
                    }
                });

        mRecyclerView.clearFocus();

        data.clear();
        mRecyclerView.getRecycledViewPool().clear();
        mAdapterComments.notifyDataSetChanged();
        if(mAdapterComments!=null) {
            mAdapterComments.notifyDataSetChanged();
        }
        getReviews();

    }

    public void getReviews(){

        mStringRequest = new StringRequest(Request.Method.POST, URL_GET_REVIEWS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                mSwipeRefreshLayout.setRefreshing(false);
                try {

                    JSONArray jsonArray = new JSONArray(response);

                    for(int i =0;i<jsonArray.length();++i){
                        DataRecycler dataRecycler = new DataRecycler();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        dataRecycler.comment_id = jsonObject.getString("comment_id");
                        dataRecycler.post_id = jsonObject.getString("post_id");
                        dataRecycler.reviews = jsonObject.getString("reviews");
                        dataRecycler.enrollment = jsonObject.getString("enrollment");
                        dataRecycler.fullname = jsonObject.getString("first_name")+" "+
                                jsonObject.getString("last_name");
                        dataRecycler.time_ago = jsonObject.getString("time_ago");
                        dataRecycler.edited = jsonObject.getString("edited");
                        dataRecycler.post_username = post_enrollment;
                        dataRecycler.is_liked = jsonObject.getString("is_liked");
                        dataRecycler.likes_number = jsonObject.getString("likes_number");
                        dataRecycler.number_replies = jsonObject.getString("@replies_number := (SELECT COUNT(*) " +
                                "from comment_replies where comment_id = post_comments.comment_id)");
                        dataRecycler.user_id = jsonObject.getString("id");
                        dataRecycler.verified = jsonObject.getString("verified");
                        dataRecycler.first_load = "1";
                        data.add(dataRecycler);

                        if(open_comment.equals("1")) {
                            open_comment = "0";
                           // mMessageInput.getInputEditText().requestFocus();
                        }

                    }

                    num_comments.setText(jsonArray.length()+" comments ∙ ");

                    mAdapterComments.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("post_id",post_id);
                hashMap.put("user_id",user_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mStringRequest.setTag(REQUEST_TAG);
        mRequestQueue.add(mStringRequest);

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

    @Override
    public void onPause() {
        super.onPause();
        mRequestQueue.cancelAll(REQUEST_TAG);
    }

    public void increase_views(){

        mStringRequest = new StringRequest(Request.Method.POST, URL_INCREASE_VIEWS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                    }
                } catch (JSONException e) {
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
                hashMap.put("post_id",post_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    public void add_to_liked(final String user_id,final String post_id){

        mStringRequest = new StringRequest(Request.Method.POST, URL_ADD_TO_LIKED, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success") && is_liked.equals("0")){
                        is_liked ="1";
                        likes_n = Integer.toString(Integer.
                                parseInt(likes_n)
                                        +1);
                        LikeImage.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                                android.graphics.PorterDuff.Mode.SRC_IN);
                        like_text_view.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                        like_text_view.setText("Liked");
                        likes_number_view.setText(likes_n+" likes ∙ ");
                        //checking if same username
                        if(!post_enrollment.equals(username)) {
                            send_liked_notification();
                            send_liked_notification_inside();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("post_id",post_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    public void remove_from_liked(final String user_id,final String post_id){

        mStringRequest = new StringRequest(Request.Method.POST, URL_REMOVE_FROM_LIKED, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success") && is_liked.equals("1")){
                        is_liked ="0";
                        likes_n = Integer.toString(Integer.
                                parseInt(likes_n)
                                -1);
                        LikeImage.setColorFilter(ContextCompat.getColor(getActivity(), R.color.gray),
                                PorterDuff.Mode.SRC_IN);
                        like_text_view.setTextColor(getActivity().getResources().getColor(R.color.gray));
                        like_text_view.setText("Like");
                        likes_number_view.setText(likes_n+" likes ∙ ");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("post_id",post_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    public void insert_review(final String user_id, final String post_id, final String inputEditText,
                              final String timeinMillis){

        mStringRequest = new StringRequest(Request.Method.POST, URL_INSERT_REVIEW, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideSoftKeyboard(getActivity(),viewParent);
                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.comment_added)
                        ,Toast.LENGTH_SHORT).show();
               // background.callOnClick();
                try {
                       JSONArray jsonArray = new JSONArray(response);

                        DataRecycler dataRecycler = new DataRecycler();
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        dataRecycler.comment_id = jsonObject.getString("comment_id");
                        dataRecycler.post_id = jsonObject.getString("post_id");
                        dataRecycler.reviews = jsonObject.getString("reviews");
                        dataRecycler.enrollment = jsonObject.getString("enrollment");
                        dataRecycler.fullname = jsonObject.getString("first_name")+" "+
                                jsonObject.getString("last_name");
                        dataRecycler.time_ago = jsonObject.getString("time_ago");
                        dataRecycler.edited = jsonObject.getString("edited");
                        dataRecycler.is_liked = jsonObject.getString("is_liked");
                        dataRecycler.likes_number = jsonObject.getString("likes_number");
                        dataRecycler.number_replies = jsonObject.getString("@replies_number := (SELECT COUNT(*) " +
                            "from comment_replies where comment_id = post_comments.comment_id)");
                        dataRecycler.user_id = jsonObject.getString("id");
                        dataRecycler.verified = jsonObject.getString("verified");
                        dataRecycler.first_load = "1";
                        data.add(dataRecycler);
                        mAdapterComments.notifyItemInserted(data.size()-1);
                        mAdapterComments.notifyDataSetChanged();


                        //scrolling to bottom when adding comment
                    mNestedScrollView.postDelayed(new Runnable()
                    {
                        public void run()
                        {
                            mNestedScrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    },200);

                        if(!dataRecycler.enrollment.equals(post_enrollment)) {
                            String fake_edit = inputEditText;
                            if(fake_edit.length()>20){
                                fake_edit = fake_edit.substring(0,19);
                            }
                            send_notification(fake_edit);
                            send_comment_notification(fake_edit);
                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("post_id",post_id);
                hashMap.put("review",inputEditText);
                hashMap.put("time_ago",timeinMillis);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    public void editComment(final String comment_id,final String comment,final int position){

        mStringRequest = new StringRequest(Request.Method.POST, URL_UPDATE_COMMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideSoftKeyboard(getActivity(),viewParent);
                Snackbar.make(viewParent,getActivity().getResources().getString(R.string.comment_edited)
                        ,Snackbar.LENGTH_SHORT).show();

                    mAdapterComments.data.get(position).setReviews(comment);
                    mAdapterComments.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getActivity(),"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("comment_id",comment_id);
                hashMap.put("review",comment);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    @Override
    public void onEdit(String comment_id,String review,int position){
        mMessageInput.getInputEditText().setText(review);
        mMessageInput.getInputEditText().requestFocus();
        mMessageInput.getInputEditText().setSelection(review.length());
        x=1;
        interface_comment_id = comment_id;
        interface_postion = position;
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInputFromWindow(
                mMessageInput.getInputEditText().getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    private void send_liked_notification_inside(){

        mStringRequest  = new StringRequest(Request.Method.POST, URL_LIKED_NOTIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",post_user_id);
                hashMap.put("sender_id",user_id);
                hashMap.put("thing",post_caption);
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                hashMap.put("post_id",post_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void send_liked_notification(){

        mStringRequest  = new StringRequest(Request.Method.POST, URL_SEND_NOTIFICATION, new Response.Listener<String>() {
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
                hashMap.put("user_id",post_user_id);
                hashMap.put("title",username);
                hashMap.put("message",user_fullname+" has liked your post.");
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    public void add_to_notebook(final String user_id,final String post_id){

        mStringRequest = new StringRequest(Request.Method.POST, URL_ADD_TO_NOTEBOOK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success") && is_saved.equals("0")){
                        is_saved = "1";
                        love_filled.setVisibility(View.VISIBLE);
                        love_lined.setVisibility(View.GONE);
                        text_save.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                        text_save.setText("Saved");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("post_id",post_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    private void remove_from_notebook(final String user_id,final String post_id){

        mStringRequest = new StringRequest(Request.Method.POST,URL_REMOVE_FROM_NOTEBOOK , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success") && is_saved.equals("1")){
                        is_saved = "0";
                        love_lined.setVisibility(View.VISIBLE);
                        love_filled.setVisibility(View.GONE);
                        text_save.setTextColor(getActivity().getResources().getColor(R.color.gray));
                        text_save.setText("Save");
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("post_id",post_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    public void delete_post(final String post_id){

        mStringRequest = new StringRequest(Request.Method.POST, URL_DELETE_POST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        getActivity().onBackPressed();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("post_id",post_id);
                hashMap.put("user_id",user_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }




}
