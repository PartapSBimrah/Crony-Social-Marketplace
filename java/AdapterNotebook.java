package com.abhigam.www.foodspot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sourabhzalke on 13/06/17.
 */

public class AdapterNotebook extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private RequestQueue mRequestQueue;
    private StringRequest request;
    long timeInMillis;
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
    public String user_check_id;

    private static final String USERNAME_VISITOR = "visitor_username";
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
    private static final String INTENT_OPEN_COMMENT = "com.abhigam.www.foodspot.OPEN_COMMENT";
    private static final String INTENT_IS_VERIFIED = "com.abhigam.www.foodspot.IS_VERIFIED";
    private static final String INTENT_IS_SAVED = "com.abhigam.www.foodspot.IS_SAVED";

    private static final String URL_REMOVE_FROM_NOTEBOOK = "http://13.233.234.79/remove_from_notebook.php";

    List<DataRecycler> data = Collections.emptyList();
    DataRecycler current;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private Context mContext;
    private LayoutInflater inflater;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterNotebook(Context context, List<DataRecycler> data) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        mRequestQueue = Volley.newRequestQueue(context);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        user_check_id = preferences.getString(PREF_USER_ID, "");
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
            View view = inflater.inflate(R.layout.container_notebook_posts, parent, false);

            vh = new MyHolder(view);
        /*else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        */
        return vh;
    }


    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder) {
            // Get current position of item in RecyclerView to bind data and assign values from list
            final MyHolder myHolder = (MyHolder) holder;
            DataRecycler current = data.get(position);
            //Instantiate the RequestQueue

            if(!current.getCaption().equals("")) {
                myHolder.caption.setText(current.getCaption());
                myHolder.caption.setVisibility(View.VISIBLE);
            }

           String url = "http://13.233.234.79/uploads/profile_pic/" + current.getEnrollment() + "_profile.jpg";


            final String URL = "http://13.233.234.79/uploads/post_images/"
                    +current.getUser_id()
                    +"_"+current.getTime_ago()+".jpg";

            myHolder.mPostImage.setImageResource(0);

            if(current.getIs_image().equals("0")){
                myHolder.mPostImage.setVisibility(View.GONE);
            }else{
                myHolder.mPostImage.setVisibility(View.VISIBLE);
            }

            //Getting the post image
            Glide.with(mContext)
                    .load(URL)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(100, 100) { //width and height
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            myHolder.mPostImage.setImageBitmap(resource);
                        }
                    });

            myHolder.mCircleImageView.setImageResource(0);
            //Getting user profile
            Glide
                    .with(mContext)
                    .load(url)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                    .skipMemoryCache(true)
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>(100, 100) { //width and height
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            myHolder.mCircleImageView.setImageBitmap(resource);
                        }
                    });

            myHolder.fullname.setText(current.getFullname());



            long time = Long.parseLong(current.getTime_ago());
            long now = System.currentTimeMillis();

            CharSequence min = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS);
            myHolder.time_ago.setText(min);

        }
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        private TextView time_ago,caption,fullname;
        private ImageView mPostImage;
        private ImageView popup;
        private CircleImageView mCircleImageView;
        private Context context;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
             context= itemView.getContext();

            time_ago = itemView.findViewById(R.id.time_ago);
            caption = itemView.findViewById(R.id.title);
            mPostImage = itemView.findViewById(R.id.post_image);
            mCircleImageView = itemView.findViewById(R.id.user_profile);
            fullname = itemView.findViewById(R.id.fullname);
            popup = itemView.findViewById(R.id.popup);

            popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(context, v);
                    menu.getMenu().add("Remove");
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getTitle().equals("Remove")){
                                AlertDialog.Builder builder;
                                builder = new AlertDialog.Builder(context);
                                builder.setTitle("Remove post")
                                        .setMessage("Are you sure you want to remove this post?")
                                        .setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                remove_from_notebook(user_check_id,data.get(getAdapterPosition()).getPost_id(),
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


            mPostImage.setOnClickListener(new View.OnClickListener() {
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
                    i.putExtra(INTENT_IS_SAVED,"1");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    i.putExtra(INTENT_OPEN_COMMENT,"0");
                    context.startActivity(i);
                }
            });

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
                    i.putExtra(INTENT_IS_SAVED,"1");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    i.putExtra(INTENT_OPEN_COMMENT,"0");
                    context.startActivity(i);
                }
            });

            mCircleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, VisitorProfile.class);
                    i.putExtra(USERNAME_VISITOR, data.get(getAdapterPosition()).getEnrollment());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                    context.startActivity(i);
                }
            });

            fullname.setOnClickListener(new View.OnClickListener() {
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

    public void remove_from_notebook(final String user_id,final String post_id,final int position){

        request = new StringRequest(Request.Method.POST,URL_REMOVE_FROM_NOTEBOOK , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        data.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                        if(mContext instanceof Notebook){
                            ((Notebook)mContext).activityOnRemoved();
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext,"Network connection error",Toast.LENGTH_SHORT).show();
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

    /*
    public  class ProgressViewHolder extends RecyclerView.ViewHolder {
        public AVLoadingIndicatorView progressBar;

        public ProgressViewHolder(View v){
            super(v);
            progressBar = (AVLoadingIndicatorView) v.findViewById(R.id.progressBar1);
        }
    }
*/

}
