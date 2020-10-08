package com.abhigam.www.foodspot;


import android.support.v4.app.Fragment;

public class CreateAd extends SingleFragmentActivity {

    public Fragment createFragment(){
        return new CreateAdFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
