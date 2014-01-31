package com.h4313.deephouse.webservice;


import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




import com.h4313.deephouse.main.TestThread;

/**
 * Servlet implementation class Main
 */
@WebServlet("/Main")
public class TestWebService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger("Main");    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestWebService() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /**
     *
     */
    public void init(ServletConfig config) throws ServletException{
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info("L'application vient de démarrer");
		TestThread monThread = new TestThread();
		monThread.start();
		int i=0;
		  while( i<3 ) {
		      // faire un traitement...
			  i++;
		      System.out.println("Ligne affichée par le main");
		      try {
		        // et faire une pause
		    	 monThread.sleep(2000);
		      }
		      catch (InterruptedException ex) {}
		 }
		
	
	     
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
