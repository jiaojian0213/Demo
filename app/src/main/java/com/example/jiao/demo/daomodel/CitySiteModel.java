package com.example.jiao.demo.daomodel;

import com.example.jiao.demo.dao.CitySiteModelDao;
import com.example.jiao.demo.dao.DaoSession;
import com.example.jiao.demo.dao.PhotoModelDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * Created by jiaojian on 2018/10/17.
 */
@Entity
public class CitySiteModel {

    @Id(autoincrement = true)
    public Long id;

    //编号	Str
    public String serialNum;
    //名称	Str
    public String name;
    //时代	Str
    public String times;
    //地点	Str
    public String address;
    //发现时间
    public String findTime;
    //发现单位
    public String findCompany;
    //位置经度
    public String longitude ;
    //位置纬度
    public String latitude;
    //海拔	Dou
    public String altitude;
    //类型
    public String type;
    //有无内城
    public boolean hasInner;
    //外城面积
    public double outerArea;
    //内城面积
    public double innerArea;
    //有无翁城
    public boolean hasWengCity;
    //有无墙体
    public boolean hasWall;
    //完整度	Str
    public String Integrity;
    //高度	Dou
    public String height;
    //墙体构成
    public String wallComposition;
    //是否维修过
    public boolean hasRepaired;
    //维修时间
    public String repairTime;
    //地貌特征
    public String features;
    //保护单位类型
    public String protectType;
    //详细信息
    public String detail;

    @ToMany(referencedJoinProperty = "taskId")
    private List<PhotoModel> photos;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1547338596)
    private transient CitySiteModelDao myDao;

    @Generated(hash = 218657225)
    public CitySiteModel(Long id, String serialNum, String name, String times,
            String address, String findTime, String findCompany, String longitude,
            String latitude, String altitude, String type, boolean hasInner,
            double outerArea, double innerArea, boolean hasWengCity,
            boolean hasWall, String Integrity, String height,
            String wallComposition, boolean hasRepaired, String repairTime,
            String features, String protectType, String detail) {
        this.id = id;
        this.serialNum = serialNum;
        this.name = name;
        this.times = times;
        this.address = address;
        this.findTime = findTime;
        this.findCompany = findCompany;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.type = type;
        this.hasInner = hasInner;
        this.outerArea = outerArea;
        this.innerArea = innerArea;
        this.hasWengCity = hasWengCity;
        this.hasWall = hasWall;
        this.Integrity = Integrity;
        this.height = height;
        this.wallComposition = wallComposition;
        this.hasRepaired = hasRepaired;
        this.repairTime = repairTime;
        this.features = features;
        this.protectType = protectType;
        this.detail = detail;
    }

    @Generated(hash = 845105419)
    public CitySiteModel() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNum() {
        return this.serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimes() {
        return this.times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFindTime() {
        return this.findTime;
    }

    public void setFindTime(String findTime) {
        this.findTime = findTime;
    }

    public String getFindCompany() {
        return this.findCompany;
    }

    public void setFindCompany(String findCompany) {
        this.findCompany = findCompany;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAltitude() {
        return this.altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getHasInner() {
        return this.hasInner;
    }

    public void setHasInner(boolean hasInner) {
        this.hasInner = hasInner;
    }

    public double getOuterArea() {
        return this.outerArea;
    }

    public void setOuterArea(double outerArea) {
        this.outerArea = outerArea;
    }

    public double getInnerArea() {
        return this.innerArea;
    }

    public void setInnerArea(double innerArea) {
        this.innerArea = innerArea;
    }

    public boolean getHasWengCity() {
        return this.hasWengCity;
    }

    public void setHasWengCity(boolean hasWengCity) {
        this.hasWengCity = hasWengCity;
    }

    public boolean getHasWall() {
        return this.hasWall;
    }

    public void setHasWall(boolean hasWall) {
        this.hasWall = hasWall;
    }

    public String getIntegrity() {
        return this.Integrity;
    }

    public void setIntegrity(String Integrity) {
        this.Integrity = Integrity;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWallComposition() {
        return this.wallComposition;
    }

    public void setWallComposition(String wallComposition) {
        this.wallComposition = wallComposition;
    }

    public boolean getHasRepaired() {
        return this.hasRepaired;
    }

    public void setHasRepaired(boolean hasRepaired) {
        this.hasRepaired = hasRepaired;
    }

    public String getRepairTime() {
        return this.repairTime;
    }

    public void setRepairTime(String repairTime) {
        this.repairTime = repairTime;
    }

    public String getFeatures() {
        return this.features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getProtectType() {
        return this.protectType;
    }

    public void setProtectType(String protectType) {
        this.protectType = protectType;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1091451885)
    public List<PhotoModel> getPhotos() {
        if (photos == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PhotoModelDao targetDao = daoSession.getPhotoModelDao();
            List<PhotoModel> photosNew = targetDao._queryCitySiteModel_Photos(id);
            synchronized (this) {
                if (photos == null) {
                    photos = photosNew;
                }
            }
        }
        return photos;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 781103891)
    public synchronized void resetPhotos() {
        photos = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1268508722)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCitySiteModelDao() : null;
    }


}
