package com.h4313.deephouse.webservices;

import java.util.Set;
import java.util.HashSet;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;


public class ApplicationRestDeepHouse extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
	public ApplicationRestDeepHouse(){
	     singletons.add(new TestRestService());
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
