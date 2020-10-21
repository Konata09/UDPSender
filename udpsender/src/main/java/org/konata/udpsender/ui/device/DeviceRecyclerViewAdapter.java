package org.konata.udpsender.ui.device;

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

import org.konata.udpsender.R;

import java.util.List;
import java.util.regex.Pattern;

public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.MyViewHolder> {
    View myView;
    private List myItems;

    @NonNull
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.deviceitem, parent, false);
        return new MyViewHolder(itemView);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView deviceName;
        public TextView deviceIp;
        public TextView deviceMac;
        public TextView button;

        public MyViewHolder(View view) {
            super(view);
            deviceName = (TextView) view.findViewById(R.id.deviceitemname);
            deviceIp = (TextView) view.findViewById(R.id.deviceitemip);
            deviceMac = (TextView) view.findViewById(R.id.deviceitemac);
            button = (TextView) view.findViewById(R.id.devicelistoptionbtn);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DeviceRecyclerViewAdapter(List devices) {
        myItems = devices;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        myView = recyclerView;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // TODO：使用 Object 替换 List
        final String name = (String) myItems.get(position);
        holder.deviceName.setText(name);
        final String mac = "FF:FF:FF:FF:FF:FF";
        final String ip = "192.168.255.254";

        // Item 菜单
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
                        int itemId = item.getItemId(); // 用户点击的菜单 id
                        if (itemId == R.id.commandoptionedit) {
                            // 弹出编辑对话框
                            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                            System.out.println("view:");
                            System.out.println(view); // androidx.appcompat.widget.AppCompatTextView{dd04441 VFED..C.. ........ 970,27-1080,106 #7f080074 app:id/commandlistoptionbtn}
                            System.out.println("myView:");
                            System.out.println(myView); // androidx.recyclerview.widget.RecyclerView{39a59bc VFED..... .F...... 0,0-1080,1704 #7f080075 app:id/commandlistview}
                            System.out.println("view.getContext:");
                            System.out.println(view.getContext()); // org.konata.udpsender.MainActivity@d40d71b

                            builder.setTitle("Edit Device");
                            // I'm using fragment here so I'm using getView() to provide ViewGroup
                            // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                            View viewInflated = LayoutInflater.from(view.getContext()).inflate(R.layout.device_add_dialog, (ViewGroup) myView, false);
                            final EditText deviceName = (EditText) viewInflated.findViewById(R.id.deviceaddname);
                            final EditText deviceIp = (EditText) viewInflated.findViewById(R.id.deviceaddip);
                            final EditText deviceMac = (EditText) viewInflated.findViewById(R.id.deviceaddmac);
                            deviceName.setText(name);
                            deviceIp.setText(ip);
                            deviceMac.setText(mac);
                            final TextInputLayout ipInput = (TextInputLayout) viewInflated.findViewById(R.id.deviceaddipinput);
                            final TextInputLayout macInput = (TextInputLayout) viewInflated.findViewById(R.id.deviceaddmacinput);
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

                            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String newDeviceName = "";
                                    String newDeviceIp = "";
                                    String newDeviceMac = "";
                                    newDeviceName = deviceName.getText().toString();
                                    newDeviceIp = deviceIp.getText().toString();
                                    newDeviceMac = deviceMac.getText().toString();
                                    boolean isIpValid = ipInput.getError() == null;
                                    boolean isMacValid = macInput.getError() == null;
                                    if (!newDeviceName.isEmpty() && ((!newDeviceIp.isEmpty()) && isIpValid) || (!newDeviceMac.isEmpty() && isMacValid)) {
                                        Snackbar.make(view, newDeviceName + " Edited Position:" + position, Snackbar.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast toast = Toast.makeText(view.getContext(), "Please check your input", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                }
                            });
                            deviceIp.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    Pattern ipPattern = Pattern.compile("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}$");
                                    if (!ipPattern.matcher(s.toString()).matches()) {
                                        ipInput.setError("invalid IP address");
                                    } else {
                                        ipInput.setError(null);
                                    }
                                }
                            });
                            deviceMac.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    Pattern macPattern = Pattern.compile("^([0-9A-Fa-f]{2}([:-]?|$)){6}$");
                                    if (!macPattern.matcher(s.toString()).matches()) {
                                        macInput.setError("invalid MAC address");
                                    } else {
                                        macInput.setError(null);
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
