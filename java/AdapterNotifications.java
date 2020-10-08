package com.abhigam.www.foodspot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sourabhzalke on 13/06/17.
 */

public class AdapterNotifications extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    List<DataItem> data= Collections.emptyList();
    DataItem current;
    private String main_text;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    private static final String USERNAME_VISITOR = "visitor_username";
    private final static String URL_SEND_NOTIFICATION = "http://13.233.234.79/send_notification.php";
    private final static String URL_ACCEPT_REQUEST_NOTIFICATION
            = "http://13.233.234.79/accepted_request_notification.php";

    private String user_id,user,user_fullname,username;

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
    private static final String INTENT_OPEN_COMMENT = "com.abhigam.www.foodspot.OPEN_COMMENT";
    private static final String INTENT_IS_VERIFIED = "com.abhigam.www.foodspot.IS_VERIFIED";
    private static final String INTENT_IS_SAVED = "com.abhigam.www.foodspot.IS_SAVED";

    private String intent_post_id,intent_user_id,intent_enrollment,intent_fullname,intent_timeago,
                   intent_caption,intent_is_liked,intent_views_number,intent_likes_number,intent_is_image,
                   intent_is_verified,intent_is_saved;

    private String URL_ACCEPT_REQUEST = "http://13.233.234.79/accept_request.php";
    private String URL_REMOVE_NOTIFICATION = "http://13.233.234.79/remove_notification.php";
    private String URL_POST_DATA = "http://13.233.234.79/notification_post_data.php";

    private Context context;
    private LayoutInflater inflater;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterNotifications(Context context, List<DataItem> data){
        this.context=context;
        inflater = LayoutInflater.from(context);
        this.data=data;
        mRequestQueue = Volley.newRequestQueue(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        user_id = prefs.getString(PREF_USER_ID,"");
        username = prefs.getString(PREF_USER_NAME,"");
        user_fullname = prefs.getString(PREF_FULLNAME,"");
    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View view = inflater.inflate(R.layout.container_notification, parent, false);

            vh = new MyHolder(view);
        }else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }



    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder) {
            // Get current position of item in RecyclerView to bind data and assign values from list
            final MyHolder myHolder = (MyHolder) holder;
            DataItem current = data.get(position);

            if (current.getFirst_load().equals("1")) {

                myHolder.image.setImageResource(R.drawable.gray);

                Glide
                        .with(context)
                        .load("http://13.233.234.79/uploads/profile_pic/" +
                                current.getImage_name() + "_profile.jpg")
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                        .skipMemoryCache(true)
                        .centerCrop()
                        .into(new SimpleTarget<Bitmap>(200, 200) { //width and height
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                myHolder.image.setImageBitmap(resource);
                            }
                        });


                if (current.getNotification_type().equals("1")) {
                    main_text = current.getUsername() + " started following you.";
                    myHolder.accept_cancel.setVisibility(View.GONE);
                } else if (current.getNotification_type().equals("2")) {
                    if (current.getThing().equals("")) {
                        main_text = current.getUsername() + " commented on your post.";
                    } else {
                        main_text = current.getUsername() + " commented on your post, '" + current.getThing() + "'.";
                    }
                    myHolder.accept_cancel.setVisibility(View.GONE);
                } else if (current.getNotification_type().equals("3")) {
                    if (current.getThing().equals("")) {
                        main_text = current.getUsername() + " liked your post.";
                    } else {
                        main_text = current.getUsername() + " liked your post, '" + current.getThing() + "'.";
                    }
                    myHolder.accept_cancel.setVisibility(View.GONE);
                } else if (current.getNotification_type().equals("4")) {
                    main_text = current.getUsername() + " accepted your follow request.";
                    myHolder.accept_cancel.setVisibility(View.GONE);
                } else if (current.getNotification_type().equals("5")) {
                    main_text = current.getUsername() + " sent you ₹" + current.getThing() + ".";
                    myHolder.accept_cancel.setVisibility(View.GONE);
                } else if (current.getNotification_type().equals("6")) {
                    if (current.getThing().equals("")) {
                        main_text = current.getUsername() + " replied to your comment.";
                    } else {
                        main_text = current.getUsername() + " replied to your comment, '" + current.getThing() + "'.";
                    }
                    myHolder.accept_cancel.setVisibility(View.GONE);
                } else if (current.getNotification_type().equals("7")) {
                    if (current.getThing().equals("")) {
                        main_text = current.getUsername() + " liked your comment.";
                    } else {
                        main_text = current.getUsername() + " liked your comment, '" + current.getThing() + "'.";
                    }
                    myHolder.accept_cancel.setVisibility(View.GONE);
                } else if(current.getNotification_type().equals("10")){
                    if(current.getThing().equals("reward")){
                        main_text = "₹1 added to wallet for your new post. You can use the money at stores.";
                    }else{
                        main_text = "Welcome to Crony. You have received ₹10 as Sign Up Bonus. Receive" +
                                " more rewards on every new post.";
                    }
                    myHolder.accept_cancel.setVisibility(View.GONE);
                } else if (current.getNotification_type().equals("8")) {
                    if (current.getThing().equals("")) {
                        main_text = current.getUsername() + " liked your reply.";
                    } else {
                        main_text = current.getUsername() + " liked your reply, '" + current.getThing() + "'.";
                    }
                    myHolder.accept_cancel.setVisibility(View.GONE);
                } else {
                    main_text = current.getUsername() + " has requested to follow you.";
                    myHolder.accept_cancel.setVisibility(View.VISIBLE);
                }

                SpannableStringBuilder str = new SpannableStringBuilder(main_text);
                if(current.getNotification_type().equals("10") && current.getThing().equals("reward")){
                    str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 18,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else if(current.getNotification_type().equals("10") && current.getThing().equals("signup")){
                    //do nothing
                }else {
                    str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, current.getUsername().length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                myHolder.notify_text.setText(str);

                long time = Long.parseLong(current.getTime_ago());
                long now = System.currentTimeMillis();

                CharSequence min = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS);
                if (min.equals("0 seconds ago"))
                    myHolder.time_ago.setText(context.getResources().getString(R.string.just_now));
                else
                    myHolder.time_ago.setText(min);
            } else {
                main_text = current.getUsername() + " started following you.";
                SpannableStringBuilder str = new SpannableStringBuilder(main_text);
                str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, current.getUsername().length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                myHolder.notify_text.setText(str);
                myHolder.accept_cancel.setVisibility(View.GONE);
            }

        }
    }

    public  class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        private ProgressViewHolder(View v){
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }


    private class MyHolder extends RecyclerView.ViewHolder{

        private TextView time_ago,notify_text;
        private CircleImageView image;
        private ImageView accept_request,cancel_request;
        private LinearLayout accept_cancel,notification;

        // create constructor to get widget reference
        private MyHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            time_ago = itemView.findViewById(R.id.time_ago);
            image = itemView.findViewById(R.id.notify_image);
            notify_text = itemView.findViewById(R.id.notify_text);
            accept_request = itemView.findViewById(R.id.accept_request);
            cancel_request = itemView.findViewById(R.id.cancel_request);
            accept_cancel = itemView.findViewById(R.id.accept_cancel);
            notification = itemView.findViewById(R.id.notification);

            notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(data.get(getAdapterPosition()).getNotification_type().equals("1")||
                            data.get(getAdapterPosition()).getNotification_type().equals("0")||
                            data.get(getAdapterPosition()).getNotification_type().equals("5")||
                            data.get(getAdapterPosition()).getNotification_type().equals("4")){
                        Intent i = new Intent(context, VisitorProfile.class);
                        i.putExtra(USERNAME_VISITOR, data.get(getAdapterPosition()).getImage_name());
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                        context.startActivity(i);
                    }else if(data.get(getAdapterPosition()).getNotification_type().equals("3")
                             || data.get(getAdapterPosition()).getNotification_type().equals("7")
                            || data.get(getAdapterPosition()).getNotification_type().equals("8")
                            || data.get(getAdapterPosition()).getNotification_type().equals("2")
                            || data.get(getAdapterPosition()).getNotification_type().equals("6")){
                            getPostData(data.get(getAdapterPosition()).getPost_id());
                    }else if(data.get(getAdapterPosition()).getNotification_type().equals("10")){
                        Intent i = new Intent(context,CoinTransactions.class);
                        context.startActivity(i);
                    }
                }
            });


            accept_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accept_request(getAdapterPosition());
                }
            });

            cancel_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel_request(getAdapterPosition());
                }
            });

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, VisitorProfile.class);
                    i.putExtra(USERNAME_VISITOR, data.get(getAdapterPosition()).getImage_name());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    context.startActivity(i);
                }
            });
        }

    }

    public void accept_request(final int position){

        mStringRequest = new StringRequest(Request.Method.POST, URL_ACCEPT_REQUEST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        data.get(position).setFirst_load("0");
                        notifyItemChanged(position);
                        send_notification(position);
                        send_accept_request_notification(position);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",data.get(position).getUser_id());
                hashMap.put("following_id",user_id);
                hashMap.put("notification_id",data.get(position).getNotification_id());
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    public void cancel_request(final int position){

        mStringRequest = new StringRequest(Request.Method.POST, URL_REMOVE_NOTIFICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(context,context.getResources().getString(R.string.request_cancelled),
                                Snackbar.LENGTH_SHORT).show();
                        data.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("notification_id",data.get(position).getNotification_id());
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    private void send_notification(final int position){

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
                hashMap.put("user_id",data.get(position).getUser_id());
                hashMap.put("title",username);
                hashMap.put("message",user_fullname+" has accepted your follow request.");
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void send_accept_request_notification(final int position){

        mStringRequest  = new StringRequest(Request.Method.POST, URL_ACCEPT_REQUEST_NOTIFICATION,
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
                hashMap.put("user_id",data.get(position).getUser_id());
                hashMap.put("sender_id",user_id);
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    //to get feed of following people
    private void getPostData(final String post_id){


        mStringRequest = new StringRequest(Request.Method.POST, URL_POST_DATA, new Response.Listener<String>() {


            @Override
            public void onResponse(String response){

                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    JSONArray jsonArray = jsonResponse.getJSONArray("notifications");


                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        intent_post_id = jsonObject.getString("post_id");
                        intent_user_id = jsonObject.getString("user_id");
                        intent_enrollment = jsonObject.getString("enrollment");
                        intent_fullname = jsonObject.getString("first_name")
                                +" "+jsonObject.getString("last_name");
                        intent_timeago = jsonObject.getString("time_ago");
                        intent_caption = jsonObject.getString("caption");
                        intent_is_liked = jsonObject.getString("is_liked");
                        intent_likes_number = jsonObject.getString("likes_number");
                        intent_views_number = jsonObject.getString("views");
                        intent_is_image = jsonObject.getString("is_image");
                        intent_is_verified = jsonObject.getString("verified");
                        intent_is_saved = jsonObject.getString("is_saved");

                    Intent i = new Intent(context, PostDetails.class);
                    i.putExtra(INTENT_POST_ID,intent_post_id);
                    i.putExtra(INTENT_ENROLLMENT,intent_enrollment);
                    i.putExtra(INTENT_USER_ID,intent_user_id);
                    i.putExtra(INTENT_FULLNAME,intent_fullname);
                    i.putExtra(INTENT_TIMEAGO,intent_timeago);
                    i.putExtra(INTENT_CAPTION,intent_caption);
                    i.putExtra(INTENT_IS_LIKED,intent_is_liked);
                    i.putExtra(INTENT_VIEWS_NUMBER,intent_views_number);
                    i.putExtra(INTENT_LIKES_NUMBER,intent_likes_number);
                    i.putExtra(INTENT_IS_IMAGE,intent_is_image);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    i.putExtra(INTENT_OPEN_COMMENT,"0");
                    i.putExtra(INTENT_IS_VERIFIED,intent_is_verified);
                    i.putExtra(INTENT_IS_SAVED,intent_is_saved);
                    context.startActivity(i);


                }catch(JSONException e){
                    e.printStackTrace();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Toast.makeText(context,"Network Error",Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("post_id",post_id);
                return hashMap;
                //add null , so the adapter will check view_type and show progress bar at bottom
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }



}
