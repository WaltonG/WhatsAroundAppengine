package com.whatsaround.dao;

import java.util.List;

import com.googlecode.objectify.Key;
import com.whatsaround.entity.Professional;
import com.whatsaround.geocell.GeocellManager;
import com.whatsaround.geocell.GeocellQueryEngine;
import com.whatsaround.geocell.model.GeocellQuery;
import com.whatsaround.geocell.model.Point;
import com.whatsaround.location.ProfessionalGeocellQueryEngine;

public class ProfessionalDAO {

	public List<Professional> findByCategoryLoc(double latitude,
			double longitude, Key<Professional> key, String professionCategory) {

		GeocellQuery baseQuery = new GeocellQuery();
		GeocellQueryEngine queryEngine = new ProfessionalGeocellQueryEngine(
				key, professionCategory);
		
		return GeocellManager.proximitySearch(new Point(latitude, longitude),
				11, 0, Professional.class, baseQuery, queryEngine,
				GeocellManager.MAX_GEOCELL_RESOLUTION);

	}
}
