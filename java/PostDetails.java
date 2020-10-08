package com.abhigam.www.foodspot;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PostDetails extends AppCompatActivity {

    public Fragment createFragment(){
        return new PostDetailsFragment();
    }

    public void activityOnEdit(String comment_id,String review,int position){
        PostDetailsFragment fragment = (PostDetailsFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        fragment.onEdit(comment_id,review,position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
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

    @Override
    public void onBackPressed() {
        PostDetailsFragment fragment =
                (PostDetailsFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if(fragment.mMessageInput.hasFocus()){
            fragment.mMessageInput.getInputEditText().setText("");
            fragment.mMessageInput.clearFocus();
        }else{
            super.onBackPressed();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }
}
