package com.example.dell.ipchangedemo.net;

import android.util.Log;

import com.example.dell.ipchangedemo.IpChangeApplication;
import com.example.dell.ipchangedemo.model.DeviceInfo;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by dell on 2017/5/14.
 * 网络请求工具类
 */

public class NetUtil {


    /**
     * 科大讯飞的AppId
     */
    private static final String KDXF_APP_ID = "58d9d690";

    /**
     * 应用名称
     */
    private static final String APP_NAME = "爱玩商店";

    /**
     * 应用名称
     */
    private static final String APP_PACKAGE_NAME = "com.xd.leplay.store";

    /**
     * 只是请求一个链接
     *
     * @param url
     */
    public static void requestUrl(String url, Callback callback) {
        RequestBody body = RequestBody.create(
                MediaType.parse("text/x-markdown; charset=utf-8"), "");
        Request request = new Request.Builder().url(url).post(body).addHeader
                ("X-protocol-ver", "2.0").build();
//        Request request = new Request.Builder().url(url).post(body).build();
        IpChangeApplication.getOkHttpClient().newCall(request)
                .enqueue(callback);
    }

    /**
     * 只是请求一个链接
     *
     * @param url
     */
    public static void requestUrlGet(String url, Callback callback) {
        RequestBody body = RequestBody.create(
                MediaType.parse("text/x-markdown; charset=utf-8"), "");
        Request request = new Request.Builder().url(url).get().addHeader
                ("X-protocol-ver", "2.0").build();
//        Request request = new Request.Builder().url(url).post(body).build();
        IpChangeApplication.getOkHttpClient().newCall(request)
                .enqueue(callback);
    }

    /**
     * 一次需要请求多个url链接
     *
     * @param urls
     */
    public static void requestUrls(String[] urls) {
        if (urls == null) {
            return;
        }
        final int length = urls.length;
        Log.i("llj", "requestUrls,总长度---length--->>>" + length);
        for (int i = 0; i < length; i++) {
            NetUtil.requestUrl(urls[i], new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e("llj", "请求多个链接有失败的情况！！！");
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    Log.e("llj", "请求多个链接单个成功了！！！");
                }
            });
        }
    }


    /**
     * 科大讯飞广告api请求方法
     *
     * @param listener
     * @return
     */
    public static void doRequestAds(String adunitid, DeviceInfo deviceInfo, String ip, final int adWidth, final int adHeight, boolean isBoot, final OnAdsResponseListener listener) {

        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("tramaterialtype", "json");

            // 广告位id
            requestBodyJson.put("adunitid", adunitid);
            // 是否支持deepLink
//            requestBodyJson.put("is_support_deeplink",0);
            // 广告位宽度
            requestBodyJson.put("adw", adWidth);
            // 广告位高度
            requestBodyJson.put("adh", adHeight);

            // 是否支持deepLink 0=不支持，1=支持
            requestBodyJson.put("is_support_deeplink", 1);
            // 设备类型 -1=未知，0=phone,1=pad,2=pc,3=tv, 4=wap
            requestBodyJson.put("devicetype", 0);
            // 操作系统类型
            requestBodyJson.put("os", "Android");
            // 操作系统版本号
            requestBodyJson.put("osv", deviceInfo.osVersion);
            requestBodyJson.put("adid", deviceInfo.androidId);
            requestBodyJson.put("imei", deviceInfo.imei);
            requestBodyJson.put("mac", deviceInfo.mac);
            requestBodyJson.put("density", deviceInfo.density);
            requestBodyJson.put("operator", deviceInfo.operator);
            // userAgent
            requestBodyJson.put("ua", deviceInfo.userAgent);
            requestBodyJson.put("ts", System.currentTimeMillis());
            // 设备屏幕宽度
            requestBodyJson.put("dvw", deviceInfo.deviceScreenWidth);
            // 设备屏幕高度
            requestBodyJson.put("dvh", deviceInfo.deviceScreenHeight);
            // 横竖屏 0=竖屏，1=横屏
            requestBodyJson.put("orientation", deviceInfo.orientation);
            // 设备生产商
            requestBodyJson.put("vendor", deviceInfo.vendor);
            // 设备型号
            requestBodyJson.put("model", deviceInfo.model);
            requestBodyJson.put("net", deviceInfo.net);
            requestBodyJson.put("ip", ip);
            // 使用语言
            requestBodyJson.put("lan", deviceInfo.language);

            // 是否开屏 1=开屏，0=非开屏
            requestBodyJson.put("isboot", isBoot ? 1 : 0);
            // 请求批量下发广告的数量，目前只能为”1”
            requestBodyJson.put("batch_cnt", "1");
            // appId 和讯飞后台保持一致
            requestBodyJson.put("appid", KDXF_APP_ID);
            // app名称 和讯飞后台保持一致
            requestBodyJson.put("appname", APP_NAME);
            // app包名 和讯飞后台保持一致
            requestBodyJson.put("pkgname", APP_PACKAGE_NAME);

//            JSONObject debugObject = new JSONObject();
//            //用于指定下发广告的交互类型，取值范围： 0，不限制；1，跳转类； 2，下载类。不指定的话，按值为 0 处理
//            debugObject.put("action_type", 0);
//            // 用于指定下发广告的落地页类型，
//            // 0=不限制,1=包含landing_url和deep_link,2=仅包含landing_url,3=仅包含deep_link,默认0
//            debugObject.put("landing_type", 3);
//            requestBodyJson.put("debug", debugObject);

            Log.i("llj", "请求参数--->>>" + requestBodyJson.toString());

        } catch (Exception e) {
            Log.e(TAG, "请求广告数据出现异常#Exception\n", e);
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("text/x-markdown; charset=utf-8"),
                requestBodyJson.toString().getBytes());

        Request request = new Request.Builder().url("http://ws.voiceads.cn/ad/request").post(body).addHeader
                ("X-protocol-ver", "2.0").build();

        IpChangeApplication.getOkHttpClient().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onResponse(final Response response)
                            throws IOException {
                        Log.i("llj", "返回响应码-------->>" + response.code());
                        String result = response.body().string();
                        Log.i("llj", "返回结果------>>>" + result);
                        if (response.code() == 200) {
                            // 服务端数据有变化
                            try {
                                JSONObject resultObject = new JSONObject(result);
                                if (resultObject.getInt("rc") == 70200) {
                                    // 请求广告成功、下发广告成功
                                    listener.onLoadSuccess(resultObject,adWidth,adHeight);
                                } else {
                                    // 连接到服务器成功，但出现一些错误，下发广告失败
                                    listener.onLoadFailed(result);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "返回成功之后，解析数据出现异常#exception:\n", e);
                            }
                        } else {
                            Log.e(TAG,
                                    "服务器报错，无法响应，返回码---->>>" + response.code());
                            // 前端自己构造一个RspPacket对象 返回给子类
                            String[] requestTag = (String[]) response.request()
                                    .tag();
                            // 这里rspPacket返回的actions 是请求TAG
                            listener.onLoadFailed(result);
                        }
                    }

                    @Override
                    public void onFailure(final Request request, IOException e) {
                        // 访问服务器出错，有可能是路径有问题也有可能是网络连接异常
                        Log.e(TAG, "访问服务器出错：" + request.toString());
                        listener.onNetError("网络异常");
                    }
                });
    }


    public interface OnAdsResponseListener {
        /**
         * 加载成功(异步方法)
         */
        void onLoadSuccess(JSONObject resultObject,int adWidth,int adHeight);

        /**
         * 加载失败(异步方法)
         */
        void onLoadFailed(String msg);

        /**
         * 网络错误(异步方法)
         */
        void onNetError(String msg);
    }
}
