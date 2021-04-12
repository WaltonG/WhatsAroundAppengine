package com.whatsaround;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.whatsaround.entity.Professional;
import com.whatsaround.entity.ProfessionalComments;
import com.whatsaround.entity.User;

public class NewProfessionalComment extends HttpServlet {

	private String userKey;
	private String parentKey;
	private String firstName;
	private String lastName;
	private DateTime dateAdded;
	private String comment;
	private Professional professional;
	private String accountName;
	private User user = new User();
	private ProfessionalComments professionalComments = new ProfessionalComments(); 

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		ServletInputStream inputStream = req.getInputStream();
		int numofbytes = inputStream.available();
		byte[] b = new byte[numofbytes];
		inputStream.read(b);
		
		accountName = req.getHeader("accountName");

		Gson gson = new Gson();
		String gsonDetails = new String(b);
		Type type = new TypeToken<Professional>() {
		}.getType();

		professional = gson.fromJson(gsonDetails, type);
		parentKey = ofy().load().entity(professional).getKey().getString();
		comment =professional.getComments();
		userKey = user.getkey(accountName).getString();
		firstName = ofy().load().key(user.getkey(accountName)).get().getFirstName();
		lastName = ofy().load().key(user.getkey(accountName)).get().getLastName();
		
		professionalComments.setComment(comment);
		professionalComments.setDateAdded(DateTime.now());
		professionalComments.setFirstName(firstName);
		professionalComments.setLastName(lastName);
		professionalComments.setParentKey(parentKey);
		professionalComments.setUserKey(userKey);
		
		ofy().save().entity(professionalComments).now();
	}
}
