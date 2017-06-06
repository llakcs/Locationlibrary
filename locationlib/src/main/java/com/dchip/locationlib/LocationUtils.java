package com.dchip.locationlib;

import android.content.Context;
import android.util.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;

/**
 * Created by llakcs on 2017/6/6.
 */

public class LocationUtils implements RadarUploadInfoCallback, BDLocationListener {


    // 定位相关
    LocationClient mLocClient;
    private LatLng positon = null;
    private String userComment = "";
    private String userID = "";
    private Context mContext;
    public static final LocationUtils utils = new LocationUtils();

    public static LocationUtils getIns() {
        return utils;
    }

    /**
     * 初始化
     * @param context
     */
    public void onCreate(Context context) {
        this.mContext = context;
        SDKInitializer.initialize(context);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        // 周边雷达设置用户，id为空默认是设备标识
        RadarSearchManager.getInstance().setUserID(userID);
        initlocation();
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
     * 设置用户标识码
     * @param id
     */
    public void setUserID(String id){
        this.userID = id;
        RadarSearchManager.getInstance().setUserID(userID);
    }

    /**
     * 设置备注信息，例如街道名称，地址
     * @param userComment
     */
    public void setuserComment(String userComment){
        this.userComment = userComment;
    }

    /**
     * 自动连续上传位置
     * @return  返回true 成功上传位置信息 返回false失败
     */
    public boolean uploadLocation(){
        if(positon == null){
          return false;
        }
        RadarSearchManager.getInstance().startUploadAuto(utils, 5000);
        return true;
    }

    /**
     * 停止上传位置信息
     */
    public void stopUpload(){
        RadarSearchManager.getInstance().stopUploadAuto();
    }

    /**
     * 清除位置信息
     */
    public void cleaninfo(){
        RadarSearchManager.getInstance().clearUserInfo();
    }


    /**
     * 退出时释放资源
     */
    public void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 释放周边雷达相关
        RadarSearchManager.getInstance().clearUserInfo();
        RadarSearchManager.getInstance().destroy();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation == null) {
            return;
        }
        positon = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }

    @Override
    public RadarUploadInfo onUploadInfoCallback() {
        RadarUploadInfo info = new RadarUploadInfo();
        info.comments = userComment;
        info.pt = positon;
        Log.e("hjtest", "OnUploadInfoCallback");
        return info;
    }
}
