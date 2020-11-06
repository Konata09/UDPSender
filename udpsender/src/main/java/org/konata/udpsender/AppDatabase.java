package org.konata.udpsender;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.konata.udpsender.dao.CommandDao;
import org.konata.udpsender.dao.DeviceDao;
import org.konata.udpsender.entity.Command;
import org.konata.udpsender.entity.Device;

@Database(entities = {Command.class, Device.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "myDatabase";

    public abstract CommandDao commandDao();
    public abstract DeviceDao deviceDao();

    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
        }
        return INSTANCE;
    }

}
