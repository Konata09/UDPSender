package org.konata.udpsender.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.konata.udpsender.MyViewModel;
import org.konata.udpsender.R;
import org.konata.udpsender.Repository;

import java.util.regex.Pattern;

public class UDPPacketFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private MyViewModel model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_udp_packet, container, false);
        Button sendButton;
        final EditText portText;


        // 命令下拉选择列表
        String[] commands = {"命令1", "command2", "turn On", "Restart", "tttteeesssttt"};
        Spinner spinner = (Spinner) v.findViewById(R.id.presetspinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, commands);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        sendButton = (Button) v.findViewById(R.id.sendbutton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model = new ViewModelProvider(getActivity()).get(MyViewModel.class);
                model.makeRequest("asd");
            }
        });

        // 端口输入
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
                    i = Integer.parseInt(s.toString());
                    if (i >= 1 && i <= 65535) {
                        portText.setError(null);
                    } else {
                        portText.setError("Port must between 1 and 65535");
                    }
                }
            }
        });

        // 设备多选列表
        recyclerView = (RecyclerView) v.findViewById(R.id.udppackettargetdevice);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        String[] devices = {"1111", "dev22", "dev333", "西楼大厅", "西楼保密室", "西办公楼保密室2", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "asd", "asddd"};
        mAdapter = new HomeDeviceRecyclerViewAdapter(devices);
        recyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button selectAllBtn;
        Button selectNoneBtn;
        selectAllBtn = (Button) view.findViewById(R.id.selectall);
        selectNoneBtn = (Button) view.findViewById(R.id.selectnone);
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
