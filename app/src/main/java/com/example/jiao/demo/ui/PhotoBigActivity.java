package com.example.jiao.demo.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.jiao.demo.R;
import com.example.jiao.demo.view.zoomable.ZoomableDraweeView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;


public class PhotoBigActivity extends BaseActivity {
    private ZoomableDraweeView photoImageview;
    private TextView tvDelete;
    private String itemImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(true);
        setContentView(R.layout.photo_layout);
        photoImageview = (ZoomableDraweeView) findViewById(R.id.photo_view);
        tvDelete = (TextView) findViewById(R.id.tv_delete);
        Toolbar photo_toolbar = (Toolbar) findViewById(R.id.toolbar);
        photo_toolbar.setTitle("图片详情");
        setSupportActionBar(photo_toolbar);
//        photo_toolbar.setNavigationIcon(R.drawable.ic_action_arrow);
        photo_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        itemImage = intent.getStringExtra("itemImage");
        Uri imageUri = Uri.parse("file://" + itemImage);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(imageUri)
                .setCallerContext("ZoomableApp")
                .build();
        photoImageview.setController(controller);

    }

//        boolean showDelete = intent.getBooleanExtra("showDelete", false);
//        boolean isSign = intent.getBooleanExtra("isSign", false);
//        if (showDelete) {
//            tvDelete.setVisibility(View.VISIBLE);
//            tvDelete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final LandPhotoDao landPhotoDao =
//                            DBManager.getInstance(GlobalVariableV.context).getWritDao().getLandPhotoDao();
//                    landPhotoDao.queryBuilder().where(LandPhotoDao.Properties.Photos.eq(itemImage))
//                            .rxPlain().list()
//                            .doOnNext(new Action1<List<LandPhoto>>() {
//                                @Override
//                                public void call(List<LandPhoto> landPhotos) {
//                                    landPhotoDao.deleteInTx(landPhotos);
//                                }
//                            })
//                            .subscribe(new Action1<List<LandPhoto>>() {
//                                @Override
//                                public void call(List<LandPhoto> landPhotos) {
//                                    finish();
//                                    EventBus.getDefault().post("");
//                                    EventBus.getDefault().post(landPhotos.get(0));
//                                }
//                            }, RetrofitUtils.getHandErr());
//
//                }
//            });
//        }
//        Uri imageUri = null;
//        if (itemImage.startsWith("http") || itemImage.startsWith("file")) {
//            imageUri = Uri.parse(itemImage);
//        } else {
//            imageUri = Uri.parse("file://"+itemImage);
//        }
//        if(imageUri != null) {
//            if(isSign) {
//                ImagePipeline imagePipeline = Fresco.getImagePipeline();
//                imagePipeline.evictFromMemoryCache(imageUri);
//                imagePipeline.evictFromDiskCache(imageUri);
//            }
//            DraweeController controller = Fresco.newDraweeControllerBuilder()
//                    .setUri(imageUri)
//                    .setCallerContext("ZoomableApp")
//                    .build();
//            photoImageview.setController(controller);
//        }
//    }
}
