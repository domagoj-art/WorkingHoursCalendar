package com.example.calendar;

public class DayModel {
    String dayText;
    boolean isToday;

    public DayModel(String dayText, boolean isToday) {
        this.dayText = dayText;
        this.isToday = isToday;
    }

    public String getDayText() {
        return dayText;
    }

    public void setDayText(String dayText) {
        this.dayText = dayText;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }
}
