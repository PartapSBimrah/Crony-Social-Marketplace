package com.abhigam.www.foodspot;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ImageDownloadActivity extends AppCompatActivity {

    private ImageView ic_back,mPostImage;
    private LinearLayout ic_download;
    private TextView toolbar_title;
    private ProgressBar mProgressBar;

    private String INTENT_URL_IMAGE = "intent_url_post_image";
    private String INTENT_POST_FULLNAME = "intent_post_full_name";
    private int x=0;
    private DataRecycler mDataRecycler = new DataRecycler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_download);

        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.transparent));
        }

        ic_back = findViewById(R.id.ic_back_arrow);
        ic_download = findViewById(R.id.ic_download);
        toolbar_title = findViewById(R.id.toolbar_title);
        mPostImage = findViewById(R.id.post_image);
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        ic_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x==1){
                    saveImage(getApplicationContext(),mDataRecycler.getBitmap());
                }else{
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.image_not_loaded),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        String URL = getIntent().getStringExtra(INTENT_URL_IMAGE);

        Zoomy.Builder builder = new Zoomy.Builder(this)
                .enableImmersiveMode(false)
                .target(mPostImage);
        builder.register();

        Glide
                .with(this)
                .load(URL)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(mPostImage.getMaxWidth(),
                        mPostImage.getMaxHeight()) { //width and height
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        mProgressBar.setVisibility(View.GONE);
                        mPostImage.setVisibility(View.VISIBLE);
                        mPostImage.setImageBitmap(resource);
                        mDataRecycler.setBitmap(resource);
                        x=1;
                    }
                });

        toolbar_title.setText(getIntent().getStringExtra(INTENT_POST_FULLNAME)+"'s Post");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void saveImage(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, UUID.randomUUID().toString() + ".jpg", "drawing");
        Toast.makeText(inContext,inContext.getResources().getString(R.string.image_saved),
                Toast.LENGTH_SHORT).show();
    }
}
