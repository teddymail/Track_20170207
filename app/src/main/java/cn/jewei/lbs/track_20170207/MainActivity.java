package cn.jewei.lbs.track_20170207;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;


import java.util.ArrayList;
import java.util.Date;

import cn.jewei.lbs.track_20170207.adapter.DBAdapter;
import cn.jewei.lbs.track_20170207.entity.Track;
import cn.jewei.lbs.track_20170207.utils.DateUtils;


public class MainActivity extends AppCompatActivity {
    private static final int PLAYBACK_OVER = 1;
    private BaiduMap baiduMap = null;
    private MyLocationListener myLocationListener;
    private LocationClient locationClient;
    private GeoCoder coder;
    private double currentLat, currentLng; // 当前的经纬度
    private String currentAddr;// 当前所在的地址
    private DBAdapter dbAdapter;//数据库适配器
    private int currentTrackLineID;// 当前跟踪的线路ID
    private boolean flag = true;//是否已经定位过标记了

    //用于存储两个相邻的经纬度点，再画线
    private ArrayList<LatLng> list = new ArrayList<LatLng>();

    private boolean isTracking = false;//是否已经模拟
    //消息队列
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case PLAYBACK_OVER:
                    Toast.makeText(MainActivity.this, "回放结束", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }

        ;
    };

    MapView mMapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        initBaiduMap();
        dbAdapter = new DBAdapter(this);
    }

    //初始化百度地图
    private void initBaiduMap() {
        //获取map地图实例
        baiduMap = mMapView.getMap();
        //打开定位图层
        baiduMap.setMyLocationEnabled(true);
        //声明定位
        locationClient = new LocationClient(getApplicationContext());
        //声明监听
        myLocationListener = new MyLocationListener();
        //注册监听
        locationClient.registerLocationListener(myLocationListener);


        //配置百度参数
        LocationClientOption option = new LocationClientOption();
        //配置为高精度
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //配置坐标系
        option.setCoorType("bd09ll");
        //设置请求时间间隔
        option.setScanSpan(5000);
        //设置需要返回地址
        option.setIsNeedAddress(true);
        //设置返回定位结果包含手机头的方向
        option.setNeedDeviceDirect(true);
        //设置配置信息
        locationClient.setLocOption(option);
        //启动SDK定位
        locationClient.start();
        //发起定位请求
        locationClient.requestLocation();


        //正向转换地理编码
        coder = GeoCoder.newInstance();
        coder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null
                        || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检索到结果
                } else {
                    // 获取地理编码结果
                    currentAddr = geoCodeResult.getAddress();
                    //更新线路的结束位置
                    dbAdapter.updateEndLoc(currentAddr, currentTrackLineID);
                }
            }
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_1://我的位置
                myLocation();
                break;
            case R.id.item_2://开始跟踪
                startTrack();
                break;
            case R.id.item_3://结束跟踪
                endTrack();
                break;
            case R.id.item_4://跟踪回放
                trackBack();
                break;
        }
        return true;
    }

    /**
     * 跟踪回放
     */
    private void trackBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("跟踪路线列表");
        System.out.println("123");

    }

    /**
     * 结束跟踪
     */
    private void endTrack() {
    }

    /**
     * 开始跟踪
     */
    private void startTrack() {
        //新建一个弹出对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("线路跟踪");
        final View view = getLayoutInflater().inflate(R.layout.dialog_start_track, null);
        builder.setView(view);
        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText endTrack = (EditText) view.findViewById(R.id.id_end_track);
                String trackName = endTrack.getText().toString();
                System.out.println(trackName);
                createTrack(trackName);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //显示对话框
        builder.show();

    }

    //创建一条线路跟踪
    private void createTrack(String trackName) {
        Track track = new Track();
        track.setTrack_name(trackName);//终点名称
        track.setCreate_date(DateUtils.toDate(new Date()));//创建日期
        track.setStart_loc(currentAddr);//地址
        currentTrackLineID = dbAdapter.addTrack(track);
        dbAdapter.addTrackDetail(currentTrackLineID, currentLat, currentLng);
        //清除地图标记
        baiduMap.clear();
        list.add(new LatLng(currentLat, currentLng));//添加划先标记
        isTracking = true;//线程模拟
        new Thread(new TrackThread()).start();
    }

    /**
     * 我的位置
     */
    private void myLocation() {
        Toast.makeText(this, "正在定位中.....", Toast.LENGTH_SHORT).show();
        //设置是加载标记
        flag = true;
        //清除屏幕上所有的自定义标记
        baiduMap.clear();
        //启用我的位置图层
        baiduMap.setMyLocationEnabled(true);
        //发送定位请求
        locationClient.requestLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    /**
     * 模拟跟踪的线程
     *
     * @author Administrator
     */
    class TrackThread implements Runnable {

        @Override
        public void run() {
            while (isTracking) {
                getLocation();
                dbAdapter.addTrackDetail(currentTrackLineID, currentLat,
                        currentLng);
                list.add(new LatLng(currentLat, currentLng));
                drawLine();
                System.out.println("drawLine");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //在两个点之间画线
    private void drawLine() {
        OverlayOptions lineOptions = new PolylineOptions().points(list).color(
                0xFFFF0000);
        baiduMap.addOverlay(lineOptions);
        list.remove(0);
    }

    /**
     * 模拟位置
     */
    private void getLocation() {
        currentLat = currentLat + Math.random() / 1000;
        currentLng = currentLng + Math.random() / 1000;
    }

    /**
     * 位置监听器
     */
    class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null && flag) {

                flag = false;
                //当前纬度
                currentLat = bdLocation.getLatitude();
                //当前经度
                currentLng = bdLocation.getLongitude();
                //当前位置
                currentAddr = bdLocation.getAddrStr();

                //构造我的当前位置信息
                MyLocationData.Builder builder = new MyLocationData.Builder();
                builder.latitude(bdLocation.getLatitude());//设置纬度
                builder.longitude(bdLocation.getLongitude());//设置经度
                builder.accuracy(bdLocation.getRadius());//设置坐标
                builder.direction(bdLocation.getDirection());//设置描述
                builder.speed(bdLocation.getSpeed());//设置速度
                MyLocationData locationDate = builder.build();//构建配置

                //把设置好的位置放到地图上
                baiduMap.setMyLocationData(locationDate);
                //配置我的位置
                LatLng latlng = new LatLng(currentLat, currentLng);
                //设置我的位置的配置信息: 模式:跟随模式,是否要显示方向,图标
                baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.FOLLOWING,
                        true, null));
                // 设置我的位置为地图的中心点
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latlng,21));
            }

        }
    }
}
