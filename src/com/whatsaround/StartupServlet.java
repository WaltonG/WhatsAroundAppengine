package com.whatsaround;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.googlecode.objectify.ObjectifyService;
import com.whatsaround.entity.Career;
import com.whatsaround.entity.CareerComments;
import com.whatsaround.entity.Product;
import com.whatsaround.entity.Professional;
import com.whatsaround.entity.ProfessionalComments;
import com.whatsaround.entity.User;

public class StartupServlet implements ServletContextListener {

	static {
		ObjectifyService.register(User.class);
		ObjectifyService.register(Professional.class);
		ObjectifyService.register(Product.class);
		ObjectifyService.register(Career.class);
		ObjectifyService.register(ProfessionalComments.class);
		ObjectifyService.register(CareerComments.class);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

	}
}
