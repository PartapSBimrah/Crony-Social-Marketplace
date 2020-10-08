package com.abhigam.www.foodspot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;


/**
 * Created by sourabhzalke on 06/04/18.
 */

public class FragmentForgotPassword extends Fragment {

    private String mobile_number;
    private TextView phoneNumberTextView;
    private CircularProgressButton continue_forgot;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private static final String MOBILE_NO_TAG = "mobile_no";
    private static final String VERIFICATION_TAG = "verificationid";
    private static final String RESEND_TOKEN = "resend_token";
    private static final String INTENT_FORGOT = "forgot";
    private String forgot_flag = "8";
    private ProgressBar mobileProgressbar;
    private int mobile_no_check_int = 0;
    private StringRequest request;
    private RequestQueue mRequestQueue;
    private static final String URL_MOBILE = "http://13.233.234.79/mobile_no_control_forgot.php";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mRequestQueue = Volley.newRequestQueue(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_forgot_password, container, false);

        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        phoneNumberTextView = v.findViewById(R.id.mobileNumber);
        continue_forgot = v.findViewById(R.id.continue_forgot);
        mobileProgressbar = v.findViewById(R.id.progress_mobile);

        mAuth = FirebaseAuth.getInstance();

        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        continue_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneNumberTextView.getText().toString().equals("") || phoneNumberTextView.getText().toString()
                        .length() != 10 || mobile_no_check_int == 0){
                    Snackbar.make(v,"Please enter a valid 10-digit mobile number",Snackbar.LENGTH_LONG).show();
                }else{
                    continue_forgot.startAnimation();
                    mobile_number = "+91" + phoneNumberTextView.getText().toString();
                    if (mCallbacks != null)
                        verifyPhone(mobile_number, mCallbacks);
                }

            }
        });

        phoneNumberTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(phoneNumberTextView.getText().toString().length()==10){
                    phoneNumberTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    mobile_number = "+91"+phoneNumberTextView.getText().toString();
                    mobileNoCheck(mobile_number);
                    mobileProgressbar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(phoneNumberTextView.getText().toString().length()!=10){
                    phoneNumberTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                }
            }
        });


        return v;
    }

    private void mobileNoCheck(final String mobile_number) {
        request = new StringRequest(Request.Method.POST, URL_MOBILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")){
                        mobileProgressbar.setVisibility(View.GONE);
                        mobile_no_check_int = 1;
                        phoneNumberTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.right_tick, 0);
                    }else{
                        mobileProgressbar.setVisibility(View.GONE);
                        mobile_no_check_int = 0;
                        phoneNumberTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.cross, 0);
                        Toast.makeText(getActivity(),"No account associated with this mobile number.",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mobile_no_check_int = 0;
                    mobileProgressbar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Unable to connect", Toast.LENGTH_SHORT).show();
                mobile_no_check_int = 0;
                phoneNumberTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                mobileProgressbar.setVisibility(View.GONE);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("mobile_no", mobile_number);
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    Toast.makeText(getActivity(), "Number verified automatically", Toast.LENGTH_SHORT).show();
                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.

                    continue_forgot.revertAnimation();
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        // ...
                    } else if (e instanceof FirebaseTooManyRequestsException){
                        // The SMS quota for the project has been exceeded
                        Toast.makeText(getActivity(), "Please try again later", Toast.LENGTH_SHORT).show();
                    }
                    // Show a message and update the UI
                    // ...
                }

                @Override
                public void onCodeSent(String verificationId,
                                       PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.

                    // Save verification ID and resending token so we can use them later
                    mVerificationId = verificationId;
                    mResendToken = token;
                    continue_forgot.revertAnimation();
                    Intent i = new Intent(getActivity(), verificationCode.class);
                    i.putExtra(MOBILE_NO_TAG, mobile_number);
                    i.putExtra(VERIFICATION_TAG, mVerificationId);
                    i.putExtra(RESEND_TOKEN, mResendToken);
                    i.putExtra(INTENT_FORGOT, forgot_flag);
                    startActivity(i);

                    // ...
                }
            };

    public void verifyPhone(String phoneNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks) {

        FirebaseApp.initializeApp(getActivity());

        com.google.firebase.auth.PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                30,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallback
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if (task.isSuccessful()) {
                            //Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = task.getResult().getUser();
                            continue_forgot.revertAnimation();
                            Intent i = new Intent(getActivity(),EditPassword.class);
                            i.putExtra(MOBILE_NO_TAG,mobile_number);
                            startActivity(i);
                            // ...
                        }else{
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                // The verification code entered was invalid
                                Toast.makeText(getActivity(), "Wrong verification code",
                                        Toast.LENGTH_SHORT).show();
                                continue_forgot.revertAnimation();
                            }
                        }
                    }
                });
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
}


