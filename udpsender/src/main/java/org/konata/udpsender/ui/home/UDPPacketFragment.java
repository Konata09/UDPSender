package org.konata.udpsender.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.konata.udpsender.AppDatabase;
import org.konata.udpsender.MyViewModel;
import org.konata.udpsender.R;
import org.konata.udpsender.RepositoryCallback;
import org.konata.udpsender.Result;
import org.konata.udpsender.entity.Command;
import org.konata.udpsender.entity.Device;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UDPPacketFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private MyViewModel model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_udp_packet, container, false);
        final RadioGroup radioGroup;
        Button sendButton;
        final EditText portText;

        // 端口输入框
        portText = v.findViewById(R.id.porttext);
        portText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int i;
                Pattern numPattern = Pattern.compile("^\\d*$");
                if (!numPattern.matcher(s.toString()).matches()) {
                    portText.setError("Port must between 1 and 65535");
                } else {
                    try {
                        i = Integer.parseInt(s.toString());
                        if (i >= 1 && i <= 65535) {
                            portText.setError(null);
                        } else {
                            portText.setError("Port must between 1 and 65535");
                        }
                    } catch (NumberFormatException e) {
                        portText.setError("Port must between 1 and 65535");

                    }
                }
            }
        });

        // 命令下拉选择列表
        final List<Command> commands = AppDatabase.getDatabase(getContext()).commandDao().getCommands();
        ArrayAdapter<Command> adapter = new SpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item, commands);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner = v.findViewById(R.id.presetspinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Command selectedCommand = commands.get(position);
                portText.setText(String.valueOf(selectedCommand.port));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 手动命令和预设命令选择
        final EditText customCmdEditText = v.findViewById(R.id.customvalue);
        radioGroup = v.findViewById(R.id.radioGroup);

        // 设备多选列表
        recyclerView = v.findViewById(R.id.udppackettargetdevice);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        final List<Device> devices = AppDatabase.getDatabase(getContext()).deviceDao().getDevices();
        mAdapter = new HomeDeviceRecyclerViewAdapter(devices);
        recyclerView.setAdapter(mAdapter);

        // 发送数据包
        sendButton = v.findViewById(R.id.sendbutton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model = new ViewModelProvider(getActivity()).get(MyViewModel.class);
                String payload = "";
                List<Device> targetDeviceList = new ArrayList<>();
                int port = Integer.parseInt(portText.getText().toString());

                if (radioGroup.getCheckedRadioButtonId() == R.id.usepreset) {
                    payload = ((Command) spinner.getSelectedItem()).commandValue;
                } else if (radioGroup.getCheckedRadioButtonId() == R.id.usecustom) {
                    payload = customCmdEditText.getText().toString();
                }

                targetDeviceList.clear();
                for (int i = 0; i < devices.size(); i++) {
                    HomeDeviceRecyclerViewAdapter.DevViewHolder holder = (HomeDeviceRecyclerViewAdapter.DevViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    if (holder.checkBox.isChecked()) {
                        targetDeviceList.add(holder.device);
                    }
                }

                model.getRepository().sendUDPPacket(targetDeviceList, port, payload, new RepositoryCallback() {
                    @Override
                    public void onComplete(Result result) {
                        if (result instanceof Result.Success) {
                            Snackbar.make(getView(), "Send Successuflly", Snackbar.LENGTH_LONG).show();
                        } else {
                            for (Result.Error e : ((List<Result.Error>) ((Result.Errors) result).errorList)) {
                                Snackbar.make(getView(), "Send Failed." + e.message + " " + e.exception, Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button selectAllBtn;
        Button selectNoneBtn;
        selectAllBtn = view.findViewById(R.id.selectall);
        selectNoneBtn = view.findViewById(R.id.selectnone);
        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((HomeDeviceRecyclerViewAdapter) mAdapter).selectAll();
            }
        });
        selectNoneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((HomeDeviceRecyclerViewAdapter) mAdapter).selectNone();
            }
        });
    }
}
