package com.abhigam.www.foodspot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sourabhzalke on 02/03/18.
 */

public class CreateAdFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private AdapterChoosePhotos mAdapterChoosePhotos;
    private List<DataRecycler> mDataRecyclers = new ArrayList<>();
    private WrapContentLinearLayoutManager linearLayoutManager;
    private ImageView add_photo;
    private final int IMG_REQUEST = 1;
    private Bitmap mBitmap;
    private Spinner mSpinner;
    private EditText title,description,expected_price;
    private CheckBox mCheckBox;
    private CircularProgressButton post_add_button;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String URL_POST_ADD = "http://13.233.234.79/post_ad.php";
    private String URL_UPLOAD_IMAGES = "http://13.233.234.79/upload_images.php";
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
    private String username,user_fullname,user_id;
    private ArrayList<Bitmap> mBitmaps = new ArrayList<>();
    AlertDialog dialog;
    Handler handler = new Handler();
    Runnable myRunnable;


    String category_string="";
    int spinner_pos = 0;
    View parentLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        SharedPreferences prefs = SaveSharedPreference.getSharedPreferences(getActivity().getApplicationContext());
        username = prefs.getString(PREF_USER_NAME,"");
        user_fullname = prefs.getString(PREF_FULLNAME,"");
        user_id = prefs.getString(PREF_USER_ID,"");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.activity_create_ad,container,false);

        //Setting up toolbar
        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        if (toolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mSpinner = v.findViewById(R.id.category_spinner);
        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.description);
        expected_price = v.findViewById(R.id.expected_price);
        mCheckBox = v.findViewById(R.id.checkbox_terms);
        post_add_button = v.findViewById(R.id.post_ad_button);

        post_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(mAdapterChoosePhotos.getItemCount()>5){
                    Toast.makeText(getActivity(),"Sorry, maximum 5 images can be uploaded",Toast.LENGTH_LONG).show();
                }
                else if(mAdapterChoosePhotos.getItemCount()<1){
                    Toast.makeText(getActivity(),"Sorry, at least 1 image must be chosen.",Toast.LENGTH_LONG).show();
                }
                if(spinner_pos == 0){
                    Toast.makeText(getActivity(),"Please select Category.",Toast.LENGTH_SHORT).show();
                }
                else if(title.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Title can't be empty.", Toast.LENGTH_SHORT).show();
                }else if(title.getText().toString().length()<5){
                    Toast.makeText(getActivity(),"Title must contain atleat 5 characters.",Toast.LENGTH_LONG).show();
                }else if(description.getText().toString().length()<15){
                    Toast.makeText(getActivity(),"Description must contain atleat 15 characters.",Toast.LENGTH_LONG).show();
                }else if(description.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"Description can't be empty.",Toast.LENGTH_SHORT).show();
                }else if(expected_price.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"Expected price can't be empty.",Toast.LENGTH_SHORT).show();
                }else if(!mCheckBox.isChecked()){
                    Toast.makeText(getActivity(),"We can't proceed if you don't agree with Terms and Conditions.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    android.support.v7.app.AlertDialog.Builder builder;
                    builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                    builder.setTitle("Are you sure?")
                            .setMessage(getActivity().getResources().getString(R.string.ad_before_send))
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    post_add_button.startAnimation();
                                    post_ad();
                                }
                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    })
                            .show();
                }
            }
        });

        final String[] category = getResources().getStringArray(R.array.category);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.category, R.layout.spinner_item);

        //setting Adapter
        mSpinner.setAdapter(categoryAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                   spinner_pos = pos;
                   if(pos==0){
                       category_string="";
                   }else{
                       category_string = category[pos];
                   }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }


        });

        add_photo = v.findViewById(R.id.add_photo);
        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI); //Opening Gallery
                    startActivityForResult(i, IMG_REQUEST);
            }
        });




        //ALL of Recyclerview
        mRecyclerView = v.findViewById(R.id.recycler_choose_photos);
        mRecyclerView.setNestedScrollingEnabled(false);

        mAdapterChoosePhotos = new AdapterChoosePhotos(getActivity(),mDataRecyclers);
        linearLayoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapterChoosePhotos);


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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null){

            Uri path = data.getData();

            mBitmap = getBitmap(path);
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
            // profile.setImageBitmap(mBitmap);
        }        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                if(Build.VERSION.SDK_INT<23){
                    try {
                        mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                        DataRecycler set_data = new DataRecycler();
                        set_data.setOriginalBitmap(mBitmap);
                        set_data.setBitmap(mBitmap);
                        mDataRecyclers.add(set_data);
                        mAdapterChoosePhotos.notifyDataSetChanged();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }else{
                    try{
                        mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                        DataRecycler set_data = new DataRecycler();
                        set_data.setOriginalBitmap(mBitmap);
                        set_data.setBitmap(mBitmap);
                        mDataRecyclers.add(set_data);
                        mAdapterChoosePhotos.notifyDataSetChanged();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }

            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
    }

    private Bitmap getBitmap(Uri path) {

        Uri uri = path;
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getActivity().getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();


            int scale = 1;
            while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }

            Bitmap resultBitmap = null;
            in = getActivity().getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                options = new BitmapFactory.Options();
                options.inSampleSize = scale;
                resultBitmap = BitmapFactory.decodeStream(in, null, options);

                // resize to desired dimensions
                int height = resultBitmap.getHeight();
                int width = resultBitmap.getWidth();

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, (int) x,
                        (int) y, true);
                resultBitmap.recycle();
                resultBitmap = scaledBitmap;

                System.gc();
            } else {
                resultBitmap = BitmapFactory.decodeStream(in);
            }
            in.close();

            return resultBitmap;
        } catch (IOException e) {
            return null;
        }
    }


    private void uploadImage(final Bitmap bitmap,final String number) {

        final String des_short = description.getText().toString().substring(0,10);
        Bitmap resource = bitmap;
        resource = Bitmap.
                createScaledBitmap(resource,
                        (int)(resource.getWidth()*0.6),
                        (int)(resource.getHeight()*0.6), true);
        final String bitmap_string = imageToString(resource);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD_IMAGES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Iterator<String> keys = jsonObject.keys();
                    // get some_name_i_wont_know in str_Name
                    String str_Name=keys.next();
                    // get the value i care about
                    String value = jsonObject.getString(str_Name);
                    if(value.equals(Integer.toString(mAdapterChoosePhotos.getItemCount()-1))){

                       //dismissing dialog
                        dialog.dismiss();
                        Intent i = new Intent(getActivity(),AdvertisementSuccessful.class);
                        getActivity().startActivity(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("title",title.getText().toString().substring(0,1).toUpperCase()+
                        title.getText().toString().substring(1));
                hashMap.put("image",bitmap_string);
                hashMap.put("user_id",user_id);
                hashMap.put("description",des_short.substring(0,1).toUpperCase()+
                                des_short.substring(1));
                hashMap.put("number",number);
                return hashMap;
            }
        };
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }

    private String imageToString(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);

    }

    private void post_ad(){

        Random rnd = new Random();
        final int token = 100000 + rnd.nextInt(900000);

        mStringRequest = new StringRequest(Request.Method.POST, URL_POST_ADD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                post_add_button.revertAnimation();
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        Iterator<String> keys = jsonObject.keys();
                        // get some_name_i_wont_know in str_Name
                        String str_Name=keys.next();
                        // get the value i care about
                        String value = jsonObject.getString(str_Name);
                        if(value.equals("success")){
                            Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
                            upload_images();
                        }else{
                            Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                post_add_button.revertAnimation();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("title",title.getText().toString().substring(0,1).toUpperCase()+
                        title.getText().toString().substring(1));
                hashMap.put("description",description.getText().toString().substring(0,1).toUpperCase()+
                        description.getText().toString().substring(1));
                hashMap.put("expected_price",expected_price.getText().toString());
                hashMap.put("category",category_string);
                hashMap.put("user_id",user_id);
                hashMap.put("image_count",Integer.toString(mAdapterChoosePhotos.getItemCount()));
                hashMap.put("token",Integer.toString(token));
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                return hashMap;
            }
        };
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private  void upload_images(){

        final int delay = 60000; //milliseconds
        myRunnable = new Runnable() {
            @Override
            public void run() {
                if(dialog.isShowing()) {
                    dialog.dismiss();
                    Intent i = new Intent(getActivity(),AdvertisementSuccessful.class);
                    getActivity().startActivity(i);
                    handler.removeCallbacks(myRunnable);
                }
            }
        };
        handler.postDelayed(myRunnable, delay);

        dialog = new SpotsDialog(getActivity(),R.style.CustomLoading);
        dialog.show();
        Bitmap mbmap;
        for(int i=0;i<mAdapterChoosePhotos.getItemCount();++i){
            {
                mbmap = mAdapterChoosePhotos.getItemBitmap(i);
                uploadImage(mbmap,Integer.toString(i));
            }

        }
    }




    /*
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
    */


}
