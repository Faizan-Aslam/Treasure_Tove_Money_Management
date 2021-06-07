package com.mcapp.mcapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.like.LikeButton;
import com.like.OnLikeListener;

public class ContactActivity extends AppCompatActivity {

    Boolean switchOnOff = false;
    Boolean likedStatus = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_contact);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_contactactivity);

            final LottieAnimationView lottieLove = findViewById(R.id.btn_love);
            final LottieAnimationView lottieSwitch = findViewById(R.id.btn_onOff);
            lottieSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(switchOnOff){
                        lottieSwitch.setMinAndMaxProgress(0.5f,1.0f);
                        lottieSwitch.playAnimation();

                        switchOnOff = false;
                    }
                    else {
                        lottieSwitch.setMinAndMaxProgress(0.0f,0.5f);
                        lottieSwitch.playAnimation();
                        switchOnOff = true;
                    }
                }
            });
            lottieLove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lottieLove.playAnimation();
                }
            });

            final LikeButton likeButton = findViewById(R.id.like_button);
            likeButton.setLiked(false);
            likedStatus = false;
            final Context context = this;
            likeButton.setOnLikeListener(new OnLikeListener(){
                @Override
                public void liked(LikeButton likeButton) {
                   /* likeButton.setLiked(true);
                    likedStatus = likeButton.isLiked() == true ? true : false;
                    Toast.makeText(context,"Liked Status: "+likedStatus, Toast.LENGTH_LONG).show();*/
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                   /* likeButton.setLiked(false);
                    likedStatus = likeButton.isLiked() ? true : false;
                    Toast.makeText(context,"Liked Status: "+ likedStatus, Toast.LENGTH_LONG).show();*/
                }
            });


        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            int id = item.getItemId();
            if (id == android.R.id.home) {
                finish();
                return true;
            }
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
