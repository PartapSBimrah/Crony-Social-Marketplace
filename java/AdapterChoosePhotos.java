package com.abhigam.www.foodspot;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.Collections;
import java.util.List;

/**
 * Created by sourabhzalke on 13/06/17.
 */

public class AdapterChoosePhotos extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<DataRecycler> data = Collections.emptyList();
    DataRecycler current;

    private Context context;
    private LayoutInflater inflater;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterChoosePhotos(Context context, List<DataRecycler> data) {
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

    public Bitmap getItemBitmap(int id){
          return data.get(id).getOriginalBitmap();
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder vh;
            View view = inflater.inflate(R.layout.container_choose_photos, parent, false);

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

                myHolder.image.setImageBitmap(current.mBitmap);
                myHolder.cancel.setVisibility(View.VISIBLE);


        }
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        private ImageView image,cancel;


        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            image = itemView.findViewById(R.id.image);
            cancel = itemView.findViewById(R.id.cancel);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.remove(getAdapterPosition());

                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(),getItemCount());
                }
            });

        }


    }




}
