package com.example.newgpscollector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorAdditionalInfo;
import android.hardware.SensorEvent;
import android.hardware.SensorEventCallback;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;

//import org.angmarch.views.NiceSpinner;
//import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.w3c.dom.Text;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TimerTask;

public class MainActivity2 extends AppCompatActivity {
    private ScrollView linearLayout3;
    private TextView clockView;
    private TextView measureText;
    private TextView customText;
    private static String fileName = null;
    private Handler handler = new Handler();
    //private Handler reloadMeasure=new Handler();
    private Handler saveHandler = new Handler();

    private TextView recordButton;

    //private Handler reloadDetailText=new Handler();
    // private NiceSpinner niceSpinner;
    private Spinner spinner;
    private Boolean startRecord = false;

    private Typeface typeface;
    private Typeface measureType;

    //private String measure_null="你猜猜看";
    private String measure_null = "null";
    //private String custom_null="null.,!/";
    private String custom_null = "null";

    private Switch aSwitchImu;
    private Boolean recordIMU = false;

    private SensorManager sensorManager;

    private GnssClock gnssClock = null;
    private GnssClock lastGnssClock=null;
    private Collection<GnssMeasurement> gnssMeasurements = null;

    private LocationManager locationManagerMeasure = null;
    private Boolean hasReload=false;
    private int reloadNum=0;

    private final String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private ImuData imuData=new ImuData();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initLinear3();

        locationManagerMeasure=(LocationManager) getSystemService(LOCATION_SERVICE);
        //MyGnssMeasure.setGnssMeasureListener();
        try {
            registerGnssMeasure();
        }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }


        handler.post(new Runnable() {
            @Override
            public void run() {
                reloadClockText();
                reloadMeasureText();
                autoReloadThreshold();
                handler.postDelayed(this, 1000);
            }
        });

        //reloadMeasure.post(reloadMeasureLoad);

        this.setTitle(R.string.LXC);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
//                //toActivity2();
//                break;
////            case R.id.AGPS:
////                //Toast.makeText(this, "点击了第" + 3 + "个", Toast.LENGTH_SHORT).show();
////                toRenix();
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

//    private void toRenix() {
//        Intent intent = new Intent(this, Renix.class);
//        startActivity(intent);
//    }
//
//    private void toHome() {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//    }
//    private Runnable reloadMeasureLoad=new Runnable() {
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        @Override
//        public void run() {
//            reloadMeasure.postDelayed(this,1000);
//            reloadMeasureText();
//        }
//    };

    private void initLinear3() {
        linearLayout3 = (ScrollView) findViewById(R.id.linear3);
        clockView = (TextView) findViewById(R.id.clockView);
        measureText = (TextView) findViewById(R.id.measureText);
        recordButton = (TextView) findViewById(R.id.recordButton);
        // niceSpinner = (NiceSpinner) findViewById(R.id.selectType);
        spinner=(Spinner)findViewById(R.id.selectType);
        customText = (TextView) findViewById(R.id.custom_measureText);

        aSwitchImu = (Switch) findViewById(R.id.switch_imu);

        // customText.setTextColor(Color.parseColor("#FFA100"));
        customText.setText(custom_null);
        measureText.setText(measure_null);
//        try {
//            typeface = ResourcesCompat.getFont(this, R.font.mm);
//            //customText.setTypeface(typeface);
//        } catch (Exception e) {
//            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
//        }

//        try {
//            measureType = ResourcesCompat.getFont(this, R.font.yufanxinyubold);
//            //measureText.setTypeface(measureType);
//        } catch (Exception e) {
//            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
//        }

        LinkedList<String> selectType = new LinkedList<>(Arrays.asList("GPS", "北斗", "Galileo", "Glonass",
                "Irnss", "Qzss", "Sbas", "全部"));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity2.this,position+"",Toast.LENGTH_LONG).show();
                switch (position){
                    case 0:
                        MyGnssValue.NOW_CONSTELLATION = GnssStatus.CONSTELLATION_GPS;
                        break;
                    case 1:
                        MyGnssValue.NOW_CONSTELLATION = GnssStatus.CONSTELLATION_BEIDOU;
                        break;
                    case 2:
                        MyGnssValue.NOW_CONSTELLATION = GnssStatus.CONSTELLATION_GALILEO;
                        break;
                    case 3:
                        MyGnssValue.NOW_CONSTELLATION = GnssStatus.CONSTELLATION_GLONASS;
                        break;
                    case 4:
                        MyGnssValue.NOW_CONSTELLATION = GnssStatus.CONSTELLATION_IRNSS;
                        break;
                    case 5:
                        MyGnssValue.NOW_CONSTELLATION = GnssStatus.CONSTELLATION_QZSS;
                        break;
                    case 6:
                        MyGnssValue.NOW_CONSTELLATION = GnssStatus.CONSTELLATION_SBAS;
                        break;
                    case 7:
                        MyGnssValue.NOW_CONSTELLATION = MyGnssValue.CONSTELLATION_ALL;
                        break;
                    default:
                        break;
                }
                Toast.makeText(MainActivity2.this,selectType.get(position),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aSwitchImu.setEnabled(true);

        initSensor();

    }

    private void initSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
    }

    @SuppressLint("NewApi")
    private SensorEventListener sensorEventListener = new SensorEventCallback() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            super.onSensorChanged(event);
            // imuData.time=System.currentTimeMillis();
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    imuData.acc=event.values;
                    imuData.isNewAcc=true;
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    imuData.gyr=event.values;
                    imuData.isNewGyr=true;
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    imuData.mag=event.values;
                    imuData.isNewMag=true;
                    break;
            }

            if(imuData.isNewMag&&imuData.isNewGyr&&imuData.isNewAcc){
                imuData.isNewAcc=false;
                imuData.isNewGyr=false;
                imuData.isNewMag=false;
//                imuData.time=System.currentTimeMillis();
//                Calendar calendar=Calendar.getInstance();
//                imuData.time=calendar.getTimeInMillis()*0.001f;
                float[] utcTime = MyGnssValue.getUTCTime();
                imuData.time=utcTime;
                // Log.e("Debug",imuData.toString());
                if(startRecord&&recordIMU){
                    Write.writeIMU(fileName,MainActivity2.this,true,imuData.toString());
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            super.onAccuracyChanged(sensor, accuracy);
        }

        @Override
        public void onFlushCompleted(Sensor sensor) {
            super.onFlushCompleted(sensor);
        }

        @Override
        public void onSensorAdditionalInfo(SensorAdditionalInfo info) {
            super.onSensorAdditionalInfo(info);
        }
    };

    public void startRecord(View view) {
        if (startRecord == true) {
            saveHandler.removeCallbacks(saveRunnable);
            Toast.makeText(MainActivity2.this, "停止记录:" + fileName, Toast.LENGTH_LONG).show();
            //Toast.makeText(this,"recording",Toast.LENGTH_LONG).show();
            recordButton.setText("开始记录");
            startRecord = false;
            fileName = null;
            aSwitchImu.setEnabled(true);
            return;
        }
        //AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //final EditText editText=new EditText(this);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            String tmpStr="Test_"+MyGnssMeasure.getDate()+"_";
//            editText.setText(tmpStr);
//        }
//        builder.setView(editText);
//        builder.setTitle("记录文件保存");
//        builder.setMessage("文件保存至：文件/documents/");
//        builder.setPositiveButton("记录", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
////                if(!startRecord){
//                    if(editText.getText().length()==0){
//                        Toast.makeText(MainActivity2.this,"请输入文件名",Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    fileName=editText.getText()+".csv";
//                    Write.write(fileName,"",MainActivity2.this,false);
//                    Toast.makeText(MainActivity2.this,"记录开始",Toast.LENGTH_LONG).show();
//                    saveHandler.post(saveRunnable);
//                    recordButton.setText("停止记录");
//
//                    startRecord = true;
////                }else{
////                    startRecord = false;
////                    recordButton.setText("开始记录");
////                    saveHandler.removeCallbacks(saveRunnable);
////                    Toast.makeText(MainActivity2.this,"记录结束："+fileName,Toast.LENGTH_LONG).show();
////                }
//            }
//        });
//        builder.show();
        float[] utcTime = MyGnssValue.getUTCTime();
        fileName = String.format("Test_%04.0f%02.0f%02.0f_%02.0f%02.0f%02.0f.csv",
                utcTime[0], utcTime[1], utcTime[2], utcTime[3], utcTime[4], utcTime[5]);
        Write.write(fileName, "", this, false);
        saveHandler.post(saveRunnable);
        Toast.makeText(this, "记录开始:" + fileName, Toast.LENGTH_LONG).show();
        recordButton.setText("停止记录");

        startRecord = true;
        recordIMU = aSwitchImu.isChecked();
        if (recordIMU) {
            Toast.makeText(this, "记录IMU数据", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "不记录IMU数据", Toast.LENGTH_LONG).show();
        }
        aSwitchImu.setEnabled(false);
    }

    private Runnable saveRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            saveHandler.postDelayed(this, 1000);
            float[] utcTime = MyGnssValue.getUTCTime();
            String utcStr=String.format("UTC Time,%04.0f-%02.0f-%02.0f %02.0f:%02.0f:%02.3f\n",
                    utcTime[0], utcTime[1], utcTime[2], utcTime[3], utcTime[4], utcTime[5]);
            Write.write(fileName, utcStr, MainActivity2.this, true);
            if (gnssClock == null || gnssMeasurements == null) {
                Write.write(fileName,"GNSS:\n",MainActivity2.this,true);
                return;
            }
            Write.write(fileName,"GNSS:\n",MainActivity2.this,true);
            Write.writeMeasureNew(fileName, gnssMeasurements, gnssClock,
                        MainActivity2.this, true);
        }
    };

//    public void saveData(View view) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        final EditText editText = new EditText(this);
//        builder.setView(editText);
//        builder.setTitle("请输入文件名");
//        builder.setMessage("保存至：文件/documents/");
//        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if (editText.getText().length() == 0) {
//                    Toast.makeText(MainActivity2.this, "请输入文件名", Toast.LENGTH_SHORT).show();
//                    return;
//                }
////                Write.writeMeasure(editText.getText()+".csv",MyGnssMeasure.gnssMeasurements,
////                        MyGnssMeasure.gnssClock,MainActivity2.this,false);
//                Write.writeMeasureNew(editText.getText() + ".csv", MyGnssMeasure.gnssMeasurements,
//                        MyGnssMeasure.gnssClock, MainActivity2.this, false);
//                Toast.makeText(MainActivity2.this, "写入成功", Toast.LENGTH_LONG).show();
//            }
//        });
//        builder.show();
//    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    // @SuppressLint("MissingPermission")
    private void registerGnssMeasure() {
        //locationManagerMeasure = (LocationManager) getSystemService(LOCATION_SERVICE);
        // MyGnssMeasure.setGnssMeasureListener();
        // locationManagerMeasure.registerGnssMeasurementsCallback(MyGnssMeasure.GnssMeasureListener);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
        // locationManagerMeasure.registerGnssMeasurementsCallback(gnssMeasurementEvent);

        //locationManagerMeasure.registerGnssMeasurementsCallback(MyGnssMeasure.GnssMeasureListener);
        locationManagerMeasure.registerGnssMeasurementsCallback(gnssMeasurementEvent);

//        locationManagerMeasure.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
//            @Override
//            //@RequiresApi(api = Build.VERSION_CODES.N)
//            public void onLocationChanged(@NonNull Location location) {
//            }
//        });
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void reloadThreshold(View view){
        if(locationManagerMeasure!=null){
            locationManagerMeasure.unregisterGnssMeasurementsCallback(gnssMeasurementEvent);
            locationManagerMeasure.registerGnssMeasurementsCallback(gnssMeasurementEvent);
            Toast.makeText(this,"reload finished!",Toast.LENGTH_LONG).show();
        }
    }

    //@SuppressLint("NewApi")
    @SuppressLint("NewApi")
    private GnssMeasurementsEvent.Callback gnssMeasurementEvent=new GnssMeasurementsEvent.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
            super.onGnssMeasurementsReceived(eventArgs);
            gnssClock=eventArgs.getClock();
            gnssMeasurements=eventArgs.getMeasurements();
            Log.e("GnssMeasurementRecived",gnssClock.toString());
        }

        @Override
        public void onStatusChanged(int status) {
            super.onStatusChanged(status);
            Log.e("GnssMeasurement Changed",status+"");
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void reloadClockText(){
        String clock=MyGnssValue.getCurrentTime();
        if (gnssClock != null && gnssMeasurements != null) {
            clock+="\n";
            //return;
            clock=clock+String.format("本地硬件时钟：%dns\n",gnssClock.getTimeNanos());
            clock=clock+String.format("本地硬件时钟与GPST的偏差(FullBiasNanos):%dns\n",gnssClock.getFullBiasNanos());
            clock=clock+String.format("FullBiasNanos的亚纳秒级偏差:%fns\n",gnssClock.getBiasNanos());
            if(hasReload){
                clock=clock+String.format("Thread Reloaded: %s",String.valueOf(reloadNum));
            }else {
                clock=clock+String.format("Thread Reloaded: False");
            }
        }


        clockView.setText(clock);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void reloadMeasureText(){
        if(gnssMeasurements==null||gnssClock==null){
            measureText.setText(measure_null);
            customText.setText(custom_null);
            Log.e("Debug","Has Null");
            return;
        }
        Log.e("Debug","Has Normal");
        /**
         * measurement view
         * */
        String text="";
        //int tmpI=1;
        for(GnssMeasurement measurement:gnssMeasurements){
            if(MyGnssValue.NOW_CONSTELLATION!=MyGnssValue.CONSTELLATION_ALL){
                if(measurement.getConstellationType()!= MyGnssValue.NOW_CONSTELLATION){
                    continue;
                }
            }
            //text=text+"Q"+tmpI+":\n";
            text=text+MyGnssValue.getMeasureString(measurement,gnssClock);

            //tmpI++;
        }
        if(gnssMeasurements.size()==0){
            text=measure_null;
        }
        measureText.setText(text);

        /**
         * custom view
         * */
        customText.setText(MyGnssValue.getCustomString(gnssMeasurements));

    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void autoReloadThreshold(){
        if(gnssClock==null||gnssMeasurements==null){
            return;
        }

        if(lastGnssClock==null){
            lastGnssClock=gnssClock;
            return;
        }

        if(lastGnssClock.getTimeNanos()==gnssClock.getTimeNanos()){
            locationManagerMeasure.unregisterGnssMeasurementsCallback(gnssMeasurementEvent);
            locationManagerMeasure.registerGnssMeasurementsCallback(gnssMeasurementEvent);
            hasReload=true;
            Log.e("Reload","reloaded New");
            reloadNum=reloadNum+1;
        }else{
            lastGnssClock=gnssClock;
            hasReload=false;
            Log.e("Reload","Normal");
            reloadNum=0;
        }
    }

    public static class ImuData{
        public float time[]=new float[6];
        public float acc[]=new float[3];
        public boolean isNewAcc=false;
        public float gyr[]=new float[3];
        public boolean isNewGyr=false;
        public float mag[]=new float[3];
        public boolean isNewMag=false;

        public String toString(){
            return String.format("%04.0f-%02.0f-%02.0f %02.0f:%02.0f:%02.3f,%f %f %f,%f %f %f,%f %f %f\n",
                    time[0],time[1],time[2],time[3],time[4],time[5],
                    acc[0],acc[1],acc[2],
                    gyr[0],gyr[1],gyr[2],
                    mag[0],mag[1],mag[2]
            );
        }
    }

}