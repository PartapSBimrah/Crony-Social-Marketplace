package com.abhigam.www.foodspot;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.disklrucache.DiskLruCache;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;


/**
 * Created by sourabhzalke on 26/02/18.
 */

public class CreatePostFragment extends Fragment{


    private TextView full_name,send_post;
    private ImageView mUploadImage;
    private CircularNetworkImageView mProfileImage;
    private EditText mCaption;
    private String enroll_string,full_name_string,user_id;
    private final static String URL_SEND_NOTIFICATION = "http://13.233.234.79/send_notification.php";
    //keys
    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME= "pref_username";
    private static final String PREF_PHONE_NUMBER = "pref_phone_number";
    private static final String PREF_EMAIL = "pref_email";
    private static final String PREF_BIRTHDAY = "pref_birthday";
    private static final String PREF_SEX= "pref_sex";
    private static final String PREF_BRANCH = "pref_branch";
    private static final String PREF_YEAR_ADM = "pref_year_adm";
    private static final String PREF_ABOUT_ME = "pref_about_me";
    private static final String PREF_POSTS = "pref_posts";
    //URLs
    private final static String URL_UPLOAD_POST_IMAGE = "http://13.233.234.79/update_post_image.php";
    private final static String URL_POST_DATA = "http://13.233.234.79/insert_post.php";
    private final static String URL_REWARD = "http://13.233.234.79/reward.php";
    private String URL_USER_DETAILS = "http://13.233.234.79/user_id.php";
    private String URL_REWARD_NOTIFICATION = "http://13.233.234.79/reward_notification.php";

            

    //TAGS
    private final int IMG_REQUEST = 1;
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;
    private Bitmap mBitmap;
    //for server
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private long timeInMillis = Calendar.getInstance().getTimeInMillis();
    private String is_image = "0";
    private String posts="";
    private String rewards="";
    private SpotsDialog mSpotsDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        enroll_string = prefs.getString(PREF_USER_NAME,"");
        full_name_string = prefs.getString(PREF_FULLNAME,"");
        user_id = prefs.getString(PREF_USER_ID,"");

        mRequestQueue = Volley.newRequestQueue(getActivity());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_create_post,container,false);

        full_name = v.findViewById(R.id.fullname);
        send_post = v.findViewById(R.id.post);
        mUploadImage = v.findViewById(R.id.upload_post);
        mCaption = v.findViewById(R.id.caption);
        mProfileImage = v.findViewById(R.id.circular_profile);

        //getting profile pic
        Glide
                .with(getActivity())
                .load("http://13.233.234.79/uploads/profile_pic/" +enroll_string+ "_profile.jpg")
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                .skipMemoryCache(true)
                .centerCrop()
                .into(new SimpleTarget<Bitmap>(300, 300) { //width and height
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        mProfileImage.setImageBitmap(resource);
                    }
                });

        full_name.setText(full_name_string);

        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        if (toolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI); //Opening Gallery
                    startActivityForResult(i, IMG_REQUEST);
                }else if(Build.VERSION.SDK_INT <23){
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI); //Opening Gallery
                    startActivityForResult(i, IMG_REQUEST);
                } else{
                    external_runtime_permissions();
                }
            }
        });

        final String sb = "You can share text or image only post, but can't share empty post.";
        send_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBitmap==null && mCaption.getText().toString().equals("")){
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Empty Post")
                            .setMessage(sb)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }else {
                        mSpotsDialog = new SpotsDialog(getActivity(),R.style.CustomLoadingPost);
                        mSpotsDialog.show();
                        post_todo(Long.toString(timeInMillis));
                        if (mBitmap != null)
                        uploadPostImage(Long.toString(timeInMillis));
                }
            }
        });

        getUserIDFirst(enroll_string);


        return v;
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
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                    if(Build.VERSION.SDK_INT<23){
                    try {
                        mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                        is_image = "1";
                        mUploadImage.setImageBitmap(mBitmap);
                    }
                         catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                            is_image = "1";
                            mUploadImage.setImageBitmap(mBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void post_todo(final String time_ago){

        mStringRequest = new StringRequest(Request.Method.POST, URL_POST_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if(!success.equals("")){
                        Toast.makeText(getActivity(),"Successfully posted",Toast.LENGTH_SHORT).show();
                        if(!posts.equals("")){
                       /*     if(Integer.parseInt(posts) < 5 && Integer.parseInt(posts)==Integer.parseInt(rewards)) {
                                //rewards(enroll_string, user_id + System.currentTimeMillis());
                                //reward_notification();
                                //reward_notification_inside();
                            }
                            */
                        }
                        if(mBitmap==null) {
                            if(mSpotsDialog.isShowing()){
                                mSpotsDialog.dismiss();
                            }
                            getActivity().onBackPressed();
                        }
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                    if(mSpotsDialog.isShowing()){
                        mSpotsDialog.dismiss();
                    }
                    Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                if(mSpotsDialog.isShowing()){
                    mSpotsDialog.dismiss();
                }
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("caption",mCaption.getText().toString());
                hashMap.put("time_ago",time_ago);
                hashMap.put("is_image",is_image);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);


    }

    private void reward_notification_inside(){

        mStringRequest= new StringRequest(Request.Method.POST, URL_REWARD_NOTIFICATION,
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
                hashMap.put("thing","reward");
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void reward_notification(){

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
            protected Map<String, String> getParams() throws AuthFailureError{
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("title",enroll_string);
                hashMap.put("message","₹1 added to wallet for your new post. You can use the money at stores.");
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void uploadPostImage(final String time_ago) {

        Bitmap resource = mBitmap;
        resource = Bitmap.
                createScaledBitmap(resource,
                        (int)(resource.getWidth()*0.6),
                        (int)(resource.getHeight()*0.6), true);
        final String bitmap_string = imageToString(resource);

        mStringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD_POST_IMAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                getActivity().onBackPressed();
                if(mSpotsDialog.isShowing()){
                    mSpotsDialog.dismiss();
                }
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray(Config.JSON_ARRAY);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(mSpotsDialog.isShowing()){
                        mSpotsDialog.dismiss();
                    }
                    Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(mSpotsDialog.isShowing()){
                    mSpotsDialog.dismiss();
                }
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("image",bitmap_string);
                hashMap.put("timestamp",time_ago);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private String imageToString(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);

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

    public void rewards(final String username,final String txnId){

        mStringRequest = new StringRequest(Request.Method.POST, URL_REWARD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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
                hashMap.put("amount_to_add","1");
                hashMap.put("sender_enroll",username);
                hashMap.put("sender_id",user_id);
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                hashMap.put("txnId",txnId);
                hashMap.put("store_name","Rewards");
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    private void getUserIDFirst(final String enrollment){

        mStringRequest = new StringRequest(Request.Method.POST, URL_USER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            posts = jsonObject.getString("posts");
                            rewards = jsonObject.getString("rewards");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
