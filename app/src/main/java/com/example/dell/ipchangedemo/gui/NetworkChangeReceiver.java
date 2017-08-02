package com.example.dell.ipchangedemo.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.dell.ipchangedemo.IpChangeApplication;
import com.example.dell.ipchangedemo.net.NetUtil;
import com.example.dell.ipchangedemo.tool.Constance;
import com.example.dell.ipchangedemo.tool.Tools;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by dell on 2017/5/14.
 * 网络状态切换监听广播类
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    private int time = 0;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("llj", "网络状态改变广播action-------->>>" + action);

        NetworkInfo.State wifiState = null;
        NetworkInfo.State mobileState = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (wifiState != null && mobileState != null
                && NetworkInfo.State.CONNECTED != wifiState
                && NetworkInfo.State.CONNECTED == mobileState) {
            // 手机网络连接成功
            Log.i("llj", "手机联网成功！！！ time----->>" + time);

            NetUtil.requestUrlGet("http://xd.livevvv.com/kdxf/ip", new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    // 获取ip地址失败
                    Log.e("llj", "记录ip地址失败！！！");
                    if (Tools.getMobileDataState(context, null)) {
                        // 再去关闭网络
                        Tools.setMobileData(IpChangeApplication.getInstance().getApplicationContext(), false);
                        Constance.IS_DIS_CONNECT = false;
                        Constance.IS_CONNECT = false;
                    } else {
                        // 再去打开网络
                        Tools.setMobileData(IpChangeApplication.getInstance().getApplicationContext(), true);
                        Constance.IS_DIS_CONNECT = false;
                        Constance.IS_CONNECT = false;
                    }
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    // 获取ip地址成功
                    Log.i("llj", "记录ip地址响应成功返回 response.code()----->>>" + response.code());
                    if (response.code() == 200) {
                        // 响应成功
                        Log.i("llj", "记录ip地址成功,并关闭数据网络！！！response.body()----->>>" + response.body().string());
                        time++;
                        if (time > 10000) {
                            return;
                        }
                        // 再去关闭网络
                        Tools.setMobileData(IpChangeApplication.getInstance().getApplicationContext(), false);
                        Constance.IS_DIS_CONNECT = false;
                    }
                }
            });
        } else if (wifiState != null && mobileState != null
                && NetworkInfo.State.CONNECTED != wifiState
                && NetworkInfo.State.CONNECTED != mobileState) {
            // 手机没有任何的网络
            Log.i("llj", "接收到没有任何网络的广播！！Constance.IS_DIS_CONNECT-------->>" + Constance.IS_DIS_CONNECT);
            if (Constance.IS_DIS_CONNECT) {
                return;
            }
            Log.e("llj", "接收到网络已经关闭的广播，当前手机为未联网状态！！！！");
            // 设置为未联网的状态
            Constance.IS_DIS_CONNECT = true;
            Constance.IS_CONNECT = false;
            // 设置所有的广告请求状态为未完成的状态
            Constance.IS_FULL_AD_DONE = false;
            Constance.IS_BANNER_AD_DONE = false;
            Constance.IS_FLIP_AD_DONE = false;
            Constance.IS_INFO_AD_DONE = false;

            Log.e("llj", "去打开数据网络！！");
            // 再去打开网络
            Tools.setMobileData(IpChangeApplication.getInstance().getApplicationContext(), true);
//            // TODO 之后会不要这个方法   不能打开wifi网络
//            Tools.setWifiNet(IpChangeApplication.getInstance().getApplicationContext(),true);
        } else if (wifiState != null && NetworkInfo.State.CONNECTED == wifiState) {
            // 无线网络连接成功
            Log.i("llj", "接收到wifi网络链接成功的广播！！Constance.IS_DIS_CONNECT-------->>" + Constance.IS_DIS_CONNECT);
            if (Constance.IS_CONNECT) {
                return;
            }
            Constance.IS_CONNECT = true;
            Constance.IS_DIS_CONNECT = false;
            // 设置所有的广告请求状态为未完成的状态
            Constance.IS_FULL_AD_DONE = false;
            Constance.IS_BANNER_AD_DONE = false;
            Constance.IS_FLIP_AD_DONE = false;
            Constance.IS_INFO_AD_DONE = false;
            // 获取当前网络ip地址
            Tools.getMobileIP(new Tools.OnResponseIpListener() {
                @Override
                public void onSuccess(String ip) {
                    // 设置当前保存的ip地址
                    Constance.curIpAddress = ip;
                    Log.i("llj", "获取到的当前ip是-------->>>" + Constance.curIpAddress);
                    // 发送获取ip地址成功的广播
                    context.sendBroadcast(new Intent(Constance.ACTION_GET_IP_ADDRESS_SUCCESS));
                }

                @Override
                public void onFailed() {
                    Log.e("llj", "获取ip地址失败！！！");

                    if (Tools.getMobileDataState(context, null)) {
                        // 再去关闭网络
                        Tools.setMobileData(IpChangeApplication.getInstance().getApplicationContext(), false);
                        Constance.IS_DIS_CONNECT = false;
                        Constance.IS_CONNECT = false;
                    } else {
                        // 再去打开网络
                        Tools.setMobileData(IpChangeApplication.getInstance().getApplicationContext(), true);
                        Constance.IS_DIS_CONNECT = false;
                        Constance.IS_CONNECT = false;
                    }

                }
            });
        }

//        if(StringUtils.equals(action,"android.ne.conn.CONNECTIVITY_CHANGE")){
//            // 网络状态发生变化
//            boolean state = Tools.getMobileDataState(context,null);
//            Log.i("llj","网络状态发生变化,当前网络状态是否开启------>>"+state);
//            if(state){
//                // 当前网络打开了,处理请求广告数据等一系列操作
//
//                // 获取当前网络ip地址
//                Tools.getMobileIP(new Tools.OnResponseIpListener() {
//                    @Override
//                    public void onSuccess(String ip) {
//                        // 设置当前保存的ip地址
//                        Constance.curIpAddress = ip;
//                        // 发送获取ip地址成功的广播
//                        context.sendBroadcast(new Intent(Constance.ACTION_GET_IP_ADDRESS_SUCCESS));
//                    }
//
//                    @Override
//                    public void onFailed() {
//                        Log.e("llj","获取ip地址失败！！！");
//                    }
//                });
//
//            }else {
//                // 当前网络关闭，再次去打开  更换ip地址
//                Tools.setMobileData(context,true);
//            }
//        }
    }
}
