package com.badereddine.qodia.smipfes6;

import android.app.DatePickerDialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FragmentGridPager extends Fragment implements DatePickerDialog.OnDateSetListener{

    ViewPager viewPager;
    DatabaseReference databaseReference;
    ArrayList<Personne> personnes;
    TextView textDate,textNbr;
    Button textDateButton;


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        databaseReference= FirebaseDatabase.getInstance().getReference();
        personnes=new ArrayList <>();
         final View view=inflater.inflate(R.layout.layout_grid_view,container,false);
        textDate=view.findViewById(R.id.textDate);
        textDateButton=view.findViewById(R.id.textDateButton);
        textNbr=view.findViewById(R.id.textNbr);


        textDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment=new DatePickerFragment();
                datePickerFragment.show(getActivity().getSupportFragmentManager(),"choisi la date");
            }
        });
        String dateTime;
        try {
            dateTime=getArguments().getString("dateTime");
            textDate.setText(dateTime);
            databaseReference.child("personnesRecongnized").child(dateTime).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot personneSnap:dataSnapshot.getChildren()){
                        Personne personne=personneSnap.getValue(Personne.class);
                        personnes.add(personne);

                    }

                    textNbr.setText("          "+personnes.size()+" Personnes");
                    viewPager= (ViewPager) view.findViewById(R.id.viewpager);
                    viewPager.setAdapter(new ViewpagerAdapter(getActivity(),personnes));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }catch (NullPointerException e){
            textDate.setText("null");

        }



        return view;
    }


}
