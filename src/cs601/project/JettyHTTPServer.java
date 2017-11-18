/**
 * 
 */
package cs601.project;

import java.util.HashMap;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author vasini
 *
 *         Server that uses Jetty
 */
public class JettyHTTPServer {

	private static final int PORT = 2050;
	private static HashMap<String, HTTPHandler> handlerHM = new HashMap<>();
	private static Object object = null;
	private static Server server;

	public JettyHTTPServer() {
		server = new Server(PORT);
	}

	/**
	 * method to get the request from client and pass to appropriate servlets
	 */
	public void startServer() {
		try {
			WebAppContext context = new WebAppContext();
			context.setResourceBase("main/webapp");
			context.setContextPath("/");
			context.setAttribute("object", object);

			context.addServlet(HotelsServlet.class, "/hotels");
			context.addServlet(HotelInfoServlet.class, "/hotelInfo");
			context.addServlet(LoginServlet.class, "/login");
			context.addServlet(LogoutServlet.class, "/logout");
			context.addServlet(RegistrationServlet.class, "/register");
			context.addServlet(ReviewsInfoServlet.class, "/reviews");
			context.addServlet(AddReviewServlet.class, "/addreview");
			context.addServlet(ModifyReviewServlet.class, "/modifyreview");
			context.addServlet(TouristAttractionServlet.class, "/touristattraction");

			context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/[^/]*jstl.*\\.jar$");

			org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList
					.setServerDefault(server);
			classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration",
					"org.eclipse.jetty.plus.webapp.EnvConfiguration",
					"org.eclipse.jetty.plus.webapp.PlusConfiguration");
			classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
					"org.eclipse.jetty.annotations.AnnotationConfiguration");

			server.setHandler(context);

			server.start();
			server.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * method to add to the handler hashmap
	 * 
	 * @param string
	 * @param hotelInfoHandler
	 */
	public void addToHandler(String path, HTTPHandler handler) {
		handlerHM.put(path, handler);
	}

	/**
	 * method to set the resource
	 * 
	 * @param obj
	 */
	public void setResource(Object obj) {
		object = obj;

	}

}
