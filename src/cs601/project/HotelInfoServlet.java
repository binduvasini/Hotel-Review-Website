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
public class HotelInfoServlet extends BaseServlet {
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();

	/**
	 * Load the hotel and review tables and call the post method
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			HttpSession session = request.getSession();
			if (session.getAttribute("user") != null) {
				String hotelId = StringEscapeUtils.escapeHtml4(request.getParameter("hotelId"));
				ArrayList<HashMap<String, Object>> resultAL = dbhandler.getHotelInfo(hotelId);
				request.setAttribute("hotelInfoAL", resultAL);
				session.setAttribute("hotelid", hotelId);
				if(request.getParameter("status")!=null){
					request.setAttribute("status", StringEscapeUtils.escapeHtml4(request.getParameter("status")));
				}
				request.getRequestDispatcher("/hotelInfo.jsp").forward(request, response);
			}
			else{
				response.sendRedirect(response.encodeRedirectURL("/login"));
			}
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	/**
	 * display the hotel list
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String searchoption = StringEscapeUtils.escapeHtml4(request.getParameter("searchoption"));
		if (searchoption.equals("searchbyname")) {
			String hotelname = StringEscapeUtils.escapeHtml4(request.getParameter("hotelname"));
			ArrayList<HashMap<String, Object>> resultAL = dbhandler.getHotelByName(hotelname);
			request.setAttribute("hotelAL", resultAL);
			request.getRequestDispatcher("/hotels.jsp").forward(request, response);
		} else if (searchoption.equals("searchbycity")) {
			String selectedoption = request.getParameter("selectoption");
			if (selectedoption != null) {
				String[] selected = selectedoption.split(",");
				ArrayList<HashMap<String, Object>> resultAL = dbhandler.getHotelByCityState(selected[0], selected[1]);
				request.setAttribute("hotelAL", resultAL);
				request.getRequestDispatcher("/hotels.jsp").forward(request, response);
			}
		} else {
			System.out.println("this form is not selected");
		}
	}
}
