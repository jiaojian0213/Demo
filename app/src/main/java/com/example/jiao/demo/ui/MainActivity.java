package com.example.jiao.demo.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.example.jiao.demo.Constants;
import com.example.jiao.demo.R;
import com.example.jiao.demo.daomodel.CitySiteModel;
import com.example.jiao.demo.layer.PeaceGraphicsLayer;
import com.example.jiao.demo.layer.SketchGraphicsOverlay;
import com.example.jiao.demo.layer.SketchGraphicsOverlayEventListener;
import com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer;
import com.example.jiao.demo.manager.DBManager;
import com.example.jiao.demo.utils.WKTUtils;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

import static com.example.jiao.demo.Constants.graphic;
import static com.example.jiao.demo.Constants.picSymbol;
import static com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer.TianDiTuTiledMapServiceType.CVA_W;
import static com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer.TianDiTuTiledMapServiceType.IMG_W;
import static com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer.TianDiTuTiledMapServiceType.VEC_W;

public class MainActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, SketchGraphicsOverlayEventListener, View.OnClickListener {

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
    @Bind(R.id.pointButton)
    ImageButton pointButton;
    @Bind(R.id.polylineButton)
    ImageButton polylineButton;
    @Bind(R.id.polygonButton)
    ImageButton polygonButton;
    @Bind(R.id.undoButton)
    ImageButton undoButton;
    @Bind(R.id.redoButton)
    ImageButton redoButton;
    @Bind(R.id.clearButton)
    ImageButton clearButton;
    private TianDiTuTiledMapServiceLayer vecLayer;
    private TianDiTuTiledMapServiceLayer imgLayer;
    private TianDiTuTiledMapServiceLayer cvaLayer;
    private PeaceGraphicsLayer citySiteLayer;
    private PeaceGraphicsLayer trajectoryLayer;
    private PeaceGraphicsLayer tempLayer;
    private PeaceGraphicsLayer locationLayer;
    private SketchGraphicsOverlay sketchGraphicsOverlay;
    private Point markerPoint;

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
        sketchGraphicsOverlay = new SketchGraphicsOverlay(mapview, this);

        rbVecMap.setChecked(true);
        rbImgMap.setChecked(false);
        imgLayer.setVisible(false);
        imgLayer.setVisible(false);

        rbVecMap.setOnCheckedChangeListener(this);
        rbImgMap.setOnCheckedChangeListener(this);
        cbCitySite.setOnCheckedChangeListener(this);
        cbTrajectory.setOnCheckedChangeListener(this);

        btLocation.setOnClickListener(this);
        btLayer1.setOnClickListener(this);

        startLocation();

        initData();
    }

    public void initData(){
        DBManager.getInstance(getApplicationContext()).getWritDao()
                .getCitySiteModelDao()
                .rxPlain().loadAll()
                .subscribe(new Action1<List<CitySiteModel>>() {
                    @Override
                    public void call(List<CitySiteModel> citySiteModels) {
                        addCitySitesToLayer(citySiteModels);
                    }
                });
    }

    public void addCitySitesToLayer(List<CitySiteModel> citySiteModels){
        for(int i = 0;i< citySiteModels.size();i++){
            addCitySiteToLayer(citySiteModels.get(i));
        }
    }

    public void addCitySiteToLayer(CitySiteModel citySiteModel){
        Point point = new Point(Double.valueOf(citySiteModel.getLongitude()), Double.valueOf(citySiteModel.getLatitude()));
        point = WKTUtils.change4326To3857(point);
        Map<String, Object> map = new HashMap<>();
        map.put(CITY_SITE_ID,citySiteModel.getId());
        Graphic graphic = new Graphic(point, picSymbol,map);
        citySiteLayer.addGraphic(graphic);
    }

    public void onAddMarkerClick(float x, float y){
        int[] graphicIds = tempLayer.getGraphicIDs();
        if(graphicIds != null && graphicIds.length > 0){
            markerPoint = mapview.toMapPoint(x, y);
            Graphic graphic = new Graphic(markerPoint, picSymbol);
            tempLayer.updateGraphic(graphicIds[0], graphic);
        }else {
            markerPoint = mapview.toMapPoint(x, y);
            Graphic graphic = new Graphic(markerPoint, picSymbol);
            tempLayer.addGraphic(graphic);
        }
    }

    public void locationing() {
        if(location == null)
            return;
        mapview.centerAt(location.getLatitude(), location.getLongitude(), true);//116.33662  40.08895
        mapview.setScale(5000);

        graphic = new Graphic(Constants.mapPoint, picSymbol);
        int[] ids = locationLayer.getGraphicIDs();
        if (ids != null && ids.length > 0) {
            locationLayer.updateGraphic(ids[0], graphic);
        } else {
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

    @Override
    public void onUndoStateChanged(boolean undoEnabled) {
        // Set the undo button's enabled/disabled state based on the event boolean
        undoButton.setEnabled(undoEnabled);
        undoButton.setClickable(undoEnabled);
    }

    @Override
    public void onRedoStateChanged(boolean redoEnabled) {
        // Set the redo button's enabled/disabled state based on the event boolean
            redoButton.setEnabled(redoEnabled);
            redoButton.setClickable(redoEnabled);
    }

    @Override
    public void onClearStateChanged(boolean clearEnabled) {
        // Set the clear button's enabled/disabled state based on the event boolean
        clearButton.setEnabled(clearEnabled);
        clearButton.setClickable(clearEnabled);
    }

    @Override
    public void onDrawingFinished() {
        // Reset the selected state of the drawing buttons when a drawing is finished
        pointButton.setSelected(false);
        polylineButton.setSelected(false);
        polygonButton.setSelected(false);
    }

    /**
     * When the point button is clicked, show it as selected and enable point drawing mode.
     *
     * @param v the button view
     */
    public void pointClick(View v) {
        if (!v.isSelected()) {
            v.setSelected(true);
            sketchGraphicsOverlay.setDrawingMode(SketchGraphicsOverlay.DrawingMode.POINT);
        } else {
            sketchGraphicsOverlay.setDrawingMode(SketchGraphicsOverlay.DrawingMode.NONE);
        }
    }

    /**
     * When the polyline button is clicked, show it as selected and enable polyline drawing mode.
     *
     * @param v the button view
     */
    public void polylineClick(View v) {
        if (!v.isSelected()) {
            v.setSelected(true);
            sketchGraphicsOverlay.setDrawingMode(SketchGraphicsOverlay.DrawingMode.POLYLINE);
        } else {
            sketchGraphicsOverlay.setDrawingMode(SketchGraphicsOverlay.DrawingMode.NONE);
        }
    }

    /**
     * When the polygon button is clicked, show it as selected and enable polygon drawing mode.
     *
     * @param v the button view
     */
    public void polygonClick(View v) {
        if (!v.isSelected()) {
            v.setSelected(true);
            sketchGraphicsOverlay.setDrawingMode(SketchGraphicsOverlay.DrawingMode.POLYGON);
        } else {
            sketchGraphicsOverlay.setDrawingMode(SketchGraphicsOverlay.DrawingMode.NONE);
        }
    }

    /**
     * When the undo button is clicked, undo the last event on the SketchGraphicsOverlay.
     *
     * @param v the button view
     */
    public void undoClick(View v) {
        sketchGraphicsOverlay.undo();
    }

    /**
     * When the redo button is clicked, redo the last undone event on the SketchGraphicsOverlay.
     *
     * @param v the button view
     */
    public void redoClick(View v) {
        sketchGraphicsOverlay.redo();
    }

    /**
     * When the clear button is clicked, clear all graphics on the SketchGraphicsOverlay.
     *
     * @param v the button view
     */
    public void clearClick(View v) {
        sketchGraphicsOverlay.clear();
    }


    public void finishClick(View v){
        if(btLayer1.isSelected()){
            Point point = WKTUtils.change3857To4326(markerPoint);
            Logger.i("完成" + point);
            CitySiteModel citySiteModel = new CitySiteModel();
            citySiteModel.setLongitude(""+point.getX());
            citySiteModel.setLatitude(""+point.getY());
            DBManager.getInstance(getApplicationContext()).getWritDao()
                    .getCitySiteModelDao()
                    .rxPlain()
                    .insert(citySiteModel)
                    .subscribe(new Action1<CitySiteModel>() {
                        @Override
                        public void call(CitySiteModel citySiteModel) {
                            Logger.i("完成" + citySiteModel.getId());
                            //TODO 将marker转移到城址图层
                            addCitySiteToLayer(citySiteModel);
                            tempLayer.removeAll();
                            Intent intent = new Intent(getApplicationContext(), CitySiteActivity.class);
                            intent.putExtra(CITY_SITE_ID,citySiteModel.getId());
                            startActivity(intent);
                        }
                    });
            return;
        }
        showDialog("您计算的结果为xxxx", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_location:
                // mapview.centerAt(40.08895,116.33662,  true);//116.33662  40.08895
                locationing();
                break;
            case R.id.bt_layer1:
                btLayer1.setSelected(!btLayer1.isSelected());
                mapview.setOnSingleTapListener(new OnSingleTapListener() {
                    @Override
                    public void onSingleTap(float v, float v1) {
                        onAddMarkerClick(v,v1);
                    }
                });
                break;

        }
    }
}
