package com.example.dell.ipchangedemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.dell.ipchangedemo.model.DeviceInfo;

import java.util.List;

/**
 * Created by dell on 2017/5/13.
 * 设备数据库操作管理类
 */
public class DeviceDBManager {

    private static volatile DeviceDBManager instance = null;
    private DBHelp dbHelp;

    private DeviceDBManager() {
    }


    public void init(Context context) {
        dbHelp = new DBHelp(context.getApplicationContext());
    }

    public static DeviceDBManager getInstance() {
        if (instance == null) {
            synchronized (DeviceDBManager.class) {
                if (instance == null) {
                    instance = new DeviceDBManager();
                }
            }
        }
        return instance;
    }

    /**
     * 开启事务，大批量插入数据
     *
     * @param list 需要插入的集合数据
     * @return
     */
    public boolean insertListDataBySql(List<DeviceInfo> list) {
        if (list.isEmpty()) {
            return false;
        }
        SQLiteDatabase db = null;
        try {
            db = dbHelp.getWritableDatabase();
            String sql = "insert into " + DBHelp.Columns.TABLE_NAME + "("
                    + DBHelp.Columns.IMEI + ","
                    + DBHelp.Columns.IMSI + ","
                    + DBHelp.Columns.ANDROID_ID + ","
                    + DBHelp.Columns.MAC_ADDRESS + ","
                    + DBHelp.Columns.MODEL + ","
                    + DBHelp.Columns.OS_VERSION + ","
                    + DBHelp.Columns.DENSITY + ","
                    + DBHelp.Columns.OPERATOR + ","
                    + DBHelp.Columns.USER_AGENT + ","
                    + DBHelp.Columns.DEVICE_SCREEN_WIDTH + ","
                    + DBHelp.Columns.DEVICE_SCREEN_HEIGHT + ","
                    + DBHelp.Columns.VENDOR + ","
                    + DBHelp.Columns.NET + ","
                    + DBHelp.Columns.ORIENTATION + ","
                    + DBHelp.Columns.LANGUAGE
                    + ") " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

//            String sql = "insert into " + DBHelp.Columns.TABLE_NAME + "("
//                    + DBHelp.Columns.IMEI + ","
//                    + DBHelp.Columns.ANDROID_ID + ") " + "values(?,?)";

            SQLiteStatement stat = db.compileStatement(sql);
            db.beginTransaction();
            for (DeviceInfo deviceInfo : list) {
//                stat.bindString(1, deviceInfo.imei);
//                stat.bindString(2, deviceInfo.androidId);
                stat.bindString(1, deviceInfo.imei);
                stat.bindString(2, deviceInfo.imsi);
                stat.bindString(3, deviceInfo.androidId);
                stat.bindString(4, deviceInfo.mac);
                stat.bindString(5, deviceInfo.model);
                stat.bindString(6, deviceInfo.osVersion);
                stat.bindString(7, deviceInfo.density);
                stat.bindString(8, deviceInfo.operator);
                stat.bindString(9, deviceInfo.userAgent);
                stat.bindLong(10, deviceInfo.deviceScreenWidth);
                stat.bindLong(11, deviceInfo.deviceScreenHeight);
                stat.bindString(12, deviceInfo.vendor);
                stat.bindLong(13, deviceInfo.net);
                stat.bindLong(14, deviceInfo.orientation);
                stat.bindString(15, deviceInfo.language);
                long result = stat.executeInsert();
                if (result < 0) {
                    return false;
                }
            }
            db.setTransactionSuccessful();
            Log.i("llj", "插入到数据库中完成！！！！");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 更新软件管理表中的某条数据
     *
     * @param deviceInfo
     * @return
     */
    public int update(DeviceInfo deviceInfo,int index)
    {
        SQLiteDatabase db = dbHelp.getWritableDatabase();
        int count = db.update(DBHelp.Columns.TABLE_NAME, getContentValues(deviceInfo),BaseColumns._ID + "= " + index,null);
        Log.i("llj","更新数据是否成功？？？count----->>"+count);
        db.close();
        return count;
    }

    /**
     * 根据id查找数据库中的一条Device数据
     *
     * @return
     */
    public DeviceInfo queryDeviceInfo(int id) {
        SQLiteDatabase db = dbHelp.getReadableDatabase();
        DeviceInfo deviceInfo = null;
        Cursor cursor = db.query(DBHelp.Columns.TABLE_NAME, null,
                DBHelp.Columns._ID + "=" + id, null, null, null, null);
        while (cursor.moveToNext()) {
            deviceInfo = getDeviceInfo(cursor);
        }
        db.close();
        return deviceInfo;
    }


    /**
     * 随机查询一条数据
     *
     * @return
     */
    public DeviceInfo queryDeviceInfoByRandom() {
        SQLiteDatabase db = dbHelp.getReadableDatabase();
        DeviceInfo deviceInfo = null;
        Cursor cursor = db.query(DBHelp.Columns.TABLE_NAME, null, null, null, null, null, "RANDOM()", "1");
        while (cursor.moveToNext()) {
            deviceInfo = getDeviceInfo(cursor);
        }
        cursor.close();
        db.close();
        return deviceInfo;
    }


//    public void getAllData() {
//        String sql = "Select " + BaseColumns._ID + " from " + DBHelp.Columns.TABLE_NAME + ";";
//        SQLiteDatabase db = dbHelp.getReadableDatabase();
//        Cursor cursor = db.rawQuery(sql,null);
//        if(cursor.moveToNext()){
//            Log.i("llj","第一条_id的值为-------->>>"+cursor.getInt(0));
//        }
//    }

    /**
     * 获取当前数据库中的数据总条数
     *
     * @return
     */
    public int getDataCount() {
        SQLiteDatabase db = dbHelp.getReadableDatabase();
        String sql = "Select count(" + BaseColumns._ID + ") from " + DBHelp.Columns.TABLE_NAME + ";";
//        String sql = "SELECT * FROM "+ DBHelp.Columns.TABLE_NAME+" ORDER BY "+ BaseColumns._ID+" DESC LIMIT 1";
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     * 从查询结果中获取出一条数据
     *
     * @param cursor
     * @return 一条数据 DeviceInfo
     * <p>
     * + DBHelp.Columns.LANGUAGE
     */
    private DeviceInfo getDeviceInfo(Cursor cursor) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.imei = cursor.getString(cursor.getColumnIndex(DBHelp.Columns.IMEI));
        deviceInfo.imsi = cursor.getString(cursor.getColumnIndex(DBHelp.Columns.IMSI));
        deviceInfo.androidId = cursor.getString(cursor.getColumnIndex(DBHelp.Columns.ANDROID_ID));
        deviceInfo.mac = cursor.getString(cursor.getColumnIndex(DBHelp.Columns.MAC_ADDRESS));
//        deviceInfo.ip = cursor.getString(cursor.getColumnIndex(DBHelp.Columns.IP_ADDRESS));
        deviceInfo.model = cursor.getString(cursor.getColumnIndex(DBHelp.Columns.MODEL));
        deviceInfo.osVersion = cursor.getString(cursor.getColumnIndex(DBHelp.Columns.OS_VERSION));
        deviceInfo.density = cursor.getString(cursor.getColumnIndex(DBHelp.Columns.DENSITY));
        deviceInfo.operator = cursor.getString(cursor.getColumnIndex(DBHelp.Columns.OPERATOR));
        deviceInfo.userAgent = cursor.getString(cursor.getColumnIndex(DBHelp.Columns.USER_AGENT));
//        deviceInfo.adWidth = cursor.getString(cursor.getColumnIndex(DBHelp.Columns.AD_WIDTH));
//        deviceInfo.adHeight = cursor.getString(cursor.getColumnIndex(DBHelp.Columns.AD_HEIGHT));
        deviceInfo.deviceScreenWidth = cursor.getInt(cursor.getColumnIndex(DBHelp.Columns.DEVICE_SCREEN_WIDTH));
        deviceInfo.deviceScreenHeight = cursor.getInt(cursor.getColumnIndex(DBHelp.Columns.DEVICE_SCREEN_HEIGHT));
        deviceInfo.vendor = cursor.getString(cursor.getColumnIndex(DBHelp.Columns.VENDOR));
        deviceInfo.net = cursor.getInt(cursor.getColumnIndex(DBHelp.Columns.NET));
        deviceInfo.orientation = cursor.getInt(cursor.getColumnIndex(DBHelp.Columns.ORIENTATION));
        deviceInfo.language = cursor.getString(cursor.getColumnIndex(DBHelp.Columns.LANGUAGE));
        return deviceInfo;
    }

    /**
     * 将AppInfo对象转换成ContentValues对象
     * @param deviceInfo
     * @return
     */
    private ContentValues getContentValues(DeviceInfo deviceInfo)
    {
        ContentValues values = new ContentValues();
        values.put(DBHelp.Columns.IMEI,deviceInfo.imei);
        values.put(DBHelp.Columns.IMSI,deviceInfo.imsi);
        values.put(DBHelp.Columns.ANDROID_ID,deviceInfo.androidId);
        values.put(DBHelp.Columns.MAC_ADDRESS,deviceInfo.mac);
        values.put(DBHelp.Columns.MODEL,deviceInfo.model);
        values.put(DBHelp.Columns.OS_VERSION,deviceInfo.osVersion);
        values.put(DBHelp.Columns.DENSITY,deviceInfo.density);
        values.put(DBHelp.Columns.OPERATOR,deviceInfo.operator);
        values.put(DBHelp.Columns.USER_AGENT,deviceInfo.userAgent);
        values.put(DBHelp.Columns.DEVICE_SCREEN_WIDTH,deviceInfo.deviceScreenWidth);
        values.put(DBHelp.Columns.DEVICE_SCREEN_HEIGHT,deviceInfo.deviceScreenHeight);
        values.put(DBHelp.Columns.VENDOR,deviceInfo.vendor);
        values.put(DBHelp.Columns.NET,deviceInfo.net);
        values.put(DBHelp.Columns.ORIENTATION,deviceInfo.orientation);
        values.put(DBHelp.Columns.LANGUAGE,deviceInfo.language);
        return values;
    }
}
