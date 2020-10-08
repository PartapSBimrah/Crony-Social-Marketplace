package com.abhigam.www.foodspot;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class ForgotPassword extends SingleFragmentActivity {

    public Fragment createFragment(){
        return new FragmentForgotPassword();
    }
}
