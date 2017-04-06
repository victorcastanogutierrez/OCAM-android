package com.ocam.model.types;

public class GPSPoint {

	public GPSPoint(float lat, float lon) {
		this.latitude = lat;
		this.longitude = lon;
	}

	private float latitude;

	private float longitude;

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
}
