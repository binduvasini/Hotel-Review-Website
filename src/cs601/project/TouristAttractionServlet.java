package cs601.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * 
 * @author vasini
 * 
 *         Class TouristAttractionServlet - handles review information
 * 
 *
 */
@SuppressWarnings("serial")
public class TouristAttractionServlet extends BaseServlet {
	private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();

	/**
	 * Forward to tourist attractions jsp
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		try {
			HttpSession session = request.getSession();
			if (session.getAttribute("user") != null) {
				request.getRequestDispatcher("/touristattractions.jsp").forward(request, response);
			} else {
				response.sendRedirect(response.encodeRedirectURL("/login"));
			}
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the input radius from user. Call the appropriate google api passing
	 * the radius and display tourist attractions
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		URL url;
		PrintWriter out = null;
		BufferedReader in = null;
		SSLSocket socket = null;
		try {
			HttpSession session = request.getSession();
			if (session.getAttribute("user") != null) {
				String rad = StringEscapeUtils.escapeHtml4(request.getParameter("radius"));
				if (rad != null) {
					Integer radiusInMiles = Integer.parseInt(rad);
					Integer radius = (int) (radiusInMiles * 1609.34);
					String hotelid = StringEscapeUtils.escapeHtml4(request.getParameter("hotelid"));
					ArrayList<HashMap<String, Object>> resultAL = dbhandler.getHotelAddress(hotelid);
					Iterator<HashMap<String, Object>> itr = resultAL.iterator();
					if (itr.hasNext()) {
						HashMap<String, Object> resultHM = itr.next();
						StringBuffer sb = new StringBuffer();
						sb.append(resultHM.get("city").toString().replaceAll("\\s", "%20") + "&location=");
						sb.append(String.valueOf((Double) resultHM.get("latitude")) + ",");
						sb.append(String.valueOf((Double) resultHM.get("longitude")) + "&");
						String urlString = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=tourist%20attractions+in+";
						urlString = urlString + sb.toString() + "radius=" + radius
								+ "&key=AIzaSyDTGhcy4N6pvfDX3p38e6GUGd1qnerv314";
						url = new URL(urlString);
						SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

						socket = (SSLSocket) factory.createSocket(url.getHost(), 443);
						out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
						String getRequest = getRequest(url.getHost(), url.getPath() + "?" + url.getQuery());
						out.println(getRequest); // send a request to the server
						out.flush();
						in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String line;
						StringBuffer strbuf = new StringBuffer();
						while ((line = in.readLine()) != null) {
							strbuf.append(line);
						}
						String[] jsonString = strbuf.toString().split("close");
						request.setAttribute("jsonString", jsonString[1]);
						request.getRequestDispatcher("/touristattractions.jsp?radius=" + rad).forward(request,
								response);
					}
				} else {
					request.getRequestDispatcher("/touristattractions.jsp?status=" + Status.RADIO_NOTSELECTED)
							.forward(request, response);
				}
			} else {
				response.sendRedirect(response.encodeRedirectURL("/login"));
			}
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Takes a host and a string containing path/resource/query and creates a
	 * string of the HTTP GET request
	 * 
	 * @param host
	 * @param pathResourceQuery
	 * @return
	 */
	private static String getRequest(String host, String pathResourceQuery) {
		String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator() // GET
		// request
				+ "Host: " + host + System.lineSeparator() // Host header
				// required for
				// HTTP/1.1
				+ "Connection: close" + System.lineSeparator() // make sure the
				// server closes
				// the
				// connection after we fetch one page
				+ System.lineSeparator();
		return request;
	}
}
