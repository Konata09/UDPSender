package org.konata.udpsender.ui.command;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.konata.udpsender.R;

import java.util.List;
import java.util.regex.Pattern;

public class CommandRecyclerViewAdapter extends RecyclerView.Adapter<CommandRecyclerViewAdapter.MyViewHolder> {
    View myView;
    private List myItems;

    @NonNull
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.commanditem, parent, false);
        return new MyViewHolder(itemView);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView button;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.commanditemname);
            button = (TextView) view.findViewById(R.id.commandlistoptionbtn);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CommandRecyclerViewAdapter(List commands) {
        myItems = commands;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        myView = recyclerView;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final String name = (String) myItems.get(position);
        holder.title.setText(name);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(view.getContext(), holder.button);
                //inflating menu from xml resource
                popup.inflate(R.menu.commandoptionmenu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.commandoptionedit) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                            System.out.println("view:");
                            System.out.println(view); // androidx.appcompat.widget.AppCompatTextView{dd04441 VFED..C.. ........ 970,27-1080,106 #7f080074 app:id/commandlistoptionbtn}
                            System.out.println("myView:");
                            System.out.println(myView); // androidx.recyclerview.widget.RecyclerView{39a59bc VFED..... .F...... 0,0-1080,1704 #7f080075 app:id/commandlistview}
                            System.out.println("view.getContext:");
                            System.out.println(view.getContext()); // org.konata.udpsender.MainActivity@d40d71b

                            builder.setTitle("Edit Command");
                            // I'm using fragment here so I'm using getView() to provide ViewGroup
                            // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                            View viewInflated = LayoutInflater.from(view.getContext()).inflate(R.layout.command_add_dialog, (ViewGroup) myView, false);
                            final EditText commandName = (EditText) viewInflated.findViewById(R.id.commandaddname);
                            final EditText commandValue = (EditText) viewInflated.findViewById(R.id.commandaddvalue);
                            commandName.setText(name);
                            commandValue.setText("FFFF");
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
                                    String newCommandName = "";
                                    String newCommandValue = "";
                                    newCommandName = commandName.getText().toString();
                                    newCommandValue = commandValue.getText().toString();
                                    boolean isValueValid = valueInput.getError() == null;
                                    if (isValueValid && !newCommandName.isEmpty() && !newCommandValue.isEmpty()) {
                                        Snackbar.make(view, newCommandName + " Edited Position:" + position, Snackbar.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast toast = Toast.makeText(view.getContext(), "Please check your input", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER,0,0);
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
                            return true;
                        } else if (itemId == R.id.commandoptiondelete) {
                            Snackbar.make(view, name + " deleted", BaseTransientBottomBar.LENGTH_LONG).setAction("UNDO", new CommandItemDeleteUndoListener()).show();
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    public class CommandItemDeleteUndoListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "undo", Toast.LENGTH_SHORT).show();
            // Code to undo the user's last action
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myItems.size();
    }
}
