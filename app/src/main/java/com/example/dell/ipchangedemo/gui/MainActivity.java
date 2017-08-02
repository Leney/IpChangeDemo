package com.example.dell.ipchangedemo.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dell.ipchangedemo.R;
import com.example.dell.ipchangedemo.db.DeviceDBManager;
import com.example.dell.ipchangedemo.model.DeviceInfo;
import com.example.dell.ipchangedemo.thread.FullAdRequestTask;
import com.example.dell.ipchangedemo.thread.ThreadPoolManager;
import com.example.dell.ipchangedemo.tool.Constance;
import com.example.dell.ipchangedemo.tool.FileUtil;
import com.example.dell.ipchangedemo.tool.Tools;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

//    private TextView ip, ipBelongAddress;

    private MainReceiver receiver;

//    private Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            if (msg.what == 1) {
//                Bundle data = msg.getData();
//                ip.setText(data.getString("ip"));
////                ipBelongAddress.setText(data.getString("ip_belong"));
//            }
//            return false;
//        }
//    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 注册广播
        receiver = new MainReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constance.ACTION_GET_IP_ADDRESS_SUCCESS);
        registerReceiver(receiver, filter);

        Button closeBtn = (Button) findViewById(R.id.close_date_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean state = Tools.getMobileDataState(MainActivity.this, null);
                Log.i("llj", "关闭按钮，当前数据网络是否开启state----------->>>" + state);
                if (state) {
                    // 如果是开启  则关闭
                    Tools.setMobileData(MainActivity.this, false);
                }


            }
        });
        Button openBtn = (Button) findViewById(R.id.open_date_btn);
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean state = Tools.getMobileDataState(MainActivity.this, null);
//                Log.i("llj", "开启按钮，当前数据网络是否开启state----------->>>" + state);
//                if (!state) {
//                    // 如果是关闭  则开启
//                    Tools.setMobileData(MainActivity.this, true);
//
////                    DeviceInfo deviceInfo = DeviceDBManager.getInstance().queryDeviceInfo(20);
////                    Log.i("llj","deviceInfo == null-------->>>"+(deviceInfo == null));
////                    if(deviceInfo != null){
////                        Log.i("llj","查询出来的imei-------->>>"+deviceInfo.imei);
////                        Log.i("llj","查询出来的androidId-------->>>"+deviceInfo.androidId);
////                    }
//                }

                Tools.copyFile("/data/data/com.example.dell.ipchangedemo/databases/DeviceInfoDb");
            }
        });

        Button getCurIpAddressBtn = (Button) findViewById(R.id.load_device_infos);
        getCurIpAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 测试加载大数据量磁盘中的文件并写入到数据库中去
                String sdPath = FileUtil.getSDPath();
                final File file = new File(sdPath + File.separator + "ipChange" + File.separator + "50w_all.txt");
                if (file.exists()) {
                    Toast.makeText(MainActivity.this, "文件存在:" + file.getPath(), Toast.LENGTH_SHORT).show();

//                    testBufferedRandomAccessRead(file);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getDeviceData(file);

                            final int count = DeviceDBManager.getInstance().getDataCount();
                            Log.i("llj","加载本地设备文件完成 ,加载数据总条数为---->"+count);
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "加载本地设备文件完成 ,加载数据总条数为----> " + count, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).start();
                } else {
                    Toast.makeText(MainActivity.this, "文件不存在:" + sdPath, Toast.LENGTH_SHORT).show();
                }
            }
        });
//        Button beginRequestAd = (Button) findViewById(R.id.begin_request_ad);
//        beginRequestAd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                RequestAdTask requestAdTask = new RequestAdTask("03D01A68574D747518974C3B48FA86D9",3,false);
////                ThreadPoolManager.getInstance().addTask(requestAdTask);
//
////                DeviceInfo deviceInfo = DeviceDBManager.getInstance().queryDeviceInfo(3);
////                Log.i("llj", "deviceInfo == null-------->>>" + (deviceInfo == null));
////                if (deviceInfo != null) {
////                    Log.i("llj", "查询出来的imei-------->>>" + deviceInfo.imei);
////                    Log.i("llj", "查询出来的androidId-------->>>" + deviceInfo.androidId);
////                    Log.i("llj", "查询出来的mac-------->>>" + deviceInfo.mac);
////                    Log.i("llj", "查询出来的vendor-------->>>" + deviceInfo.vendor);
////                    Log.i("llj", "查询出来的model-------->>>" + deviceInfo.model);
////                }
//
//            }
//        });


//        ip = (TextView) findViewById(R.id.ip_address);
//        ipBelongAddress = (TextView) findViewById(R.id.ip_address_belong);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    //    private String result;

    //    private IPBean ip=new IPBean();
//    public void getMobileIP() {
//        new Thread() {
//            @Override
//            public void run() {
//                HttpURLConnection urlConnection = null;
//                try {
////                    URL url = new URL("http://ip.chinaz.com/getip.aspx");
//                    URL url = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
//                    urlConnection = (HttpURLConnection) url.openConnection();
//                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//                    result = readInStream(in);
//                    Log.i("llj", "result--------->>" + result);
//
//                    // 从反馈的结果中提取出IP地址
//                    int start = result.indexOf("{");
//                    int end = result.indexOf("}");
//                    String json = result.substring(start, end + 1);
//
//                    if (!result.isEmpty()) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(json);
////                            String ip = jsonObject.getString("ip");
////                            String address =  jsonObject.getString("address");
//                            String ip = jsonObject.optString("cip");
//                            Log.i("llj", "当前ip------->>" + ip);
////                            Log.i("llj","当前ip所在地址----->>>"+address);
//                            Message message = new Message();
//                            Bundle bundle = new Bundle();
//                            bundle.putString("ip", ip);
////                            bundle.putString("ip_belong",address);
//                            message.setData(bundle);
//                            message.what = 1;
//                            handler.sendMessage(message);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } catch (MalformedURLException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } finally {
//                    urlConnection.disconnect();
//                }
//            }
//        }.start();
//    }


//    private String readInStream(InputStream in) {
//        Scanner scanner = new Scanner(in).useDelimiter("\\A");
//        return scanner.hasNext() ? scanner.next() : "";
//    }


    private static final String ENCODING = "UTF-8";
    private static final int NUM = 500000;

//    /**
//     * 测试RandomAccessFile读取文件
//     */
//    public void testBufferedRandomAccessRead(File file) {
//        long start = System.currentTimeMillis();
//        long pos = 0L;
//        while (true) {
//            Map<String, Object> res = FileUtil.BufferedRandomAccessFileReadLine(file, ENCODING, pos, NUM);
//            Log.i("llj", "读取的数据条数------>>>>" + res.size());
//            // 如果返回结果为空结束循环
//            if (MapUtils.isEmpty(res)) {
//                break;
//            }
//            List<String> pins = (List<String>) res.get("pins");
//            if (CollectionUtils.isNotEmpty(pins)) {
////                logger.info(Arrays.toString(pins.toArray()));
//                if (pins.size() < NUM) {
//                    break;
//                }
//            } else {
//                break;
//            }
//            pos = (Long) res.get("pos");
//        }
//    }

    public void getDeviceData(File file) {
        long pos = 0L;
        while (true) {
            Map<String, Object> res = Tools.bufferedRandomAccessFileReadLineAndInsertToDB(file, ENCODING, pos, NUM);
            // 如果返回结果为空结束循环
            if (MapUtils.isEmpty(res)) {
                break;
            }
            List<String> pins = (List<String>) res.get("pins");
            if (CollectionUtils.isNotEmpty(pins)) {
//                logger.info(Arrays.toString(pins.toArray()));
                if (pins.size() < NUM) {
                    break;
                }
            } else {
                break;
            }
            pos = (Long) res.get("pos");
        }

        Log.i("llj","数据库中的数据总条数-------》》》"+DeviceDBManager.getInstance().getDataCount());
    }


//    Uri uri = Uri.parse("content://telephony/carriers/preferapn");
//
//    // 开启APN
//    public void openAPN() {
//        List<APN> list = getAPNList();
//        for (APN apn : list) {
//            ContentValues cv = new ContentValues();
//
//            // 获取及保存移动或联通手机卡的APN网络匹配
//            cv.put("apn", APNMatchTools.matchAPN(apn.apn));
//            cv.put("type", APNMatchTools.matchAPN(apn.type));
//
//            // 更新系统数据库，改变移动网络状态
//            getContentResolver().update(uri, cv, "_id=?", new String[]
//                    {
//                            apn.id
//                    });
//        }
//
//    }

//    // 关闭APN
//    public void closeAPN() {
//        List<APN> list = getAPNList();
//        for (APN apn : list) {
//            // 创建ContentValues保存数据
//            ContentValues cv = new ContentValues();
//            // 添加"close"匹配一个错误的APN，关闭网络
//            cv.put("apn", APNMatchTools.matchAPN(apn.apn) + "close");
//            cv.put("type", APNMatchTools.matchAPN(apn.type) + "close");
//
//            // 更新系统数据库，改变移动网络状态
//            getContentResolver().update(uri, cv, "_id=?", new String[]
//                    {
//                            apn.id
//                    });
//        }
//    }

//    public static class APN {
//        String id;
//
//        String apn;
//
//        String type;
//    }
//
//    private List<APN> getAPNList() {
//        // current不为空表示可以使用的APN
//        String projection[] =
//                {
//                        "_id, apn, type, current"
//                };
//        // 查询获取系统数据库的内容
//        Cursor cr = getContentResolver().query(uri, projection, null, null, null);
//
//        // 创建一个List集合
//        List<APN> list = new ArrayList<APN>();
//
//        while (cr != null && cr.moveToNext()) {
//
//            Log.d("ApnSwitch", "id" + cr.getString(cr.getColumnIndex("_id")) + " \n" + "apn"
//                    + cr.getString(cr.getColumnIndex("apn")) + "\n" + "type"
//                    + cr.getString(cr.getColumnIndex("type")) + "\n" + "current"
//                    + cr.getString(cr.getColumnIndex("current")));
//
//            APN a = new APN();
//
//            a.id = cr.getString(cr.getColumnIndex("_id"));
//            a.apn = cr.getString(cr.getColumnIndex("apn"));
//            a.type = cr.getString(cr.getColumnIndex("type"));
//            list.add(a);
//        }
//
//        if (cr != null)
//            cr.close();
//
//        return list;
//    }


    /**
     * 内部广播类
     */
    private class MainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, Constance.ACTION_GET_IP_ADDRESS_SUCCESS)) {
                // 获取ip地址成功
                Log.i("llj", "接收到获取ip地址成功的广播");
                //获取数据库中的数据总条数
                Constance.curDataCountFromDB = DeviceDBManager.getInstance().getDataCount();
                Log.i("llj", "当前数据库中的数据总条数为--------->>" + Constance.curDataCountFromDB);
                // 获取随机条数
                final int index = Tools.randomMinMax(1, Constance.curDataCountFromDB);
                Log.i("llj", "随机查询的index为--------->>" + index);

                DeviceInfo deviceInfo = DeviceDBManager.getInstance().queryDeviceInfo(index);
                if (deviceInfo == null) {
                    // 关闭数据网络  重新再通过监控网络状态  打开  再进行一次
                    Tools.setMobileData(context, false);
                    return;
                }

                if (StringUtils.isEmpty(deviceInfo.osVersion)) {
                    // 随机获取系统版本号
                    deviceInfo.osVersion = Constance.OS_VERSIONS[Tools.randomMinMax(0, Constance.OS_VERSIONS.length - 1)];
                }

                if (StringUtils.isEmpty(deviceInfo.density)) {
                    // 随机获取density
                    deviceInfo.density = Constance.DENSITY[Tools.randomMinMax(0, Constance.DENSITY.length - 1)];
                }

                if (StringUtils.isEmpty(deviceInfo.operator)) {
                    // 随机获取 运营商编号
                    deviceInfo.operator = Constance.OPERATORS[Tools.randomMinMax(0, Constance.OPERATORS.length - 1)];
                }

                if (StringUtils.isEmpty(deviceInfo.userAgent)) {
                    // 随机获取UserAgent 并替换掉里面的Android系统版本号
                    deviceInfo.userAgent = Constance.USER_AGENTS[Tools.randomMinMax(0, Constance.USER_AGENTS.length - 1)].replace("OS_VERSION", deviceInfo.osVersion);
                }

                if (deviceInfo.deviceScreenWidth == 0 || deviceInfo.deviceScreenHeight == 0) {
                    // 随机获取屏幕尺寸分辨率
                    int[] screen_ressolution = Constance.SCREEN_RESSOLUTION[Tools.randomMinMax(0, Constance.SCREEN_RESSOLUTION.length - 1)];
                    deviceInfo.deviceScreenWidth = screen_ressolution[0];
                    deviceInfo.deviceScreenHeight = screen_ressolution[1];
                }

                // 保存到当前使用的deviceInfo
                Constance.curDeviceInfo = deviceInfo;

                Log.i("llj", "查询出来的imei-------->>>" + Constance.curDeviceInfo.imei);
                Log.i("llj", "查询出来的androidId-------->>>" + Constance.curDeviceInfo.androidId);

                // 更新数据库中的此条设备信息
                DeviceDBManager.getInstance().update(Constance.curDeviceInfo, index);

                // 添加请求全屏广告的任务
                FullAdRequestTask task = new FullAdRequestTask("03D01A68574D747518974C3B48FA86D9", deviceInfo.deviceScreenWidth, deviceInfo.deviceScreenHeight);
                ThreadPoolManager.getInstance().addTask(task);

            }
        }
    }
}
