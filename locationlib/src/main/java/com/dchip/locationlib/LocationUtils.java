package com.dchip.locationlib;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;
import com.dchip.locationlib.Application.locationApplication;
import com.dchip.locationlib.Mode.LMode;
import com.dchip.locationlib.Service.LocationService;

/**
 * Created by llakcs on 2017/6/6.
 */

public class LocationUtils implements RadarUploadInfoCallback, BDLocationListener, RadarSearchListener,BaiduMap.OnMarkerClickListener,BaiduMap.OnMapClickListener{

    // 定位相关
    LocationClient mLocClient;
    private LatLng positon = null;
    private String userComment = "";
    private String userID = "";
    private String tag = "LocationUtils";
    private Context mContext;
    private boolean autoupload = false;
    public static final LocationUtils utils = new LocationUtils();
    private LMode lmode;
    private boolean mEnable;
    private boolean mUploadType;
    private LocationService locationService;
    private Activity mActivity;
    //地图相关
    private MyLocationData locData;
    private BaiduMap mBaiduMap = null;
    public static LocationUtils getIns() {
        return utils;
    }
    boolean isFirstLoc = true; // 是否首次定位
    private TextView popupText = null; // 泡泡view
    BitmapDescriptor ff3 = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
    //回调接口
    UploadstateListner mUpload;
    ClearInfoStateListner mClearinfo;
    NearbyInfoListListner mNearby;

    public interface UploadstateListner {
        void Getuploadstate(RadarSearchError error);
    }

    public interface ClearInfoStateListner {
        void Getclearstate(RadarSearchError error);
    }

    public interface NearbyInfoListListner {
        void GetNearbyInfo(boolean statue);
    }
    /**
     * 设置监听单次上传状态
     *
     * @param upload
     */
    public void SetUploadsateListner(UploadstateListner upload) {

        this.mUpload = upload;
    }

    /**
     * 设置监听清除位置信息状态
     *
     * @param Clearinfo
     */
    public void SetClearInfoStateListner(ClearInfoStateListner Clearinfo) {
        this.mClearinfo = Clearinfo;
    }

    /**
     * 设置监听周边雷达消息
     *
     * @param Nearby
     */
    public void SetNearbyInfoListListner(NearbyInfoListListner Nearby) {
        this.mNearby = Nearby;
    }


    /**
     * 初始化
     * @param activity    传入当前的activity
     * @param context     context
     * @param enable     是否开启上传位置功能,
     * @param uploadType true代表连续自动上传位置信息 ,false代表上传一次
     *
     */
    public void onCreate(Activity activity,Context context,boolean enable, boolean uploadType) {
        lmode = new LMode();
        this.mContext = context;
        this.mActivity = activity;
        this.mEnable = enable;
        this.mUploadType = uploadType;

        // 周边雷达设置监听
        RadarSearchManager.getInstance().addNearbyInfoListener(this);
        //定位服务初始化
        lServiceinit();
        RadarSearchManager.getInstance().setUserID(userID);
        if(locationService != null) {
            locationService.start();// 定位SDK
            // start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
        }
        initlocation();
    }
    private void lServiceinit(){
        // -----------location config ------------
        locationService = ((locationApplication)mContext.getApplicationContext()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(this);
        //注册监听
        int type =mActivity.getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
    }


    private void initlocation() {
        // 定位初始化
        mLocClient = new LocationClient(mContext);
        mLocClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(false); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }


    /**
     *
     * @param map 默认为null不开启地图功能
     */
    public void enaleMap(BaiduMap map){
        this.mBaiduMap = map;
        if(mBaiduMap != null){
            mBaiduMap.setMyLocationEnabled(true);
            mBaiduMap.setOnMarkerClickListener(this);
            mBaiduMap.setOnMapClickListener(this);
        }
    }

    /**
     * 设置地图上设备或人标识图片
     * @param ff3
     */
    public void setLocationdrawable(BitmapDescriptor ff3){
        this.ff3 = ff3;
    }


    /**
     * 设置用户标识码
     *
     * @param id
     */
    public void setUserID(String id) {
        this.userID = id;
        RadarSearchManager.getInstance().setUserID(userID);
    }

    /**
     * 设置备注信息，例如街道名称，地址
     *
     * @param userComment
     */
    public void setuserComment(String userComment) {
        this.userComment = userComment;
    }


    /**
     * 上传一次位置
     *
     * @param
     */
    private void uploadOnce() {
        if (positon == null) {
            return;
        }
        autoupload = true;
        RadarUploadInfo info = new RadarUploadInfo();
        info.comments = userComment;
        info.pt = positon;
        RadarSearchManager.getInstance().uploadInfoRequest(info);
    }


    /**
     * 自动连续上传位置
     *
     * @return 返回true 成功上传位置信息 返回false失败
     */
    private  void uploadLocation() {
        if (positon == null) {
            return;
        }
        autoupload = true;
        Log.e(tag, "########自动上传位置--------!");
        RadarSearchManager.getInstance().startUploadAuto(utils, 5000);

    }

    /**
     * 查找周边的人或设备
     */
    public void search() {
        RadarNearbySearchOption option = new RadarNearbySearchOption()
                .centerPt(positon).pageNum(0).radius(2000).pageCapacity(11);
        RadarSearchManager.getInstance().nearbyInfoRequest(option);
        if(mBaiduMap != null) {
            mBaiduMap.hideInfoWindow();
        }
    }




    /**
     * 停止上传位置信息
     */
    public void stopUpload() {
        RadarSearchManager.getInstance().stopUploadAuto();
    }

    /**
     * 清除位置信息
     */
    public void cleaninfo() {
        RadarSearchManager.getInstance().clearUserInfo();
    }

    /**
     * @return 返回地址信息
     */
    public String GetAddrstr() {
        return lmode.getAddrStr();
    }

    /**
     * @return 返回最近定位时间
     */
    public String GetTime() {
        return lmode.getTime();
    }

    /**
     * @return 返回城市代码
     */
    public String GetCitycode() {
        return lmode.getCitycode();
    }


    /**
     * @return 返回位置语义
     */
    public String GetLocationDescribe() {
        return lmode.getLocationDescribe();
    }

    /**
     * 退出时释放资源
     */
    public void onDestroy() {
        // 退出时销毁定位
        if(mLocClient != null && locationService != null) {
            mLocClient.stop();
            locationService.unregisterListener(this); //注销掉监听
            locationService.stop(); //停止定位服务
        }
        ff3.recycle();
        if(mBaiduMap != null) {
            mBaiduMap.removeMarkerClickListener(this);
            mBaiduMap = null;
        }
        // 释放周边雷达相关
        RadarSearchManager.getInstance().removeNearbyInfoListener(this);
        RadarSearchManager.getInstance().clearUserInfo();
        RadarSearchManager.getInstance().destroy();
        autoupload = false;
        mUpload = null;
        mClearinfo = null;
        mNearby = null;
    }


    /**
     * 更新结果地图
     *
     * @param res
     */
    private void parseResultToMap(RadarNearbyResult res,BitmapDescriptor ff3) {
        if(mBaiduMap != null) {
            mBaiduMap.clear();
            if (res != null && res.infoList != null && res.infoList.size() > 0) {
                for (int i = 0; i < res.infoList.size(); i++) {
                    MarkerOptions option = new MarkerOptions().icon(ff3).position(res.infoList.get(i).pt);
                    Bundle des = new Bundle();
                    if (res.infoList.get(i).comments == null || res.infoList.get(i).comments.equals("")) {
                        des.putString("des", "没有备注");
                    } else {
                        des.putString("des", res.infoList.get(i).comments);
                    }

                    option.extraInfo(des);
                    mBaiduMap.addOverlay(option);
                }
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(mBaiduMap != null) {
            mBaiduMap.hideInfoWindow();
        }
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(mBaiduMap != null) {
            mBaiduMap.hideInfoWindow();
        }
        if (marker != null) {
            popupText = new TextView(mContext);
            popupText.setBackgroundResource(R.drawable.popup);
            popupText.setTextColor(0xFF000000);
            popupText.setText(marker.getExtraInfo().getString("des"));
            mBaiduMap.showInfoWindow(new InfoWindow(popupText, marker.getPosition(), -47));
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(marker.getPosition());
            mBaiduMap.setMapStatus(update);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError radarSearchError) {

        if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
            parseResultToMap(radarNearbyResult,ff3);
            if (mNearby != null) {
                mNearby.GetNearbyInfo(true);
            }

        } else {
            // 获取失败
            if (mNearby != null) {
                mNearby.GetNearbyInfo(false);
            }
        }

    }

    @Override
    public void onGetUploadState(RadarSearchError radarSearchError) {
        if (mUpload != null) {
            mUpload.Getuploadstate(radarSearchError);
        }
    }

    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {

        if (mClearinfo != null) {
            mClearinfo.Getclearstate(radarSearchError);
        }
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation == null) {
            return;
        }
        positon = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
        lmode.setAddrStr(bdLocation.getAddrStr());
        lmode.setCitycode(bdLocation.getCityCode());
        lmode.setLocationDescribe(bdLocation.getLocationDescribe());
        lmode.setTime(bdLocation.getTime());
        setuserComment(bdLocation.getAddrStr());
        //位置信息
        locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(positon).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
        if(mEnable) {
            if (!autoupload) {
                if(mUploadType) {
                    uploadLocation();
                }else{
                    uploadOnce();
                }
            }
        }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }

    @Override
    public RadarUploadInfo onUploadInfoCallback() {
        RadarUploadInfo info = new RadarUploadInfo();
        info.comments = userComment;
        info.pt = positon;
        Log.e("onUploadInfoCallback", "OnUploadInfoCallback");
        return info;
    }
}
