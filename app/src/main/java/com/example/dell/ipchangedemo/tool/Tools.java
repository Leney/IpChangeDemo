package com.example.dell.ipchangedemo.tool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.example.dell.ipchangedemo.db.DeviceDBManager;
import com.example.dell.ipchangedemo.model.AdInfo;
import com.example.dell.ipchangedemo.model.DeviceInfo;
import com.example.dell.ipchangedemo.net.NetUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dell on 2017/5/13.
 * 工具类
 */

public class Tools {
    /**
     * 设置手机的移动数据
     */
    public static void setMobileData(Context pContext, boolean pBoolean) {

        try {

            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = new Class[1];
            argsClass[0] = boolean.class;

            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);

            method.invoke(mConnectivityManager, pBoolean);

            Log.i("llj", "设置数据网络状态完成！！！");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("llj", "移动数据设置错误:", e);
//            System.out.println("移动数据设置错误: " + e.toString());
        }
    }

    /**
     * 返回手机移动数据的状态
     *
     * @param pContext
     * @param arg      默认填null
     * @return true 连接 false 未连接
     */
    public static boolean getMobileDataState(Context pContext, Object[] arg) {

        try {

            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = null;
            if (arg != null) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }

            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);

            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);

            return isOpen;

        } catch (Exception e) {
            // TODO: handle exception

            System.out.println("得到移动数据状态出错");
            return false;
        }

    }


    /**
     * WIFI网络开关
     *
     * @param enabled
     * @return 设置是否success
     */
    public static boolean setWifiNet(Context context, boolean enabled) {
        WifiManager wm = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        return wm.setWifiEnabled(enabled);

    }


    /**
     * 通过BufferedRandomAccessFile读取文件,并插入到数据库中去
     *
     * @param file     源文件
     * @param encoding 文件编码
     * @param pos      偏移量
     * @param num      读取量
     * @return pins文件内容，pos当前偏移量
     */
    public static Map<String, Object> bufferedRandomAccessFileReadLineAndInsertToDB(File file, String encoding, long pos, int num) {
        Map<String, Object> res = Maps.newHashMap();
//        List<String> pins = Lists.newArrayList();
        List<DeviceInfo> deviceInfos = Lists.newArrayList();
        res.put("pins", deviceInfos);
        BufferedRandomAccessFile reader = null;
        try {
            reader = new BufferedRandomAccessFile(file, "r");
            reader.seek(pos);
            for (int i = 0; i < num; i++) {
                String pin = reader.readLine();
//                Log.i("llj", "一行数据pin---->>>" + pin);
//                if(StringUtils.isEmpty(getBettweenStr(pin,"imei:","end"))){
//                    break;
//                }
//                // 添加到集合中去
//                deviceInfos.add(getDeviceInfoByRead(pin));
                if (StringUtils.isBlank(pin)) {
                    break;
                }
                String line = new String(pin.getBytes("8859_1"), encoding);
                Log.i("llj", "line---"+i+"----->>>>" + line);
//                pins.add(test);
                deviceInfos.add(getDeviceInfoByRead(line));

                if (i % 10000 == 0) {
                    // 每10000条数据开始往数据库中写
                    Log.i("llj", "有10000条了开始插入数据到数据库中去");
                    // 批量插入到数据库中去
                    DeviceDBManager.getInstance().insertListDataBySql(deviceInfos);
                    deviceInfos.clear();
                }
            }
            res.put("pos", reader.getFilePointer());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
        }

        Log.i("llj", "读取文件完成！！");
        if (!deviceInfos.isEmpty()) {
            Log.i("llj", "开始插入数据到数据库中去");
            // 批量插入到数据库中去
            DeviceDBManager.getInstance().insertListDataBySql(deviceInfos);
        }
        return res;
    }


    /**
     * 根据从文件中读取出来的一行数据得到deviceInfo信息
     * 可以在这里设置一些默认值、随机值等
     * ="imei_start:"&A1&"imei_end;"
     * ="android_id_start:"&B1&"android_id_end;"
     *
     * @param source
     * @return
     */
    private static DeviceInfo getDeviceInfoByRead(String source) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.imei = getBettweenStr(source, "imei_start:", "imei_end;");
        deviceInfo.imsi = "";
        deviceInfo.androidId = getBettweenStr(source, "android_id_start:", "android_id_end;");
        deviceInfo.mac = getBettweenStr(source, "mac_start:", "mac_end;");
//        deviceInfo.ip = "";
        deviceInfo.model = getBettweenStr(source, "model_start:", "model_end;");
        deviceInfo.vendor = getBettweenStr(source, "vendor_start:", "vendor_end;");


        deviceInfo.osVersion = "";
        deviceInfo.density = "";
        deviceInfo.operator = "";
        deviceInfo.userAgent = "";
//        deviceInfo.adWidth = "";
//        deviceInfo.adHeight = "";
        deviceInfo.deviceScreenWidth = 0;
        deviceInfo.deviceScreenHeight = 0;


        // 设定死都为wifi网络
        deviceInfo.net = 2;
        // 设定死都为竖屏
        deviceInfo.orientation = 0;
        // 设定死都为中文语言
        deviceInfo.language = "zh-CN";

        return deviceInfo;
    }


    /**
     * 获取两个字符串之间的数据
     *
     * @param source
     * @param start
     * @param end
     * @return
     */
    private static String getBettweenStr(String source, String start, String end) {
        Pattern p = Pattern.compile(start + "(.*?)" + end);
        Matcher m = p.matcher(source);
        if (!m.hitEnd() && m.find()) {
            return m.group(1);
        }
        return "";
    }


    /**
     * 获取当前网络ip地址是否成功的监听器
     */
    public interface OnResponseIpListener {
        void onSuccess(String ip);

        void onFailed();
    }

    /**
     * 取当前网络的外网ip地址
     */
    public static void getMobileIP(final OnResponseIpListener listener) {
        if (listener == null) {
            return;
        }
        NetUtil.requestUrl("http://pv.sohu.com/cityjson?ie=utf-8", new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                // 获取ip地址失败
                listener.onFailed();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                // 获取ip地址成功
                if (response.code() == 200) {
                    // 响应成功
                    String result = response.body().string();
                    int start = result.indexOf("{");
                    int end = result.indexOf("}");
                    String json = result.substring(start, end + 1);
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String ip = jsonObject.optString("cip");
                        if (!TextUtils.isEmpty(ip)) {
                            listener.onSuccess(ip);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    listener.onFailed();
                }
            }
        });
    }


    /**
     * 获取数值之间的随机数
     *
     * @param min
     * @param max
     * @return
     */
    public static int randomMinMax(int min, int max) {
        if (min >= max) {
            return min;
        }
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }


    /**
     * 普通广告点击事件处理
     *
     * @param adInfo
     */
    public static void doClick(final AdInfo adInfo, DeviceInfo deviceInfo, int adWidth, int adHeight) {
        if (adInfo.isClick() || adInfo.getClickUrls() == null) {
            return;
        }
        // 有clickUrl需要上报
        final int length = adInfo.getClickUrls().length;
        for (int i = 0; i < length; i++) {
            // 先组每组的点击url
            String url = Tools.replaceAdClickUrl(adInfo.getClickUrls()[i], deviceInfo, adWidth, adHeight);
            adInfo.getClickUrls()[i] = url;
        }

        NetUtil.requestUrls(adInfo.getClickUrls());
        // 设置为已点击
        adInfo.setClick(true);
    }


    /**
     * 替换科大讯飞中的click_url
     *
     * @param url
     * @return
     */
    public static String replaceAdClickUrl(String url, DeviceInfo deviceInfo, int adWidth, int adHeight) {
        if (deviceInfo == null) {
            return url;
        }
        float downX = adWidth * 0.635f;
        float downY = adHeight * 0.685f;
        float upX = downX - 0.26f;
        float upY = downY - 0.26f;
        url = url.replace("IT_CLK_PNT_DOWN_X", String.valueOf(downX));
        url = url.replace("IT_CLK_PNT_DOWN_Y", String.valueOf(downY));
        url = url.replace("IT_CLK_PNT_UP_X", String.valueOf(upX));
        url = url.replace("IT_CLK_PNT_UP_Y", String.valueOf(upY));
        return url;
    }


    /**
     * 模拟Imei
     *
     * @return
     */
    private static String simulationImsi() {
        // 460022535025034
        String title = "4600";
        int second = 0;
        do {
            second = new java.util.Random().nextInt(8);
        } while (second == 4);
        int r1 = 10000 + new java.util.Random().nextInt(90000);
        int r2 = 10000 + new java.util.Random().nextInt(90000);
        return title + "" + second + "" + r1 + "" + r2;
    }

    /**
     * 模拟Mac地址
     *
     * @return
     */
    public static String simulationMac() {
        char[] char1 = "abcdef".toCharArray();
        char[] char2 = "0123456789".toCharArray();
        StringBuffer mBuffer = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            int t = new java.util.Random().nextInt(char1.length);
            int y = new java.util.Random().nextInt(char2.length);
            int key = new java.util.Random().nextInt(2);
            if (key == 0) {
                mBuffer.append(char2[y]).append(char1[t]);
            } else {
                mBuffer.append(char1[t]).append(char2[y]);
            }

            if (i != 5) {
                mBuffer.append(":");
            }
        }
        return mBuffer.toString();
    }

    /**
     * 模拟imei
     *
     * @return
     */
    public static String simulationImei() {// calculator IMEI
        int r1 = 1000000 + new java.util.Random().nextInt(9000000);
        int r2 = 1000000 + new java.util.Random().nextInt(9000000);
        String input = r1 + "" + r2;
        char[] ch = input.toCharArray();
        int a = 0, b = 0;
        for (int i = 0; i < ch.length; i++) {
            int tt = Integer.parseInt(ch[i] + "");
            if (i % 2 == 0) {
                a = a + tt;
            } else {
                int temp = tt * 2;
                b = b + temp / 10 + temp % 10;
            }
        }
        int last = (a + b) % 10;
        if (last == 0) {
            last = 0;
        } else {
            last = 10 - last;
        }
        return input + last;
    }


    /**
     * 复制文件到sdcard
     * @param oldFile
     */
    public static void copyFile(String oldFile) {
        File f = new File(oldFile); //比如  "/data/data/com.hello/databases/test.db"

        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File o = new File(sdcardPath + "/copy.db"); //sdcard上的目标地址

        if (f.exists()) {

            FileChannel outF;

            try {

                outF = new FileOutputStream(o).getChannel();
                Log.i("llj","开始复制！！！");
                new FileInputStream(f).getChannel().transferTo(0, f.length(), outF);

            } catch (FileNotFoundException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }

            Log.i("llj","复制成功！！！");
        }else {
            Log.i("llj","需要复制的文件不存在！！！");
        }
    }
}
