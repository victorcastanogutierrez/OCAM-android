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

	private ActivityStatus status;

	private HikerDTO owner;

	private List<HikerDTO> hikers = new ArrayList<HikerDTO>();

	private List<HikerDTO> guides = new ArrayList<HikerDTO>();

	private List<ReportDTO> reportDTOs = new ArrayList<ReportDTO>();

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

	public HikerDTO getOwner() {
		return owner;
	}

	public void setOwner(HikerDTO owner) {
		this.owner = owner;
	}

	public List<HikerDTO> getHikers() {
		return hikers;
	}

	public void setHikers(List<HikerDTO> hikers) {
		this.hikers = hikers;
	}

	public List<HikerDTO> getGuides() {
		return guides;
	}

	public void setGuides(List<HikerDTO> guides) {
		this.guides = guides;
	}

	public List<ReportDTO> getReportDTOs() {
		return reportDTOs;
	}

	public void setReportDTOs(List<ReportDTO> reportDTOs) {
		this.reportDTOs = reportDTOs;
	}

    public String getFormattedStatus() {
        return new String("RUNNING").equals(this.status.toString()) ? "EN CURSO" :
				new String("CLOSED").equals(this.status.toString()) ? "FINALIZADA" : "PENDIENTE";
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
