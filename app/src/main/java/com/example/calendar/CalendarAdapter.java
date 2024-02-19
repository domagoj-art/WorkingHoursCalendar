package com.example.calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<DayModel> daysOfMonth;
    private final OnItemListener onItemListener;


    public CalendarAdapter(ArrayList<DayModel> daysOfMonth, OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;

        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell,parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener);


    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        DayModel dayModel = daysOfMonth.get(position);
        holder.daysOfMonth.setText(dayModel.getDayText());

        // Highlight today's date
        if (dayModel.isToday()) {
            holder.daysOfMonth.setBackgroundResource(R.drawable.circle_background); // Customize as needed
        } else {
            holder.daysOfMonth.setBackgroundResource(0); // Remove any previous background
        }
    }


        /*holder.daysOfMonth.setText(daysOfMonth.get(position));

    }*/

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface OnItemListener{
        void onItemListener(int position, String dayText);
    }
}
