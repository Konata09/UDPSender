package org.konata.udpsender.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Command {
    @PrimaryKey(autoGenerate = true)
    public int cid;

    public String commandName;
    public String commandValue;

    public Command(String commandName, String commandValue) {
        this.commandName = commandName;
        this.commandValue = commandValue;
    }

    @Ignore
    public Command(int cid, String commandName, String commandValue) {
        this.cid = cid;
        this.commandName = commandName;
        this.commandValue = commandValue;
    }

    @Ignore
    public Command(int cid) {
        this.cid = cid;
    }


}
