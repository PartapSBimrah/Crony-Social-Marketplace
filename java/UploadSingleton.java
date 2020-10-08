package com.abhigam.www.foodspot;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by sourabhzalke on 25/03/17.
 */

public class UploadSingleton {

    private static UploadSingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private UploadSingleton(Context context){
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue(){

        if(mRequestQueue==null)
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            return mRequestQueue;
    }

    public static synchronized UploadSingleton getmInstance(Context context){

        if(mInstance == null){
            mInstance = new UploadSingleton(context);
        }
        return mInstance;
    }

    public<T> void addToRequestQue(Request<T> request){

        getRequestQueue().add(request);
    }


}
