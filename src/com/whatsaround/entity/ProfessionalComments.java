package com.whatsaround.entity;

import org.joda.time.DateTime;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;

@Entity
@Cache
public class ProfessionalComments {
	
	@Id
	private Long id;
	
	@Index
	private String userKey;
	
	@Index
	private String parentKey;
	
	@Unindex
	private String firstName;
	
	@Unindex
	private String lastName;
	
	@Index
	private DateTime dateAdded;
	
	@Unindex
	private String comment;

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public String getParentKey() {
		return parentKey;
	}

	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
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

	public DateTime getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(DateTime dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
}
