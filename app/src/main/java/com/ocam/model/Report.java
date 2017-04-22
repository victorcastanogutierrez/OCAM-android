package com.ocam.model;

import com.google.gson.annotations.Expose;
import com.ocam.model.types.GPSPoint;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;
import com.ocam.model.types.GPSPointDao;

import java.util.Date;

@Entity
public class Report {

    @Id
    private Long id;

    @NotNull
    private Long date;

    @Transient
    private Activity activity;

    @ToOne(joinProperty = "hikerId")
    private Hiker hiker;

    private Long hikerId;

    private Long activityId;

    @ToOne(joinProperty = "gpsPointId")
    private GPSPoint point;

    /**
     * Indica si el report esta pendiente de envio o no
     */
    @NotNull
    @Expose(deserialize = false, serialize =  false)
    private Boolean pending = Boolean.FALSE;

    private Long gpsPointId;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 485466363)
    private transient ReportDao myDao;



    @Generated(hash = 416159130)
    public Report(Long id, @NotNull Long date, Long hikerId, Long activityId,
            @NotNull Boolean pending, Long gpsPointId) {
        this.id = id;
        this.date = date;
        this.hikerId = hikerId;
        this.activityId = activityId;
        this.pending = pending;
        this.gpsPointId = gpsPointId;
    }

    @Generated(hash = 1739299007)
    public Report() {
    }

    @Generated(hash = 99060812)
    private transient Long hiker__resolvedKey;

    @Generated(hash = 1150438727)
    private transient Long point__resolvedKey;



    public Boolean getPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGpsPointId() {
        return this.gpsPointId;
    }

    public void setGpsPointId(Long gpsPointId) {
        this.gpsPointId = gpsPointId;
    }

    public Long getActivityId() {
        return this.activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getHikerId() {
        return this.hikerId;
    }

    public void setHikerId(Long hikerId) {
        this.hikerId = hikerId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2085977116)
    public Hiker getHiker() {
        Long __key = this.hikerId;
        if (hiker__resolvedKey == null || !hiker__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            HikerDao targetDao = daoSession.getHikerDao();
            Hiker hikerNew = targetDao.load(__key);
            synchronized (this) {
                hiker = hikerNew;
                hiker__resolvedKey = __key;
            }
        }
        return hiker;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1937889363)
    public void setHiker(Hiker hiker) {
        synchronized (this) {
            this.hiker = hiker;
            hikerId = hiker == null ? null : hiker.getId_local();
            hiker__resolvedKey = hikerId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1123972688)
    public GPSPoint getPoint() {
        Long __key = this.gpsPointId;
        if (point__resolvedKey == null || !point__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GPSPointDao targetDao = daoSession.getGPSPointDao();
            GPSPoint pointNew = targetDao.load(__key);
            synchronized (this) {
                point = pointNew;
                point__resolvedKey = __key;
            }
        }
        return point;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1094467023)
    public void setPoint(GPSPoint point) {
        synchronized (this) {
            this.point = point;
            gpsPointId = point == null ? null : point.getId();
            point__resolvedKey = gpsPointId;
        }
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
    @Generated(hash = 1237337053)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getReportDao() : null;
    }
}
