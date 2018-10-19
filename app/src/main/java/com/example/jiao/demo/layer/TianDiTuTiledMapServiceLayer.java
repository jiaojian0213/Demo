package com.example.jiao.demo.layer;

import android.util.Log;

import com.esri.android.map.TiledServiceLayer;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.io.UserCredentials;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.RejectedExecutionException;

/**
 * 天地图
 */
public class TianDiTuTiledMapServiceLayer extends TiledServiceLayer {
    private TianDiTuTiledMapServiceType _mapType;
    private TileInfo tiandituTileInfo;

    double[] res={
            156543.03392800014,
            78271.516963999937,
            39135.758482000092,
            19567.879240999919,
            9783.9396204999593,
            4891.9698102499797,
            2445.9849051249898,
            1222.9924525624949,
            611.49622628137968,
            305.74811314055756,
            152.87405657041106,
            76.43702828507324,
            38.21851414253662,
            19.10925707126831,
            9.554628535634155,
            4.77731426794937,
            2.388657133974685,
            1.1943285668550503,
            0.5971642835598172,
            0.29858214164761665
    };
    double[] scale={
            591657527.591555,
            295828763.79577702,
            147914381.89788899,
            73957190.948944002,
            36978595.474472001,
            18489297.737236001,
            9244648.8686180003,
            4622324.4343090001,
            2311162.2171550002,
            1155581.108577,
            577790.554289,
            288895.277144,
            144447.638572,
            72223.819286,
            36111.909643,
            18055.954822,
            9027.977411,
            4513.988705,
            2256.994353,
            1128.497176
    };

    public TianDiTuTiledMapServiceLayer() {
        this(null, null,true);
    }
    public TianDiTuTiledMapServiceLayer(TianDiTuTiledMapServiceType mapType){
        this(mapType, null,true);
    }
 
    public TianDiTuTiledMapServiceLayer(TianDiTuTiledMapServiceType mapType,UserCredentials usercredentials){
        this(mapType, usercredentials, true);
    }
    public TianDiTuTiledMapServiceLayer(TianDiTuTiledMapServiceType mapType, UserCredentials usercredentials, boolean flag){
        super("");
        this._mapType=mapType;
        setCredentials(usercredentials);
         
        if(flag)
            try
            {
                getServiceExecutor().submit(new Runnable() {
 
                    public final void run()
                    {
                        a.initLayer();
                    }
 
                    final TianDiTuTiledMapServiceLayer a;
 
             
            {
                a = TianDiTuTiledMapServiceLayer.this;
                //super();
            }
                });
                return;
            }
            catch(RejectedExecutionException _ex) { }
    }
    public TianDiTuTiledMapServiceType getMapType(){
        return this._mapType;
    }
     protected void initLayer(){
         this.buildTileInfo();
//         this.setFullExtent(new Envelope(-180,-90,180,90));
//         this.setDefaultSpatialReference(SpatialReference.create(4490));   //CGCS2000
//         this.setDefaultSpatialReference(SpatialReference.create(4326)); //GCS_WGS_1984
//         this.setInitialExtent(new Envelope(90.52,33.76,113.59,42.88));
//         this.setInitialExtent(new Envelope(118.91208162289873,28.43301753729623,120.89833669751555,29.7232802677077));
         // -20037508.342787001, 20037508.342787001
         this.setFullExtent(new Envelope(-20037508.342787001, -20037508.342787001,20037508.342787001, 20037508.342787001));
//         this.setFullExtent(new Envelope(8608652.792907715,4728956.08831787,9423041.921691895,5329032.288330078));
         this.setDefaultSpatialReference(SpatialReference.create(3857)); //GCS_WGS_1984
//         this.setInitialExtent(new Envelope(8608652.792907715, 4728956.08831787, 9423041.921691895, 5329032.288330078));
//         initializeMinMaxScale();
         initializeMinMaxScale(scale[3],scale[19]);
         super.initLayer();
     }
      public void refresh()
        {
            try
            {
                getServiceExecutor().submit(new Runnable() {
 
                    public final void run()
                    {
                        if(a.isInitialized())
                            try
                            {
                                a.b();
                                a.clearTiles();
                                return;
                            }
                            catch(Exception exception)
                            {
                                Log.e("ArcGIS", "Re-initialization of the layer failed.", exception);
                            }
                    }
 
                    final TianDiTuTiledMapServiceLayer a;
 
                 
                {
                    a = TianDiTuTiledMapServiceLayer.this;
                    //super();
                }
                });
                return;
            }
            catch(RejectedExecutionException _ex)
            {
                return;
            }
        }
        final void b()
                throws Exception
            {
              
            }
 
    @Override
    protected byte[] getTile(int level, int col, int row) throws Exception {
        /**
         * 
         * */
         
        byte[] result = null;
//        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
 
            URL sjwurl = new URL(this.getTianDiMapUrl(level, col, row));
            HttpURLConnection httpUrl = null;
            BufferedInputStream bis = null;
            byte[] buf = new byte[1024];
 
            httpUrl = (HttpURLConnection) sjwurl.openConnection();
            httpUrl.connect();
            //425字节
            if(httpUrl.getContentLength() == 425 ||
                httpUrl.getContentLength() == 103||
                httpUrl.getContentLength() == 213)
                return null;
            bis = new BufferedInputStream(httpUrl.getInputStream());
            while (true) {
                int bytes_read = bis.read(buf);
                if (bytes_read > 0) {
                    bos.write(buf, 0, bytes_read);
                } else {
                    break;
                }
            }
            ;
            bis.close();
            httpUrl.disconnect();
 
            result = bos.toByteArray();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
         
        return result;
    }
 
 
    @Override
    public TileInfo getTileInfo(){
        return this.tiandituTileInfo;
    }
    /**
     * 
     * */
    private String getTianDiMapUrl(int level, int col, int row){
         
         /**
         * 天地图矢量、影像
          * http://t5.tianditu.gov.cn/DataServer?T=vec_w&x=3413&y=1678&l=12
         * */
        StringBuilder url=new StringBuilder("http://t");
        Random random=new Random();
        int subdomain = (random.nextInt(6) + 1);
        url.append(subdomain);
        switch(this._mapType){
        case VEC_C://电子地图
             url.append(".tianditu.com/DataServer?T=vec_c&X=").append(col).append("&Y=").append(row).append("&L=").append(level);
            break;
        case CVA_C://地名
             url.append(".tianditu.com/DataServer?T=cva_c&X=").append(col).append("&Y=").append(row).append("&L=").append(level);
            break;
        case CIA_C://路网
             url.append(".tianditu.com/DataServer?T=cia_c&X=").append(col).append("&Y=").append(row).append("&L=").append(level);
            break;
        case IMG_C://影像
             url.append(".tianditu.com/DataServer?T=img_c&X=").append(col).append("&Y=").append(row).append("&L=").append(level);
            break;
        case VEC_W://电子地图
             url.append(".tianditu.com/DataServer?T=vec_w&X=").append(col).append("&Y=").append(row).append("&L=").append(level);
            break;
        case CVA_W://地名
             url.append(".tianditu.com/DataServer?T=cva_w&X=").append(col).append("&Y=").append(row).append("&L=").append(level);
            break;
        case CIA_W://路网
             url.append(".tianditu.com/DataServer?T=cia_w&X=").append(col).append("&Y=").append(row).append("&L=").append(level);
            break;
        case IMG_W://影像
             url.append(".tianditu.com/DataServer?T=img_w&X=").append(col).append("&Y=").append(row).append("&L=").append(level);
            break;
            default:
                return null;
        }
//        Log.i("info","url = "+url.toString());
        return url.toString();
//        String s = "http://t0.tianditu.com/cva_c/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=cva&STYLE=default&TILEMATRIXSET=c&TILEMATRIX="+level+"&TILEROW="+row+"&TILECOL="+col+"&FORMAT=tiles";
//        return s;
    }
     
    private void buildTileInfo()
    {//8608652.792907715,4728956.08831787,9423041.921691895,5329032.288330078
//        Point originalPoint=new Point(8608652.792907715,5329032.288330078);
        Point originalPoint=new Point( -20037508.342787001, 20037508.342787001);
//
//        double[] res={
//                1.40625,
//                0.703125,
//                0.3515625,
//                0.17578125,
//                0.087890625,
//                0.0439453125,
//                0.02197265625,
//                0.010986328125,
//                0.0054931640625,
//                0.00274658203125//,
//                0.001373291015625,
//                0.0006866455078125,
//                0.00034332275390625,
//                0.000171661376953125,
//                8.58306884765629E-05,
//                4.29153442382814E-05,
//                2.14576721191407E-05,
//                1.07288360595703E-05,
//                5.36441802978515E-06,
//                2.68220901489258E-06,
//                1.34110450744629E-06
//        };

        int levels=21;
        int dpi=96;
        int tileWidth=256;
        int tileHeight=256;
        this.tiandituTileInfo=new com.esri.android.map.TiledServiceLayer.TileInfo(originalPoint, scale, res, levels, dpi, tileWidth,tileHeight);
        this.setTileInfo(this.tiandituTileInfo);
    }
 
     
    public enum  TianDiTuTiledMapServiceType {
        /**
         * 天地图矢量
         * */
        VEC_C,
        /**
         * 天地图影像
         * */
        IMG_C,
        /**
         * 天地图矢量标注
         * */
        CVA_C,
        /**
         * 天地图影像标注
         * */
        CIA_C,
        /**
         * 天地图矢量
         * */
        VEC_W,
        /**
         * 天地图影像
         * */
        IMG_W,
        /**
         * 天地图矢量标注
         * */
        CVA_W,
        /**
         * 天地图影像标注
         * */
        CIA_W
    }
}