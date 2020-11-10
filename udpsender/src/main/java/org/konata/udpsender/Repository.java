package org.konata.udpsender;

import org.konata.udpsender.util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Repository {
    private final String loginUrl = "https://www.baidu.com";
//    private final ResponseParser responseParser;

    private final Executor executor;
    private DatagramSocket socket;
    //    private byte[] buf = new byte[256];
//    private byte[] buf = "abc234".getBytes();
    private String in = "4C696768746F6EFE051452016CFF";
    private int REPEAT = 3; // 重复发送次数

    public Repository() {
        this.executor = Executors.newFixedThreadPool(4);
    }

    public void sendUDPPacket(final String ip, final int port, final String payload, final RepositoryCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Result result = sendSynchronousUDPPacket(ip, port, payload);
                    callback.onComplete(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    Result errorResult = new Result.Error<>(e);
                    callback.onComplete(errorResult);
                }
            }
        });
    }

    public Result sendSynchronousUDPPacket(String ip, int port, String payload) {
        byte[] buf = Utils.hexStringToByteArray(in);
        try {
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
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }


}
