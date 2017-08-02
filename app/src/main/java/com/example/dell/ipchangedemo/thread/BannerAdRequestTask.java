package com.example.dell.ipchangedemo.thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.dell.ipchangedemo.IpChangeApplication;
import com.example.dell.ipchangedemo.model.AdInfo;
import com.example.dell.ipchangedemo.net.NetUtil;
import com.example.dell.ipchangedemo.tool.Constance;
import com.example.dell.ipchangedemo.tool.ParseUtil;
import com.example.dell.ipchangedemo.tool.Tools;

import org.json.JSONObject;

import static com.example.dell.ipchangedemo.tool.Tools.randomMinMax;

/**
 * Created by dell on 2017/5/15.
 * banner广告请求任务对象
 */
public class BannerAdRequestTask implements Runnable {

    /**
     * 广告位id
     */
    private String adunitid;
    /**
     * 执行多少次
     */
    private int executeTime = 1;
    /**
     * 广告展示的宽度
     */
    private int adWidth;

    /**
     * 广告展示的高度
     */
    private int adHeight;

    public BannerAdRequestTask(String adunitid, int maxExecuteTime, int adWidth, int adHeight) {
        this.adunitid = adunitid;
        this.adWidth = adWidth;
        this.adHeight = adHeight;
        // 随机获取需要执行多少次
        this.executeTime = randomMinMax(1, maxExecuteTime);
        Log.i("llj", "初始化请求任务时 此种广告需要请求的次数为--executeTime------->>>" + executeTime);
    }

    public BannerAdRequestTask() {

    }


    @Override
    public void run() {
        NetUtil.doRequestAds(adunitid, Constance.curDeviceInfo, Constance.curIpAddress, adWidth, adHeight, false, new NetUtil.OnAdsResponseListener() {
            @Override
            public void onLoadSuccess(JSONObject resultObject, int adWidth, int adHeight) {
                // 广告请求成功
                AdInfo adInfo = ParseUtil.getAdInfo(resultObject);
                if (adInfo == null) {
                    return;
                }
                // 上报展示数据成功   请求上报链接
                NetUtil.requestUrls(adInfo.getImprUrls());

                // 获取随机数
                int random = Tools.randomMinMax(1, Constance.curDataCountFromDB);

                int indexLength = String.valueOf(random).length();

                boolean isClick = indexLength == 5 || indexLength == 4;
                if (isClick) {
                    // 如果随机数的长度是 4 或者 5  则等一段时间进行点击上报

                    Log.i("llj", "休眠一段时间  准备上报点击url");
                    int sleepTimeRandom = Tools.randomMinMax(1000, 6000);
                    try {
                        // 休眠随机的时间
                        Thread.sleep(sleepTimeRandom);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 设置上报点击
                    Tools.doClick(adInfo, Constance.curDeviceInfo, adWidth, adHeight);
                }

                executeTime--;
                Log.e("llj", "减去执行任务次数之后的执行次数为executeTime------->>>" + executeTime);
                if (executeTime > 0) {
                    // 也要休眠一下  不然就很快速请求了下次广告信息
                    int sleepTimeRandom = Tools.randomMinMax(1000, 3358);
                    try {
                        // 休眠随机的时间
                        Thread.sleep(sleepTimeRandom);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 创建一个新的广告请求线程
                    BannerAdRequestTask requestAdTask = new BannerAdRequestTask();
                    requestAdTask.adunitid = adunitid;
                    requestAdTask.executeTime = executeTime;
                    requestAdTask.adWidth = adWidth;
                    requestAdTask.adHeight = adHeight;
                    // 添加一个新的广告请求任务到线程池中去
                    ThreadPoolManager.getInstance().addTask(requestAdTask);
                } else {
                    // 不用再去请求一次此类广告数据了
                    Log.e("llj", "banner广告类型的请求已经完毕了！！");
                    Constance.IS_BANNER_AD_DONE = true;

                    if (Constance.IS_FULL_AD_DONE && Constance.IS_BANNER_AD_DONE && Constance.IS_FLIP_AD_DONE && Constance.IS_INFO_AD_DONE) {
                        // 四种广告请求都已经完成  并且 相同类型的广告的多次请求也已经完成
                        if (isClick) {
                            // 如果有点击上报的  则等待2000ms
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e("llj", "所有广告请求都已完成、准备关闭网络  开始下一轮的广告请求！！");
                        Handler handler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                // 设置网络关闭
                                if (msg.what == 5) {
                                    Log.e("llj", "关闭网络!!!");
                                    Tools.setMobileData(IpChangeApplication.getInstance().getApplicationContext(), false);
                                    Tools.setWifiNet(IpChangeApplication.getInstance().getApplicationContext(), false);
                                    Constance.IS_DIS_CONNECT = false;
                                    Constance.IS_CONNECT = false;
                                }
                            }
                        };
                        handler.sendEmptyMessage(5);

                    }
                }
            }

            @Override
            public void onLoadFailed(String msg) {

            }

            @Override
            public void onNetError(String msg) {

            }
        });
    }
}
