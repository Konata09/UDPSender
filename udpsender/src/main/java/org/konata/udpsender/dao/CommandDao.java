package org.konata.udpsender.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.konata.udpsender.entity.Command;

import java.util.List;

@Dao
public interface CommandDao {

    @Query("SELECT * FROM command")
    List<Command> getCommands();

    @Delete
    void deleteCommand(Command ...commands);

    @Update
    void updateCommand(Command ...commands);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCommand(Command ...commands);
}
