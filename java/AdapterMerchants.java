package com.abhigam.www.foodspot;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.Collections;
import java.util.List;

/**
 * Created by sourabhzalke on 13/06/17.
 */
public class AdapterMerchants extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private RequestQueue mRequestQueue;
    private StringRequest request;
    private static final String PREF_USER_ID = "pref_user_id";
    public String user_check_id;


    List<DataRecycler> data = Collections.emptyList();
    DataRecycler current;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private Context context;
    private LayoutInflater inflater;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterMerchants(Context context, List<DataRecycler> data) {
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
        View view = inflater.inflate(R.layout.container_merchants, parent, false);

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
            final DataRecycler current = data.get(position);

            final String URL = "http://13.233.234.79/uploads/merchant_pic/"
                    + current.getMer_username()
                    +".jpg";

            myHolder.mer_image.setImageResource(R.drawable.dark_gradient);

            Glide
                    .with(context)
                    .load(URL)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(myHolder.mer_image.getMaxWidth(),
                            myHolder.mer_image.getMaxHeight()) { //width and height
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            myHolder.mer_image.setImageBitmap(resource);
                            current.setBitmap(resource);
                        }
                    });
            //Instantiate the RequestQueue
            myHolder.mer_name.setText(current.getStore_name());
            myHolder.num_hits.setText("1k");

        }
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        private ImageView mer_image;
        private TextView mer_name,num_hits;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            mer_image = itemView.findViewById(R.id.mer_image);
            mer_name = itemView.findViewById(R.id.mer_name);
            num_hits = itemView.findViewById(R.id.num_hits);


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
