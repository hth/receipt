/**
 *
 */
package com.tholix.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tholix.repository.UserAuthenticationManager;
import com.tholix.repository.UserProfileManager;
import com.tholix.web.controller.LoginController;
import com.tholix.web.form.UserLoginForm;
import com.tholix.web.validator.UserLoginValidator;

/**
 * @author hitender
 * @since Dec 27, 2012 6:27:18 PM
 *
 * {@link http://rstoyanchev.github.com/spring-31-and-mvc-test/#100}
 * {@link http://svn.code.sf.net/p/akura/code/trunk/akura/akura-web/test/src/com/virtusa/akura/staff/controller/StaffDetailsControllerTest.java}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/receipt-servlet-test.xml"})
public class LoginControllerTests {

	@Autowired private UserAuthenticationManager userAuthenticationManager;
	@Autowired private UserProfileManager userProfileManager;
	@Autowired private UserLoginValidator userLoginValidator;

	@Autowired private ApplicationContext applicationContext;

	/**
	 * {@link reference - http://stackoverflow.com/questions/2457239/injecting-mockito-mocks-into-a-spring-bean}
	 * {@link http://stackoverflow.com/questions/10906945/mockito-junit-and-spring}
	 * {@link http://www.jayway.com/2011/11/30/spring-integration-tests-part-i-creating-mock-objects/}
	 */
	private Model model;

	private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private RequestMappingHandlerAdapter handlerAdapter;
	private LoginController controller;
	private UserLoginForm userLoginForm;

	@Mock private BindingResult result;
	@Mock private RedirectAttributes redirectAttrs;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		model = new ExtendedModelMap();
		request = new MockHttpServletRequest();
	    response = new MockHttpServletResponse();
	    handlerAdapter = applicationContext.getBean(RequestMappingHandlerAdapter.class);

		//This will inject any mocked objects in the test class, so in this case it will inject mockedObject in testObject. This was mentioned above but here is the code.
		MockitoAnnotations.initMocks(this);

		/**
		 * {@link reference - http://stackoverflow.com/questions/8299607/junit-testing-for-annotated-controller}
		 */
		// While the default boolean return value for a mock is 'false',
	    // it's good to be explicit anyway:
	    Mockito.when(result.hasErrors()).thenReturn(false);

	    /** Populate the Controller */
		controller = new LoginController();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		userAuthenticationManager = null;
		userProfileManager = null;
		userLoginValidator = null;
		applicationContext = null;
		controller = null;
		handlerAdapter = null;
		model = null;
		redirectAttrs = null;
		request = null;
		response = null;
		result = null;
	}

	@Test
    public void shouldAutowireDependencies() {
		assertNotNull(userAuthenticationManager);
		assertNotNull(userProfileManager);
        assertNotNull(userLoginValidator);
        assertNotNull(applicationContext);
    }

	/**
	 * Test method for {@link com.tholix.web.controller.LoginController#getUserLoginForm()}.
	 */
	@Test
	public void testGetUserLoginForm() {
		assertNotNull(controller.getUserLoginForm());
	}

	/**
	 * Test method for {@link com.tholix.web.controller.LoginController#loadForm(org.springframework.ui.Model)}.
	 */
	@Test
	public void testLoadForm() {
		assertNotNull(controller);
		assertEquals("login", controller.loadForm(request));
	}

	/**
	 * Test method for {@link com.tholix.web.controller.LoginController#post(com.tholix.web.form.UserLoginForm, org.springframework.validation.BindingResult, org.springframework.web.servlet.mvc.support.RedirectAttributes)}.
	 * {@link reference http://stackoverflow.com/questions/8299607/junit-testing-for-annotated-controller}
	 */
	@Test
	public void testPost() {
//        controller.setUserAuthenticationManager(userAuthenticationManager);
//        controller.setUserLoginValidator(userLoginValidator);
//        controller.setUserProfileManager(userProfileManager);

        /** Validation failure condition */
        userLoginForm = UserLoginForm.newInstance();
		userLoginForm.setEmailId("test");
		userLoginForm.setPassword("test");
	    result = new BeanPropertyBindingResult(userLoginForm, "userLoginForm");
		assertEquals("login", controller.post(userLoginForm, result, redirectAttrs));

		/** Validation failure condition. Result has to be rest since its corrupted from previous call */
		userLoginForm.setEmailId("test@test.com");
		userLoginForm.setPassword("XXXX");
	    result = new BeanPropertyBindingResult(userLoginForm, "userLoginForm");
		assertEquals("login", controller.post(userLoginForm, result, redirectAttrs));

		/** Validation failure condition. Result has to be rest since its corrupted from previous call */
		userLoginForm.setEmailId("test-me@test.com");
		userLoginForm.setPassword("mine");
	    result = new BeanPropertyBindingResult(userLoginForm, "userLoginForm");
		assertEquals("login", controller.post(userLoginForm, result, redirectAttrs));

		/** Validation success condition. Result has to be rest since its corrupted from previous call */
		userLoginForm.setEmailId("test@test.com");
		userLoginForm.setPassword("mine");
	    result = new BeanPropertyBindingResult(userLoginForm, "userLoginForm");
		assertEquals("redirect:/landing.htm", controller.post(userLoginForm, result, redirectAttrs));

//		request.setMethod("POST");
//      request.setRequestURI("/users");
//      request.setParameter("email", "bla@gmail.com");
//      request.setParameter("prename", "Cyril");
//      request.setParameter("surname", "bla");
//      request.setParameter("password", "123");
//      request.setParameter("repeat", "123");
//      request.setParameter("birthdate", "2000-01-01");
//      request.setParameter("city", "Baden");


	}

	 /**
     * @link - http://www.finalconcept.com.au/article/view/spring-unit-testing-controllers
     *
     * This method finds the handler for a given request URI.
     *
     * It will also ensure that the URI Parameters i.e. /context/test/{name} are added to the request
     *
     * @param request
     * @return
     * @throws Exception
     */
    private Object getHandler(MockHttpServletRequest request) throws Exception {
        HandlerExecutionChain chain = null;

        Map<String, HandlerMapping> map = applicationContext.getBeansOfType(HandlerMapping.class);
        Iterator<HandlerMapping> itt = map.values().iterator();

        while (itt.hasNext()) {
            HandlerMapping mapping = itt.next();
            chain = mapping.getHandler(request);
            if (chain != null) {
                break;
            }

        }

        if (chain == null) {
            throw new InvalidParameterException("Unable to find handler for request URI: " + request.getRequestURI());
        }

        return chain.getHandler();
    }
}
