package com.example.calendar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;



import com.example.calendar.db.CalendarDatabase;
import com.example.calendar.entities.StatisticsData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class StatisticsActivity extends AppCompatActivity {
    TextView yearlyEarnings;
    TextView monthlyEarnings;
    private CalendarDatabase calendarDatabase;
    int year;
    String month;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        yearlyEarnings = findViewById(R.id.yearlyEarnings);
        monthlyEarnings = findViewById(R.id.monthlyEarnings);


        Intent intent = getIntent();
        String monthYear = intent.getStringExtra("monthYear");
        String[] array = monthYear.split(" ");
        String tempYear = array[1];
        year = Integer.parseInt(tempYear);
        month = array[0];
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        final Handler handler = new Handler();
        Executors.newSingleThreadExecutor()
                .execute(() -> {
                    calendarDatabase = CalendarDatabase.getInstance(StatisticsActivity.this);
                    final List<String> yearly = calendarDatabase.statisticsDataDao().getAllYearlyEarnings(year);
                    final List<String> monthly = calendarDatabase.statisticsDataDao().getAllMonthlyEarnings(year, month);
                    final List<StatisticsData> allMonthlyEarnings = calendarDatabase.statisticsDataDao().getAllMonthly(month,year);
                    final List<StatisticsData> allYearlyEarnings = calendarDatabase.statisticsDataDao().getAllYearly(year);
                    handler.post(() -> createMonthlyChart(allMonthlyEarnings));
                    handler.post(() -> createYearlyChart(allYearlyEarnings));
                    handler.post(() -> {
                        String value = sumEarnings(yearly);
                        String text = "In " + year + " you have earned: " + value;
                        yearlyEarnings.setText(text);
                    });
                    handler.post(() -> {
                        String value = sumEarnings(monthly);
                        String text = "In " + month + " you have earned: " + value;
                        monthlyEarnings.setText(text);
                    });
                });
    }


    private String sumEarnings(List<String> list){
        Double total = 0.00;

        for (String earnings : list) {
            try {
                double earningsValue = Double.parseDouble(earnings);
                total += earningsValue;
            } catch (NumberFormatException e) {
                total = -1.0;
                e.printStackTrace();
            }
        }

        return String.valueOf(total);
    }
    private void createYearlyChart(List<StatisticsData> list){
        BarChart barChart = findViewById(R.id.yearlyChart);
        barChart.getAxisRight().setDrawLabels(false);

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> monthsLabels = new ArrayList<>();

        float yMaxValue = 0;

        for (int i = 0; i <= list.size() - 1; i++) {
            String month = list.get(i).getMonth();
            if(!monthsLabels.contains(month)){
                monthsLabels.add(month);
            }
        }
        int xPosition = 0;
        for (String month : monthsLabels) {
            ArrayList<String> monthValue = new ArrayList<>();
            for (int i = 0; i <= list.size() - 1; i++) {
                String getMonth = list.get(i).getMonth();
                if (month.equals(getMonth)) {
                    monthValue.add(list.get(i).getDailyEarnings());
                }
            }

            float entry = Float.parseFloat(sumEarnings(monthValue));
            if(yMaxValue < entry){
                yMaxValue = entry;
            }
            entries.add(new BarEntry(xPosition, entry));
            xPosition++;



        }
        System.out.println(yMaxValue);
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisLineWidth(1f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(5);
        yAxis.setTextSize(10f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(10f);
        xAxis.setLabelCount(12);


        BarDataSet barDataSet = new BarDataSet(entries, "Months");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barChart.getDescription().setEnabled(false);
        barChart.invalidate();

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(monthsLabels));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
    }
    private void createMonthlyChart(List<StatisticsData> list){
        BarChart barChart = findViewById(R.id.monthlyChart);
        barChart.getAxisRight().setDrawLabels(false);

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> days = new ArrayList<>();
        List<Float> allMonthlyEarnings = new ArrayList<>();
        for(int i = 0; i <= list.size()-1; i++){
            try {
                float earningsValue = Float.parseFloat(list.get(i).getDailyEarnings());
                allMonthlyEarnings.add(earningsValue);
                entries.add(new BarEntry(i, earningsValue));
                days.add(String.valueOf(list.get(i).getDay()));

            }catch (NumberFormatException e){
                
            }
        }
        float highestValue = getHighestValue(allMonthlyEarnings);


        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(highestValue);
        yAxis.setAxisLineWidth(1f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(5);
        yAxis.setTextSize(10f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(10f);
        xAxis.setLabelCount(31);


        BarDataSet barDataSet = new BarDataSet(entries, "Day of month " + month);
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barChart.getDescription().setEnabled(false);
        barChart.invalidate();

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(days));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
    }
    private float getHighestValue(List<Float> list){
        float highestValue = 0;
        for (int i = 0; i <= list.size()-1; i++){
            if (list.get(i) > highestValue){
                highestValue = list.get(i);
            }
        }
        return highestValue;
    }
}