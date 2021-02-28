package com.example.ai_job_predictor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Profile extends AppCompatActivity {
ImageButton back;
ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        back=findViewById(R.id.back);
        photo=findViewById(R.id.imageView3);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this,MainActivity.class));
                overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
                finish();
            }
        });

    }
    @Override
    public void onBackPressed(){
        startActivity(new Intent(Profile.this, MainActivity.class));
        overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
        finish();
        super.onBackPressed();
    }
}