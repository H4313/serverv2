package com.h4313.deephouse.main;

import java.util.logging.Logger;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;



@WebListener
public class TestControleur implements ServletContextListener {

	private static Logger logger = Logger.getLogger("TestControleur");  
	
	
	/** Cette méthode appelée lors du démarrage de l'application*/ 
	@SuppressWarnings("static-access")
	public void contextInitialized(ServletContextEvent sce) {
	
//		logger.info("L'application vient de démarrer");
//		TestThread monThread = new TestThread();
//		monThread.start();
//		  while( monThread.isAlive() ) {
//		      // faire un traitement...
//		      System.out.println("Ligne affichée par le main");
//		      try {
//		        // et faire une pause
//		    	  monThread.sleep(800);
//		      }
//		      catch (InterruptedException ex) {}
//		 }
	}


	/** Cette méthode appelée lors de l'arret de l'application*/ 
	public void contextDestroyed(ServletContextEvent sce) {
	
		System.out.println("L'application vient de s'arreter");
	}
}
