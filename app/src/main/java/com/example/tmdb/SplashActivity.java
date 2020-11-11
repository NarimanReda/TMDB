package com.example.tmdb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Animation animation= AnimationUtils.loadAnimation(SplashActivity.this,R.anim.slide_up);
        ImageView imageViewLogo=findViewById(R.id.image_tmdb_logo);
        imageViewLogo.startAnimation(animation);
        imageViewLogo.setVisibility(View.VISIBLE);
        int seconds = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        }, seconds * 2000);
    }
}
