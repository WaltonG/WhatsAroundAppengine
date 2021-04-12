package com.whatsaround.location;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import com.whatsaround.entity.Career;
import com.whatsaround.geocell.GeocellQueryEngine;
import com.whatsaround.geocell.model.GeocellQuery;

public class CareerGeocellQueryEngine implements GeocellQueryEngine {
	
	public static final String GEOCELLS_PROPERTY = "geoCells";
	public static final int PAGE_SIZE = 10;
	private int pageSize;
	private String geocellsProperty;
	private Key<Career> key;
	private String careerCategory;

	public CareerGeocellQueryEngine(Key<Career> key,
			String careerCategory) {
		this(GEOCELLS_PROPERTY, key, careerCategory, PAGE_SIZE);
	}

	public CareerGeocellQueryEngine(String geocellsProperty,
			Key<Career> key, String careerCategory, int pageSize) {

		this.geocellsProperty = geocellsProperty;
		this.key = key;
		this.careerCategory = careerCategory;
		this.pageSize = pageSize;
	}

	@Override
	public <T> List<T> query(GeocellQuery baseQuery, List<String> geoCells,
			Class<T> entityClass) {

		Query<T> query = ofy().load().type(entityClass);
		query = query.filter(geocellsProperty + " IN", geoCells);
		query = query.filter("careerCategory ==", careerCategory);

		if (key != null) {
			query = query.filterKey(">=", key);
		}
		return query.limit(pageSize + 1).list();
	}

}
