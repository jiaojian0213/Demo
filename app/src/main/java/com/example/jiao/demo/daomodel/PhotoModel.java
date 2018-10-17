package com.example.jiao.demo.daomodel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Created by jiaojian on 2017/6/8.
 */

@Entity
public class PhotoModel implements Serializable {

    static final long serialVersionUID = 645435;

    public interface Type {
        int CITYSITE = 1;
        int IDCARD = 2;
        int BANKCARD = 3;
        int OTHERCARD = 4;
        int PRINTPHOTO = 5;
        int SIGINPHOTO = 6;
        int PUBLICITYPHOTO = 8;
    }

    public interface TaskType {
        int YZ_CHENGBAO = 1;
        int YZ_LIPEI = 2;
    }

    @Id(autoincrement = true)
    private Long id;
    private long taskId;
    private String path;
    private double longitude;
    private double latitude;
    private String type;
    private String url;
    private String thumbnail_url;
    private String createdate;
    private int status;//0未完成，1已完成 2线上图片，验地用
    private int taskType;
    @Generated(hash = 896115059)
    public PhotoModel(Long id, long taskId, String path, double longitude,
            double latitude, String type, String url, String thumbnail_url,
            String createdate, int status, int taskType) {
        this.id = id;
        this.taskId = taskId;
        this.path = path;
        this.longitude = longitude;
        this.latitude = latitude;
        this.type = type;
        this.url = url;
        this.thumbnail_url = thumbnail_url;
        this.createdate = createdate;
        this.status = status;
        this.taskType = taskType;
    }
    @Generated(hash = 804076068)
    public PhotoModel() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getTaskId() {
        return this.taskId;
    }
    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public double getLongitude() {
        return this.longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return this.latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getThumbnail_url() {
        return this.thumbnail_url;
    }
    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }
    public String getCreatedate() {
        return this.createdate;
    }
    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public int getTaskType() {
        return this.taskType;
    }
    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

}
