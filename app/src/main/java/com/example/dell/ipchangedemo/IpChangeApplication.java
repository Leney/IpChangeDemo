package com.example.dell.ipchangedemo;

import android.app.Application;

import com.example.dell.ipchangedemo.db.DeviceDBManager;
import com.squareup.okhttp.OkHttpClient;


/**
 * Created by dell on 2017/5/14.
 */

public class IpChangeApplication extends Application {

    private static volatile OkHttpClient okHttpClient;

    private static IpChangeApplication instance;

    public  static IpChangeApplication getInstance(){
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化数据库管理器类
        DeviceDBManager.getInstance().init(this);
        instance = this;
    }

    /***
     * 获取httpClient对象
     *
     * @return
     */
    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (OkHttpClient.class){
                if(okHttpClient == null){
                    okHttpClient = new OkHttpClient();
                }
            }
        }
        return okHttpClient;
    }
}
