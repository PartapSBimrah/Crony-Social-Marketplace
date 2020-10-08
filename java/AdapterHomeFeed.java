package com.abhigam.www.foodspot;

import android.*;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.Zoomy;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterHomeFeed extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private RequestQueue mRequestQueue;
    private StringRequest request;
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
    public String user_check_id,username,user_fullname;

    //intent_tags
    private static final String INTENT_POST_ID = "com.abhigam.www.foodspot.POST_ID";
    private static final String INTENT_USER_ID = "com.abhigam.www.foodspot.USER_ID";
    private static final String INTENT_ENROLLMENT = "com.abhigam.www.foodspot.ENROLLMENT";
    private static final String INTENT_FULLNAME = "com.abhigam.www.foodspot.FULLNAME";
    private static final String INTENT_TIMEAGO = "com.abhigam.www.foodspot.TIMEAGO";
    private static final String INTENT_CAPTION = "com.abhigam.www.foodspot.CAPTION";
    private static final String INTENT_IS_LIKED = "com.abhigam.www.foodspot.IS_LIKED";
    private static final String INTENT_VIEWS_NUMBER = "com.abhigam.www.foodspot.VIEWS_NUMBER";
    private static final String INTENT_LIKES_NUMBER = "com.abhigam.www.foodspot.LIKES_NUMBER";
    private static final String INTENT_IS_IMAGE = "com.abhigam.www.foodspot.IS_IMAGE";
    private static final String INTENT_IS_VERIFIED = "com.abhigam.www.foodspot.IS_VERIFIED";
    private static final String INTENT_IS_SAVED = "com.abhigam.www.foodspot.IS_SAVED";


    private static final String INTENT_OPEN_COMMENT = "com.abhigam.www.foodspot.OPEN_COMMENT";
    private static final String URL_LIKED_NOTIFICATION = "http://13.233.234.79/liked_notification_inside.php";
    private final static String URL_SEND_NOTIFICATION = "http://13.233.234.79/send_notification.php";

    private static final String URL_REMOVE_FROM_NOTEBOOK = "http://13.233.234.79/remove_from_notebook.php";

    List<DataRecycler> data = Collections.emptyList();
    DataRecycler current;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private static final String USERNAME_VISITOR = "visitor_username";

    private FragmentActivity mContext;
    private LayoutInflater inflater;
    private  Context context;

    private String URL_ADD_TO_NOTEBOOK = "http://13.233.234.79/add_to_notebook.php";
    private String URL_ADD_TO_LIKED = "http://13.233.234.79/add_to_liked.php";
    private String URL_REMOVE_FROM_LIKED = "http://13.233.234.79/remove_from_liked.php";
    private String URL_DELETE_POST = "http://13.233.234.79/delete_posts.php";

    private static char[] c = new char[]{'k', 'm', 'b', 't'};
    long y=0,z=0;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterHomeFeed(FragmentActivity context, List<DataRecycler> data) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        mRequestQueue = Volley.newRequestQueue(context);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        user_check_id = preferences.getString(PREF_USER_ID, "");
        username = preferences.getString(PREF_USER_NAME,"");
        user_fullname = preferences.getString(PREF_FULLNAME,"");
        setHasStableIds(true);
    }

    // return total item from List
    @Override
    public int getItemCount() {

        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View view = inflater.inflate(R.layout.container_home_feed, parent, false);

            vh = new MyHolder(view);
        }else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }


    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder) {
            // Get current position of item in RecyclerView to bind data and assign values from list
            final MyHolder myHolder = (MyHolder) holder;
            final DataRecycler current = data.get(position);
            //Instantiate the RequestQueue

            if(current.getFirst_load().equals("1")) {
                myHolder.fullname.setText(current.getFullname());

                if(!current.getCaption().equals("")){
                    myHolder.caption.setText(current.getCaption());
                    myHolder.caption.setVisibility(View.VISIBLE);
                }else{
                    myHolder.caption.setVisibility(View.GONE);
                }

                myHolder.caption.post(new Runnable() {

                    @Override
                    public void run() {
                        if(myHolder.caption.getLineCount()>=7 && myHolder.caption.getLineCount()==0){
                            myHolder.read_more.setVisibility(View.VISIBLE);
                        }else{
                            myHolder.read_more.setVisibility(View.GONE);
                        }
                    }
                });

                String url = "http://13.233.234.79/uploads/profile_pic/" + current.getEnrollment() + "_profile.jpg";

                myHolder.user_profile.setImageResource(R.drawable.gray);
                //Getting user profile
                Glide
                        .with(context)
                        .load(url)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                        .skipMemoryCache(true)
                        .centerCrop()
                        .into(new SimpleTarget<Bitmap>(100, 100) { //width and height
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                myHolder.user_profile.setImageBitmap(resource);
                            }
                        });

                if (current.is_image.equals("1")) {
                    myHolder.mPostImage.setBackground(context.getResources().getDrawable(R.drawable.dark_gradient));
                    myHolder.mPostImage.setVisibility(View.VISIBLE);
                }else{
                    myHolder.mPostImage.setVisibility(View.GONE);
                    if(myHolder.caption.length()<40)
                        myHolder.caption.setTextSize((float)18);
                    else
                    myHolder.caption.setTextSize((float)16);
                }

                if(current.verified.equals("1")){
                    myHolder.verified.setVisibility(View.VISIBLE);
                }else{
                    myHolder.verified.setVisibility(View.GONE);
                }

                final String URL = "http://13.233.234.79/uploads/post_images/"
                        + current.getUser_id()
                        + "_" + current.getTime_ago() + ".jpg";

                myHolder.mPostImage.setImageResource(R.drawable.gray);

                Glide
                        .with(context)
                        .load(URL)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(myHolder.mPostImage.getMaxWidth(),
                                myHolder.mPostImage.getMaxHeight()) { //width and height
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                myHolder.mPostImage.setImageBitmap(resource);
                                current.setBitmap(resource);
                            }
                        });
                /*Getting the post image
                Glide.with(context)
                        .load(URL)
                        .into(myHolder.mPostImage);
                        */


                if (Integer.parseInt(current.getIs_liked()) == 0) {
                    myHolder.image_like.setColorFilter(ContextCompat.getColor(context, R.color.gray),
                            PorterDuff.Mode.SRC_IN);
                    myHolder.text_like.setTextColor(context.getResources().getColor(R.color.gray));
                    myHolder.text_like.setText("Like");
                } else {
                    myHolder.image_like.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    myHolder.text_like.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    myHolder.text_like.setText("Liked");
                }

                if (Integer.parseInt(current.getIs_saved()) == 0) {
                    myHolder.love_lined.setVisibility(View.VISIBLE);
                    myHolder.love_filled.setVisibility(View.GONE);
                    myHolder.saved_text.setTextColor(context.getResources().getColor(R.color.gray));
                    myHolder.saved_text.setText("Save");
                } else {
                    myHolder.love_filled.setVisibility(View.VISIBLE);
                    myHolder.love_lined.setVisibility(View.GONE);
                    myHolder.saved_text.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    myHolder.saved_text.setText("Saved");
                }

                myHolder.likes_number.setText(current.getLikes_number() + " likes ∙ ");
                y = Long.parseLong(current.getViews_number());
                if(y>1000){
                    String views_cool = coolFormat(y,0);
                    myHolder.views_number.setText(views_cool+" views");
                }else {
                    myHolder.views_number.setText(current.getViews_number() + " views");
                }
                myHolder.comments_number.setText(current.getComments_number()+" comments ∙");


                long time = Long.parseLong(current.getTime_ago());
                long now = System.currentTimeMillis();

                CharSequence min = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS);
                if(min.equals("0 seconds ago"))
                    myHolder.time_ago.setText(context.getResources().getString(R.string.just_now));
                else
                    myHolder.time_ago.setText(min);


            }else {

               if(current.getIs_liked().equals("1")) {
                   myHolder.image_like.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary),
                           android.graphics.PorterDuff.Mode.SRC_IN);
                   myHolder.text_like.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                   myHolder.text_like.setText("Liked");
                   myHolder.likes_number.setText(current.getLikes_number() + " likes ∙ ");
               }else{
                   myHolder.image_like.setColorFilter(ContextCompat.getColor(context, R.color.gray),
                           android.graphics.PorterDuff.Mode.SRC_IN);
                   myHolder.text_like.setTextColor(context.getResources().getColor(R.color.gray));
                   myHolder.text_like.setText("Like");
                   myHolder.likes_number.setText(current.getLikes_number() + " likes ∙ ");
               }

                if (Integer.parseInt(current.getIs_saved()) == 0) {
                    myHolder.love_lined.setVisibility(View.VISIBLE);
                    myHolder.love_filled.setVisibility(View.GONE);
                    myHolder.saved_text.setTextColor(context.getResources().getColor(R.color.gray));
                    myHolder.saved_text.setText("Save");
                } else {
                    myHolder.love_filled.setVisibility(View.VISIBLE);
                    myHolder.love_lined.setVisibility(View.GONE);
                    myHolder.saved_text.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    myHolder.saved_text.setText("Saved");
                }

            }


        }
    }



    public class MyHolder extends RecyclerView.ViewHolder {

        private TextView fullname,time_ago,caption,
                text_like,likes_number,comments_number,views_number,saved_text,read_more;
        private CircleImageView user_profile;
        private ImageView mPostImage,image_like,popup,verified,love_lined,love_filled;
        private LinearLayout like_it,comment_it,save_it,linear_user;

        Zoomy.Builder builder = new Zoomy.Builder(mContext);


        //create constructor to get widget reference
        public MyHolder(final View itemView) {
            super(itemView);

            context = itemView.getContext();

            fullname =itemView.findViewById(R.id.fullname);
            time_ago = itemView.findViewById(R.id.time_ago);
            caption =  itemView.findViewById(R.id.caption);
            user_profile = itemView.findViewById(R.id.circular_profile);
            mPostImage = itemView.findViewById(R.id.post_image);
            like_it = itemView.findViewById(R.id.like_it);
            image_like = itemView.findViewById(R.id.image_like);
            text_like = itemView.findViewById(R.id.text_like);
            likes_number = itemView.findViewById(R.id.likes_number);
            views_number = itemView.findViewById(R.id.views_number);
            comments_number = itemView.findViewById(R.id.num_comments);
            comment_it = itemView.findViewById(R.id.comment_it);
            linear_user = itemView.findViewById(R.id.linear_user);
            popup = itemView.findViewById(R.id.popup_post);
            verified = itemView.findViewById(R.id.ic_verified);
            love_filled = itemView.findViewById(R.id.love_filled);
            love_lined = itemView.findViewById(R.id.love_lined);
            saved_text = itemView.findViewById(R.id.save_text);
            read_more = itemView.findViewById(R.id.read_more);

            caption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, PostDetails.class);
                    i.putExtra(INTENT_POST_ID,data.get(getAdapterPosition()).getPost_id());
                    i.putExtra(INTENT_ENROLLMENT,data.get(getAdapterPosition()).getEnrollment());
                    i.putExtra(INTENT_USER_ID,data.get(getAdapterPosition()).getUser_id());
                    i.putExtra(INTENT_FULLNAME,data.get(getAdapterPosition()).getFullname());
                    i.putExtra(INTENT_TIMEAGO,data.get(getAdapterPosition()).getTime_ago());
                    i.putExtra(INTENT_CAPTION,data.get(getAdapterPosition()).getCaption());
                    i.putExtra(INTENT_IS_LIKED,data.get(getAdapterPosition()).getIs_liked());
                    i.putExtra(INTENT_VIEWS_NUMBER,data.get(getAdapterPosition()).getViews_number());
                    i.putExtra(INTENT_LIKES_NUMBER,data.get(getAdapterPosition()).getLikes_number());
                    i.putExtra(INTENT_IS_IMAGE,data.get(getAdapterPosition()).getIs_image());
                    i.putExtra(INTENT_IS_VERIFIED,data.get(getAdapterPosition()).getVerified());
                    i.putExtra(INTENT_IS_SAVED,data.get(getAdapterPosition()).getIs_saved());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    i.putExtra(INTENT_OPEN_COMMENT,"0");
                    context.startActivity(i);
                }
            });

            read_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, PostDetails.class);
                    i.putExtra(INTENT_POST_ID,data.get(getAdapterPosition()).getPost_id());
                    i.putExtra(INTENT_ENROLLMENT,data.get(getAdapterPosition()).getEnrollment());
                    i.putExtra(INTENT_USER_ID,data.get(getAdapterPosition()).getUser_id());
                    i.putExtra(INTENT_FULLNAME,data.get(getAdapterPosition()).getFullname());
                    i.putExtra(INTENT_TIMEAGO,data.get(getAdapterPosition()).getTime_ago());
                    i.putExtra(INTENT_CAPTION,data.get(getAdapterPosition()).getCaption());
                    i.putExtra(INTENT_IS_LIKED,data.get(getAdapterPosition()).getIs_liked());
                    i.putExtra(INTENT_VIEWS_NUMBER,data.get(getAdapterPosition()).getViews_number());
                    i.putExtra(INTENT_LIKES_NUMBER,data.get(getAdapterPosition()).getLikes_number());
                    i.putExtra(INTENT_IS_IMAGE,data.get(getAdapterPosition()).getIs_image());
                    i.putExtra(INTENT_IS_VERIFIED,data.get(getAdapterPosition()).getVerified());
                    i.putExtra(INTENT_IS_SAVED,data.get(getAdapterPosition()).getIs_saved());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    i.putExtra(INTENT_OPEN_COMMENT,"0");
                    context.startActivity(i);
                }
            });

            builder.target(mPostImage).tapListener(new TapListener() {
                @Override
                public void onTap(View v) {
                    Intent i = new Intent(context, PostDetails.class);
                    i.putExtra(INTENT_POST_ID,data.get(getAdapterPosition()).getPost_id());
                    i.putExtra(INTENT_ENROLLMENT,data.get(getAdapterPosition()).getEnrollment());
                    i.putExtra(INTENT_USER_ID,data.get(getAdapterPosition()).getUser_id());
                    i.putExtra(INTENT_FULLNAME,data.get(getAdapterPosition()).getFullname());
                    i.putExtra(INTENT_TIMEAGO,data.get(getAdapterPosition()).getTime_ago());
                    i.putExtra(INTENT_CAPTION,data.get(getAdapterPosition()).getCaption());
                    i.putExtra(INTENT_IS_LIKED,data.get(getAdapterPosition()).getIs_liked());
                    i.putExtra(INTENT_VIEWS_NUMBER,data.get(getAdapterPosition()).getViews_number());
                    i.putExtra(INTENT_LIKES_NUMBER,data.get(getAdapterPosition()).getLikes_number());
                    i.putExtra(INTENT_IS_IMAGE,data.get(getAdapterPosition()).getIs_image());
                    i.putExtra(INTENT_IS_VERIFIED,data.get(getAdapterPosition()).getVerified());
                    i.putExtra(INTENT_IS_SAVED,data.get(getAdapterPosition()).getIs_saved());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    i.putExtra(INTENT_OPEN_COMMENT,"0");
                    context.startActivity(i);
                }
            })
            .register();



            popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(context, v);
                    menu.getMenu().add("Share");
                    if(data.get(getAdapterPosition()).getUser_id().equals(user_check_id))
                    menu.getMenu().add("Delete");
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getTitle().equals("Report")){

                            }else if(item.getTitle().equals("Share")){
                                if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    if(data.get(getAdapterPosition()).getIs_image().equals("1")) {
                                        Uri share_uri = getImageUri(context,data.get(getAdapterPosition()).getBitmap());
                                        String text_share = data.get(getAdapterPosition()).getCaption() +
                                                "\n- " + data.get(getAdapterPosition()).getFullname() +
                                                " For more visit: www.crony.co.in";
                                        Intent shareIntent = new Intent();
                                        shareIntent.setAction(Intent.ACTION_SEND);
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, text_share);
                                        shareIntent.putExtra(Intent.EXTRA_STREAM, share_uri);
                                        shareIntent.setType("image/jpg");
                                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        context.startActivity(Intent.createChooser(shareIntent, "Share"));


                                    }else{
                                        String text_share = data.get(getAdapterPosition()).getCaption() +
                                                "\n- "+data.get(getAdapterPosition()).getFullname()+
                                                "\n For more visit: www.crony.co.in";
                                        Intent shareIntent = new Intent();
                                        shareIntent.setAction(Intent.ACTION_SEND);
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, text_share);
                                        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Crony Post");
                                        shareIntent.setType("text/plain");
                                        context.startActivity(Intent.createChooser(shareIntent, "Share"));
                                    }

                                }else {

                                    AlertDialog.Builder builder;
                                    builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Permission Required")
                                            .setMessage("To use this feature we require permission to write in your " +
                                                    "External storage. Go to settings to change it.")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();
                                }
                            }else{

                                AlertDialog.Builder builder;
                                builder = new AlertDialog.Builder(context);
                                builder.setTitle("Delete post")
                                        .setMessage("Are you sure you want to delete this post?")
                                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                delete_post(data.get(getAdapterPosition()).getPost_id(),
                                                        getAdapterPosition());
                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                            }
                                        })
                                        .show();
                            }
                            return true;
                        }
                    });
                    menu.show();
                }
            });

            save_it = itemView.findViewById(R.id.save_it);

            comment_it.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,PostDetails.class);
                    i.putExtra(INTENT_POST_ID,data.get(getAdapterPosition()).getPost_id());
                    i.putExtra(INTENT_ENROLLMENT,data.get(getAdapterPosition()).getEnrollment());
                    i.putExtra(INTENT_USER_ID,data.get(getAdapterPosition()).getUser_id());
                    i.putExtra(INTENT_FULLNAME,data.get(getAdapterPosition()).getFullname());
                    i.putExtra(INTENT_TIMEAGO,data.get(getAdapterPosition()).getTime_ago());
                    i.putExtra(INTENT_CAPTION,data.get(getAdapterPosition()).getCaption());
                    i.putExtra(INTENT_IS_LIKED,data.get(getAdapterPosition()).getIs_liked());
                    i.putExtra(INTENT_VIEWS_NUMBER,data.get(getAdapterPosition()).getViews_number());
                    i.putExtra(INTENT_LIKES_NUMBER,data.get(getAdapterPosition()).getLikes_number());
                    i.putExtra(INTENT_IS_IMAGE,data.get(getAdapterPosition()).getIs_image());
                    i.putExtra(INTENT_IS_VERIFIED,data.get(getAdapterPosition()).getVerified());
                    i.putExtra(INTENT_IS_SAVED,data.get(getAdapterPosition()).getIs_saved());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    i.putExtra(INTENT_OPEN_COMMENT,"1");
                    context.startActivity(i);
                }
            });

            comments_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,PostDetails.class);
                    i.putExtra(INTENT_POST_ID,data.get(getAdapterPosition()).getPost_id());
                    i.putExtra(INTENT_ENROLLMENT,data.get(getAdapterPosition()).getEnrollment());
                    i.putExtra(INTENT_USER_ID,data.get(getAdapterPosition()).getUser_id());
                    i.putExtra(INTENT_FULLNAME,data.get(getAdapterPosition()).getFullname());
                    i.putExtra(INTENT_TIMEAGO,data.get(getAdapterPosition()).getTime_ago());
                    i.putExtra(INTENT_CAPTION,data.get(getAdapterPosition()).getCaption());
                    i.putExtra(INTENT_IS_LIKED,data.get(getAdapterPosition()).getIs_liked());
                    i.putExtra(INTENT_VIEWS_NUMBER,data.get(getAdapterPosition()).getViews_number());
                    i.putExtra(INTENT_LIKES_NUMBER,data.get(getAdapterPosition()).getLikes_number());
                    i.putExtra(INTENT_IS_IMAGE,data.get(getAdapterPosition()).getIs_image());
                    i.putExtra(INTENT_IS_VERIFIED,data.get(getAdapterPosition()).getVerified());
                    i.putExtra(INTENT_IS_SAVED,data.get(getAdapterPosition()).getIs_saved());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    i.putExtra(INTENT_OPEN_COMMENT,"0");
                    context.startActivity(i);
                }
            });

            likes_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!likes_number.getText().toString().equals("0 likes")){
                        Intent i = new Intent(context, LikedUsers.class);
                        i.putExtra(INTENT_POST_ID, data.get(getAdapterPosition()).getPost_id());
                        context.startActivity(i);
                    }
                }
            });


            like_it.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Integer.parseInt(data.get(getAdapterPosition()).getIs_liked())==0) {
                        add_to_liked(user_check_id, data.get(getAdapterPosition()).getPost_id(),getAdapterPosition());
                    }else{
                        remove_from_liked(user_check_id, data.get(getAdapterPosition()).getPost_id(),getAdapterPosition());
                    }
                }
            });

            save_it.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Integer.parseInt(data.get(getAdapterPosition()).getIs_saved())==0) {
                        add_to_notebook(user_check_id, data.get(getAdapterPosition()).getPost_id(),getAdapterPosition());
                    }else{
                        remove_from_notebook(user_check_id,data.get(getAdapterPosition()).getPost_id(),getAdapterPosition());
                    }
                }
            });


            user_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, VisitorProfile.class);
                    i.putExtra(USERNAME_VISITOR, data.get(getAdapterPosition()).getEnrollment());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    context.startActivity(i);
                }
            });
            linear_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, VisitorProfile.class);
                    i.putExtra(USERNAME_VISITOR, data.get(getAdapterPosition()).getEnrollment());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    context.startActivity(i);
                }
            });

        }

    }
    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, UUID.randomUUID().toString() + ".jpg", "drawing");
        return Uri.parse(path);
    }


    public  class ProgressViewHolder extends RecyclerView.ViewHolder {
            public ProgressBar progressBar;

            public ProgressViewHolder(View v) {
                super(v);
                progressBar = v.findViewById(R.id.progressBar1);
            }
        }



    private void add_to_liked(final String user_id,final String post_id,final int position){

        request = new StringRequest(Request.Method.POST, URL_ADD_TO_LIKED, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success") && data.get(position).getIs_liked().equals("0")){
                            data.get(position).setIs_liked("1");
                            data.get(position).setLikes_number(Integer.toString(Integer.parseInt(
                                    data.get(position).getLikes_number()
                            ) + 1));

                        if(!username.equals(data.get(position).getEnrollment())) {
                            send_liked_notification_inside(position);
                            send_notification(position);
                        }

                        data.get(position).setFirst_load("0");
                        notifyItemChanged(position);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                   Toast.makeText(context,"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("post_id",post_id);
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);

    }


    private static String coolFormat(double n, int iteration) {
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : coolFormat(d, iteration+1));

    }

    private void remove_from_liked(final String user_id,final String post_id,final int position){

        request = new StringRequest(Request.Method.POST, URL_REMOVE_FROM_LIKED, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success") && data.get(position).getIs_liked().equals("1")){
                        data.get(position).setIs_liked("0");
                        data.get(position).setLikes_number(Integer.toString(Integer.parseInt(
                                data.get(position).getLikes_number()
                        ) - 1));

                        data.get(position).setFirst_load("0");
                        notifyItemChanged(position);
                        }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("post_id",post_id);
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);

    }

    private void delete_post(final String post_id,final int position){

        request = new StringRequest(Request.Method.POST, URL_DELETE_POST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        data.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                        Toast.makeText(context,"Post deleted",Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("post_id",post_id);
                hashMap.put("user_id",user_check_id);
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);

    }



        private void add_to_notebook(final String user_id,final String post_id,final int position){

        request = new StringRequest(Request.Method.POST, URL_ADD_TO_NOTEBOOK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success") && data.get(position).getIs_saved().equals("0")){
                        data.get(position).setIs_saved("1");

                        data.get(position).setFirst_load("0");
                        notifyItemChanged(position);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("post_id",post_id);
                return hashMap;
            }
        };

            request.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);

        }

    private void remove_from_notebook(final String user_id,final String post_id,final int position){

        request = new StringRequest(Request.Method.POST,URL_REMOVE_FROM_NOTEBOOK , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success") && data.get(position).getIs_saved().equals("1")){
                        data.get(position).setIs_saved("0");
                        data.get(position).setFirst_load("0");
                        notifyItemChanged(position);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Network connection error",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                hashMap.put("post_id",post_id);
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);

    }


    private void send_liked_notification_inside(final int position){

        request  = new StringRequest(Request.Method.POST, URL_LIKED_NOTIFICATION,
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
                hashMap.put("user_id",data.get(position).getUser_id());
                hashMap.put("sender_id",user_check_id);
                hashMap.put("thing",data.get(position).getCaption());
                hashMap.put("time_ago",Long.toString(Calendar.getInstance().getTimeInMillis()));
                hashMap.put("post_id",data.get(position).getPost_id());
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    private void send_notification(final int position){

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
                hashMap.put("user_id",data.get(position).getUser_id());
                hashMap.put("title",username);
                hashMap.put("message",user_fullname+" has liked your post.");
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }




}
