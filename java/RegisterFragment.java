package com.abhigam.www.foodspot;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sourabhzalke on 18/05/17.
 */

public class RegisterFragment extends Fragment {


    private EditText firstname, lastname, email, password, birthday,admission_year;
    private ImageView setphoto;
    private CircleImageView uploaded;
    private final int IMG_REQUEST = 1;
    private Bitmap mBitmap;
    private CircularProgressButton msignUp;
    private String username_save,username,mobile_no;
    private ProgressBar mEmailProgressBar;
    private Spinner mSexSpinner;
    private static final String URL_SEND_TOKEN = "http://13.233.234.79/update_user_id_fcm_token.php";


    //DatePicker
    final Calendar myCalendar = Calendar.getInstance();

    private static final String URL = "http://13.233.234.79/uploader.php";

    private static final String URL_EMAIL = "http://13.233.234.79/email_control.php";

    private static final String URL_SIGN_UP = "http://13.233.234.79/register_control.php";
    private static final String ENROLL_TAG = "enroll_no";
    private static final String MOBILE_NO_TAG = "mobile_no";

    private static final String WEB_URL_INTENT = "web_url_intent_crony";
    private static final String crony_first_url = "https://www.crony.co.in/term-and-conditions";

    public int upload_profile=0,upload_data=0,email_ok=1;

    private StringRequest request;

    private RequestQueue mRequestQueue;

    private String[] sex;

    private String bir_day;

    private int year_ad = 0;

    private LinearLayout terms_sign_up;
    private String user_id;
    private String username_get;
    private String fcm;

    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME= "pref_username";
    private static final String PREF_PHONE_NUMBER = "pref_phone_number";
    private static final String PREF_EMAIL = "pref_email";
    private static final String PREF_BIRTHDAY = "pref_birthday";
    private static final String PREF_SEX= "pref_sex";
    private static final String PREF_YEAR_ADM = "pref_year_adm";
    private static final String PREF_POSTS = "pref_posts";
    private static final String PREF_PRIVATE = "pref_private_ac";
    private static final String PREF_BIO = "pref_bio";
    private static final String PREF_VERIFY_REQUESTED = "pref_verify_request";
    private static final String PREF_VERIFIED = "pref_verified";
    private String URL_REWARD_NOTIFICATION = "http://13.233.234.79/reward_notification.php";
    private String URL_SEND_NOTIFICATION = "http://13.233.234.79/send_notification.php";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_register, container, false);

        firstname = v.findViewById(R.id.first_name);
        lastname = v.findViewById(R.id.last_name);
        email = v.findViewById(R.id.email);
        password = v.findViewById(R.id.password);
        birthday = v.findViewById(R.id.birthday);
        uploaded = v.findViewById(R.id.uploaded);
        msignUp = v.findViewById(R.id.signup);
        mEmailProgressBar =  v.findViewById(R.id.progressBar1);
        mSexSpinner = v.findViewById(R.id.sex);
        admission_year = v.findViewById(R.id.admission_year);
        terms_sign_up = v.findViewById(R.id.terms);

        terms_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),WebActivity.class);
                i.putExtra(WEB_URL_INTENT,crony_first_url);
                getActivity().startActivity(i);
            }
        });


        username = getActivity().getIntent().getStringExtra(ENROLL_TAG);
        mobile_no = getActivity().getIntent().getStringExtra(MOBILE_NO_TAG);

        //password hint font problem
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());

        //initialising options
        sex = new String[]{"Select","Female", "Male"};


        //Initialising adapters
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(getContext(),
                                              android.R.layout.simple_spinner_dropdown_item, sex);


        //setting Adapter
        mSexSpinner.setAdapter(sexAdapter);

        mRequestQueue = Volley.newRequestQueue(getActivity());

        upload_data = 0;
        upload_profile = 0;


        Toolbar toolbar = v.findViewById(R.id.toolbar_top);


        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
           // final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
           // ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        setphoto = v.findViewById(R.id.set_photo);

        setphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                msignUp.revertAnimation();
                runtime_permissions();

            }
        });

        uploaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runtime_permissions();
            }
        });

        admission_year.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!admission_year.getText().toString().equals("")) {

                    if (Integer.parseInt(admission_year.getText().toString()) > 1957 && Integer.parseInt(
                            admission_year.getText().toString()) <= 2019) {
                        admission_year.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.right_tick, 0);
                    }else{
                        admission_year.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.cross, 0);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s){

            }
        });

        msignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View x) {
                username_save = username;

                bir_day = birthday.getText().toString();
                //birthday format check
                Date date = null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                    date = sdf.parse(bir_day);
                    if (!bir_day.equals(sdf.format(date))) {
                        date = null;
                    }
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                //admission_year check
                if(admission_year.getText().toString().equals("") || Integer.parseInt(admission_year.
                            getText().toString()) <1957 || Integer.parseInt(admission_year.getText()
                              .toString()) >2019){
                    year_ad =0;
                }else{
                    year_ad = Integer.parseInt(admission_year.getText().toString());
                }

                if(email_ok == 0) {
                    if (!isValidPassword(password.getText().toString().trim())) {
                        Toast.makeText(getContext(),"Invalid password pattern",Toast.LENGTH_SHORT).show();
                    }
                    else if(bir_day.equals("")){
                        Toast.makeText(getContext(),"Please enter date of Birth",Toast.LENGTH_SHORT).show();
                    }
                    else if(date == null){
                        Toast.makeText(getContext(),"Please enter valid date format",Toast.LENGTH_LONG).show();
                    }
                    else if(mSexSpinner.getSelectedItemPosition()==0){
                        Toast.makeText(getContext(),"Please select Sex",Toast.LENGTH_SHORT).show();

                      }
                   else if(year_ad == 0){
                        Toast.makeText(getContext(),"Please enter correct year of Admission",Toast.LENGTH_SHORT).show();
                        admission_year.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.cross,0);
                    }
                    else{
                          hideSoftKeyboard(getActivity(),v);
                          msignUp.startAnimation();
                          signup();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Enter valid email pattern", Toast.LENGTH_SHORT).show();
                    msignUp.revertAnimation();
                }


            }
        });


        //username.addTextChangedListener(new TextWatcher() {
          //  @Override
            //public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              //  username.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
           // }

//            @Override
  //          public void onTextChanged(CharSequence s, int start, int before, int count) {
    //            username.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
      //          mProgressBar.setVisibility(View.VISIBLE);
        //    }

          //  @Override
            //public void afterTextChanged(Editable s){

              //  usernameCheck();

           // }
       // });


        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mEmailProgressBar.setVisibility(View.VISIBLE);
                    emailCheck();
                }
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){


            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel();
                hideSoftKeyboard(getActivity(),v);   //hiding keyboard
            }
        };

        birthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                 new DatePickerDialog(getActivity(),date,myCalendar
                                   .get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),
                                   myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });


        return v;
    }


    public void openGallery(){

        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI); //Opening Gallery
        startActivityForResult(i, IMG_REQUEST);

    }

    public  static boolean isValidEmail(CharSequence target) {
        if(TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }

    private void updateLabel() {

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        birthday.setText(sdf.format(myCalendar.getTime()));
        birthday.setSelection(birthday.getText().length());  //setting cursor
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {

            Uri path = data.getData();
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), path);
                uploaded.setImageBitmap(mBitmap);
                uploaded.setVisibility(View.VISIBLE);
                setphoto.setVisibility(View.GONE);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
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

    private boolean runtime_permissions(){
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            return true;
        }else{
            openGallery();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                openGallery();
            }else{

            }
        }
    }

    private void uploadImage() {

        Bitmap resource = mBitmap;

        resource = Bitmap.
                createScaledBitmap(resource,
                        (int)(resource.getWidth()*0.6),
                        (int)(resource.getHeight()*0.6), true);
        final String bitmap_string = imageToString(resource);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String name = "";
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray(Config.JSON_ARRAY);
                    JSONObject profile_photo = jsonArray.getJSONObject(0);
                    name = profile_photo.getString(Config.RESPONSE);
                    if (!name.equals("")) {
                            upload_profile = 1;
                    }else{
                        Toast.makeText(getContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("username",username_save.toUpperCase());
                hashMap.put("image",bitmap_string);
                return hashMap;
            }
        };
        UploadSingleton.getmInstance(getActivity()).addToRequestQue(stringRequest);
    }

    private String imageToString(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);

    }



    private void emailCheck(){
        request = new StringRequest(Request.Method.POST, URL_EMAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
                        mEmailProgressBar.setVisibility(View.GONE);
                        email_ok = 1;
                        email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.cross, 0);
                        Toast.makeText(getContext(),"Email already registered, you can Login.",Toast.LENGTH_SHORT).show();
                    } else {
                        mEmailProgressBar.setVisibility(View.GONE);
                        if(isValidEmail(email.getText().toString())) {
                            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.right_tick, 0);
                            email_ok = 0;
                        }
                        else {
                            email_ok = 1;
                            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.cross, 0);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mEmailProgressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Unable to connect", Toast.LENGTH_SHORT).show();
                email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                mEmailProgressBar.setVisibility(View.GONE);
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("email", email.getText().toString());
                return hashMap;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    private void signup(){

        if(mBitmap==null) {
            if (mSexSpinner.getSelectedItem().toString().equals("Male")) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.male_avatar);
            } else {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.female_avatar);
            }
        }

        request = new StringRequest(Request.Method.POST, URL_SIGN_UP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.names().get(0).equals("error")) {
                            Toast.makeText(getContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                            msignUp.revertAnimation();
                        }else if (jsonObject.names().get(0).equals("empty")) {
                            Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                            msignUp.revertAnimation();
                        }else{
                            upload_data = 1;
                            if(mBitmap!=null)
                            uploadImage();
                            msignUp.revertAnimation();

                            JSONArray jsonArray = jsonObject.getJSONArray("items");

                            JSONObject jsonRow = jsonArray.getJSONObject(0);
                            SharedPreferences.Editor editor = PreferenceManager.
                                        getDefaultSharedPreferences(getActivity()).edit();
                            editor.putString(PREF_USER_NAME,jsonRow.getString("enrollment"));
                            editor.putString(PREF_FULLNAME,jsonRow.getString("first_name")
                            +" "+jsonRow.getString("last_name"));
                            editor.putString(PREF_USER_ID,jsonRow.getString("id"));
                            editor.putString(PREF_PHONE_NUMBER,jsonRow.getString("mobile_no"));
                            editor.putString(PREF_EMAIL,jsonRow.getString("email"));
                            editor.putString(PREF_BIRTHDAY,jsonRow.getString("birthday"));
                            editor.putString(PREF_SEX,jsonRow.getString("sex"));
                            editor.putString(PREF_YEAR_ADM,jsonRow.getString("year_adm"));
                            editor.putString(PREF_POSTS,jsonRow.getString("posts"));
                            editor.putString(PREF_PRIVATE,jsonRow.getString("private_ac"));
                            editor.putString(PREF_BIO,jsonRow.getString("bio"));
                            editor.putString(PREF_VERIFIED,jsonRow.getString("verified"));
                            editor.putString(PREF_VERIFY_REQUESTED,jsonRow.getString("verify_requested"));
                            editor.apply();
                            user_id = jsonRow.getString("id");
                            username_get = jsonRow.getString("enrollment");
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            fcm = prefs.getString(getActivity().getResources().getString(R.string.FCM_TOKEN), "");
                            send_fcm_token(user_id,fcm);
                           // reward_notification_inside();

                            Toast.makeText(getActivity(), "Sign up successfully done", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getActivity(), TakeBranch.class);
                            i.putExtra(ENROLL_TAG, username);
                            startActivity(i);

                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                msignUp.revertAnimation();
                Toast.makeText(getActivity(),"Unable to connect",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("first_name",firstname.getText().toString().substring(0,1).toUpperCase()+
                            firstname.getText().toString().substring(1));
                hashMap.put("last_name",lastname.getText().toString().substring(0,1).toUpperCase()+
                           lastname.getText().toString().substring(1));
                hashMap.put("enrollment",username.toUpperCase());
                hashMap.put("email",email.getText().toString());
                hashMap.put("password",password.getText().toString());
                hashMap.put("birthday",birthday.getText().toString());
                hashMap.put("sex",sex[mSexSpinner.getSelectedItemPosition()]);
                hashMap.put("year_adm",admission_year.getText().toString());
                hashMap.put("mobile_no",mobile_no);
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                return hashMap;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    private void reward_notification_inside(){

        request= new StringRequest(Request.Method.POST, URL_REWARD_NOTIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("sender_id",user_id);
                hashMap.put("thing","signup");
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    public void send_fcm_token(final String user_id, final String fcm_token) {
        request = new StringRequest(Request.Method.POST, URL_SEND_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")) {
                       // reward_notification();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_id", user_id);
                hashMap.put("fcm_token", fcm_token);
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    private void reward_notification(){

        request  = new StringRequest(Request.Method.POST, URL_SEND_NOTIFICATION, new Response.Listener<String>() {
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
                hashMap.put("user_id",user_id);
                hashMap.put("title",username_get);
                hashMap.put("message","Welcome to Crony. You have received ₹10 as Sign Up Bonus. Receive" +
                        " more rewards on every new post.");
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);

    }


}
