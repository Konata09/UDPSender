package org.konata.udpsender.ui.command;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.konata.udpsender.AppDatabase;
import org.konata.udpsender.R;
import org.konata.udpsender.entity.Command;

import java.util.List;
import java.util.regex.Pattern;

public class CommandRecyclerViewAdapter extends RecyclerView.Adapter<CommandRecyclerViewAdapter.MyViewHolder> {
    View myView;
    private List<Command> commands;

    @NonNull
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.commanditem, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView commandName;
        public TextView popupBtn;

        public MyViewHolder(View view) {
            super(view);
            commandName = (TextView) view.findViewById(R.id.commanditemname);
            popupBtn = (TextView) view.findViewById(R.id.commandlistoptionbtn);
        }
    }

    public CommandRecyclerViewAdapter(List<Command> commands) {
        this.commands = commands;
    }

    void refreshData() {
        commands.clear();
        commands.addAll(AppDatabase.getDatabase(myView.getContext()).commandDao().getCommands());
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        myView = recyclerView;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final String name = commands.get(position).commandName;
        final String value = commands.get(position).commandValue;
        final int port = commands.get(position).port;
        final int cid = commands.get(position).cid;
        holder.commandName.setText(name);
        holder.popupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), holder.popupBtn);
                popup.inflate(R.menu.commandoptionmenu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) { // item：用户点击的菜单项
                        int itemId = item.getItemId();
                        if (itemId == R.id.commandoptionedit) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            final EditText commandValue;
                            final EditText commandName;
                            final EditText commandPort;
                            final TextInputLayout valueInput;
                            final TextInputLayout portInput;
                            builder.setTitle("Edit Command");
                            View viewInflated = LayoutInflater.from(view.getContext()).inflate(R.layout.command_add_dialog, (ViewGroup) myView, false);
                            valueInput = (TextInputLayout) viewInflated.findViewById(R.id.commandaddvalueinput);
                            portInput = (TextInputLayout) viewInflated.findViewById(R.id.commandaddportinput);
                            commandName = (EditText) viewInflated.findViewById(R.id.commandaddname);
                            commandValue = (EditText) viewInflated.findViewById(R.id.commandaddvalue);
                            commandPort = (EditText) viewInflated.findViewById(R.id.commandaddport);
                            commandName.setText(name);
                            commandValue.setText(value);
                            commandPort.setText(String.valueOf(port));
                            builder.setView(viewInflated);
                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

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

                            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String newCommandName;
                                    String newCommandValue;
                                    String newCommandPort;
                                    newCommandName = commandName.getText().toString();
                                    newCommandValue = commandValue.getText().toString();
                                    newCommandPort = commandPort.getText().toString();
                                    boolean isValueValid = valueInput.getError() == null;
                                    boolean isPortValid = portInput.getError() == null;
                                    if (isValueValid && isPortValid && !newCommandName.isEmpty() && !newCommandValue.isEmpty() && !newCommandPort.isEmpty()) {
                                        Command command = new Command(cid, newCommandName, newCommandValue, Integer.parseInt(newCommandPort));
                                        AppDatabase.getDatabase(view.getContext()).commandDao().updateCommand(command);
                                        dialog.dismiss();
                                        Snackbar.make(view, newCommandName + " Edited Position:" + position, Snackbar.LENGTH_LONG).show();
                                        refreshData();
                                    } else {
                                        Toast toast = Toast.makeText(view.getContext(), "Please check your input", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                }
                            });
                            // 输入检测
                            commandValue.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    Pattern hexPattern = Pattern.compile("(^[A-Fa-f0-9]+$|^$)");
                                    if (!hexPattern.matcher(s.toString()).matches()) {
                                        valueInput.setError("Hexadecimal Required");
                                    } else {
                                        valueInput.setError(null);
                                    }
                                }
                            });

                            commandPort.addTextChangedListener(new TextWatcher() {
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
                                        portInput.setError("Port must between 1 and 65535");
                                    } else {
                                        i = Integer.parseInt(s.toString());
                                        if (i >= 1 && i <= 65535) {
                                            portInput.setError(null);
                                        } else {
                                            portInput.setError("Port must between 1 and 65535");
                                        }
                                    }
                                }
                            });
                            return true;
                        } else if (itemId == R.id.commandoptiondelete) {
                            AppDatabase.getDatabase(view.getContext()).commandDao().deleteCommand(new Command(cid));
                            Snackbar.make(view, name + " deleted", BaseTransientBottomBar.LENGTH_LONG).show();
                            refreshData();
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return commands.size();
    }
}
