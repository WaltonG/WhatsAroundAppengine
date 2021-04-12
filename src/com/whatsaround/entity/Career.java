package com.whatsaround.entity;

import java.util.List;

import org.joda.time.DateTime;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;

@Entity
@Cache
public class Career {
	
	@Id
	private Long id;

	@Index
	private String parentKey;

	@Index
	private String country;

	@Unindex
	private double longitude;

	@Unindex
	private double latitude;

	@Index
	private String careerCategory;

	@Unindex
	private String careerTitle;

	@Unindex
	private String careerDetails;

	@Unindex
	private String contactDetails;

	@Index
	private List<String> geoCells;

	@Index
	private DateTime dateAdded;
	
	@IgnoreSave
	private String comments;
	

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getParentKey() {
		return parentKey;
	}

	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getCareerCategory() {
		return careerCategory;
	}

	public void setCareerCategory(String careerCategory) {
		this.careerCategory = careerCategory;
	}

	public String getCareerTitle() {
		return careerTitle;
	}

	public void setCareerTitle(String careerTitle) {
		this.careerTitle = careerTitle;
	}

	public String getCareerDetails() {
		return careerDetails;
	}

	public void setCareerDetails(String careerDetails) {
		this.careerDetails = careerDetails;
	}

	public String getContactDetails() {
		return contactDetails;
	}

	public void setContactDetails(String contactDetails) {
		this.contactDetails = contactDetails;
	}

	public List<String> getGeoCells() {
		return geoCells;
	}

	public void setGeoCells(List<String> geoCells) {
		this.geoCells = geoCells;
	}

	public DateTime getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(DateTime dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getCalender() {

		return dateAdded.toString("dd-MM-yyyy");
	}

}
