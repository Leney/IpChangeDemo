package com.example.dell.ipchangedemo.model;

/**
 * Created by dell on 2017/5/13.
 * 广告请求所需的设备信息model
 */
public class DeviceInfo {
    public String imei;
    public String imsi;
    public String androidId;
    public String mac;
//    public String ip;
    public String model;
    /** 系统版本号*/
    public String osVersion;
    /** 屏幕密度*/
    public String density;
    /** 网络运营商*/
    public String operator;
    /** 浏览器user agent*/
    public String userAgent;
    /** 设备屏幕宽度*/
    public int deviceScreenWidth;
    /** 设备屏幕高度*/
    public int deviceScreenHeight;
    /** 设备生产商名称*/
    public String vendor;
    /** 联网类型(0—未知，
     1—Ethernet， 2—wifi，
     3—蜂窝网络，未知代，
     4—， 2G， 5—蜂窝网
     络， 3G， 6—蜂窝网络，
     4G)*/
    public int net;
    /** 横竖屏 0=竖屏，1=横屏*/
    public int orientation;
    /** 使用语言  zh-CN*/
    public String language;
}
