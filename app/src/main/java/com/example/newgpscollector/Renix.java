package com.example.newgpscollector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GnssCapabilities;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.TimeUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collection;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//import com.google.android.gms.common.logging.Logger;


public class Renix extends AppCompatActivity {

    // private LoggerDataChanged loggerDataChanged;

    /**
     * services
     * */
    // private LocationManager navigationLocationManager;
    private LocationManager measurementLocationManager;
    private LocationManager locationManager;
    private LocationManager nmeaMangaer;

    /**
     * permissions
     * */
    private String[] permissions=new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET
    };

    /**
     * handlers
     * */
    private Handler reloadGpsNavigationHandler=new Handler();
    private Handler reloadBdNavigationHandler=new Handler();
    private Handler reloadSp3NavigationHandler=new Handler();

    private Handler reloadLocationHandler=new Handler();
    private Handler reloadNMEAHandler=new Handler();
    private Handler reloadObserveHandler=new Handler();

    /**
     * messages
     * */
    private String mGpsNavigationMessage="";
    private String mBdNavigationMessage="";
    private String mSp3NavigationMessage="";

    private String mGpsError="";
    private String mBdError="";
    private String mSp3Error="";

    /**
     * Timers
     * */
    private Timer timerGps=new Timer();
    private Timer timerBd=new Timer();
    private Timer timerSp3=new Timer();

    /**
     * Viewers
     * */
    private TextView mGpsNavigationView;
    private TextView mBdNavigationView;
    private TextView mSp3NavigationView;

    private TextView mLocatioView;
    private TextView mNmeaView;
    private TextView mMeasureView;

    /**
     * recive broadcast classes
     * */
    // private reciveGPSbroadcast m_reciveBroadCast=new reciveGPSbroadcast();

    private ReciveGPSNavigation reciveGPSNavigation;
    //private reciveBdbroadCast m_reciveBdCast=new reciveBdbroadCast();
    private ReciveBDNavigation reciveBDNavigation;
    private ReciveSp3Navigation reciveSp3Navigation;
    //private reciveSp3 m_reciveSp3=new reciveSp3();
//    private final String localDirection="Renix_ZT";
//    private final String tmpLocalDirection=".Renix_ZT";

//    private final String childGpsDirection=tmpLocalDirection+"/.GPS";
//    private final String childBdDirection=tmpLocalDirection+"/.Bd";
//    private final String childSp3Direction=tmpLocalDirection+"/.Sp3";
//
//    private final String storageGpsDirection=localDirection+"/GPS";
//    private final String storageBdDirection=localDirection+"/Bd";
//    private final String storageSp3Direction=localDirection+"/Sp3";
//    private final File tmpLocalDirect=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),tmpLocalDirection);
//    private final File localDirect=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),localDirection);
//    private final File childGpsDirect=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),childGpsDirection);
//    private final File childBdDirect=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),childBdDirection);
//    private final File childSp3Direct=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),childSp3Direction);
//
//    private final File storageGpsDirect=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),storageGpsDirection);
//    private final File storageBdDirect=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),storageBdDirection);
//    private final File storageSp3Direct=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),storageSp3Direction);

    private ScrollView navigationScrollView;
    private ScrollView observeScrollView;
    // private TableLayout tableLayout;

    /**
     * Values
     * */
    private Location mLocation=null;
    private String mNMEA=null;
    private GnssMeasurementsEvent measurementsEvent=null;
    private TabLayout tableLayout;

    private Boolean isRecording=false;
    private TextView button;

    private String str_begin_record;
    private String str_stop_record;

    private File observeFile;
    private File nmeaFile;
    private File locationFile;
    private File gpsNavigationFile;
    private File bdNavigationFile;
    private File sp3NavigationFile;

    private String timeStr="";

    private final long minNans=60L*1000000000L;
    private final Long hourNans=60L*60L*1000000000L;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renix);


        mGpsNavigationView=(TextView)findViewById(R.id.m_gps_navigation);
        mBdNavigationView=(TextView)findViewById(R.id.m_bd_navigation);
        mSp3NavigationView=(TextView)findViewById(R.id.m_sp3_navigation);

        //navigationLocationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
        measurementLocationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
        nmeaMangaer=(LocationManager) getSystemService(LOCATION_SERVICE);

        askForPermission();

        // mkDirs();
        initRecivedNavigation();

        navigationScrollView=(ScrollView) findViewById(R.id.renix_navigation);
        observeScrollView=(ScrollView) findViewById(R.id.observeView);
        tableLayout=(TabLayout)findViewById(R.id.tab_renix);

        button=(TextView)findViewById(R.id.save_renix_button);

        mLocatioView=(TextView)findViewById(R.id.m_position);
        mNmeaView=(TextView)findViewById(R.id.m_nmea);
        mMeasureView=(TextView)findViewById(R.id.m_observe);

        navigationScrollView.setVisibility(View.VISIBLE);
        observeScrollView.setVisibility(View.GONE);

        str_begin_record=this.getString(R.string.begin_record);
        str_stop_record=this.getString(R.string.stop_record);

        button.setText(str_begin_record);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renixSaveClick();
            }
        });
        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    navigationScrollView.setVisibility(View.VISIBLE);
                    observeScrollView.setVisibility(View.GONE);
                } else if (tab.getPosition()==1) {
                    navigationScrollView.setVisibility(View.GONE);
                    observeScrollView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerListener();
        }

        reloadGpsNavigationHandler.post(runnable_navigation);
        reloadBdNavigationHandler.post(runnable_bd_navigation);
        reloadSp3NavigationHandler.post(runnable_sp3_navigation);

        reloadLocationHandler.post(runnable_position);
        reloadNMEAHandler.post(runnable_nmea);
        reloadObserveHandler.post(runnable_measurement);

        timerGps.schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                    processFTPGps();
                }catch (Exception e){
                    //mGpsError=e.toString();
                }
            }
        },0,1000);

        timerBd.schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                    processFTPBd();
                }catch (Exception e){
                    //mBdError=e.toString();
                }
            }
        },0,1000);

        timerSp3.schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                    processFTPSp3();
                }catch (Exception e){
                    //mSp3Error=e.toString();
                }
            }
        },0,1000);

        observeFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"ZT-"+timeStr+"-observe.txt");
        nmeaFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"ZT-"+timeStr+"-nmea.txt");
        locationFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"ZT-"+timeStr+"-location.txt");

        this.setTitle(R.string.AGPS);
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

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        // mTextView.setText(item.getTitle());
//        switch (item.getItemId()) {
//            case R.id.Home:
//                //Toast.makeText(this, "点击了第" + 1 + "个", Toast.LENGTH_SHORT).show();
//                toHome();
//                break;
//            case R.id.LXC:
//                //Toast.makeText(this, "点击了第" + 2 + "个", Toast.LENGTH_SHORT).show();
//                toActivity2();
//                break;
////            case R.id.AGPS:
////                //Toast.makeText(this, "点击了第" + 3 + "个", Toast.LENGTH_SHORT).show();
////                //toRenix();
////                break;
////            case R.id.menu4:
////                Toast.makeText(this, "点击了第" + 4 + "个", Toast.LENGTH_SHORT).show();
////                break;
////            case R.id.menu5:
////                Toast.makeText(this, "点击了第" + 5 + "个", Toast.LENGTH_SHORT).show();
////                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void toHome(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    private void toActivity2(){
        Intent intent=new Intent(this,MainActivity2.class);
        startActivity(intent);
    }

    private void initRecivedNavigation(){
        reciveGPSNavigation=new ReciveGPSNavigation(this.getFilesDir().getAbsolutePath(),"");
        reciveBDNavigation=new ReciveBDNavigation(this.getFilesDir().getAbsolutePath(),"");
        reciveSp3Navigation=new ReciveSp3Navigation(this.getFilesDir().getAbsolutePath(),"");
        try{
            if(reciveGPSNavigation.initLogger()){
                mGpsNavigationMessage=reciveGPSNavigation.log;
            }
        }catch (Exception e){
            mGpsError=e.toString();
        }

        try{
            if(reciveBDNavigation.initLogger()){
                mBdNavigationMessage=reciveBDNavigation.log;
            }
        }catch (Exception e){
            mBdError=e.toString();
        }

        try{
            if(reciveSp3Navigation.initLogger()){
                mSp3NavigationMessage=reciveSp3Navigation.log;
            }
        }catch (Exception e){
            mSp3Error=e.toString();
        }


    }

//    private void mkDirs(){
//        // mkdir localDirection
//        try{
//            if(!tmpLocalDirect.exists()){
//                tmpLocalDirect.mkdir();
//            }
//        }catch (Exception e){}
//        try{
//            if(!localDirect.exists()){
//                localDirect.mkdir();
//            }
//        }catch (Exception e){}
//
//        ///////////////////
//        try{
//            if(!childGpsDirect.exists()){
//                childGpsDirect.mkdir();
//            }
//        }catch (Exception e){}
//        try{
//            if(!childBdDirect.exists()){
//                childBdDirect.mkdir();
//            }
//        }catch (Exception e){}
//        try{
//            if(!childSp3Direct.exists()){
//                childSp3Direct.mkdir();
//            }
//        }catch (Exception e){}
//
//        ///////////////////
//        try{
//            if(!storageGpsDirect.exists()){
//                storageGpsDirect.mkdir();
//            }
//        }catch (Exception e){}
//
//        try{
//            if(!storageBdDirect.exists()){
//                storageBdDirect.mkdir();
//            }
//        }catch (Exception e){}
//
//        try{
//            if(!storageSp3Direct.exists()){
//                storageSp3Direct.mkdir();
//            }
//        }catch (Exception e){}
//    }

    private void processFTPGps(){
//        if(m_reciveBroadCast.getBroadCast()){
//            mGpsNavigationMessage=m_reciveBroadCast.log;
//            Log.e("GPS","recived");
//        }else{
//            Log.e("GPS",m_reciveBroadCast.log);
//        }
        try{
            if(reciveGPSNavigation.updateLogger()){
                mGpsNavigationMessage=reciveGPSNavigation.log;
                mGpsError="";
                Log.e("GPS","recived");
            }else{
                mGpsError=reciveGPSNavigation.log;
                Log.e("GPS",reciveGPSNavigation.log);
            }
        }catch (Exception e){
            //mGpsError=e.toString();
        }
    }

    private void processFTPBd(){
//        if(m_reciveBdCast.getBroadCast()){
//            mBdNavigationMessage= m_reciveBdCast.log;
//            Log.e("bd","recived");
//        }else{
//            Log.e("bd",m_reciveBdCast.log);
//        }
        try{
            if(reciveBDNavigation.updateLogger()){
                mBdNavigationMessage=reciveBDNavigation.log;
                mBdError="";
                Log.e("BD","recived");
            }else{
                mBdError=reciveBDNavigation.log;
                Log.e("BD",reciveBDNavigation.log);
            }
        }catch (Exception e){
            //mBdError=e.toString();
        }
    }

    private void processFTPSp3(){
//        if(m_reciveSp3.getBroadCast()){
//            mSp3NavigationMessage=m_reciveSp3.log;
//            Log.e("sp3","recived");
//        }else{
//            Log.e("sp3",m_reciveSp3.log);
//        }
        try{
            if(reciveSp3Navigation.updateLogger()){
                mSp3NavigationMessage=reciveSp3Navigation.log;
                mSp3Error="";
                Log.e("SP3","recived");
            }else {
                mSp3Error=reciveSp3Navigation.log;
                Log.e("SP3",reciveSp3Navigation.log);
            }
        }catch (Exception e){
            //mSp3Error=e.toString();
        }
    }


    /**
     * Handler Runables
     * */
    private Runnable runnable_navigation=new Runnable() {
        @Override
        public void run() {
            reloadGpsNavigationUI();
            reloadGpsNavigationHandler.postDelayed(this,1000);
        }
    };

    private Runnable runnable_bd_navigation=new Runnable() {
        @Override
        public void run() {
            reloadBdMessageUI();
            reloadBdNavigationHandler.postDelayed(this,1000);
        }
    };

    private Runnable runnable_sp3_navigation=new Runnable() {
        @Override
        public void run() {
            reloadSp3MessageUI();
            reloadSp3NavigationHandler.postDelayed(this,1000);
        }
    };

    private Runnable runnable_position=new Runnable() {
        @Override
        public void run() {
            reloadLocationUI();
            reloadLocationHandler.postDelayed(this,1000);
        }
    };

    private Runnable runnable_nmea=new Runnable() {
        @Override
        public void run() {
            reloadNMEAUI();
            reloadNMEAHandler.postDelayed(this,1000);
        }
    };

    private Runnable runnable_measurement=new Runnable() {
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    reloadMeasureUI();
                }
            }
            reloadObserveHandler.postDelayed(this,1000);
        }
    };

    /**
     * service listeners
     * */
//    @SuppressLint("NewApi")
//    private GnssNavigationMessage.Callback gnssNavigationMessageListener=new GnssNavigationMessage.Callback() {
//        @Override
//        public void onGnssNavigationMessageReceived(GnssNavigationMessage event) {
//            super.onGnssNavigationMessageReceived(event);
//            Toast.makeText(Renix.this,"Navigation Recived!",Toast.LENGTH_SHORT).show();
//            gnssNavigationMessage=event;
//            //Log.d("Navigation Message",event.toString());
//        }
//        @Override
//        public void onStatusChanged(int status) {
//            super.onStatusChanged(status);
//            Log.e("gnssNavgaition",String.valueOf(status));
//        }
//        @Override
//        public int hashCode() {
//            return super.hashCode();
//        }
//
//        @Override
//        public boolean equals(@Nullable Object obj) {
//            return super.equals(obj);
//        }
//
//        @NonNull
//        @Override
//        protected Object clone() throws CloneNotSupportedException {
//            return super.clone();
//        }
//
//        @NonNull
//        @Override
//        public String toString() {
//            return super.toString();
//        }
//
//        @Override
//        protected void finalize() throws Throwable {
//            super.finalize();
//        }
//    };

    @SuppressLint("NewApi")
    private GnssMeasurementsEvent.Callback gnssMeasurementListener=new GnssMeasurementsEvent.Callback() {
        @Override
        public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
            super.onGnssMeasurementsReceived(eventArgs);
            // TODO process GNSS measurements
            //Log.e("Measurement",String.valueOf(eventArgs.getMeasurements().size()));
            // measurementsEvent=eventArgs;
            measurementsEvent=eventArgs;
        }

        @Override
        public void onStatusChanged(int status) {
            super.onStatusChanged(status);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return super.equals(obj);
        }

        @NonNull
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }
    };

    private LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            // TODO process location
            // mlocation=location;
            mLocation=location;
        }
        @Override
        public void onProviderEnabled(@NonNull String provider) {
            //System.out.println("DEBUG 2");
            //Toast.makeText(MainActivity.this,"onProviderEnabled",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            //System.out.println("DEBUG 3");
            //Toast.makeText(MainActivity.this,"onProviderDisabled",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //System.out.println("DEBUG 4");
            //Toast.makeText(MainActivity.this,"onStatusChanged",Toast.LENGTH_LONG).show();

        }
    };

    @SuppressLint("NewApi")
    private OnNmeaMessageListener nmeaMessageListener=new OnNmeaMessageListener() {
        @Override
        public void onNmeaMessage(String s, long l) {
            mNMEA=s;
            // nmea=s;
        }
    };

    /**
     * register or unregister the listener functions
     * */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void registerListener(){
        // navigationLocationManager.registerGnssNavigationMessageCallback(gnssNavigationMessageListener,handler_navigation);
        measurementLocationManager.registerGnssMeasurementsCallback(gnssMeasurementListener);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1,0.0f,locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,0.1f,locationListener);

        nmeaMangaer.addNmeaListener(nmeaMessageListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void unregisterListner(){
        // navigationLocationManager.unregisterGnssNavigationMessageCallback(gnssNavigationMessageListener);
        measurementLocationManager.unregisterGnssMeasurementsCallback(gnssMeasurementListener);

        locationManager.removeNmeaListener(nmeaMessageListener);
    }
    /**
     * ask for permission function
     * */
    private void askForPermission(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,permissions,1);
        }
    }
    /**
     * reload UI functions
     * */
    private void reloadGpsNavigationUI(){
        if(mGpsNavigationMessage.length()==0||mGpsError.length()>0){
            //mGpsNavigationView.setText(mGpsNavigationMessage);
            mGpsNavigationView.setText(mGpsError);
//            if(mGpsError.length()>0){
//                Toast.makeText(this,"Gps Error:"+mGpsError,Toast.LENGTH_SHORT).show();
//            }
            return;
        }

        String[] lines=mGpsNavigationMessage.split("\n");
        String str="";
        for (int i=0;i<lines.length/2;i++){
            str+=lines[i]+"\n";

            String[] tmpArr=lines[i].split(" ");
            Vector<String> useStr=new Vector<String>();
            for(String tmpStr:tmpArr){
                if(tmpStr.length()>0){
                    useStr.add(tmpStr);
                }
            }

            if(useStr.size()==3){
                if(useStr.elementAt(0).equals("END")&&
                    useStr.elementAt(1).equals("OF")&&
                    useStr.elementAt(2).equals("HEADER")){
                    break;
                }
            }
        }
        for (int i=lines.length-8;i<lines.length;i++){
            str+=lines[i]+"\n";
        }

        mGpsNavigationView.setText(str);

        if(isRecording){
            gpsNavigationFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"ZT-"+timeStr+reciveGPSNavigation.fileName);
            if(!gpsNavigationFile.exists()){
                try {
                    gpsNavigationFile.createNewFile();
                    FileOutputStream fileOutputStream=new FileOutputStream(gpsNavigationFile,false);
                    fileOutputStream.write(mGpsNavigationMessage.getBytes());
                    fileOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void reloadBdMessageUI(){
        if(mBdNavigationMessage.length()==0||mBdError.length()>0){
            //mBdNavigationView.setText(mBdNavigationMessage);
            mBdNavigationView.setText(mBdError);
//            if(mBdError.length()>0){
//                Toast.makeText(this,"Bd Error:"+mBdError,Toast.LENGTH_SHORT).show();
//            }
            return;
        }

        String[] lines=mBdNavigationMessage.split("\n");
        String str="";
        for (int i=0;i<lines.length/2;i++){
            str+=lines[i]+"\n";

            String[] tmpArr=lines[i].split(" ");
            Vector<String> useStr=new Vector<String>();
            for(String tmpStr:tmpArr){
                if(tmpStr.length()>0){
                    useStr.add(tmpStr);
                }
            }

            if(useStr.size()==3){
                if(useStr.elementAt(0).equals("END")&&
                        useStr.elementAt(1).equals("OF")&&
                        useStr.elementAt(2).equals("HEADER")){
                    break;
                }
            }
        }
        for (int i=lines.length-8;i<lines.length;i++){
            str+=lines[i]+"\n";
        }

        mBdNavigationView.setText(str);

        if(isRecording){
            bdNavigationFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"ZT-"+timeStr+reciveBDNavigation.fileName);
            if(!bdNavigationFile.exists()){
                try {
                    bdNavigationFile.createNewFile();
                    FileOutputStream fileOutputStream=new FileOutputStream(bdNavigationFile,false);
                    fileOutputStream.write(mBdNavigationMessage.getBytes());
                    fileOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void reloadSp3MessageUI(){
        if(mSp3NavigationMessage.length()==0||mSp3Error.length()>0){
            //mSp3NavigationView.setText(mSp3NavigationMessage);
            mSp3NavigationView.setText(mSp3Error);
//            if(mSp3Error.length()>0){
//                Toast.makeText(this,"Sp3 Error:"+mSp3Error,Toast.LENGTH_SHORT).show();
//            }
            return;
        }

        String[] lines=mSp3NavigationMessage.split("\n");
        String str="";
        for (int i=0;i<lines.length/2;i++){
            str+=lines[i]+"\n";

            String[] tmpArr=lines[i].split(" ");
            Vector<String> useStr=new Vector<String>();
            for(String tmpStr:tmpArr){
                if(tmpStr.length()>0){
                    useStr.add(tmpStr);
                }
            }

            if(useStr.size()==1){
                if(useStr.elementAt(0).equals("/*")){
                    break;
                }
            }
        }

        int begin_index=lines.length-2;
        if(begin_index>0){
            for (int i=begin_index;i<lines.length;i++){
                str+=lines[i]+"\n";
            }
        }

        mSp3NavigationView.setText(str);

        if(isRecording){
            sp3NavigationFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"ZT-"+timeStr+reciveSp3Navigation.fileName);
            if(!sp3NavigationFile.exists()){
                try {
                    sp3NavigationFile.createNewFile();
                    FileOutputStream fileOutputStream=new FileOutputStream(sp3NavigationFile,false);
                    fileOutputStream.write(mSp3NavigationMessage.getBytes());
                    fileOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void reloadLocationUI(){
        if(mLocation!=null){
            String str=String.format("Lat:%f,Lon:%f,Height:%f,Accuaracy:%f,Provider:%s",
                    mLocation.getLatitude(),
                    mLocation.getLongitude(),
                    mLocation.getAltitude(),
                    mLocation.getAccuracy(),
                    mLocation.getProvider());

            mLocatioView.setText(str);

            if(isRecording){
                Integer[] nowTime=getTime();
                String nowTimeStr=String.format("%s %s %s %s %s %s",
                        String.valueOf(nowTime[0]),String.valueOf(nowTime[1]),String.valueOf(nowTime[2]),
                        String.valueOf(nowTime[3]),String.valueOf(nowTime[4]),String.valueOf(nowTime[5]));

                String bodyStr=String.format("%f %f %f %f",
                        mLocation.getLatitude(),mLocation.getLongitude(),mLocation.getAltitude(),mLocation.getAccuracy());

                String tmpStr=nowTimeStr+" "+bodyStr+"\n";
                try {
                    FileOutputStream fileOutputStream=new FileOutputStream(locationFile,true);
                    fileOutputStream.write(tmpStr.getBytes());
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void reloadNMEAUI(){
        if(mNMEA!=null){
            mNmeaView.setText(mNMEA);
            if(isRecording){
                try {
                    FileOutputStream fileOutputStream=new FileOutputStream(nmeaFile,true);
                    fileOutputStream.write(mNMEA.getBytes());
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void reloadMeasureUI(){
        if(measurementsEvent!=null){
            //Long clockNans= (long) (measurementsEvent.getClock().getTimeNanos()-measurementsEvent.getClock().getBiasNanos()-measurementsEvent.getClock().getFullBiasNanos());
            //Long secondNans=Math.floorMod(clockNans,minNans);
            //double second=secondNans/(1e9*1.0);
            //double second2=second-measurementsEvent.getClock().getLeapSecond()/(1e9*1.0);

            Long UtcTimeNanos= (long) (measurementsEvent.getClock().getTimeNanos()-(
                                measurementsEvent.getClock().getFullBiasNanos()+measurementsEvent.getClock().getBiasNanos())-
                                measurementsEvent.getClock().getLeapSecond()*1000000000L);

            Long minNans=Math.floorMod(UtcTimeNanos,hourNans);

            Long minuets=(long)(minNans/(60L*1000000000L));
            Long ns=minNans-minuets*(60L*1000000000L);
            double UtCSecond=(double)(ns/1000000000.0D);

            String str="";
            for(GnssMeasurement gnssMeasurement:measurementsEvent.getMeasurements()){
                double pseudorange=MyGnssValue.calcPseudorange(measurementsEvent.getClock(),gnssMeasurement);
                double CN0=gnssMeasurement.getCn0DbHz();
                double doppler=gnssMeasurement.getPseudorangeRateMetersPerSecond();
                double carrier=gnssMeasurement.getAccumulatedDeltaRangeMeters();
                int svid=gnssMeasurement.getSvid();
                String type=MyGnssValue.getConstellationType(gnssMeasurement.getConstellationType());
                str+=String.format("%s-%s Pseudorange:%f m, CN0:%f dB-Hz, Doppler:%f m, Carrier:%f m, min2:%s  second2:%f s, Leap Second:%s s\n",
                        type,String.valueOf(svid),pseudorange,CN0,doppler,carrier,
                        String.valueOf(minuets),
                        UtCSecond,
                        String.valueOf(measurementsEvent.getClock().getLeapSecond()));
            }

            mMeasureView.setText(str);

            if(isRecording){
                Integer[] nowTime=getTime();
                String nowTimeStr=String.format("%s %s %s %s %s %s",
                        String.valueOf(nowTime[0]),String.valueOf(nowTime[1]),String.valueOf(nowTime[2]),
                        String.valueOf(nowTime[3]),String.valueOf(nowTime[4]),String.valueOf(nowTime[5]));

                String tmpStr="";
                for(GnssMeasurement gnssMeasurement:measurementsEvent.getMeasurements()){
                    double pseudorange=MyGnssValue.calcPseudorange(measurementsEvent.getClock(),gnssMeasurement);
                    double CN0=gnssMeasurement.getCn0DbHz();
                    double doppler=gnssMeasurement.getPseudorangeRateMetersPerSecond();
                    double carrier=gnssMeasurement.getAccumulatedDeltaRangeMeters();
                    String svid=String.valueOf(gnssMeasurement.getSvid());
                    String type=MyGnssValue.getConstellationType(gnssMeasurement.getConstellationType());

                    String localSatellite=String.format("%s %s %f %f %f %f, %s %.9f %s",
                            type,svid,pseudorange,doppler,carrier,CN0,
                            String.valueOf(minuets),
                            UtCSecond,
                            String.valueOf(measurementsEvent.getClock().getLeapSecond()));

                    String detailMeasure="";
                    try {
                        detailMeasure+=String.valueOf(measurementsEvent.getClock().getTimeNanos())+" "+
                                String.valueOf(measurementsEvent.getClock().getLeapSecond())+" "+
                                String.valueOf(measurementsEvent.getClock().getBiasNanos())+" "+
                                String.valueOf(measurementsEvent.getClock().getFullBiasNanos())+" "+
                                String.valueOf(measurementsEvent.getClock().getDriftNanosPerSecond())+" "+
                                String.valueOf(measurementsEvent.getClock().getReferenceCodeTypeForIsb())+" "+
                                String.valueOf(measurementsEvent.getClock().getReferenceConstellationTypeForIsb())+" "+
                                String.valueOf(measurementsEvent.getClock().getReferenceConstellationTypeForIsb())+" "+
                                String.valueOf(measurementsEvent.getClock().getDriftUncertaintyNanosPerSecond())+" "+
                                String.valueOf(measurementsEvent.getClock().getBiasUncertaintyNanos())+" "+
                                String.valueOf(measurementsEvent.getClock().getElapsedRealtimeNanos())+" "+
                                String.valueOf(measurementsEvent.getClock().getElapsedRealtimeUncertaintyNanos())+" "+
                                String.valueOf(measurementsEvent.getClock().getHardwareClockDiscontinuityCount())+" "+
                                String.valueOf(measurementsEvent.getClock().getTimeUncertaintyNanos())+" "+
                                String.valueOf(gnssMeasurement.getSvid())+" "+
                                MyGnssValue.getConstellationType(gnssMeasurement.getConstellationType())+" "+
                                String.valueOf(gnssMeasurement.getCn0DbHz())+" "+
                                String.valueOf(gnssMeasurement.getPseudorangeRateUncertaintyMetersPerSecond())+" "+
                                String.valueOf(gnssMeasurement.getAccumulatedDeltaRangeMeters())+" "+
                                String.valueOf(gnssMeasurement.getAccumulatedDeltaRangeState())+" "+
                                String.valueOf(gnssMeasurement.getSnrInDb())+" "+
                                String.valueOf(gnssMeasurement.getAutomaticGainControlLevelDb())+" "+
                                String.valueOf(gnssMeasurement.getAccumulatedDeltaRangeUncertaintyMeters())+" "+
                                String.valueOf(gnssMeasurement.getBasebandCn0DbHz())+" "+
                                String.valueOf(gnssMeasurement.getPseudorangeRateMetersPerSecond())+" "+
                                String.valueOf(gnssMeasurement.getAutomaticGainControlLevelDb())+" "+
                                String.valueOf(gnssMeasurement.getCarrierFrequencyHz())+" "+
                                String.valueOf(gnssMeasurement.getCodeType())+" "+
                                String.valueOf(gnssMeasurement.getAccumulatedDeltaRangeState())+" "+
                                String.valueOf(gnssMeasurement.getFullInterSignalBiasNanos())+" "+
                                String.valueOf(gnssMeasurement.getFullInterSignalBiasUncertaintyNanos())+" "+
                                String.valueOf(gnssMeasurement.getMultipathIndicator())+" "+
                                String.valueOf(gnssMeasurement.getReceivedSvTimeNanos())+" "+
                                String.valueOf(gnssMeasurement.getReceivedSvTimeUncertaintyNanos())+" "+
                                String.valueOf(gnssMeasurement.getSatelliteInterSignalBiasNanos())+" "+
                                String.valueOf(gnssMeasurement.getSatelliteInterSignalBiasUncertaintyNanos())+" "+
                                String.valueOf(gnssMeasurement.getState())+" "+
                                String.valueOf(gnssMeasurement.getTimeOffsetNanos())+" "+
                                String.valueOf(gnssMeasurement.getCarrierCycles())+" "+
                                String.valueOf(gnssMeasurement.getCarrierPhase())+" "+
                                String.valueOf(gnssMeasurement.getCarrierPhaseUncertainty());
                    }catch (Exception e){

                    }



                    tmpStr+=nowTimeStr+" "+localSatellite+":"+detailMeasure+"\n";
                }

                try {
                    FileOutputStream fileOutputStream=new FileOutputStream(observeFile,true);
                    fileOutputStream.write(tmpStr.getBytes());
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Integer[] getTime(){
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        int Year=calendar.get(Calendar.YEAR);
        int Month=calendar.get(Calendar.MONTH)+1;
        int Day=calendar.get(Calendar.DAY_OF_MONTH);
        int Hour=calendar.get(Calendar.HOUR_OF_DAY);
        int Min=calendar.get(Calendar.MINUTE);
        int Second=calendar.get(Calendar.SECOND);

        return new Integer[]{Year,Month,Day,Hour,Min,Second};
    }



    private void renixSaveClick(){
        if(isRecording){
            isRecording=false;
            button.setText(str_begin_record);
        }else{
            isRecording=true;
            button.setText(str_stop_record);

            Calendar calendar=Calendar.getInstance();
            int Year=calendar.get(Calendar.YEAR);
            int Day=calendar.get(Calendar.DAY_OF_YEAR);
            int Hour=calendar.get(Calendar.HOUR_OF_DAY);
            int Min=calendar.get(Calendar.MINUTE);

            timeStr=String.format("%s%s-%s%s",
                    String.valueOf(Year),String.valueOf(Day),String.valueOf(Hour),String.valueOf(Min));

            observeFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"ZT-"+timeStr+"-observe.txt");
            nmeaFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"ZT-"+timeStr+"-nmea.txt");
            locationFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"ZT-"+timeStr+"-location.txt");

            if(!observeFile.exists()){
                try {
                    observeFile.createNewFile();
                    FileOutputStream observeStream=new FileOutputStream(observeFile,false);
                    String tmpTxt="Year Month Day Hour Min Second Type SVID Psedurange Doppler Carrier CN0\n";
                    try {
                        observeStream.write(tmpTxt.getBytes());
                        observeStream.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if(!nmeaFile.exists()){
                try {
                    nmeaFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if(!locationFile.exists()){
                try {
                    locationFile.createNewFile();
                    FileOutputStream locationStram=new FileOutputStream(locationFile,false);
                    String tmpTxt="Year Month Day Hour Min Second Lat Lon Height Accuracy\n";
                    locationStram.write(tmpTxt.getBytes());
                    locationStram.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

//    public class mGnssMeasurement{
//        public double pseudorange;
//        public double doppler_meter_pers;
//        public double CN0;
//        public double carrierPhase_meter;
//    };
}