package com.ocam.model;

import com.ocam.model.types.GPSPoint;


public class ReportDTO {

	private Long date;

	private Activity activity;

	private HikerDTO hiker;

	private GPSPoint point;

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

	public HikerDTO getHikerDTO() {
		return hiker;
	}

	public void setHikerDTO(HikerDTO hikerDTO) {
		this.hiker = hikerDTO;
	}

	public GPSPoint getPoint() {
		return point;
	}

	public void setPoint(GPSPoint point) {
		this.point = point;
	}

}
