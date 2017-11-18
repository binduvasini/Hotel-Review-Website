package cs601.project;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import cs601.concurrent.WorkQueue;

/**
 * @author vasini
 * 
 *
 *         Class HotelDataBuilder - to allow concurrent threads to parse the
 *         given json files and merge them at the end
 */
public class HotelDataBuilder {
	private ThreadSafeHotelData globaldata;
	private WorkQueue q = new WorkQueue();
	private static final Logger logger = LogManager.getLogger();
	private volatile int numTasks; // how many runnable tasks are pending

	public HotelDataBuilder(ThreadSafeHotelData data, WorkQueue q) {
		this.globaldata = data;
		this.q = q;
	}

	public HotelDataBuilder(ThreadSafeHotelData data) {
		this.globaldata = data;
	}

	/**
	 * Read the json file with information about the hotels (id, name, address,
	 * etc) and load it into the appropriate data structure(s). Note: This
	 * method does not load reviews
	 * 
	 * @param filename
	 *            the name of the json file that contains information about the
	 *            hotels
	 */
	@SuppressWarnings("unchecked")
	public void loadHotelInfo(String jsonFilename) {

		// Hint: Use JSONParser from JSONSimple library
		// FILL IN CODE
		JSONParser parser = new JSONParser();
		try {
			JSONObject hotels = (JSONObject) parser.parse(new FileReader(jsonFilename));
			JSONArray sr = (JSONArray) hotels.get("sr");
			Iterator<JSONObject> hotelsJsonItr = sr.iterator();
			while (hotelsJsonItr.hasNext()) {
				JSONObject hotelDetails = hotelsJsonItr.next();
				JSONObject latLong = (JSONObject) hotelDetails.get("ll");
				globaldata.addHotel(hotelDetails.get("id").toString(), hotelDetails.get("f").toString(),
						hotelDetails.get("ci").toString(), (String) hotelDetails.get("pr"),
						hotelDetails.get("ad").toString(), Double.parseDouble(latLong.get("lat").toString()),
						Double.parseDouble(latLong.get("lng").toString()), hotelDetails.get("c").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load reviews for all the hotels into the appropriate data structure(s).
	 * Traverse a given directory recursively to find all the json files with
	 * reviews and load reviews from each json. Note: this method must be
	 * recursive and use DirectoryStream as discussed in class.
	 * 
	 * @param path
	 *            the path to the directory that contains json files with
	 *            reviews Note that the directory can contain json files, as
	 *            well as subfolders (of subfolders etc..) with more json files
	 * @throws IOException
	 */
	public void loadReviews(Path path) {
		try {
			DirectoryStream<Path> filesList = Files.newDirectoryStream(path);
			String filename = "";
			for (Path file : filesList) {
				if (Files.isDirectory(file))
					loadReviews(file);
				else {
					filename = file.toString();
					if (filename.lastIndexOf('.') > 0
							&& filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase("json")) {
						q.execute(new LoadReview(file));
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parse a single json file and read its content
	 */
	@SuppressWarnings("unchecked")
	public void readReviewJsonFile(Path file) {
		ThreadSafeHotelData localdata = new ThreadSafeHotelData();
		JSONParser parser = new JSONParser();

		try {
			JSONObject reviewJsonObj = (JSONObject) parser.parse(new FileReader(file.toString()));
			JSONObject reviewDetails = (JSONObject) reviewJsonObj.get("reviewDetails");
			
			JSONObject reviewCollection = (JSONObject) reviewDetails.get("reviewCollection");
			JSONArray review = (JSONArray) reviewCollection.get("review");
			Iterator<JSONObject> reviewItr = review.iterator();
			while (reviewItr.hasNext()) {
				JSONObject reviewDetailsJsonObj = reviewItr.next();
				String username = reviewDetailsJsonObj.get("userNickname").toString();
				if ("".equals(username) || username == null)
					username = "anonymous";
				
				localdata.addReview(reviewDetailsJsonObj.get("hotelId").toString(),
						reviewDetailsJsonObj.get("reviewId").toString(),
						Integer.parseInt(reviewDetailsJsonObj.get("ratingOverall").toString()),
						reviewDetailsJsonObj.get("title").toString(), reviewDetailsJsonObj.get("reviewText").toString(),
						Boolean.parseBoolean(reviewDetailsJsonObj.get("isRecommended").toString()),
						reviewDetailsJsonObj.get("reviewSubmissionTime").toString(), username);
			}
			globaldata.mergeData(localdata);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Call the printToFile method of ThreadSafeHotelData clas
	 * 
	 * @param file
	 */
	public void printToFile(Path file) {
		waitUntilFinished();
		globaldata.printToFile(file);
	}

	/**
	 * Increment the number of tasks
	 */
	public synchronized void incrementTasks() {
		numTasks++;
	}

	/**
	 * Decrement the number of tasks. Call notifyAll() if no pending work left.
	 */
	public synchronized void decrementTasks() {
		numTasks--;
		if (numTasks <= 0)
			notifyAll();
	}

	/**
	 * Wait until there is no pending work, then shutdown the queue
	 */
	public synchronized void shutdown() {
		waitUntilFinished();
		q.shutdown();
		q.awaitTermination();
	}

	/**
	 * Wait for all pending work to finish
	 */
	public synchronized void waitUntilFinished() {
		while (numTasks > 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				logger.warn("Got interrupted while waiting for pending work to finish, ", e);
			}
		}
	}

	/**
	 * Inner class - LoadReview In the run() method, it iterates over all
	 * subdirectories. when it reaches the json file, it reads and stores in a
	 * local copy of the treemap.
	 * 
	 *
	 */
	public class LoadReview implements Runnable {
		private Path path;

		public LoadReview(Path path) {
			this.path = path;
			incrementTasks();
		}

		@Override
		public void run() {
			try {
				readReviewJsonFile(path);
			} catch (Exception e) {
				logger.warn("Exception occurred : " + e.getMessage());
				logger.catching(Level.DEBUG, e);
			} finally {
				decrementTasks(); // done with this task
			}

		}
	}
}
