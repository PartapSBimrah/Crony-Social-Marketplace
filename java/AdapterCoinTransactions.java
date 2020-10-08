package com.abhigam.www.foodspot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
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

public class AdapterCoinTransactions extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    List<DataRecycler> data= Collections.emptyList();
    DataRecycler current;
    private String main_text;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    private static final String USERNAME_VISITOR = "visitor_username";

    private String user_id,user,user_fullname,username;

    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME= "pref_username";
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;


    private Context context;
    private LayoutInflater inflater;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterCoinTransactions(Context context, List<DataRecycler> data){
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
            View view = inflater.inflate(R.layout.container_transaction, parent, false);

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
            DataRecycler current = data.get(position);

            myHolder.enrollment.setText(current.getEnrollment());
            myHolder.fullname.setText(current.getFullname());
            myHolder.txnId.setText("Txn Id: #" + current.txnId);

            Glide
                    .with(context)
                    .load("http://13.233.234.79/uploads/profile_pic/" +
                            current.getEnrollment() + "_profile.jpg")
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

            long time = Long.parseLong(current.getTime_ago());
            long now = System.currentTimeMillis();

            CharSequence min = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS);
            if (min.equals("0 seconds ago"))
                myHolder.time_ago.setText(context.getResources().getString(R.string.just_now));
            else
                myHolder.time_ago.setText(min);

            if (current.getType().equals("1")) {
                myHolder.amount.setText("+ ₹" + current.getAmount());
                myHolder.amount.setTextColor(context.getResources().getColor(R.color.dark_green));
            } else {
                myHolder.amount.setText("- ₹" + current.getAmount());
                myHolder.amount.setTextColor(Color.RED);
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

        private TextView time_ago,amount,fullname,enrollment,txnId;
        private CircleImageView image;
        private LinearLayout mLinearLayout;

        // create constructor to get widget reference
        private MyHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            time_ago = itemView.findViewById(R.id.time_ago);
            amount = itemView.findViewById(R.id.amount);
            image = itemView.findViewById(R.id.circular_profile);
            fullname = itemView.findViewById(R.id.fullname);
            enrollment = itemView.findViewById(R.id.enrollment);
            mLinearLayout = itemView.findViewById(R.id.linear);
            txnId = itemView.findViewById(R.id.txnId);

            mLinearLayout.setOnClickListener(new View.OnClickListener() {
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

}
