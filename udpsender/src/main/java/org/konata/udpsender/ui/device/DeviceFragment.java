package org.konata.udpsender.ui.device;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.konata.udpsender.AppDatabase;
import org.konata.udpsender.R;
import org.konata.udpsender.entity.Device;
import org.konata.udpsender.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DeviceFragment extends Fragment {

    private RecyclerView.Adapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_device, container, false);
        final List<Device> devices;

        // 设备列表
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.devicelistview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        devices = AppDatabase.getDatabase(getContext()).deviceDao().getDevices();
        mAdapter = new DeviceRecyclerViewAdapter(devices);
        recyclerView.setAdapter(mAdapter);

        // 增加按钮
        FloatingActionButton fab = v.findViewById(R.id.devicelistfab);
        fab.setOnClickListener(new View.OnClickListener() {
            private String newDeviceName = "";
            private String newDeviceIp = "";
            private String newDeviceMac = "";
            private boolean newDeviceEnableUDP;
            private boolean newDeviceEnableWOL;

            @Override
            public void onClick(final View view) {
                final EditText deviceName;
                final EditText deviceIp;
                final EditText deviceMac;
                final TextInputLayout ipInput;
                final TextInputLayout macInput;
                final CheckBox udpBox;
                final CheckBox wolBox;
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add New Device");

                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.device_add_dialog, (ViewGroup) getView(), false);
                deviceName = (EditText) viewInflated.findViewById(R.id.deviceaddname);
                deviceIp = (EditText) viewInflated.findViewById(R.id.deviceaddip);
                deviceMac = (EditText) viewInflated.findViewById(R.id.deviceaddmac);
                ipInput = (TextInputLayout) viewInflated.findViewById(R.id.deviceaddipinput);
                macInput = (TextInputLayout) viewInflated.findViewById(R.id.deviceaddmacinput);
                udpBox = (CheckBox) viewInflated.findViewById(R.id.enableudp);
                wolBox = (CheckBox) viewInflated.findViewById(R.id.enablewol);
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
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newDeviceEnableUDP = udpBox.isChecked();
                        newDeviceEnableWOL = wolBox.isChecked();
                        newDeviceName = deviceName.getText().toString();
                        newDeviceIp = deviceIp.getText().toString();
                        newDeviceMac = Utils.trimMACtoStor(deviceMac.getText().toString());
                        boolean isIpValid = ipInput.getError() == null;
                        boolean isMacValid = macInput.getError() == null;

                        // 设备名不为空并且ip和mac至少有一个符合要求
                        if (!newDeviceName.isEmpty() && ((!newDeviceIp.isEmpty()) && isIpValid && isMacValid) || (!newDeviceMac.isEmpty() && isIpValid && isMacValid)) {
                            if (newDeviceEnableWOL && newDeviceMac.isEmpty()) { // 网络唤醒必须填写MAC地址
                                Toast toast = Toast.makeText(view.getContext(), "WoL require MAC address", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            } else {
                                Device device = new Device(newDeviceName, newDeviceIp, newDeviceMac, newDeviceEnableUDP, newDeviceEnableWOL);
                                dialog.dismiss();
                                AppDatabase.getDatabase(getContext()).deviceDao().insertDevice(device);
                                Snackbar.make(v, newDeviceName + " Added", Snackbar.LENGTH_LONG).show();
                                devices.clear();
                                devices.addAll(AppDatabase.getDatabase(getContext()).deviceDao().getDevices());
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast toast = Toast.makeText(getContext(), "Please check your input", Toast.LENGTH_SHORT);
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
            }
        });
        return v;
    }
}