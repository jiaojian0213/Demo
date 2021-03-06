package com.example.jiao.demo.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.example.jiao.demo.Constants;
import com.example.jiao.demo.R;
import com.example.jiao.demo.daomodel.CitySiteModel;
import com.example.jiao.demo.daomodel.TrajectoryModel;
import com.example.jiao.demo.layer.PeaceGraphicsLayer;
import com.example.jiao.demo.layer.PointCollection;
import com.example.jiao.demo.layer.SketchGraphicsOverlay;
import com.example.jiao.demo.layer.SketchGraphicsOverlayEventListener;
import com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer;
import com.example.jiao.demo.manager.DBManager;
import com.example.jiao.demo.utils.MarkerUtils;
import com.example.jiao.demo.utils.PermissionUtils;
import com.example.jiao.demo.utils.WKTUtils;
import com.example.jiao.demo.view.MyToolbar;
import com.orhanobut.logger.Logger;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.functions.Action1;

import static com.example.jiao.demo.Constants.graphic;
import static com.example.jiao.demo.Constants.picSymbol;
import static com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer.TianDiTuTiledMapServiceType.CVA_W;
import static com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer.TianDiTuTiledMapServiceType.IMG_W;
import static com.example.jiao.demo.layer.TianDiTuTiledMapServiceLayer.TianDiTuTiledMapServiceType.VEC_W;

@RuntimePermissions
public class MainActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, SketchGraphicsOverlayEventListener, View.OnClickListener, SensorEventListener {

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
    @Bind(R.id.toolbar)
    MyToolbar toolbar;
    @Bind(R.id.bt_measure)
    ImageView btMeasure;
    @Bind(R.id.finishButton)
    ImageButton finishButton;
    @Bind(R.id.buttom_layout)
    RelativeLayout buttomLayout;
    @Bind(R.id.bt_addMarker)
    ImageView btAddMarker;
    @Bind(R.id.bt_trackroute)
    ImageView btTrackroute;
    private TianDiTuTiledMapServiceLayer vecLayer;
    private TianDiTuTiledMapServiceLayer imgLayer;
    private TianDiTuTiledMapServiceLayer cvaLayer;
    private PeaceGraphicsLayer citySiteLayer;
    private PeaceGraphicsLayer citySiteNameLayer;
    private PeaceGraphicsLayer trajectoryLayer;
    private PeaceGraphicsLayer tempLayer;
    private PeaceGraphicsLayer locationLayer;
    private SketchGraphicsOverlay sketchGraphicsOverlay;
    private Point markerPoint;

    private static int CITY_SITE_DETAL_REQUESTCODE = 1024;

    private OnSingleTapListener onSingleTapListener = new OnSingleTapListener() {
        @Override
        public void onSingleTap(float v, float v1) {
            if (btAddMarker.isSelected())
                onAddMarkerClick(v, v1);
            onMarkerClick(v, v1);
        }
    };
    private PictureMarkerSymbol markerSymbol;
    private SensorManager manager;
    private PointCollection trackroutePoints;
    private PeaceGraphicsLayer tempTrackrouteLayer;
    private AlertDialog trackrouteDialog;
    private SimpleLineSymbol lineSym;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ArcGISRuntime.setClientId("9yNxBahuPiGPbsdi");//去水印
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Drawable locationDrawable = getResources().getDrawable(R.drawable.location2);
        picSymbol = new PictureMarkerSymbol(locationDrawable);
        Drawable markerDrawable = getResources().getDrawable(R.drawable.marker_icon);
        markerSymbol = new PictureMarkerSymbol(markerDrawable);

        lineSym = new SimpleLineSymbol(Color.RED, 3);

        toolbar.setTitle("Demo");
        toolbar.setNavigationIcon(null);
        setSupportActionBar(toolbar);

        btLayer.setSelected(false);
        mapLayer.setVisibility(View.GONE);
        btAddMarker.setSelected(false);
        btMeasure.setSelected(false);
        buttomLayout.setVisibility(View.GONE);
        btTrackroute.setSelected(false);

        vecLayer = new TianDiTuTiledMapServiceLayer(VEC_W);
        cvaLayer = new TianDiTuTiledMapServiceLayer(CVA_W);
        imgLayer = new TianDiTuTiledMapServiceLayer(IMG_W);

        citySiteLayer = new PeaceGraphicsLayer();
        citySiteNameLayer = new PeaceGraphicsLayer();
        trajectoryLayer = new PeaceGraphicsLayer();
        locationLayer = new PeaceGraphicsLayer();
        tempLayer = new PeaceGraphicsLayer();
        tempTrackrouteLayer = new PeaceGraphicsLayer();
        mapview.addLayer(vecLayer);
        mapview.addLayer(imgLayer);
        mapview.addLayer(cvaLayer);
        mapview.addLayer(citySiteLayer);
        mapview.addLayer(citySiteNameLayer);
        mapview.addLayer(trajectoryLayer);
        mapview.addLayer(locationLayer);
        mapview.addLayer(tempLayer);
        mapview.addLayer(tempTrackrouteLayer);
        mapview.setOnSingleTapListener(onSingleTapListener);
        sketchGraphicsOverlay = new SketchGraphicsOverlay(mapview, this);

        rbVecMap.setChecked(true);
        rbImgMap.setChecked(false);
        imgLayer.setVisible(false);
        imgLayer.setVisible(false);
        trajectoryLayer.setVisible(true);

        rbVecMap.setOnCheckedChangeListener(this);
        rbImgMap.setOnCheckedChangeListener(this);
        cbCitySite.setOnCheckedChangeListener(this);
        cbTrajectory.setOnCheckedChangeListener(this);

        btLayer.setOnClickListener(this);
        btAddMarker.setOnClickListener(this);
        btMeasure.setOnClickListener(this);
        btLocation.setOnClickListener(this);
        btTrackroute.setOnClickListener(this);

        MainActivityPermissionsDispatcher.locationStartWithCheck(this);

        //初始化添加定位
        mapview.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if(status == STATUS.LAYER_LOADED){
                    mapview.centerAt(33.368273, 106.272517,false);
                    mapview.setScale(130000000);
                }
            }
        });
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initGPS();
//        mMapView.setVisibility(View.VISIBLE);

        // initPermission();//针对6.0以上版本做权限适配
        Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //应用在前台时候注册监听器
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
//        mMapView.pause();
//        mMapView.setVisibility(View.GONE);
        //应用不在前台时候销毁掉监听器
        manager.unregisterListener(this);
        super.onPause();
    }

    public void initData() {
        DBManager.getInstance(getApplicationContext()).getWritDao()
                .getCitySiteModelDao()
                .rxPlain().loadAll()
                .subscribe(new Action1<List<CitySiteModel>>() {
                    @Override
                    public void call(List<CitySiteModel> citySiteModels) {
                        addCitySitesToLayer(citySiteModels);
                    }
                });

        DBManager.getInstance(getApplicationContext()).getWritDao()
                .getTrajectoryModelDao()
                .rxPlain().loadAll()
                .subscribe(new Action1<List<TrajectoryModel>>() {
                    @Override
                    public void call(List<TrajectoryModel> trajectoryModels) {
                        addTrajectoryModelToLayer(trajectoryModels);
                    }
                });
    }

    public void addCitySitesToLayer(List<CitySiteModel> citySiteModels) {
        for (int i = 0; i < citySiteModels.size(); i++) {
            addCitySiteToLayer(citySiteModels.get(i));
        }
    }

    public void addTrajectoryModelToLayer(List<TrajectoryModel> trajectoryModels) {
        for (int i = 0; i < trajectoryModels.size(); i++) {
            addTrajectoryModelToLayer(trajectoryModels.get(i));
        }
    }

    public void addCitySiteToLayer(CitySiteModel citySiteModel) {
        Point point = new Point(Double.valueOf(citySiteModel.getLongitude()), Double.valueOf(citySiteModel.getLatitude()));
        point = WKTUtils.change4326To3857(point);
        Map<String, Object> map = new HashMap<>();
        map.put(CITY_SITE_ID, citySiteModel.getId());
        Graphic graphic = new Graphic(point, markerSymbol, map);
        citySiteLayer.addGraphic(graphic);
        addCitySiteNameToLayer(citySiteModel,point);
    }

    public void addCitySiteNameToLayer(CitySiteModel citySiteModel, Point point){
        if(!TextUtils.isEmpty(citySiteModel.name)){
            PictureMarkerSymbol textPicMarkerSymbol = MarkerUtils.createTextPicMarkerSymbol(citySiteModel.name,
                    24, Color.RED, Color.TRANSPARENT, Color.TRANSPARENT);
            Point point1 = new Point(point.getX(), point.getY());
            Map<String, Object> textMap = new HashMap<>();
            textMap.put(CITY_SITE_ID, citySiteModel.getId());
            Graphic textGraphic = new Graphic(point1, textPicMarkerSymbol, textMap);
            citySiteNameLayer.addGraphic(textGraphic);
        }
    }

    public void addCitySiteNameToLayer(CitySiteModel citySiteModel){
        Point point = new Point(Double.valueOf(citySiteModel.getLongitude()), Double.valueOf(citySiteModel.getLatitude()));
        point = WKTUtils.change4326To3857(point);
        addCitySiteNameToLayer(citySiteModel,point);
    }

    public void addTrajectoryModelToLayer(TrajectoryModel trajectoryModel){
        try {
            String points = trajectoryModel.getPoints();
            JsonParser jsonParser = new JsonFactory().createJsonParser(points);
            MapGeometry mapGeometry = GeometryEngine.jsonToGeometry(jsonParser);
            Geometry geometry = mapGeometry.getGeometry();
            Graphic graphic = new Graphic(geometry, lineSym);
            trajectoryLayer.addGraphic(graphic);

            Polyline polyline = (Polyline) geometry;
            Point point = polyline.getPoint(0);
            PictureMarkerSymbol textPicMarkerSymbol = MarkerUtils.createTextPicMarkerSymbol(trajectoryModel.name,
                    24, Color.parseColor("#FF1493"), Color.TRANSPARENT, Color.TRANSPARENT);
            Graphic nameGraphic = new Graphic(point, textPicMarkerSymbol);
            trajectoryLayer.addGraphic(nameGraphic);
        }catch (Exception e){e.printStackTrace();}

    }

    private void onMarkerClick(float v, float v1) {
        int[] graphicIDs = citySiteLayer.getGraphicIDs(v, v1, 12);
        if (graphicIDs == null || graphicIDs.length == 0)
            return;
        for (int i = 0; i < graphicIDs.length; i++) {
            Graphic graphic = citySiteLayer.getGraphic(graphicIDs[i]);
            Object attrValue = graphic.getAttributeValue(CITY_SITE_ID);
            if (attrValue != null) {
                Long citySiteId = (Long) attrValue;
                Logger.i("citySiteId = " + citySiteId);
                Intent intent = new Intent(getApplicationContext(), CitySiteActivity.class);
                intent.putExtra(CITY_SITE_ID, citySiteId);
                startActivityForResult(intent,CITY_SITE_DETAL_REQUESTCODE);
                break;
            }
        }
    }

    public void onAddMarkerClick(float x, float y) {
        int[] graphicIds = tempLayer.getGraphicIDs();
        if (graphicIds != null && graphicIds.length > 0) {
            markerPoint = mapview.toMapPoint(x, y);
            Graphic graphic = new Graphic(markerPoint, markerSymbol);
            tempLayer.updateGraphic(graphicIds[0], graphic);
        } else {
            markerPoint = mapview.toMapPoint(x, y);
            Graphic graphic = new Graphic(markerPoint, markerSymbol);
            tempLayer.addGraphic(graphic);
        }
    }

    public void locationing() {
        if (location == null)
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
                citySiteNameLayer.setVisible(b);
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


    public void finishClick(View v) {
        if (btAddMarker.isSelected()) {
            if(markerPoint == null){
                Toast.makeText(getApplicationContext(),"请点击要添加的位置！",Toast.LENGTH_SHORT).show();
                return;
            }
            Point point = WKTUtils.change3857To4326(markerPoint);
            Logger.i("完成" + point);
            CitySiteModel citySiteModel = new CitySiteModel();
            citySiteModel.setLongitude("" + point.getX());
            citySiteModel.setLatitude("" + point.getY());
            DBManager.getInstance(getApplicationContext()).getWritDao()
                    .getCitySiteModelDao()
                    .rxPlain()
                    .insert(citySiteModel)
                    .subscribe(new Action1<CitySiteModel>() {
                        @Override
                        public void call(CitySiteModel citySiteModel) {
                            Logger.i("citySiteModel id = " + citySiteModel.getId());
                            addCitySiteToLayer(citySiteModel);
                            buttomLayout.setVisibility(View.GONE);
                            btAddMarker.setSelected(false);
                            tempLayer.removeAll();
                            markerPoint = null;
                            Intent intent = new Intent(getApplicationContext(), CitySiteActivity.class);
                            intent.putExtra(CITY_SITE_ID, citySiteModel.getId());
                            startActivityForResult(intent,CITY_SITE_DETAL_REQUESTCODE);
                        }
                    });
            return;
        }
        SketchGraphicsOverlay.DrawingMode drawingMode = sketchGraphicsOverlay.getDrawingMode();
        String result = "";
        if (drawingMode == SketchGraphicsOverlay.DrawingMode.POLYGON) {
            result = sketchGraphicsOverlay.getPolygonArea() + "平方米";
        } else if (drawingMode == SketchGraphicsOverlay.DrawingMode.POLYLINE) {
            result = sketchGraphicsOverlay.getPolygonDistance() + "米";
        }
        showDialog("您计算的结果为" + result, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_location:
                // mapview.centerAt(40.08895,116.33662,  true);//116.33662  40.08895
                locationing();
                break;
            case R.id.bt_layer:
                btLayer.setSelected(!btLayer.isSelected());
                if (btLayer.isSelected()) {
                    mapLayer.setVisibility(View.VISIBLE);
                } else {
                    mapLayer.setVisibility(View.GONE);
                }
                break;
            case R.id.bt_measure:
                if (btAddMarker.isSelected()) {
                    btAddMarker.setSelected(false);
                    buttomLayout.setVisibility(View.GONE);
                    tempLayer.removeAll();
                }
                btMeasure.setSelected(!btMeasure.isSelected());
                if (btMeasure.isSelected()) {
                    buttomLayout.setVisibility(View.VISIBLE);
                    for (int i = 0; i < buttomLayout.getChildCount(); i++) {
                        View childView = buttomLayout.getChildAt(i);
                        if (childView.getId() == R.id.pointButton)
                            continue;
                        childView.setVisibility(View.VISIBLE);
                    }
                } else {
                    buttomLayout.setVisibility(View.GONE);
                    sketchGraphicsOverlay.clear();
                    mapview.setOnTouchListener(new MapOnTouchListener(getApplicationContext(),mapview));
                }
                break;
            case R.id.bt_addMarker:
                if (btMeasure.isSelected()) {
                    btMeasure.setSelected(false);
                    buttomLayout.setVisibility(View.GONE);
                    sketchGraphicsOverlay.clear();
                    mapview.setOnTouchListener(new MapOnTouchListener(getApplicationContext(),mapview));
                }
                markerPoint = null;
                mapview.setOnSingleTapListener(onSingleTapListener);
                btAddMarker.setSelected(!btAddMarker.isSelected());
                if (btAddMarker.isSelected()) {
                    buttomLayout.setVisibility(View.VISIBLE);
                    for (int i = 0; i < buttomLayout.getChildCount(); i++) {
                        View childView = buttomLayout.getChildAt(i);
                        if (childView.getId() == R.id.finishButton) {
                            childView.setVisibility(View.VISIBLE);
                        } else {
                            childView.setVisibility(View.GONE);
                        }
                    }
                } else {
                    buttomLayout.setVisibility(View.GONE);
                }
                if (!btAddMarker.isSelected()) {
                    tempLayer.removeAll();
                }
                break;

            case R.id.bt_trackroute:
                btTrackroute.setSelected(!btTrackroute.isSelected());
                if(btTrackroute.isSelected()) {
                    if (trackroutePoints == null) {
                        trackroutePoints = new PointCollection(SpatialReference.create(3857));
                    } else {
                        trackroutePoints.clear();
                    }
                    trackroutePoints.add(Constants.mapPoint);
                }else{
                    if(trackroutePoints.size() < 2){
                        Toast.makeText(getApplicationContext(),"您还没有产生轨迹！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String trackrouteDistance = "0";
                    if(trackroutePoints != null)
                        trackrouteDistance = getTrackrouteDistance();
                    final String distance = trackrouteDistance;
                    trackrouteDialog = createDialog("是否保存当前轨迹记录？",trackrouteDistance, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText etName = (EditText) trackrouteDialog.findViewById(R.id.et_name);
                                String name = etName.getText().toString().trim();
                                if(TextUtils.isEmpty(name)){
                                    Toast.makeText(getApplicationContext(),"请填写轨迹名称！",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                saveTrackrouteInfo(name,distance);
                                if(trackroutePoints != null)
                                    trackroutePoints.clear();
                                dialogInterface.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(trackroutePoints != null)
                                    trackroutePoints.clear();
                                tempTrackrouteLayer.removeAll();
                            }
                        });
                    }
                break;
        }
    }

    public String getTrackrouteDistance(){
        try {
            double length = 0;
            int pathCount = trackroutePoints.size();
            for (int i = 0; i < pathCount - 1; i++) {
                Point pointStart = trackroutePoints.get(i);
                Point pointEnd = trackroutePoints.get(i + 1);
                length += GeometryEngine.geodesicDistance(pointStart, pointEnd, SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR), null);
            }
            String lengthResultStr = "" + WKTUtils.formatArea(length);
            return lengthResultStr;
        }catch (Exception e){
            return "0";
        }
    }

    public AlertDialog createDialog(String content,String distance, final DialogInterface.OnClickListener oklistener, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder
                .setTitle("提醒")
                .setMessage(content)
                .setCancelable(false)
                .setView(R.layout.edit_dialog)
                .setNegativeButton("取消", listener)
                .setPositiveButton("确定", null).show();
        if(!TextUtils.isEmpty(distance)) {
            TextView tvDis = (TextView) alertDialog.findViewById(R.id.tv_dis);
            tvDis.setText(distance+"米");
        }
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(oklistener != null){
                        oklistener.onClick(alertDialog,0);
                    }
                }
            });
        return alertDialog;
    }

    public void saveTrackrouteInfo(String name, String distance){
        Logger.i("save inof");
        int[] graphicIDs = tempTrackrouteLayer.getGraphicIDs();
        if(graphicIDs != null) {
            Graphic[] graphics = new Graphic[graphicIDs.length];
            Graphic[] graphicNames = new Graphic[graphicIDs.length];
            ArrayList<TrajectoryModel> trajectoryModels = new ArrayList<>();
            for (int i = 0; i < graphicIDs.length; i++) {
                graphics[i] = tempTrackrouteLayer.getGraphic(graphicIDs[i]);
                String points = GeometryEngine.geometryToJson(mapview.getSpatialReference(), graphics[i].getGeometry());
                Log.i("info","points = "+points);
                TrajectoryModel trajectoryModel = new TrajectoryModel();
                trajectoryModel.setName(name);
                trajectoryModel.setPoints(points);
                String wgs84Point = getWgs84Point(points);
                Log.i("info","wgs84Point = "+wgs84Point);
                trajectoryModel.setGeojsonPoints(wgs84Point);
                trajectoryModel.setDistance(distance);
                trajectoryModels.add(trajectoryModel);

                Polyline polyline = (Polyline) graphics[i].getGeometry();
                Point point = polyline.getPoint(0);
                PictureMarkerSymbol textPicMarkerSymbol = MarkerUtils.createTextPicMarkerSymbol(name,
                        24, Color.parseColor("#FF1493"), Color.TRANSPARENT, Color.TRANSPARENT);
                Graphic graphic = new Graphic(point, textPicMarkerSymbol);
                graphicNames[i] =  graphic;
            }
            trajectoryLayer.addGraphics(graphics);
            trajectoryLayer.addGraphics(graphicNames);
            //TODO 绘制名字
            tempTrackrouteLayer.removeAll();
            DBManager.getInstance(getApplicationContext()).getWritDao()
                    .getTrajectoryModelDao().rxPlain()
                    .insertInTx(trajectoryModels)
                    .subscribe(new Action1<Iterable<TrajectoryModel>>() {
                        @Override
                        public void call(Iterable<TrajectoryModel> trajectoryModels) {
                            Logger.i("保存成功");
                        }
                    });
        }


//        trackroutePoints
    }

    public String getWgs84Point(String points){
        try {
            String aaa = "{\"type\": \"FeatureCollection\",\"features\": [{\"type\": \"Feature\",\"geometry\": {\"type\": \"LineString\",\"coordinates\": [$]},\"properties\": {\"prop0\": \"value0\",\"prop1\": 0.0}}]}";
            String subPoints = points.substring(points.indexOf("[[[") + 3, points.indexOf("]]]"));
            String[] split = subPoints.split("\\],\\[");
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < split.length; i++) {
                try {
                    String[] pointStr = split[i].split(",");
                    Point point3857 = new Point(Double.valueOf(pointStr[0]), Double.valueOf(pointStr[1]));
                    Point point4326 = WKTUtils.change3857To4326(point3857);
                    sb.append(point4326.getX()).append(",").append(point4326.getY()).append("],[");
                }catch (Exception e){}
            }
            String result = aaa.replace("$", sb.substring(0, sb.length() - 2));
            return result;
        }catch (Exception e){
            return points;
        }
    }

    @Override
    public void locationChange(Location location) {
        super.locationChange(location);
        if(btTrackroute.isSelected()){
            trackroutePoints.add(Constants.mapPoint);
            //TODO 绘制路线到临时图层 tempTrackrouteLayer
            Polyline lastPolyline = trackroutePoints.getLastPolyline();
            Graphic graphic = new Graphic(lastPolyline, lineSym);
            int[] graphicIDs = tempTrackrouteLayer.getGraphicIDs();
            if(graphicIDs == null || graphicIDs.length == 0) {
                tempTrackrouteLayer.addGraphic(graphic);
            }else{
                tempTrackrouteLayer.updateGraphic(graphicIDs[0],graphic);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            float degree = event.values[0];// 存放了方向值
            picSymbol.setAngle(degree);
            Constants.graphic = new Graphic(Constants.mapPoint, picSymbol);
            int[] ids = locationLayer.getGraphicIDs();
            if (ids == null) {
                return;
            }
            locationLayer.updateGraphic(ids[0], graphic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void locationStart() {
        startLocation();
    }

    @OnShowRationale({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE})
    void showRationaleForLocation(PermissionRequest request) {
        PermissionUtils.showRationaleDialog(this, "为保证app功能正常运行，请授权允许相关权限", request);
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE})
    void onLocationDenied() {
        Toast.makeText(this, "没有获取到相关权限", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE})
    void showNeverAskForLocation() {
        if (PermissionUtils.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {            //点击始终允许时也会调用
            startLocation();
        } else {
            startLocation();
//            PermissionUtils.showNeverAskDialog(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onBackPressedSupport() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("提醒")
                .setMessage("是否退出程序")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }

                }).setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create(); // 创建对话框
        alertDialog.show(); // 显示对话框
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CITY_SITE_DETAL_REQUESTCODE && resultCode == CitySiteActivity.REQULTCODE && data != null){
            long citySiteId = data.getLongExtra(CITY_SITE_ID, -1);
            DBManager.getInstance(getApplicationContext()).getWritDao()
                    .getCitySiteModelDao().rxPlain().load(citySiteId)
                    .subscribe(new Action1<CitySiteModel>() {
                        @Override
                        public void call(CitySiteModel citySiteModel) {
                            updateCitySiteName(citySiteModel);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
        }
    }

    public void updateCitySiteName(CitySiteModel citySiteModel){
        int[] graphicIDs = citySiteNameLayer.getGraphicIDs();
        for(int i = 0 ;graphicIDs != null && i< graphicIDs.length;i++){
            Graphic graphic = citySiteNameLayer.getGraphic(graphicIDs[i]);
            Object attributeValue = graphic.getAttributeValue(CITY_SITE_ID);
            if(citySiteModel.getId().equals(attributeValue)){
                PictureMarkerSymbol textPicMarkerSymbol = MarkerUtils.createTextPicMarkerSymbol(citySiteModel.name,
                        24, Color.RED, Color.TRANSPARENT, Color.TRANSPARENT);
                Map<String, Object> textMap = new HashMap<>();
                textMap.put(CITY_SITE_ID, citySiteModel.getId());
                citySiteNameLayer.updateGraphic(graphic,textPicMarkerSymbol);
                return;
            }
        }
        addCitySiteNameToLayer(citySiteModel);
    }
}
