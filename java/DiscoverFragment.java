package com.abhigam.www.foodspot;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
 * Created by sourabhzalke on 01/07/18.
 */

public class DiscoverFragment extends Fragment {

    private static final String places_key = "AIzaSyCrkw0c9C5Kd9b_gE2niYbA6I9atGQlScQ";
    private RecyclerView mRecyclerViewRestaurants,mRecyclerViewCafes,
            mRecyclerViewHotels,mRecyclerViewParks,mRecyclerViewMonuments,mRecyclerViewHospital,
            mRecyclerViewGroceries;
    private AdapterDiscoverRestaurants mAdapterDiscoverRestaurants,mAdapterDiscoverCafes,
            mAdapterDiscoverHotels,mAdapterDiscoverParks,mAdapterDiscoverMonuments,mAdapterHospital,
            mAdapterGroceries;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private LinearLayoutManager linearLayoutManager;
    private NestedScrollView mNestedScrollView;

    private static final String URL_RESTAURANTS
            = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=26.2183,78.1828" +
            "&radius=15000&type=restaurant&keyword=restaurant&key="+places_key;

    private static final String URL_CAFES
            = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=26.2183,78.1828" +
            "&radius=15000&type=cafe&keyword=cafe&key="+places_key;

    private static final String URL_HOTELS
            = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=26.2183,78.1828" +
            "&radius=15000&type=hotel&keyword=hotel&key="+places_key;

    private static final String URL_PARKS
            = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=26.2183,78.1828" +
            "&radius=15000&type=park&keyword=park&key="+places_key;

    private static final String URL_MONUMENTS
            = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=26.2183,78.1828" +
            "&radius=15000&type=monument&keyword=mandir&key="+places_key;

    private static final String URL_HOSPITAL
            = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=26.2183,78.1828" +
            "&radius=15000&type=hospital&keyword=hospital&key="+places_key;

    private static final String URL_GROCERIES
            = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=26.2183,78.1828" +
            "&radius=15000&type=store&keyword=store&key="+places_key;


    private List<DataRecycler> data_restaurants = new ArrayList<>();
    private List<DataRecycler> data_cafes = new ArrayList<>();
    private List<DataRecycler> data_hotels = new ArrayList<>();
    private List<DataRecycler> data_parks = new ArrayList<>();
    private List<DataRecycler> data_monuments = new ArrayList<>();
    private List<DataRecycler> data_hospital = new ArrayList<>();
    private List<DataRecycler> data_grocery = new ArrayList<>();

    public static DiscoverFragment newInstance() {
        DiscoverFragment fragment = new DiscoverFragment();
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discover,container,false);


        mNestedScrollView = v.findViewById(R.id.nestedScrollview);

        mRecyclerViewRestaurants = v.findViewById(R.id.restaurants);
        mRecyclerViewCafes = v.findViewById(R.id.cafes);
        mRecyclerViewHotels = v.findViewById(R.id.hotels);
        mRecyclerViewParks = v.findViewById(R.id.parks);
        mRecyclerViewMonuments = v.findViewById(R.id.monuments);
        mRecyclerViewHospital = v.findViewById(R.id.hospital);
        mRecyclerViewGroceries = v.findViewById(R.id.groceries);

        //ALL of Recyclerview
        mRecyclerViewRestaurants.setNestedScrollingEnabled(false);
        mAdapterDiscoverRestaurants = new AdapterDiscoverRestaurants(getActivity(),data_restaurants);
        linearLayoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        mRecyclerViewRestaurants.setLayoutManager(linearLayoutManager);
        mRecyclerViewRestaurants.setAdapter(mAdapterDiscoverRestaurants);

        //ALL of Recyclerview
        mRecyclerViewCafes.setNestedScrollingEnabled(false);
        mAdapterDiscoverCafes = new AdapterDiscoverRestaurants(getActivity(),data_cafes);
        linearLayoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        mRecyclerViewCafes.setLayoutManager(linearLayoutManager);
        mRecyclerViewCafes.setAdapter(mAdapterDiscoverCafes);

        //ALL of Recyclerview
        mRecyclerViewHotels.setNestedScrollingEnabled(false);
        mAdapterDiscoverHotels = new AdapterDiscoverRestaurants(getActivity(),data_hotels);
        linearLayoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        mRecyclerViewHotels.setLayoutManager(linearLayoutManager);
        mRecyclerViewHotels.setAdapter(mAdapterDiscoverHotels);

        //ALL of Recyclerview
        mRecyclerViewParks.setNestedScrollingEnabled(false);
        mAdapterDiscoverParks = new AdapterDiscoverRestaurants(getActivity(),data_parks);
        linearLayoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        mRecyclerViewParks.setLayoutManager(linearLayoutManager);
        mRecyclerViewParks.setAdapter(mAdapterDiscoverParks);

        //ALL of Recyclerview
        mRecyclerViewMonuments.setNestedScrollingEnabled(false);
        mAdapterDiscoverMonuments = new AdapterDiscoverRestaurants(getActivity(),data_monuments);
        linearLayoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        mRecyclerViewMonuments.setLayoutManager(linearLayoutManager);
        mRecyclerViewMonuments.setAdapter(mAdapterDiscoverMonuments);

        //ALL of Recyclerview
        mRecyclerViewHospital.setNestedScrollingEnabled(false);
        mAdapterHospital = new AdapterDiscoverRestaurants(getActivity(),data_hospital);
        linearLayoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        mRecyclerViewHospital.setLayoutManager(linearLayoutManager);
        mRecyclerViewHospital.setAdapter(mAdapterHospital);

        //ALL of Recyclerview
        mRecyclerViewGroceries.setNestedScrollingEnabled(false);
        mAdapterGroceries = new AdapterDiscoverRestaurants(getActivity(),data_grocery);
        linearLayoutManager = new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        mRecyclerViewGroceries.setLayoutManager(linearLayoutManager);
        mRecyclerViewGroceries.setAdapter(mAdapterGroceries);

        getRestaurants();
        getCafes();
        getHotels();
        getParks();
        getMonuments();
        getHospital();
        getGroceries();

        return v;
    }

    private void getRestaurants(){

        mStringRequest = new StringRequest(Request.Method.GET, URL_RESTAURANTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        DataRecycler post_data = new DataRecycler();
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        post_data.restaurant_name = jsonObject1.getString("name");
                        JSONArray photos = jsonObject1.getJSONArray("photos");
                        JSONObject jsonObject2 = photos.getJSONObject(0);
                        post_data.photo_reference = jsonObject2.getString("photo_reference");
                        data_restaurants.add(post_data);
                    }

                    mAdapterDiscoverRestaurants.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                    mAdapterDiscoverRestaurants.notifyDataSetChanged();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    private void getCafes(){

        mStringRequest = new StringRequest(Request.Method.GET, URL_CAFES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        DataRecycler post_data = new DataRecycler();
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        post_data.restaurant_name = jsonObject1.getString("name");
                        JSONArray photos = jsonObject1.getJSONArray("photos");
                        JSONObject jsonObject2 = photos.getJSONObject(0);
                        post_data.photo_reference = jsonObject2.getString("photo_reference");
                        data_cafes.add(post_data);
                    }

                    mAdapterDiscoverCafes.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                    mAdapterDiscoverCafes.notifyDataSetChanged();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    private void getHotels(){

        mStringRequest = new StringRequest(Request.Method.GET, URL_HOTELS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        DataRecycler post_data = new DataRecycler();
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        post_data.restaurant_name = jsonObject1.getString("name");
                        JSONArray photos = jsonObject1.getJSONArray("photos");
                        JSONObject jsonObject2 = photos.getJSONObject(0);
                        post_data.photo_reference = jsonObject2.getString("photo_reference");
                        data_hotels.add(post_data);
                    }

                    mAdapterDiscoverHotels.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                    mAdapterDiscoverHotels.notifyDataSetChanged();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    private void getParks(){

        mStringRequest = new StringRequest(Request.Method.GET, URL_PARKS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        DataRecycler post_data = new DataRecycler();
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        post_data.restaurant_name = jsonObject1.getString("name");
                        JSONArray photos = jsonObject1.getJSONArray("photos");
                        JSONObject jsonObject2 = photos.getJSONObject(0);
                        post_data.photo_reference = jsonObject2.getString("photo_reference");
                        data_parks.add(post_data);
                    }

                    mAdapterDiscoverParks.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                    mAdapterDiscoverParks.notifyDataSetChanged();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    private void getMonuments(){

        mStringRequest = new StringRequest(Request.Method.GET, URL_MONUMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        DataRecycler post_data = new DataRecycler();
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        post_data.restaurant_name = jsonObject1.getString("name");
                        JSONArray photos = jsonObject1.getJSONArray("photos");
                        JSONObject jsonObject2 = photos.getJSONObject(0);
                        post_data.photo_reference = jsonObject2.getString("photo_reference");
                        data_monuments.add(post_data);
                    }

                    mAdapterDiscoverMonuments.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                    mAdapterDiscoverMonuments.notifyDataSetChanged();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    private void getHospital(){

        mStringRequest = new StringRequest(Request.Method.GET, URL_HOSPITAL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        DataRecycler post_data = new DataRecycler();
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        post_data.restaurant_name = jsonObject1.getString("name");
                        JSONArray photos = jsonObject1.getJSONArray("photos");
                        JSONObject jsonObject2 = photos.getJSONObject(0);
                        post_data.photo_reference = jsonObject2.getString("photo_reference");
                        data_hospital.add(post_data);
                    }

                    mAdapterHospital.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                    mAdapterHospital.notifyDataSetChanged();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }

    private void getGroceries(){

        mStringRequest = new StringRequest(Request.Method.GET, URL_GROCERIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        DataRecycler post_data = new DataRecycler();
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        post_data.restaurant_name = jsonObject1.getString("name");
                        JSONArray photos = jsonObject1.getJSONArray("photos");
                        JSONObject jsonObject2 = photos.getJSONObject(0);
                        post_data.photo_reference = jsonObject2.getString("photo_reference");
                        data_grocery.add(post_data);
                    }

                    mAdapterGroceries.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                    mAdapterGroceries.notifyDataSetChanged();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(mStringRequest);

    }



}
