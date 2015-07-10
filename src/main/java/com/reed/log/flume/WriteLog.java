package com.reed.log.flume;

import java.util.Date;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * log
 */
public class WriteLog {

	protected static final Logger logger = LoggerFactory
			.getLogger(WriteLog.class);

	public static void echo() {
		System.out.println(new Date() + "============");
	}

	public static void main(String[] args) {
		// using xml/properties to setting log4j
		//PropertyConfigurator.configure("target/classes/log4j.properties");
		DOMConfigurator.configure("target/test-classes/log4j.xml");
		while (true) {
			logger.info(String.valueOf(new Date().getTime()));
			// echo();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}