package com.example.calendar.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "statisticsData")
public class StatisticsData {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String id;

    @NonNull
    private Integer year;
    @NonNull
    private String month;
    @NonNull
    private Integer day;
    @NonNull
    private Integer hours;
    @NonNull
    private Integer minutes;
    @NonNull
    private String hourlyRate;
    @NonNull
    private String dailyEarnings;
    @NonNull
    private String startOfWork;
    @NonNull
    private String endOfWork;



    public StatisticsData(String id, Integer year, String month, Integer day, Integer hours,
                          Integer minutes, String hourlyRate, String dailyEarnings,
                          String startOfWork, String endOfWork) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hours = hours;
        this.minutes = minutes;
        this.hourlyRate = hourlyRate;
        this.dailyEarnings = dailyEarnings;
        this.startOfWork = startOfWork;
        this.endOfWork = endOfWork;
    }

    public String getId() {
        return id;
    }

    public void setId(Integer _id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public String getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(String hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getDailyEarnings() {
        return dailyEarnings;
    }

    public void setDailyEarnings(String dailyEarnings) {
        this.dailyEarnings = dailyEarnings;
    }

    public String getStartOfWork() {
        return startOfWork;
    }

    public void setStartOfWork(String startOfWork) {
        this.startOfWork = startOfWork;
    }

    public String getEndOfWork() {
        return endOfWork;
    }

    public void setEndOfWork(String endOfWork) {
        this.endOfWork = endOfWork;
    }
}
