package org.konata.udpsender.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONObject;
import org.konata.udpsender.AppDatabase;
import org.konata.udpsender.UDPSenderApplication;
import org.konata.udpsender.entity.Command;
import org.konata.udpsender.entity.Device;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.List;
import java.util.Objects;

public class ImportExport {

    private static final int DATAVERSION = 1;

    public static boolean exportCommand(Context context) {
        List<Command> commands = AppDatabase.getDatabase(context).commandDao().getCommands();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("version", DATAVERSION);
            JSONArray jsonArray = new JSONArray();
            for (Command c : commands) {
                JSONObject singleCmd = new JSONObject();
                singleCmd.put("name", c.commandName);
                singleCmd.put("value", c.commandValue);
                singleCmd.put("port", c.port);
                jsonArray.put(singleCmd);
            }
            jsonObject.put("cmd", jsonArray);
            Log.d("ExportCommand", jsonObject.toString());
            File file = new File(context.getFilesDir(), "udpsender_cmd.json");
            Writer writer = new BufferedWriter(new FileWriter(file));
            writer.write(jsonObject.toString());
            writer.close();
            shareFile(file, context, "Command Configuration");
            return true;
        } catch (Exception e) {
            Log.e("ExportCommand", e.getMessage());
            return false;
        }
    }

    public static boolean exportDevice(Context context) {
        List<Device> devices = AppDatabase.getDatabase(context).deviceDao().getDevices();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("version", DATAVERSION);
            JSONArray jsonArray = new JSONArray();
            for (Device d : devices) {
                JSONObject singleDev = new JSONObject();
                singleDev.put("name", d.deviceName);
                singleDev.put("ip", d.ipAddr);
                singleDev.put("mac", d.macAddr);
                singleDev.put("enUDP", d.enableUDP);
                singleDev.put("enWOL", d.enableWOL);
                jsonArray.put(singleDev);
            }
            jsonObject.put("dev", jsonArray);
            Log.d("ExportDevice", jsonObject.toString());
            File file = new File(context.getFilesDir(), "udpsender_dev.json");
            Writer writer = new BufferedWriter(new FileWriter(file));
            writer.write(jsonObject.toString());
            writer.close();
            shareFile(file, context, "Device List");
            return true;
        } catch (Exception e) {
            Log.e("ExportDevice", e.getMessage());
            return false;
        }
    }

    public static boolean importCommand(JSONObject json) {
        try {
            int version = (int) json.get("version");
            JSONArray jsonArray = (JSONArray) json.get("cmd");
            Command[] commands = new Command[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject singleCmd = jsonArray.getJSONObject(i);
                String name = (String) singleCmd.get("name");
                String value = (String) singleCmd.get("value");
                int port = (int) singleCmd.get("port");
                Command command = new Command(name, value, port);
                commands[i] = command;
            }
            AppDatabase.getDatabase(UDPSenderApplication.getAppContext()).commandDao().insertCommand(commands);
            return true;
        } catch (Exception e) {
            Log.e("Import Command", e.getMessage());
            return false;
        }
    }

    public static boolean importDevice(JSONObject json) {
        try {
            int version = (int) json.get("version");
            JSONArray jsonArray = (JSONArray) json.get("dev");
            Device[] devices = new Device[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject singleDev = jsonArray.getJSONObject(i);
                String name = (String) singleDev.get("name");
                String ip = (String) singleDev.get("ip");
                String mac = (String) singleDev.get("mac");
                boolean enUDP = (boolean) singleDev.get("enUDP");
                boolean enWoL = (boolean) singleDev.get("enWOL");
                Device device = new Device(name, ip, mac, enUDP, enWoL);
                devices[i] = device;
            }
            AppDatabase.getDatabase(UDPSenderApplication.getAppContext()).deviceDao().insertDevice(devices);
            return true;
        } catch (Exception e) {
            Log.e("Import Command", e.getMessage());
            return false;
        }
    }

    public static void shareFile(File file, Context context, String filedesp) {
        Uri uri = getUriFromFile(file);

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        if (file.exists()) {
            intentShareFile.setType("text/json");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, filedesp);
            intentShareFile.putExtra(Intent.EXTRA_TEXT, filedesp);
            context.startActivity(Intent.createChooser(intentShareFile, filedesp));
        }

    }

    public static Uri getUriFromFile(File file) {
        Context context = UDPSenderApplication.getAppContext();
        Uri uriForFile;
        try {
            uriForFile = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            return uriForFile;
        } catch (Exception e) {
            Log.e("Sharefile", e.getMessage());
            return null;
        }
    }

    public static boolean importFile(Uri uri, Activity activity, int type) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean ret;
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            switch (type) {
                case 1: // 导入命令
                    ret = importCommand(jsonObject);
                    break;
                case 2: // 导入设备
                    ret = importDevice(jsonObject);
                    break;
                default:
                    return false;
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
