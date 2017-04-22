package com.ocam.model;

import android.util.Patterns;

import com.google.gson.annotations.Expose;
import com.ocam.model.types.ActivityStatus;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity
public class Activity {

    /**
     * ID de la Activity en BBDD
     */
    @Unique
	private Long id;

    /**
     * ID autogenerada de la Activity en BD local
     */
    @Id
    private Long id_local;

	@NotNull
	private String shortDescription;

	private String longDescription;

	@ToMany (referencedJoinProperty = "activityId")
	private List<Report> reports;

	private String mide;

	@NotNull
	private Date startDate;

	private Long maxPlaces;

	@Transient
	private String password;

	@NotNull
    @Convert(converter = StatusConverter.class, columnType = String.class)
	private ActivityStatus status;

    @ToOne(joinProperty = "ownerId")
	private Hiker owner;

    private Long ownerId;

	@ToMany
    @JoinEntity(
            entity = JoinActivityHikers.class,
            sourceProperty = "activityId",
            targetProperty = "hikerId"
    )
    @Expose(deserialize = false)
	private List<Hiker> hikers;

    @ToMany
    @JoinEntity(
            entity = JoinActivityGuides.class,
            sourceProperty = "activityId",
            targetProperty = "hikerId"
    )
    @Expose(deserialize = false)
	private List<Hiker> guides;

	@Transient
	private List<ReportDTO> reportDTOs = new ArrayList<ReportDTO>();

	/** Used to resolve relations */
	@Generated(hash = 2040040024)
	private transient DaoSession daoSession;

	/** Used for active entity operations. */
	@Generated(hash = 1662302404)
	private transient ActivityDao myDao;


	@Generated(hash = 1329843187)
	public Activity(Long id, Long id_local, @NotNull String shortDescription,
			String longDescription, String mide, @NotNull Date startDate, Long maxPlaces,
			@NotNull ActivityStatus status, Long ownerId) {
		this.id = id;
		this.id_local = id_local;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		this.mide = mide;
		this.startDate = startDate;
		this.maxPlaces = maxPlaces;
		this.status = status;
		this.ownerId = ownerId;
	}

	@Generated(hash = 126967852)
	public Activity() {
	}

	@Generated(hash = 1847295403)
	private transient Long owner__resolvedKey;


	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public String getMide() {
        if (this.mide != null && Patterns.WEB_URL.matcher(this.mide).matches()) {
            return mide;
        } else {
            return null;
        }
	}

	public void setMide(String mide) {
		this.mide = mide;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Long getMaxPlaces() {
		return maxPlaces;
	}

	public void setMaxPlaces(Long maxPlaces) {
		this.maxPlaces = maxPlaces;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ActivityStatus getStatus() {
		return status;
	}

	public void setStatus(ActivityStatus status) {
		this.status = status;
	}

	public List<ReportDTO> getReportDTOs() {
		return reportDTOs;
	}

	public void setReportDTOs(List<ReportDTO> reportDTOs) {
		this.reportDTOs = reportDTOs;
	}

    public String getLabel() {
		return this.longDescription != null ? this.longDescription : this.mide;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public static class StatusConverter implements PropertyConverter<ActivityStatus, String> {

        @Override
        public ActivityStatus convertToEntityProperty(String databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            for (ActivityStatus status : ActivityStatus.values()) {
                if (status.name().equals(databaseValue)) {
                    return status;
                }
            }
            return ActivityStatus.CLOSED;
        }

        @Override
        public String convertToDatabaseValue(ActivityStatus entityProperty) {
            return entityProperty == null ? null : entityProperty.name();
        }
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

				public Long getId_local() {
					return this.id_local;
				}

				public void setId_local(Long id_local) {
					this.id_local = id_local;
				}

				/** To-one relationship, resolved on first access. */
				@Generated(hash = 850704684)
				public Hiker getOwner() {
					Long __key = this.ownerId;
					if (owner__resolvedKey == null || !owner__resolvedKey.equals(__key)) {
						final DaoSession daoSession = this.daoSession;
						if (daoSession == null) {
							throw new DaoException("Entity is detached from DAO context");
						}
						HikerDao targetDao = daoSession.getHikerDao();
						Hiker ownerNew = targetDao.load(__key);
						synchronized (this) {
							owner = ownerNew;
							owner__resolvedKey = __key;
						}
					}
					return owner;
				}

				/** called by internal mechanisms, do not call yourself. */
				@Generated(hash = 586503802)
				public void setOwner(Hiker owner) {
					synchronized (this) {
						this.owner = owner;
						ownerId = owner == null ? null : owner.getId_local();
						owner__resolvedKey = ownerId;
					}
				}

				/**
				 * To-many relationship, resolved on first access (and after reset).
				 * Changes to to-many relations are not persisted, make changes to the target entity.
				 */
				@Generated(hash = 2007253026)
				public List<Hiker> getHikers() {
					if (hikers == null) {
						final DaoSession daoSession = this.daoSession;
						if (daoSession == null) {
							throw new DaoException("Entity is detached from DAO context");
						}
						HikerDao targetDao = daoSession.getHikerDao();
						List<Hiker> hikersNew = targetDao._queryActivity_Hikers(id_local);
						synchronized (this) {
							if (hikers == null) {
								hikers = hikersNew;
							}
						}
					}
					return hikers;
				}

				/** Resets a to-many relationship, making the next get call to query for a fresh result. */
				@Generated(hash = 1959915735)
				public synchronized void resetHikers() {
					hikers = null;
				}

				/**
				 * To-many relationship, resolved on first access (and after reset).
				 * Changes to to-many relations are not persisted, make changes to the target entity.
				 */
				@Generated(hash = 373301198)
				public List<Hiker> getGuides() {
					if (guides == null) {
						final DaoSession daoSession = this.daoSession;
						if (daoSession == null) {
							throw new DaoException("Entity is detached from DAO context");
						}
						HikerDao targetDao = daoSession.getHikerDao();
						List<Hiker> guidesNew = targetDao._queryActivity_Guides(id_local);
						synchronized (this) {
							if (guides == null) {
								guides = guidesNew;
							}
						}
					}
					return guides;
				}

				/** Resets a to-many relationship, making the next get call to query for a fresh result. */
				@Generated(hash = 989145583)
				public synchronized void resetGuides() {
					guides = null;
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

				/**
				 * To-many relationship, resolved on first access (and after reset).
				 * Changes to to-many relations are not persisted, make changes to the target entity.
				 */
				@Generated(hash = 1130036934)
				public List<Report> getReports() {
					if (reports == null) {
						final DaoSession daoSession = this.daoSession;
						if (daoSession == null) {
							throw new DaoException("Entity is detached from DAO context");
						}
						ReportDao targetDao = daoSession.getReportDao();
						List<Report> reportsNew = targetDao._queryActivity_Reports(id_local);
						synchronized (this) {
							if (reports == null) {
								reports = reportsNew;
							}
						}
					}
					return reports;
				}

				/** Resets a to-many relationship, making the next get call to query for a fresh result. */
				@Generated(hash = 1539079726)
				public synchronized void resetReports() {
					reports = null;
				}

				/** called by internal mechanisms, do not call yourself. */
				@Generated(hash = 1002377807)
				public void __setDaoSession(DaoSession daoSession) {
					this.daoSession = daoSession;
					myDao = daoSession != null ? daoSession.getActivityDao() : null;
				}


}
