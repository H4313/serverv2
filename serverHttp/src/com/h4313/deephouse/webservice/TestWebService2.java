package com.h4313.deephouse.webservice;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.h4313.deephouse.sensor.*;
import com.h4313.deephouse.util.HibernateUtil;


/**
 * Servlet implementation class Main
 */
@WebServlet("/WebService")
public class TestWebService2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger("Main");    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestWebService2() {
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
		  logger.info("toto");   
	      PrintWriter pw = response.getWriter() ;
	      BooleanSensor s =new BooleanSensor("01",SensorType.SWITCH);
	     s.setLastValue(false);
	      s.setId("022");
	      Boolean bool=s.getLastValue();
	      pw.write(bool+"\n");
	      Session session = HibernateUtil.getSession();
	      
	      Transaction transaction = null;
			try {
				transaction = session.beginTransaction();
				session.save(s);
				List<BooleanSensor>  b =session.createQuery("FROM BooleanSensor").list();
				logger.info("la taille de la liste : "+b.size());
				transaction.commit();
			} catch (HibernateException e) {
				transaction.rollback();
				e.printStackTrace();
			} finally {
				session.close();
			}

	      pw.write("Hello world !\n");
	      pw.write(s.getId());

	
	     
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
