package com.abhigam.www.foodspot;
/**
 * Created by sourabhzalke on 18/04/18.
 */

import android.support.v4.app.Fragment;


public class Market extends SingleFragmentActivity {

    public Fragment createFragment(){
        return new CronyMarket();
    }

}
