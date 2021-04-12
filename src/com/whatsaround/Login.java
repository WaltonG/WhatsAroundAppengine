package com.whatsaround;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

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

public class Login extends HttpServlet{
private User user;
private Key<User> userKey;
private String accountName;
Professional professional;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {


		ServletInputStream inputStream = req.getInputStream();
		int numofbytes = inputStream.available();
		byte[] b = new byte[numofbytes];
		inputStream.read(b);
		
		user = new User();
		
		accountName = new String(b);
		userKey = user.getkey(accountName);
		
		user = ofy().load().key(userKey).get();
		if(user == null){
			professional = null;
		}else {
			professional = new Professional();
			professional.setFirstName(user.getFirstName());
			professional.setLastName(user.getLastName());
			professional.setProfilePicture(user.getProfilePicture());
			professional.setCountry(user.getCountry());
			
		}
		
		Gson gsonBuilder = new GsonBuilder().serializeNulls().create();
		Type resultType = new TypeToken<Professional>() {
		}.getType();
	    String resultDetails = gsonBuilder.toJson(professional, resultType);
	    
	    ServletOutputStream os = resp.getOutputStream();
		os.write(resultDetails.getBytes());
	}

}
