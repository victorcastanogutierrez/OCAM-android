package com.ocam.model;

import com.ocam.model.types.GPSPoint;

import java.util.Date;


public class Report implements Comparable<Report> {

	private Date date;

	private Activity activity;

	private Hiker hiker;

	private GPSPoint point;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Hiker getHiker() {
		return hiker;
	}

	public void setHiker(Hiker hiker) {
		this.hiker = hiker;
	}

	public GPSPoint getPoint() {
		return point;
	}

	public void setPoint(GPSPoint point) {
		this.point = point;
	}

	@Override
	public int compareTo(Report o) {
		return this.date.compareTo(o.getDate());
	}

}
