package cn.jewei.lbs.track_20170207.entity;

/**
 * 线路点详情实体类
 * Created by teddy on 2017/2/7.
 */

public class TrackDetail {
    private int id;//ID
    private double lat;//纬度
    private double lng;//精度
    private Track track;//当前坐标点所属的线路

    public TrackDetail() {}

    public TrackDetail(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public TrackDetail(int id, double lat, double lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }
}
