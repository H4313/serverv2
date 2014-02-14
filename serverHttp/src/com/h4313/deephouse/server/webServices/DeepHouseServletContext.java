package com.h4313.deephouse.server.webServices;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;



@WebListener
public class DeepHouseServletContext implements ServletContextListener {

	private static Logger logger = Logger.getLogger("TestControleur");  
	
	
	/** Cette m?thode appel?e lors du d?marrage de l'application*/ 
	@SuppressWarnings("static-access")
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Context Initialized");
	}


	/** Cette m?thode appel?e lors de l'arret de l'application*/ 
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("L'application vient de s'arreter");
	}
}
