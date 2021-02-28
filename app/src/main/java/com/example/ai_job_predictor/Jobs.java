package com.example.ai_job_predictor;

public class Jobs {
    String name;
    float percentage;
    public Jobs(String name,float percentage){
        this.name=name;
        this.percentage=percentage;
    }
    public String getName() {
        return name;
    }
    public float getPercentage() {
        return percentage;
    }
}
