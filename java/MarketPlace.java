package com.abhigam.www.foodspot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by sourabhzalke on 31/05/18.
 */

public class MarketPlace extends Fragment{

    ViewPager mViewPager;
    TabLayout mTabLayout;
    LinearLayout ic_person_add,ic_bookmark;
    private TextView app_name;

    public static MarketPlace newInstance(){
        MarketPlace fragment = new MarketPlace();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_tab_layout, container, false);

        mViewPager = v.findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        SFragmentPagerAdapter adapter = new SFragmentPagerAdapter(getActivity(),
                getChildFragmentManager());

        app_name = v.findViewById(R.id.app_name);

        // Set the adapter onto the view pager
        mViewPager.setAdapter(adapter);

        app_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scrolltoTop();
            }
        });

        ic_person_add =v.findViewById(R.id.person_add);
        ic_person_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),SuggestFriends.class);
                getActivity().startActivity(i);
            }
        });
        ic_bookmark = v.findViewById(R.id.ic_bookmark);
        ic_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),WishList.class);
                getActivity().startActivity(i);
            }
        });

        // Give the TabLayout the ViewPager
        mTabLayout = v.findViewById(R.id.sliding_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        return v;
    }

}
