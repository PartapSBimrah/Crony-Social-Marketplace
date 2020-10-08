package com.abhigam.www.foodspot;
/**
 * Created by sourabhzalke on 18/04/18.
 */

import android.support.v4.app.Fragment;


public class ActivityNotifications extends SingleFragmentActivity {

    public Fragment createFragment(){
        return new FragmentNotifications();
    }

}