package com.ocam.model;

import java.util.ArrayList;
import java.util.List;

public class Hiker {

	private String email;

	private String login;

	private String password;

	private Boolean active = Boolean.FALSE;

	private String activeCode;

	private List<Activity> activities = new ArrayList<Activity>();

	private List<Activity> activityGuide = new ArrayList<Activity>();

	private List<Report> reports = new ArrayList<Report>();

	private List<Activity> owneds = new ArrayList<Activity>();

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getActiveCode() {
		return activeCode;
	}

	public void setActiveCode(String activeCode) {
		this.activeCode = activeCode;
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}

	public List<Activity> getActivityGuide() {
		return activityGuide;
	}

	public void setActivityGuide(List<Activity> activityGuide) {
		this.activityGuide = activityGuide;
	}

	public List<Report> getReports() {
		return reports;
	}

	public void setReports(List<Report> reports) {
		this.reports = reports;
	}

	public List<Activity> getOwneds() {
		return owneds;
	}

	public void setOwneds(List<Activity> owneds) {
		this.owneds = owneds;
	}
}
