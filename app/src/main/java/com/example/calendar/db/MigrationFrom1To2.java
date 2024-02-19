package com.example.calendar.db;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class MigrationFrom1To2 extends Migration {
    public MigrationFrom1To2() {
        super(1, 2);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE statisticsData ADD COLUMN startOfWork String");
        database.execSQL("ALTER TABLE statisticsData ADD COLUMN endOfWork String");
    }
}
