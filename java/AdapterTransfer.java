package com.abhigam.www.foodspot;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

/**
 * Created by sourabhzalke on 13/06/17.
 */

public class AdapterTransfer extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


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

    private String INTENT_ENROLLMENT = "com.abhigam.www.foodspot.enrollment";
    private String INTENT_FULLNAME = "com.abhigam.www.foodspot.fullname";
    private String INTENT_USER_ID = "com.abhigam.www.foodspot.user_id";
    private String RECIEVER_INTENT_COINS = "com.abhigam.www.foodspot.reciever_intent_coins";
    private String SENDER_INTENT_COINS = "com.abhigam.www.foodspot.sender_intent_coins";
    private String INTENT_COINS = "com.abhigam.www.foodspot.intent_coins";
    private String INTENT_LINEAR = "com.abhigam.www.foodspot.linear";

    private String login_user_id;

    private Context mContext;
    private LayoutInflater inflater;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterTransfer(Activity context, List<DataItem> data){
        this.mContext=context;
        inflater = LayoutInflater.from(context);
        this.data=data;

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

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.container_transfer, parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in RecyclerView to bind data and assign values from list
        final MyHolder myHolder= (MyHolder) holder;
        DataItem current=data.get(position);
        myHolder.username.setText(current.getUsername());

        if(!current.getFull_name().equals("0"))
            myHolder.fullname.setText(current.getFull_name());

           String curren = Integer.toString(current.getFollowed());


        if(current.verified.equals("1")){
            myHolder.ic_verified.setVisibility(View.VISIBLE);
        }else{
            myHolder.ic_verified.setVisibility(View.GONE);
        }

        String url = "http://13.233.234.79/uploads/profile_pic/"+current.getUsername()+"_profile.jpg";

        myHolder.user_profile.setImageResource(0);

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

    class MyHolder extends RecyclerView.ViewHolder {

        private TextView username, fullname;
        private CircularNetworkImageView user_profile;
        private ImageView ic_verified;

        private LinearLayout mLinearLayout;
        long timeInMillis;


        private String URL = "http://13.233.234.79/follow_people.php";

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);

            final Context context = itemView.getContext();


            mLinearLayout = itemView.findViewById(R.id.linear);

            username = itemView.findViewById(R.id.username);
            user_profile = itemView.findViewById(R.id.circular_user);
            fullname = itemView.findViewById(R.id.fullname);
            ic_verified = itemView.findViewById(R.id.ic_verified);

            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    Intent i = new Intent(context,AmountTransferMoney.class);
                    i.putExtra(INTENT_ENROLLMENT,data.get(getAdapterPosition()).getUsername());
                    i.putExtra(RECIEVER_INTENT_COINS,data.get(getAdapterPosition()).getPrice());
                    i.putExtra(SENDER_INTENT_COINS,data.get(getAdapterPosition()).getSender_money());
                    i.putExtra(INTENT_COINS,data.get(getAdapterPosition()).getAmount());
                    i.putExtra(INTENT_USER_ID,data.get(getAdapterPosition()).getUser_id());
                    i.putExtra(INTENT_FULLNAME,data.get(getAdapterPosition()).getFull_name());
                    i.putExtra(INTENT_LINEAR,"0");
                    context.startActivity(i);
                }
            });


        }

    }

}
