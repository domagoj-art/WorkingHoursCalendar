package com.example.calendar.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.calendar.dao.StatisticsDataDao;
import com.example.calendar.entities.StatisticsData;

import java.util.concurrent.Executors;

@Database(entities = {StatisticsData.class},  exportSchema = false,version = 3)
public abstract class CalendarDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "db_calendar";
    public static final Migration MIGRATION_1_2 = new MigrationFrom1To2();
    public static final Migration MIGRATION_2_3 = new MigrationFrom2To3();
    private static CalendarDatabase calendarDatabase;

    public static synchronized CalendarDatabase getInstance(final Context context){
        if(calendarDatabase == null){
            calendarDatabase = Room
                    .databaseBuilder(context.getApplicationContext(),CalendarDatabase.class ,DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    getInstance(context).statisticsDataDao();
                                }
                            });
                        }
                    }).build();
        }
        return calendarDatabase;
    }
    public abstract StatisticsDataDao statisticsDataDao();
}
