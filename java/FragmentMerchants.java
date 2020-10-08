package com.abhigam.www.foodspot;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sourabhzalke on 31/05/18.
 */

public class FragmentMerchants extends Fragment {

    private StringRequest mStringRequest;
    private RequestQueue mRequestQueue;
    private String URL_MERCHANTS = "http://13.233.234.79/get_merchants.php";
    private RecyclerView mRecyclerView;
    private AdapterMerchants mAdapterMerchants;
    private List<DataRecycler> data = new ArrayList<>();
    private WrapContentLinearLayoutManager linearLayoutManager;
    private String REQUEST_TAG = "com.abhigam.www.foodspot.tag_request";

    public static FragmentMerchants newInstance(){
        FragmentMerchants fragment = new FragmentMerchants();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_merchants,container,false);

        mRequestQueue = Volley.newRequestQueue(getActivity());

        //ALL of Recyclerview
        mRecyclerView = v.findViewById(R.id.recycler_merchants);
        mRecyclerView.setNestedScrollingEnabled(false);


        mAdapterMerchants = new AdapterMerchants(getActivity(),data);
        linearLayoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapterMerchants);

        getMerchants();

        return v;
    }

    private void getMerchants(){

        mStringRequest = new StringRequest(Request.Method.GET, URL_MERCHANTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonArray = jsonResponse.getJSONArray("merchants");
                            for (int i=0;i<jsonArray.length();++i){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                DataRecycler item_data = new DataRecycler();
                                item_data.store_name = jsonObject.getString("name");
                                item_data.mer_balance = jsonObject.getString("balance");
                                item_data.mer_username = jsonObject.getString("mer_username");
                                item_data.mer_mobile = jsonObject.getString("mer_mobile");
                                item_data.fullname = jsonObject.getString("fullname");
                                item_data.merchant_id = jsonObject.getString("merchant_id");
                                item_data.location = jsonObject.getString("location");
                                item_data.city = jsonObject.getString("city");
                                item_data.latitude = jsonObject.getString("Latitude");
                                item_data.longitude = jsonObject.getString("Longitude");
                                if(!item_data.mer_username.equals("cronymerchant"))
                                    data.add(item_data);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mAdapterMerchants.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mStringRequest.setTag(REQUEST_TAG);
        mRequestQueue.add(mStringRequest);

    }

}
