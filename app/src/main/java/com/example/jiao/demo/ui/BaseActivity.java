package com.example.jiao.demo.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.example.jiao.demo.Constants;
import com.example.jiao.demo.service.LocationService;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation_swipeback.SwipeBackActivity;


/**
 * Created by jiaojian on 2017/6/27.
 */

public class BaseActivity extends SwipeBackActivity implements ServiceConnection,LocationService.OnLocationChangeListener{

    public LocationService.MyBinder mLocationService;
    public boolean locationIsRun = false;
    private LocationService.OnLocationChangeListener listener;
    private static final String LAYOUT_LINEARLAYOUT = "LinearLayout";
    private static final String LAYOUT_FRAMELAYOUT = "FrameLayout";
    private static final String LAYOUT_RELATIVELAYOUT = "RelativeLayout";
    protected Location location;
    public static final String CITY_SITE_ID = "CITY_SITE_ID";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        if (name.equals(LAYOUT_FRAMELAYOUT)) {
            view = new AutoFrameLayout(context, attrs);
        }

        if (name.equals(LAYOUT_LINEARLAYOUT)) {
            view = new AutoLinearLayout(context, attrs);
        }

        if (name.equals(LAYOUT_RELATIVELAYOUT)) {
            view = new AutoRelativeLayout(context, attrs);
        }

        if (view != null) return view;

        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Logger.i("onServiceConnected = " + (service instanceof LocationService.MyBinder));
        this.mLocationService = (LocationService.MyBinder) service;
        if(mLocationService != null) {
            mLocationService.startLocation();
            mLocationService.setLocationChangeListener(getLocationChangeListener());
        }
        locationIsRun = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Logger.i("onServiceDisconnected");

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(locationIsRun && PermissionUtils.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION
//                    ,Manifest.permission.ACCESS_FINE_LOCATION)){
//            startLocation();
//        }
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        stopLocation(true);
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocation();
    }

    public void startLocation(){
        if(mLocationService == null || (mLocationService != null && !mLocationService.isStarted())) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.putExtra("cityCode","");
            bindService(intent, this, Context.BIND_AUTO_CREATE);
        }
        if(mLocationService != null && !mLocationService.isStarted()){
            mLocationService.startLocation();
        }
    }

    public void stopLocation(){
        stopLocation(false);
    }

    public void stopLocation(boolean isPause){
        if(!isPause)
            locationIsRun = false;
        if(mLocationService != null) {
            mLocationService.stopLocation();
        }
        try {
            unbindService(this);
        }catch(Exception e){}
    }

    public void setLocationChangeListener(LocationService.OnLocationChangeListener listener){
        this.listener = listener;
        if(mLocationService != null)
            mLocationService.setLocationChangeListener(getLocationChangeListener());
    }

    public LocationService.OnLocationChangeListener getLocationChangeListener(){
        return listener == null?this:listener;
    }


    @Override
    public void locationChange(Location location) {
        Constants.latitude = location.getLatitude();
        Constants.longitude = location.getLongitude();
        Point wgspoint = new Point(location.getLongitude(), location.getLatitude());
        Constants.mapPoint = (Point) GeometryEngine.project(wgspoint, SpatialReference.create(4326), SpatialReference.create(3857));
        if(Constants.picSymbol != null)
            Constants.graphic = new Graphic(Constants.mapPoint,Constants.picSymbol);
        this.location = location;
        Logger.i("base location = "+ location.getLatitude() +"--"+ location.getLongitude());
    }

    public void showDialog(String content, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder
                .setTitle("提醒")
                .setMessage(content)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("确定", listener).show();
    }

    public void showDialog(String content, DialogInterface.OnClickListener oklistener,DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder
                .setTitle("提醒")
                .setMessage(content)
                .setNegativeButton("取消", listener)
                .setPositiveButton("确定", oklistener).show();
    }

    public void initGPS() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "请打开GPS", Toast.LENGTH_SHORT).show();
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("请打开GPS连接");
            dialog.setMessage("为了定位更精确，请先打开GPS");
            dialog.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // 转到手机设置界面，用户设置GPS
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    Toast.makeText(getApplicationContext(), "打开后直接点击返回键即可，若不打开返回下次将再次出现", Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                }
            });
            dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {
        }
    }

    public void initGPSNETWORK() {
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Toast.makeText(getApplicationContext(), "请打开GPS", Toast.LENGTH_SHORT).show();
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("请打开GPS连接");
            dialog.setMessage("为了定位更精确，请先打开GPS");
            dialog.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // 转到手机设置界面，用户设置GPS
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    Toast.makeText(getApplicationContext(), "打开后直接点击返回键即可，若不打开返回下次将再次出现", Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                }
            });
            dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                    if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        Toast.makeText(getApplicationContext(), "请打开GPS定位后进入！", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
            dialog.show();
        } else {
        }
    }

    @Override
    public void finish() {
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        try {
            unbindService(this);
        }catch(Exception e){}
        super.finish();
    }
}
