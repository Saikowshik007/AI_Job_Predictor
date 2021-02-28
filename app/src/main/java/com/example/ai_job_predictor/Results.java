package com.example.ai_job_predictor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Results extends AppCompatActivity {
    ImageButton back;
    private RecyclerView jobRecycler;
    public static ListAdapter listAdapter;
    public static List<Jobs>jobs=new ArrayList<>();
    @Override
    protected void onStart(){
        fetch();
        jobRecycler = findViewById(R.id.job_recycler);
        jobRecycler.setLayoutManager(new LinearLayoutManager(this));
        listAdapter = new ListAdapter(Results.this,jobs);
        listAdapter.msort();
        jobRecycler.setAdapter(listAdapter);
        jobRecycler.getLayoutManager();
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        fetch();

        back=findViewById(R.id.back3);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Results.this,MainActivity.class));
                jobs.clear();
               finish();
            }
        });
    }
    public void fetch(){
        jobs.clear();
        Intent i=getIntent();
        DecimalFormat df = new DecimalFormat("##.##");
        List<String>labels= Arrays.asList(".Net Developer","Android developer","Big Data Engineer","Business Analyst","Data Analyst","Data Scientist","Database Administrator","DevOps Engineer","Digital Marketing","Financial Advisor","IOS developer","IT Support","Information Security Analyst","Java Developer","Machine Learning Engineer","Project Manager","SAP Consultant","Software Developer","Software Engineer","Software Tester","Technical Support","UI Designer","Web Developer","graphic designer","human resource manager");
        float[]results=i.getFloatArrayExtra("list");
        for(int j=0;j<results.length;j++) {
            jobs.add(new Jobs(labels.get(j),Float.parseFloat(df.format(results[j]*100))));
        }
    }
}