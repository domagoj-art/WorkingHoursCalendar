package com.example.calendar.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.calendar.entities.StatisticsData;

import java.util.List;

@Dao
public interface StatisticsDataDao {

    @Query("select * from statisticsData where id = :id")
    StatisticsData getById(String id);
    @Query("select dailyEarnings from statisticsData where year = :year")
    List<String> getAllYearlyEarnings(int year);
    @Query("select dailyEarnings from statisticsData where year = :year and month = :month")
    List<String> getAllMonthlyEarnings(int year, String month);
    @Query("select dailyEarnings from statisticsData where day = :day and month = :month and year = :year")
    String getDailyEarnings(int day, String month, int year);
    @Query("select startOfWork from statisticsData where day = :day and month = :month and year = :year")
    String getStartOfWork(int day, String month, int year );
    @Query("select endOfWork from statisticsData where day = :day and month = :month and year = :year")
    String getEndOfWork(int day, String month, int year );
    @Insert
    void insertData(StatisticsData data);
    @Update
    void updateData(StatisticsData data);
    @Query("delete from statisticsData where day = :day and month = :month and year = :year")
    void deleteData(int day, String month, int year);

    @Query("select * from statisticsData where month = :month and year = :year")
    List<StatisticsData> getAllMonthly(String month, int year);
    @Query("select * from statisticsData where year = :year")
    List<StatisticsData> getAllYearly(int year);

}
