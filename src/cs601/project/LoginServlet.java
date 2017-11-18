/**
 * 
 */
package cs601.project;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * 
 * @author vasini
 * 
 *         Class LoginServlet - handles user login
 * 
 *
 */
@SuppressWarnings("serial")
public class LoginServlet extends BaseServlet {
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();

	/**
	 * Forward the request to login jsp
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		try {
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		}

	}

	/**
	 * redirect to home page if login is successful. Show error otherwise
	 * 
	 * @throws ServletException
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String user = request.getParameter("user");
		String password = request.getParameter("pass");
		user = StringEscapeUtils.escapeHtml4(user);
		password = StringEscapeUtils.escapeHtml4(password);

		Status status = dbhandler.userLogin(user, password);

		if (status == Status.OK) {
			HttpSession session = request.getSession(true);
			session.setAttribute("user", user);
			ThreadSafeHotelData data = (ThreadSafeHotelData) getServletContext().getAttribute("object");
			dbhandler.loadHotelAndReview(data);
			Date logintime = dbhandler.getLastLogin(user);
			if (logintime != null) {
				session.setAttribute("logintime", logintime);
			}
			request.getRequestDispatcher("/home.jsp").forward(request, response);
		} else {
			String errorMessage = getStatusMessage(status.name());
			String url = "/login?status=" + errorMessage;
			url = response.encodeRedirectURL(url);
			response.sendRedirect(url);
		}

	}
}
