package com.ocam.model;

import android.util.Patterns;

import com.ocam.model.types.ActivityStatus;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityDTO {

	private Long id;

	private String shortDescription;

	private String longDescription;

	private String mide;

	private Date startDate;

	private Long maxPlaces;

	private String password;

	private ActivityStatus status;

	private Hiker owner;

    private Long ownerId;

	private List<ReportDTO> reportDTOs = new ArrayList<ReportDTO>();

	private List<Hiker> guides = new ArrayList<Hiker>();

	public List<Hiker> getGuides() {
		return guides;
	}

	private List<Hiker> hikers = new ArrayList<Hiker>();

	public List<Hiker> getHikers() {
		return hikers;
	}

	public void setHikers(List<Hiker> hikers) {
		this.hikers = hikers;
	}

	public void setGuides(List<Hiker> guides) {
		this.guides = guides;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
		return mide;
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

	public Hiker getOwner() {
		return owner;
	}

	public void setOwner(Hiker owner) {
		this.owner = owner;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public List<ReportDTO> getReportDTOs() {
		return reportDTOs;
	}

	public void setReportDTOs(List<ReportDTO> reportDTOs) {
		this.reportDTOs = reportDTOs;
	}
}
