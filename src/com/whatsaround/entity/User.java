package com.whatsaround.entity;

import java.util.List;


import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;

@Entity
@Cache
public class User {
	
	public Key<User> getkey(String accountName) {
		return Key.create(User.class, accountName);
	}

	@Id
	private String accountName;
	
	@Index
	private String country;

	@Unindex
	private double longitude;

	@Unindex
	private double latitude;

	@Index
	private String professionCategory;

	@Index
	private String firstName;

	@Index
	private String lastName;

	@Unindex
	private String phoneNumber;

	@Unindex
	private byte[] profilePicture;

	@Unindex
	private String professionTitle;

	@Index
	private List<String> geoCells;

	@Unindex
	private byte[] cv;

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

	public byte[] getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(byte[] profilePicture) {
		this.profilePicture = profilePicture;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getProfessionCategory() {
		return professionCategory;
	}

	public void setProfessionCategory(String professionCategory) {
		this.professionCategory = professionCategory;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}



	public String getProfessionTitle() {
		return professionTitle;
	}

	public void setProfessionTitle(String professionTitle) {
		this.professionTitle = professionTitle;
	}

	public List<String> getGeoCells() {
		return geoCells;
	}

	public void setGeoCells(List<String> geoCells) {
		this.geoCells = geoCells;
	}

	public byte[] getCv() {
		return cv;
	}

	public void setCv(byte[] cv) {
		this.cv = cv;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
}
