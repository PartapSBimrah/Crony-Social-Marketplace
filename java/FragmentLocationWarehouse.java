package com.abhigam.www.foodspot;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by sourabhzalke on 03/05/18.
 */

public class FragmentLocationWarehouse extends Fragment {

    private ImageView back;
    //banners
    ViewPager viewPager;
    List<DataRecycler> bannerDataRecycler;
    ViewPagerAdapter viewPagerAdapter;
    CircleIndicator indicator;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_ware_house,container,false);

        Window window = getActivity().getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.asana_blue));
        }
        viewPager = v.findViewById(R.id.viewPager);
        indicator =  v.findViewById(R.id.indicator);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        getImages();

        back = v.findViewById(R.id.back_arrow_image);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return v;
    }

    private void getImages(){

        bannerDataRecycler = new ArrayList<>();

        for(int i=0;i<3;++i){

            DataRecycler dataRecycler = new DataRecycler();
            dataRecycler.setBannerUrl("http://13.233.234.79/uploads/w_h_images/"+
                    "om_"+i+".jpg");
            bannerDataRecycler.add(dataRecycler);
        }

        viewPagerAdapter = new ViewPagerAdapter(bannerDataRecycler, getActivity());
        viewPager.setAdapter(viewPagerAdapter);
        indicator.setViewPager(viewPager);
        viewPagerAdapter.registerDataSetObserver(indicator.getDataSetObserver());

    }

}
