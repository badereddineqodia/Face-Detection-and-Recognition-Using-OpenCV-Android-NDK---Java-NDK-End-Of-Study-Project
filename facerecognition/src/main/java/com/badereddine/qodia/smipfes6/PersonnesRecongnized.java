package com.badereddine.qodia.smipfes6;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class PersonnesRecongnized extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personnes_recongnized);
        FragmentGridPager fragmentGridPager=new FragmentGridPager();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,fragmentGridPager).commit();

    }
}
