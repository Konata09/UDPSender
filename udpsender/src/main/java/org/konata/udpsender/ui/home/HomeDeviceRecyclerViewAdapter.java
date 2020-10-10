package org.konata.udpsender.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import org.konata.udpsender.R;

public class HomeDeviceRecyclerViewAdapter extends RecyclerView.Adapter<HomeDeviceRecyclerViewAdapter.MyViewHolder> {
    private Boolean isSelectAll = false;
    private Boolean isSelectNone = false;
    private String[] mDataset;

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
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CheckBox checkBox;

        public MyViewHolder(CheckBox cb) {
            super(cb);
            checkBox = cb;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HomeDeviceRecyclerViewAdapter(String[] devices) {
        mDataset = devices;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HomeDeviceRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CheckBox v = (CheckBox) LayoutInflater.from(parent.getContext()).inflate(R.layout.device_select_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.checkBox.setText(mDataset[position]);
        Integer lastPosition = mDataset.length - 1;
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
        return mDataset.length;
    }

}
