package com.whatsaround.dao;

import java.util.List;

import com.googlecode.objectify.Key;
import com.whatsaround.entity.Career;
import com.whatsaround.geocell.GeocellManager;
import com.whatsaround.geocell.GeocellQueryEngine;
import com.whatsaround.geocell.model.GeocellQuery;
import com.whatsaround.geocell.model.Point;
import com.whatsaround.location.CareerGeocellQueryEngine;

public class CareerDAO {
	
	public List<Career> findByCategoryLoc(double latitude,
			double longitude, Key<Career> key, String careerCategory) {

		GeocellQuery baseQuery = new GeocellQuery();
		GeocellQueryEngine queryEngine = new CareerGeocellQueryEngine(
				key, careerCategory);
		
		return GeocellManager.proximitySearchCareer(new Point(latitude, longitude),
				11, 0, Career.class, baseQuery, queryEngine,
				GeocellManager.MAX_GEOCELL_RESOLUTION);

	}
}
