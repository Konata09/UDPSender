package org.konata.udpsender.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Command {
    @PrimaryKey(autoGenerate = true)
    public int cid;

    public String commandName;
    public String commandValue;
    public int port;

    public Command(String commandName, String commandValue, int port) {
        this.commandName = commandName;
        this.commandValue = commandValue;
        this.port = port;
    }

    @Ignore
    public Command(int cid, String commandName, String commandValue, int port) {
        this.cid = cid;
        this.commandName = commandName;
        this.commandValue = commandValue;
        this.port = port;
    }

    @Ignore
    public Command(int cid) {
        this.cid = cid;
    }


}
