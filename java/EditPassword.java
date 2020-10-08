package com.abhigam.www.foodspot;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EditPassword extends SingleFragmentActivity {

    public Fragment createFragment(){
        return new FragmentEditPassword();
    }

}
