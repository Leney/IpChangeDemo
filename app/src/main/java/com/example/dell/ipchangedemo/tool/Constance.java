package com.example.dell.ipchangedemo.tool;

import com.example.dell.ipchangedemo.model.DeviceInfo;

/**
 * Created by dell on 2017/5/14.
 * 常量保存类
 */

public class Constance {

    /** 为了保证切换成联网状态后  不接收到多次广播*/
    public static boolean IS_CONNECT = false;

    /** 为了保证切换成无网络状态后  不接收到多次广播*/
    public static boolean IS_DIS_CONNECT = false;

    /** 标识全屏广告是否请求完毕*/
    public static boolean IS_FULL_AD_DONE = false;

    /** 标识Banner广告是否请求完毕*/
    public static boolean IS_BANNER_AD_DONE = false;

    /** 标识弹窗广告是否请求完毕*/
    public static boolean IS_FLIP_AD_DONE = false;

    /** 标识信息流广告是否请求完毕*/
    public static boolean IS_INFO_AD_DONE = false;

    /**
     * 当前网络的ip地址
     */
    public static String curIpAddress;

    /**
     * 当前请求广告的设备信息对象
     */
    public static DeviceInfo curDeviceInfo;

    /**
     * 当前数据库中数据的总条数
     */
    public static int curDataCountFromDB;


    /**
     * 获取ip地址成功的广播
     */
    public static final String ACTION_GET_IP_ADDRESS_SUCCESS = "com.example.dell.ipchangedemo.ACTION_GET_IP_ADDRESS_SUCCESS";

    /**
     * 整个一轮请求广告的动作都已经完成的广播
     */
    public static final String ACTION_REQUEST_ADS_DONE = "com.example.dell.ipchangedemo.ACTION_REQUEST_ADS_DONE";


    /**
     * Android 系统版本
     */
    public static final String[] OS_VERSIONS = {"2.2.1", "2.2.2", "2.2.3", "2.2.4", "2.3.3", "2.3.4", "2.3.7", "4.0.3", "4.0.4", "4.1.1", "4.1.2", "4.1.3", "4.1.4", "4.1.5", "4.2.1", "4.2.2", "4.2.3", "4.2.4", "4.3.1", "4.3.2", "4.3.3", "4.4.1", "4.4.2", "4.4.3", "4.4.4", "5.0.1", "5.0.2", "5.0.3", "5.1.1", "5.1.2", "5.1.3", "6.0.1", "6.0.2", "6.0.3", "7.0.1", "7.0.2", "7.1.1"};

    /**
     * Android 屏幕density
     */
    public static final String[] DENSITY = {"0.75", "1.0", "1.5", "2.0", "2.5", "3.0", "4.0"};

    /**
     * 运营商的编号  移动最多，其次联通，最后电信
     */
    public static final String[] OPERATORS = {"46000", "46000", "46000", "46001", "46001", "46003"};

    /**
     * UserAgent
     */
    public static final String[] USER_AGENTS = {
            "Mozilla/5.0 (Linux; U; Android OS_VERSION; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1"
            , "MQQBrowser/26 Mozilla/5.0 (Linux; U; Android OS_VERSION; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1"
            , "JUC (Linux; U; OS_VERSION; zh-cn; MB200; 320*480) UCWEB7.9.3.103/139/999"
            , "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:7.0a1) Gecko/20110623 Firefox/7.0a1 Fennec/7.0a1"
            , "Opera/9.80 (Android OS_VERSION; Linux; Opera Mobi/build-1107180945; U; en-GB) Presto/2.8.149 Version/11.10"};

    /**
     * 常见手机屏幕分辨率 主流屏幕分辨率会多一些
     */
    public static final int[][] SCREEN_RESSOLUTION = {new int[]{240, 320}, new int[]{320, 480}, new int[]{480, 800}, new int[]{480, 800}, new int[]{480, 854}, new int[]{540, 960}, new int[]{720, 1280}, new int[]{720, 1280}, new int[]{720, 1280}, new int[]{1080, 1920}, new int[]{1080, 1920}, new int[]{1080, 1920}};
}
