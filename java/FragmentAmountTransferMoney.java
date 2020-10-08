package com.abhigam.www.foodspot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

import static android.app.Activity.RESULT_OK;


/**
 * Created by sourabhzalke on 31/03/18.
 */

public class FragmentAmountTransferMoney extends Fragment {

    private String INTENT_ENROLLMENT = "com.abhigam.www.foodspot.enrollment";
    private String INTENT_FULLNAME = "com.abhigam.www.foodspot.fullname";
    private String INTENT_USER_ID = "com.abhigam.www.foodspot.user_id";
    private String RECIEVER_INTENT_COINS = "com.abhigam.www.foodspot.reciever_intent_coins";
    private String SENDER_INTENT_COINS = "com.abhigam.www.foodspot.sender_intent_coins";
    private String INTENT_COINS = "com.abhigam.www.foodspot.intent_coins";
    private String INTENT_LINEAR = "com.abhigam.www.foodspot.linear";

    private static  final String INTENT_PRODUCT_ID = "com.abhigam.www.foodspot.product_id";

    private String URL_USER_DETAILS = "http://13.233.234.79/user_id.php";
    private String URL_MONEY_TO_WALLET = "http://13.233.234.79/add_money_wallet.php";
    private String URL_PAY_TO_STORE = "http://13.233.234.79/pay_to_store.php";
    private String URL_ORDERED = "http://13.233.234.79/order_details.php";

    private StringRequest mStringRequest;
    private RequestQueue mRequestQueue;

    private TextView title,fullname,enrollment,total_text;
    private CircularProgressButton mCircularProgressButton;
    private String receiver_enrollment,receiver_fullname,receiver_user_id,receiver_no_coins,
            sender_user_id,sender_no_coins,sender_enrollment,sender_fullname,sender_mobile,
            sender_email,linear_show,product_id;
    //Profile Image
    private CircularNetworkImageView mNetworkImageView;

    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME= "pref_username";
    private static final String PREF_PHONE_NUMBER = "pref_phone_number";
    private static final String PREF_EMAIL = "pref_email";

    private static final String URL_TRANSFER_MONEY = "http://13.233.234.79/transfer_money.php";
    private TextView amount_textview;
    private static final String URL_TRANSACTION_NOTIFICATION = "http://13.233.234.79/transaction_notification.php";
    private final static String URL_SEND_NOTIFICATION = "http://13.233.234.79/send_notification.php";
    private CheckBox mCheckBox;
    private String amount_to_transfer,dummy_amount,sender_money;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    private LinearLayout mLinearLayout;
    private String URL_PROFILE;
    private LinearLayout back;
    private TextView wallet_used_money;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        SharedPreferences prefs = SaveSharedPreference.getSharedPreferences(getActivity()
                .getApplicationContext());
        sender_enrollment = prefs.getString(PREF_USER_NAME,"");
        sender_fullname = prefs.getString(PREF_FULLNAME,"");
        sender_user_id = prefs.getString(PREF_USER_ID,"");
        sender_mobile = prefs.getString(PREF_PHONE_NUMBER,"");
        sender_email = prefs.getString(PREF_EMAIL,"");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_amount_transfer_money,container,false);

        title = v.findViewById(R.id.title);
        fullname = v.findViewById(R.id.fullname);
        enrollment = v.findViewById(R.id.enrollment);
        mCircularProgressButton = v.findViewById(R.id.trasfer_coins);
        mNetworkImageView = v.findViewById(R.id.circular_profile);
        mCheckBox = v.findViewById(R.id.checkbox);
        mLinearLayout = v.findViewById(R.id.wallet_linear);
        amount_textview = v.findViewById(R.id.amount_textview);
        total_text = v.findViewById(R.id.total_text);
        back = v.findViewById(R.id.back);
        wallet_used_money = v.findViewById(R.id.wallet_use_money);

        Window window = getActivity().getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.amount_transfer));
        }


        receiver_enrollment = getActivity().getIntent().getStringExtra(INTENT_ENROLLMENT);
        receiver_fullname = getActivity().getIntent().getStringExtra(INTENT_FULLNAME);
        receiver_user_id = getActivity().getIntent().getStringExtra(INTENT_USER_ID);
        receiver_no_coins = getActivity().getIntent().getStringExtra(RECIEVER_INTENT_COINS);
        amount_to_transfer = getActivity().getIntent().getStringExtra(INTENT_COINS);
        dummy_amount = amount_to_transfer;
        sender_money = getActivity().getIntent().getStringExtra(SENDER_INTENT_COINS);
        linear_show = getActivity().getIntent().getStringExtra(INTENT_LINEAR);
        product_id = getActivity().getIntent().getStringExtra(INTENT_PRODUCT_ID);

        if(Integer.parseInt(dummy_amount)>=Integer.parseInt(sender_money)){
            wallet_used_money.setText("₹"+sender_money);
        }else{
            wallet_used_money.setText("₹"+dummy_amount);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        total_text.setText("Total: ₹"+dummy_amount);

        if(linear_show.equals("1")){
            //for adding to wallet
            mLinearLayout.setVisibility(View.GONE);
            title.setText("To "+receiver_fullname);
            fullname.setText(receiver_fullname);
            amount_to_transfer = dummy_amount;
            amount_textview.setText("Add amount : ₹"+amount_to_transfer);
            URL_PROFILE = "http://13.233.234.79/uploads/profile_pic/" +receiver_enrollment+ "_profile.jpg";

        }else if(linear_show.equals("2")){
            //for paying at store
            title.setText("To "+receiver_enrollment);
            fullname.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.VISIBLE);
            URL_PROFILE = "http://13.233.234.79/uploads/merchant_pic/"+receiver_enrollment+".jpg";
            int x = Integer.parseInt(sender_money);
            int y = Integer.parseInt(amount_to_transfer);

            if (x >= y) {
                amount_to_transfer = dummy_amount;
                amount_textview.setText("Pay : ₹"+amount_to_transfer);
            }else{
                amount_to_transfer = Integer.toString(y - x);
                amount_textview.setText("Pay via other options: ₹" + amount_to_transfer);
            }
        }else if(linear_show.equals("3")) {
            //buy product
            title.setText("To " + receiver_enrollment);
            fullname.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.VISIBLE);
            URL_PROFILE = "http://13.233.234.79/uploads/merchant_pic/" + receiver_enrollment + ".jpg";
            int x = Integer.parseInt(sender_money);
            int y = Integer.parseInt(amount_to_transfer);

            if (x >= y) {
                amount_to_transfer = dummy_amount;
                amount_textview.setText("Pay : ₹" + amount_to_transfer);
            } else {
                amount_to_transfer = Integer.toString(y - x);
                amount_textview.setText("Pay via other options: ₹" + amount_to_transfer);
            }
        }
        else{
            //For transfering
            title.setText("To "+receiver_fullname);
            fullname.setText(receiver_fullname);
            mLinearLayout.setVisibility(View.VISIBLE);
            URL_PROFILE = "http://13.233.234.79/uploads/profile_pic/" +receiver_enrollment+ "_profile.jpg";
            int x = Integer.parseInt(sender_money);
            int y = Integer.parseInt(amount_to_transfer);

            if (x>=y){
                amount_to_transfer = dummy_amount;
                amount_textview.setText("Pay : ₹"+amount_to_transfer);
            }else{
                amount_to_transfer = Integer.toString(y - x);
                amount_textview.setText("Pay via other options: ₹" +amount_to_transfer);
            }
        }

        if(sender_money.equals("0")) {
           mLinearLayout.setVisibility(View.GONE);
        }else{
            mCheckBox.setChecked(true);
        }

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    int x = Integer.parseInt(sender_money);
                    int y = Integer.parseInt(amount_to_transfer);

                    if(x>=y){
                        amount_to_transfer = dummy_amount;
                        amount_textview.setText("Pay : ₹"+amount_to_transfer);
                    }else{
                        amount_to_transfer = Integer.toString(y-x);
                        amount_textview.setText("Pay via other options: ₹"+amount_to_transfer);
                    }
                }else{
                    amount_to_transfer = dummy_amount;
                    amount_textview.setText("Pay via other options : ₹"+amount_to_transfer);
                }
            }
        });



        Glide
                .with(getActivity())
                .load(URL_PROFILE)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                .skipMemoryCache(true)
                .centerCrop()
                .into(new SimpleTarget<Bitmap>(300, 300){ //width and height
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        mNetworkImageView.setImageBitmap(resource);
                    }
                });

        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        enrollment.setText(receiver_enrollment);

        mCircularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //condition for directly paying from wallet
                if(linear_show.equals("0") && Integer.parseInt(dummy_amount)<=
                        Integer.parseInt(sender_money) && mCheckBox.isChecked()){
                    getUserID(sender_enrollment, amount_to_transfer,
                            sender_user_id+System.currentTimeMillis());
                }
                else if(linear_show.equals("2") && Integer.parseInt(dummy_amount)<=
                       Integer.parseInt(sender_money) && mCheckBox.isChecked()
                        ){

                    pay_to_store(receiver_enrollment,dummy_amount,
                            Integer.toString(Integer.parseInt(sender_money)
                                    -Integer.parseInt(dummy_amount)),sender_enrollment,
                            sender_user_id+System.currentTimeMillis());
                }else if(linear_show.equals("3") && Integer.parseInt(dummy_amount)<=
                        Integer.parseInt(sender_money) && mCheckBox.isChecked()){

                    buy_product(receiver_enrollment,dummy_amount,
                            Integer.toString(Integer.parseInt(sender_money)
                                    -Integer.parseInt(dummy_amount)),sender_enrollment,
                            sender_user_id+System.currentTimeMillis(),
                            "",sender_user_id+System.currentTimeMillis(),
                            dummy_amount,"","","",sender_fullname,
                            sender_email,sender_mobile,"","","",
                            "",""
                            );
                }else{
                    launchPayUMoneyFlow(amount_to_transfer,sender_fullname,sender_email,
                            sender_user_id,sender_mobile,receiver_user_id);
                }
            }
        });
        return v;
    }

    private void getUserID(final String enrollment,final String transfer_amount,final String txnId){

        mStringRequest = new StringRequest(Request.Method.POST, URL_USER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            sender_user_id = jsonObject.getString("id");
                            sender_no_coins = jsonObject.getString("coins");

                                String dup_recevier_money,dup_sender_money;

                                //case for adding money at receiver's end
                                dup_recevier_money = Integer.toString(Integer.parseInt(dummy_amount)
                                        + Integer
                                        .parseInt(receiver_no_coins));

                                //case for deducting amount from sender
                                if(mCheckBox.isChecked()){
                                    dup_sender_money = Integer.toString(Integer.parseInt(sender_no_coins) -
                                            Integer.parseInt(transfer_amount));
                                    if(Integer.parseInt(dup_sender_money)<=0){
                                        dup_sender_money = "0";
                                    }
                                }else{
                                    dup_sender_money = sender_no_coins;
                                }

                                //starting transaction
                                 transaction(sender_user_id, receiver_user_id,
                                         sender_fullname,receiver_fullname,
                                       dup_sender_money, dup_recevier_money,
                                         transfer_amount,
                                         sender_enrollment,receiver_enrollment,txnId);

                        }catch (JSONException e) {
                            e.printStackTrace();
                            mCircularProgressButton.revertAnimation();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCircularProgressButton.revertAnimation();
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
        mRequestQueue.add(mStringRequest);
    }

    private void transaction(final String sender_user_id,final String receiver_user_id,
                             final String sender_fullname,final String receiver_fullname,
                             final String sender_no_coins,final String receiver_no_coins,
                             final String amount,
                             final String sender_enrollment,final String receiver_enrollment,
                             final String txnId) {

        mStringRequest = new StringRequest(Request.Method.POST, URL_TRANSFER_MONEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.names().get(0).toString().equals("success")) {
                                send_notification(amount);
                                send_transaction_notification(amount);
                                Intent i = new Intent(getActivity(), TransactionDone.class);
                                i.putExtra(INTENT_FULLNAME, receiver_fullname);
                                i.putExtra(INTENT_COINS, dummy_amount);
                                i.putExtra(INTENT_ENROLLMENT, receiver_enrollment);
                                i.putExtra(INTENT_LINEAR,"1");

                                getActivity().startActivity(i);
                            } else {
                                Toast.makeText(getActivity(), "Try again later", Toast.LENGTH_SHORT).show();
                            }
                            mCircularProgressButton.revertAnimation();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mCircularProgressButton.revertAnimation();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCircularProgressButton.revertAnimation();
                Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("sender_user_id", sender_user_id);
                hashMap.put("receiver_user_id", receiver_user_id);
                hashMap.put("sender_enrollment", sender_enrollment);
                hashMap.put("receiver_enrollment", receiver_enrollment);
                hashMap.put("sender_no_coins", sender_no_coins);
                hashMap.put("receiver_no_coins", receiver_no_coins);
                hashMap.put("amount", amount);
                hashMap.put("sender_fullname", sender_fullname);
                hashMap.put("receiver_fullname", receiver_fullname);
                hashMap.put("time_ago", Long.toString(Calendar.getInstance().getTimeInMillis()));
                hashMap.put("txnId",txnId);
                return hashMap;
            }
        };
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void send_transaction_notification(final String amount){

        mStringRequest  = new StringRequest(Request.Method.POST, URL_TRANSACTION_NOTIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                    Log.e("x",response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",receiver_user_id);
                hashMap.put("sender_id",sender_user_id);
                hashMap.put("thing",amount);
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void send_notification(final String amount){

        mStringRequest  = new StringRequest(Request.Method.POST, URL_SEND_NOTIFICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",receiver_user_id);
                hashMap.put("title",sender_enrollment);
                hashMap.put("message", sender_fullname + " sent you ₹" + amount +
                            ".");
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
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchPayUMoneyFlow(final String amount_to_transfer,final String fullname,
                                     final String email_string,final String sender_user_id,
                                     final String mobile_no,final String receiver_user_id) {


        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
        payUmoneyConfig.setDoneButtonText("Crony");

        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle("Crony");

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        double amount = 0;
        try{
            amount = Double.parseDouble(amount_to_transfer);
        }catch(Exception e){
            e.printStackTrace();
        }
        String txnId = sender_user_id+System.currentTimeMillis();
        String phone = mobile_no.substring(3,mobile_no.length());
        String productName = "Transfer Money to "+receiver_user_id;
        String firstName = fullname.replace(" ","").toLowerCase();
        String email = email_string;
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";



        builder.setAmount(amount)
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(false)
                .setKey("IJUtYfOp")
                .setMerchantId("6151917");

        try {
            mPaymentParams = builder.build();

            /*
            * Hash should always be generated from your server side.
            * */
          generateHashFromServer(mPaymentParams);

       /*//**
     * Do not use below code when going live
     * Below code is provided to generate hash from sdk.
     * It is recommended to generate hash from server side only.
     * */

           // mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

          //  PayUmoneyFlowManager
          //          .startPayUMoneyFlow(mPaymentParams,
           //                 MainActivity.this, R.style.AppTheme, false);


        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    /**
     * This method generates hash from server.
     *
     * @param paymentParam payments params used for hash generation
     */

    public void generateHashFromServer(PayUmoneySdkInitializer.PaymentParam paymentParam) {
        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.

        HashMap<String, String> params = paymentParam.getParams();

        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayUmoneyConstants.KEY, params.get(PayUmoneyConstants.KEY)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.AMOUNT, params.get(PayUmoneyConstants.AMOUNT)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.TXNID, params.get(PayUmoneyConstants.TXNID)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.EMAIL, params.get(PayUmoneyConstants.EMAIL)));
        postParamsBuffer.append(concatParams("productInfo", params.get(PayUmoneyConstants.PRODUCT_INFO)));
        postParamsBuffer.append(concatParams("firstName", params.get(PayUmoneyConstants.FIRSTNAME)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF1, params.get(PayUmoneyConstants.UDF1)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF2, params.get(PayUmoneyConstants.UDF2)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF3, params.get(PayUmoneyConstants.UDF3)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF4, params.get(PayUmoneyConstants.UDF4)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF5, params.get(PayUmoneyConstants.UDF5)));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }


    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    /**
     * This AsyncTask generates hash from server.
     */

    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... postParams) {

            String merchantHash = "";
            try {
                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                java.net.URL url = new URL("http://13.233.234.79/payu_hash.php");

                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {

    /**
     * This hash is mandatory and needs to be generated from merchant's server side
     *
     */

                        case "payment_hash":
                            merchantHash = response.getString(key);
                            break;
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return merchantHash;
        }

        @Override
        protected void onPostExecute(String merchantHash) {
            super.onPostExecute(merchantHash);

            progressDialog.dismiss();

            if (merchantHash.isEmpty() || merchantHash.equals("")) {
                Toast.makeText(getActivity(), "Could not generate hash", Toast.LENGTH_SHORT).show();
            } else {
                mPaymentParams.setMerchantHash(merchantHash);

                  PayUmoneyFlowManager
                         .startPayUMoneyFlow(mPaymentParams,
                                getActivity(), R.style.AppTheme, false);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if(transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)){
                //Success Transaction
                    if(linear_show.equals("0")) {
                        // Response from Payumoney
                        String payuResponse = transactionResponse.getPayuResponse();
                        String paymentId;
                        try {
                            JSONObject jsonObject = new JSONObject(payuResponse);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                            paymentId = jsonObject1.getString("paymentId");
                            getUserID(sender_enrollment, dummy_amount,paymentId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(linear_show.equals("1")) {
                        // Response from Payumoney
                        String payuResponse = transactionResponse.getPayuResponse();
                        String paymentId;
                        try {
                            JSONObject jsonObject = new JSONObject(payuResponse);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                            paymentId = jsonObject1.getString("paymentId");
                            add_to_wallet(sender_enrollment, dummy_amount,paymentId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(linear_show.equals("2")) {
                        String dup_sender_money;
                        //case for deducting amount from sender
                        if (mCheckBox.isChecked()) {
                            dup_sender_money = Integer.toString(Integer.parseInt(sender_money) -
                                    Integer.parseInt(dummy_amount));
                            if (Integer.parseInt(dup_sender_money) <= 0) {
                                dup_sender_money = "0";
                            }
                        } else {
                            dup_sender_money = sender_money;
                        }
                        // Response from Payumoney
                        String payuResponse = transactionResponse.getPayuResponse();
                        String paymentId;
                        try {
                            JSONObject jsonObject = new JSONObject(payuResponse);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                            paymentId = jsonObject1.getString("paymentId");
                            pay_to_store(receiver_enrollment, dummy_amount,
                                    dup_sender_money, sender_enrollment,paymentId);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else if(linear_show.equals("3")){
                        String dup_sender_money;
                        //case for deducting amount from sender
                        if(mCheckBox.isChecked()){
                            dup_sender_money = Integer.toString(Integer.parseInt(sender_money) -
                                    Integer.parseInt(dummy_amount));
                            if(Integer.parseInt(dup_sender_money)<=0){
                                dup_sender_money = "0";
                            }
                        }else{
                            dup_sender_money = sender_money;
                        }

                        // Response from Payumoney
                        String payuResponse = transactionResponse.getPayuResponse();
                        String paymentId,mode,txnId,addedOn,createdOn,productInfo,
                                firstname,email,phone,bank_ref_num,
                                bankCode,name_on_card,card_num,amount,pg_type;
                        try {
                            JSONObject jsonObject = new JSONObject(payuResponse);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                            paymentId = jsonObject1.getString("paymentId");
                            mode = jsonObject1.getString("mode");
                            txnId = jsonObject1.getString("txnid");
                            amount = jsonObject1.getString("amount");
                            addedOn = jsonObject1.getString("addedon");
                            createdOn = jsonObject1.getString("createdOn");
                            productInfo = jsonObject1.getString("productinfo");
                            firstname = jsonObject1.getString("firstname");
                            email = jsonObject1.getString("email");
                            phone = jsonObject1.getString("phone");
                            bank_ref_num = jsonObject1.getString("bank_ref_num");
                            bankCode = jsonObject1.getString("bankcode");
                            name_on_card = jsonObject1.getString("name_on_card");
                            card_num = jsonObject1.getString("cardnum");
                            pg_type = jsonObject1.getString("pg_TYPE");
                            buy_product(receiver_enrollment,dummy_amount,
                                    dup_sender_money,sender_enrollment,
                                    paymentId,mode,txnId,amount,addedOn,createdOn,
                                    productInfo,firstname,email,phone,bank_ref_num,
                                    bankCode,name_on_card,card_num,pg_type);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } else{
                  //Failure Transaction
                    Toast.makeText(getActivity(),"failure",Toast.LENGTH_SHORT).show();
                }
                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();
            }
        }
    }

    public void add_to_wallet(final String sender_enrollment,final String dummy_amount,final String txnId){

        mStringRequest = new StringRequest(Request.Method.POST, URL_MONEY_TO_WALLET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.names().get(0).equals("success")){
                                Intent i = new Intent(getActivity(), TransactionDone.class);
                                i.putExtra(INTENT_FULLNAME, sender_fullname);
                                i.putExtra(INTENT_COINS, dummy_amount);
                                i.putExtra(INTENT_ENROLLMENT, sender_enrollment);
                                i.putExtra(INTENT_LINEAR,"1");
                                getActivity().startActivity(i);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("enrollment",sender_enrollment);
                hashMap.put("amount_to_add",dummy_amount);
                hashMap.put("sender_id",sender_user_id);
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                hashMap.put("txnId",txnId);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    public void pay_to_store(final String username,final String dummy_amount,final String sender_money,
                             final String sender_enrollment,final String txnId){

        mStringRequest = new StringRequest(Request.Method.POST, URL_PAY_TO_STORE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.names().get(0).equals("success")){
                                Intent i = new Intent(getActivity(), TransactionDone.class);
                                i.putExtra(INTENT_FULLNAME,"Store");
                                i.putExtra(INTENT_COINS, dummy_amount);
                                i.putExtra(INTENT_ENROLLMENT,"cronymerchant");
                                i.putExtra(INTENT_LINEAR,linear_show);
                                getActivity().startActivity(i);
                            }

                        }catch(JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("username",username);
                hashMap.put("amount_to_add",dummy_amount);
                hashMap.put("sender_enroll",sender_enrollment);
                hashMap.put("sender_money",sender_money);
                hashMap.put("sender_id",sender_user_id);
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                hashMap.put("txnId",txnId);
                hashMap.put("store_name",receiver_enrollment);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    public void buy_product(final String username,final String dummy_amount,final String sender_money,
                             final String sender_enrollment,final String paymentId,
                            final String mode,final String txnId,final String amount,
                            final String addedOn,final String createdOn,final String productInfo,
                            final String firstname,final String email,final String phone,
                            final String  bank_ref_num,final String bankCode,
                            final String name_on_card,final String card_num,
                            final String pg_type){

        mStringRequest = new StringRequest(Request.Method.POST, URL_PAY_TO_STORE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.names().get(0).equals("success")){
                                order_details(product_id,sender_enrollment,
                                        paymentId,mode,txnId,amount,addedOn,createdOn,productInfo,
                                        firstname,email,phone,bank_ref_num,bankCode,
                                        name_on_card,card_num,pg_type);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("username",username);
                hashMap.put("amount_to_add",dummy_amount);
                hashMap.put("sender_enroll",sender_enrollment);
                hashMap.put("sender_money",sender_money);
                hashMap.put("sender_id",sender_user_id);
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                hashMap.put("txnId",paymentId);
                hashMap.put("store_name",receiver_enrollment);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    public void order_details(final String product_id,final String username,
                              final String paymentId,
                              final String mode,final String txnId,final String amount,
                              final String addedOn,final String createdOn,final String productInfo,
                              final String firstname,final String email,final String phone,
                              final String  bank_ref_num,final String bankCode,
                              final String name_on_card,final String card_num,
                              final String pg_type){

        Random rnd = new Random();
        final int token = 100000 + rnd.nextInt(900000);

        mStringRequest = new StringRequest(Request.Method.POST, URL_ORDERED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.names().get(0).equals("success")){

                                Intent i = new Intent(getActivity(), TransactionDone.class);
                                i.putExtra(INTENT_FULLNAME,"Store");
                                i.putExtra(INTENT_COINS, dummy_amount);
                                i.putExtra(INTENT_ENROLLMENT,"cronymerchant");
                                i.putExtra(INTENT_LINEAR,linear_show);
                                getActivity().startActivity(i);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("item_id",product_id);
                hashMap.put("user_id",sender_user_id);
                hashMap.put("time_ago",Long.toString(Calendar.getInstance()
                .getTimeInMillis()));
                hashMap.put("transaction_id",txnId);
                hashMap.put("status","0");
                hashMap.put("token",Integer.toString(token));
                hashMap.put("paymentId",paymentId);
                hashMap.put("mode",mode);
                hashMap.put("amount",amount);
                hashMap.put("addedOn",addedOn);
                hashMap.put("createdOn",createdOn);
                hashMap.put("productInfo",productInfo);
                hashMap.put("firstname",firstname);
                hashMap.put("email",email);
                hashMap.put("phone",phone);
                hashMap.put("bank_ref_num",bank_ref_num);
                hashMap.put("bankCode",bankCode);
                hashMap.put("name_on_card",name_on_card);
                hashMap.put("card_num",card_num);
                hashMap.put("pg_TYPE",pg_type);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }




    /*

    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        stringBuilder.append("");

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

*/
}
