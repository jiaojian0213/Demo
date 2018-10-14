package com.example.jiao.demo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esri.android.map.MapView;
import com.esri.android.runtime.ArcGISRuntime;
import com.example.jiao.demo.R;
import com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer.TianDiTuTiledMapServiceType.CVA_W;
import static com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer.TianDiTuTiledMapServiceType.IMG_W;
import static com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer.TianDiTuTiledMapServiceType.VEC_W;

public class MainActivity extends BaseActivity {

    @Bind(R.id.distance)
    TextView distance;
    @Bind(R.id.vec_map)
    RadioButton vecMap;
    @Bind(R.id.img_map)
    RadioButton imgMap;
    @Bind(R.id.radioGroupID)
    RadioGroup radioGroupID;
    @Bind(R.id.cb_land_map)
    CheckBox cbLandMap;
    @Bind(R.id.land_map)
    TextView landMap;
    @Bind(R.id.cb_cun_map)
    CheckBox cbCunMap;
    @Bind(R.id.tv_cun_map)
    TextView tvCunMap;
    @Bind(R.id.cb_landcollection_map)
    CheckBox cbLandcollectionMap;
    @Bind(R.id.tv_landcollection_map)
    TextView tvLandcollectionMap;
    @Bind(R.id.map_layer)
    RelativeLayout mapLayer;
    @Bind(R.id.bt_layer)
    ImageView btLayer;
    @Bind(R.id.bt_location)
    ImageView btLocation;
    @Bind(R.id.controller_layout)
    RelativeLayout controllerLayout;
    @Bind(R.id.mapview)
    MapView mapview;
    private TianDiTuTiledMapServiceLayer vecLayer;
    private TianDiTuTiledMapServiceLayer imgLayer;
    private TianDiTuTiledMapServiceLayer cvaLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ArcGISRuntime.setClientId("9yNxBahuPiGPbsdi");//去水印

        vecLayer = new TianDiTuTiledMapServiceLayer(VEC_W);
        cvaLayer = new TianDiTuTiledMapServiceLayer(CVA_W);
        imgLayer = new TianDiTuTiledMapServiceLayer(IMG_W);
        mapview.addLayer(vecLayer);
        mapview.addLayer(imgLayer);
        mapview.addLayer(cvaLayer);

        vecMap.setChecked(true);
        imgMap.setChecked(false);
        imgLayer.setVisible(false);
        imgLayer.setVisible(false);

        vecMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                vecLayer.setVisible(b);
            }
        });
        imgMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                imgLayer.setVisible(b);
            }
        });

        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mapview.centerAt(40.08895,116.33662,  true);//116.33662  40.08895
                mapview.centerAt(location.getLatitude(),location.getLongitude() , true);//116.33662  40.08895
                mapview.setScale(5000);
            }
        });

        startLocation();
    }
}
