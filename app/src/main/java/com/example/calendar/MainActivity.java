package com.example.calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener{
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    //private static final LocalDate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState != null){
            String currentDate = savedInstanceState.getString("currentDate");
            selectedDate = LocalDate.parse(currentDate);
            initWidgets();
            setMonthView();
        }else {

            initWidgets();
            selectedDate = LocalDate.now();
            setMonthView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.statistics) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            intent.putExtra("monthYear",monthYearFromDate(selectedDate));
            startActivity(intent);
            return true;
        } else {

            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentDate", String.valueOf(selectedDate));
    }

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }
    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<DayModel> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<DayModel> daysInMonthArray(LocalDate date) {
        ArrayList<DayModel> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        LocalDate today = LocalDate.now();

        for (int i = 1; i <= 42; i++) {
            boolean isCurrentMonth;
            String dayText;
            boolean isToday;

            if (i > dayOfWeek && i <= daysInMonth + dayOfWeek) {
                
                isCurrentMonth = true;
                dayText = String.valueOf(i - dayOfWeek);
                isToday = isCurrentMonth && LocalDate.of(date.getYear(), date.getMonth(), i - dayOfWeek).equals(today);
            } else {
                isCurrentMonth = false;
                dayText = "";
                isToday = false;
            }

            daysInMonthArray.add(new DayModel(dayText, isToday));
        }

        return daysInMonthArray;

    }

    private String monthYearFromDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public void previousMonthAction(View view) {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemListener(int position, String dayText) {
        if(!dayText.equals("")){
            Intent intent = new Intent(this, WorkHoursActivity.class);
            intent.putExtra("day",dayText);
            intent.putExtra("monthYear",monthYearFromDate(selectedDate));
            startActivity(intent);

        }
    }
}