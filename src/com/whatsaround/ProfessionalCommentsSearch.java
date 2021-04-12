package com.whatsaround;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.googlecode.objectify.cmd.Query;
import com.whatsaround.entity.Professional;
import com.whatsaround.entity.ProfessionalComments;
import com.whatsaround.utility.JodaSerializer;

public class ProfessionalCommentsSearch extends HttpServlet {

	public static final int PAGE_SIZE = 10;
	private Professional professional;
	private List<ProfessionalComments> professionalCommentsList;
	private String parentKey;
	private String cursorString;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		ServletInputStream inputStream = req.getInputStream();
		int numofbytes = inputStream.available();
		byte[] b = new byte[numofbytes];
		inputStream.read(b);

		Gson gson = new Gson();
		String gsonDetails = new String(b);
		Type type = new TypeToken<Professional>() {
		}.getType();

		professional = gson.fromJson(gsonDetails, type);

		parentKey = ofy().load().entity(professional).getKey().getString();

		cursorString = req.getHeader("cursorString");

		Query<ProfessionalComments> query = ofy().load()
				.type(ProfessionalComments.class)
				.filter("parentKey ==", parentKey).order("-dateAdded")
				.limit(PAGE_SIZE);

		if (cursorString != null) {
			query = query.startAt(Cursor.fromWebSafeString(cursorString));
			professionalCommentsList = query.list();
			QueryResultIterator<ProfessionalComments> iterator = query
					.iterator();
			if (iterator.getCursor() != null) {
				cursorString = iterator.getCursor().toWebSafeString();
				resp.setHeader("cursorString", cursorString);
			}
		} else {
			professionalCommentsList = query.list();
			QueryResultIterator<ProfessionalComments> iterator = query
					.iterator();
			if (iterator.getCursor() != null) {
				cursorString = iterator.getCursor().toWebSafeString();
				resp.setHeader("cursorString", cursorString);
			}
		}
		Gson gsonBuilder = (new GsonBuilder()).serializeNulls()
				.registerTypeAdapter(DateTime.class, new JodaSerializer())
				.create();
		Type typeOfSrc = new TypeToken<List<ProfessionalComments>>() {
		}.getType();
		String professionalComments = gsonBuilder.toJson(
				professionalCommentsList, typeOfSrc);

		ServletOutputStream os = resp.getOutputStream();
		os.write(professionalComments.getBytes());

	}
}
