# Locationlibrary

Locationlibrary �����˰ٶȵ�ͼ���ܱ��״�Ͷ�λ���ܣ�����ˮƽ���ޣ����Դ����е���....�㶮�Ĵ�Ҷ����£�

how to use

ʹ��ǰ׼����ȥ�ٶȵ�ͼ��������������key �����ú��ܱ��״�    

ע������:   1.����key�Ĺ�������Ҫ��дpackage,���package��APP��package�� 2.demo�����Լ��ϴ�����,�Լ����ܱ��״����ǲ鿴���ˡ�����ʾ��ѯʧ�ܣ���ʱ����Ҫ����һ̨�ֻ����ϴ�������Ϣ�ſ��Բ鿴! 

1.������locationlib-manifests.xml ���ðٶȵ�ͼ�����key   <meta-data
                             android:name="com.baidu.lbsapi.API_KEY"
                             android:value="OSyXv072EE6zfx43D3NKT9YbgiAaTQf0" />       //key:�����������baidumapKey
                             
2.��app��application��������������

        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());
        //��4.3.0�𣬰ٶȵ�ͼSDK���нӿھ�֧�ְٶ�����͹�������꣬�ô˷���������ʹ�õ���������.
        //����BD09LL��GCJ02�������꣬Ĭ����BD09LL���ꡣ
        SDKInitializer.setCoordType(CoordType.BD09LL);


3. LocationUtils��������activity���ʼ�����ֻ��ʹ�ö�λ���ܣ���������

   locationService = ((MyApplication)getApplicationContext()).locationService;
   
   LocationUtils.getIns().onCreate(Instance,MainActivity.thislocationService,true,true);//Activity activity,Context context,locationService locationService boolean enable, boolean uploadType

   
   LocationUtils.getIns().setUserID("FFFFF"); //�û���ʶ  
   
   ��λ���ϵ�ͼ���ܿ�������baidumap��LocationUtils.getIns().enaleMap(mBaiduMap);
     
   ���õ�ͼͼƬ��ʶ��LocationUtils.getIns().setLocationdrawable(ff3);
   
   ���ü����������ܱ��״��ѯ״̬: LocationUtils.getIns().SetNearbyInfoListListner
                                             
4. ��activity�رյ�ʱ���ͷ���Դ      LocationUtils.getIns().onDestroy();//�ͷ���Դ
      
����ʹ����ο��������demo
                             
�ص��ӿ�

SetUploadsateListner   //���������ϴ�״̬

SetClearInfoStateListner  //�������λ����Ϣ״̬

SetNearbyInfoListListner  //�����ܱ��״���Ϣ״̬



����

setUserID(String id)   //�����û����豸��ʶ

setuserComment(String userComment)   //�����û����豸��ע��Ϣ

stopUpload()          //ֹͣ�ϴ���ǰ��λ��

cleaninfo()           //�����ǰ��λ����Ϣ

GetAddrstr()          //���ص�ǰ�ĵ�ַ��Ϣ

GetTime()             //�������һ�ζ�λ��ʱ�� ���뿪�������ϴ�����Ч��

GetCitycode();        //���ص�ǰ�ĳ��д���


GetLocationDescribe() //����λ������

search����            //�����ܱ���ͬ���豸���û�

enaleMap(Baidumap map)  //�����ܱ��״﹦��

setLocationdrawable(BitmapDescriptor ff3)  ������ʾ�ڵ�ͼ�ϵ�ͼ��ͼƬ


ok!�������ʲô���������ϵ��QQ1085377046��ӭ����!
