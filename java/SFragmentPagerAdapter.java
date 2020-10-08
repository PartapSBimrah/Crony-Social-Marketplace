package com.abhigam.www.foodspot;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
/**
 * Created by sourabhzalke on 31/05/18.
 */

public class SFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public SFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new CronyMarket();
        }else if(position == 1){
            return new FragmentMerchants();
        }else if(position == 2){
            return new FragmentMerchants();
        }else if(position == 3){
            return new FragmentMerchants();
        }else if(position == 4){
            return new FragmentMerchants();
        }else if(position == 5){
            return new FragmentMerchants();
        }else if(position == 6){
            return new FragmentMerchants();
        }else{
            return new FragmentMerchants();
        }

    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 7;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.items);
            case 1:
                return mContext.getString(R.string.merchants);
            case 2:
                return mContext.getString(R.string.cafes);
            case 3:
                return mContext.getString(R.string.restaurants);
            case 4:
                return mContext.getString(R.string.hotels);
            case 5:
                return mContext.getString(R.string.street);
            case 6:
                return mContext.getString(R.string.places);
            default:
                return null;
        }
    }

}
