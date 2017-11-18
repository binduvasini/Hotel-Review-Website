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
 *         Class ModifyReviewServlet - handles modify review and delete review
 *         options for a review
 * 
 *
 */
@SuppressWarnings("serial")
public class ModifyReviewServlet extends BaseServlet {
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();

	/**
	 * If delete review is selected, call the appropriate method from db handler
	 * and redirect to review page
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		if (session.getAttribute("user") != null) {
			String reviewId = StringEscapeUtils.escapeHtml4(request.getParameter("deletereviewId"));
			String hotelName = StringEscapeUtils.escapeHtml4(request.getParameter("hotelName"));
			Status status = Status.ERROR;
			status = dbhandler.deleteReview(reviewId);

			if (status == Status.OK) {
				response.sendRedirect("/reviews?hotelname=" + hotelName);
			}

			else {
				out.println("not done");
			}
		} else {
			response.sendRedirect(response.encodeRedirectURL("/login"));
		}
	}

	/**
	 * If modify review is clicked, call the appropriate db handler method and
	 * redirect to review jsp
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		if (session.getAttribute("user") != null) {
			String reviewId = StringEscapeUtils.escapeHtml4(request.getParameter("reviewId"));
			String hotelName = StringEscapeUtils.escapeHtml4(request.getParameter("hotelName"));
			boolean isRecom = Boolean.parseBoolean(StringEscapeUtils.escapeHtml4(request.getParameter("isrecom")));
			String reviewTitle = StringEscapeUtils.escapeHtml4(request.getParameter("reviewtitle"));
			String review = StringEscapeUtils.escapeHtml4(request.getParameter("review"));
			String rat = StringEscapeUtils.escapeHtml4(request.getParameter("rating"));

			if (rat.isEmpty() || reviewTitle.isEmpty() || review.isEmpty()) {
				response.sendRedirect("modifyreview.jsp?reviewId=" + reviewId + "&hotelName=" + hotelName + "&status="
						+ Status.REVIEW_MANDATORY_FIELDS_UNFILLED);
			} else {
				int rating = 0;
				if (!rat.isEmpty() && rat != null)
					rating = Integer.parseInt(rat);
				Status status = Status.ERROR;
				status = dbhandler.modifyReview(reviewId, rating, reviewTitle, review, isRecom);

				if (status == Status.OK) {
					response.sendRedirect("/reviews?hotelname=" + hotelName);
				}

				else {
					out.println("not done");
				}
			}
		} else {
			response.sendRedirect(response.encodeRedirectURL("/login"));
		}
	}
}
