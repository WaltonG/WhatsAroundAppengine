package com.whatsaround.geocell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.whatsaround.entity.Career;
import com.whatsaround.entity.Professional;
import com.whatsaround.geocell.comparator.EntityLocationComparableTuple;
import com.whatsaround.geocell.model.GeocellQuery;
import com.whatsaround.geocell.model.Point;
import com.whatsaround.geocell.model.Tuple;

/**
 * # # Copyright 2010 Alexandre Gellibert # # Licensed under the Apache License,
 * Version 2.0 (the "License"); # you may not use this file except in compliance
 * with the License. # You may obtain a copy of the License at # #
 * http://www.apache.org/licenses/LICENSE-2.0 # # Unless required by applicable
 * law or agreed to in writing, software # distributed under the License is
 * distributed on an "AS IS" BASIS, # WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. # See the License for the specific language
 * governing permissions and # limitations under the License.
 */

public class GeocellManager {

	// The maximum *practical* geocell resolution.
	public static final int MAX_GEOCELL_RESOLUTION = 13;

	/**
	 * Returns the list of geocells (all resolutions) that are containing the
	 * point
	 * 
	 * @param point
	 * @return Returns the list of geocells (all resolutions) that are
	 *         containing the point
	 */
	public static List<String> generateGeoCell(Point point) {
		List<String> geocells = new ArrayList<String>();
		String geocellMax = GeocellUtils.compute(point,
				GeocellManager.MAX_GEOCELL_RESOLUTION);
		for (int i = 1; i < GeocellManager.MAX_GEOCELL_RESOLUTION; i++) {
			geocells.add(GeocellUtils.compute(point, i));
		}
		geocells.add(geocellMax);
		return geocells;
	}

	public static final List<Career> proximitySearchCareer(Point center,
			int maxResults, double maxDistance, Class<Career> entityClass,
			GeocellQuery baseQuery, GeocellQueryEngine queryEngine,
			int maxGeocellResolution) {
		List<EntityLocationComparableTuple<Career>> results = new ArrayList<EntityLocationComparableTuple<Career>>();

		// The current search geocell containing the lat,lon.
		String curContainingGeocell = GeocellUtils.compute(center,
				maxGeocellResolution);

		// Set of already searched cells
		Set<String> searchedCells = new HashSet<String>();

		/*
		 * The currently-being-searched geocells. NOTES: Start with max
		 * possible. Must always be of the same resolution. Must always form a
		 * rectangular region. One of these must be equal to the
		 * cur_containing_geocell.
		 */
		List<String> curGeocells = new ArrayList<String>();
		curGeocells.add(curContainingGeocell);
		double closestPossibleNextResultDist = 0;

		/*
		 * Assumes both a and b are lists of (entity, dist) tuples, *sorted by
		 * dist*. NOTE: This is an in-place merge, and there are guaranteed no
		 * duplicates in the resulting list.
		 */

		int noDirection[] = { 0, 0 };
		@SuppressWarnings("unchecked")
		List<Tuple<int[], Double>> sortedEdgesDistances = Arrays
				.asList(new Tuple<int[], Double>(noDirection, 0d));

		while (!curGeocells.isEmpty()) {
			closestPossibleNextResultDist = sortedEdgesDistances.get(0)
					.getSecond();
			if (maxDistance > 0 && closestPossibleNextResultDist > maxDistance) {
				break;
			}

			Set<String> curTempUnique = new HashSet<String>(curGeocells);
			curTempUnique.removeAll(searchedCells);
			List<String> curGeocellsUnique = new ArrayList<String>(
					curTempUnique);

			List<Career> newResultEntities = queryEngine.query(baseQuery,
					curGeocellsUnique, entityClass);

			searchedCells.addAll(curGeocells);

			// Begin storing distance from the search result entity to the
			// search center along with the search result itself, in a tuple.
			List<EntityLocationComparableTuple<Career>> newResults = new ArrayList<EntityLocationComparableTuple<Career>>();
			for (Career entity : newResultEntities) {
				newResults.add(new EntityLocationComparableTuple<Career>(
						entity, GeocellUtils.distance(center,
								GeocellUtils.getCareerLocation(entity))));
			}

			if (newResults.size() > maxResults) {
				Collections.sort(newResults);
			}
			newResults = newResults.subList(0,
					Math.min(maxResults, newResults.size()));

			// Merge new_results into results
			for (EntityLocationComparableTuple<Career> tuple : newResults) {
				// contains method will check if entity in tuple have same key
				if (!results.contains(tuple)) {
					results.add(tuple);
				}
			}

			Collections.sort(results);
			results = results.subList(0, Math.min(maxResults, results.size()));

			sortedEdgesDistances = GeocellUtils.distanceSortedEdges(
					curGeocells, center);

			if (results.size() == 0 || curGeocells.size() == 4) {
				/*
				 * Either no results (in which case we optimize by not looking
				 * at adjacents, go straight to the parent) or we've searched 4
				 * adjacent geocells, in which case we should now search the
				 * parents of those geocells.
				 */
				curContainingGeocell = curContainingGeocell.substring(0,
						Math.max(curContainingGeocell.length() - 1, 0));
				if (curContainingGeocell.length() == 0) {
					break; // Done with search, we've searched everywhere.
				}
				List<String> oldCurGeocells = new ArrayList<String>(curGeocells);
				curGeocells.clear();
				for (String cell : oldCurGeocells) {
					if (cell.length() > 0) {
						String newCell = cell.substring(0, cell.length() - 1);
						if (!curGeocells.contains(newCell)) {
							curGeocells.add(newCell);
						}
					}
				}
				if (curGeocells.size() == 0) {
					break; // Done with search, we've searched everywhere.
				}
			} else if (curGeocells.size() == 1) {
				// Get adjacent in one direction.
				int nearestEdge[] = sortedEdgesDistances.get(0).getFirst();
				curGeocells.add(GeocellUtils.adjacent(curGeocells.get(0),
						nearestEdge));
			} else if (curGeocells.size() == 2) {
				// Get adjacents in perpendicular direction.
				int nearestEdge[] = GeocellUtils
						.distanceSortedEdges(
								Arrays.asList(curContainingGeocell), center)
						.get(0).getFirst();
				int[] perpendicularNearestEdge = { 0, 0 };
				if (nearestEdge[0] == 0) {
					// Was vertical, perpendicular is horizontal.
					for (Tuple<int[], Double> edgeDistance : sortedEdgesDistances) {
						if (edgeDistance.getFirst()[0] != 0) {
							perpendicularNearestEdge = edgeDistance.getFirst();
							break;
						}
					}
				} else {
					// Was horizontal, perpendicular is vertical.
					for (Tuple<int[], Double> edgeDistance : sortedEdgesDistances) {
						if (edgeDistance.getFirst()[0] == 0) {
							perpendicularNearestEdge = edgeDistance.getFirst();
							break;
						}
					}
				}
				List<String> tempCells = new ArrayList<String>();
				for (String cell : curGeocells) {
					tempCells.add(GeocellUtils.adjacent(cell,
							perpendicularNearestEdge));
				}
				curGeocells.addAll(tempCells);
			}

			// We don't have enough items yet, keep searching.
			if (results.size() < maxResults) {

				continue;
			}

			// If the currently max_results'th closest item is closer than any
			// of the next test geocells, we're done searching.
			double currentFarthestReturnableResultDist = GeocellUtils.distance(
					center, GeocellUtils.getCareerLocation(results.get(
							maxResults - 1).getFirst()));
			if (closestPossibleNextResultDist >= currentFarthestReturnableResultDist) {

				break;
			}

		}
		List<Career> result = new ArrayList<Career>();
		for (Tuple<Career, Double> entry : results.subList(0,
				Math.min(maxResults, results.size()))) {
			if (maxDistance == 0 || entry.getSecond() < maxDistance) {
				result.add(entry.getFirst());
			} else {

			}
		}

		return result;
	}

	public static final List<Professional> proximitySearch(Point center,
			int maxResults, double maxDistance,
			Class<Professional> entityClass, GeocellQuery baseQuery,
			GeocellQueryEngine queryEngine, int maxGeocellResolution) {
		List<EntityLocationComparableTuple<Professional>> results = new ArrayList<EntityLocationComparableTuple<Professional>>();

		// The current search geocell containing the lat,lon.
		String curContainingGeocell = GeocellUtils.compute(center,
				maxGeocellResolution);

		// Set of already searched cells
		Set<String> searchedCells = new HashSet<String>();

		/*
		 * The currently-being-searched geocells. NOTES: Start with max
		 * possible. Must always be of the same resolution. Must always form a
		 * rectangular region. One of these must be equal to the
		 * cur_containing_geocell.
		 */
		List<String> curGeocells = new ArrayList<String>();
		curGeocells.add(curContainingGeocell);
		double closestPossibleNextResultDist = 0;

		/*
		 * Assumes both a and b are lists of (entity, dist) tuples, *sorted by
		 * dist*. NOTE: This is an in-place merge, and there are guaranteed no
		 * duplicates in the resulting list.
		 */

		int noDirection[] = { 0, 0 };
		@SuppressWarnings("unchecked")
		List<Tuple<int[], Double>> sortedEdgesDistances = Arrays
				.asList(new Tuple<int[], Double>(noDirection, 0d));

		while (!curGeocells.isEmpty()) {
			closestPossibleNextResultDist = sortedEdgesDistances.get(0)
					.getSecond();
			if (maxDistance > 0 && closestPossibleNextResultDist > maxDistance) {
				break;
			}

			Set<String> curTempUnique = new HashSet<String>(curGeocells);
			curTempUnique.removeAll(searchedCells);
			List<String> curGeocellsUnique = new ArrayList<String>(
					curTempUnique);

			List<Professional> newResultEntities = queryEngine.query(baseQuery,
					curGeocellsUnique, entityClass);

			searchedCells.addAll(curGeocells);

			// Begin storing distance from the search result entity to the
			// search center along with the search result itself, in a tuple.
			List<EntityLocationComparableTuple<Professional>> newResults = new ArrayList<EntityLocationComparableTuple<Professional>>();
			for (Professional entity : newResultEntities) {
				newResults.add(new EntityLocationComparableTuple<Professional>(
						entity, GeocellUtils.distance(center,
								GeocellUtils.getLocation(entity))));
			}

			if (newResults.size() > maxResults) {
				Collections.sort(newResults);
			}
			newResults = newResults.subList(0,
					Math.min(maxResults, newResults.size()));

			// Merge new_results into results
			for (EntityLocationComparableTuple<Professional> tuple : newResults) {
				// contains method will check if entity in tuple have same key
				if (!results.contains(tuple)) {
					results.add(tuple);
				}
			}

			Collections.sort(results);
			results = results.subList(0, Math.min(maxResults, results.size()));

			sortedEdgesDistances = GeocellUtils.distanceSortedEdges(
					curGeocells, center);

			if (results.size() == 0 || curGeocells.size() == 4) {
				/*
				 * Either no results (in which case we optimize by not looking
				 * at adjacents, go straight to the parent) or we've searched 4
				 * adjacent geocells, in which case we should now search the
				 * parents of those geocells.
				 */
				curContainingGeocell = curContainingGeocell.substring(0,
						Math.max(curContainingGeocell.length() - 1, 0));
				if (curContainingGeocell.length() == 0) {
					break; // Done with search, we've searched everywhere.
				}
				List<String> oldCurGeocells = new ArrayList<String>(curGeocells);
				curGeocells.clear();
				for (String cell : oldCurGeocells) {
					if (cell.length() > 0) {
						String newCell = cell.substring(0, cell.length() - 1);
						if (!curGeocells.contains(newCell)) {
							curGeocells.add(newCell);
						}
					}
				}
				if (curGeocells.size() == 0) {
					break; // Done with search, we've searched everywhere.
				}
			} else if (curGeocells.size() == 1) {
				// Get adjacent in one direction.
				int nearestEdge[] = sortedEdgesDistances.get(0).getFirst();
				curGeocells.add(GeocellUtils.adjacent(curGeocells.get(0),
						nearestEdge));
			} else if (curGeocells.size() == 2) {
				// Get adjacents in perpendicular direction.
				int nearestEdge[] = GeocellUtils
						.distanceSortedEdges(
								Arrays.asList(curContainingGeocell), center)
						.get(0).getFirst();
				int[] perpendicularNearestEdge = { 0, 0 };
				if (nearestEdge[0] == 0) {
					// Was vertical, perpendicular is horizontal.
					for (Tuple<int[], Double> edgeDistance : sortedEdgesDistances) {
						if (edgeDistance.getFirst()[0] != 0) {
							perpendicularNearestEdge = edgeDistance.getFirst();
							break;
						}
					}
				} else {
					// Was horizontal, perpendicular is vertical.
					for (Tuple<int[], Double> edgeDistance : sortedEdgesDistances) {
						if (edgeDistance.getFirst()[0] == 0) {
							perpendicularNearestEdge = edgeDistance.getFirst();
							break;
						}
					}
				}
				List<String> tempCells = new ArrayList<String>();
				for (String cell : curGeocells) {
					tempCells.add(GeocellUtils.adjacent(cell,
							perpendicularNearestEdge));
				}
				curGeocells.addAll(tempCells);
			}

			// We don't have enough items yet, keep searching.
			if (results.size() < maxResults) {

				continue;
			}

			// If the currently max_results'th closest item is closer than any
			// of the next test geocells, we're done searching.
			double currentFarthestReturnableResultDist = GeocellUtils.distance(
					center, GeocellUtils.getLocation(results
							.get(maxResults - 1).getFirst()));
			if (closestPossibleNextResultDist >= currentFarthestReturnableResultDist) {

				break;
			}

		}
		List<Professional> result = new ArrayList<Professional>();
		for (Tuple<Professional, Double> entry : results.subList(0,
				Math.min(maxResults, results.size()))) {
			if (maxDistance == 0 || entry.getSecond() < maxDistance) {
				result.add(entry.getFirst());
			} else {

			}
		}

		return result;
	}

}
