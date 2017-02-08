package cn.jewei.lbs.track_20170207.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import cn.jewei.lbs.track_20170207.db.DbHalper;
import cn.jewei.lbs.track_20170207.entity.Track;
import cn.jewei.lbs.track_20170207.entity.TrackDetail;

/**
 * 数据库适配器
 * Created by teddy on 2017/2/7.
 */

public class DBAdapter {
    //帮助类引用
    private DbHalper dbHalper;

    //构造
    public DBAdapter(Context context) {
        dbHalper = new DbHalper(context);
    }

    //添加线路跟踪
    public int addTrack(Track track) {
        SQLiteDatabase db = dbHalper.getWritableDatabase();//获取写
        ContentValues values = new ContentValues();
        values.put(DbHalper.TRACK_NAME, track.getTrack_name());
        values.put(DbHalper.CREATE_DATE, track.getCreate_date());
        values.put(DbHalper.START_LOC, track.getStart_loc());
        values.put(DbHalper.END_LOC, track.getEnd_loc());
        long id = db.insertOrThrow(DbHalper.TABLE_TRACK, null, values);
        db.close();
        return (int) id;
    }

    //更新终点地址
    public void updateEndLoc(String endLoc, int id) {
        String sql = "update track set end_loc=? where _id=?";
        SQLiteDatabase db = dbHalper.getWritableDatabase();
        db.execSQL(sql, new Object[]{endLoc, id});
        db.close();
    }

    //添加线路跟踪明细
    public void addTrackDetail(int tid, double lat, double lng) {
        SQLiteDatabase db = dbHalper.getWritableDatabase();//获取写权限
        String sql = "insert into track_detail(tid,lat,lng)values(?,?,?)";
        db.execSQL(sql, new Object[]{tid, lat, lng});
        db.close();
    }

    //根据Id查询线路跟踪
    public ArrayList<TrackDetail> getTrackDetails(int id) {
        String sql = "select _id,lat,lng from track_detail where tid=? order by _id desc";
        ArrayList<TrackDetail> list = new ArrayList<TrackDetail>();
        SQLiteDatabase db = dbHalper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        if (cursor != null) {
            TrackDetail detail = null;
            while (cursor.moveToNext()) {
                detail = new TrackDetail(cursor.getInt(0), cursor.getDouble(1),
                        cursor.getDouble(2));
                list.add(detail);
            }
            cursor.close();//关闭游标
        }
        return list;
    }

    //查询所有线路
    public ArrayList<Track> getTracks() {
        ArrayList<Track> tracks = new ArrayList<Track>();
        String sql = "select _id,track_name,create_date,start_loc,end_loc from track ";
        SQLiteDatabase db = dbHalper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor != null){
            Track track = null;
            while (cursor.moveToNext()){
                track = new Track(cursor.getInt(0),cursor.getString(1),cursor.getString(2), cursor.getString(3),cursor.getString(4));
                tracks.add(track);
            }
            cursor.close();
        }
        return tracks;
    }

    //根据ID删除线路跟踪
    public void delTrack(int id){
        SQLiteDatabase db = dbHalper.getWritableDatabase();
        String sql1 = "delete from track where _id=?";
        String sql2 = "delete from track_detail where tid=?";
        try {
            db.beginTransaction();
            db.execSQL(sql2, new Object[] { id });
            db.execSQL(sql1, new Object[] { id });
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            if (db != null)
                db.close();
        }
    }

}
