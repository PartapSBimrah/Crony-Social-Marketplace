package com.abhigam.www.foodspot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by sourabhzalke on 19/05/17.
 */

public class FirstScreenFragment extends Fragment {

    private CircularProgressButton signup;
    private EditText enrollmentNumber,mobileNumber;
    private LinearLayout login;
    private String mobile_number;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar,mobileProgressbar;
    private static final String ENROLL_TAG = "enroll_no";
    private static final String MOBILE_NO_TAG = "mobile_no";
    private static final String VERIFICATION_TAG = "verificationid";
    private static final String RESEND_TOKEN = "resend_token";
    private RequestQueue mRequestQueue;
    private StringRequest request;
    private static final String URL_USERNAME = "http://13.233.234.79/username_control.php";
    private static final String URL_MOBILE = "http://13.233.234.79/mobile_no_control.php";
    private int username_check_int = 0;
    private int mobile_no_check_int = 0;
    private static final int REQUEST_CODE = 99;
    private static final String INTENT_FORGOT = "forgot";

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mRequestQueue = Volley.newRequestQueue(getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_first_screen,container,false);

        signup = v.findViewById(R.id.signup);
        login = v.findViewById(R.id.login);
        mobileNumber = v.findViewById(R.id.mobile);
        enrollmentNumber = v.findViewById(R.id.enroll);
        FirebaseApp.initializeApp(getActivity());
        mAuth = FirebaseAuth.getInstance();
        mProgressBar = v.findViewById(R.id.progressBar);
        mobileProgressbar = v.findViewById(R.id.progress_mobile);


        enrollmentNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(enrollmentNumber.getText().toString().length()==12 &&
                            enrollmentNumber.getText().toString().toLowerCase().startsWith("0901")){
                        enrollmentNumber.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                        mProgressBar.setVisibility(View.VISIBLE);
                        usernameCheck();
                        mProgressBar.setVisibility(View.VISIBLE);
                    }else if(enrollmentNumber.getText().toString().length()==12){
                        username_check_int = 0;
                        enrollmentNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                R.mipmap.cross, 0);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(enrollmentNumber.getText().toString().length()!=12){
                    enrollmentNumber.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                }
            }
        });

        enrollmentNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(!hasFocus){
                    if(enrollmentNumber.getText().toString().length()!=12){
                    enrollmentNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.mipmap.cross, 0);
                    username_check_int = 0;
                    }
                }
            }
        });

        mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mobileNumber.getText().toString().length()==10){
                    mobileNumber.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    mobileNoCheck();
                    mobileProgressbar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mobileNumber.getText().toString().length()!=10){
                    mobileNumber.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login.setClickable(false);

                if( enrollmentNumber.getText().toString().equals("")
                        || enrollmentNumber.getText().toString().length()!=12 ||username_check_int == 0){
                    Snackbar.make(v,"Please enter a valid enrollment number",Snackbar.LENGTH_LONG).show();
                }
                else if(mobileNumber.getText().toString().equals("") || mobileNumber.getText().toString()
                        .length() != 10 || mobile_no_check_int == 0){
                    Snackbar.make(v,"Please enter a valid 10-digit mobile number",Snackbar.LENGTH_LONG).show();
                }else{

                    signup.startAnimation();
                    mobile_number = "+91"+mobileNumber.getText().toString();
                    if(mCallbacks!= null)
                    verifyPhone(mobile_number,mCallbacks);
                }

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getActivity(),LoginActivity.class);
                startActivity(login);
            }
        });

        return v;
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    Toast.makeText(getActivity(),"Verification successful",Toast.LENGTH_SHORT).show();
                    signup.revertAnimation();
                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.

                    signup.revertAnimation();
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                        // ...
                    } else if (e instanceof FirebaseTooManyRequestsException){
                        // The SMS quota for the project has been exceeded

                        Toast.makeText(getActivity(),"Please try again later",Toast.LENGTH_SHORT).show();
                        // ...
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
                    signup.revertAnimation();
                    login.setClickable(true);
                    Intent i = new Intent(getActivity(),verificationCode.class);
                    i.putExtra(MOBILE_NO_TAG,mobile_number);
                    i.putExtra(ENROLL_TAG,enrollmentNumber.getText().toString());
                    i.putExtra(VERIFICATION_TAG,mVerificationId);
                    i.putExtra(RESEND_TOKEN,mResendToken);
                    i.putExtra(INTENT_FORGOT,"0");
                    startActivity(i);

                    // ...
                }
            };

    public void verifyPhone(String phoneNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks){

        FirebaseApp.initializeApp(getActivity());

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                30,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallback
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {


        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Toast.makeText(getActivity(),"Verification successful",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();

                            Intent i = new Intent(getActivity(),RegisterActivity.class);
                            i.putExtra(ENROLL_TAG,enrollmentNumber.getText().toString());
                            i.putExtra(MOBILE_NO_TAG,mobile_number);
                            startActivity(i);
                            signup.revertAnimation();
                            login.setClickable(true);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getActivity(),"Wrong verification code",
                                        Toast.LENGTH_SHORT).show();
                                signup.revertAnimation();
                                login.setClickable(true);

                            }
                        }
                    }
                });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        signup.dispose();
    }

    private void usernameCheck() {
        request = new StringRequest(Request.Method.POST, URL_USERNAME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
                        mProgressBar.setVisibility(View.GONE);
                        username_check_int =0;
                        enrollmentNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.cross, 0);
                        Toast.makeText(getActivity(),"Enrollment No. already registered, Please Login.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        username_check_int = 1;
                        enrollmentNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.right_tick, 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    username_check_int = 0;
                    mProgressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Unable to connect", Toast.LENGTH_SHORT).show();
                username_check_int = 0;
                enrollmentNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                mProgressBar.setVisibility(View.GONE);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("enrollment", enrollmentNumber.getText().toString());
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);

    }

    private void mobileNoCheck() {
        request = new StringRequest(Request.Method.POST, URL_MOBILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
                        mobileProgressbar.setVisibility(View.GONE);
                        mobile_no_check_int = 0;
                        mobileNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.cross, 0);
                        Toast.makeText(getActivity(),"Mobile No. already registered, Please Login.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        mobileProgressbar.setVisibility(View.GONE);
                        mobile_no_check_int = 1;
                        mobileNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.right_tick, 0);
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
                mobileNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                mobileProgressbar.setVisibility(View.GONE);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("mobile_no","+91"+mobileNumber.getText().toString());
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);

    }

}
