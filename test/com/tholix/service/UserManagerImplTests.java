/**
 * 
 */
package com.tholix.service;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.types.TaxEnum;
import com.tholix.utils.DateUtil;

/**
 * @author hitender
 * @when Dec 28, 2012 9:25:26 PM
 * 
 */

public class UserManagerImplTests {
	private final Log log = LogFactory.getLog(getClass());

	private UserAuthenticationManager userAuthenticationManager;
	private UserProfileManager userProfileManager;
	private ReceiptManager receiptManager;
	private ItemManager itemManager;
	private ItemFeatureManager itemFeatureManager;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		File file = new File("/Users/hitender/Documents/workspace-sts-3.1.0.RELEASE/receipt/WebContent/WEB-INF/receipt-servlet.xml");
		log.info(file.isFile() + file.getName());

		// create and configure beans
		// ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
		// "classpath:/Users/hitender/Documents/workspace-sts-3.1.0.RELEASE/receipt/WebContent/WEB-INF/receipt-servlet.xml",
		// "classpath:/Users/hitender/Documents/workspace-sts-3.1.0.RELEASE/receipt/WebContent/WEB-INF/mongo-config.xml"});

		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("classpath*:/receipt-servlet.xml");

		// retrieve configured instance
		userAuthenticationManager = context.getBean("userAuthenticationManager", UserAuthenticationManagerImpl.class);
	}

}
