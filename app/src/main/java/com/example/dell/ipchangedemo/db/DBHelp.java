package com.example.dell.ipchangedemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBHelp extends SQLiteOpenHelper {

    private static final String TAG = "DBHelp";

    // 数据库名称
    private static final String DATABASE_NAME = "DeviceInfoDb";

    // 数据库版本
    private static final int DATABASE_VERSION = 2;
    /**
     * 创建软件管理表的sql语句
     */
    private static final String CREATE_SOFTWARES_TABLE = "create table if not exists "
            + Columns.TABLE_NAME
            + " ("
            + Columns._ID
            + " integer primary key autoincrement, "
            + Columns.IMEI
            + " text, "
            + Columns.IMSI
            + " text, "
            + Columns.ANDROID_ID
            + " text, "
            + Columns.MAC_ADDRESS
//            + " text,"
//            + Columns.IP_ADDRESS
            + " text,"
            + Columns.MODEL
            + " text,"
            + Columns.OS_VERSION
            + " text,"
            + Columns.DENSITY
            + " text,"
            + Columns.OPERATOR
            + " text,"
            + Columns.USER_AGENT
            + " text,"
            + Columns.NET
            + " integer,"
            + Columns.ORIENTATION
            + " integer,"
            + Columns.LANGUAGE
            + " text,"
//			 + Columns.SAME_SIGN
//			 + " bit,"
//			 + Columns.IS_INLAY
//			 + " bit,"
//			 + Columns.IS_SHOW_INSTALLED_LIST
//			 + " bit,"
//            + Columns.AD_WIDTH
//            + " text,"
//            + Columns.AD_HEIGHT
//            + " text,"
            + Columns.DEVICE_SCREEN_WIDTH
            + " integer,"
            + Columns.DEVICE_SCREEN_HEIGHT
            + " integer,"
            + Columns.VENDOR
            + " text);";

//    /**
//     * 创建软件管理表的sql语句
//     */
//    private static final String CREATE_SOFTWARES_TABLE = "create table if not exists "
//            + Columns.TABLE_NAME
//            + " ("
//            + Columns._ID
//            + " integer primary key autoincrement, "
//            + Columns.IMEI
//            + " text, "
//            + Columns.ANDROID_ID
//            + " text);";

    /**
     * 删除软件管理表的sql语句
     */
    private static final String DELETE_SOFTWARE_TABLE = "drop table "
            + Columns.TABLE_NAME;

    // /** 创建更新软件数量的表 */
    // private static final String CREATE_UPDATE_COUNT_TABLE =
    // "create table if not exists "
    // + "miss_infos "
    // + "("
    // + Columns._ID
    // + " integer primary key autoincrement," + " miss integer);";

//    /**
//     * 删除更新软件数量的表sql语句
//     */
//    private static final String DELETE_UPDATE_COUNT_TABLE = "drop table miss_infos;";

    public DBHelp(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SOFTWARES_TABLE);
        // db.execSQL(CREATE_UPDATE_COUNT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_SOFTWARE_TABLE);
        db.execSQL(CREATE_SOFTWARES_TABLE);

//        try {
//            db.execSQL(DELETE_UPDATE_COUNT_TABLE);
//        } catch (Exception e) {
//            Log.i("llj", "要删除的表不存在！！");
//            Log.e(TAG, "onUpgrade()#exception", e);
//        }
        // DLog.i("lilijun", "创建了更新数量表！！");
        // db.execSQL(CREATE_UPDATE_COUNT_TABLE);

        Log.i(TAG, "oldVersion=" + oldVersion + ", newVersion=" + newVersion);
    }

    public static class Columns implements BaseColumns {
        /**
         * 表名称
         */
        public static final String TABLE_NAME = "device_tab";
        public static final String IMEI = "imei";
        public static final String IMSI = "imsi";
        public static final String ANDROID_ID = "android_id";
        public static final String MAC_ADDRESS = "mac_address";
//        public static final String IP_ADDRESS = "ip_address";
        /**
         * 机型
         */
        public static final String MODEL = "model";
        /**
         * 操作系统版本号
         */
        public static final String OS_VERSION = "os_version";
        /**
         * 屏幕密度
         */
        public static final String DENSITY = "density";
        /**
         * 网络运营商
         */
        public static final String OPERATOR = "operator";
        /**
         * 浏览器user_agent
         */
        public static final String USER_AGENT = "user_agent";
//        /**
//         * 广告位宽度
//         */
//        public static final String AD_WIDTH = "ad_width";
//        /**
//         * 广告位高度
//         */
//        public static final String AD_HEIGHT = "ad_height";
        /**
         * 设备屏幕宽度
         */
        public static final String DEVICE_SCREEN_WIDTH = "device_screen_width";
        /**
         * 设备屏幕高度
         */
        public static final String DEVICE_SCREEN_HEIGHT = "device_screen_height";
        /**
         * 设备生产商名称
         */
        public static final String VENDOR = "vendor";

        /**
         * 联网类型(0—未知，
         * 1—Ethernet， 2—wifi，
         * 3—蜂窝网络，未知代，
         * 4—， 2G， 5—蜂窝网
         * 络， 3G， 6—蜂窝网络，
         * 4G)
         */
        public static final String NET = "net";
        /**
         * 横竖屏 0=竖屏，1=横屏
         */
        public static final String ORIENTATION = "orientation";
        /**
         * 使用语言
         */
        public static final String LANGUAGE = "language";
    }

}
