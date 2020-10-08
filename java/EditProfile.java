package com.abhigam.www.foodspot;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EditProfile extends SingleFragmentActivity {

   public Fragment createFragment(){
      return  new EditProfileFragment();
   }
}
