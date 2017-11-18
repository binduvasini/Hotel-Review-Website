package cs601.project;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * 
 * @author vasini
 * 
 *         Class AddReviewServlet - handles add review option for a hotel
 * 
 *
 */
@SuppressWarnings("serial")
public class AddReviewServlet extends BaseServlet {
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();

	/**
	 * For this hotel, call the respective method from database handler. if the
	 * status is success, print a success message. Else print an error message
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		if (session.getAttribute("user") != null) {
			String hotelId = StringEscapeUtils.escapeHtml4(request.getParameter("hotelId"));
			String rat = StringEscapeUtils.escapeHtml4(request.getParameter("rating"));
			int rating = 0;
			Status status = Status.ADD_REVIEW_FAILURE;
			boolean isRecom = Boolean.parseBoolean(StringEscapeUtils.escapeHtml4(request.getParameter("isrecom")));
			String reviewTitle = StringEscapeUtils.escapeHtml4(request.getParameter("reviewtitle"));
			String review = StringEscapeUtils.escapeHtml4(request.getParameter("review"));
			if (rat.isEmpty() || reviewTitle.isEmpty() || review.isEmpty()) {
				status = Status.REVIEW_MANDATORY_FIELDS_UNFILLED;
			} else {

				if (!rat.isEmpty() && rat != null)
					rating = Integer.parseInt(rat);
				status = dbhandler.addReview(hotelId, rating, reviewTitle, review, isRecom,
						StringEscapeUtils.escapeHtml4(request.getParameter("user")));
			}
			out.println(status);
		} else {
			response.sendRedirect(response.encodeRedirectURL("/login"));
		}
	}
}
