package com.abhigam.www.foodspot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

/**
 * Created by sourabhzalke on 18/05/17.
 */

public class LoginFragment extends Fragment {

    private EditText email,password;
    private CircularProgressButton login;
    private RequestQueue mRequestQueue;
    private static final String URL="http://13.233.234.79/user_control.php";
    private StringRequest request;

    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME= "pref_username";
    private static final String PREF_PHONE_NUMBER = "pref_phone_number";
    private static final String PREF_EMAIL = "pref_email";
    private static final String PREF_BIRTHDAY = "pref_birthday";
    private static final String PREF_SEX= "pref_sex";
    private static final String PREF_BRANCH = "pref_branch";
    private static final String PREF_YEAR_ADM = "pref_year_adm";
    private static final String PREF_POSTS = "pref_posts";
    private static final String PREF_PRIVATE = "pref_private_ac";
    private static final String PREF_BIO = "pref_bio";
    private static final String PREF_VERIFIED = "pref_verified";
    private static final String PREF_VERIFY_REQUESTED = "pref_verify_request";

    private LinearLayout terms,forgot_password;

    private static final String WEB_URL_INTENT = "web_url_intent_crony";
    private static final String crony_first_url = "https://www.crony.co.in/term-and-conditions";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if ((getActivity().getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            getActivity().finish();
            return;
        }

        if(SaveSharedPreference.getUserName(getActivity()).length() != 0)
        {
            Intent main = new Intent(getContext(),MainActivity.class);
            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(main);
        }

        getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    public void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_login,container,false);


        email= v.findViewById(R.id.username);
        password= v.findViewById(R.id.password);
        login= v.findViewById(R.id.login);
        terms = v.findViewById(R.id.terms);

        //password hint font problem
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());

        mRequestQueue = Volley.newRequestQueue(getActivity());

        Toolbar toolbar = v.findViewById(R.id.toolbar_top);

        if (toolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        forgot_password = v.findViewById(R.id.forgot_password);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),ForgotPassword.class);
                getActivity().startActivity(i);
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),WebActivity.class);
                i.putExtra(WEB_URL_INTENT,crony_first_url);
                getActivity().startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View x) {

                if (email.getText().toString().equals("")) {
                   Toast.makeText(getActivity(),"Please enter Enrollment No.",Toast.LENGTH_SHORT)
                           .show();
                } else if (password.getText().toString().equals("")) {
                    Toast.makeText(getActivity(),"Please enter Password",Toast.LENGTH_SHORT)
                            .show();
                }else{

                    hideSoftKeyboard(getActivity(),v);

                    login.startAnimation();

                    request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.names().get(0).equals("error")) {
                                    Toast.makeText(getContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                                    login.revertAnimation();
                                }else{

                                    JSONArray jsonArray = jsonObject.getJSONArray("items");

                                    JSONObject jsonRow = jsonArray.getJSONObject(0);
                                    if(jsonRow.getString("block").equals("1")) {
                                        Toast.makeText(getContext(),
                                                "You account is blocked. Kindly, contact us to reactivate your account", Toast.LENGTH_SHORT).show();
                                        login.revertAnimation();
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
                                        login.revertAnimation();
                                    }

                                }
                            }catch(JSONException e){
                                e.printStackTrace();
                                Toast.makeText(getContext(),"Error", Toast.LENGTH_SHORT).show();
                                login.revertAnimation();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
                            login.revertAnimation();
                        }
                    }
                    ){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("email", email.getText().toString());
                            hashMap.put("password", password.getText().toString());
                            return hashMap;
                        }
                    };
                    request.setRetryPolicy(new DefaultRetryPolicy(5000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    mRequestQueue.add(request);
                }
            }
        });

        return v;
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

    @Override
    public void onPause() {
        super.onPause();
        getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }


}
