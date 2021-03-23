package com.example.ai_job_predictor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;public int[] imageArray={R.drawable.a,R.drawable.b,R.drawable.d};
    public String[] titleArray={"Image1","Image2","Image3"};
    public String[] descriptionArray={"A1","A2"};
    public int[] backgroundColorArray={R.color.jet,R.color.oil,R.color.accent,R.color.teal_700};
    public SlideAdapter(Context context){
        this.context=context;

    }
    @Override
    public int getCount() {
        return titleArray.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater= (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.slide,container,false);
        //ImageView imageView=(ImageView)view.findViewById(R.id.);
        LinearLayout linearLayout=view.findViewById(R.id.linearLayout);
        //linearLayout.setBackgroundColor(backgroundColorArray[position]);
        linearLayout.setBackgroundResource(imageArray[position]);

        container.addView(view);
        return view;
    }

}
