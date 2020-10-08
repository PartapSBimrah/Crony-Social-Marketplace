package com.abhigam.www.foodspot;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by sourabhzalke on 05/04/18.
 */

public class FragmentLoginCheckActivity extends Fragment {

    private TextView quote;
    private String[] quotes;
    private String quote_string;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login_check,container,false);

        Window window = getActivity().getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        window.setStatusBarColor(ContextCompat.getColor(getActivity(), android.R.color.white));
        }

        //getting quotes array
        quotes =  getResources().getStringArray(R.array.quotes);

        quote = v.findViewById(R.id.quote);

        //generating random no
        int idx = new Random().nextInt(quotes.length);
        quote_string = (quotes[idx]);

        //showing quote
        quote.setText(quote_string);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if(SaveSharedPreference.getUserName(getActivity()).length() != 0)
                {
                    Intent main = new Intent(getContext(),MainActivity.class);
                    main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(main);
                    getActivity().finish();
                }else{
                    Intent i = new Intent(getActivity(),FirstScreen.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(i);
                    getActivity().finish();
                }
            }
        }, 2500);
        return v;
    }
}
