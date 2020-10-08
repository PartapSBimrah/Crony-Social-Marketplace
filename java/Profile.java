package com.abhigam.www.foodspot;

import android.support.v4.app.Fragment;
import android.os.Bundle;

public class Profile extends SingleFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.right_in_faded, R.anim.fade_out_slow);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.right_out_faded);
    }

    public Fragment createFragment(){
        return new FragmentProfile();
    }
}
