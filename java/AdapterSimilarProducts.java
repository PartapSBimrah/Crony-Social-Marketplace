package com.abhigam.www.foodspot;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class AdapterSimilarProducts extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<DataRecycler> data = Collections.emptyList();
    DataRecycler current;

    private Context context;
    private FragmentActivity mContext;
    private LayoutInflater inflater;

    private static  final String INTENT_PRODUCT_ID = "com.abhigam.www.foodspot.product_id";
    private static  final String INTENT_PRODUCT_TITLE = "com.abhigam.www.foodspot.title";
    private static  final String INTENT_PRODUCT_DESCRIPTION = "com.abhigam.www.foodspot.description";
    private static  final String INTENT_PRODUCT_PRICE = "com.abhigam.www.foodspot.price";
    private static  final String INTENT_PRODUCT_IMAGE_COUNT = "com.abhigam.www.foodspot.image_count";
    private static  final String INTENT_PRODUCT_CATEGORY = "com.abhigam.www.foodspot.category";
    private static  final String INTENT_PRODUCT_USER_ID = "com.abhigam.www.foodspot.user_id";
    private static final String INTENT_PRODUCT_IMAGE_NAME = "com.abhigam.www.foodspot.image_name";
    private static final String INTENT_IS_IN_WISHLIST = "com.abhigam.www.foodspot.is_in_wishlist";
    private static final String INTENT_SELLER = "com.abhigam.www.foodspot.seller";

    // create constructor to initialize context and data sent from MainActivity
    public AdapterSimilarProducts(FragmentActivity context, List<DataRecycler> data){
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
        View view = inflater.inflate(R.layout.container_similar_products, parent, false);

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

            myHolder.productImage.setImageResource(0);

            Glide
                    .with(context)
                    .load("http://13.233.234.79/uploads/ad_images/"
                            +current.image_name+"_0.jpg")
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                    .skipMemoryCache(true)
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>(200, 200) { //width and height
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            myHolder.productImage.setImageBitmap(resource);
                        }
                    });

            myHolder.title.setText(current.getTitle());
            myHolder.description.setText(current.getDescription());
            myHolder.price.setText("₹"+current.getPrice());


        }
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView title,description,price;
        private LinearLayout mLinearLayout,mLinear;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            productImage = itemView.findViewById(R.id.product_image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            mLinearLayout = itemView.findViewById(R.id.linearLayout);
            mLinear = itemView.findViewById(R.id.linear);

            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,ProductDetails.class);
                    i.putExtra(INTENT_PRODUCT_ID,data.get(getAdapterPosition()).getProduct_id());
                    i.putExtra(INTENT_PRODUCT_CATEGORY,data.get(getAdapterPosition()).getCategory());
                    i.putExtra(INTENT_PRODUCT_IMAGE_COUNT,data.get(getAdapterPosition()).getProduct_image_count());
                    i.putExtra(INTENT_PRODUCT_PRICE,data.get(getAdapterPosition()).getPrice());
                    i.putExtra(INTENT_PRODUCT_DESCRIPTION,data.get(getAdapterPosition()).getDescription());
                    i.putExtra(INTENT_PRODUCT_TITLE,data.get(getAdapterPosition()).getTitle());
                    i.putExtra(INTENT_PRODUCT_USER_ID,data.get(getAdapterPosition()).getUser_id());
                    i.putExtra(INTENT_PRODUCT_IMAGE_NAME,data.get(getAdapterPosition()).getImage_name());
                    i.putExtra(INTENT_SELLER,data.get(getAdapterPosition()).getSeller());
                    i.putExtra(INTENT_IS_IN_WISHLIST,data.get(getAdapterPosition()).getIs_in_wishlist());
                    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(
                                mContext,mLinear,mLinear.getTransitionName())
                                .toBundle();
                        mContext.startActivity(i,bundle);
                    }else {
                        context.startActivity(i);
                    }
                }
            });

            productImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,ProductDetails.class);
                    i.putExtra(INTENT_PRODUCT_ID,data.get(getAdapterPosition()).getProduct_id());
                    i.putExtra(INTENT_PRODUCT_CATEGORY,data.get(getAdapterPosition()).getCategory());
                    i.putExtra(INTENT_PRODUCT_IMAGE_COUNT,data.get(getAdapterPosition()).getProduct_image_count());
                    i.putExtra(INTENT_PRODUCT_PRICE,data.get(getAdapterPosition()).getPrice());
                    i.putExtra(INTENT_PRODUCT_DESCRIPTION,data.get(getAdapterPosition()).getDescription());
                    i.putExtra(INTENT_PRODUCT_TITLE,data.get(getAdapterPosition()).getTitle());
                    i.putExtra(INTENT_PRODUCT_USER_ID,data.get(getAdapterPosition()).getUser_id());
                    i.putExtra(INTENT_PRODUCT_IMAGE_NAME,data.get(getAdapterPosition()).getImage_name());
                    i.putExtra(INTENT_SELLER,data.get(getAdapterPosition()).getSeller());
                    i.putExtra(INTENT_IS_IN_WISHLIST,data.get(getAdapterPosition()).getIs_in_wishlist());
                    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(
                                mContext,mLinear,mLinear.getTransitionName())
                                .toBundle();
                        mContext.startActivity(i,bundle);
                    }else {
                        context.startActivity(i);
                    }
                }
            });

        }


    }




}
