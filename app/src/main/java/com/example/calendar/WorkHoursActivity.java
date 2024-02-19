package com.example.calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.calendar.db.CalendarDatabase;
import com.example.calendar.entities.StatisticsData;
import com.google.android.material.textfield.TextInputLayout;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkHoursActivity extends AppCompatActivity {
    TimePicker timePickerFrom, timePickerTo;
    TextView textView;
    int fromHour, fromMinute, toHour, toMinute, day, year;
    String month, monthYearIntent ;
    private CalendarDatabase calendarDatabase;
    TextInputLayout editText;
    StatisticsData data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_hours);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        textView = findViewById(R.id.displayEarningsId);
        timePickerFrom = findViewById(R.id.timePickerFrom);
        timePickerFrom.setIs24HourView(true);
        timePickerTo = findViewById(R.id.timePickerTo);
        timePickerTo.setIs24HourView(true);

        Intent intent = getIntent();
        day = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("day")));
        String monthYear = intent.getStringExtra("monthYear");
        String[] array = monthYear.split(" ");
        month = array[0];
        String tempYear = array[1];
        year = Integer.parseInt(tempYear);
        monthYearIntent = monthYear;

        if (savedInstanceState != null){
            editText = findViewById(R.id.hourlyRate);
            String hourlyRate = savedInstanceState.getString("hourlyRate");
            fromHour = savedInstanceState.getInt("fromHour");
            fromMinute = savedInstanceState.getInt("fromMinute");
            toHour = savedInstanceState.getInt("toHour");
            toMinute = savedInstanceState.getInt("toMinute");

            editText.getEditText().setText(hourlyRate);
            timePickerFrom.setHour(fromHour);
            timePickerFrom.setMinute(fromMinute);
            timePickerTo.setHour(toHour);
            timePickerTo.setMinute(toMinute);

            final Handler handler = new Handler();
            Executors.newSingleThreadExecutor()
                    .execute(() -> {
                        calendarDatabase = CalendarDatabase.getInstance(WorkHoursActivity.this);
                        String dailyEarnings = calendarDatabase.statisticsDataDao().getDailyEarnings(day, month, year);
                        handler.post(() -> displayEarnings(dailyEarnings));
                    });
        }else {
            final Handler handler = new Handler();
            Executors.newSingleThreadExecutor()
                    .execute(() -> {
                        calendarDatabase = CalendarDatabase.getInstance(WorkHoursActivity.this);
                        String startOfWork = calendarDatabase.statisticsDataDao().getStartOfWork(day, month, year);
                        String endOfWork = calendarDatabase.statisticsDataDao().getEndOfWork(day, month, year);
                        String dailyEarnings = calendarDatabase.statisticsDataDao().getDailyEarnings(day, month, year);
                        handler.post(() -> setWorkingHours(startOfWork, endOfWork));
                        handler.post(() -> displayEarnings(dailyEarnings));
                    });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.statistics) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            intent.putExtra("monthYear", monthYearIntent);
            intent.putExtra("month", month);
            startActivity(intent);
            return true;
        } else {

            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String hourlyRate;
        editText = findViewById(R.id.hourlyRate);
        fromHour = timePickerFrom.getHour();
        fromMinute = timePickerFrom.getMinute();
        toHour = timePickerTo.getHour();
        toMinute = timePickerTo.getMinute();
        hourlyRate = editText.getEditText().getText().toString();

        outState.putInt("fromHour", fromHour);
        outState.putInt("fromMinute", fromMinute);
        outState.putInt("toHour", toHour);
        outState.putInt("toMinute", toMinute);
        outState.putString("hourlyRate", hourlyRate);
    }

    public void deleteOnClick(View view) {
        String text;
        text = "On " + day + "." + " "+ month + " you have earned: "+ "0";
        DateTimeFormatter hours = DateTimeFormatter.ofPattern("HH");
        DateTimeFormatter minutes = DateTimeFormatter.ofPattern("mm");
        LocalDateTime now = LocalDateTime.now();
        int currentHour = Integer.parseInt(hours.format(now));
        int currentMinutes = Integer.parseInt(minutes.format(now));

        deleteData(day,month,year);
        textView.setText(text);
        timePickerFrom.setHour(currentHour);
        timePickerFrom.setMinute(currentMinutes);
        timePickerTo.setHour(currentHour);
        timePickerTo.setMinute(currentMinutes);
    }

    public void saveOnClick(View view) {
        String text;
        editText = findViewById(R.id.hourlyRate);
        fromHour = timePickerFrom.getHour();
        fromMinute = timePickerFrom.getMinute();
        toHour = timePickerTo.getHour();
        toMinute = timePickerTo.getMinute();

        if(editText.getEditText().getText().toString().length() == 0){
            String message = "You forget to put your hourly rate";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return;
        }

        LocalTime startTime = LocalTime.of(fromHour, fromMinute);
        LocalTime endTime = LocalTime.of(toHour, toMinute);
        String timeElapsedString = getTimeElapsedString(startTime, endTime);

        int hours = getHours(timeElapsedString);
        int minutes = getMinutes(timeElapsedString);
        String hourlyRate = (Objects.requireNonNull(editText.getEditText()).getText().toString());
        String dailyEarnings = calculateDailyEarnings(hours,minutes, hourlyRate);
        String startOfWork = timePickerValueToString(fromHour, fromMinute);
        String endOfWork = timePickerValueToString(toHour, fromMinute);
        String id = generateId();

        data = new StatisticsData(id, year, month, day, hours, minutes, hourlyRate,
                dailyEarnings,startOfWork, endOfWork );

        addDataInBackground(data);
        text = "On " + day + "." + " "+ month + " you have earned: " + dailyEarnings;
        textView.setText(text);
        editText.getEditText().setText("");
    }

    @NonNull
    private String getTimeElapsedString(LocalTime startTime, LocalTime endTime) {
        Duration timeElapsed = Duration.between(startTime, endTime);

        if(toHour < fromHour){
            timeElapsed = timeElapsed.plusDays(1);
            if(!timeElapsed.toString().contains("H")){
                return timeElapsed.toString().replaceAll("PT", "");
            }
            return timeElapsed.toString().replaceAll("PT", "")
                    .replaceAll("H", ":")
                    .replaceAll("M", "");

        } else if (timeElapsed.toString().contains("0S")) {
            return "";

        }else {
            if(!timeElapsed.toString().contains("H")) {
                return timeElapsed.toString().replaceAll("PT", "");
            }
            return timeElapsed.toString().replaceAll("PT", "")
                    .replaceAll("H", ":")
                    .replaceAll("M", "");
        }
    }

    private int getHours(String timeElapsed){
        String[] extractHour;
        if(timeElapsed.contains("M") || timeElapsed.equals("")){
            return 0;
        }
        extractHour = timeElapsed.split(":");
        return Integer.parseInt(extractHour[0]);
    }

    private int getMinutes(String timeElapsed){
        String[] extractMinute;
        int minutes;
        if(timeElapsed.contains("-") || timeElapsed.equals("")){
            return 0;
        } else if (timeElapsed.contains("M")) {
            minutes = Integer.parseInt(timeElapsed.substring(0, timeElapsed.length() - 1));
            return minutes;

        }else {
            extractMinute = timeElapsed.split(":");
            try {
                minutes = Integer.parseInt(extractMinute[1]);
            }catch (Exception e){
                return 0;
            }
            return minutes;
        }
    }

    private String timePickerValueToString(int hourFrom, int minuteFrom){
        StringBuilder stringBuilder = new StringBuilder();
        String hour = Integer.toString(hourFrom);
        String minute = Integer.toString(minuteFrom);

        stringBuilder.append(hour);
        stringBuilder.append(":");
        stringBuilder.append(minute);

        return stringBuilder.toString();

    }

    private String calculateDailyEarnings(int hours, int minutes, String dailyRate){
        double totalMinutes = minutes + (hours * 60);
        float dailyRateInt = Float.parseFloat(dailyRate);

        double dailyEarnings = totalMinutes * (dailyRateInt / 60.0);

        return String.valueOf(dailyEarnings);
    }
    private String generateId(){
        return day + month + year;
    }
    private void displayEarnings(String dailyEarnings){
        textView = findViewById(R.id.displayEarningsId);
        String text;
        if(dailyEarnings == null){
            dailyEarnings = "0";
        }
        text = "On " + day + "." + " "+ month + " you have earned: "+ dailyEarnings;
        textView.setText(text);
    }
    private void setWorkingHours(String startOfWork, String endOfWork){

        if(startOfWork != null || endOfWork != null){
        String[] extractStartOfWork = startOfWork.split(":");
        int fromHour = Integer.parseInt(extractStartOfWork[0]);
        int fromMinute = Integer.parseInt(extractStartOfWork[1]);

        String[] extractEndOfWork = endOfWork.split(":");
        int toHour = Integer.parseInt(extractEndOfWork[0]);
        int toMinute = Integer.parseInt(extractEndOfWork[1]);

        timePickerFrom.setHour(fromHour);
        timePickerFrom.setMinute(fromMinute);
        timePickerTo.setHour(toHour);
        timePickerTo.setMinute(toMinute);
        }


    }

    private void addDataInBackground(StatisticsData data){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            try {
                calendarDatabase.statisticsDataDao().insertData(data);
                handler.post(() -> Toast.makeText(WorkHoursActivity.this, "Data added", Toast.LENGTH_SHORT).show());

            }catch (Exception e){
                String message = "You can not have multiple entries";
                handler.post(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void deleteData(int day, String month, int year){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler();
        executorService.execute(() -> {
            calendarDatabase.statisticsDataDao().deleteData(day, month, year);
            handler.post(() -> Toast.makeText(WorkHoursActivity.this, "Data deleted", Toast.LENGTH_SHORT).show());
        });
    }

}