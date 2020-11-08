package org.konata.udpsender.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer extends Thread {
    private DatagramSocket socket;
    private byte[] buf = new byte[256];
    private int REPEAT = 3;
    private int port = 9;
    private String targetIP = "172.31.161.200";
    private String payload = "FFFFFFFF";

    public UDPServer() throws SocketException {
        socket = new DatagramSocket();
    }

    public void run() {
        int count = 0;
        while (count <= REPEAT) {
            try {
                DatagramPacket packet;
                InetAddress address = InetAddress.getByName(targetIP);
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            count++;
        }
        socket.close();
    }
}
