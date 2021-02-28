package com.example.ai_job_predictor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class Welcome extends AppCompatActivity {
    Button login,register;
    ViewPager viewpager;
    SlideAdapter slideAdapter;
    WormDotsIndicator wormDotsIndicator;
    FirebaseAuth fa;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        login=findViewById(R.id.button3);
        fa= FirebaseAuth.getInstance();
        register=findViewById(R.id.button4);
        slideAdapter=new SlideAdapter(this);
        viewpager=findViewById(R.id.viewPager);
        viewpager.setAdapter(slideAdapter);
        wormDotsIndicator = (WormDotsIndicator) findViewById(R.id.worm_dots_indicator);
        wormDotsIndicator.setViewPager(viewpager);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this,Login.class));
                overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this,Register.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }
}
