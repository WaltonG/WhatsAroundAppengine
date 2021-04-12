package com.whatsaround.location;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import com.whatsaround.entity.Professional;
import com.whatsaround.geocell.GeocellQueryEngine;
import com.whatsaround.geocell.model.GeocellQuery;

public class ProfessionalGeocellQueryEngine implements GeocellQueryEngine {
	
	public static final String GEOCELLS_PROPERTY = "geoCells";
	public static final int PAGE_SIZE = 10;
	private int pageSize;
	private String geocellsProperty;
	private Key<Professional> key;
	private String professionCategory;

	public ProfessionalGeocellQueryEngine(Key<Professional> key,
			String professionCategory) {
		this(GEOCELLS_PROPERTY, key, professionCategory, PAGE_SIZE);
	}

	public ProfessionalGeocellQueryEngine(String geocellsProperty,
			Key<Professional> key, String professionCategory, int pageSize) {

		this.geocellsProperty = geocellsProperty;
		this.key = key;
		this.professionCategory = professionCategory;
		this.pageSize = pageSize;
	}

	@Override
	public <T> List<T> query(GeocellQuery baseQuery, List<String> geoCells,
			Class<T> entityClass) {

		Query<T> query = ofy().load().type(entityClass);
		query = query.filter(geocellsProperty + " IN", geoCells);
		query = query.filter("professionCategory ==", professionCategory);

		if (key != null) {
			query = query.filterKey(">=", key);
		}
		return query.limit(pageSize + 1).list();
	}

}
