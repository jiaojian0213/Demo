package com.example.jiao.demo.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiao.demo.R;
import com.example.jiao.demo.dao.PhotoModelDao;
import com.example.jiao.demo.daomodel.CitySiteModel;
import com.example.jiao.demo.daomodel.PhotoModel;
import com.example.jiao.demo.manager.DBManager;
import com.example.jiao.demo.utils.FileUtils;
import com.example.jiao.demo.utils.PermissionUtils;
import com.example.jiao.demo.view.MyToolbar;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.functions.Action1;

import static android.R.attr.type;

/**
 * Created by jiaojian on 2018/10/17.
 */
@RuntimePermissions
public class CitySiteActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener, PhotoImageAdapter.OnItemClickListener, CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.toolbar)
    MyToolbar toolbar;
    @Bind(R.id.et_serialNum)
    EditText etSerialNum;
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_times)
    EditText etTimes;
    @Bind(R.id.et_address)
    EditText etAddress;
    @Bind(R.id.et_findTime)
    EditText etFindTime;
    @Bind(R.id.et_findCompany)
    EditText etFindCompany;
    @Bind(R.id.et_type)
    EditText etType;
    @Bind(R.id.et_outerArea)
    EditText etOuterArea;
    @Bind(R.id.et_innerArea)
    EditText etInnerArea;
    @Bind(R.id.et_integrity)
    EditText etIntegrity;
    @Bind(R.id.et_height)
    EditText etHeight;
    @Bind(R.id.et_wallComposition)
    EditText etWallComposition;
    @Bind(R.id.et_repairTime)
    EditText etRepairTime;
    @Bind(R.id.et_features)
    EditText etFeatures;
    @Bind(R.id.et_protectType)
    EditText etProtectType;
    @Bind(R.id.et_detail)
    EditText etDetail;
    @Bind(R.id.text_layout)
    AutoLinearLayout textLayout;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.tv_msg)
    TextView tvMsg;
    @Bind(R.id.cb_hasInner)
    CheckBox cbHasInner;
    @Bind(R.id.inner_layout)
    AutoRelativeLayout innerLayout;
    @Bind(R.id.cb_hasWengCity)
    CheckBox cbHasWengCity;
    @Bind(R.id.cb_hasWall)
    CheckBox cbHasWall;
    @Bind(R.id.wallComposition_layout)
    AutoRelativeLayout wallCompositionLayout;
    @Bind(R.id.cb_hasRepaired)
    CheckBox cbHasRepaired;
    @Bind(R.id.repairTime_layout)
    AutoRelativeLayout repairTimeLayout;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private File tempFile;
    private CitySiteModel citySiteModel;
    private PhotoImageAdapter imageAdapter;
    public static int REQULTCODE = 1025;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.citysite_detail_layout);
        ButterKnife.bind(this);

        toolbar.setTitle("城址详情");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                saveInfo();
                Intent intent = new Intent();
                intent.putExtra(CITY_SITE_ID,citySiteModel.getId());
                setResult(REQULTCODE,intent);
                finish();
                return true;
            }
        });

        cbHasInner.setOnCheckedChangeListener(this);
        cbHasRepaired.setOnCheckedChangeListener(this);
        cbHasWall.setOnCheckedChangeListener(this);
        cbHasWengCity.setOnCheckedChangeListener(this);
        innerLayout.setVisibility(View.GONE);
        repairTimeLayout.setVisibility(View.GONE);
        wallCompositionLayout.setVisibility(View.GONE);

        Intent intent = getIntent();
        long city_site_id = intent.getLongExtra(CITY_SITE_ID, 0);

        citySiteModel = DBManager.getInstance(getApplicationContext())
                .getWritDao()
                .getCitySiteModelDao()
                .load(city_site_id);

        initView(citySiteModel);


        String[] pics = {"/mnt/sdcard/Demo/111.jpg",
//                "/mnt/sdcard/Demo/222.jpg",
                "/mnt/sdcard/Demo/333.jpg"};

//        ArrayList<PhotoModel> photoModels = new ArrayList<>();
//        for (int i = 0; i < pics.length; i++) {
//            PhotoModel photoModel = new PhotoModel();
//            photoModel.setPath(pics[i]);
//            photoModels.add(photoModel);
//            photoModels.add(photoModel);
//            photoModels.add(photoModel);
//            photoModels.add(photoModel);
//            photoModels.add(photoModel);
//            photoModels.add(photoModel);
//            photoModels.add(photoModel);
//            photoModels.add(photoModel);
//            photoModels.add(photoModel);
//            photoModels.add(photoModel);
//        }
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        recyclerView.addItemDecoration(new MarginDecoration(getApplicationContext()));
//        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
//                DividerItemDecoration.VERTICAL));
//        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(getApplicationContext(), photoModels);

        imageAdapter = new PhotoImageAdapter(getApplicationContext(), null);
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.setOnItemClickListener(this);
        Logger.i("aaa = " + citySiteModel.getId());

        initPhotoModel();
    }

    public void initPhotoModel() {
        DBManager.getInstance(getApplicationContext()).getWritDao()
                .getPhotoModelDao().queryBuilder()
                .where(PhotoModelDao.Properties.TaskId.eq(citySiteModel.getId()))
                .rxPlain().list()
                .subscribe(new Action1<List<PhotoModel>>() {
                    @Override
                    public void call(List<PhotoModel> photoModels) {
                        imageAdapter.setData(photoModels);
                    }
                });
    }

    public void setTextStr(EditText view, String string) {
        view.setText(string == null ? "" : string);
    }

    public void initView(CitySiteModel citySiteModel) {
        etSerialNum.setText(citySiteModel.getSerialNum());
        etName.setText(citySiteModel.getName());
        etTimes.setText(citySiteModel.getTimes());
        etAddress.setText(citySiteModel.getAddress());
        etFindTime.setText(citySiteModel.getFindTime());
        etFindCompany.setText(citySiteModel.getFindCompany());
        etType.setText(citySiteModel.getType());
        cbHasInner.setChecked(citySiteModel.getHasInner());
        etInnerArea.setText(""+citySiteModel.getInnerArea());
        etOuterArea.setText(""+citySiteModel.getOuterArea());
        cbHasWall.setChecked(citySiteModel.getHasWall());
        cbHasWengCity.setChecked(citySiteModel.getHasWengCity());
        etIntegrity.setText(citySiteModel.getIntegrity());
        etHeight.setText(citySiteModel.getHeight());
        etWallComposition.setText(citySiteModel.getWallComposition());
        cbHasRepaired.setChecked(citySiteModel.getHasRepaired());
        etRepairTime.setText(citySiteModel.getRepairTime());
        etFeatures.setText(citySiteModel.getFeatures());
        etProtectType.setText(citySiteModel.getProtectType());
        etDetail.setText(citySiteModel.getDetail());
    }

    public boolean saveInfo() {
        String serialNum = getTextStr(etSerialNum);
        String name = getTextStr(etName);
        String times = getTextStr(etTimes);
        String address = getTextStr(etAddress);
        String findTime = getTextStr(etFindTime);
        String findCompany = getTextStr(etFindCompany);
        String type = getTextStr(etType);
        boolean hasInner = cbHasInner.isChecked();
        String outerArea = getTextStr(etOuterArea);
        String innerArea = getTextStr(etInnerArea);
        boolean hasWengCity = cbHasWengCity.isChecked();
        boolean hasWall = cbHasWall.isChecked();
        String integrity = getTextStr(etIntegrity);
        String height = getTextStr(etHeight);
        String wallComposition = getTextStr(etWallComposition);
        boolean hasRepaired = cbHasRepaired.isChecked();
        String repairTime = getTextStr(etRepairTime);
        String features = getTextStr(etFeatures);
        String protectType = getTextStr(etProtectType);
        String detail = getTextStr(etDetail);

        citySiteModel.setSerialNum(serialNum);
        citySiteModel.setName(name);
        citySiteModel.setTimes(times);
        citySiteModel.setAddress(address);
        citySiteModel.setFindTime(findTime);
        citySiteModel.setFindCompany(findCompany);
        citySiteModel.setType(type);
        citySiteModel.setHasInner(hasInner);
        citySiteModel.setOuterArea(Double.valueOf(outerArea));
        citySiteModel.setInnerArea(Double.valueOf(innerArea));
        citySiteModel.setHasWall(hasWall);
        citySiteModel.setHasWengCity(hasWengCity);
        citySiteModel.setIntegrity(integrity);
        citySiteModel.setHeight(height);
        citySiteModel.setWallComposition(wallComposition);
        citySiteModel.setHasRepaired(hasRepaired);
        citySiteModel.setRepairTime(repairTime);
        citySiteModel.setFeatures(features);
        citySiteModel.setProtectType(protectType);
        citySiteModel.setDetail(detail);
        DBManager.getInstance(getApplicationContext())
                .getWritDao()
                .getCitySiteModelDao()
                .update(citySiteModel);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("保存").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    public String getTextStr(EditText view) {
        return view.getText().toString().trim();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CitySiteActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    void showRationaleForCamera(PermissionRequest request) {
        PermissionUtils.showRationaleDialog(this, "为保证app功能正常运行，请授权允许相机权限", request);
    }

    @OnPermissionDenied({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    void onCameraDenied() {
        Toast.makeText(this, "没有获取到相机权限", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    void showNeverAskForCamera() {
        if (PermissionUtils.checkPermission(this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {            //点击始终允许时也会调用
            showCameraAction();
        } else {
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
                PhotoModel photoModel = new PhotoModel();
                photoModel.setPath(tempFile.getAbsolutePath());
                photoModel.setTaskId(citySiteModel.getId());
                imageAdapter.addData(photoModel);
                DBManager.getInstance(getApplicationContext()).getWritDao()
                        .getPhotoModelDao().rxPlain().insert(photoModel)
                        .subscribe(new Action1<PhotoModel>() {
                            @Override
                            public void call(PhotoModel photoModel) {

                            }
                        });
            }
        } else {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    @Override
    public void onGlobalLayout() {
        try {
            final int height = recyclerView.getHeight();

            final int desireSize = getResources().getDimensionPixelOffset(R.dimen.image_size);
            final int numCount = 3;//photo_gridView.getWidth() / desireSize;
            final int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);

            //图片宽度
            int columnWidth = (recyclerView.getWidth() - columnSpace * (numCount - 1)) / numCount;
            imageAdapter.setItemSize(columnWidth, type);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } else {
                recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraClick(View view) {
        CitySiteActivityPermissionsDispatcher.openCameraWithCheck(this);
    }

    @Override
    public void onImageClick(View view, int position, PhotoModel photoModel) {
//        Logger.i("点击");
        Intent intent = new Intent(getApplicationContext(), PhotoBigActivity.class);
        intent.putExtra("itemImage", "" + photoModel.getPath());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(View view, int position, final PhotoModel photoModel) {
        showDialog("您是否确定删除图片？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imageAdapter.deleteItem(photoModel);
                DBManager.getInstance(getApplicationContext()).getWritDao()
                        .getPhotoModelDao().delete(photoModel);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.cb_hasInner:
                innerLayout.setVisibility(b?View.VISIBLE:View.GONE);
                break;
            case R.id.cb_hasRepaired:
                repairTimeLayout.setVisibility(b?View.VISIBLE:View.GONE);
                break;
            case R.id.cb_hasWall:
                wallCompositionLayout.setVisibility(b?View.VISIBLE:View.GONE);
                break;
        }
    }
}
