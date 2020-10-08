package com.abhigam.www.foodspot;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sourabhzalke on 13/06/17.
 */

public class AdapterStores extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<DataRecycler> data = Collections.emptyList();
    DataRecycler current;

    private String INTENT_ENROLLMENT = "com.abhigam.www.foodspot.enrollment";
    private String INTENT_FULLNAME = "com.abhigam.www.foodspot.fullname";
    private String INTENT_USER_ID = "com.abhigam.www.foodspot.user_id";
    private String RECIEVER_INTENT_COINS = "com.abhigam.www.foodspot.reciever_intent_coins";
    private String SENDER_INTENT_COINS = "com.abhigam.www.foodspot.sender_intent_coins";
    private String INTENT_COINS = "com.abhigam.www.foodspot.intent_coins";
    private String INTENT_LINEAR = "com.abhigam.www.foodspot.linear";

    private Context context;
    private FragmentActivity mContext;
    private LayoutInflater inflater;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterStores(FragmentActivity context, List<DataRecycler> data){
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        setHasStableIds(true);
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

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        View view = inflater.inflate(R.layout.container_stores, parent, false);

        vh = new MyHolder(view);
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder) {
            // Get current position of item in RecyclerView to bind data and assign values from list
            final MyHolder myHolder = (MyHolder) holder;
            DataRecycler current = data.get(position);
            //Instantiate the RequestQueue
            myHolder.store.setText(current.getStore_name());

        }
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        private Button store;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            store = itemView.findViewById(R.id.store_id);

            store.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mContext instanceof PayOrTransfer){
                        String amount = ((PayOrTransfer)mContext).checkAmount();
                        if(amount!=null){
                            String store_name = data.get(getAdapterPosition()).getStore_name();
                            Intent i = new Intent(context, AmountTransferMoney.class);
                            i.putExtra(INTENT_ENROLLMENT, store_name);
                            i.putExtra(RECIEVER_INTENT_COINS, data.get(getAdapterPosition()).getMer_balance());
                            i.putExtra(SENDER_INTENT_COINS, data.get(getAdapterPosition()).getSender_money());
                            i.putExtra(INTENT_COINS, amount);
                            i.putExtra(INTENT_USER_ID, data.get(getAdapterPosition()).getUser_id());
                            i.putExtra(INTENT_FULLNAME, data.get(getAdapterPosition()).getFullname());
                            i.putExtra(INTENT_LINEAR, "2");
                            context.startActivity(i);
                        }
                    }

                }
            });


        }


    }




}
