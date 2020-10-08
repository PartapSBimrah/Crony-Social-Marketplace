package com.abhigam.www.foodspot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class verificationCode extends AppCompatActivity {

    private TextView mMobileOTP,resend,timer;
    private EditText mCode;
    private CircularProgressButton submit;
    private String mVerificationId,code,enroll_no,mobile_no,with_mob_no;
    private PhoneAuthProvider.ForceResendingToken mResendingToken;
    private FirebaseAuth mAuth;
    private static final String ENROLL_TAG = "enroll_no";
    private static final String MOBILE_NO_TAG = "mobile_no";
    private static final String VERIFICATION_TAG = "verificationid";
    View parentLayout;
    int time=30;
    private static final String INTENT_FORGOT = "forgot";
    private String forgot_control="0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);
        parentLayout = findViewById(android.R.id.content);

        mVerificationId = getIntent().getStringExtra(VERIFICATION_TAG);
        mobile_no = getIntent().getStringExtra(MOBILE_NO_TAG);
        enroll_no = getIntent().getStringExtra(ENROLL_TAG);
        forgot_control = getIntent().getStringExtra(INTENT_FORGOT);

        startTimer();

        mMobileOTP = findViewById(R.id.text_mobile_otp);
        mCode =  findViewById(R.id.code);
        submit = findViewById(R.id.next);
        resend = findViewById(R.id.resend);
        resend.setEnabled(false);
        timer = findViewById(R.id.timer);


        with_mob_no = getString(R.string.enter_6_digit_code)+" "+mobile_no+".";

        mMobileOTP.setText(with_mob_no);

        mAuth = FirebaseAuth.getInstance();

        resend.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPhone(mobile_no,mCallbacks);
                resend.setTextColor(getResources().getColor(R.color.transparent_white));
                resend.setClickable(false);
                resend.setEnabled(false);
                time = 30;
                startTimer();

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mCode.getText().toString().length()!=6){
                    Snackbar.make(v, "Please enter 6 digit code", Snackbar.LENGTH_LONG)
                            .show();
                }else{
                    hideSoftKeyboard(verificationCode.this,parentLayout);
                    submit.startAnimation();

                    code = mCode.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code);
                    signInWithPhoneAuthCredential(credential);
                }



            }
        });

    }

    private void startTimer(){
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("0:"+checkDigit(time));
                time--;
            }

            public void onFinish() {
                timer.setText("0:0");
                resend.setTextColor(getResources().getColor(R.color.white));
                resend.setClickable(true);
                resend.setEnabled(true);
            }

        }.start();

    }

    public void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }



public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
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
                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.

                    submit.revertAnimation();

                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                        // ...
                    } else if (e instanceof FirebaseTooManyRequestsException){
                        // The SMS quota for the project has been exceeded
                        Toast.makeText(getApplicationContext(),"Please try again later",Toast.LENGTH_SHORT).show();
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
                    Snackbar.make(parentLayout,"Verification code resent",Snackbar.LENGTH_LONG).show();

                    // ...
                }
            };



    public void verifyPhone(String phoneNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks){

        FirebaseApp.initializeApp(this);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                30,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallback
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {


        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information


                            FirebaseUser user = task.getResult().getUser();

                            if(forgot_control.equals("8")) {
                                Intent i = new Intent(getApplicationContext(),EditPassword.class);
                                i.putExtra(MOBILE_NO_TAG,mobile_no);
                                startActivity(i);
                                submit.revertAnimation();
                            }else {
                                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                                i.putExtra(ENROLL_TAG, enroll_no);
                                i.putExtra(MOBILE_NO_TAG, mobile_no);
                                startActivity(i);
                                submit.revertAnimation();
                            }
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(),"Wrong verification code",
                                        Toast.LENGTH_SHORT).show();
                                submit.revertAnimation();
                            }
                        }
                    }
                });
    }



}
