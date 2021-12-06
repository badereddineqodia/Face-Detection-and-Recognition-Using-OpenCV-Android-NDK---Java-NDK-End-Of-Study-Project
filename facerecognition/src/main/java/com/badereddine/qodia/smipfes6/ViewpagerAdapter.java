package com.badereddine.qodia.smipfes6;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

class ViewpagerAdapter extends PagerAdapter {

    private ArrayList<Personne> personnes;
    LayoutInflater layoutInflater;
    View view;
    Context context;
    ImageView imagePersonne;
    TextView textName;
    TextView textTime;
    Picasso picasso;
    TextView textPrediction;


    public ViewpagerAdapter(Context context, ArrayList<Personne> personnes){
        this.personnes=personnes;
        this.context=context;
        picasso=Picasso.get();

    }


    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.layout_grid_adapter, container, false);
        imagePersonne=view.findViewById(R.id.imagePersonne);
        textName=view.findViewById(R.id.textName);
        textTime=view.findViewById(R.id.textTime);
        textPrediction=view.findViewById(R.id.textPrediction);
        File file=new File(personnes.get(position).getImagePathPersonne()+personnes.get(position).getNamePersonne());

        picasso.load(file)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(imagePersonne);

        textName.setText(personnes.get(position).getNamePersonne());
        textPrediction.setText(personnes.get(position).getPortion()+"%");
        textTime.setText(personnes.get(position).getTimePersonne());

        container.addView(view);
        return view;
    }


    @Override
    public int getCount() {
        return personnes.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view==(LinearLayout)o);
    }
}
