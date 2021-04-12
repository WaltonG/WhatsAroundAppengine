package com.whatsaround.location;

import java.util.List;

import com.whatsaround.geocell.GeocellManager;
import com.whatsaround.geocell.model.Point;

public class GeocellImplementer {

	private Point point;

	/**
	 * 
	 * @param latitude
	 *            current location lat
	 * @param longitude
	 *            current location long
	 * @return the geocells
	 */
	public List<String> geocellGenerator(double latitude, double longitude) {

		point = new Point(latitude, longitude);
		return GeocellManager.generateGeoCell(point);
	}

}
