package com.example.jiao.demo.ui;

import android.graphics.drawable.Drawable;
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
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.example.jiao.demo.Constants;
import com.example.jiao.demo.R;
import com.example.jiao.demo.layer.PeaceGraphicsLayer;
import com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.jiao.demo.Constants.graphic;
import static com.example.jiao.demo.Constants.picSymbol;
import static com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer.TianDiTuTiledMapServiceType.CVA_W;
import static com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer.TianDiTuTiledMapServiceType.IMG_W;
import static com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer.TianDiTuTiledMapServiceType.VEC_W;

public class MainActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.distance)
    TextView distance;
    @Bind(R.id.radioGroupID)
    RadioGroup radioGroupID;
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
    @Bind(R.id.bt_layer1)
    ImageView btLayer1;
    @Bind(R.id.bt_layer2)
    ImageView btLayer2;
    @Bind(R.id.cb_citySite)
    CheckBox cbCitySite;
    @Bind(R.id.cb_trajectory)
    CheckBox cbTrajectory;
    @Bind(R.id.rb_vec_map)
    RadioButton rbVecMap;
    @Bind(R.id.rb_img_map)
    RadioButton rbImgMap;
    private TianDiTuTiledMapServiceLayer vecLayer;
    private TianDiTuTiledMapServiceLayer imgLayer;
    private TianDiTuTiledMapServiceLayer cvaLayer;
    private PeaceGraphicsLayer citySiteLayer;
    private PeaceGraphicsLayer trajectoryLayer;
    private PeaceGraphicsLayer tempLayer;
    private PeaceGraphicsLayer locationLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ArcGISRuntime.setClientId("9yNxBahuPiGPbsdi");//去水印

        Drawable drawable = getResources().getDrawable(R.drawable.location2);
        picSymbol = new PictureMarkerSymbol(drawable);

        vecLayer = new TianDiTuTiledMapServiceLayer(VEC_W);
        cvaLayer = new TianDiTuTiledMapServiceLayer(CVA_W);
        imgLayer = new TianDiTuTiledMapServiceLayer(IMG_W);
        citySiteLayer = new PeaceGraphicsLayer();
        trajectoryLayer = new PeaceGraphicsLayer();
        locationLayer = new PeaceGraphicsLayer();
        tempLayer = new PeaceGraphicsLayer();
        mapview.addLayer(vecLayer);
        mapview.addLayer(imgLayer);
        mapview.addLayer(cvaLayer);
        mapview.addLayer(citySiteLayer);
        mapview.addLayer(trajectoryLayer);
        mapview.addLayer(locationLayer);
        mapview.addLayer(tempLayer);

        rbVecMap.setChecked(true);
        rbImgMap.setChecked(false);
        imgLayer.setVisible(false);
        imgLayer.setVisible(false);

        rbVecMap.setOnCheckedChangeListener(this);
        rbImgMap.setOnCheckedChangeListener(this);
        cbCitySite.setOnCheckedChangeListener(this);
        cbTrajectory.setOnCheckedChangeListener(this);

        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mapview.centerAt(40.08895,116.33662,  true);//116.33662  40.08895
                locationing();
            }
        });

        startLocation();
    }

    public void locationing(){
        mapview.centerAt(location.getLatitude(), location.getLongitude(), true);//116.33662  40.08895
        mapview.setScale(5000);

        graphic = new Graphic(Constants.mapPoint, picSymbol);
        int[] ids = locationLayer.getGraphicIDs();
        if (ids != null && ids.length >0) {
            locationLayer.updateGraphic(ids[0], graphic);
        }else{
            locationLayer.addGraphic(graphic);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.rb_vec_map:
                vecLayer.setVisible(b);
                break;
            case R.id.rb_img_map:
                imgLayer.setVisible(b);
                break;
            case R.id.cb_citySite:
                citySiteLayer.setVisible(b);
                break;
            case R.id.cb_trajectory:
                trajectoryLayer.setVisible(b);
                break;
        }
    }
}
