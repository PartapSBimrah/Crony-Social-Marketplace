package com.abhigam.www.foodspot;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sourabhzalke on 13/06/17.
 */

public class AdapterDiscoverRestaurants extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<DataRecycler> data = Collections.emptyList();
    DataRecycler current;
    private static final String places_key = "AIzaSyCrkw0c9C5Kd9b_gE2niYbA6I9atGQlScQ";

    private Context context;
    private LayoutInflater inflater;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterDiscoverRestaurants(Context context, List<DataRecycler> data) {
        this.context = context;
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

        RecyclerView.ViewHolder vh;
        View view = inflater.inflate(R.layout.container_discover_restaurants, parent, false);

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

            String url = "https://maps.googleapis.com/maps/api/place/photo?" +
                    "maxwidth=400&photoreference="+current.getPhoto_reference()+"&key="+
                    places_key;

            myHolder.image.setImageResource(R.drawable.gray);

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
                            myHolder.image.setImageBitmap(resource);
                        }
                    });

            myHolder.name.setText(current.getRestaurant_name());


        }
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView name,address;


        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            image =  itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);

        }


    }




}

