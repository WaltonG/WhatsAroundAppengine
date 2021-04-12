package com.whatsaround;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.googlecode.objectify.cmd.Query;
import com.whatsaround.entity.Professional;

public class ProfessionalSearchCountry extends HttpServlet {
	public static final int PAGE_SIZE = 10;
	private String country;
	private List<Professional> professionalList;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		ServletInputStream inputStream = req.getInputStream();
		int numofbytes = inputStream.available();
		byte[] b = new byte[numofbytes];
		inputStream.read(b);

		Gson gson = new Gson();

		country = new String(b);
		
		professionalList = ofy().load().type(Professional.class)
				.filter("country == ", country).list();
		

		Type typeOfSrc = new TypeToken<List<Professional>>() {
		}.getType();
		String professionalListString = gson
				.toJson(professionalList, typeOfSrc);
		
		ServletOutputStream os = resp.getOutputStream();
		os.write(professionalListString.getBytes());
	}
}
