package com.ocam.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HikerDTO {

	private String email;

	private String login;

	private String username;

	private String password;

	private String newPassword;

	private Boolean active = Boolean.FALSE;

	private String activeCode;

	private List<Activity> activities = new ArrayList<Activity>();

	private List<Activity> activityGuide = new ArrayList<Activity>();

	private List<ReportDTO> reportDTOs = new ArrayList<ReportDTO>();

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

	public List<ReportDTO> getReportDTOs() {
		return reportDTOs;
	}

	public void setReportDTOs(List<ReportDTO> reportDTOs) {
		this.reportDTOs = reportDTOs;
	}

	public List<Activity> getOwneds() {
		return owneds;
	}

	public void setOwneds(List<Activity> owneds) {
		this.owneds = owneds;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
