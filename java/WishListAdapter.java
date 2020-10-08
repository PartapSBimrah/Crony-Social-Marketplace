package com.abhigam.www.foodspot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sourabhzalke on 31/05/17.
 */

public class WishListAdapter extends BaseAdapter{

    private final Context mContext;
    List<DataRecycler> data= Collections.emptyList();
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String URL_REMOVE_FROM_WISHLIST = "http://13.233.234.79/remove_from_wishlist.php";
    private InterfaceForAdapter listener;

    private static  final String INTENT_PRODUCT_ID = "com.abhigam.www.foodspot.product_id";
    private static  final String INTENT_PRODUCT_TITLE = "com.abhigam.www.foodspot.title";
    private static  final String INTENT_PRODUCT_DESCRIPTION = "com.abhigam.www.foodspot.description";
    private static  final String INTENT_PRODUCT_PRICE = "com.abhigam.www.foodspot.price";
    private static  final String INTENT_PRODUCT_IMAGE_COUNT = "com.abhigam.www.foodspot.image_count";
    private static  final String INTENT_PRODUCT_CATEGORY = "com.abhigam.www.foodspot.category";
    private static  final String INTENT_PRODUCT_USER_ID = "com.abhigam.www.foodspot.user_id";
    private static final String INTENT_IS_IN_WISHLIST = "com.abhigam.www.foodspot.is_in_wishlist";
    private static final String INTENT_SELLER = "com.abhigam.www.foodspot.seller";
    private static final String INTENT_PRODUCT_IMAGE_NAME = "com.abhigam.www.foodspot.image_name";


    private String user_id;
    private static final String PREF_USER_ID = "pref_user_id";

    // 1
    public WishListAdapter(Context context, List<DataRecycler> data) {
        this.mContext = context;
        this.data = data;
        mRequestQueue = Volley.newRequestQueue(mContext);
        SharedPreferences prefs = SaveSharedPreference.getSharedPreferences(mContext);
        user_id = prefs.getString(PREF_USER_ID,"");
    }




    // 2
    @Override
    public int getCount() {

        return data.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        final DataRecycler current=data.get(position);
        // 2
        if (convertView == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.container_wishlist_data, null);
        }



        // 3
        final SquareImageView imageView = convertView.findViewById(R.id.item_image);
        TextView title = convertView.findViewById(R.id.title);
        TextView description = convertView.findViewById(R.id.description);
        TextView price = convertView.findViewById(R.id.price);
        ImageView cancel_item = convertView.findViewById(R.id.cancel);
        LinearLayout buy_now = convertView.findViewById(R.id.buy_now);

        buy_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,ProductDetails.class);
                i.putExtra(INTENT_PRODUCT_ID,current.getProduct_id());
                i.putExtra(INTENT_PRODUCT_CATEGORY,current.getCategory());
                i.putExtra(INTENT_PRODUCT_IMAGE_COUNT,current.getProduct_image_count());
                i.putExtra(INTENT_PRODUCT_PRICE,current.getPrice());
                i.putExtra(INTENT_PRODUCT_DESCRIPTION,current.getDescription());
                i.putExtra(INTENT_PRODUCT_TITLE,current.getTitle());
                i.putExtra(INTENT_PRODUCT_USER_ID,current.getUser_id());
                i.putExtra(INTENT_IS_IN_WISHLIST,"1");
                i.putExtra(INTENT_SELLER,current.getSeller());
                i.putExtra(INTENT_PRODUCT_IMAGE_NAME,current.getImage_name());
                mContext.startActivity(i);
            }
        });

        cancel_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromWishlist(user_id,current.getProduct_id(),position);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,ProductDetails.class);
                i.putExtra(INTENT_PRODUCT_ID,current.getProduct_id());
                i.putExtra(INTENT_PRODUCT_CATEGORY,current.getCategory());
                i.putExtra(INTENT_PRODUCT_IMAGE_COUNT,current.getProduct_image_count());
                i.putExtra(INTENT_PRODUCT_PRICE,current.getPrice());
                i.putExtra(INTENT_PRODUCT_DESCRIPTION,current.getDescription());
                i.putExtra(INTENT_PRODUCT_TITLE,current.getTitle());
                i.putExtra(INTENT_PRODUCT_USER_ID,current.getUser_id());
                i.putExtra(INTENT_IS_IN_WISHLIST,"1");
                i.putExtra(INTENT_SELLER,current.getSeller());
                i.putExtra(INTENT_PRODUCT_IMAGE_NAME,current.getImage_name());
                mContext.startActivity(i);
            }
        });

        LinearLayout linearLayout  = convertView.findViewById(R.id.crony_market_item);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,ProductDetails.class);
                i.putExtra(INTENT_PRODUCT_ID,current.getProduct_id());
                i.putExtra(INTENT_PRODUCT_CATEGORY,current.getCategory());
                i.putExtra(INTENT_PRODUCT_IMAGE_COUNT,current.getProduct_image_count());
                i.putExtra(INTENT_PRODUCT_PRICE,current.getPrice());
                i.putExtra(INTENT_PRODUCT_DESCRIPTION,current.getDescription());
                i.putExtra(INTENT_PRODUCT_TITLE,current.getTitle());
                i.putExtra(INTENT_PRODUCT_USER_ID,current.getUser_id());
                i.putExtra(INTENT_IS_IN_WISHLIST,"1");
                i.putExtra(INTENT_SELLER,current.getSeller());
                i.putExtra(INTENT_PRODUCT_IMAGE_NAME,current.getImage_name());
                mContext.startActivity(i);
            }
        });


        // 4
        Glide.with(mContext).load("http://13.233.234.79/uploads/ad_images/"
                +current.image_name+"_0.jpg").into(imageView);
        title.setText(current.getTitle());
        description.setText(current.getDescription());
        price.setText("₹"+current.getPrice());

        return convertView;
    }



    private void removeFromWishlist(final String user_id,final String item_id,final int position){

        mStringRequest = new StringRequest(Request.Method.POST, URL_REMOVE_FROM_WISHLIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        data.remove(position);
                        notifyDataSetChanged();
                        if(mContext instanceof WishList){
                            ((WishList)mContext).activityOnRemoved();
                        }
                        }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("item_id",item_id);
                return hashMap;
            }
        };

        mRequestQueue.add(mStringRequest);

    }

}
