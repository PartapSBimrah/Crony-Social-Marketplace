package com.abhigam.www.foodspot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.preference.PreferenceManager;
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

public class AdapterSuggestInbox extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String USERNAME_VISITOR = "visitor_username";
    //Tags

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

    private static final String INTENT_FULLNAME = "intent_fullname";
    private static final String INTENT_OTHER_ID = "intent_other_id";
    private static final String INTENT_USERNAME = "intent_username";

    private final static String URL_SEND_NOTIFICATION = "http://13.233.234.79/send_notification.php";

    private Context mContext;
    private LayoutInflater inflater;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterSuggestInbox(Activity context, List<DataItem> data){
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
            View view = inflater.inflate(R.layout.container_suggest_inbox, parent, false);
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
            else



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
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                    .skipMemoryCache(true)
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
        private CircleImageView user_profile;
        private ImageView ic_verified;
        long timeInMillis;
        private LinearLayout mLinearLayout;

        private String URL = "http://13.233.234.79/connect_chat.php";

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);

            final Context context = itemView.getContext();

            username = itemView.findViewById(R.id.username);
            user_profile = itemView.findViewById(R.id.circular_user);
            fullname = itemView.findViewById(R.id.fullname);
            ic_verified = itemView.findViewById(R.id.ic_verified);
            mLinearLayout = itemView.findViewById(R.id.linear);


            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connect_chat(getAdapterPosition(),data.get(getAdapterPosition()).getUser_id(),
                             data.get(getAdapterPosition()).getFull_name());
                }
            });

        }

        private void connect_chat(final int position,final String user2,final String user_fullname){

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Connect to "+user_fullname)
                    .setMessage("Do you want to start chat with "+user_fullname+"?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            final ProgressDialog progress = new ProgressDialog(mContext);
                            progress.setMessage("Connecting :) ");
                            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progress.setIndeterminate(true);
                            timeInMillis = Calendar.getInstance().getTimeInMillis();

                            mStringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {


                                    try {
                                        JSONObject jsonObject = new JSONObject(response);

                                        if(jsonObject.names().get(0).equals("success")){
                                            progress.cancel();
                                            send_notification(position);
                                            Intent i = new Intent(mContext,EnterChatRoom.class);
                                            i.putExtra(INTENT_FULLNAME,data.get(getAdapterPosition()).getFull_name());
                                            i.putExtra(INTENT_OTHER_ID,data.get(getAdapterPosition()).getUser_id());
                                            i.putExtra(INTENT_USERNAME,data.get(getAdapterPosition()).getUsername());
                                            mContext.startActivity(i);
                                        }else{
                                            Toast.makeText(mContext,"Error",Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(mContext,"Error",Toast.LENGTH_SHORT).show();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    HashMap<String,String> hashMap = new HashMap<>();
                                    hashMap.put("user1",user_id);
                                    hashMap.put("user2",user2);
                                    return hashMap;
                                }
                            };

                            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            mRequestQueue.add(mStringRequest);

                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        }


    }

    private void send_notification(final int postition){

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
                hashMap.put("user_id",data.get(postition).getUser_id());
                hashMap.put("title",user);
                hashMap.put("message",data.get(postition).getFull_name()+" started conversation with you.");
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }


}
