package org.konata.udpsender.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.konata.udpsender.entity.Device;

import java.util.List;

@Dao
public interface DeviceDao {

    @Query("SELECT * FROM device")
    List<Device> getDevices();

    @Delete
    void deleteDevice(Device... devices);

    @Update
    void updateDevice(Device... devices);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDevice(Device... devices);
}
