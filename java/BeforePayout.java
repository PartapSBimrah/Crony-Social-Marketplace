package com.abhigam.www.foodspot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class BeforePayout extends AppCompatActivity {

    private String INTENT_ENROLLMENT = "com.abhigam.www.foodspot.enrollment";
    private String INTENT_FULLNAME = "com.abhigam.www.foodspot.fullname";
    private String INTENT_USER_ID = "com.abhigam.www.foodspot.user_id";
    private String RECIEVER_INTENT_COINS = "com.abhigam.www.foodspot.reciever_intent_coins";
    private String SENDER_INTENT_COINS = "com.abhigam.www.foodspot.sender_intent_coins";
    private String INTENT_COINS = "com.abhigam.www.foodspot.intent_coins";
    private String INTENT_LINEAR = "com.abhigam.www.foodspot.linear";
    private static final String URL_MERCHANT = "http://13.233.234.79/merchant_id.php";
    private String URL_USER_DETAILS = "http://13.233.234.79/user_id.php";

    private static  final String INTENT_PRODUCT_ID = "com.abhigam.www.foodspot.product_id";
    private static  final String INTENT_PRODUCT_TITLE = "com.abhigam.www.foodspot.title";
    private static  final String INTENT_PRODUCT_DESCRIPTION = "com.abhigam.www.foodspot.description";
    private static  final String INTENT_PRODUCT_PRICE = "com.abhigam.www.foodspot.price";
    private static  final String INTENT_PRODUCT_IMAGE_COUNT = "com.abhigam.www.foodspot.image_count";
    private static  final String INTENT_PRODUCT_CATEGORY = "com.abhigam.www.foodspot.category";
    private static  final String INTENT_PRODUCT_USER_ID = "com.abhigam.www.foodspot.user_id";
    private static final String INTENT_PRODUCT_IMAGE_NAME = "com.abhigam.www.foodspot.image_name";
    private static final String INTENT_SELLER = "com.abhigam.www.foodspot.seller";

    //prefs
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

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    private TextView title,total_amount,description,category;
    private String title_string,total_amount_string,description_string,
            category_string,image_string,username,user_id,user_fullname,
            product_id,sender_money;
    private ImageView mImageView;
    private CircularProgressButton mCircularProgressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_payout);
        mRequestQueue = Volley.newRequestQueue(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        username = prefs.getString(PREF_USER_NAME,"");
        user_id = prefs.getString(PREF_USER_ID,"");
        user_fullname = prefs.getString(PREF_FULLNAME,"");

        Toolbar toolbar = findViewById(R.id.toolbar_top);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        title = findViewById(R.id.title);
        total_amount = findViewById(R.id.total_amount);
        description = findViewById(R.id.product_details);
        category = findViewById(R.id.category);
        mImageView = findViewById(R.id.product_image);
        mCircularProgressButton = findViewById(R.id.proceed);

        title_string = getIntent().getStringExtra(INTENT_PRODUCT_TITLE);
        total_amount_string = getIntent().getStringExtra(INTENT_PRODUCT_PRICE);
        description_string = getIntent().getStringExtra(INTENT_PRODUCT_DESCRIPTION);
        category_string = getIntent().getStringExtra(INTENT_PRODUCT_CATEGORY);
        image_string = getIntent().getStringExtra(INTENT_PRODUCT_IMAGE_NAME);
        product_id = getIntent().getStringExtra(INTENT_PRODUCT_ID);

        title.setText(title_string);
        total_amount.setText("₹"+total_amount_string);
        description.setText(description_string);
        category.setText(category_string);

        Glide
                .with(this)
                .load("http://13.233.234.79/uploads/ad_images/"+
                        image_string+"_0.jpg")
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                .skipMemoryCache(true)
                .into(mImageView);


        mCircularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                getUserIDFirst(username);
            }
        });


    }

    private void merchantCheck(){
        mStringRequest = new StringRequest(Request.Method.POST, URL_MERCHANT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Intent i = new Intent(getApplicationContext(),AmountTransferMoney.class);
                    i.putExtra(INTENT_ENROLLMENT,"cronymerchant");
                    i.putExtra(RECIEVER_INTENT_COINS,jsonObject.getString("balance"));
                    i.putExtra(SENDER_INTENT_COINS,sender_money);
                    i.putExtra(INTENT_COINS,total_amount_string);
                    i.putExtra(INTENT_USER_ID,user_id);
                    i.putExtra(INTENT_FULLNAME,user_fullname);
                    i.putExtra(INTENT_PRODUCT_ID,product_id);
                    i.putExtra(INTENT_LINEAR,"3");
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("username","cronymerchant");
                return hashMap;
            }

        };
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void getUserIDFirst(final String enrollment){

        mStringRequest = new StringRequest(Request.Method.POST, URL_USER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            sender_money = jsonObject.getString("coins");
                            merchantCheck();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("enrollment",enrollment);
                return hashMap;
            }
        };
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
