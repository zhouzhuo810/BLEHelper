package me.zhouzhuo810.blehelperdemo.ui.adapter;

import android.content.Context;
import android.view.View;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

import me.zhouzhuo810.blehelperdemo.R;
import me.zhouzhuo810.magpie.ui.adapter.RvBaseAdapter;

public class BleDeviceAdapter extends RvBaseAdapter<BleDevice> {
    public BleDeviceAdapter(Context context, List<BleDevice> data) {
        super(context, data);
    }


    public void addDevice(BleDevice bleDevice) {
        if (data == null) {
            data = new ArrayList<>();
        }
        removeDevice(bleDevice);
        data.add(bleDevice);
    }

    public void removeDevice(BleDevice bleDevice) {
        if (data == null) {
            data = new ArrayList<>();
        }
        for (int i = 0; i < data.size(); i++) {
            BleDevice device = data.get(i);
            if (bleDevice.getKey().equals(device.getKey())) {
                data.remove(i);
            }
        }
    }

    public void clearConnectedDevice() {
        for (int i = 0; i < data.size(); i++) {
            BleDevice device = data.get(i);
            if (BleManager.getInstance().isConnected(device)) {
                data.remove(i);
            }
        }
    }

    public void clearScanDevice() {
        if (data == null) {
            data = new ArrayList<>();
        }
        for (int i = 0; i < data.size(); i++) {
            BleDevice device = data.get(i);
            if (!BleManager.getInstance().isConnected(device)) {
                data.remove(i);
            }
        }
    }

    public void clear() {
        clearConnectedDevice();
        clearScanDevice();
    }

    @Override
    protected int getLayoutId(int i) {
        return R.layout.adapter_device;
    }

    @Override
    protected void fillData(ViewHolder viewHolder, final BleDevice bleDevice, int i) {
        if (bleDevice != null) {
            boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
            String name = bleDevice.getName();
            String mac = bleDevice.getMac();
            int rssi = bleDevice.getRssi();
            viewHolder.setText(R.id.txt_name, name);
            viewHolder.setText(R.id.txt_mac, mac);
            viewHolder.setText(R.id.txt_rssi, String.valueOf(rssi));
            if (isConnected) {
                viewHolder.setImageResource(R.id.img_blue, R.drawable.ic_bluetooth_black_24dp);
                viewHolder.setTextColor(R.id.txt_name, 0xFF1DE9B6);
                viewHolder.setTextColor(R.id.txt_mac, 0xFF1DE9B6);
                viewHolder.setGone(R.id.layout_idle, true);
                viewHolder.setVisible(R.id.layout_connected, true);
            } else {
                viewHolder.setImageResource(R.id.img_blue, R.drawable.ic_bluetooth_searching_black_24dp);
                viewHolder.setTextColor(R.id.txt_name, 0xFF000000);
                viewHolder.setTextColor(R.id.txt_mac, 0xFF000000);
                viewHolder.setVisible(R.id.layout_idle, true);
                viewHolder.setGone(R.id.layout_connected, true);
            }
            viewHolder.setOnClickListener(R.id.btn_connect, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onConnect(bleDevice);
                    }
                }
            });
            viewHolder.setOnClickListener(R.id.btn_disconnect, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onDisConnect(bleDevice);
                    }
                }
            });
            viewHolder.setOnClickListener(R.id.btn_detail, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onDetail(bleDevice);
                    }
                }
            });

        }

    }


    public interface OnDeviceClickListener {
        void onConnect(BleDevice bleDevice);

        void onDisConnect(BleDevice bleDevice);

        void onDetail(BleDevice bleDevice);
    }

    private OnDeviceClickListener mListener;

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.mListener = listener;
    }
}
