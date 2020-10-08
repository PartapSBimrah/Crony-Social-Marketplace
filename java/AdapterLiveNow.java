package com.abhigam.www.foodspot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sourabhzalke on 13/06/17.
 */

public class AdapterLiveNow extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<DataRecycler> data = Collections.emptyList();
    DataRecycler current;
    private static final String places_key = "AIzaSyCrkw0c9C5Kd9b_gE2niYbA6I9atGQlScQ";
    private final static String URL_SEND_NOTIFICATION = "http://13.233.234.79/send_notification.php";

    private Context mContext;
    private LayoutInflater inflater;
    private StringRequest mStringRequest;
    private RequestQueue mRequestQueue;

    private String real_user__id,real_username,real_fullname;

    private static final String INTENT_FULLNAME_1 = "intent_fullname";
    private static final String INTENT_OTHER_ID_1 = "intent_other_id";
    private static final String INTENT_USERNAME_1 = "intent_username";

    //prefs
    private static final String PREF_FULLNAME = "pref_full_name";
    private static final String PREF_USER_ID = "pref_user_id";
    private static final String PREF_USER_NAME= "pref_username";

    private String CONNECT_CHAT = "http://13.233.234.79/connect_chat.php";

    // create constructor to initialize context and data sent from MainActivity
    public AdapterLiveNow(Context context, List<DataRecycler> data) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        mRequestQueue = Volley.newRequestQueue(mContext);

        //getting username and user_id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        real_user__id = prefs.getString(PREF_USER_ID,"");
        real_username = prefs.getString(PREF_USER_NAME,"");
        real_fullname = prefs.getString(PREF_FULLNAME,"");

        setHasStableIds(true);
    }

    // return total item from List
    @Override
    public int getItemCount() {

        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        View view = inflater.inflate(R.layout.container_live_now, parent, false);

        vh = new MyHolder(view);
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

            String url = "http://13.233.234.79/uploads/profile_pic/" + current.getEnrollment()
                    + "_profile.jpg";

            myHolder.image.setImageResource(R.drawable.gray);

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
                            myHolder.image.setImageBitmap(resource);
                        }
                    });

            myHolder.name.setText(current.getFirst_name());


        }
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        private CircleImageView image;
        private TextView name;
        private LinearLayout mLinearLayout;


        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            Context context = itemView.getContext();
            image =  itemView.findViewById(R.id.circular_small_profile);
            name = itemView.findViewById(R.id.name);
            mLinearLayout = itemView.findViewById(R.id.chat_linear);

            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(data.get(getAdapterPosition()).getChat().equals("1")){
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Connect to "+data.get(getAdapterPosition()).getFullname())
                                .setMessage("Do you want to start chat with "
                                        +data.get(getAdapterPosition()).getFullname()+"?")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        final ProgressDialog progress = new ProgressDialog(mContext);
                                        progress.setMessage("Connecting :) ");
                                        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        progress.setIndeterminate(true);

                                         mStringRequest= new StringRequest(Request.Method.POST,
                                                CONNECT_CHAT, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {


                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);

                                                    if(jsonObject.names().get(0).equals("success")){
                                                        progress.cancel();
                                                        send_conv_notification(getAdapterPosition());
                                                        Intent i = new Intent(mContext,EnterChatRoom.class);
                                                        i.putExtra(INTENT_FULLNAME_1,
                                                                data.get(getAdapterPosition()).getFullname());
                                                        i.putExtra(INTENT_OTHER_ID_1,
                                                                data.get(getAdapterPosition()).
                                                                        getUser_id());
                                                        i.putExtra(INTENT_USERNAME_1,data.
                                                                get(getAdapterPosition()).getEnrollment());
                                                        mContext.startActivity(i);
                                                    }else{
                                                        Toast.makeText(mContext,"Error",Toast.LENGTH_SHORT).show();
                                                        progress.cancel();
                                                    }
                                                }catch(JSONException e) {
                                                    e.printStackTrace();
                                                    progress.cancel();
                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                progress.cancel();
                                                Toast.makeText(mContext,"Error",Toast.LENGTH_SHORT).show();
                                            }
                                        }){
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                HashMap<String,String> hashMap = new HashMap<>();
                                                hashMap.put("user1",real_user__id);
                                                hashMap.put("user2",data.get(getAdapterPosition())
                                                .getUser_id());
                                                return hashMap;
                                            }
                                        };

                                        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                        mRequestQueue.add(mStringRequest);

                                    }
                                })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .show();
                    }
                    else{
                        Intent i = new Intent(mContext,EnterChatRoom.class);
                        i.putExtra(INTENT_FULLNAME_1,data.get(getAdapterPosition()).getFullname());
                        i.putExtra(INTENT_OTHER_ID_1,data.get(getAdapterPosition()).getUser_id());
                        i.putExtra(INTENT_USERNAME_1,data.get(getAdapterPosition()).getEnrollment());
                       mContext.startActivity(i);
                    }
                }
            });

        }

        private void send_conv_notification(final int position){

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
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("user_id",data.get(position).getUser_id());
                    hashMap.put("title",real_username);
                    hashMap.put("message",real_fullname+" started conversation with you.");
                    return hashMap;
                }
            };

            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mRequestQueue.add(mStringRequest);
        }


    }




}

