package org.konata.udpsender;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.konata.udpsender.dao.CommandDao;
import org.konata.udpsender.entity.Command;

@Database(entities = {Command.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CommandDao commandDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context){
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "myDatabase").allowMainThreadQueries().build();
        }
        return INSTANCE;
    }

}
