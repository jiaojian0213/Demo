package com.example.jiao.demo.layer;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;

import java.util.ArrayList;

/**
 * Created by jiaojian on 2018/1/4.
 */

public class PointCollection extends ArrayList<Point>{
    public PointCollection(SpatialReference spatialReference) {

    }

    public Polyline getLastPolyline(){
        Polyline polyline = new Polyline();
        int size = size();
        for(int i=0;i< size;i++) {
            if(i == 0){
                polyline.startPath(get(0));
            }else{
                polyline.lineTo(get(i));
            }
        }
        return polyline;
    }

    public Geometry getLastPolygon() {
        Polygon polygon = new Polygon();
        int size = size();
        for(int i=0;i< size;i++) {
            if(i == 0){
                polygon.startPath(get(0));
            }else{
                polygon.lineTo(get(i));
            }
        }
        return polygon;
    }
}
