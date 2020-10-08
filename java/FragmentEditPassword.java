package com.abhigam.www.foodspot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

/**
 * Created by sourabhzalke on 07/04/18.
 */

public class FragmentEditPassword extends Fragment{

    private CircularProgressButton mCircularProgressButton;
    private EditText mEditText;

    private RequestQueue mRequestQueue;
    private static final String URL = "http://13.233.234.79/forgot_control.php";
    private static final String URL_EDIT_PASSWORD = "http://13.233.234.79/edit_password.php";
    private static final String MOBILE_NO_TAG = "mobile_no";
    private String mobile_no;
    private StringRequest request;

    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME = "pref_username";
    private static final String PREF_PHONE_NUMBER = "pref_phone_number";
    private static final String PREF_EMAIL = "pref_email";
    private static final String PREF_BIRTHDAY = "pref_birthday";
    private static final String PREF_SEX = "pref_sex";
    private static final String PREF_BRANCH = "pref_branch";
    private static final String PREF_YEAR_ADM = "pref_year_adm";
    private static final String PREF_POSTS = "pref_posts";
    private static final String PREF_PRIVATE = "pref_private_ac";
    private static final String PREF_BIO = "pref_bio";
    private static final String PREF_VERIFIED = "pref_verified";
    private static final String PREF_VERIFY_REQUESTED = "pref_verify_request";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mRequestQueue = Volley.newRequestQueue(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_edit_password,container,false);

        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        if (toolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        mCircularProgressButton = v.findViewById(R.id.edit_button);
        mEditText = v.findViewById(R.id.edited_password);

        mobile_no = getActivity().getIntent().getStringExtra(MOBILE_NO_TAG);

        mCircularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidPassword(mEditText.getText().toString().trim())) {
                    Toast.makeText(getContext(), "Invalid password pattern", Toast.LENGTH_SHORT).show();
                } else {
                    edit_password(mobile_no);
                }
            }
        });


        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    private void edit_password(final String mobile_no){

        request = new StringRequest(Request.Method.POST, URL_EDIT_PASSWORD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(getActivity(),"Password changed successfully",Toast.LENGTH_SHORT).show();
                        login(mobile_no);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Network error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("mobile_no",mobile_no);
                hashMap.put("edited_password",mEditText.getText().toString());
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);

    }

    private void login(final String mobileNumber){
        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                        mCircularProgressButton.revertAnimation();
                    } else {

                        JSONArray jsonArray = jsonObject.getJSONArray("items");

                        JSONObject jsonRow = jsonArray.getJSONObject(0);
                        if(jsonRow.getString("block").equals("1")) {
                            Toast.makeText(getContext(),
                                    "You account is blocked. Kindly, contact us to reactivate your account", Toast.LENGTH_SHORT).show();
                            mCircularProgressButton.revertAnimation();
                        }else {
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(getActivity()).edit();
                            editor.putString(PREF_USER_NAME, jsonRow.getString("enrollment"));
                            editor.putString(PREF_FULLNAME, jsonRow.getString("first_name")
                                    + " " + jsonRow.getString("last_name"));
                            editor.putString(PREF_USER_ID, jsonRow.getString("id"));
                            editor.putString(PREF_PHONE_NUMBER, jsonRow.getString("mobile_no"));
                            editor.putString(PREF_EMAIL, jsonRow.getString("email"));
                            editor.putString(PREF_BIRTHDAY, jsonRow.getString("birthday"));
                            editor.putString(PREF_SEX, jsonRow.getString("sex"));
                            editor.putString(PREF_BRANCH, jsonRow.getString("branch"));
                            editor.putString(PREF_YEAR_ADM, jsonRow.getString("year_adm"));
                            editor.putString(PREF_POSTS, jsonRow.getString("posts"));
                            editor.putString(PREF_PRIVATE, jsonRow.getString("private_ac"));
                            editor.putString(PREF_BIO, jsonRow.getString("bio"));
                            editor.putString(PREF_VERIFIED, jsonRow.getString("verified"));
                            editor.putString(PREF_VERIFY_REQUESTED, jsonRow.getString("verify_requested"));
                            editor.apply();

                            Intent rui = new Intent(getActivity(), MainActivity.class);
                            rui.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(rui);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mCircularProgressButton.revertAnimation();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
                mCircularProgressButton.revertAnimation();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("mobile_number", mobileNumber);
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

}
