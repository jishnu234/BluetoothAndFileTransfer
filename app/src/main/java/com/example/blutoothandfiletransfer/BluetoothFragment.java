package com.example.blutoothandfiletransfer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.blutoothandfiletransfer.databinding.FragmentBluetoothBinding;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothFragment extends Fragment implements OnToggledListener {

    private FragmentBluetoothBinding binding;
    private BluetoothAdapter bluetoothAdapter;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_ID = 0;
    private static final int REQUEST_DISCOVERABLE_ID = 1;
    private List<String> devicesList;
    private ArrayAdapter<String> listAdapter;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBluetoothBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        devicesList = new ArrayList<>();
        listAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, devicesList);
        binding.pairedList.setAdapter(listAdapter);

        binding.statusButton.setOnToggledListener(this);
        binding.pairedBox.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                if (isChecked) {
                    if(!checkEnabled()) {
                        Toast.makeText(context, "Gps disabled", Toast.LENGTH_SHORT).show();
                        binding.pairedBox.setChecked(false);
                    }
                    getPairedDevicesDetails();
                } else {
                    devicesList.clear();
                    listAdapter.notifyDataSetChanged();
                }
            }
        });

        if (bluetoothAdapter != null) {
            if (checkEnabled()) {
                binding.statusButton.setOn(true);
                getPairedDevicesDetails();
                binding.pairedBox.setChecked(true);
            }
//            else showBluetoothToast();

//            checkDiscoverable()
        } else Log.d(TAG, "onCreate: Adapter not initialized");
    }

    @Override
    public void onSwitched(ToggleableView toggleableView, boolean isOn) {
        if (toggleableView == binding.statusButton) {
            if (isOn) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_ID);
            } else {
                binding.pairedBox.setChecked(false);
                bluetoothAdapter.disable();
                Toast.makeText(context, "Bluetooth disabling...", Toast.LENGTH_SHORT).show();
            }
//        } else if (toggleableView == binding.discSwitch) {
//            if (isOn) {
//                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(intent, REQUEST_DISCOVERABLE_ID);
//            } else {
//                bluetoothAdapter.cancelDiscovery();
//                binding.discSwitch.setOn(true);
//                Toast.makeText(this, "bluetooth discovery cancelling...", Toast.LENGTH_SHORT).show();
//            }
        } else {
            Log.d(TAG, "onSwitched: wrong switch view captured");
        }
    }

    private boolean checkEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    private void getPairedDevicesDetails() {
        devicesList.clear();

        if (checkEnabled()) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                devicesList.add(device.getName());
            }
            if (devicesList.size() == 0) {
                devicesList.add("No paired devices Found");
                Toast.makeText(context, "No paired devices Found", Toast.LENGTH_SHORT).show();
            }
            listAdapter.notifyDataSetChanged();
        }
    }
}