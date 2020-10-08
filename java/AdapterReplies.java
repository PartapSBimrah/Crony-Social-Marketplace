package com.abhigam.www.foodspot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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

public class AdapterReplies extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<DataRecycler> data = Collections.emptyList();
    DataRecycler current;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
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

    private String URL_DELETE_REPLY = "http://13.233.234.79/delete_replies.php";
    private String URL_ADD_TO_LIKED_REPLY = "http://13.233.234.79/add_to_liked_reply.php";
    private String URL_REMOVE_FROM_LIKED_REPLY = "http://13.233.234.79/remove_from_liked_reply.php";

    private static final String URL_LIKED_NOTIFICATION_REPLY = "http://13.233.234.79/liked_reply_notification_inside.php";
    private final static String URL_SEND_NOTIFICATION = "http://13.233.234.79/send_notification.php";


    private Context context;
    private LayoutInflater inflater;
    private String original_username,user_fullname,user_check_id;

    private static final String USERNAME_VISITOR = "visitor_username";
    private static char[] c = new char[]{'k', 'm', 'b', 't'};
    long y=0,z=0;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterReplies(Context context, List<DataRecycler> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        mRequestQueue = Volley.newRequestQueue(context);
        setHasStableIds(true);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        original_username = prefs.getString(PREF_USER_NAME,"");
        user_fullname = prefs.getString(PREF_FULLNAME,"");
        user_check_id = prefs.getString(PREF_USER_ID,"");
    }

    // return total item from List
    @Override
    public int getItemCount() {

        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        RecyclerView.ViewHolder vh;
        View view = inflater.inflate(R.layout.container_reply,parent,false);

        vh = new MyHolder(view);
        return vh;
    }


    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder) {
            // Get current position of item in RecyclerView to bind data and assign values from list
            final MyHolder myHolder = (MyHolder) holder;
            DataRecycler current = data.get(position);
            //Instantiate the RequestQueue

            if(current.getFirst_load().equals("1")) {

                String url = "http://13.233.234.79/uploads/profile_pic/" + current.getEnrollment() + "_profile.jpg";

                myHolder.mSmallImageUser.setImageResource(0);
                //Getting user profile
                Glide
                        .with(context)
                        .load(url)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                        .skipMemoryCache(true)
                        .centerCrop()
                        .into(new SimpleTarget<Bitmap>(100, 100) { //width and height
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                myHolder.mSmallImageUser.setImageBitmap(resource);
                            }
                        });

                if (Integer.parseInt(current.getIs_liked()) == 0) {
                    myHolder.like_reply.setColorFilter(ContextCompat.getColor(context, R.color.gray),
                            PorterDuff.Mode.SRC_IN);
                } else {
                    myHolder.like_reply.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                }

                if(current.getVerified().equals("1")){
                    myHolder.ic_verified.setVisibility(View.VISIBLE);
                }else{
                    myHolder.ic_verified.setVisibility(View.GONE);
                }

                myHolder.number_likes.setText(current.getLikes_number());


                long time = Long.parseLong(current.getTime_ago());
                long now = System.currentTimeMillis();

                CharSequence min = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS);
                if (min.equals("0 seconds ago")) {
                    if (current.getEdited().equals("0"))
                        myHolder.time_ago.setText(" ∙ " + context.getResources().getString(R.string.just_now));
                    else
                        myHolder.time_ago.setText(" ∙ Edited ∙ " + context.getResources().getString(R.string.just_now));
                } else {
                    if (current.getEdited().equals("0"))
                        myHolder.time_ago.setText(" ∙ " + min);
                    else
                        myHolder.time_ago.setText(" ∙ Edited ∙ " + min);
                }
                if (current.getEdited().equals("0"))
                    myHolder.full_name.setText(current.getFullname());
                else
                    myHolder.full_name.setText(current.getFullname());
                myHolder.replies.setText(current.getReviews());

            }else{
                if(current.getIs_liked().equals("1")) {
                    myHolder.like_reply.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    myHolder.number_likes.setText(current.getLikes_number());
                }else{
                    myHolder.like_reply.setColorFilter(ContextCompat.getColor(context, R.color.gray),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    myHolder.number_likes.setText(current.getLikes_number());
                }
            }

        }
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        private TextView replies,full_name,time_ago,number_likes;
        private CircleImageView mSmallImageUser;
        private ImageView popup,like_reply,ic_verified;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            replies = itemView.findViewById(R.id.reply);
            full_name = itemView.findViewById(R.id.fullname);
            time_ago = itemView.findViewById(R.id.time_ago);
            number_likes = itemView.findViewById(R.id.number_likes);

            mSmallImageUser = itemView.findViewById(R.id.circular_small_profile);
            popup = itemView.findViewById(R.id.popup_reply);
            like_reply = itemView.findViewById(R.id.like_reply);
            ic_verified = itemView.findViewById(R.id.ic_verified);


            like_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Integer.parseInt(data.get(getAdapterPosition()).getIs_liked())==0) {
                        add_to_liked(user_check_id, data.get(getAdapterPosition()).getReply_id(),getAdapterPosition());
                    }else{
                        remove_from_liked(user_check_id, data.get(getAdapterPosition()).getReply_id(),getAdapterPosition());
                    }
                }
            });


            popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(context, v);
                    if(data.get(getAdapterPosition())
                            .getEnrollment().equals(original_username))
                        menu.getMenu().add("Edit");
                    /*
                    if(!data.get(getAdapterPosition()).getEnrollment().equals(original_username))
                        menu.getMenu().add("Report");
                        */
                    if(data.get(getAdapterPosition()).getEnrollment().equals(original_username)
                            || data.get(getAdapterPosition())
                            .getPost_username().equals(original_username))
                        menu.getMenu().add("Delete");
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getTitle().equals("Edit")){
                                if(context instanceof CommentReplies){
                                    ((CommentReplies)context).activityOnEdit(data.get(getAdapterPosition())
                                                    .getReply_id(),data.get(getAdapterPosition()).getReviews(),
                                            getAdapterPosition());
                                }
                            }else{
                                AlertDialog.Builder builder;
                                builder = new AlertDialog.Builder(context);
                                builder.setTitle("Delete post")
                                        .setMessage("Are you sure you want to delete this comment?")
                                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                delete_comment(getAdapterPosition());
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

            mSmallImageUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, VisitorProfile.class);
                    i.putExtra(USERNAME_VISITOR, data.get(getAdapterPosition()).getEnrollment());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    context.startActivity(i);
                }
            });

            full_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, VisitorProfile.class);
                    i.putExtra(USERNAME_VISITOR, data.get(getAdapterPosition()).getEnrollment());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    context.startActivity(i);
                }
            });

        }


    }

    public void delete_comment(final int position){

        mStringRequest = new StringRequest(Request.Method.POST, URL_DELETE_REPLY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(context,context.getResources().getString(R.string.reply_deleted),
                                Snackbar.LENGTH_SHORT).show();
                        data.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(context,"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("reply_id",data.get(position).getReply_id());
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    public void add_to_liked(final String user_id,final String reply_id,final int position){

        mStringRequest = new StringRequest(Request.Method.POST, URL_ADD_TO_LIKED_REPLY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success") && data.get(position).getIs_liked().equals("0")){
                        data.get(position).setIs_liked("1");
                        data.get(position).setLikes_number(Integer.toString(Integer.parseInt(
                                data.get(position).getLikes_number()
                        ) + 1));

                        if(!original_username.equals(data.get(position).getEnrollment())) {
                            send_liked_notification_inside(position);
                            send_notification(position);
                        }

                        data.get(position).setFirst_load("0");
                        notifyItemChanged(position);
                    }
                } catch(JSONException e){
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
                hashMap.put("user_id",user_id);
                hashMap.put("reply_id",reply_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

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

    public void remove_from_liked(final String user_id,final String reply_id,final int position){

        mStringRequest = new StringRequest(Request.Method.POST, URL_REMOVE_FROM_LIKED_REPLY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.names().get(0).equals("success") && data.get(position).getIs_liked().equals("1")){
                                data.get(position).setIs_liked("0");
                                data.get(position).setLikes_number(Integer.toString(Integer.parseInt(
                                        data.get(position).getLikes_number()
                                ) - 1));

                                data.get(position).setFirst_load("0");
                                notifyItemChanged(position);
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
                hashMap.put("user_id",user_id);
                hashMap.put("reply_id",reply_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    private void send_liked_notification_inside(final int position){

        mStringRequest  = new StringRequest(Request.Method.POST, URL_LIKED_NOTIFICATION_REPLY,
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
                hashMap.put("sender_id",user_check_id);
                hashMap.put("thing",data.get(position).getReviews());
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                hashMap.put("post_id",data.get(position).getPost_id());
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
                hashMap.put("title",original_username);
                hashMap.put("message",user_fullname+" has liked your reply.");
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

}
