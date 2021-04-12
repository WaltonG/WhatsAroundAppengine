package com.whatsaround;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.googlecode.objectify.Key;
import com.whatsaround.entity.Professional;
import com.whatsaround.entity.User;
import com.whatsaround.location.GeocellImplementer;

public class NewUser extends HttpServlet {
	private String firstName;
	private String lastName;
	private String professionTitle;
	private String country;
	private String phoneNumber;
	private byte[] profilePic;
	private byte[] cV;
	private double longitude;
	private double latitude;
	private String professionCategory;
	private List<String> geoCells;
	private GeocellImplementer geocellImplementer = new GeocellImplementer();
	private User user;
	private Professional professional;
	private String accountName;
	private Key<User> userKey;
	byte[] b;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		ServletInputStream inputStream = req.getInputStream();
		int numofbytes = inputStream.available();
		byte[] b = new byte[numofbytes];
		inputStream.read(b);

		Gson gson = new Gson();
		String gsonDetails = new String(b);
		Type type = new TypeToken<HashMap<String, byte[]>>() {
		}.getType();
		HashMap<String, byte[]> userDetails = gson.fromJson(gsonDetails, type);

		if (userDetails.containsKey("firstName")) {
			firstName = new String(userDetails.get("firstName"));
		}
		if (userDetails.containsKey("lastName")) {
			lastName = new String(userDetails.get("lastName"));
		}

		if (userDetails.containsKey("latitude")) {
			latitude = Double.parseDouble(new String(userDetails.get("latitude")));
		}
		if (userDetails.containsKey("longitude")) {
			longitude = Double.parseDouble(new String(userDetails.get("longitude")));
		}

		if (userDetails.containsKey("profilePicture")) {
			profilePic = userDetails.get("profilePicture");
		}

		if (userDetails.containsKey("country")) {
			country = new String(userDetails.get("country"));
		}
		if (userDetails.containsKey("professionCategory")) {
			professionCategory = new String (userDetails.get("professionCategory"));
		}
		if (userDetails.containsKey("professionTitle")) {
			professionTitle = new String (userDetails.get("professionTitle"));
		}
		if (userDetails.containsKey("phoneNumber")) {
			phoneNumber = new String(userDetails.get("phoneNumber"));
		}
		if (userDetails.containsKey("accountName")) {
			accountName = new String(userDetails.get("accountName"));
		}

		user = new User();
		geoCells = geocellImplementer.geocellGenerator(latitude, longitude);

		userKey = user.getkey(accountName);

		professional = new Professional();

		if (profilePic != null) {
			professional.setProfilePicture(profilePic);
			user.setProfilePicture(profilePic);
		}

		if (professionTitle != null) {
			professional.setProfessionTitle(professionTitle);
			user.setProfessionTitle(professionTitle);
		}
		if (professionCategory != null) {
			professional.setProfessionCategory(professionCategory);
			user.setProfessionCategory(professionCategory);
		}
		if (longitude != 0 || latitude != 0) {
			professional.setLongitude(longitude);
			professional.setLatitude(latitude);
			user.setLongitude(longitude);
			user.setLatitude(latitude);
		}

		if (phoneNumber != null) {
			professional.setPhoneNumber(phoneNumber);
			professional.setPhoneNumber(phoneNumber);
		}
		if (geoCells != null) {
			professional.setGeoCells(geoCells);
			user.setGeoCells(geoCells);
		}
		professional.setFirstName(firstName);
		user.setFirstName(firstName);

		professional.setLastName(lastName);
		professional.setCountry(country);
		professional.setParentKey(userKey.getString());
		user.setLastName(lastName);
		user.setCountry(country);

		user.setAccountName(accountName);

		ofy().save().entities(user, professional).now();

		user = ofy().load().key(userKey).get();

		if (user == null) {
			professional = null;
		} else {
			professional = new Professional();
			professional.setProfilePicture(user.getProfilePicture());
			professional.setFirstName(user.getFirstName());
			professional.setLastName(user.getLastName());
		}

		Gson gsonBuilder = new GsonBuilder().serializeNulls().create();
		Type resultType = new TypeToken<Professional>() {
		}.getType();
		String resultDetails = gsonBuilder.toJson(professional, resultType);

		ServletOutputStream os = resp.getOutputStream();
		os.write(resultDetails.getBytes());
	}

}
