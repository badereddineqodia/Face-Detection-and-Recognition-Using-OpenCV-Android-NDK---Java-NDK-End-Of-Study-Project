package com.badereddine.qodia.smipfes6;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment  implements DatePickerDialog.OnDateSetListener{
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar=Calendar.getInstance();
        int Year =calendar.get(Calendar.YEAR);
        int month =calendar.get(Calendar.MONTH);
        int day =calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),this,Year,month,day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        String format= DateFormat.getDateInstance().format(calendar.getTime());
        Bundle bundle=new Bundle();
        bundle.putString("dateTime",format);
        FragmentGridPager fragmentGridPager=new FragmentGridPager();
        fragmentGridPager.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.content_frame,fragmentGridPager).commit();
    }
}
