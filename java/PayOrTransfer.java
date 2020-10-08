package com.abhigam.www.foodspot;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PayOrTransfer extends SingleFragmentActivity {

    public Fragment createFragment(){
        return new FragmentPayOrTransfer();
    }

    public String checkAmount(){
        FragmentPayOrTransfer fragment = (FragmentPayOrTransfer) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        return fragment.checkAmount();
    }

}
