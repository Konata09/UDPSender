package org.konata.udpsender.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.konata.udpsender.entity.Command;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<Command> {
    private Context context;
    private List<Command> commands;

    public SpinnerAdapter(Context context, int textViewResourceId, List<Command> commands) {
        super(context, textViewResourceId, commands);
        this.context = context;
        this.commands = commands;
    }

    @Override
    public int getCount() {
        return commands.size();
    }

    @Nullable
    @Override
    public Command getItem(int position) {
        return commands.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(commands.get(position).commandName);
        return label;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(commands.get(position).commandName);
        return label;
    }
}
