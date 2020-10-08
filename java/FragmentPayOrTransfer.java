package com.abhigam.www.foodspot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

/**
 * Created by sourabhzalke on 14/04/18.
 */

public class FragmentPayOrTransfer extends Fragment {

    private EditText amount,store_id;
    private TextView rupee,wallet_balance;
    private ImageView back_button;
    private StringRequest mStringRequest;
    private RequestQueue mRequestQueue;
    private String URL_USER_DETAILS = "http://13.233.234.79/user_id.php";
    private String sender_user_id,sender_money,username,user_fullname;
    //prefs
    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME= "pref_username";
    private String INTENT_ENROLLMENT = "com.abhigam.www.foodspot.enrollment";
    private String INTENT_FULLNAME = "com.abhigam.www.foodspot.fullname";
    private String INTENT_USER_ID = "com.abhigam.www.foodspot.user_id";
    private String RECIEVER_INTENT_COINS = "com.abhigam.www.foodspot.reciever_intent_coins";
    private String SENDER_INTENT_COINS = "com.abhigam.www.foodspot.sender_intent_coins";
    private String INTENT_COINS = "com.abhigam.www.foodspot.intent_coins";
    private String INTENT_LINEAR = "com.abhigam.www.foodspot.linear";
    private static final String URL_MERCHANT = "http://13.233.234.79/merchant_id.php";

    private CircularProgressButton mPayMoney;
    private LinearLayout mTransferMoney,mAddMoney;
    private StringRequest request;
    private ProgressBar merchantProgressBar;

    private RecyclerView mRecyclerView;
    private AdapterStores mAdapterStores;
    private List<DataRecycler> mDataRecyclers = new ArrayList<>();
    private WrapContentLinearLayoutManager linearLayoutManager;
    private NestedScrollView mNestedScrollView;
    private LinearLayout wallet_linear;

    private Toolbar mToolbar;

    private String URL_STORES = "http://13.233.234.79/get_merchants.php";
    private static final String REQUEST_TAG = "request_tag";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //getting username and user_id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        username = prefs.getString(PREF_USER_NAME,"");
        user_fullname = prefs.getString(PREF_FULLNAME,"");
        mRequestQueue = Volley.newRequestQueue(getActivity());
        getUserIDFirst(username);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pay_or_transfer,container,false);

        amount = v.findViewById(R.id.amount);
        rupee = v.findViewById(R.id.rupee);
        wallet_balance = v.findViewById(R.id.balance);
        mTransferMoney = v.findViewById(R.id.transfer_money);
        mAddMoney = v.findViewById(R.id.add_money);
        merchantProgressBar = v.findViewById(R.id.progressBar1);
        mPayMoney = v.findViewById(R.id.pay_money);
        store_id = v.findViewById(R.id.store_id);
        mNestedScrollView = v.findViewById(R.id.nestedScrollview);
        mToolbar = v.findViewById(R.id.toolbar_top);
        wallet_linear = v.findViewById(R.id.wallet_linear);
        wallet_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),CoinTransactions.class);
                getActivity().startActivity(i);
            }
        });

        back_button = v.findViewById(R.id.back_arrow_image);

        back_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               getActivity().onBackPressed();
           }
        });

        mTransferMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(amount.getText().length()==0) {
                    enterAmount();
                }else {
                    Intent i = new Intent(getActivity(), SearchForTransfer.class);
                    String am = amount.getText().toString().replace(",","");
                    i.putExtra(INTENT_COINS,am);
                    i.putExtra(SENDER_INTENT_COINS,sender_money);
                    getActivity().startActivity(i);
                }
            }
        });

        Window window = getActivity().getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        }

        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //bahar hi rakhna

                if (scrollY > oldScrollY){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        mToolbar.setElevation((float)8.0);
                    }
                }else if(scrollY == 0){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        mToolbar.setElevation((float)0);
                    }
                }
            }
        });


        mAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(amount.getText().length()==0) {
                    enterAmount();
                }else {
                    String am = amount.getText().toString().replace(",","");
                    Intent i = new Intent(getActivity(),AmountTransferMoney.class);
                    i.putExtra(INTENT_ENROLLMENT,username);
                    i.putExtra(RECIEVER_INTENT_COINS,sender_money);
                    i.putExtra(SENDER_INTENT_COINS,sender_money);
                    i.putExtra(INTENT_COINS,am);
                    i.putExtra(INTENT_USER_ID,sender_user_id);
                    i.putExtra(INTENT_FULLNAME,user_fullname);
                    i.putExtra(INTENT_LINEAR,"1");
                    getActivity().startActivity(i);
                }
            }
        });

        mPayMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(amount.getText().length()==0) {
                   enterAmount();
                }else if(store_id.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"Please enter or select Store ID",
                            Toast.LENGTH_SHORT).show();
                    store_id.requestFocus();
                    InputMethodManager inputMethodManager =
                            (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert inputMethodManager != null;
                    inputMethodManager.toggleSoftInputFromWindow(
                            amount.getApplicationWindowToken(),
                            InputMethodManager.SHOW_FORCED, 0);

                } else {
                    merchantProgressBar.setVisibility(View.VISIBLE);
                    merchantCheck();
                }
            }
        });

        //ALL of Recyclerview
        mRecyclerView = v.findViewById(R.id.stores);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapterStores = new AdapterStores(getActivity(),mDataRecyclers);
        linearLayoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapterStores);

        return v;
    }

    private void getStores(){

        mStringRequest = new StringRequest(Request.Method.GET, URL_STORES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonArray = jsonResponse.getJSONArray("merchants");
                            for (int i=0;i<jsonArray.length();++i){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                DataRecycler item_data = new DataRecycler();
                                item_data.store_name = jsonObject.getString("mer_username");
                                item_data.mer_balance = jsonObject.getString("balance");
                                item_data.sender_money = sender_money;
                                item_data.user_id = sender_user_id;
                                item_data.fullname = user_fullname;
                                if(!item_data.store_name.equals("cronymerchant"))
                                mDataRecyclers.add(item_data);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mAdapterStores.notifyDataSetChanged();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll(REQUEST_TAG);
    }

    private void merchantCheck(){
        request = new StringRequest(Request.Method.POST, URL_MERCHANT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    merchantProgressBar.setVisibility(View.GONE);
                    String am = amount.getText().toString();
                    Intent i = new Intent(getActivity(),AmountTransferMoney.class);
                    i.putExtra(INTENT_ENROLLMENT,jsonObject.getString("mer_username"));
                    i.putExtra(RECIEVER_INTENT_COINS,jsonObject.getString("balance"));
                    i.putExtra(SENDER_INTENT_COINS,sender_money);
                    i.putExtra(INTENT_COINS,am);
                    i.putExtra(INTENT_USER_ID,sender_user_id);
                    i.putExtra(INTENT_FULLNAME,user_fullname);
                    i.putExtra(INTENT_LINEAR,"2");
                    startActivity(i);

                }catch(JSONException e) {
                    e.printStackTrace();
                    merchantProgressBar.setVisibility(View.GONE);
                    store_id.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.cross, 0);
                    Toast.makeText(getContext(),"Enter valid Store ID No.",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Unable to connect", Toast.LENGTH_SHORT).show();
                store_id.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                merchantProgressBar.setVisibility(View.GONE);
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("username", store_id.getText().toString());
                return hashMap;
            }

        };
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }


    private void getUserIDFirst(final String enrollment){

        mStringRequest = new StringRequest(Request.Method.POST, URL_USER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            sender_user_id = jsonObject.getString("id");
                            sender_money = jsonObject.getString("coins");
                            wallet_balance.setText(" ₹ "+sender_money);
                            getStores();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getActivity(),"Network Error",Toast.LENGTH_SHORT).show();
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
        mStringRequest.setTag(REQUEST_TAG);
        mRequestQueue.add(mStringRequest);
    }

    public String checkAmount(){
        if(amount.getText().toString().equals("")){
            enterAmount();
            return null;
        }else{
            return amount.getText().toString();
        }
    }

    public void enterAmount(){
        Toast.makeText(getActivity(),"First enter amount",
                Toast.LENGTH_SHORT).show();
        amount.requestFocus();
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInputFromWindow(
                amount.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

}
