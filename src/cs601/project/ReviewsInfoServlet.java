/**
 * 
 */
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
 * 
 * @author vasini
 * 
 *         Class ReviewInfoServlet - handles review information
 * 
 *
 */
@SuppressWarnings("serial")
public class ReviewsInfoServlet extends BaseServlet {

	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();

	/**
	 * Get the review information from table and send it to review jsp. If user
	 * has selected to sort the review, call the appropriate sql query and send
	 * the information to review jsp
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session.getAttribute("user") != null) {
			String hotelId = (String) session.getAttribute("hotelid");
			String hotelname = StringEscapeUtils.escapeHtml4(request.getParameter("hotelname"));
			int page = 1;
			if (request.getParameter("page") != null) {
				page = Integer.parseInt(request.getParameter("page"));
			}
			String sortBy = "";
			if (request.getParameter("sortby") != null) {
				sortBy = StringEscapeUtils.escapeHtml4(request.getParameter("sortby"));
			}
			ArrayList<HashMap<String, Object>> resultAL = dbhandler.getReview(hotelId, (page - 1) * 5, sortBy);
			int noOfRecords = dbhandler.getNoOfRecords();
			int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / 5);
			request.setAttribute("reviewAL", resultAL);
			request.setAttribute("hotelId", hotelId);
			request.setAttribute("hotelname", hotelname);
			request.setAttribute("noOfPages", noOfPages);
			request.setAttribute("currentPage", page);
			request.setAttribute("sortBy", sortBy);
			request.getRequestDispatcher("/review.jsp").forward(request, response);
		} else {
			response.sendRedirect(response.encodeRedirectURL("/login"));
		}
	}

}
