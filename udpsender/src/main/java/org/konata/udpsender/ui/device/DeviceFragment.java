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

import org.konata.udpsender.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DeviceFragment extends Fragment {

    private RecyclerView.Adapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_device, container, false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.devicelistview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        List devices = new ArrayList();
        devices.add("dev1");
        devices.add("dev2");
        devices.add("dev3");
        devices.add("dev4");
        devices.add("dev5");
        devices.add("设备");
        devices.add("dev2");
        devices.add("dev3");
        devices.add("dev4");
        devices.add("dev5");
        devices.add("dev1");
        devices.add("dev2");
        devices.add("dev3");
        devices.add("dev4");
        devices.add("dev5");
        devices.add("dev1");
        devices.add("dev2");
        devices.add("dev3");
        devices.add("dev4");
        devices.add("dev5");
        devices.add("dev1");
        devices.add("dev2");
        devices.add("dev3");
        devices.add("dev4");
        devices.add("dev5last");
        mAdapter = new DeviceRecyclerViewAdapter(devices);
        recyclerView.setAdapter(mAdapter);


        // 增加按钮
        FloatingActionButton fab = v.findViewById(R.id.devicelistfab);
        fab.setOnClickListener(new View.OnClickListener() {
            private String newDeviceName = "";
            private String newDeviceIp = "";
            private String newDeviceMac = "";

            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add New Device");
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.device_add_dialog, (ViewGroup) getView(), false);
                final EditText deviceName = (EditText) viewInflated.findViewById(R.id.deviceaddname);
                final EditText deviceIp = (EditText) viewInflated.findViewById(R.id.deviceaddip);
                final EditText deviceMac = (EditText) viewInflated.findViewById(R.id.deviceaddmac);
                final TextInputLayout ipInput = (TextInputLayout) viewInflated.findViewById(R.id.deviceaddipinput);
                final TextInputLayout macInput = (TextInputLayout) viewInflated.findViewById(R.id.deviceaddmacinput);
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
                        newDeviceName = deviceName.getText().toString();
                        newDeviceIp = deviceIp.getText().toString();
                        newDeviceMac = deviceMac.getText().toString();
                        boolean isIpValid = deviceIp.getError() == null;
                        boolean isMacValid = deviceMac.getError() == null;


                        if (isIpValid && isMacValid && !newDeviceName.isEmpty() && (!newDeviceIp.isEmpty() | !newDeviceMac.isEmpty())) {
                            Snackbar.make(v, newDeviceName + " Added", Snackbar.LENGTH_LONG).show();
                            dialog.dismiss();
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