package com.example.dell.ipchangedemo.thread;

import android.util.Log;

import com.example.dell.ipchangedemo.model.AdInfo;
import com.example.dell.ipchangedemo.net.NetUtil;
import com.example.dell.ipchangedemo.tool.Constance;
import com.example.dell.ipchangedemo.tool.ParseUtil;
import com.example.dell.ipchangedemo.tool.Tools;

import org.json.JSONObject;

import static com.example.dell.ipchangedemo.tool.Tools.randomMinMax;

/**
 * Created by dell on 2017/5/15.
 * <p>
 * 请求广告任务线程对象
 */
public class RequestAdTask implements Runnable {

    /**
     * 广告位id
     */
    private String adunitid;
    /**
     * 是否是开屏广告
     */
    private boolean isBoot;
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

    public RequestAdTask(String adunitid, int maxExecuteTime, int adWidth, int adHeight, boolean isBoot) {
        this.adunitid = adunitid;
        this.isBoot = isBoot;
        this.adWidth = adWidth;
        this.adHeight = adHeight;
        if (isBoot) {
            // 是开屏广告 则只执行一次
            this.executeTime = 1;
        } else {
            // 随机获取需要执行多少次
            this.executeTime = randomMinMax(1, maxExecuteTime);
        }
        Log.i("llj","初始化请求任务时 此种广告需要请求的次数为--executeTime------->>>"+executeTime);
    }

    public RequestAdTask() {

    }


    @Override
    public void run() {
        NetUtil.doRequestAds(adunitid, Constance.curDeviceInfo, Constance.curIpAddress, adWidth, adHeight, isBoot, new NetUtil.OnAdsResponseListener() {
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
//                if (indexLength == 5 || indexLength == 4) {
                    // 如果随机数的长度是 4 或者 5  则等一段时间进行点击上报

                    Log.i("llj","休眠一段时间  准备上报点击url");
                    int sleepTimeRandom = Tools.randomMinMax(1000, 6000);
                    try {
                        // 休眠随机的时间
                        Thread.sleep(sleepTimeRandom);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 设置上报点击
                    Tools.doClick(adInfo, Constance.curDeviceInfo, adWidth, adHeight);
//                }

                executeTime--;

                Log.e("llj","减去执行任务次数之后的执行次数为executeTime------->>>"+executeTime);
                if (executeTime > 0) {
                    // 创建一个新的广告请求线程
                    RequestAdTask requestAdTask = new RequestAdTask();
                    requestAdTask.adunitid = adunitid;
                    requestAdTask.executeTime = executeTime;
                    requestAdTask.isBoot = isBoot;

                    // 添加一个新的广告请求任务到线程池中去
                    ThreadPoolManager.getInstance().addTask(requestAdTask);
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
