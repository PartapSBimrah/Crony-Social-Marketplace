package com.abhigam.www.foodspot;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

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

public class AdapterSuggestedUsers extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String USERNAME_VISITOR = "visitor_username";
    //Tags
    private final static String URL_SEND_NOTIFICATION = "http://13.233.234.79/send_notification.php";

    List<DataItem> data= Collections.emptyList();
    DataItem current;

    private String user_id,user,user_fullname;

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

    private String login_user_id;

    private Context mContext;
    private LayoutInflater inflater;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterSuggestedUsers(Activity context, List<DataItem> data){
        this.mContext=context;
        inflater = LayoutInflater.from(context);
        this.data=data;
        mRequestQueue = Volley.newRequestQueue(mContext);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        user_id = prefs.getString(PREF_USER_ID,"");
        user = prefs.getString(PREF_USER_NAME,"");
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
            View view = inflater.inflate(R.layout.container_suggested_users, parent, false);

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

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder) {
            // Get current position of item in RecyclerView to bind data and assign values from list
            final MyHolder myHolder = (MyHolder) holder;
            DataItem current = data.get(position);
            myHolder.username.setText(current.getUsername());

            if (!current.getFull_name().equals("0"))
                myHolder.fullname.setText(current.getFull_name());

            String curren = Integer.toString(current.getFollowed());

            if (current.followed == 1) {
                myHolder.follow.setVisibility(View.GONE);
                myHolder.request_button.setVisibility(View.GONE);
                myHolder.following.setVisibility(View.VISIBLE);
            } else if (current.followed == 2) {
                myHolder.follow.setVisibility(View.GONE);
                myHolder.request_button.setVisibility(View.GONE);
                myHolder.following.setVisibility(View.GONE);
            } else if (current.followed == 5) {
                myHolder.follow.setVisibility(View.GONE);
                myHolder.request_button.setVisibility(View.VISIBLE);
                myHolder.following.setVisibility(View.GONE);
            } else {
                myHolder.follow.setVisibility(View.VISIBLE);
                myHolder.request_button.setVisibility(View.GONE);
                myHolder.following.setVisibility(View.GONE);
            }

            if (user.equals(current.getUsername())) {
                myHolder.follow.setVisibility(View.GONE);
                myHolder.request_button.setVisibility(View.GONE);
                myHolder.following.setVisibility(View.GONE);
            }

            if (current.verified.equals("1")) {
                myHolder.ic_verified.setVisibility(View.VISIBLE);
            } else {
                myHolder.ic_verified.setVisibility(View.GONE);
            }

            String url = "http://13.233.234.79/uploads/profile_pic/" + current.getUsername() + "_profile.jpg";

            myHolder.user_profile.setImageResource(R.drawable.gray);

            Glide
                    .with(mContext)
                    .load(url)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                    .skipMemoryCache(true)
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>(100, 100) { //width and height
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            myHolder.user_profile.setImageBitmap(resource);
                        }
                    });
        }
    }

    public  class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        private ProgressViewHolder(View v){
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private TextView username,fullname;
        private Button follow;
        private Button following,request_button;
        private CircleImageView user_profile;
        private ImageView ic_verified;
        long timeInMillis;

        private String URL = "http://13.233.234.79/follow_people.php";

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);

            final Context context = itemView.getContext();

            username = itemView.findViewById(R.id.username);
            user_profile = itemView.findViewById(R.id.circular_user);
            fullname = itemView.findViewById(R.id.fullname);
            follow = itemView.findViewById(R.id.follow);
            following = itemView.findViewById(R.id.following);
            ic_verified = itemView.findViewById(R.id.ic_verified);
            this.setIsRecyclable(false);
            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    follow_people(getAdapterPosition());
                }
            });
            following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unfollow_dialog(getAdapterPosition());
                }
            });
            request_button = itemView.findViewById(R.id.request);

            request_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    request_dialog(getAdapterPosition());
                }
            });

            fullname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,VisitorProfile.class);
                    i.putExtra(USERNAME_VISITOR,data.get(getAdapterPosition()).getUsername());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    context.startActivity(i);
                }
            });

            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,VisitorProfile.class);
                    i.putExtra(USERNAME_VISITOR,data.get(getAdapterPosition()).getUsername());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    context.startActivity(i);
                }
            });

            user_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,VisitorProfile.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    //was an error runtime
                    i.putExtra(USERNAME_VISITOR,data.get(getAdapterPosition()).getUsername());
                    context.startActivity(i);
                }
            });
        }

        private void follow_people(final int position){

            timeInMillis = Calendar.getInstance().getTimeInMillis();

            mStringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try {
                       JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.names().get(0).equals("success")){
                        if(data.get(position).getPrivate_ac().equals("0")) {
                            follow.setVisibility(View.GONE);
                            following.setVisibility(View.VISIBLE);
                            request_button.setVisibility(View.GONE);
                            data.get(position).setFollowed(1);
                        }else{
                            follow.setVisibility(View.GONE);
                            following.setVisibility(View.GONE);
                            request_button.setVisibility(View.VISIBLE);
                            data.get(position).setFollowed(5);
                        }
                        send_notification(position);
                        }else{
                        Toast.makeText(mContext,"Error",Toast.LENGTH_SHORT).show();
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
                    hashMap.put("user_id",user_id);
                    hashMap.put("following_id",data.get(getAdapterPosition()).getUser_id());
                    hashMap.put("enrollment",user);
                    hashMap.put("time_ago",Long.toString(timeInMillis));
                    hashMap.put("fullname",user_fullname);
                    if(data.get(position).getPrivate_ac().equals("0")){
                        hashMap.put("private_ac","0");
                    }
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
                    hashMap.put("user_id",data.get(getAdapterPosition()).getUser_id());
                    hashMap.put("title",user);
                    if(data.get(position).getPrivate_ac().equals("0")){
                        hashMap.put("message", user_fullname + " started following you.");
                    }else {
                        hashMap.put("message", user_fullname + " has requested to follow you.");
                    }
                    return hashMap;
                }
            };

            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mRequestQueue.add(mStringRequest);
        }

        private void request_dialog(final int position){

            String fullname = data.get(getAdapterPosition()).getFull_name();

            final String URL_UNFOLLOW = "http://13.233.234.79/unfollow_people.php";

            final SpannableStringBuilder sb = new SpannableStringBuilder("Are you sure " +
                    "you want to cancel request to follow "+fullname+" ?");

            final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
            sb.setSpan(bss, 50, sb.length()-2, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // making fullname characters Bold


            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Cancel request")
                    .setMessage(sb)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            mStringRequest = new StringRequest(Request.Method.POST, URL_UNFOLLOW, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    following.setVisibility(View.GONE);
                                    follow.setVisibility(View.VISIBLE);
                                    request_button.setVisibility(View.GONE);
                                    data.get(position).setFollowed(0);

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
                                    hashMap.put("following_id",data.get(getAdapterPosition()).getUser_id());
                                    hashMap.put("username",user);
                                    return hashMap;
                                }
                            };

                            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            mRequestQueue.add(mStringRequest);

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        }

        private void unfollow_dialog(final int position){

            String fullname = data.get(position).getFull_name();

            final String URL_UNFOLLOW = "http://13.233.234.79/unfollow_people.php";

            final SpannableStringBuilder sb = new SpannableStringBuilder("Are you sure " +
                    "you want to unfollow "+fullname+" ?");

            final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
            sb.setSpan(bss, 34, sb.length()-2, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // making fullname characters Bold


            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Unfollow")
                    .setMessage(sb)
                    .setPositiveButton(R.string.unfollow, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            mStringRequest = new StringRequest(Request.Method.POST, URL_UNFOLLOW, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    following.setVisibility(View.GONE);
                                    follow.setVisibility(View.VISIBLE);
                                    request_button.setVisibility(View.GONE);
                                    data.get(position).setFollowed(0);

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
                                    hashMap.put("following_id",data.get(getAdapterPosition()).getUser_id());
                                    hashMap.put("username",user);
                                    return hashMap;
                                }
                            };

                            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            mRequestQueue.add(mStringRequest);

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        }
    }


}
