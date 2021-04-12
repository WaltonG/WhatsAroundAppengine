package com.whatsaround;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.googlecode.objectify.Key;
import com.whatsaround.dao.CareerDAO;
import com.whatsaround.entity.Career;
import com.whatsaround.entity.User;
import com.whatsaround.location.GeocellImplementer;
import static com.googlecode.objectify.ObjectifyService.ofy;

public class NewCareer extends HttpServlet {
	private String careerTitle;
	private String country;
	private String careerCategory;
	private String careerDetails;
	private String contactDetails;
	private double longitude;
	private double latitude;
	private List<String> geoCells;
	private GeocellImplementer geocellImplementer = new GeocellImplementer();
	private User user = new User();
	private Career career = new Career();
	private String accountName;
	private Key<User> userKey;
	private CareerDAO careerDAO = new CareerDAO();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		ServletInputStream inputStream = req.getInputStream();
		int numofbytes = inputStream.available();
		byte[] b = new byte[numofbytes];
		inputStream.read(b);

		Gson gson = new Gson();
		String gsonDetails = new String(b);
		Type type = new TypeToken<HashMap<String, String>>() {
		}.getType();
		HashMap<String, String> careerDetail = gson.fromJson(gsonDetails, type);

		if (careerDetail.containsKey("careerCategory")) {
			careerCategory = careerDetail.get("careerCategory");
		}
		if (careerDetail.containsKey("careerDetails")) {
			careerDetails = careerDetail.get("careerDetails");
		}

		if (careerDetail.containsKey("latitude")) {
			latitude = Double.parseDouble(careerDetail.get("latitude"));
		}
		if (careerDetail.containsKey("longitude")) {
			longitude = Double.parseDouble(careerDetail.get("longitude"));
		}

		if (careerDetail.containsKey("contactDetails")) {
			contactDetails= careerDetail.get("contactDetails");
		}

		if (careerDetail.containsKey("country")) {
			country = careerDetail.get("country");
		}
		
		if (careerDetail.containsKey("careerTitle")) {
			careerTitle = careerDetail.get("careerTitle");
		}
		
		if (careerDetail.containsKey("accountName")) {
			accountName = careerDetail.get("accountName");
		}

		geoCells = geocellImplementer.geocellGenerator(latitude, longitude);

		userKey = user.getkey(accountName);

		career.setCareerCategory(careerCategory);
		career.setGeoCells(geoCells);
		career.setLatitude(latitude);
		career.setLongitude(longitude);
		career.setCareerDetails(careerDetails);
		career.setCareerTitle(careerTitle);
		career.setParentKey(userKey.getString());
        career.setContactDetails(contactDetails);
		career.setCountry(country);
		career.setDateAdded(DateTime.now());
		
		ofy().save().entity(career).now();
		}
}
