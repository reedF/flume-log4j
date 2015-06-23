package com.reed.log.flume;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * log
 */
public class WriteLog {

	protected static final Logger logger = LoggerFactory
			.getLogger(WriteLog.class);

	public static void main(String[] args) throws Exception {
		while (true) {
			logger.info(String.valueOf(new Date().getTime()));
			Thread.sleep(2000);
		}
	}

}