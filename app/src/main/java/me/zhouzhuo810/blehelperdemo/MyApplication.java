package me.zhouzhuo810.blehelperdemo;

import android.app.Application;

import com.clj.fastble.BleManager;
import com.clj.fastble.scan.BleScanRuleConfig;

import me.zhouzhuo810.magpie.utils.BaseUtil;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化工具类
        BaseUtil.init(this);
        //初始化蓝牙
        BleManager.getInstance().init(this);
        //初始化蓝牙配置
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000);

        //蓝牙扫描配置
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setAutoConnect(true)
                .setScanTimeOut(10000)
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }
}
