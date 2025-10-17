package com.example.newgpscollector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.*;
import android.location.*;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
// import com.github.mikephil.charting.charts.CombinedChart;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    final String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private LocationManager locationManager;

    private Location _lastLocation;//WGS84

    private TextureMapView mapView;
    private BaiduMap baiduMap;

    private LocationClient locationClient;

    private Boolean updateTargets = false;

    private SensorManager sensorManager;

    private Float _lastYaw = null;

    private TabLayout tabLayout;

    private FrameLayout linearLayout1;
    private LinearLayout linearBaidu;
    private TextView baiduInfText;

    private ScrollView linearLayout2;
    private LinearLayout linear2Child;
    // private CombinedChart combinedChart;
    private StatelliteTab statelliteTab;
    private LinearLayout stateChildLinear;
    public static boolean gnssChanged = false;

    private Handler reloadHandler = new Handler();
    private Handler reloadState = new Handler();



    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ask the need permission
        askForPermission();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //register the sensor
        registerSensor();
        //initializer the baidu sdk
        initBaiduMap();
        //load BaiduMapView
        loadBaiduMapView();
        //set BaiduMap
        setBaiduMap();
        //set baidu location client
        setBaiduLocationClient();
        //initTab
        initTab();

        //init linear2
        initLinear2();
        //register the GnssStaus Service
        try {
            registerGnssStatus();
        }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }


        reloadHandler.post(reloadRunnable);
        reloadState.post(reloadStateRunable);
        initLoadNowLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }
    /**
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // mTextView.setText(item.getTitle());
        switch (item.getItemId()) {
            case R.id.Home:
                //Toast.makeText(this, "点击了第" + 1 + "个", Toast.LENGTH_SHORT).show();
                break;
            case R.id.LXC:
                //Toast.makeText(this, "点击了第" + 2 + "个", Toast.LENGTH_SHORT).show();
                toActivity2();
                break;
//            case R.id.AGPS:
//                //Toast.makeText(this, "点击了第" + 3 + "个", Toast.LENGTH_SHORT).show();
//                toRenix();
//                break;
//            case R.id.menu4:
//                Toast.makeText(this, "点击了第" + 4 + "个", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.menu5:
//                Toast.makeText(this, "点击了第" + 5 + "个", Toast.LENGTH_SHORT).show();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }*/



    private void initTab() {
        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                //Toast.makeText(MainActivity.this,tab.getPosition()+"",Toast.LENGTH_SHORT).show();
                if (tab.getPosition() == 0) {
                    tab1Selected();
                } else if (tab.getPosition() == 1) {
                    tab2Selected();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void tab1Selected() {
        linearLayout1.setVisibility(View.VISIBLE);
        linearLayout2.setVisibility(View.GONE);
        //linearLayout3.setVisibility(View.GONE);
    }

    private void tab2Selected() {
        linearLayout1.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.VISIBLE);
        //linearLayout3.setVisibility(View.GONE);
    }

    /*
    private void tab3Selected(){
        if(_lastLocation==null){
            return;
        }
        if(_lastLocation.getProvider().equals("network")){
            tab1Selected();
            tabLayout.getTabAt(0).select();
            Toast.makeText(this,"无卫星，不能查看",Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this,"加载中，稍等...",Toast.LENGTH_SHORT).show();
        try{
            Thread.sleep(1000);
        }catch (Exception e){}
        linearLayout1.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.GONE);
        linearLayout3.setVisibility(View.VISIBLE);
    }*/
    private void initBaiduMap() {
        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
        SDKInitializer.initialize(this.getApplicationContext());
        LocationClient.setAgreePrivacy(true);
    }

    private void loadBaiduMapView() {
        BaiduMapOptions options = new BaiduMapOptions();
        options.scaleControlPosition(new Point(20, 240));
        options.zoomControlsPosition(new Point(0, 0));
        options.scaleControlEnabled(true);
        options.zoomControlsEnabled(true);


        mapView = new TextureMapView(this, options);

        linearLayout1 = (FrameLayout) findViewById(R.id.linear1);
        linearBaidu = (LinearLayout) findViewById(R.id.linearBaidu);
        linearBaidu.addView(mapView);
        linearLayout1.setVisibility(View.VISIBLE);
        baiduInfText = (TextView) findViewById(R.id.baiduInfText);
        //Log.e("debug",linearLayout.getWidth()+","+linearLayout.getHeight());
    }

    private void initLinear2() {
        linearLayout2 = (ScrollView) findViewById(R.id.linear2);
        //combinedChart = (CombinedChart) findViewById(R.id.combiedChart);
        //DrawCombined.drawCombined(combinedChart, MyGnssStatus.satellites);

        //卫星列表模块相关设置
        linear2Child = (LinearLayout) findViewById(R.id.linear2Child);//linear2内的滚动linear
        statelliteTab = new StatelliteTab(this);
        linear2Child.addView(statelliteTab.mainLinear);//这里只有表头

        stateChildLinear = new LinearLayout(this);//这里是表身
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 30);
        stateChildLinear.setLayoutParams(params);
        linear2Child.addView(stateChildLinear);

        //添加查看详细信息按钮
        //TextView detailButton = publicUI.getButton("伪卫星", 0, this);
//        detailButton.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onClick(View view) {
//                toActivity2();
//            }
//        });
//        linear2Child.addView(detailButton, 0);
        //添加保存文件按钮
        TextView saveButton = publicUI.getButton("Save GPS status", 0, this);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveStatus();
            }
        });
        linear2Child.addView(saveButton, 0);
        // add the button for jumping to the renix view
        //TextView toRenix=publicUI.getButton("Renix",0,this);
//        toRenix.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toRenix();
//            }
//        });
//        linear2Child.addView(toRenix,2);

        linearLayout2.setVisibility(View.GONE);
    }

    private void toActivity2() {
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        startActivity(intent);

    }

    private void toRenix(){
        Intent intent=new Intent(MainActivity.this,Renix.class);
        startActivity(intent);
    }

    private void saveStatus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setTitle("请输入文件名");
        builder.setMessage("保存至：文件/documents/");
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (editText.getText().length() == 0) {
                    Toast.makeText(MainActivity.this, "请输入文件名", Toast.LENGTH_SHORT).show();
                    return;
                }
                Write.writeStatus(editText.getText() + ".csv", MyGnssStatus.gnssStatus, MainActivity.this);
                Toast.makeText(MainActivity.this, "写入成功", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void registerGnssStatus() {
        LocationManager locationManagerStatus = (LocationManager) getSystemService(LOCATION_SERVICE);
        MyGnssStatus.registerGnss();
        locationManagerStatus.registerGnssStatusCallback(MyGnssStatus.callback);
        locationManagerStatus.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if(MyGnssStatus.isUsed>=4){
                    _lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }else{
                    _lastLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if(_lastLocation==null||_lastYaw==null){
                    return;
                }
                toNowLocation(_lastLocation);
            }
        });
    }

    private Runnable reloadStateRunable = new Runnable() {
        @Override
        public void run() {
            reloadState.postDelayed(this, 1000);
            //DrawCombined.drawCombined(combinedChart, MyGnssStatus.satellites);
        }
    };
    private Runnable reloadRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            reloadHandler.postDelayed(this, 1000);
            if (MyGnssStatus.gnssStatus == null) {
                return;
            }
            //Log.e("debug","reload");
            //stateChildLinear=statelliteTab.loadChildLinears(MainActivity.this,MyGnssStatus.gnssStatus);
            //Log.e("debug","reload");
            stateChildLinear.removeAllViews();
            LinearLayout linear = statelliteTab.loadChildLinears(MainActivity.this, MyGnssStatus.gnssStatus);
            stateChildLinear.addView(linear);

        }
    };

    private void registerSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
    }

    private void askForPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    private BDAbstractLocationListener bdAbstractLocationListener = new BDAbstractLocationListener() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

        }
    };

    private SensorEventListener sensorEventListener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch (sensorEvent.sensor.getType()){
                case Sensor.TYPE_ORIENTATION:
                    double yaw=sensorEvent.values[SensorManager.DATA_X];
                    //Toast.makeText(MainActivity.this,yaw+"",Toast.LENGTH_SHORT).show();
                    if(_lastYaw==null){
                        _lastYaw=new Float(yaw);
                        return;
                    }
                    if(Math.abs(_lastYaw-yaw)>1.0){
                        _lastYaw=new Float(yaw);
                        if(_lastLocation==null){
                            return;
                        }
                        LatLng center=Transer.WGS2BD09(_lastLocation.getLatitude(),_lastLocation.getLongitude());
                        MyLocationData locationData=new MyLocationData.Builder()
                                .direction(_lastYaw)
                                .accuracy(_lastLocation.getAccuracy())
                                .latitude(center.latitude)
                                .longitude(center.longitude)
                                .build();
                        baiduMap.setMyLocationData(locationData);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
    private void toNowLocation(Location location){
        LatLng latLng=Transer.WGS2BD09(location.getLatitude(),location.getLongitude());
        MyLocationData locationData=new MyLocationData.Builder()
                .accuracy(location.getAccuracy())
                .direction(_lastYaw)
                .latitude(latLng.latitude)
                .longitude(latLng.longitude)
                .build();
        baiduMap.setMyLocationData(locationData);

        if(!updateTargets){
            MapStatus mapStatus=new MapStatus.Builder()
                    .target(latLng)
                    .zoom(22)
                    .build();
            MapStatusUpdate mapStatusUpdate=MapStatusUpdateFactory.newMapStatus(mapStatus);
            baiduMap.setMapStatus(mapStatusUpdate);

            updateTargets=true;
        }

        //setInfo
        String info="\n提供者："+_lastLocation.getProvider()+"\n";
        info=info+String.format("卫星时间：%dms\n",_lastLocation.getTime());
        info=info+String.format("纬度：%f°\n经度:%f°\n",_lastLocation.getLatitude(),_lastLocation.getLongitude());
        info=info+String.format("精度：%fm\n高度:%fm\n",_lastLocation.getAccuracy(),_lastLocation.getAltitude());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            info=info+String.format("速度：%fm/s\n速度精度：%sm/s\n",_lastLocation.getSpeed(),_lastLocation.getSpeedAccuracyMetersPerSecond());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            info=info+String.format("卫星时间:%dns\n时间不确定性：%fns\n",
                    _lastLocation.getElapsedRealtimeNanos(),_lastLocation.getElapsedRealtimeUncertaintyNanos());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            info=info+String.format("当前位置的bearing:%f°\nbearing 的精度：%f°\n",
                    _lastLocation.getBearing(),_lastLocation.getBearingAccuracyDegrees());
        }
        baiduInfText.setText(info);
    }

    @SuppressLint("MissingPermission")
    private void setBaiduMap(){
        baiduMap=mapView.getMap();

        baiduMap.setMyLocationEnabled(true);//开启定位图层
        MyLocationConfiguration myLocationConfiguration=new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                true,null);
        baiduMap.setMyLocationConfiguration(myLocationConfiguration);//显示方向信息

        if(MyGnssStatus.isUsed>=6){
            _lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }else{
            _lastLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if(_lastLocation==null||_lastYaw==null){
            return;
        }
        updateTargets=true;
        LatLng center=Transer.WGS2BD09(_lastLocation.getLatitude(),_lastLocation.getLongitude());
        MapStatus mapStatus=new MapStatus.Builder()
                .zoom(22)
                .target(center)
                .build();
        MapStatusUpdate mapStatusUpdate=MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.setMapStatus(mapStatusUpdate);

        MyLocationData locationData=new MyLocationData.Builder()
                .accuracy(_lastLocation.getAccuracy())
                .direction(_lastYaw)
                .latitude(center.latitude)
                .longitude(center.longitude)
                .build();
        baiduMap.setMyLocationData(locationData);
    }
    private void setBaiduLocationClient(){
        LocationClientOption locationClientOption=new LocationClientOption();
        locationClientOption.setOpenGps(true);
        locationClientOption.setCoorType("bd09ll");
        locationClientOption.setScanSpan(1000);//设置更新时间ms,连续定位需大于等于1s
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        try{
            locationClient=new LocationClient(this,locationClientOption);
            locationClient.registerLocationListener(bdAbstractLocationListener);
            locationClient.startIndoorMode();
            locationClient.start();
        }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }
    public void loadNowLocation(View view){
        if(_lastLocation==null){
            return;
        }
        LatLng center=Transer.WGS2BD09(_lastLocation.getLatitude(),_lastLocation.getLongitude());
        MapStatus mapStatus=new MapStatus.Builder()
                .zoom(22)
                .target(center)
                .build();
        MapStatusUpdate mapStatusUpdate=MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.setMapStatus(mapStatusUpdate);
    }
    @SuppressLint("MissingPermission")
    private void initLoadNowLocation(){
        _lastLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(_lastLocation==null){
            return;
        }
        LatLng center=Transer.WGS2BD09(_lastLocation.getLatitude(),_lastLocation.getLongitude());
        MapStatus mapStatus=new MapStatus.Builder()
                .zoom(22)
                .target(center)
                .build();
        MapStatusUpdate mapStatusUpdate=MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.setMapStatus(mapStatusUpdate);

        //setInfo
        String info="\n提供者："+_lastLocation.getProvider()+"\n";
        info=info+String.format("卫星时间：%dms\n",_lastLocation.getTime());
        info=info+String.format("纬度：%f°\n经度:%f°\n",_lastLocation.getLatitude(),_lastLocation.getLongitude());
        info=info+String.format("精度：%fm\n高度:%fm\n",_lastLocation.getAccuracy(),_lastLocation.getAltitude());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            info=info+String.format("速度：%fm/s\n速度精度：%sm/s\n",_lastLocation.getSpeed(),_lastLocation.getSpeedAccuracyMetersPerSecond());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            info=info+String.format("卫星时间:%dns\n时间不确定性：%fns\n",
                    _lastLocation.getElapsedRealtimeNanos(),_lastLocation.getElapsedRealtimeUncertaintyNanos());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            info=info+String.format("当前位置的bearing:%f°\nbearing 的精度：%f°\n",
                    _lastLocation.getBearing(),_lastLocation.getBearingAccuracyDegrees());
        }
        baiduInfText.setText(info);
    }
    public void writeData(View view){
        if(_lastLocation==null){
            return;
        }
        final EditText editText=new EditText(this);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(editText);
        builder.setTitle("请输入文件名");
        builder.setMessage("保存至：文件/documents/");
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(editText.getText().length()==0){
                    Toast.makeText(MainActivity.this,"请输入文件名",Toast.LENGTH_SHORT).show();
                    return;
                }
                Write.writeLocation(editText.getText()+".csv",_lastLocation,MainActivity.this);
                Toast.makeText(MainActivity.this,"写入成功",Toast.LENGTH_LONG).show();
            }
        });

        builder.show();
    }
}