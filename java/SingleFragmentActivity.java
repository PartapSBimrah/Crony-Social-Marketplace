package com.abhigam.www.foodspot;


import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;


/**
 * Created by sourabhzalke on 03/01/17.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        setContentView(R.layout.activity_fragment);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment= fm.findFragmentById(R.id.fragmentContainer);

        if(fragment == null){
            fragment=createFragment();
            fm.beginTransaction()
            .add(R.id.fragmentContainer,fragment)
            .commit();
        }
    }

}