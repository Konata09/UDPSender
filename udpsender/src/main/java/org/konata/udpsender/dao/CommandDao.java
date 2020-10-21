package org.konata.udpsender.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import org.konata.udpsender.entity.Command;

@Dao
public class CommandDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCommand(Command ...commands);
}
