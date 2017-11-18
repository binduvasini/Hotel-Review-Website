package cs601.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * @author vasini
 *
 *         Servlet to display hotel information
 * 
 *
 */
@SuppressWarnings("serial")
public class HotelsServlet extends BaseServlet {
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();

	/**
	 * Load the hotel and review tables and call the post method
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {

			HttpSession session = request.getSession();
			
			if(session.getAttribute("hotelid")!=null){
				session.removeAttribute("hotelid");
			}
			if (session.getAttribute("user") != null) {
				
				String viewhotel = StringEscapeUtils.escapeHtml4(request.getParameter("viewhotel"));
				if (viewhotel != null) {
					if (viewhotel.equalsIgnoreCase("saved")) {
						String user = (String) session.getAttribute("user");
						ArrayList<HashMap<String, Object>> resultAL = dbhandler.getSavedHotel(user);
						request.setAttribute("hotelAL", resultAL);
						request.setAttribute("viewhotel", viewhotel);
						request.getRequestDispatcher("/hotels.jsp").forward(request, response);
					} else if (viewhotel.equalsIgnoreCase("all")) {
						ArrayList<HashMap<String, Object>> resultAL = dbhandler.getHotel();
						request.setAttribute("hotelAL", resultAL);
						request.setAttribute("viewhotel", viewhotel);
						request.getRequestDispatcher("/hotels.jsp").forward(request, response);
					}
				} else {
					ArrayList<HashMap<String, Object>> resultAL = dbhandler.getHotel();
					request.setAttribute("hotelAL", resultAL);
					request.getRequestDispatcher("/hotels.jsp").forward(request, response);
				}
			}

			else{
				response.sendRedirect(response.encodeRedirectURL("/login"));
			}
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		HttpSession session = request.getSession();
		if (session.getAttribute("user") != null) {
			String hotelaction = StringEscapeUtils.escapeHtml4(request.getParameter("hotelaction"));
			if (hotelaction != null) {
				if (hotelaction.equalsIgnoreCase("savehotel")) {
					String hotelid = StringEscapeUtils.escapeHtml4(request.getParameter("hotelid"));
					String hotelname = StringEscapeUtils.escapeHtml4(request.getParameter("hotelname"));
					String username = (String) session.getAttribute("user");
					Status status = dbhandler.saveHotel(hotelid, hotelname, username);
					String url = "/hotelInfo?hotelId=" + hotelid + "&status=" + status;
					url = response.encodeRedirectURL(url);
					response.sendRedirect(url);
				} else if (hotelaction.equalsIgnoreCase("clearhotel")) {
					String username = (String) session.getAttribute("user");
					Status status = dbhandler.clearSavedHotel(username);
					String url = "/home.jsp?status=" + status;
					url = response.encodeRedirectURL(url);
					response.sendRedirect(url);
				}
			}
		}
		else{
			response.sendRedirect(response.encodeRedirectURL("/login"));
		}
	}
}
