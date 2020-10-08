package com.abhigam.www.foodspot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.relex.circleindicator.CircleIndicator;


public class ProductDetailsFragment extends Fragment {

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



    private static final String URL_SIMILAR_PRODUCTS = "http://13.233.234.79/similar_products.php";
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

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

    private String product_id,product_image_count,product_title,product_price,product_category,
                   product_description,user_id,username,product_user_id,product_seller
            ,product_image,product_is_in_wishlist;

    private String URL_ADD_TO_WISHLIST = "http://13.233.234.79/add_to_wishlist.php";
    private String URL_REMOVE_FROM_WISHLIST = "http://13.233.234.79/remove_from_wishlist.php";

    //product_images
    ViewPager viewPager;
    List<DataRecycler> bannerDataRecycler;
    ViewPagerAdapter viewPagerAdapter;
    CircleIndicator indicator;

    private LinearLayout attached_buttons;
    private ImageView wishlist_added,wishlist;
    private TextView title,price,product_details,seller_text,toolbar_title;

    private RecyclerView mRecyclerView;
    private AdapterSimilarProducts mAdapterSimilarProducts;
    private List<DataRecycler> mDataRecyclers = new ArrayList<>();
    private WrapContentLinearLayoutManager linearLayoutManager;
    private LinearLayout save_product,buy_product;

    private ImageView back;
    private Toolbar mToolbar;
    private NestedScrollView mNestedScrollView;

    private TextView quote;
    private String[] quotes;
    private String quote_string;
    private static final String REQUEST_TAG = "request_tag";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //getting username and user_id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        username = prefs.getString(PREF_USER_NAME,"");
        user_id = prefs.getString(PREF_USER_ID,"");

        mRequestQueue = Volley.newRequestQueue(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_details,container,false);

        product_id = getActivity().getIntent().getStringExtra(INTENT_PRODUCT_ID);
        product_category = getActivity().getIntent().getStringExtra(INTENT_PRODUCT_CATEGORY);
        product_description = getActivity().getIntent().getStringExtra(INTENT_PRODUCT_DESCRIPTION);
        product_image_count = getActivity().getIntent().getStringExtra(INTENT_PRODUCT_IMAGE_COUNT);
        product_price = getActivity().getIntent().getStringExtra(INTENT_PRODUCT_PRICE);
        product_title = getActivity().getIntent().getStringExtra(INTENT_PRODUCT_TITLE);
        product_user_id = getActivity().getIntent().getStringExtra(INTENT_PRODUCT_USER_ID);
        product_image = getActivity().getIntent().getStringExtra(INTENT_PRODUCT_IMAGE_NAME);
        product_seller = getActivity().getIntent().getStringExtra(INTENT_SELLER);
        product_is_in_wishlist = getActivity().getIntent().getStringExtra(INTENT_IS_IN_WISHLIST);

        viewPager = v.findViewById(R.id.viewPager);
        indicator =  v.findViewById(R.id.indicator);
        title = v.findViewById(R.id.title);
        price = v.findViewById(R.id.price);
        product_details = v.findViewById(R.id.product_details);
        seller_text= v.findViewById(R.id.seller_text);
        buy_product = v.findViewById(R.id.buy_product);
        back = v.findViewById(R.id.back_arrow_image);
        mToolbar = v.findViewById(R.id.toolbar_top);
        mNestedScrollView = v.findViewById(R.id.nestedScrollview);
        toolbar_title = v.findViewById(R.id.toolbar_title);
        toolbar_title.setText(product_category);
        wishlist = v.findViewById(R.id.wishlist);
        wishlist_added = v.findViewById(R.id.wishlist_added);

        //getting quotes array
        quotes =  getResources().getStringArray(R.array.quotes);

        quote = v.findViewById(R.id.quote);

        //generating random no
        int idx = new Random().nextInt(quotes.length);
        quote_string = (quotes[idx]);

        //showing quote
        quote.setText(quote_string);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(v.getChildAt(v.getChildCount() - 1) != null) {

                    if (scrollY > oldScrollY) {
                        mToolbar.setBackgroundColor(getResources().getColor(R.color.white));
                        back.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                                android.graphics.PorterDuff.Mode.MULTIPLY);
                        toolbar_title.setVisibility(View.VISIBLE);
                    }else if(scrollY == 0){
                        mToolbar.setBackgroundColor(Color.TRANSPARENT);
                        back.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white),
                                android.graphics.PorterDuff.Mode.MULTIPLY);
                        toolbar_title.setVisibility(View.GONE);
                    }
                }
            }
        });

        if(Integer.parseInt(product_is_in_wishlist)==1){
            wishlist.setVisibility(View.GONE);
            wishlist_added.setVisibility(View.VISIBLE);
        }

        wishlist_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromWishlist(user_id,product_id);
                wishlist_added.setVisibility(View.GONE);
                wishlist.setVisibility(View.VISIBLE);
            }
        });


        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWishlist(user_id,product_id);
                wishlist.setVisibility(View.GONE);
                wishlist_added.setVisibility(View.VISIBLE);
            }
        });

        if(product_seller.equals("2"))
        seller_text.setText("Crony User");
        else
        seller_text.setText("Crony Merchant");

        buy_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),BeforePayout.class);
                i.putExtra(INTENT_PRODUCT_ID,product_id);
                i.putExtra(INTENT_PRODUCT_CATEGORY,product_category);
                i.putExtra(INTENT_PRODUCT_IMAGE_COUNT,product_image_count);
                i.putExtra(INTENT_PRODUCT_PRICE,product_price);
                i.putExtra(INTENT_PRODUCT_DESCRIPTION,product_description);
                i.putExtra(INTENT_PRODUCT_TITLE,product_title);
                i.putExtra(INTENT_PRODUCT_USER_ID,product_user_id);
                i.putExtra(INTENT_PRODUCT_IMAGE_NAME,product_image);
                i.putExtra(INTENT_SELLER,product_seller);
                getActivity().startActivity(i);
            }
        });

        //setting texts
        title.setText(product_title);
        price.setText("₹"+product_price);
        product_details.setText(product_description);

        // retrieve display dimensions
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        //setting margins around imageimageview
        params.height=(int)(displayRectangle.width() * 1.2f); //left, top, right, bottom
        //adding attributes to the imageview
        viewPager.setLayoutParams(params);

        attached_buttons = v.findViewById(R.id.attached_buttons);

        //ALL of Recyclerview
        mRecyclerView = v.findViewById(R.id.recycler_similar_products);
        mRecyclerView.setNestedScrollingEnabled(false);

        mAdapterSimilarProducts = new AdapterSimilarProducts(getActivity(),mDataRecyclers);
        linearLayoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapterSimilarProducts);


        //only in oncreate
        getProductImages();
        mDataRecyclers.clear();
        mRecyclerView.getRecycledViewPool().clear();
        getSimilarProducts();

        return v;
    }

    private void getProductImages(){

        bannerDataRecycler = new ArrayList<>();

        for(int i=0;i<Integer.parseInt(product_image_count);++i){

            DataRecycler dataRecycler = new DataRecycler();
            dataRecycler.setBannerUrl("http://13.233.234.79/uploads/ad_images/"+
                    product_image+"_"+i+".jpg");
            bannerDataRecycler.add(dataRecycler);
        }

        viewPagerAdapter = new ViewPagerAdapter(bannerDataRecycler, getActivity());
        viewPager.setAdapter(viewPagerAdapter);
        indicator.setViewPager(viewPager);
        viewPagerAdapter.registerDataSetObserver(indicator.getDataSetObserver());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getSimilarProducts(){

        mStringRequest = new StringRequest(Request.Method.POST, URL_SIMILAR_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonArray = jsonResponse.getJSONArray("similar_products");
                            for (int i=0;i<jsonArray.length();++i){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                DataRecycler item_data = new DataRecycler();
                                item_data.user_id = jsonObject.getString("user_id");
                                item_data.product_id = jsonObject.getString("id");
                                String title = jsonObject.getString("title");
                                String description = jsonObject.getString("description");
                                String image = jsonObject.getString("user_id") + "_" +
                                        title +
                                        "_" + description.substring(0, 10);
                                item_data.image_name = image;
                                item_data.title = jsonObject.getString("title");
                                item_data.description = jsonObject.getString("description");
                                item_data.price = jsonObject.getString("expected_price");
                                item_data.category = jsonObject.getString("category");
                                item_data.is_in_wishlist = jsonObject.getString("is_wishlist");
                                item_data.seller = jsonObject.getString("seller");
                                item_data.product_image_count = jsonObject.getString("image_count");
                                if(!jsonObject.getString("id").equals(product_id)
                                        && jsonObject.getString("verified").equals("1"))
                                mDataRecyclers.add(item_data);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mAdapterSimilarProducts.notifyDataSetChanged();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("category",product_category);
                hashMap.put("user_id",user_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mStringRequest.setTag(REQUEST_TAG);
        mRequestQueue.add(mStringRequest);

    }

    @Override
    public void onPause() {
        super.onPause();
        mRequestQueue.cancelAll(REQUEST_TAG);
    }

    private void addToWishlist(final String user_id, final String item_id){

        mStringRequest = new StringRequest(Request.Method.POST, URL_ADD_TO_WISHLIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(getActivity(),"Added to Wishlist",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
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

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    private void removeFromWishlist(final String user_id,final String item_id){

        mStringRequest = new StringRequest(Request.Method.POST, URL_REMOVE_FROM_WISHLIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        //do nothing
                    }
                } catch (JSONException e) {
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

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }



}
