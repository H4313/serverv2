package com.h4313.deephouse.serverHttp.main;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;



@WebListener
public class TestControleur implements ServletContextListener {

	private static Logger logger = Logger.getLogger("TestControleur");  
	
	
	/** Cette méthode appelée lors du démarrage de l'application*/ 
	@SuppressWarnings("static-access")
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Context Initialized");
	}


	/** Cette méthode appelée lors de l'arret de l'application*/ 
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("L'application vient de s'arreter");
	}
}
