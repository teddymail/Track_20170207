package cn.jewei.lbs.track_20170207.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by teddy on 2017/2/7.
 */

public class DbHalper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    //数据库名
    public static final String DB_NAME= "track.db";
    //表名
    public static final String TABLE_TRACK = "track";
    public static final String TABLE_DETAIL = "track_detail";
    //字段
    public static final String ID="_id";
    //跟踪表
    public static final String TRACK_NAME = "track_name";
    public static final String CREATE_DATE="create_date";
    public static final String START_LOC="start_loc";
    public static final String END_LOC="end_loc";
    //明细表
    public static final String TID="tid";//线路的ID
    public static final String LAT="lat";//纬度
    public static final String LNG="lng";//精度

    public static final String CREATE_TABLE_TRACK = "create table track(" +
            "_id integer primary key autoincrement," +
            "track_name text," +
            "create_date text," +
            "start_loc text," +
            "end_loc text)";
    public static final String CREATE_TABLE_TRACK_DETAIL = "create table track_detail(" +
            "_id integer primary key autoincrement," +
            "tid integer not null," +
            "lat real," +
            "lng real)";

    public DbHalper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //初始化新建表
        db.execSQL(CREATE_TABLE_TRACK);
        db.execSQL(CREATE_TABLE_TRACK_DETAIL);
    }

    //升级
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists track");
            db.execSQL("drop table if exists track_detail");
            db.execSQL(CREATE_TABLE_TRACK);
            db.execSQL(CREATE_TABLE_TRACK_DETAIL);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
