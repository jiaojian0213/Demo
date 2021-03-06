package com.example.jiao.demo.daomodel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by jiao on 2018/10/18.
 */

@Entity
public class TrajectoryModel {

    @Id(autoincrement = true)
    public Long id;

    public String name;
    public String points;
    public String distance;

    public String geojsonPoints;
    @Generated(hash = 1774364428)
    public TrajectoryModel(Long id, String name, String points, String distance,
            String geojsonPoints) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.distance = distance;
        this.geojsonPoints = geojsonPoints;
    }
    @Generated(hash = 1154364838)
    public TrajectoryModel() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPoints() {
        return this.points;
    }
    public void setPoints(String points) {
        this.points = points;
    }
    public String getDistance() {
        return this.distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }
    public String getGeojsonPoints() {
        return this.geojsonPoints;
    }
    public void setGeojsonPoints(String geojsonPoints) {
        this.geojsonPoints = geojsonPoints;
    }
    
    
}
