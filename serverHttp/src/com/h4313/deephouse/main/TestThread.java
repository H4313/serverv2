package com.h4313.deephouse.main;

import org.jboss.logging.Logger;

public class TestThread extends Thread{
	
	private static Logger logger = Logger.getLogger("MonThread");  
	public void run() {
		    
	
		 long start = System.currentTimeMillis();
		    // boucle tant que la durée de vie du thread est < à 5 secondes
		    while( System.currentTimeMillis() < ( start + (1000 * 30))) {
		      // traitement
		     logger.info("Ligne affichée par le thread");
		      try {
		        // pause
		        sleep(2000);
		      }
		      catch (InterruptedException ex) {}
		    }
		  }    
}
