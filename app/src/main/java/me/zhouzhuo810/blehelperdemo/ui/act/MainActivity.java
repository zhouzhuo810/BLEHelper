package me.zhouzhuo810.blehelperdemo.ui.act;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import me.zhouzhuo810.blehelperdemo.R;
import me.zhouzhuo810.magpie.ui.act.BaseActivity;
import me.zhouzhuo810.magpie.ui.dialog.TwoBtnTextDialog;

public class MainActivity extends BaseActivity {

    private SwitchCompat swBt;
    private SwitchCompat swGps;
    private SwitchCompat swLocation;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public boolean shouldSupportMultiLanguage() {
        return false;
    }

    @Override
    public void initView(@Nullable Bundle bundle) {
        swBt = findViewById(R.id.sw_bt);
        swGps = findViewById(R.id.sw_gps);
        swLocation = findViewById(R.id.sw_location);
    }

    @Override
    public void initData() {
        checkFunction();

    }

    private void checkFunction() {
        swGps.setChecked(checkGPSIsOpen());
        swBt.setChecked(BleManager.getInstance().isBlueEnable());
        swLocation.setChecked(AndPermission.hasPermissions(this, Permission.ACCESS_COARSE_LOCATION));
    }

    @Override
    public void initEvent() {
        findViewById(R.id.btn_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BleManager.getInstance().isBlueEnable()) {
                    showTwoBtnTextDialog("温馨提示", "请打开蓝牙", false, new TwoBtnTextDialog.OnTwoBtnTextClick() {
                        @Override
                        public void onLeftClick(TextView textView) {
                        }

                        @Override
                        public void onRightClick(TextView textView) {
                            BleManager.getInstance().enableBluetooth();
                        }
                    });
                    return;
                }

                if (!checkGPSIsOpen()) {
                    showTwoBtnTextDialog("温馨提示", "请打开GPS", false, new TwoBtnTextDialog.OnTwoBtnTextClick() {
                        @Override
                        public void onLeftClick(TextView textView) {
                        }

                        @Override
                        public void onRightClick(TextView textView) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0x01);
                        }
                    });
                    return;
                }

                startSearch();

            }
        });

        swBt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!BleManager.getInstance().isBlueEnable()) {
                        BleManager.getInstance().enableBluetooth();
                    }
                } else {
                    if (BleManager.getInstance().isBlueEnable()) {
                        BleManager.getInstance().disableBluetooth();
                    }
                }
            }
        });

        swGps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 0x01);
                }
            }
        });

        swLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AndPermission.with(MainActivity.this).runtime().setting().start();
                }
            }
        });
    }

    private void startSearch() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        startAct(BLEListActivity.class);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        closeAct();
                    }
                })
                .start();
    }

    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }


    @Override
    protected void onResume() {
        super.onResume();
        swBt.setChecked(BleManager.getInstance().isBlueEnable());
        swLocation.setChecked(AndPermission.hasPermissions(this, Permission.ACCESS_COARSE_LOCATION));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x01) {
            checkFunction();
        }
    }
}
