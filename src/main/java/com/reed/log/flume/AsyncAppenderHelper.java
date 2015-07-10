package com.reed.log.flume;

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.Logger;

/*
 * This class helps configure to AsyncAppender from log4j as part of log4j.properties
 * You can inject other appenders to AsyncAppender using the AsyncAppenderHelper
 * This would free up the main program thread to be independent of log4j's logging operation
 * @Author http://www.linkedin.com/in/jobypgeorge
 */

public class AsyncAppenderHelper extends AsyncAppender {

	public AsyncAppenderHelper() {
		super();
	}

	public void setAppenderFromLogger(String name) {
		Logger l = Logger.getLogger(name);

		Enumeration<Appender> e = l.getAllAppenders();

		while (e.hasMoreElements()) {
			Appender a = e.nextElement();
			this.addAppender(a);
			System.out.println("The newAppender " + a.getName()
					+ " attach status " + this.isAttached(a));
		}

	}
}