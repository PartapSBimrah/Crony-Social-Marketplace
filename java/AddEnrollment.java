package com.abhigam.www.foodspot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddEnrollment extends AppCompatActivity {

    private LinearLayout mLinearLayout;
    private EditText mEditText,amount;
    private View parentView;
    private StringRequest mStringRequest;
    private RequestQueue mRequestQueue;
    private String URL_USER_DETAILS = "http://13.233.234.79/user_id.php";
    private String intent_enrollment,intent_fullname,intent_user_id,username,intent_no_coins;
    private String INTENT_ENROLLMENT = "com.abhigam.www.foodspot.enrollment";
    private String INTENT_FULLNAME = "com.abhigam.www.foodspot.fullname";
    private String INTENT_USER_ID = "com.abhigam.www.foodspot.user_id";
    private String INTENT_COINS = "com.abhigam.www.foodspot.intent_coins";
    private LinearLayout back_arrow;

    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME= "pref_username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        setContentView(R.layout.activity_add_enrollment);
        parentView = findViewById(android.R.id.content);
        SharedPreferences prefs = SaveSharedPreference.getSharedPreferences(getApplicationContext()
                .getApplicationContext());
        username = prefs.getString(PREF_USER_NAME,"");

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mEditText = findViewById(R.id.enrollment_receiver);

        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
        }

        back_arrow = findViewById(R.id.back);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        amount = findViewById(R.id.amount);

        mLinearLayout = findViewById(R.id.linear);


    }

    private void getUserID(final String enrollment){

        mStringRequest = new StringRequest(Request.Method.POST, URL_USER_DETAILS,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.names().get(0).equals("error")){
                        Toast.makeText(getApplicationContext(),
                                "Enter valid enrollment no.",Toast.LENGTH_SHORT).show();
                    }else {
                        intent_user_id = jsonObject.getString("id");
                        intent_fullname = jsonObject.getString("first_name") + " " + jsonObject.
                                getString("last_name");
                        intent_enrollment = jsonObject.getString("enrollment");
                        intent_no_coins = jsonObject.getString("coins");
                        if(!username.equals(intent_enrollment)) {
                            Intent i = new Intent(getApplicationContext(),AmountTransferMoney.class);
                            i.putExtra(INTENT_ENROLLMENT, intent_enrollment);
                            i.putExtra(INTENT_FULLNAME, intent_fullname);
                            i.putExtra(INTENT_USER_ID, intent_user_id);
                            i.putExtra(INTENT_COINS,intent_no_coins);
                            startActivity(i);
                        }else{
                            Toast.makeText(getApplicationContext(),
                                    "You can't send coins to yourself",Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Enter valid enrollment no.",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
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
