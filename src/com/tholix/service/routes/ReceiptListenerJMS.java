/**
 * 
 */
package com.tholix.service.routes;

import java.util.Map;

import org.apache.log4j.Logger;

import org.springframework.stereotype.Component;

/**
 * @author hitender 
 * @when Mar 30, 2013 11:46:45 AM
 *
 */
@Component
public class ReceiptListenerJMS {
	private static final Logger log = Logger.getLogger(ReceiptListenerJMS.class);
	
	public void receive(Map<String, Object> message) throws Exception {
		String id = (String) message.get("id");
		String description = (String) message.get("description");
		String level = (String) message.get("level");
		
		log.info("Message received: " + id + ", description: " + description + ", user level: " + level);
	}
}
