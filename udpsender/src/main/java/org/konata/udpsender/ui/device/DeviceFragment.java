package org.konata.udpsender.ui.device;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.konata.udpsender.AppDatabase;
import org.konata.udpsender.R;
import org.konata.udpsender.entity.Device;
import org.konata.udpsender.util.ImportExport;
import org.konata.udpsender.util.Utils;

import java.util.List;
import java.util.regex.Pattern;

public class DeviceFragment extends Fragment {

    private RecyclerView.Adapter mAdapter;
    private List<Device> devices;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_device, container, false);
        setHasOptionsMenu(true);

        // 设备列表
        RecyclerView recyclerView = v.findViewById(R.id.devicelistview);
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
                deviceName = viewInflated.findViewById(R.id.deviceaddname);
                deviceIp = viewInflated.findViewById(R.id.deviceaddip);
                deviceMac = viewInflated.findViewById(R.id.deviceaddmac);
                ipInput = viewInflated.findViewById(R.id.deviceaddipinput);
                macInput = viewInflated.findViewById(R.id.deviceaddmacinput);
                udpBox = viewInflated.findViewById(R.id.enableudp);
                wolBox = viewInflated.findViewById(R.id.enablewol);
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
                                refreshData();
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.import_export, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_import:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/*");
                startActivityForResult(intent, 3);
                return true;
            case R.id.action_export:
                if (ImportExport.exportDevice(this.getContext())) {
                    Snackbar.make(getView(), "Export Successfully", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getView(), "Export Failed", Snackbar.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (data != null) {
                uri = data.getData();
                Log.i("Export", "Uri: " + uri.toString());
                if (ImportExport.importFile(uri, getActivity(), 2)) {
                    refreshData();
                    Snackbar.make(getView(), "Import Successfully", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getView(), "Import Failed", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    void refreshData() {
        devices.clear();
        devices.addAll(AppDatabase.getDatabase(getContext()).deviceDao().getDevices());
        mAdapter.notifyDataSetChanged();
    }
}