package me.zhouzhuo810.blehelperdemo.ui.act;

import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import me.zhouzhuo810.blehelperdemo.R;
import me.zhouzhuo810.blehelperdemo.ui.adapter.BleDeviceAdapter;
import me.zhouzhuo810.magpie.ui.act.BaseActivity;
import me.zhouzhuo810.magpie.ui.widget.MarkView;
import me.zhouzhuo810.magpie.ui.widget.TitleBar;
import me.zhouzhuo810.magpie.utils.ToastUtil;

public class BLEListActivity extends BaseActivity {

    private SmartRefreshLayout refreshLayout;
    private RecyclerView rv;
    private BleDeviceAdapter adapter;
    private TitleBar titleBar;

    @Override
    public int getLayoutId() {
        return R.layout.activity_ble;
    }

    @Override
    public boolean shouldSupportMultiLanguage() {
        return false;
    }

    @Override
    public void initView(@Nullable Bundle bundle) {
        titleBar = findViewById(R.id.title_bar);
        refreshLayout = findViewById(R.id.refresh_layout);
        rv = findViewById(R.id.rv);
    }

    @Override
    public void initData() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BleDeviceAdapter(this, null);
        rv.setAdapter(adapter);

    }

    @Override
    public void initEvent() {
        titleBar.setOnTitleClickListener(new TitleBar.OnTitleClick() {
            @Override
            public void onLeftClick(ImageView imageView, MarkView markView, TextView textView) {
                closeAct();
            }

            @Override
            public void onTitleClick(TextView textView) {

            }

            @Override
            public void onRightClick(ImageView imageView, MarkView markView, TextView textView) {

            }
        });

        adapter.setOnDeviceClickListener(new BleDeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onConnect(BleDevice bleDevice) {
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().cancelScan();
                    connect(bleDevice);
                }
            }

            @Override
            public void onDisConnect(final BleDevice bleDevice) {
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().disconnect(bleDevice);
                }
            }

            @Override
            public void onDetail(BleDevice bleDevice) {
                if (BleManager.getInstance().isConnected(bleDevice)) {
//                    Intent intent = new Intent(BLEListActivity.this, OperationActivity.class);
//                    intent.putExtra(OperationActivity.KEY_DATA, bleDevice);
//                    startActivity(intent);
                }
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                startScan();
            }
        });
        refreshLayout.autoRefresh();
    }


    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                showLoadingDialog("连接中...");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                hideLoadingDialog();
                ToastUtil.showShortToast("连接失败");
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                hideLoadingDialog();
                adapter.addDevice(bleDevice);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                hideLoadingDialog();

                adapter.removeDevice(bleDevice);
                adapter.notifyDataSetChanged();

                if (isActiveDisConnected) {
                    ToastUtil.showShortToast("已断开连接");
                } else {
                    ToastUtil.showShortToast("已断开连接");
//                    ObserverManager.getInstance().notifyObserver(bleDevice);
                }

            }
        });
    }


    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                adapter.clearScanDevice();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                Log.e("TTT", bleDevice.getMac());
                adapter.addDevice(bleDevice);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                refreshLayout.finishRefresh();
            }
        });
    }
}
