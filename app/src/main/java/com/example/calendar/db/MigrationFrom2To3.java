package com.example.calendar.db;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class MigrationFrom2To3 extends Migration {
    public MigrationFrom2To3() {
        super(2,3);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
        supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS new_statisticsData (" +
                "id TEXT PRIMARY KEY NOT NULL, " +
                "year INTEGER NOT NULL, " +
                "month TEXT NOT NULL, " +
                "day INTEGER NOT NULL, " +
                "hours INTEGER NOT NULL, " +
                "minutes INTEGER NOT NULL, "+
                "hourlyRate TEXT NOT NULL, " +
                "dailyEarnings TEXT NOT NULL,"+
                "startOfWork TEXT NOT NULL, " +
                "endOfWork TEXT NOT NULL )");
        supportSQLiteDatabase.execSQL("drop table statisticsData");
        supportSQLiteDatabase.execSQL("DROP TABLE IF EXISTS statisticsData");

        // Step 4: Rename the temporary table to the original table name
        supportSQLiteDatabase.execSQL("ALTER TABLE new_statisticsData RENAME TO statisticsData");


    }
}
