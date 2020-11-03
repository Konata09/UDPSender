package org.konata.udpsender.ui.command;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.konata.udpsender.AppDatabase;
import org.konata.udpsender.R;
import org.konata.udpsender.entity.Command;

import java.util.List;
import java.util.regex.Pattern;

public class CommandFragment extends Fragment {
    private RecyclerView.Adapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_command, container, false);

        // 命令列表
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.commandlistview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        List<Command> commands = AppDatabase.getDatabase(getContext()).commandDao().getCommands();

        mAdapter = new CommandRecyclerViewAdapter(commands);
        recyclerView.setAdapter(mAdapter);

        // 增加按钮
        FloatingActionButton fab = v.findViewById(R.id.commandlistfab);
        fab.setOnClickListener(new View.OnClickListener() {
            private String newCommandName = "";
            private String newCommandValue = "";

            @Override
            public void onClick(final View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add New Command");
                // I'm using fragment here so I'm using getView() to provide ViewGroup
                // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.command_add_dialog, (ViewGroup) getView(), false);
                final EditText commandName = (EditText) viewInflated.findViewById(R.id.commandaddname);
                final EditText commandValue = (EditText) viewInflated.findViewById(R.id.commandaddvalue);
                final TextInputLayout valueInput = (TextInputLayout) viewInflated.findViewById(R.id.commandaddvalueinput);
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
                        newCommandName = commandName.getText().toString();
                        newCommandValue = commandValue.getText().toString();
                        boolean isValueValid = valueInput.getError() == null;
                        if (isValueValid && !newCommandName.isEmpty() && !newCommandValue.isEmpty()) {
                            Command command = new Command(newCommandName, newCommandValue);
                            AppDatabase.getDatabase(getContext()).commandDao().insertCommand(command);
                            dialog.dismiss();
                            Snackbar.make(view, newCommandName + " Added", Snackbar.LENGTH_LONG).show();
                        } else {
                            Toast toast = Toast.makeText(view.getContext(), "Please check your input", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });
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
            }
        });
        return v;
    }
}