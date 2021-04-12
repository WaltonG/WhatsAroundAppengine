package com.whatsaround;

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

import com.google.gson.reflect.TypeToken;
import com.googlecode.objectify.Key;
import com.whatsaround.dao.ProfessionalDAO;
import com.whatsaround.entity.Professional;


import static com.googlecode.objectify.ObjectifyService.ofy;

public class ProfessionalSearch extends HttpServlet {
	
	public static final int PAGE_SIZE = 10;
	private String professionCategory;
	private String webSafeString;
	private Key<Professional> professionalKey;
	private List<Professional> professionalList;
	private double latitude;
	private double longitude;
	private String nextResultCursor;

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

		HashMap<String, String> professionalDetails = gson.fromJson(
				gsonDetails, type);
		if (professionalDetails.containsKey("professionCategory")) {
			professionCategory = professionalDetails
					.get("professionCategory");
		}

		if (professionalDetails.containsKey("latitude")) {
			latitude = Double.parseDouble(professionalDetails.get("latitude"));
		}
		if (professionalDetails.containsKey("longitude")) {
			longitude = Double
					.parseDouble(professionalDetails.get("longitude"));
		}

		if (req.getHeader("webSafeString") == null) {
			professionalKey = null;
		} else {
			webSafeString = req.getHeader(webSafeString);
			professionalKey = Key.create(webSafeString);
		}

		ProfessionalDAO professionalDAO = new ProfessionalDAO();

		professionalList = professionalDAO.findByCategoryLoc(latitude,
				longitude, professionalKey, professionCategory);

		if (professionalList.size() == PAGE_SIZE + 1) {

			Key<Professional> nextResultCursorKey = ofy().load()
					.entity(professionalList.get(PAGE_SIZE)).getKey();
			nextResultCursor = nextResultCursorKey.getString();
			resp.setHeader("nextResultCursor", nextResultCursor);
			professionalList.remove(PAGE_SIZE);
		}
		

		Type typeOfSrc = new TypeToken<List<Professional>>() {
		}.getType();
		String professionalListString = gson.toJson(professionalList,
				typeOfSrc);

		ServletOutputStream os = resp.getOutputStream();
		os.write(professionalListString.getBytes());

	}
}
