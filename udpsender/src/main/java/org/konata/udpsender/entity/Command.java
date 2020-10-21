package org.konata.udpsender.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Command {
    @PrimaryKey(autoGenerate = true)
    public int cid;

    public String commandName;
    public String commandValue;

}
