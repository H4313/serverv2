package com.h4313.deephouse.server.webServices;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.h4313.deephouse.exceptions.DeepHouseException;


public class ApplicationRestDeepHouse extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
	public ApplicationRestDeepHouse() throws DeepHouseException{
	     singletons.add(new DeepHouseServicesImpl());
	}
	@Override
	public Set<Class<?>> getClasses() {
	     return empty;
	}
	@Override
	public Set<Object> getSingletons() {
	     return singletons;
	}
}
