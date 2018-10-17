package com.example.jiao.demo.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.jiao.demo.R;
import com.example.jiao.demo.daomodel.CitySiteModel;
import com.example.jiao.demo.manager.DBManager;
import com.example.jiao.demo.utils.FileUtils;
import com.example.jiao.demo.utils.PermissionUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by jiaojian on 2018/10/17.
 */
@RuntimePermissions
public class CitySiteActivity extends BaseActivity {

    private File tempFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citysite_detail_layout);
        Intent intent = getIntent();
        long city_site_id = intent.getLongExtra("CITY_SITE_ID", 0);

        CitySiteModel citySiteModel = DBManager.getInstance(getApplicationContext())
                .getWritDao()
                .getCitySiteModelDao()
                .load(city_site_id);

        Logger.i("aaa = " + citySiteModel.getId());
//        CitySiteActivityPermissionsDispatcher.openCameraWithCheck(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CitySiteActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void openCamera() {
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        //我把代码也给你吧  最后list的size大于0就说明可以打开相机才是正常 然后看看打不开有没有报什么异常
        if (list != null && list.size() > 0) {
            showCameraAction();
        }
    }

    @OnShowRationale({Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    void showRationaleForCamera(PermissionRequest request) {
        PermissionUtils.showRationaleDialog(this,"为保证app功能正常运行，请授权允许相机权限", request);
    }

    @OnPermissionDenied({Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    void onCameraDenied() {
        Toast.makeText(this, "没有获取到相机权限", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    void showNeverAskForCamera() {
        if (PermissionUtils.checkPermission(this,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)){            //点击始终允许时也会调用
            showCameraAction();
        }else {
            showCameraAction();
//            PermissionUtils.showNeverAskDialog(this);
        }
    }

    /**
     * 选择相机
     */
    private void showCameraAction() {
        try {
            // 跳转到系统照相机
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                // 设置系统相机拍照后的输出路径
                // 创建临时文件
                tempFile = FileUtils.createTmpFile(this);
                Uri uri = Uri.parse("file://" + tempFile.getAbsolutePath());
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);    //这一步很重要。给目标应用一个临时的授权。
                startActivityForResult(cameraIntent, 10000);
            } else {
                Toast.makeText(getApplicationContext(), "错误", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (tempFile != null) {
                Log.i("info", "---" + tempFile.getAbsolutePath());
//                EventBus.getDefault().post(new PhotoEvent(tempFile));
            }
        } else {
//            EventBus.getDefault().post(new PhotoEvent(null));
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

}
