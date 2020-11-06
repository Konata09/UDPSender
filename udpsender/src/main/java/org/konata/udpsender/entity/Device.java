package org.konata.udpsender.entity;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Device {
    @PrimaryKey(autoGenerate = true)
    private int did;

    private String deviceName;
    private String ipAddr;
    private String macAddr;
    private boolean enableUDP;
    private boolean enableWOL;

    public Device(String deviceName, String ipAddr, String macAddr, boolean enableUDP, boolean enableWOL) {
        this.deviceName = deviceName;
        this.ipAddr = ipAddr;
        this.macAddr = macAddr;
        this.enableUDP = enableUDP;
        this.enableWOL = enableWOL;
    }

    @Ignore
    public Device(int did, String deviceName, String ipAddr, String macAddr, boolean enableUDP, boolean enableWOL) {
        this.did = did;
        this.deviceName = deviceName;
        this.ipAddr = ipAddr;
        this.macAddr = macAddr;
        this.enableUDP = enableUDP;
        this.enableWOL = enableWOL;
    }

    @Ignore
    public Device(int did) {
        this.did = did;
    }
}
