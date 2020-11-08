package org.konata.udpsender.ui.device;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.konata.udpsender.AppDatabase;
import org.konata.udpsender.R;
import org.konata.udpsender.entity.Device;
import org.konata.udpsender.util.Utils;

import java.util.List;
import java.util.regex.Pattern;

public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.MyViewHolder> {
    View myView;
    private List<Device> devices;

    @NonNull
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.deviceitem, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView deviceName;
        public TextView deviceIp;
        public TextView deviceMac;
        public TextView popupBtn;

        public MyViewHolder(View view) {
            super(view);
            deviceName = (TextView) view.findViewById(R.id.deviceitemname);
            deviceIp = (TextView) view.findViewById(R.id.deviceitemip);
            deviceMac = (TextView) view.findViewById(R.id.deviceitemac);
            popupBtn = (TextView) view.findViewById(R.id.devicelistoptionbtn);
        }
    }

    public DeviceRecyclerViewAdapter(List<Device> devices) {
        this.devices = devices;
    }

    void refreshData() {
        devices.clear();
        devices.addAll(AppDatabase.getDatabase(myView.getContext()).deviceDao().getDevices());
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        myView = recyclerView;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final String name = devices.get(position).deviceName;
        final String ip = devices.get(position).ipAddr;
        final String mac = Utils.trimMACtoShow(devices.get(position).macAddr);
        final boolean udp = devices.get(position).enableUDP;
        final boolean wol = devices.get(position).enableWOL;
        final int did = devices.get(position).did;
        holder.deviceName.setText(name);
        holder.deviceIp.setText(ip);
        holder.deviceMac.setText(mac);
        // 弹出菜单
        holder.popupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), holder.popupBtn);
                popup.inflate(R.menu.commandoptionmenu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId(); // 用户点击的菜单 id
                        if (itemId == R.id.commandoptionedit) {
                            // 弹出编辑对话框
                            final TextInputLayout ipInput;
                            final TextInputLayout macInput;
                            final EditText deviceName;
                            final EditText deviceIp;
                            final EditText deviceMac;
                            final CheckBox wolBox;
                            final CheckBox udpBox;
                            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Edit Device");
                            View viewInflated = LayoutInflater.from(view.getContext()).inflate(R.layout.device_add_dialog, (ViewGroup) myView, false);
                            ipInput = (TextInputLayout) viewInflated.findViewById(R.id.deviceaddipinput);
                            macInput = (TextInputLayout) viewInflated.findViewById(R.id.deviceaddmacinput);
                            deviceName = (EditText) viewInflated.findViewById(R.id.deviceaddname);
                            deviceIp = (EditText) viewInflated.findViewById(R.id.deviceaddip);
                            deviceMac = (EditText) viewInflated.findViewById(R.id.deviceaddmac);
                            wolBox = (CheckBox) viewInflated.findViewById(R.id.enablewol);
                            udpBox = (CheckBox) viewInflated.findViewById(R.id.enableudp);
                            deviceName.setText(name);
                            deviceIp.setText(ip);
                            deviceMac.setText(mac);
                            wolBox.setChecked(wol);
                            udpBox.setChecked(udp);
                            builder.setView(viewInflated);
                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 为了输入检查不通过时点击按钮对话框不消失，这里先留空
                                }
                            });

                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            final AlertDialog dialog = builder.create();
                            dialog.show();

                            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String newDeviceName;
                                    String newDeviceIp;
                                    String newDeviceMac;
                                    boolean newDeviceUDP;
                                    boolean newDeviceWOL;
                                    newDeviceName = deviceName.getText().toString();
                                    newDeviceIp = deviceIp.getText().toString();
                                    newDeviceMac = Utils.trimMACtoStor(deviceMac.getText().toString());
                                    newDeviceUDP = udpBox.isChecked();
                                    newDeviceWOL = wolBox.isChecked();
                                    boolean isIpValid = ipInput.getError() == null;
                                    boolean isMacValid = macInput.getError() == null;
                                    // 设备名不为空并且ip和mac有一项符合要求
                                    if (!newDeviceName.isEmpty() && ((!newDeviceIp.isEmpty()) && isIpValid && isMacValid) || (!newDeviceMac.isEmpty() && isMacValid && isIpValid)) {
                                        if (newDeviceWOL && newDeviceMac.isEmpty()) { // 网络唤醒必须填写MAC地址
                                            Toast toast = Toast.makeText(view.getContext(), "WoL require MAC address", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                        } else {
                                            Device device = new Device(did, newDeviceName, newDeviceIp, newDeviceMac, newDeviceUDP, newDeviceWOL);
                                            dialog.dismiss();
                                            AppDatabase.getDatabase(v.getContext()).deviceDao().updateDevice(device);
                                            Snackbar.make(view, newDeviceName + " Edited Position:" + position, Snackbar.LENGTH_LONG).show();
                                            devices.clear();
                                            devices.addAll(AppDatabase.getDatabase(v.getContext()).deviceDao().getDevices());
                                            notifyDataSetChanged();
                                        }
                                    } else {
                                        Toast toast = Toast.makeText(view.getContext(), "Please check your input", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                }
                            });
                            deviceIp.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    Pattern ipPattern = Pattern.compile("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}$");
                                    if (!ipPattern.matcher(s.toString()).matches()) {
                                        ipInput.setError("invalid IP address");
                                    } else {
                                        ipInput.setError(null);
                                    }
                                }
                            });
                            deviceMac.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    Pattern macPattern = Pattern.compile("^([0-9A-Fa-f]{2}([:-]?|$)){6}$");
                                    if (!macPattern.matcher(s.toString()).matches()) {
                                        macInput.setError("invalid MAC address");
                                    } else {
                                        macInput.setError(null);
                                    }
                                }
                            });
                            return true;
                        } else if (itemId == R.id.commandoptiondelete) {
                            AppDatabase.getDatabase(view.getContext()).deviceDao().deleteDevice(new Device(did));
                            Snackbar.make(view, name + " deleted", BaseTransientBottomBar.LENGTH_LONG).show();
                            refreshData();
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return devices.size();
    }
}
