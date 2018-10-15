package com.example.jiao.demo.layer;


import com.esri.android.map.GraphicsLayer;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.Symbol;
import com.example.jiao.demo.utils.map.ArrayMap;
import com.orhanobut.logger.Logger;

/**
 * Created by jiaojian on 2018/1/10.
 */

public class PeaceGraphicsLayer extends GraphicsLayer {

    private final ArrayMap<Graphic, Integer> graphicIdMap;

    public PeaceGraphicsLayer(){
        graphicIdMap = new ArrayMap<>();
    }

    @Override
    public int addGraphic(Graphic graphic) {
        int graphicId = super.addGraphic(graphic);
        graphicIdMap.put(graphic,graphicId);
        return graphicId;
    }

    public boolean contains(Graphic graphic){
        int uid = graphic.getUid();
        if(uid != -1 && graphicIdMap.containsValue(uid)) {
            Logger.i("containsValue");
            return true;
        }
        return  graphicIdMap.containsKey(graphic);
    }

    @Override
    public void removeGraphic(int id) {
        super.removeGraphic(id);
        for(int i = 0;i < graphicIdMap.size();i++){
            if(graphicIdMap.valueAt(i) == id){
                graphicIdMap.remove(graphicIdMap.keyAt(i));
            }
        }
    }

    public void updateGraphic(Graphic graphic, Symbol symbol){
        int uid = graphic.getUid();
        if(uid != -1 && graphicIdMap.containsValue(uid)){
            super.updateGraphic(uid,symbol);
        }
    }
}
