package com.example.ai_job_predictor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.ImmutableList;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter{
    private Context mcontext;
    private List<Jobs> JobList;
    TextView title,percentage;
    ImageButton button;
public ListAdapter(Context context,List<Jobs> jobList){
    JobList=jobList;
    mcontext=context;
}
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.jobs_layout, parent, false);
        return new jobHolder(view);
    }
    @Override
    public int getItemViewType(int position) {
        Jobs job = JobList.get(position);

        return position;
    }
    @Override
    public int getItemCount() {
        return JobList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    final Jobs details=JobList.get(position);
        ((jobHolder)holder).bind(details);
        button=holder.itemView.findViewById(R.id.imageButton);
        if(position!=0) { button.setVisibility(View.INVISIBLE); }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String job = details.getName();
                mcontext.startActivity(new Intent(mcontext, Search.class).putExtra("job", job));
            }
        });
    }
    public class jobHolder extends RecyclerView.ViewHolder {
        TextView title;
        public jobHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv10);
            percentage=itemView.findViewById(R.id.textView11);
        }

        void bind(Jobs message) {
            title.setText(message.getName());
            percentage.setText(String.valueOf(message.getPercentage()));
        }
    }

    public void clear() {
        JobList.clear();
    }
    public void msort(){
        Collections.sort(JobList, new Comparator<Jobs>() {
            @Override
            public int compare(Jobs o1, Jobs o2) {
                return new Float(o1.getPercentage()).compareTo(new Float(o2.getPercentage()));

            }
        }.reversed());

    }

}