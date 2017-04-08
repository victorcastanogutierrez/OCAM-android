package com.ocam.model;

import com.ocam.model.types.ActivityStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Activity {

	private Long id;

	private String shortDescription;

	private String longDescription;

	private String mide;

	private Date startDate;

	private Long maxPlaces;

	private String password;

	private String track;

	private ActivityStatus status;

	private Hiker owner;

	private List<Hiker> hikers = new ArrayList<Hiker>();

	private List<Hiker> guides = new ArrayList<Hiker>();

	private List<Report> reports = new ArrayList<Report>();

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

	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
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

	public List<Hiker> getHikers() {
		return hikers;
	}

	public void setHikers(List<Hiker> hikers) {
		this.hikers = hikers;
	}

	public List<Hiker> getGuides() {
		return guides;
	}

	public void setGuides(List<Hiker> guides) {
		this.guides = guides;
	}

	public List<Report> getReports() {
		return reports;
	}

	public void setReports(List<Report> reports) {
		this.reports = reports;
	}

    public String getFormattedStatus() {
        return new String("RUNNING").equals(this.status.toString()) ? "EN CURSO" : "PENDIENTE";
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
}
