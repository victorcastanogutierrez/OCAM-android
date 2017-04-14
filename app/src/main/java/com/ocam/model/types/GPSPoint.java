package com.ocam.model.types;

import android.location.Location;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GPSPoint {

	@Id
	private Long id;

	public GPSPoint(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Generated(hash = 1190344889)
	public GPSPoint(Long id, float latitude, float longitude) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public GPSPoint(Location location) {
		this.latitude = (float) location.getLatitude();
		this.longitude = (float) location.getLongitude();
	}

	@Generated(hash = 332693450)
	public GPSPoint() {
	}

	@NotNull
	private float latitude;

	@NotNull
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
