package cs601.project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import cs601.concurrent.ReentrantReadWriteLock;

/**
 * Class ThreadSafeHotelData - a data structure that stores information about
 * hotels and hotel reviews. Allows to quickly lookup a hotel given the hotel
 * id. Allows to easily find hotel reviews for a given hotel, given the hotelID.
 * Reviews for a given hotel id are sorted by the date and user nickname.
 *
 */
public class ThreadSafeHotelData {

	// FILL IN CODE - declare data structures to store hotel data
	private TreeMap<String, Hotel> hotelTM;
	private TreeMap<String, TreeSet<Review>> reviewTM;
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	/**
	 * Default constructor.
	 */
	public ThreadSafeHotelData() {
		// Initialize all data structures
		// FILL IN CODE
		hotelTM = new TreeMap<>();
		reviewTM = new TreeMap<>();
	}

	/**
	 * Create a Hotel given the parameters, and add it to the appropriate data
	 * structure(s).
	 * 
	 * @param hotelId
	 *            - the id of the hotel
	 * @param hotelName
	 *            - the name of the hotel
	 * @param city
	 *            - the city where the hotel is located
	 * @param state
	 *            - the state where the hotel is located.
	 * @param streetAddress
	 *            - the building number and the street
	 * @param latitude
	 * @param longitude
	 */
	public void addHotel(String hotelId, String hotelName, String city, String state, String streetAddress, double lat,
			double lon, String country) {
		// FILL IN CODE
		lock.lockWrite();
		try {
			hotelTM.put(hotelId, new Hotel(hotelId, hotelName, new Address(streetAddress, city, state, lon, lat, country)));
		} finally {
			lock.unlockWrite();
		}
	}

	/**
	 * Add a new review.
	 * 
	 * @param hotelId
	 *            - the id of the hotel reviewed
	 * @param reviewId
	 *            - the id of the review
	 * @param rating
	 *            - integer rating 1-5.
	 * @param reviewTitle
	 *            - the title of the review
	 * @param review
	 *            - text of the review
	 * @param isRecommended
	 *            - whether the user recommends it or not
	 * @param date
	 *            - date of the review in the format yyyy-MM-dd, e.g.
	 *            2016-08-29.
	 * @param username
	 *            - the nickname of the user writing the review.
	 * @return true if successful, false if unsuccessful because of invalid date
	 *         or rating. Needs to catch and handle ParseException if the date
	 *         is invalid. Needs to check whether the rating is in the correct
	 *         range
	 */
	public boolean addReview(String hotelId, String reviewId, int rating, String reviewTitle, String review,
			boolean isRecom, String date, String username) {
		boolean flag = false;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date reviewdate = format.parse(date);
			if (rating >= 1 && rating <= 5) {
				flag = true;
				lock.lockWrite();
				try {
					if (reviewTM.containsKey(hotelId)) {
						TreeSet<Review> ts = reviewTM.get(hotelId);
						ts.add(new Review(hotelId, reviewId, rating, reviewTitle, review, isRecom, reviewdate,
								username));
						reviewTM.put(hotelId, ts);
					} else {
						TreeSet<Review> ts = new TreeSet<>();
						ts.add(new Review(hotelId, reviewId, rating, reviewTitle, review, isRecom, reviewdate,
								username));
						reviewTM.put(hotelId, ts);
					}
				} finally {
					lock.unlockWrite();
				}
			}
		} catch (ParseException pe) {
			System.out.println("Invalid date entered. ParseException occurred : " + pe.getMessage());
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return flag;
	}

	/**
	 * Return an alphabetized list of the ids of all hotels
	 * 
	 * @return
	 */
	public List<String> getHotels() {
		// FILL IN CODE
		lock.lockRead();
		List<String> hotelIds = new ArrayList<>();
		try {
			for (String hotelId : hotelTM.keySet()) {
				hotelIds.add(hotelId);
			}
		} finally {
			lock.unlockRead();
		}
		return hotelIds;
	}

	/**
	 * Returns a string representing information about the hotel with the given
	 * id, including all the reviews for this hotel separated by
	 * -------------------- Format of the string: HoteName: hotelId
	 * streetAddress city, state -------------------- Review by username: rating
	 * ReviewTitle ReviewText -------------------- Review by username: rating
	 * ReviewTitle ReviewText ...
	 * 
	 * @param hotel
	 *            id
	 * @return - output string.
	 */
	public String toString(String hotelId) {

		// FILL IN CODE
		lock.lockRead();
		StringBuffer sb = new StringBuffer();
		try {
			if (hotelTM.get(hotelId) != null) {
				sb.append(hotelTM.get(hotelId).getHotelName() + ": " + hotelTM.get(hotelId).getHotelId() + "\n"
						+ hotelTM.get(hotelId).getAddr().getStreetAddress() + "\n");
				sb.append(hotelTM.get(hotelId).getAddr().getCity() + ", " + hotelTM.get(hotelId).getAddr().getState()
						+ "\n");
				if (reviewTM.size() > 0 && reviewTM.get(hotelId) != null) {
					Iterator<Review> itr = reviewTM.get(hotelId).iterator();
					while (itr.hasNext()) {
						Review rev = (Review) itr.next();
						sb.append("--------------------\nReview by " + rev.getUsername() + ": " + rev.getRating() + "\n"
								+ rev.getReviewTitle() + "\n" + rev.getReview() + "\n");
					}
				}
			}
		} finally {
			lock.unlockRead();
		}
		return sb.toString();
	}

	/**
	 * Save the string representation of the hotel data to the file specified by
	 * filename in the following format: an empty line A line of 20 asterisks
	 * ******************** on the next line information for each hotel, printed
	 * in the format described in the toString method of this class.
	 * 
	 * @param filename
	 *            - Path specifying where to save the output.
	 */
	public void printToFile(Path filename) {
		// FILL IN CODE
		List<String> hotelIds = getHotels();
		try (PrintWriter writer = new PrintWriter(filename.toString())) {
			for (int i = 0; i < hotelIds.size(); i++) {
				writer.write("\n********************\n");
				writer.write(toString(hotelIds.get(i)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Merge the result of local thread with the global (big) main thread
	 * 
	 * @param localdata
	 */
	public void mergeData(ThreadSafeHotelData localdata) {
		lock.lockWrite();
		try {
			for (String keyHotelId : localdata.reviewTM.keySet()) {
				this.reviewTM.put(keyHotelId, localdata.reviewTM.get(keyHotelId));
			}
		} finally {
			lock.unlockWrite();
		}
	}

	public Hotel getHotelInfo(String hotelId){

		return hotelTM.get(hotelId);
	}

	public TreeSet<Review> getReviewInfo(String hotelId){
		return reviewTM.get(hotelId);

	}
}