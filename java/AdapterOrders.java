package com.abhigam.www.foodspot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.preference.PreferenceManager;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sourabhzalke on 13/06/17.
 */

public class AdapterOrders extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private RequestQueue mRequestQueue;
    private StringRequest request;
    long timeInMillis;
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
    public String user_check_id;

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

    private static final String URL_REMOVE_FROM_NOTEBOOK = "http://13.233.234.79/remove_from_notebook.php";

    List<DataRecycler> data = Collections.emptyList();
    DataRecycler current;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private Context context;
    private LayoutInflater inflater;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterOrders(Context context, List<DataRecycler> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        mRequestQueue = Volley.newRequestQueue(context);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        user_check_id = preferences.getString(PREF_USER_ID, "");
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

    @Override
    public int getItemViewType(int position) {

        return data.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        View view = inflater.inflate(R.layout.container_my_orders, parent, false);

        vh = new MyHolder(view);
        /*else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        */
        return vh;
    }


    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder){
            // Get current position of item in RecyclerView to bind data and assign values from list
            final MyHolder myHolder = (MyHolder) holder;
            DataRecycler current = data.get(position);
            //Instantiate the RequestQueue



            myHolder.mProductImage.setImageResource(0);

                    Glide
                    .with(context)
                    .load("http://13.233.234.79/uploads/ad_images/"+
                            current.getImage_name()+"_0.jpg")
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                    .skipMemoryCache(true)
                    .centerCrop()
                    .into(myHolder.mProductImage);


            myHolder.token.setText(current.getToken());
            myHolder.title.setText(current.getTitle());
            myHolder.description.setText(current.getDescription());
            myHolder.category.setText(current.getCategory());
            myHolder.price.setText("₹"+current.getPrice());
            if(current.getStatus().equals("0")){
                myHolder.status.setText("PROCESSING");
                myHolder.status.setTextColor(context.getResources().getColor(R.color.my_advertise));
            }else if(current.getStatus().equals("1")){
                myHolder.status.setText("READY TO PICK UP");
                myHolder.status.setTextColor(context.getResources().getColor(R.color.my_wallet));
            }else if(current.getStatus().equals("2")){
                myHolder.status.setText("ORDER SUCCESSFUL");
                myHolder.status.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }


            long time = Long.parseLong(current.getTime_ago());
            long now = System.currentTimeMillis();

            CharSequence min = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS);
            myHolder.time_ago.setText(min);

        }
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        private TextView title,description,time_ago,category,price,status;
        private ImageView mProductImage;
        private LinearLayout mProduct;
        private TextView token;


        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            title = itemView.findViewById(R.id.title);
            mProductImage = itemView.findViewById(R.id.product_image);
            category = itemView.findViewById(R.id.category);
            description = itemView.findViewById(R.id.product_details);
            price = itemView.findViewById(R.id.total_amount);
            mProduct = itemView.findViewById(R.id.product);
            status = itemView.findViewById(R.id.status);
            time_ago = itemView.findViewById(R.id.time_ago);
            token = itemView.findViewById(R.id.token);

        }

    }

    /*
    public  class ProgressViewHolder extends RecyclerView.ViewHolder {
        public AVLoadingIndicatorView progressBar;

        public ProgressViewHolder(View v){
            super(v);
            progressBar = (AVLoadingIndicatorView) v.findViewById(R.id.progressBar1);
        }
    }
*/

}
