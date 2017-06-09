# Locationlibrary

Locationlibrary 整合了百度地图的周边雷达和定位功能！

how to use

使用前准备：去百度地图开发者中心申请key 和设置好周边雷达    

注意事项:   申请key的过程中需要填写package,这个package是APP的package。

1.首先在locationlib-manifests.xml 设置百度地图申请的key   <meta-data
                             android:name="com.baidu.lbsapi.API_KEY"
                             android:value="OSyXv072EE6zfx43D3NKT9YbgiAaTQf0" />       //key:开发者申请的baidumapKey
                             

2. LocationUtils操作类在activity里初始化     LocationUtils.getIns().onCreate(Instance,MainActivity.this,true,true);//Activity activity,Context context, boolean enable, boolean uploadType
                                             LocationUtils.getIns().setUserID("FFFFF"); //用户标识 
                                             
3. 在activity关闭的时候释放资源              LocationUtils.getIns().onDestroy();//释放资源
      
具体使用请参考工程里的demo
                             
回调接口

SetUploadsateListner   //监听单次上传状态

SetClearInfoStateListner  //监听清除位置信息状态

SetNearbyInfoListListner  //监听周边雷达信息状态


方法

setUserID(String id)   //设置用户或设备标识

setuserComment(String userComment)   //设置用户或设备备注信息

GetPostion()          //返回当前latlng位置

stopUpload()          //停止上传当前的位置

cleaninfo()           //清除当前的位置信息

GetAddrstr()          //返回当前的地址信息

GetTime()             //返回最近一次定位的时间 必须开启连续上传才有效果

GetCitycode();        //返回当前的城市代码


GetLocationDescribe() //返回位置语义

search（）            //查找周边相同的设备或用户





