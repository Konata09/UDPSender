package org.konata.udpsender;

import org.konata.udpsender.entity.Device;
import org.konata.udpsender.util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Repository {
    private final String loginUrl = "https://www.baidu.com";
    // private final ResponseParser responseParser;

    private final Executor executor;
    private DatagramSocket socket;
    private int REPEAT = 3; // 重复发送次数

    public Repository() {
        this.executor = Executors.newFixedThreadPool(5);
    }

    public void sendUDPPacket(final List<Device> devices, final int port, final String payload, final RepositoryCallback callback) {
        final List<Result.Error> errors = new ArrayList<>();
        for (final Device d : devices) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        sendSynchronousUDPPacket(d.ipAddr, port, payload);
                        System.out.println("Succ:" + d.ipAddr + " " + port + " " + payload);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(d.deviceName);
                        errors.add(new Result.Error<>(e, d.deviceName));
                        System.out.println(errors.size());
                    }
                }
            });
        }
        System.out.println(errors.size());
        if (errors.size() == 0)
            callback.onComplete(new Result.Success("OK"));
        else
            callback.onComplete(new Result.Errors(errors));
    }

    private Result sendSynchronousUDPPacket(String ip, int port, String payload) throws Exception {
        byte[] buf;
//        try {
        buf = Utils.hexStringToByteArray(payload);
//        } catch (StringIndexOutOfBoundsException ee) {
//            System.out.println("ee");
//            return new Result.Error(ee, "Bad payload");
//        }

//        try {
        socket = new DatagramSocket();
        int count = 0;
        while (count < REPEAT) {
            try {
                DatagramPacket packet;
                InetAddress address = InetAddress.getByName(ip);
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(count);
                }
                count++;
            }
            socket.close();

//            URL url = new URL(loginUrl);
//            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
//            httpConnection.setRequestMethod("GET");
//            httpConnection.setRequestProperty("Accept", "*/*");
//            InputStream inputStream = httpConnection.getInputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            StringBuffer stringBuffer = new StringBuffer();
//            String temp;
//            while ((temp = bufferedReader.readLine()) != null) {
//                stringBuffer.append(temp);
//            }
//            bufferedReader.close();
//            inputStreamReader.close();
//            inputStream.close();

        return new Result.Success("OK");
//        } catch (Exception e) {
//            return new Result.Error(e, ip);
//        }
    }
}
