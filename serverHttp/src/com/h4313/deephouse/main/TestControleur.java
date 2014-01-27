package com.h4313.deephouse.main;

import java.util.logging.Logger;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;



@WebListener
public class TestControleur implements ServletContextListener {

	private static Logger logger = Logger.getLogger("TestControleur");  
	
	
	/** Cette m�thode appel�e lors du d�marrage de l'application*/ 
	@SuppressWarnings("static-access")
	public void contextInitialized(ServletContextEvent sce) {
	
//		logger.info("L'application vient de d�marrer");
//		TestThread monThread = new TestThread();
//		monThread.start();
//		  while( monThread.isAlive() ) {
//		      // faire un traitement...
//		      System.out.println("Ligne affich�e par le main");
//		      try {
//		        // et faire une pause
//		    	  monThread.sleep(800);
//		      }
//		      catch (InterruptedException ex) {}
//		 }
	}


	/** Cette m�thode appel�e lors de l'arret de l'application*/ 
	public void contextDestroyed(ServletContextEvent sce) {
	
		System.out.println("L'application vient de s'arreter");
	}
}
