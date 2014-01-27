package com.h4313.deephouse.util;

import org.hibernate.*;
import org.hibernate.cfg.*;

import com.h4313.deephouse.model.*;

public class HibernateUtil {
	private static final SessionFactory sessionFactory;
	
	static {
		try {
			sessionFactory = new AnnotationConfiguration()
			.addPackage("com.h4313.deephouse.model") // le nom complet du package
			.addAnnotatedClass(BooleanSensor1.class)
			.buildSessionFactory();
		} catch (Throwable ex) {
			// Log exception!
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static Session getSession() throws HibernateException {
		return sessionFactory.openSession();
	}
}