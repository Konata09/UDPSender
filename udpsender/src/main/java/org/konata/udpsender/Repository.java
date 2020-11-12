package org.konata.udpsender;

import android.util.Log;

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
//    private final String loginUrl = "https://www.baidu.com";
    // private final ResponseParser responseParser;

    private final Executor executor;
    private int REPEAT = 3; // 重复发送次数

    public Repository() {
        this.executor = Executors.newFixedThreadPool(5);
    }

    public void sendUDPPacket(final List<Device> devices, final int port, final String payload, final RepositoryCallback callback) {
        final boolean[] allSuccess = {true};

        for (int i = 0; i < devices.size(); i++) {
            final int finalI = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Device d = devices.get(finalI);
                    try {
                        sendSynchronousUDPPacket(d.ipAddr, port, payload);
                        Log.d("SendUDPPacket", "SUCCESS:" + d.ipAddr + " " + port + " " + payload);
                    } catch (Exception e) {
                        e.printStackTrace();
                        allSuccess[0] = false;
                        Log.d("SendUDPPacket", "FAIL:" + e.getMessage() + " " + d.ipAddr + " " + port + " " + payload);
                    }
                    if (devices.size() == finalI + 1) { // 最后一个目标设备执行callback通知调用函数 可能有Bug
                        callback.onComplete(allSuccess[0] ? new Result.Success(null) : new Result.Errors(null));
                    }
                }
            });
        }
    }

    private void sendSynchronousUDPPacket(String ip, int port, String payload) throws Exception {
        byte[] buf = Utils.hexStringToByteArray(payload);
        int count = 0;
        List<Exception> exceptionsList = new ArrayList<>();
        DatagramSocket socket = new DatagramSocket();
        while (count < REPEAT) {
            try {
                DatagramPacket packet;
                InetAddress address = InetAddress.getByName(ip);
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch (IOException e) {
//                System.out.println("err" + count + " " + ip);
                exceptionsList.add(e);
            } finally {
//                System.out.println(count + " " + ip);
                count++;
            }
        }
        socket.close();
        for (Exception e : exceptionsList) {
            throw e; // 只能返回某个地址的第一个错误 但不会中断上面的循环
        }

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

    }
}
