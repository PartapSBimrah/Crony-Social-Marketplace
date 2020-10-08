package com.abhigam.www.foodspot;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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
import com.stfalcon.chatkit.messages.MessageInput;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sourabhzalke on 30/03/18.
 */

public class CommentRepliesFragment extends Fragment implements InterfaceComment{


    private String intent_comment_id,intent_post_id,intent_reviews,intent_enrollment,intent_fullname,
                   intent_timeago,intent_post_username,intent_edited,intent_user_id,
                   intent_is_liked,intent_number_replies,intent_number_likes;
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

    private static final String INTENT_COMMENT_ID = "com.abhigam.www.foodspot.comment_id";
    private static final String INTENT_POST_ID = "com.abhigam.www.foodspot.post_id";
    private static final String INTENT_REVIEWS = "com.abhigam.www.foodspot.reviews";
    private static final String INTENT_ENROLLMENT = "com.abhigam.www.foodspot.enrollment";
    private static final String INTENT_FULLNAME = "com.abhigam.www.foodspot.fullname";
    private static final String INTENT_TIMEAGO = "com.abhigam.www.foodspot.time_ago";
    private static final String INTENT_EDITED = "com.abhigam.www.foodspot.edited";
    private static final String INTENT_POST_USERNAME = "com.abhigam.www.foodspot.post_username";
    private static final String INTENT_IS_LIKED = "com.abhigam.www.foodspot.is_liked";
    private static final String INTENT_LIKES_NUMBER = "com.abhigam.www.foodspot.likes_number";
    private static final String INTENT_REPLIES_NUMBER = "com.abhigam.www.foodspot.replies_number";
    private static final String INTENT_USER_ID = "com.abhigam.www.foodspot.user_id";
    private static final String INTENT_IS_VERIFIED = "com.abhigam.www.foodspot.IS_VERIFIED";

    //urls
    private final static String URL_SEND_NOTIFICATION = "http://13.233.234.79/send_notification.php";
    private final static String URL_REPLIED_NOTIFICATION = "http://13.233.234.79/replied_notification.php";

    private int x =0;
    private int interface_postion=-1;
    private String interface_reply_id="";

    private static final String URL_GET_REPLIES = "http://13.233.234.79/get_comment_replies.php";

    private CircleImageView mCircleImageView;
    private TextView fullname,time_ago,review,title,number_replies,number_likes;

    private String username,user_id,user_fullname,intent_is_verified;

    private ImageView like_it,reply_it,popup,ic_verified_icon;

    private RecyclerView mRecyclerView;
    private AdapterReplies mAdapterReplies;
    private NestedScrollView mNestedScrollView;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<DataRecycler> data = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    MessageInput mMessageInput;
    private String inputEditText;
    private View viewParent;
    private String URL_UPDATE_REPLY = "http://13.233.234.79/update_reply.php";
    private String URL_INSERT_REPLY = "http://13.233.234.79/insert_replies.php";
    private String URL_DELETE_COMMENT = "http://13.233.234.79/delete_comments.php";
    private String URL_ADD_TO_LIKED_COMMENT = "http://13.233.234.79/add_to_liked_comment.php";
    private String URL_REMOVE_FROM_LIKED_COMMENT = "http://13.233.234.79/remove_from_liked_comment.php";

    private static final String URL_LIKED_NOTIFICATION_COMMENT = "http://13.233.234.79/liked_comment_notification_inside.php";
    private String URL_UPDATE_COMMENT = "http://13.233.234.79/update_comment.php";

    DataRecycler mDataRecycler = new DataRecycler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //getting username and user_id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        username = prefs.getString(PREF_USER_NAME,"");
        user_fullname = prefs.getString(PREF_FULLNAME,"");
        user_id = prefs.getString(PREF_USER_ID,"");
        mRequestQueue = Volley.newRequestQueue(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_comment_replies,container,false);
        viewParent = v;

        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        intent_comment_id = getActivity().getIntent().getStringExtra(INTENT_COMMENT_ID);
        intent_post_username = getActivity().getIntent().getStringExtra(INTENT_POST_USERNAME);
        intent_enrollment = getActivity().getIntent().getStringExtra(INTENT_ENROLLMENT);
        intent_reviews = getActivity().getIntent().getStringExtra(INTENT_REVIEWS);
        intent_post_id = getActivity().getIntent().getStringExtra(INTENT_POST_ID);
        intent_fullname = getActivity().getIntent().getStringExtra(INTENT_FULLNAME);
        intent_timeago = getActivity().getIntent().getStringExtra(INTENT_TIMEAGO);
        intent_edited = getActivity().getIntent().getStringExtra(INTENT_EDITED);
        intent_is_liked = getActivity().getIntent().getStringExtra(INTENT_IS_LIKED);
        intent_number_likes = getActivity().getIntent().getStringExtra(INTENT_LIKES_NUMBER);
        intent_number_replies = getActivity().getIntent().getStringExtra(INTENT_REPLIES_NUMBER);
        intent_user_id = getActivity().getIntent().getStringExtra(INTENT_USER_ID);
        intent_is_verified = getActivity().getIntent().getStringExtra(INTENT_IS_VERIFIED);



        mCircleImageView = v.findViewById(R.id.circular_profile);
        fullname = v.findViewById(R.id.fullname);
        time_ago = v.findViewById(R.id.time_ago);
        review = v.findViewById(R.id.review);
        title = v.findViewById(R.id.full_n);
        mMessageInput = v.findViewById(R.id.input);
        number_replies = v.findViewById(R.id.number_replies);
        number_likes = v.findViewById(R.id.number_likes);
        like_it = v.findViewById(R.id.like_comment);
        reply_it = v.findViewById(R.id.reply);
        popup = v.findViewById(R.id.popup_comment);
        ic_verified_icon = v.findViewById(R.id.ic_verified);

        if(intent_is_verified.equals("1")){
            ic_verified_icon.setVisibility(View.VISIBLE);
        }else{
            ic_verified_icon.setVisibility(View.GONE);
        }


        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), VisitorProfile.class);
                i.putExtra(USERNAME_VISITOR, intent_enrollment);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                getActivity().startActivity(i);
            }
        });

        fullname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), VisitorProfile.class);
                i.putExtra(USERNAME_VISITOR, intent_enrollment);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                getActivity().startActivity(i);
            }
        });

        popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(getActivity(), v);
                if(intent_enrollment.equals(username)) {
                    menu.getMenu().add("Edit");
                }
                /*
                if(!intent_enrollment.equals(username))
                    menu.getMenu().add("Report");
                    */
                if(intent_enrollment.equals(username) || intent_post_username.equals(username)){
                    menu.getMenu().add("Delete");
                }
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Edit")){
                            // background.setVisibility(View.VISIBLE);
                            mMessageInput.getInputEditText().setText(intent_reviews);
                            mMessageInput.getInputEditText().requestFocus();
                            mMessageInput.getInputEditText().setSelection(review.length());
                            x=2;
                            InputMethodManager inputMethodManager =
                                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            assert inputMethodManager != null;
                            inputMethodManager.toggleSoftInputFromWindow(
                                    mMessageInput.getInputEditText().getApplicationWindowToken(),
                                    InputMethodManager.SHOW_FORCED, 0);
                        }else if(item.getTitle().equals("Report")){
                            // add_to_notebook(user_check_id,data.get(getAdapterPosition()).getPost_id());
                        }else{
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Delete post")
                                    .setMessage("Are you sure you want to delete this comment?")
                                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            delete_comment();
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

        like_it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(intent_is_liked)==0) {
                    add_to_liked(user_id, intent_comment_id);
                }else{
                    remove_from_liked(user_id, intent_comment_id);
                }
            }
        });

        if(Integer.parseInt(intent_is_liked)==0){
            like_it.setColorFilter(ContextCompat.getColor(getActivity(), R.color.gray),
                    PorterDuff.Mode.SRC_IN);
        }else{
            like_it.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        reply_it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        number_likes.setText(intent_number_likes);
        number_replies.setText(intent_number_replies);


        mMessageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                inputEditText = mMessageInput.getInputEditText().getText().toString();
                if(x==0){
                    insert_reply(user_id, intent_comment_id, inputEditText, Long.toString(Calendar.getInstance().getTimeInMillis()));
                }else if(x==2){
                    editComment(intent_comment_id,mMessageInput.getInputEditText().getText().toString());
                    x=0;
                }
                else{
                    editReply(interface_reply_id,mMessageInput.getInputEditText().getText().toString(),
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

        mSwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(){
                onResume();
            }
        });


        fullname.setText(intent_fullname);
        review.setText(intent_reviews);
        title.setText(intent_fullname+"'s Comment");

        mRecyclerView = v.findViewById(R.id.recycler_replies);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapterReplies = new AdapterReplies(getActivity(), data);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapterReplies);


        long time = Long.parseLong(intent_timeago);
        long now = System.currentTimeMillis();

        CharSequence min = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS);
        if(min.equals("0 seconds ago")) {
            if(intent_edited.equals("0"))
                time_ago.setText(" ∙ "+getActivity().getResources().getString(R.string.just_now));
            else
                time_ago.setText(" ∙ Edited ∙ "+getActivity().getResources().getString(R.string.just_now));
        }
        else {
            if(intent_edited.equals("0"))
                time_ago.setText(" ∙ "+min);
            else
                time_ago.setText(" ∙ Edited ∙ "+min);
            }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        Glide
                .with(getActivity())
                .load("http://13.233.234.79/uploads/profile_pic/" +intent_enrollment+ "_profile.jpg")
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
        data.clear();
        mRecyclerView.getRecycledViewPool().clear();
        mAdapterReplies.notifyDataSetChanged();
        getReviews();
    }

    public void getReviews(){

        mStringRequest = new StringRequest(Request.Method.POST, URL_GET_REPLIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                mSwipeRefreshLayout.setRefreshing(false);

                try {

                    JSONArray jsonArray = new JSONArray(response);

                    for(int i =0;i<jsonArray.length();++i){
                        DataRecycler dataRecycler = new DataRecycler();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        dataRecycler.reply_id = jsonObject.getString("reply_id");
                        dataRecycler.comment_id = jsonObject.getString("comment_id");
                        dataRecycler.reviews = jsonObject.getString("reviews");
                        dataRecycler.enrollment = jsonObject.getString("enrollment");
                        dataRecycler.fullname = jsonObject.getString("first_name")+" "+
                                jsonObject.getString("last_name");
                        dataRecycler.time_ago = jsonObject.getString("time_ago");
                        dataRecycler.edited = jsonObject.getString("edited");
                        dataRecycler.is_liked = jsonObject.getString("is_liked");
                        dataRecycler.likes_number = jsonObject.getString("likes_number");
                        dataRecycler.user_id = jsonObject.getString("id");
                        dataRecycler.verified = jsonObject.getString("verified");
                        dataRecycler.first_load = "1";
                        dataRecycler.post_username = intent_enrollment;
                        dataRecycler.post_id = intent_post_id;
                        data.add(dataRecycler);

                    }
                    number_replies.setText(intent_number_replies);

                    mAdapterReplies.notifyDataSetChanged();

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
                hashMap.put("comment_id",intent_comment_id);
                hashMap.put("user_id",user_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    @Override
    public void onEdit(String reply_id,String review,int position){
        mMessageInput.getInputEditText().setText(review);
        mMessageInput.getInputEditText().requestFocus();
        mMessageInput.getInputEditText().setSelection(review.length());
        x=1;
        interface_reply_id = reply_id;
        interface_postion = position;
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInputFromWindow(
                mMessageInput.getInputEditText().getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    public void editReply(final String reply_id,final String reply,final int position){

        mStringRequest = new StringRequest(Request.Method.POST, URL_UPDATE_REPLY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).toString().equals("success")) {
                        hideSoftKeyboard(getActivity(), viewParent);
                        Toast.makeText(getActivity(), getActivity().
                                getResources().getString(R.string.comment_edited),
                                Toast.LENGTH_SHORT).show();

                        mAdapterReplies.data.get(position).setReviews(reply);
                        mAdapterReplies.notifyDataSetChanged();
                    }else{
                        Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                hashMap.put("reply_id",reply_id);
                hashMap.put("review",reply);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    public void insert_reply(final String user_id, final String comment_id, final String inputEditText,
                              final String timeinMillis){

        mStringRequest = new StringRequest(Request.Method.POST, URL_INSERT_REPLY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideSoftKeyboard(getActivity(),viewParent);
                Snackbar.make(viewParent,getActivity().getResources().getString(R.string.comment_added)
                        ,Snackbar.LENGTH_SHORT).show();
                //background.callOnClick();
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    DataRecycler dataRecycler = new DataRecycler();
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    dataRecycler.reply_id = jsonObject.getString("reply_id");
                    dataRecycler.comment_id = jsonObject.getString("comment_id");
                    dataRecycler.reviews = jsonObject.getString("reviews");
                    dataRecycler.enrollment = jsonObject.getString("enrollment");
                    dataRecycler.fullname = jsonObject.getString("first_name")+" "+
                            jsonObject.getString("last_name");
                    dataRecycler.time_ago = jsonObject.getString("time_ago");
                    dataRecycler.edited = jsonObject.getString("edited");
                    dataRecycler.is_liked = jsonObject.getString("is_liked");
                    dataRecycler.likes_number = jsonObject.getString("likes_number");
                    dataRecycler.user_id = jsonObject.getString("id");
                    dataRecycler.first_load = "1";
                    dataRecycler.post_username = intent_enrollment;
                    dataRecycler.post_id = intent_post_id;
                    dataRecycler.verified = jsonObject.getString("verified");
                    data.add(dataRecycler);
                    mAdapterReplies.notifyItemInserted(data.size()-1);
                    mAdapterReplies.notifyDataSetChanged();
                    if(!dataRecycler.enrollment.equals(intent_enrollment)) {
                        String fake_edit = inputEditText;
                        if(fake_edit.length()>20){
                            fake_edit = fake_edit.substring(0,19);
                        }
                        send_notification(fake_edit);
                        send_replied_notification(fake_edit);
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
                hashMap.put("comment_id",comment_id);
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


    public void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
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
                hashMap.put("user_id",intent_user_id);
                hashMap.put("title",username);
                hashMap.put("message",user_fullname+" replied to your comment, '"+inputEditText+"...'.");
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void send_replied_notification(final String inputEditText){

        mStringRequest  = new StringRequest(Request.Method.POST, URL_REPLIED_NOTIFICATION,
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
                hashMap.put("user_id",intent_user_id);
                hashMap.put("sender_id",user_id);
                hashMap.put("thing",inputEditText);
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                hashMap.put("post_id",intent_post_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

    public void add_to_liked(final String user_id,final String comment_id){

        mStringRequest = new StringRequest(Request.Method.POST, URL_ADD_TO_LIKED_COMMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success") && intent_is_liked.equals("0")){
                        intent_is_liked ="1";
                        intent_number_likes = Integer.toString(Integer.
                                parseInt(intent_number_likes)
                                +1);
                        like_it.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                                android.graphics.PorterDuff.Mode.SRC_IN);
                        number_likes.setText(intent_number_likes);

                        //checking if same username
                        if(!intent_enrollment.equals(username)) {
                            send_comment_liked_notification();
                            send_liked_comment_notification_inside();
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
                hashMap.put("comment_id",comment_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    public void remove_from_liked(final String user_id,final String comment_id){

        mStringRequest = new StringRequest(Request.Method.POST, URL_REMOVE_FROM_LIKED_COMMENT,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success") && intent_is_liked.equals("1")){
                        intent_is_liked ="0";
                        intent_number_likes = Integer.toString(Integer.
                                parseInt(intent_number_likes)
                                -1);
                        like_it.setColorFilter(ContextCompat.getColor(getActivity(), R.color.gray),
                                PorterDuff.Mode.SRC_IN);
                        number_likes.setText(intent_number_likes);
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
                hashMap.put("comment_id",comment_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    public void delete_comment(){

        mStringRequest = new StringRequest(Request.Method.POST, URL_DELETE_COMMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                       getActivity().onBackPressed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("comment_id",intent_comment_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    private void send_liked_comment_notification_inside(){

        mStringRequest  = new StringRequest(Request.Method.POST, URL_LIKED_NOTIFICATION_COMMENT,
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
                hashMap.put("user_id",intent_user_id);
                hashMap.put("sender_id",user_id);
                hashMap.put("thing",intent_reviews);
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                hashMap.put("post_id",intent_post_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void send_comment_liked_notification(){

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
                hashMap.put("user_id",intent_user_id);
                hashMap.put("title",username);
                hashMap.put("message",user_fullname+" has liked your comment.");
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    public void editComment(final String comment_id,final String comment){

        mStringRequest = new StringRequest(Request.Method.POST, URL_UPDATE_COMMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideSoftKeyboard(getActivity(),viewParent);
                Snackbar.make(viewParent,getActivity().getResources().getString(R.string.comment_edited)
                        ,Snackbar.LENGTH_SHORT).show();

                intent_reviews = comment;
                review.setText(comment);

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





}
