package org.konata.udpsender.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import org.konata.udpsender.R;
import org.konata.udpsender.entity.Device;

import java.util.List;

public class HomeDeviceRecyclerViewAdapter extends RecyclerView.Adapter<HomeDeviceRecyclerViewAdapter.DevViewHolder> {
    private Boolean isSelectAll = false;
    private Boolean isSelectNone = false;
    private List<Device> mDataset;

    public void selectAll() {
        isSelectAll = true;
        notifyDataSetChanged();
    }

    public void selectNone() {
        isSelectNone = true;
        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class DevViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CheckBox checkBox;
        public String devIP;
        public String devMAC;
        public Device device;

        public DevViewHolder(CheckBox cb) {
            super(cb);
            checkBox = cb;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HomeDeviceRecyclerViewAdapter(List<Device> devices) {
        mDataset = devices;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DevViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CheckBox v = (CheckBox) LayoutInflater.from(parent.getContext()).inflate(R.layout.device_select_view, parent, false);
        DevViewHolder vh = new DevViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DevViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Device dev = mDataset.get(position);
        holder.checkBox.setText(dev.deviceName);
//        holder.devIP = dev.ipAddr;
//        holder.devMAC = dev.macAddr;
        holder.device = dev;
        Integer lastPosition = mDataset.size() - 1;
        if (isSelectAll) {
            holder.checkBox.setChecked(true);
            if (position == lastPosition)
                isSelectAll = false;
        }
        if (isSelectNone) {
            holder.checkBox.setChecked(false);
            if (position == lastPosition)
                isSelectNone = false;
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
