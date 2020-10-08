package com.abhigam.www.foodspot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.Collections;
import java.util.List;

/**
 * Created by sourabhzalke on 13/06/17.
 */

public class AdapterMyAdvertisement extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static  final String INTENT_PRODUCT_ID = "com.abhigam.www.foodspot.product_id";
    private static  final String INTENT_PRODUCT_TITLE = "com.abhigam.www.foodspot.title";
    private static  final String INTENT_PRODUCT_DESCRIPTION = "com.abhigam.www.foodspot.description";
    private static  final String INTENT_PRODUCT_PRICE = "com.abhigam.www.foodspot.price";
    private static  final String INTENT_PRODUCT_IMAGE_COUNT = "com.abhigam.www.foodspot.image_count";
    private static  final String INTENT_PRODUCT_CATEGORY = "com.abhigam.www.foodspot.category";
    private static  final String INTENT_PRODUCT_USER_ID = "com.abhigam.www.foodspot.user_id";
    private static final String INTENT_PRODUCT_IMAGE_NAME = "com.abhigam.www.foodspot.image_name";
    private static final String INTENT_SELLER = "com.abhigam.www.foodspot.seller";
    private static final String INTENT_IS_IN_WISHLIST = "com.abhigam.www.foodspot.is_in_wishlist";

    List<DataRecycler> data= Collections.emptyList();
    DataRecycler current;

    private Context context;
    private LayoutInflater inflater;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterMyAdvertisement(Context context, List<DataRecycler> data){
        this.context=context;
        inflater = LayoutInflater.from(context);
        this.data=data;
    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.container_my_advertisement, parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }


    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in RecyclerView to bind data and assign values from list
        final MyHolder myHolder= (MyHolder) holder;
        DataRecycler current=data.get(position);

        myHolder.title.setText(current.getTitle());
        myHolder.category.setText(current.getCategory());
        myHolder.description.setText(current.getDescription());
        myHolder.token.setText(current.getToken());

        myHolder.image.setImageResource(0);

                Glide
                .with(context)
                .load("http://13.233.234.79/uploads/ad_images/"+current.getImage_name()+"_0.jpg")
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

        myHolder.amount.setText("₹"+current.getAmount());
        if(current.getStatus().equals("0")){
            myHolder.status.setText("PROCESSING");
            myHolder.status.setTextColor(context.getResources().getColor(R.color.my_notebook));
        }else if(current.getStatus().equals("1")){
            myHolder.status.setText("LIVE");
            myHolder.status.setTextColor(context.getResources().getColor(R.color.dark_green));
        }else if(current.getStatus().equals("2")){
            myHolder.status.setText("SOLD");
            myHolder.status.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }else if(current.getStatus().equals("3")){
            myHolder.status.setText("DENIED");
            myHolder.status.setTextColor(context.getResources().getColor(R.color.my_advertise));
        }


    }


    private class MyHolder extends RecyclerView.ViewHolder{

        private TextView time_ago,token,amount,title,description,category,status;
        private ImageView image;
        private LinearLayout mLinearLayout;

        // create constructor to get widget reference
        private MyHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            time_ago = itemView.findViewById(R.id.time_ago);
            amount = itemView.findViewById(R.id.amount);
            image = itemView.findViewById(R.id.ad_first_image);
            title = itemView.findViewById(R.id.title);
            category = itemView.findViewById(R.id.category);
            description = itemView.findViewById(R.id.description);
            mLinearLayout = itemView.findViewById(R.id.linear);
            time_ago = itemView.findViewById(R.id.time_ago);
            token = itemView.findViewById(R.id.token);
            status = itemView.findViewById(R.id.status);

            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,ProductDetails.class);
                    i.putExtra(INTENT_PRODUCT_ID,data.get(getAdapterPosition()).getProduct_id());
                    i.putExtra(INTENT_PRODUCT_CATEGORY,data.get(getAdapterPosition()).getCategory());
                    i.putExtra(INTENT_PRODUCT_IMAGE_COUNT,data.get(getAdapterPosition()).getProduct_image_count());
                    i.putExtra(INTENT_PRODUCT_PRICE,data.get(getAdapterPosition()).getAmount());
                    i.putExtra(INTENT_PRODUCT_DESCRIPTION,data.get(getAdapterPosition()).getDescription());
                    i.putExtra(INTENT_PRODUCT_TITLE,data.get(getAdapterPosition()).getTitle());
                    i.putExtra(INTENT_PRODUCT_USER_ID,data.get(getAdapterPosition()).getUser_id());
                    i.putExtra(INTENT_PRODUCT_IMAGE_NAME,data.get(getAdapterPosition()).getImage_name());
                    i.putExtra(INTENT_SELLER,data.get(getAdapterPosition()).getSeller());
                    i.putExtra(INTENT_IS_IN_WISHLIST,data.get(getAdapterPosition()).getIs_in_wishlist());
                    context.startActivity(i);
                }
            });

        }

    }

}
