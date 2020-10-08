package com.abhigam.www.foodspot;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class AmountTransferMoney extends SingleFragmentActivity {

    public Fragment createFragment(){
        return  new FragmentAmountTransferMoney();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

}