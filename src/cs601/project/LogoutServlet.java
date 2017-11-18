/**
 * 
 */
package cs601.project;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author vasini
 * 
 *         Class LogoutServlet - handles logout
 * 
 *
 */
@SuppressWarnings("serial")
public class LogoutServlet extends BaseServlet {

	/**
	 * Remove the session attributes and invalidate the session. Forward to
	 * logout jsp
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			HttpSession session = request.getSession();
			session.removeAttribute("user");
			session.removeAttribute("logintime");
			session.invalidate();
			request.getRequestDispatcher("/logout.jsp").forward(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		}

	}
}
