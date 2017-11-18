package cs601.project;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * A servlet that handles user registration. doGet() method displays an HTML
 * form with a button and two textfields: one for the username, one for the
 * password. doPost() processes the form: if the username is not taken, it adds
 * user info to the database.
 *
 */
@SuppressWarnings("serial")
public class RegistrationServlet extends BaseServlet {

	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();

	/**
	 * Forward to register jsp
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		try {
			request.getRequestDispatcher("/register.jsp").forward(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	/**
	 * If status is OK, redirect to success page. Else redirect to same page
	 * with appropriate error message
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			// Get data from the textfields of the html form
			String newuser = request.getParameter("user");
			String newpass = request.getParameter("pass");
			// sanitize user input to avoid XSS attacks:
			newuser = StringEscapeUtils.escapeHtml4(newuser);
			newpass = StringEscapeUtils.escapeHtml4(newpass);

			// add user's info to the database
			Status status = dbhandler.registerUser(newuser, newpass);

			if (status == Status.OK) { // registration was successful
				request.getRequestDispatcher("/registersuccess.jsp").forward(request, response);
			} else { // if something went wrong
				String errorMessage = getStatusMessage(status.name());
				String url = "/register?status=" + errorMessage;
				url = response.encodeRedirectURL(url);
				response.sendRedirect(url);
			}
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

}