package com.example.jiao.demo;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.util.DisplayMetrics;

import com.example.jiao.demo.crash.CrashHandler;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.orhanobut.logger.AndroidLogTool;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;


/**
 * Created by jiao on 15/3/23.
 */
public class MyApplication extends Application {

//    private static ApplicationComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        Fresco.initialize(this);
//        GlobalVariableV.context = getApplicationContext();
//        try {
//            DBManager.getInstance(getApplicationContext());
//        }catch (Exception e){}
//        catch (Error er){}
//        CategoryUtils.initCategorys();
//        appComponent = DaggerApplicationComponent.builder()
//                .applicationModule(new ApplicationModule(this))
//                .apiModule(new ApiModule())
//                .userModule(new UserModule())
//                .build();
//
//        appComponent.inject(this);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Constants.width = displayMetrics.widthPixels;
        Constants.height = displayMetrics.heightPixels;
//        try {
//            JPushInterface.setDebugMode(false);    // 设置开启日志,发布时请关闭日志
//            JPushInterface.init(this);            // 初始化 JPush
//        } catch (Error e) {
//            e.printStackTrace();
//        }


//        LeakCanary.install(this);
        Logger
            .init("demo")                 // default PRETTYLOGGER or use just init()
            .methodCount(0)                 // default 2
//            .hideThreadInfo()               // default shown
            .logLevel(BuildConfig.DEBUG?LogLevel.FULL:LogLevel.NONE)        // default LogLevel.FULL
            .methodOffset(0)                // default 0
            .logTool(new AndroidLogTool()); // custom log tool, optional

        try {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext());
            crashHandler.scaneCrashFile();
        }catch (Exception e){}
        catch (Error er){}

    }



//    public static ApplicationComponent appComponent(){
//        return appComponent;
//    }
}
