package com.abhigam.www.foodspot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
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
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sourabhzalke on 24/06/17.
 */

public class EditProfileFragment extends Fragment{

    private CircularNetworkImageView profile;
    private LinearLayout focus;
    private EditText username,email,first_name,last_name,bio_editext,branch_editext;
    private TextView change_photo,logout,username_taken,email_taken;

    private CircularProgressButton save_changes;

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

    private StringRequest request;
    private RequestQueue mRequestQueue;

    private final int IMG_REQUEST = 1;
    private final int ID_IMG_REQUEST = 2;
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;
    public int upload_profile=0,upload_data=0;

    private Bitmap mBitmap;

    private String username_text,email_text,full_n,first_username_text,
            first_string,last_string,first_email_text,private_string="0",bio_string,
            user_id_card,verified_string,verify_requested_string,email_string,fullname_string,
            enrollment_string,branch_string;

    private LinearLayout id_linear;
    private Button upload_id;
    private TextView id_hint_text;

    private Switch private_ac;
    AlertDialog dialog;

    //URLs
    private static final String URL = "http://13.233.234.79/uploader_update.php";
    private static final String URL_ID_CARD = "http://13.233.234.79/upload_id_card.php";
    private static final String URL_SAVE_CHANGES = "http://13.233.234.79/update_edit_profile.php";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        SharedPreferences prefs = SaveSharedPreference.getSharedPreferences(getActivity().getApplicationContext());
        private_string = prefs.getString(PREF_PRIVATE,"");
        bio_string = prefs.getString(PREF_BIO,"");
        user_id_card = prefs.getString(PREF_USER_ID,"");
        verified_string = prefs.getString(PREF_VERIFIED,"");
        verify_requested_string = prefs.getString(PREF_VERIFY_REQUESTED,"");
        email_string = prefs.getString(PREF_EMAIL,"");
        fullname_string = prefs.getString(PREF_FULLNAME,"");
        enrollment_string = prefs.getString(PREF_USER_NAME,"");
        branch_string = prefs.getString(PREF_BRANCH,"");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_edit_profile,container,false);


        upload_data=0;

        username_text = enrollment_string;
        first_username_text = username_text;
        email_text = email_string;
        first_email_text = email_text;
        full_n = fullname_string;

        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        if (toolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        //upload id
        id_linear = v.findViewById(R.id.id_linear);
        upload_id = v.findViewById(R.id.upload_id);
        id_hint_text = v.findViewById(R.id.id_hint_text);

        upload_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED){
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI); //Opening Gallery
                    startActivityForResult(i, ID_IMG_REQUEST);
                }else {
                    external_runtime_permissions();
                }
            }
        });

        if(verify_requested_string.equals("0")){
            id_linear.setVisibility(View.VISIBLE);
        }else{
            id_linear.setVisibility(View.GONE);
        }


        //for clearing focus
        focus = v.findViewById(R.id.focus);
        focus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                username.clearFocus();
            }
        });

        private_ac = v.findViewById(R.id.switch_private);
        private_ac.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    private_string="1";
                else
                    private_string="0";
            }
        });

        if(private_string.equals("1"))
            private_ac.setChecked(true);

        //for picking image
        profile = v.findViewById(R.id.circular_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI); //Opening Gallery
                    startActivityForResult(i, IMG_REQUEST);
                }else {
                    external_runtime_permissions();
                }
            }
        });

        change_photo = v.findViewById(R.id.change_photo);
        change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI); //Opening Gallery
                    startActivityForResult(i, IMG_REQUEST);
                }else {
                    external_runtime_permissions();
                }
            }
        });

        //logging user out
        logout = v.findViewById(R.id.log_out);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                            SaveSharedPreference.clearUserName(getActivity().getApplicationContext());
                            Intent i = new Intent(getActivity(),FirstScreen.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
            }
        });


        //EditTexts and setting strings from profile
        username = v.findViewById(R.id.username);
        username.setText(username_text);
        email = v.findViewById(R.id.email);
        if(!email_text.equals(""))
        email.setText(email_text);
        branch_editext = v.findViewById(R.id.branch);
        branch_editext.setText(branch_string);

        //converting fullname into first_name and lastname
        Pattern pattern = Pattern.compile(" ");
        Matcher matcher = pattern.matcher(full_n);

        if (matcher.find()){
            first_string = full_n.substring(0, matcher.start());
            last_string = full_n.substring(matcher.end());
        }

        first_name = v.findViewById(R.id.first_name);
        first_name.setText(first_string);
        last_name = v.findViewById(R.id.last_name);
        last_name.setText(last_string);

        //Textview for wrong input
        username_taken = v.findViewById(R.id.username_taken);
        email_taken = v.findViewById(R.id.email_taken);
        bio_editext = v.findViewById(R.id.bio);
        bio_editext.setText(bio_string);


        //focus removing
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View z, boolean hasFocus){
                if(!hasFocus){
                    hideSoftKeyboard(getActivity(),v);
                }
            }
        });

        //focus removing
        bio_editext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View z, boolean hasFocus){
                if(!hasFocus){
                    hideSoftKeyboard(getActivity(),v);
                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View z, boolean hasFocus) {
                if(!hasFocus){
                    hideSoftKeyboard(getActivity(),v);
                }
            }
        });

        first_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View z, boolean hasFocus) {
                if(!hasFocus){
                    hideSoftKeyboard(getActivity(),v);
                }
            }
        });

        last_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View z, boolean hasFocus) {
                if(!hasFocus){
                    hideSoftKeyboard(getActivity(),v);
                }
            }
        });


        //At last saving changes button work
        save_changes = v.findViewById(R.id.save_changes);
        save_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_changes();
                username_taken.setVisibility(View.GONE);
                email_taken.setVisibility(View.GONE);
            }
        });


        //loading profile pic
                 Glide
                .with(getActivity())
                .load("http://13.233.234.79/uploads/profile_pic/" +username_text+ "_profile.jpg")
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                .skipMemoryCache(true)
                .centerCrop()
                .into(new SimpleTarget<Bitmap>(300, 300) { //width and height
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        mBitmap = resource;
                        profile.setImageBitmap(mBitmap);
                    }
                });

        return v;
    }

    public void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {

            Uri path = data.getData();

            try {
                if(Build.VERSION.SDK_INT<23){
                    mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), path);
                    CropImage.activity(path)
                            .start(getContext(), this);
                }else {
                    try {
                        mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), path);
                        CropImage.activity(path)
                                .start(getContext(), this);
                        //mUploadImage.setImageBitmap(mBitmap);
                        //mUploadImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(requestCode == ID_IMG_REQUEST && resultCode == RESULT_OK && data !=null){
            Uri path = data.getData();

            try{

                try{
                    Bitmap id_card_bitmap =
                            MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), path);
                    dialog = new SpotsDialog(getActivity(),R.style.CustomLoading);
                    dialog.show();
                    uploadIdentityCard(id_card_bitmap);
                }catch(FileNotFoundException e) {
                    e.printStackTrace();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                if(Build.VERSION.SDK_INT<23){
                    try {
                        mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                        profile.setImageBitmap(mBitmap);
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }else{
                    try{
                        mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                        profile.setImageBitmap(mBitmap);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }

            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
    }

    public static Bitmap cropCenter(Bitmap bmp) {
        int dimension = Math.min(bmp.getWidth(), bmp.getHeight());
        return ThumbnailUtils.extractThumbnail(bmp, dimension, dimension);
    }

    private void uploadIdentityCard(final Bitmap bitmap) {

        Bitmap resource = bitmap;
        resource = Bitmap.
                createScaledBitmap(resource,
                        (int)(resource.getWidth()*0.6),
                        (int)(resource.getHeight()*0.6), true);
        final String bitmap_string = imageToString(resource);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ID_CARD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                       Toast.makeText(getActivity(),getActivity().getResources().getString(
                               R.string.changes_reflect
                       ),Toast.LENGTH_LONG).show();
                       id_linear.setVisibility(View.GONE);
                       dialog.dismiss();
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(getActivity()).edit();
                        editor.putString(PREF_VERIFY_REQUESTED,"1");
                        editor.commit();
                    }else{
                        Toast.makeText(getActivity(),"Error",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id_card);
                hashMap.put("image",bitmap_string);
                hashMap.put("enrollment",username_text);
                return hashMap;
            }
        };
        UploadSingleton.getmInstance(getActivity()).addToRequestQue(stringRequest);
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
                    if (name.equals("")){
                        upload_profile = 1;
                        if(upload_data != 0)
                        username_text = username.getText().toString();
                        mRequestQueue.getCache().remove("http://13.233.234.79/uploads/profile_pic/"+username.getText().toString()+"_profile.jpg");
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
                hashMap.put("username_update",username.getText().toString());
                hashMap.put("image",bitmap_string);
                hashMap.put("username",username_text);
                return hashMap;
            }
        };
        UploadSingleton.getmInstance(getActivity()).addToRequestQue(stringRequest);
    }


    private String imageToString(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);

    }



    private void save_changes(){


        save_changes.startAnimation();

        request = new StringRequest(Request.Method.POST, URL_SAVE_CHANGES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                save_changes.revertAnimation();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success_everything")){
                        upload_data = 1;
                        uploadImage();

                        first_email_text = email.getText().toString();
                        email_text = email.getText().toString();
                        first_username_text = username.getText().toString();
                        bio_string = bio_editext.getText().toString();

                        Toast.makeText(getActivity(),R.string.changes_saved,Toast.LENGTH_SHORT).show();
                        SaveSharedPreference.setUserName(getContext(),username.getText().toString().toUpperCase());
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(getActivity()).edit();
                        editor.putString(PREF_USER_NAME,first_username_text);
                        editor.putString(PREF_FULLNAME,first_name.getText().toString()+" "+
                                        last_name.getText().toString());
                        editor.putString(PREF_PRIVATE,private_string);
                        editor.putString(PREF_BIO,bio_editext.getText().toString());
                        editor.commit();
                    }
                    else if(jsonObject.names().get(0).equals("empty")){
                        Toast.makeText(getActivity(),"Please fill all the fields",Toast.LENGTH_SHORT).show();
                    }
                    else if(jsonObject.names().get(0).equals("success_username")){

                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(getActivity()).edit();
                        editor.putString(PREF_FULLNAME,first_name.getText().toString()+" "+
                                last_name.getText().toString());
                        editor.putString(PREF_PRIVATE,private_string);
                        editor.putString(PREF_BIO,bio_editext.getText().toString());
                        editor.putString(PREF_EMAIL,email.getText().toString());
                        editor.commit();

                        if(!username_text.equals(username.getText().toString())) {    //for showing when not same as username
                            username_taken.setVisibility(View.VISIBLE);
                        }
                        else {
                            email_text = email.getText().toString();
                            first_email_text = email.getText().toString();
                            upload_data = 0;
                            uploadImage();
                            Toast.makeText(getActivity(),R.string.changes_saved,Toast.LENGTH_SHORT).show();
                            }
                        }
                    else if(jsonObject.names().get(0).equals("success_email")){

                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(getActivity()).edit();
                        editor.putString(PREF_FULLNAME,first_name.getText().toString()+" "+
                                last_name.getText().toString());
                        editor.putString(PREF_PRIVATE,private_string);
                        editor.putString(PREF_BIO,bio_editext.getText().toString());
                        editor.commit();

                        if(!email_text.equals(email.getText().toString())) {
                            email_taken.setVisibility(View.VISIBLE);
                        }
                        upload_data = 1;
                        uploadImage();
                        first_username_text = username.getText().toString();
                        Toast.makeText(getActivity(),R.string.changes_saved,Toast.LENGTH_SHORT).show();
                        SaveSharedPreference.setUserName(getContext(),username.getText().toString().toUpperCase());
                    }
                    else if(jsonObject.names().get(0).equals("success_both")){

                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(getActivity()).edit();
                        editor.putString(PREF_FULLNAME,first_name.getText().toString()+" "+
                                last_name.getText().toString());
                        editor.putString(PREF_PRIVATE,private_string);
                        editor.putString(PREF_BIO,bio_editext.getText().toString());
                        editor.commit();

                        if(!username_text.equals(username.getText().toString())) {
                            username_taken.setVisibility(View.VISIBLE);
                        }
                        if(!email_text.equals(email.getText().toString())){
                            email_taken.setVisibility(View.VISIBLE);  //for showing when not same as username
                        }

                        //first_username_text the intent username firstly send for updating profile if nothing is changed
                        if(first_username_text.equals(username.getText().toString()) && first_email_text.equals(email.getText().toString()))
                        {
                            upload_data = 0;
                            uploadImage();
                            Toast.makeText(getActivity(),R.string.changes_saved,Toast.LENGTH_SHORT).show();
                        }

                    }
                    else if(jsonObject.names().get(0).equals("connect")){
                        Toast.makeText(getActivity(),jsonObject.getString("connect"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                save_changes.revertAnimation();
                Toast.makeText(getActivity(),"Unable to connect",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("username_update",username.getText().toString());
                hashMap.put("username",first_username_text);
                hashMap.put("email",email.getText().toString());
                hashMap.put("first_name",first_name.getText().toString());
                hashMap.put("last_name",last_name.getText().toString());
                hashMap.put("bio",bio_editext.getText().toString());
                hashMap.put("private_ac",private_string);
                return hashMap;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

        private void external_runtime_permissions(){

            if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if(shouldShowRequestPermissionRationale(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts
                }

                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant that should be quite unique
            }
        }


        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if(requestCode == 101){
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){

                }else{
                    external_runtime_permissions();
                }
            }
        }
    }
