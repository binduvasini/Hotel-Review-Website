package cs601.project;

import java.io.File;
import java.nio.file.Paths;

/**
 * @author vasini
 *
 *         Driver class has the main method to start the server
 * 
 */
public class ProjectDriver {

	public static void main(String args[]) {
		ThreadSafeHotelData data = new ThreadSafeHotelData();
		HotelDataBuilder dataBuilder = new HotelDataBuilder(data);
		dataBuilder.loadHotelInfo(TestUtils.INPUT_DIR + File.separator + "hotels200.json");
		dataBuilder.loadReviews(Paths.get(TestUtils.INPUT_DIR + File.separator + "reviews"));

		Thread jettyThread = new Thread() {
			public void run() {
				JettyHTTPServer jettyServer = new JettyHTTPServer();
				jettyServer.setResource(data);
				jettyServer.startServer();
			}
		};
		try {
			jettyThread.start();
			jettyThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
