package com.h4313.deephouse.main;

import org.jboss.logging.Logger;

public class TestThread extends Thread{
	
	private static Logger logger = Logger.getLogger("MonThread");  
	public void run() {
		    
	
		 long start = System.currentTimeMillis();
		    // boucle tant que la dur�e de vie du thread est < � 5 secondes
		    while( System.currentTimeMillis() < ( start + (1000 * 30))) {
		      // traitement
		     logger.info("Ligne affich�e par le thread");
		      try {
		        // pause
		        sleep(2000);
		      }
		      catch (InterruptedException ex) {}
		    }
		  }    
}
