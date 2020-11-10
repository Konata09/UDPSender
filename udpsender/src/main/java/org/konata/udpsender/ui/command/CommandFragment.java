package org.konata.udpsender.ui.command;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.konata.udpsender.AppDatabase;
import org.konata.udpsender.R;
import org.konata.udpsender.entity.Command;
import org.konata.udpsender.util.ImportExport;
import org.konata.udpsender.util.Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class CommandFragment extends Fragment {

    private RecyclerView.Adapter mAdapter;
    private List<Command> commands;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_command, container, false);
        setHasOptionsMenu(true);
//        final List<Command> commands;

        // 命令列表
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.commandlistview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        commands = AppDatabase.getDatabase(getContext()).commandDao().getCommands();
        mAdapter = new CommandRecyclerViewAdapter(commands);
        recyclerView.setAdapter(mAdapter);

        // 增加按钮
        FloatingActionButton fab = v.findViewById(R.id.commandlistfab);
        fab.setOnClickListener(new View.OnClickListener() {
            private String newCommandName = "";
            private String newCommandValue = "";
            private String newCommandPort = "";

            @Override
            public void onClick(final View view) {
                final EditText commandName;
                final EditText commandValue;
                final EditText commandPort;
                final TextInputLayout valueInput;
                final TextInputLayout portInput;
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add New Command");

                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.command_add_dialog, (ViewGroup) getView(), false);
                commandName = (EditText) viewInflated.findViewById(R.id.commandaddname);
                commandValue = (EditText) viewInflated.findViewById(R.id.commandaddvalue);
                commandPort = (EditText) viewInflated.findViewById(R.id.commandaddport);
                valueInput = (TextInputLayout) viewInflated.findViewById(R.id.commandaddvalueinput);
                portInput = (TextInputLayout) viewInflated.findViewById(R.id.commandaddportinput);
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
                        newCommandName = commandName.getText().toString();
                        newCommandValue = commandValue.getText().toString();
                        newCommandPort = commandPort.getText().toString();
                        boolean isValueValid = valueInput.getError() == null;
                        boolean isPortVaild = portInput.getError() == null;

                        if (isValueValid && isPortVaild && !newCommandName.isEmpty() && !newCommandValue.isEmpty() && !newCommandPort.isEmpty()) {
                            Command command = new Command(newCommandName, newCommandValue, Integer.parseInt(newCommandPort));
                            dialog.dismiss();
                            AppDatabase.getDatabase(getContext()).commandDao().insertCommand(command);
                            Snackbar.make(v, newCommandName + " Added", Snackbar.LENGTH_LONG).show();
                            refreshData();
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
                            try {
                                i = Integer.parseInt(s.toString());
                                if (i >= 1 && i <= 65535) {
                                    portInput.setError(null);
                                } else {
                                    portInput.setError("Port must between 1 and 65535");
                                }
                            } catch (NumberFormatException e) {
                                portInput.setError("Port must between 1 and 65535");
                            }
                        }
                    }
                });
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.import_export, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_import:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/*");
                startActivityForResult(intent, 2);
                return true;
            case R.id.action_export:
                if (ImportExport.exportCommand(this.getContext())) {
                    Snackbar.make(getView(), "Export Successfully", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getView(), "Export Fail", Snackbar.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("Export", "Uri: " + uri.toString());
                if (ImportExport.importFile(uri, getActivity(), 1)) {
                    refreshData();
                    Snackbar.make(getView(), "Import Successfully", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getView(), "Import Fail", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

     void refreshData() {
        commands.clear();
        commands.addAll(AppDatabase.getDatabase(getView().getContext()).commandDao().getCommands());
        mAdapter.notifyDataSetChanged();
    }

}