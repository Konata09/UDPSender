package org.konata.udpsender.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.konata.udpsender.R;

public class WakeOnLanFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wake_on_lan, container, false);

        // 设备多选列表
        recyclerView = (RecyclerView) v.findViewById(R.id.udppackettargetdevice);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        String[] devices = {"1111", "22", "3", "设备4", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555", "dev22", "dev333", "dev444", "dev555"};
//        mAdapter = new HomeDeviceRecyclerViewAdapter(devices);
        recyclerView.setAdapter(mAdapter);

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
