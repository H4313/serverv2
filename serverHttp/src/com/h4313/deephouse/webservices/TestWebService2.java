package com.h4313.deephouse.webservices;


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

import com.h4313.deephouse.dao.DAO;
import com.h4313.deephouse.dao.RoomDAO;
import com.h4313.deephouse.exceptions.DeepHouseException;
import com.h4313.deephouse.exceptions.DeepHouseFormatException;
import com.h4313.deephouse.exceptions.DeepHouseTypeException;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.housemodel.RoomConstants;
import com.h4313.deephouse.housemodel.RoomFactory;
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

	     
	      

	      DAO<Room> roomDao = new RoomDAO();
	     Room r1= RoomFactory.createInstance(RoomConstants.ID_BEDROOM);
	     Room r2= RoomFactory.createInstance(RoomConstants.ID_BATHROOM);
	     Room r3= RoomFactory.createInstance(RoomConstants.ID_LIVING_ROOM);
	     

	    
	     try {
			r3.addSensor("00", SensorType.LIGHT);
		} catch (DeepHouseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	     roomDao.delete(r3);
	      Room listRoom = null;
		try {
			listRoom = roomDao.find("toto");
		} catch (DeepHouseTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(listRoom !=null){
			pw.write("nb : "+listRoom.getIdRoom()+"  \n");
		}

	     
	     
	     
	    
	    // roomDao.delete(r1);
	     
	    
	      pw.write("Hello world !\n");
	

	
	     
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
