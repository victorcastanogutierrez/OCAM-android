package com.ocam.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class JoinActivityHikers {

    @Id
    private Long id;

    private Long activityId;

    private Long hikerId;

    @Generated(hash = 1424997153)
    public JoinActivityHikers(Long id, Long activityId, Long hikerId) {
        this.id = id;
        this.activityId = activityId;
        this.hikerId = hikerId;
    }

    @Generated(hash = 1842200853)
    public JoinActivityHikers() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
