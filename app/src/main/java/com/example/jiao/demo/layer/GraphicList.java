package com.example.jiao.demo.layer;

import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;

/**
 * Created by jiaojian on 2018/1/5.
 */

public class GraphicList extends ArrayList<Graphic> {

    private final SketchGraphicsOverlay layer;
    private Observable observable;

    public GraphicList(SketchGraphicsOverlay layer){
        this.layer = layer;
        observable = new GraphicObservable();
        observable.addObserver(layer);
    }

    @Override
    public int indexOf(Object o) {
        Graphic graphic = (Graphic) o;
        if(graphic.getGeometry() instanceof Point) {
            Point point = (Point)graphic.getGeometry();
            for (int i = 0; i < size(); i++) {
                Graphic graphic1 = get(i);
                if(graphic1.getGeometry() instanceof Point) {
                    Point point1 = (Point)graphic1.getGeometry();
                    if (point.getX() == point1.getX() && point.getY() == point1.getY())
                        return i;
                }
            }
        }
        return super.indexOf(o);
    }

    @Override
    public boolean add(Graphic graphic) {
        boolean add = super.add(graphic);
        observable.notifyObservers(new GraphicEvent(EventType.ADD,graphic));
        return add;
    }

    @Override
    public void add(int index, Graphic element) {
        super.add(index, element);
        observable.notifyObservers(new GraphicEvent(EventType.ADD,element));
    }

    @Override
    public boolean addAll(Collection<? extends Graphic> c) {
        boolean addAll = super.addAll(c);
        observable.notifyObservers(new GraphicEvent(EventType.ADDALL,c));
        return addAll;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Graphic> c) {
        boolean addAll = super.addAll(index, c);
        observable.notifyObservers(new GraphicEvent(EventType.ADDALL,c));
        return addAll;
    }

    @Override
    public boolean remove(Object o) {
        boolean remove = super.remove(o);
        observable.notifyObservers(new GraphicEvent(EventType.REMOVE,(Graphic)o));
        return remove;
    }

    @Override
    public Graphic remove(int index) {
        Graphic graphic = super.remove(index);
        observable.notifyObservers(new GraphicEvent(EventType.REMOVE,graphic));
        return graphic;
    }

    public enum  EventType{
        ADD,
        ADDALL,
        REMOVE,
        REMOVEALl
    }
    public class GraphicEvent{
        public final EventType type;
        public Graphic graphic;
        public Collection<? extends Graphic> graphics;

        public GraphicEvent(EventType type,Graphic graphic){
            this.type = type;
            this.graphic = graphic;
        }
        public GraphicEvent(EventType type,Collection<? extends Graphic> graphics){
            this.type = type;
            this.graphics = graphics;
        }

    }

    class GraphicObservable extends Observable {
        @Override
        public void notifyObservers(Object arg) {
            setChanged();
            super.notifyObservers(arg);
        }
    }
}
