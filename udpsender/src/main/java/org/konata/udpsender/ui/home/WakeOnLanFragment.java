package org.konata.udpsender.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

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
import org.konata.udpsender.entity.Device;

import java.util.ArrayList;
import java.util.List;

public class WakeOnLanFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private MyViewModel model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wake_on_lan, container, false);
        Button sendButton;
        final CheckBox toDeviceAddress;
        final CheckBox toBroadcastAddress;
        final CheckBox toDeviceBroadcastAddress;

        // magic packet 目标地址
        toDeviceAddress = v.findViewById(R.id.woldeviceip);
        toBroadcastAddress = v.findViewById(R.id.wolbroad255);
        toDeviceBroadcastAddress = v.findViewById(R.id.woldevice255);


        // 设备多选列表
        recyclerView = v.findViewById(R.id.udppackettargetdevice);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        final List<Device> devices = AppDatabase.getDatabase(getContext()).deviceDao().getWoLDevices();
        mAdapter = new HomeDeviceRecyclerViewAdapter(devices);
        recyclerView.setAdapter(mAdapter);

        sendButton = v.findViewById(R.id.wolsendbutton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean toDeviceAddr = toDeviceAddress.isChecked();
                boolean toBroadcast = toBroadcastAddress.isChecked();
                boolean toDeviceBroadcast = toDeviceBroadcastAddress.isChecked();
                if (!toDeviceAddr && !toBroadcast && !toDeviceBroadcast) {
                    Snackbar.make(getView(), "You must specify at least one address", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                model = new ViewModelProvider(getActivity()).get(MyViewModel.class);
                List<Device> targetDeviceList = new ArrayList<>();
                targetDeviceList.clear();
                for (int i = 0; i < devices.size(); i++) {
                    HomeDeviceRecyclerViewAdapter.DevViewHolder holder = (HomeDeviceRecyclerViewAdapter.DevViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    if (holder.checkBox.isChecked()) {
                        targetDeviceList.add(holder.device);
                    }
                }
                model.getRepository().sendWoLPacket(targetDeviceList, toDeviceAddr, toBroadcast, toDeviceBroadcast, new RepositoryCallback() {
                    @Override
                    public void onComplete(Result result) {
                        if (result instanceof Result.Success) {
                            Snackbar.make(getView(), "Send Successuflly", Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(getView(), "Some packect send failed, see logs for details", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button selectAllBtn = (Button) view.findViewById(R.id.selectall);
        Button selectNoneBtn = (Button) view.findViewById(R.id.selectnone);
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
