# Locationlibrary

Locationlibrary 整合了百度地图的周边雷达和定位功能！由于水平有限，所以代码有点乱....你懂的大家多体谅！

how to use

使用前准备：去百度地图开发者中心申请key 和设置好周边雷达    

注意事项:   1.申请key的过程中需要填写package,这个package是APP的package。 2.demo里面自己上传坐标,自己在周边雷达里是查看不了。会显示查询失败！这时候需要另外一台手机来上传坐标信息才可以查看! 

1.首先在locationlib-manifests.xml 设置百度地图申请的key   <meta-data
                             android:name="com.baidu.lbsapi.API_KEY"
                             android:value="OSyXv072EE6zfx43D3NKT9YbgiAaTQf0" />       //key:开发者申请的baidumapKey
                             

2. LocationUtils操作类在activity里初始化如果只是使用定位功能，代码如下
 
   LocationUtils.getIns().onCreate(Instance,MainActivity.this,true,true);//Activity activity,Context context, boolean enable, boolean uploadType

   
   LocationUtils.getIns().setUserID("FFFFF"); //用户标识  
   
   定位加上地图功能开启传入baidumap：LocationUtils.getIns().enaleMap(mBaiduMap);
     
   设置地图图片标识：LocationUtils.getIns().setLocationdrawable(ff3);
   
   设置监听器返回周边雷达查询状态: LocationUtils.getIns().SetNearbyInfoListListner
                                             
3. 在activity关闭的时候释放资源      LocationUtils.getIns().onDestroy();//释放资源
      
具体使用请参考工程里的demo
                             
回调接口

SetUploadsateListner   //监听单次上传状态

SetClearInfoStateListner  //监听清除位置信息状态

SetNearbyInfoListListner  //监听周边雷达信息状态



方法

setUserID(String id)   //设置用户或设备标识

setuserComment(String userComment)   //设置用户或设备备注信息

stopUpload()          //停止上传当前的位置

cleaninfo()           //清除当前的位置信息

GetAddrstr()          //返回当前的地址信息

GetTime()             //返回最近一次定位的时间 必须开启连续上传才有效果

GetCitycode();        //返回当前的城市代码


GetLocationDescribe() //返回位置语义

search（）            //查找周边相同的设备或用户

enaleMap(Baidumap map)  //开启周边雷达功能

setLocationdrawable(BitmapDescriptor ff3)  设置显示在地图上的图标图片


ok!最后大家有什么问题可以联系我QQ1085377046欢迎交流!
