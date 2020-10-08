package com.abhigam.www.foodspot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class EnterChatRoomFragment extends Fragment{



    MessageInput mMessageInput;
    MessagesList mMessagesList;

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

    private static final String USERNAME_VISITOR = "visitor_username";
    private String is_online_user;


    private final static String URL_SEND_TEXT = "http://13.233.234.79/submit.php";
    private final static String URL_UPDATE_HIGHLIGHT = "http://13.233.234.79/highlight.php";


    private final static String URL_REFRESH_CHATROOM = "http://13.233.234.79/refresh.php";

    private final static String URL_NO_ONLINE = "http://13.233.234.79/no_online.php";

    private final static String URL_REFRESH_ONLINE = "http://13.233.234.79/refresh_online.php";
    private final static String URL_LOAD_MORE = "http://13.233.234.79/load_more_chat.php";

    private static final String INTENT_FULLNAME = "intent_fullname";
    private static final String INTENT_OTHER_ID = "intent_other_id";
    private static final String INTENT_USERNAME = "intent_username";

    private static final String URL_REMOVE_CHAT = "http://13.233.234.79/remove_chat.php";

    private final static String URL_SEND_NOTIFICATION = "http://13.233.234.79/send_notification.php";

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    private TextView chatname;
    private String other_fullname,the_other_id,other_username,seen;
    private LinearLayout progressBar;
    private ScrollView mScrollView;

    String username,temp_user1,temp_user2,temp_sender,user_id
    ,inputEditText,full_name;

    MessagesListAdapter<Message> adapter = new MessagesListAdapter<>(user_id, null);


    ImageLoader mImageLoader;
    private LinearLayout mMoreVertLinear;


    private int lastTimeID = 0;
    private ImageView mBack;

    Handler handler = new Handler();
    Runnable myRunnable;

    Handler handler1 = new Handler();
    Runnable myRunnable1;

    private String count;

    private TextView is_online;

    private  RelativeLayout mRelativeLayout;
    private CircleImageView mCircleImageView;
    private int page=0,totalItems =0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getting username and user_id
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        username = prefs.getString(PREF_USER_NAME,"");
        user_id = prefs.getString(PREF_USER_ID,"");
        full_name= prefs.getString(PREF_FULLNAME,"");

        temp_user1 = user_id;

        mImageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Glide.with(getActivity()).load(url).into(imageView);
            }
        };

        MessageHolders holdersConfig = new MessageHolders();
        holdersConfig.setOutcomingTextConfig(CustomOutcomingMessageViewHolder.class,
                R.layout.item_custom_outcoming_message);
        adapter = new MessagesListAdapter<>(user_id, holdersConfig, mImageLoader);
        // adapter = new MessagesListAdapter<Message>(user_id,mImageLoader);

        adapter.registerViewClickListener(R.id.messageUserAvatar, new MessagesListAdapter.OnMessageViewClickListener<Message>() {
            @Override
            public void onMessageViewClick(View view, Message message) {
                Intent i = new Intent(getActivity(), VisitorProfile.class);
                i.putExtra(USERNAME_VISITOR, other_username);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                getActivity().startActivity(i);
            }
        });

        adapter.setLoadMoreListener(new MessagesListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if(totalItemsCount>25)
                load_more();
            }
        });


        mRequestQueue = Volley.newRequestQueue(getActivity());
        //get_online();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.activity_enter_chat_room,container,false);

        Window window = getActivity().getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        }

        other_fullname = getActivity().getIntent().getStringExtra(INTENT_FULLNAME);
        the_other_id = getActivity().getIntent().getStringExtra(INTENT_OTHER_ID);
        other_username = getActivity().getIntent().getStringExtra(INTENT_USERNAME);
        is_online = v.findViewById(R.id.time);

        mRelativeLayout = v.findViewById(R.id.root);

        chatname = v.findViewById(R.id.chatname);
        chatname.setText(other_fullname);

        mCircleImageView = v.findViewById(R.id.circular_user);
        String url = "http://13.233.234.79/uploads/profile_pic/" + other_username + "_profile.jpg";

        mCircleImageView.setImageResource(R.drawable.gray);

        Glide
                .with(getActivity())
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                .skipMemoryCache(true)
                .centerCrop()
                .into(new SimpleTarget<Bitmap>(100, 100) { //width and height
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        mCircleImageView.setImageBitmap(resource);
                    }
                });

        mMessageInput = v.findViewById(R.id.input);
        mMessagesList = v.findViewById(R.id.messagesList);

        progressBar = v.findViewById(R.id.progress_bar);
        mScrollView = v.findViewById(R.id.scrollView);
        mMessagesList.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);

        mMoreVertLinear = v.findViewById(R.id.more_vert_linear);

        mMoreVertLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(getActivity(), v);
                    menu.getMenu().add("View profile");
                    menu.getMenu().add("Disconnect");
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Disconnect")) {
                            remove_chat();
                        }else if(item.getTitle().equals("View profile")){
                            Intent i = new Intent(getActivity(), VisitorProfile.class);
                            i.putExtra(USERNAME_VISITOR, other_username);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //was an error runtime
                            getActivity().startActivity(i);
                        }
                        return true;
                    }
                });
                menu.show();
            }
        });
        mBack = v.findViewById(R.id.back_arrow_image);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mMessageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                //validate and send message
                inputEditText = mMessageInput.getInputEditText().getText().toString();
                sendChatText();
                return true;
            }
        });

        final int delay = 2000; //milliseconds
        myRunnable = new Runnable() {
            @Override
            public void run() {
                refreshChatRoom();
                handler.postDelayed(this, delay);
            }
        };
        handler.postDelayed(myRunnable, delay);

        //refresh online
        final int delay1 = 60000; //milliseconds
        myRunnable1 = new Runnable() {
            @Override
            public void run() {
                refresh_online();
                handler1.postDelayed(this, delay1);
            }
        };
        handler1.postDelayed(myRunnable1, delay);

        update_highlight();

        return v;
    }



    private void refreshChatRoom(){

        final ArrayList<Message> listMessages = new ArrayList<>();


        mStringRequest = new StringRequest(Request.Method.POST, URL_REFRESH_CHATROOM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){

                String image = other_username;

                progressBar.setVisibility(View.GONE);
                mScrollView.setVisibility(View.VISIBLE);

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jArray = jsonResponse.getJSONArray("results");

                    if (lastTimeID == 0){
                        for (int i = jArray.length()-1; i >=0; i--) {
                            JSONObject data = jArray.getJSONObject(i);
                            temp_user1 = data.getString("user1");
                            temp_user2 = data.getString("user2");
                            temp_sender = data.getString("sender");
                            inputEditText = data.getString("chattext");
                            seen = data.getString("seen");
                            Calendar cal = Calendar.getInstance();
                            String  s = getDate(Long.parseLong(data.getString("time")),
                                    "EEE MMM dd HH:mm:ss z yyyy");
                            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                            Date date1 = format.parse(s);
                            cal.setTime(date1);
                            Date date = cal.getTime();
                            //the id here is the author's first initialisation
                            Author author1 = new Author(temp_sender,image);

                            //i_status = 2 for not showing sent or not sent
                                Message message1 = new Message(temp_sender,inputEditText,author1,
                                        date,2,null,seen);
                                listMessages.add(message1);

                            if(i == 0){
                                lastTimeID = Integer.parseInt(data.getString("id"));
                            }
                            if(i == jArray.length()-1){
                                count = data.getString("id");
                            }
                        }
                        if(jArray.length()!=0)
                        adapter.addToEnd(listMessages, true);
                        totalItems = totalItems + jArray.length();
                }else{
                        for (int i = jArray.length()-1; i >=0; i--) {
                            JSONObject data = jArray.getJSONObject(i);
                            temp_user1 = data.getString("user1");
                            temp_user2 = data.getString("user2");
                            temp_sender = data.getString("sender");
                            inputEditText = data.getString("chattext");
                            seen = data.getString("seen");
                            Calendar cal = Calendar.getInstance();
                            String  s = getDate(Long.parseLong(data.getString("time")),
                                    "EEE MMM dd HH:mm:ss z yyyy");
                            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                            Date date1 = format.parse(s);
                            cal.setTime(date1);
                            Date date = cal.getTime();
                            Author author2 = new Author(temp_sender,image);
                            Message message2 = new Message(temp_sender,inputEditText,author2,
                                    date,2,null,seen);
                            if(i == 0){
                                lastTimeID = Integer.parseInt(data.getString("id"));
                            }
                            if(i == jArray.length()-1){
                                count = data.getString("id");
                            }
                            adapter.addToStart(message2,true);
                            totalItems++;
                        }
                    }


                } catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"JSON Error",Toast.LENGTH_SHORT).show();
                }catch(ParseException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError{

                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("lastTimeID",Integer.toString(lastTimeID));
                hashMap.put("user1",user_id);
                hashMap.put("user2",the_other_id);
                return hashMap;

            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void sendChatText(){

        final String time = Long.toString(Calendar.getInstance().getTimeInMillis());
        Calendar cal = Calendar.getInstance();
        String  s = getDate(Long.parseLong(time),
                "EEE MMM dd HH:mm:ss z yyyy");
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        Date date1 = null;
        try {
            date1 = format.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(date1);
        final Date date = cal.getTime();

        mStringRequest = new StringRequest(Request.Method.POST, URL_SEND_TEXT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(is_online_user.equals("0"))
                send_notification(inputEditText);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Check network connection",Toast.LENGTH_SHORT).show();
                Author author = new Author(user_id,null);
                Message message = new Message(user_id,inputEditText,author,date,0,null,"0");
                adapter.addToStart(message,true);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user1",user_id);
                hashMap.put("user2",the_other_id);
                hashMap.put("sender",user_id);
                hashMap.put("chattext",inputEditText);
                hashMap.put("time",time);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void remove_chat(){


        final ProgressBar progressBar = new ProgressBar(getActivity(),null,android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mRelativeLayout.addView(progressBar,params);
        progressBar.setVisibility(View.VISIBLE);

        mStringRequest = new StringRequest(Request.Method.POST, URL_REMOVE_CHAT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.names().get(0).equals("success")){
                        getActivity().onBackPressed();
                    }else{
                        Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }catch(JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user1",user_id);
                hashMap.put("user2",the_other_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);


    }

    /*
    private void get_online(){

        mStringRequest = new StringRequest(Request.Method.POST, URL_NO_ONLINE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    no_online_string = jsonObject.getString("no_online");
                    no_online.setText("("+no_online_string+" online)");

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_id", user_id);   //making user online
                return hashMap;

            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }
    */

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(myRunnable);
        handler1.removeCallbacks(myRunnable1);
    }

    private void update_highlight() {

        mStringRequest = new StringRequest(Request.Method.POST, URL_UPDATE_HIGHLIGHT,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user1", the_other_id);
                hashMap.put("user2", user_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }
    private void send_notification(final String msg){

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
                hashMap.put("user_id",the_other_id);
                hashMap.put("title",username);
                hashMap.put("message",full_name+": "+msg);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }
/*
    private void get_offline(){

        mStringRequest = new StringRequest(Request.Method.POST, URL_NO_ONLINE_BACK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_id", user_id);   //making user online
                return hashMap;

            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }
*/

    private void refresh_online(){
        mStringRequest = new StringRequest(Request.Method.POST, URL_REFRESH_ONLINE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("info");
                    JSONObject JsonItem = jsonArray.getJSONObject(0);
                    //getting online status
                    is_online_user = JsonItem.getString("online");
                    if(!JsonItem.getString("online").equals("1")){
                    long time = Long.parseLong(JsonItem.getString("time_ago"));
                    long now = System.currentTimeMillis();

                    CharSequence min = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS);
                    if(min.equals("0 seconds ago")) {
                        is_online.setText("Active Now");
                        is_online.setVisibility(View.VISIBLE);
                    }
                    else {
                        is_online.setText(min);
                        is_online.setVisibility(View.VISIBLE);
                    }

                    }else{
                        is_online.setText("Active Now");
                        is_online.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",the_other_id);
                return hashMap;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

    private void load_more(){

        final ArrayList<Message> listMessages = new ArrayList<>();


        mStringRequest = new StringRequest(Request.Method.POST, URL_LOAD_MORE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){

                String image = other_username;
                progressBar.setVisibility(View.GONE);
                mScrollView.setVisibility(View.VISIBLE);
                page++;

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jArray = jsonResponse.getJSONArray("results");


                        for (int i = jArray.length()-1; i >= 0; i--) {
                            JSONObject data = jArray.getJSONObject(i);
                            temp_user1 = data.getString("user1");
                            temp_user2 = data.getString("user2");
                            temp_sender = data.getString("sender");
                            inputEditText = data.getString("chattext");
                            seen = data.getString("seen");
                            String  s = getDate(Long.parseLong(data.getString("time")),
                                    "EEE MMM dd HH:mm:ss z yyyy");
                            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                            Date date1 = format.parse(s);
                            Author author2 = new Author(temp_sender,image);
                            Message message2 = new Message(temp_sender,inputEditText,author2,
                                    date1,2,null,seen);
                            listMessages.add(message2);
                            if(i == jArray.length()-1){
                                count = data.getString("id");
                            }
                            totalItems++;
                        }
                    if(jArray.length()>0) {
                        adapter.addToEnd(listMessages, true);
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"JSON Error",Toast.LENGTH_SHORT).show();
                }catch(ParseException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError{

                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("lastTimeID",count);
                hashMap.put("user1",user_id);
                hashMap.put("user2",the_other_id);
                return hashMap;

            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);
    }

}
