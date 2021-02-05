package com.charan.playspot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;

import org.joda.time.DateTime;

public class Booking extends Fragment implements DatePickerListener {

    RecyclerView parentRV, childRV1, childRV2, childRV3;
    Adapter parentAdapter, childAdapter1, childAdapter2, childAdapter3;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_booking,container,false);

        parentRV = view.findViewById(R.id.rvParent);
        childRV1 = view.findViewById(R.id.rvChild1);
        childRV2 = view.findViewById(R.id.rvChild2);
        childRV3 = view.findViewById(R.id.rvChild3);

        parentRV.setLayoutManager(new LinearLayoutManager(getContext()));
        childRV1.setLayoutManager(new LinearLayoutManager(getContext()));
        childRV2.setLayoutManager(new LinearLayoutManager(getContext()));
        childRV3.setLayoutManager(new LinearLayoutManager(getContext()));

        HorizontalPicker picker=(HorizontalPicker)view.findViewById(R.id.calender);
        picker.setListener(this)
                .setDays(20)
                .setOffset(10)
                .setDateSelectedColor(Color.RED)
                .setDateSelectedTextColor(Color.WHITE)
                .setMonthAndYearTextColor(Color.BLACK)
                .setTodayButtonTextColor(Color.DKGRAY)
                .setTodayDateTextColor(Color.BLACK)
                .setTodayDateBackgroundColor(Color.GRAY)
                .setDayOfWeekTextColor(Color.BLACK)
                .setUnselectedDayTextColor(Color.BLACK)
                .showTodayButton(false)
                .init();


        picker.setBackgroundColor(Color.WHITE);
        picker.setDate(new DateTime().plusDays(4));
        return view;

    }

    @Override
    public void onDateSelected(DateTime dateSelected) {
        String s = dateSelected.toString();
        Toast.makeText(getContext(),s,Toast.LENGTH_SHORT).show();
    }
}