package com.abhigam.www.foodspot;

import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TransactionDone extends AppCompatActivity {

    private String INTENT_ENROLLMENT = "com.abhigam.www.foodspot.enrollment";
    private String INTENT_FULLNAME = "com.abhigam.www.foodspot.fullname";
    private String INTENT_COINS = "com.abhigam.www.foodspot.intent_coins";
    private String INTENT_LINEAR = "com.abhigam.www.foodspot.linear";

    private String enrollment,fullname,coins,linear;
    private LinearLayout ok;
    private TextView info_transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_done);

        enrollment = getIntent().getStringExtra(INTENT_ENROLLMENT);
        fullname = getIntent().getStringExtra(INTENT_FULLNAME);
        coins = getIntent().getStringExtra(INTENT_COINS);
        info_transaction = findViewById(R.id.info_transaction);
        linear = getIntent().getStringExtra(INTENT_LINEAR);

        Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
        }

        ok = findViewById(R.id.done);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        if(Integer.parseInt(coins)==1){
            coins = "₹"+coins;
        }else{
            coins = "₹"+coins;
        }
        if(!fullname.equals("Store")){
            String sourceString = "<b>" + coins + "</b> successfully sent to <b> " + fullname
                    + "</b> with enrollment <b> " +
                    enrollment + "</b>.";
            info_transaction.setText(Html.fromHtml(sourceString));
        }else{
            String sourceString = "Transaction of "+"<b>" + coins + "</b> has been successful.\n" +
                    "For more details, see all your transactions in <b>Wallet</b>.";
            info_transaction.setText(Html.fromHtml(sourceString));
        }



    }

    public void goBack() {
        if(linear.equals("3")){
            Intent x = new Intent(this, MainActivity.class);
            x.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(x);
        }else {
            Intent i = new Intent(this, PayOrTransfer.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }
}
