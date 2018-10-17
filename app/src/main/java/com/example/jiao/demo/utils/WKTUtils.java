package com.example.jiao.demo.utils;

import android.text.TextUtils;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.example.jiao.demo.Constants;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiaojian on 2018/10/16.
 */

public class WKTUtils {

    public static String changeString(String str){
//        String start = "MULTIPOLYGON(((";
//        String end = ")))";
//        str = str.replace(","," ");
//        str = str.replace(";",",");
//        str = str.replace("#","),(");
//        return start+str +end;
        String start = " {\"rings\":[[[";
        String end = "]]],\"spatialReference\":{\"wkid\":1}}";
        str = str.replace(")","");
        str = str.replace("(","");
        str = str.replace(";","],[");
        str = str.replace("#","]],[[");
        return start + str + end;
    }

    public static String changeString1(String str){
//        String start = "MULTIPOLYGON(((";
//        String end = ")))";
//        str = str.replace(","," ");
//        str = str.replace(";",",");
//        str = str.replace("#","),(");
//        return start+str +end;
        String start = "{\"rings\":[[[";
        String end = "]]],\"spatialReference\":{\"wkid\":1}}";
        str = str.replace("MULTIPOLYGON(((",start);
        str = str.replace(")","");
        str = str.replace("(","");
        str = str.replace(",","],[");
        str = str.replace(" ",",");
        str = str.replace("#","]],[[");
        return str + end;
    }

    public static Geometry getGeometry(String geomstring){
//        MULTIPOLYGON(((12959354.3568183 4852661.48549783,12959373.2523276 4852540.0831168,12959509.2151926 4852550.0530169,12959487.9119241 4852676.23391442,12959354.3568183 4852661.48549783)))
        String changeString = "";
        if(geomstring.contains("MULTIPOLYGON")){
            changeString = changeString1(geomstring);
        }else {
            changeString = changeString(geomstring);
        }
//        Log.e("info", "changeString " + changeString);
//        String multipolygonWktToJson = WKT.getMULTIPOLYGONWktToJson(changeString, strings, 1);
//        Log.e("info", "multipolygonWktToJson " + multipolygonWktToJson);
        Geometry geometry = null;
        try {
            JsonParser jsonParser = new JsonFactory().createJsonParser(changeString);
            MapGeometry mapGeometry = GeometryEngine.jsonToGeometry(jsonParser);
            geometry = mapGeometry.getGeometry();
//            Log.e("info", "string " + geometry.getClass().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return geometry;
    }

    public static String getGeomStringFromGeometry(Geometry geometry){
        String geometryToJson = null;
        try {
            geometryToJson = GeometryEngine.geometryToJson(SpatialReference.create(3857), geometry);
//            Logger.i("geometryToJson = "+geometryToJson);
            geometryToJson = geometryToJson.substring(geometryToJson.indexOf("rings")+10,geometryToJson.indexOf("spatialReference")-5);
            geometryToJson = geometryToJson.replace("]],[[","#");
            geometryToJson = geometryToJson.replace("],[",";");
            geometryToJson = geometryToJson.replace(",0.0,null;",";");
//            Logger.i("geometryToJson = "+geometryToJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return geometryToJson;
    }

    public static List<Point> getPoints(String pointStr){
        ArrayList<Point> pointArrayList = new ArrayList<>();
        pointStr = pointStr.replace("#", ";");
        String pointlist[] = pointStr.split(";");
        for (int i = 0; i < pointlist.length; i++) {
            try {
                String points[] = pointlist[i].split(",");
                String lng = points[0];
                String lat = points[1];
                Point point = new Point(Double.parseDouble(lng), Double.parseDouble(lat));
                pointArrayList.add(point);
            }catch (Exception e){}
        }
        return pointArrayList;
    }

    public static Point getCenterPoint(String pointStr){
        List<Point> points = getPoints(pointStr);
        return getCenterPointWithPints(points);
    }

    //返回画完地块的中心点坐标
    public static Point getCenterPointWithPints(List<Point> pointArray)
    {
        double xmax = -1000000000, xmin = 1000000000, ymax = -100000000, ymin = 1000000000;
        for (int i = 0;i<pointArray.size();i++){
            Point p = pointArray.get(i);
            if (p.getX() > xmax) xmax = p.getX();
            if (p.getX() < xmin) xmin = p.getX();
            if (p.getY() > ymax) ymax = p.getY();
            if (p.getY() < ymin) ymin = p.getY();
        }

        double xcenter = (xmax + xmin) / 2;
        double ycenter = (ymax + ymin) / 2;

        return new Point(xcenter, ycenter);
    }

    /**
     * 判断坐标点是否落在指定的多边形区域内
     * @param point  指定的坐标点
     * @param list   多变形区域的节点集合
     * @return   True 落在范围内 False 不在范围内
     */
    public static boolean isWithIn(Point point, List<Point> list) {
        double x = point.getX();
        double y = point.getY();

        int isum, icount, index;
        double dLon1 = 0, dLon2 = 0, dLat1 = 0, dLat2 = 0, dLon;

        if (list.size() < 3) {
            return false;
        }

        isum = 0;
        icount = list.size();

        for (index = 0; index < icount - 1; index++) {
            if (index == icount - 1) {
                dLon1 = list.get(index).getX();
                dLat1 = list.get(index).getY();
                dLon2 = list.get(0).getX();
                dLat2 = list.get(0).getY();
            } else {
                dLon1 = list.get(index).getX();
                dLat1 = list.get(index).getY();
                dLon2 = list.get(index + 1).getX();
                dLat2 = list.get(index + 1).getY();
            }

            // 判断指定点的 纬度是否在 相邻两个点(不为同一点)的纬度之间
            if (((y >= dLat1) && (y < dLat2)) || ((y >= dLat2) && (y < dLat1))) {
                if (Math.abs(dLat1 - dLat2) > 0) {
                    dLon = dLon1 - ((dLon1 - dLon2) * (dLat1 - y)) / (dLat1 - dLat2);
                    if (dLon < x){
                        isum++;
                    }
                }
            }
        }

        if ((isum % 2) != 0) {
            return true;
        } else {
            return false;
        }
    }

    public static double calculateArea2D(String points) {
        Geometry geometry = getGeometry(points);
        return calculateArea2D(geometry);
    }

    public static double calculateArea2D(Geometry geometry) {
        double geodesicArea = GeometryEngine.geodesicArea(geometry, SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR), null);
        //(1000 * 2f / 3f) = 666.666...
        double area = Math.abs(geodesicArea) / (1000 * 2f / 3f);// * 0.5761155954260821
        return area;
    }

    //        MULTIPOLYGON(((12959354.3568183 4852661.48549783,12959373.2523276 4852540.0831168,12959509.2151926 4852550.0530169,12959487.9119241 4852676.23391442,12959354.3568183 4852661.48549783)))
    public static String changeMultiPolygonToGeomString(String multiPolygonStr){
        String result = "";
        if(TextUtils.isEmpty(multiPolygonStr)) {
            return result;
        }
        result = multiPolygonStr;
        if(result.contains("MULTIPOLYGON")){
            result = result.replace("),(", "#");
            result = result.replace("MULTIPOLYGON(((","");
            result = result.replace(")))","");
            result = result.replace("(((","");
            result = result.replace(",",";");
            result = result.replace(" ",",");
        }
        return result;
    }

//    public static Observable<List<String>> union(final List<UnionModel.PolygonsEntity> polygons){
//        return Observable.create(new Observable.OnSubscribe<List<String>>() {
//            @Override
//            public void call(Subscriber<? super List<String>> subscriber) {
//                Geometry[] geometries = new Geometry[polygons.size()];
//                for(int i=0;i< polygons.size();i++){
//                    geometries[i] = getGeometry(polygons.get(i).geom);
//                }
//                for(int i =0;i< geometries.length;i++){
//                    if(geometries[i] == null)
//                        continue;
//                    for(int j = i+1;j< geometries.length;j++) {
//                        if(geometries[j] == null)
//                            continue;
//
//                        boolean intersects = GeometryEngine.intersects(geometries[i], geometries[j], SpatialReference.create(3857));
//                        Geometry intersect = GeometryEngine.intersect(geometries[i], geometries[j], SpatialReference.create(3857));
//                        if( intersect.getType() == Geometry.Type.POINT)
//                            continue;
//                        String geometryToJson = GeometryEngine.geometryToJson(SpatialReference.create(3857), intersect);
//                        Logger.i("geometryToJson aaaaa = "+intersect.isEmpty()+" points = "+geometryToJson );
//                        Geometry union = GeometryEngine.union(new Geometry[]{geometries[i],geometries[j]},SpatialReference.create(3857));
//                        if(union != null){
//                            geometries[i] = union;
//                            geometries[j] = null;
//                        }
//                    }
//                }
//                ArrayList<String> geomStrs = new ArrayList<>();
//                for(int i=0;i< geometries.length;i++){
//                    if(geometries[i] == null)
//                        continue;
//                    geomStrs.add(getGeomStringFromGeometry(geometries[i]));
//                }
//                subscriber.onNext(geomStrs);
//                subscriber.onCompleted();
//            }
//        });
//    }

    public static String formatArea(Double area){
        if(!TextUtils.isEmpty(Constants.area_precision)){
            String format = String.format("%." + Constants.area_precision + "f", area);
            return Double.valueOf(format) == 0?"0":Double.valueOf(format)+"";
        }
        return String.format("%.1f", area);
    }

    public static String formatArea(String area){
        return String.format("%.1f", Double.valueOf(area));
    }


    public static Point change3857To4326(Point inPoint){
        Point point = (Point) GeometryEngine.project(inPoint, SpatialReference.create(3857), SpatialReference.create(4326));
        return point;
    }

    public static Point change4326To3857(Point inPoint){
        Point point = (Point) GeometryEngine.project(inPoint, SpatialReference.create(4326), SpatialReference.create(3857));
        return point;
    }
}
