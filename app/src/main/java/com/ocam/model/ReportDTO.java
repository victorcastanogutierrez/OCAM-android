package com.ocam.model;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.ocam.model.types.GPSPoint;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ReportDTO {

	private Long date;

	private Activity activity;

	private HikerDTO hiker;

	private GPSPoint point;

	private String color;

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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getFormattedDate() {
		return new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new Date(this.date));
	}
}
