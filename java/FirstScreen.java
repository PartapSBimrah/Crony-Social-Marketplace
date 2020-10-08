package com.abhigam.www.foodspot;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FirstScreen extends SingleFragmentActivity {


    public Fragment createFragment(){
        return new FirstScreenFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
