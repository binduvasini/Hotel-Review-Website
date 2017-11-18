package cs601.project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles all database-related actions. Uses singleton design pattern.
 * 
 */
public class DatabaseHandler {

	/** Makes sure only one database handler is instantiated. */
	private static DatabaseHandler singleton = new DatabaseHandler();

	/** Used to determine if users table exists. */
	private static final String CHECK_TABLES_SQL = "SHOW TABLES LIKE ?;";

	/** Used to create users table for this example. */
	private static final String CREATE_USERS_SQL = "CREATE TABLE users (userid INTEGER AUTO_INCREMENT PRIMARY KEY, username VARCHAR(32) NOT NULL UNIQUE, password CHAR(64) NOT NULL, usersalt CHAR(32) NOT NULL);";
	private static final String CREATE_USER_HISTORY_SQL = "CREATE TABLE user_history (username VARCHAR(32) NOT NULL, logintime DATETIME, FOREIGN KEY(username) REFERENCES users(username));";
	private static final String CREATE_HOTEL_SQL = "CREATE TABLE hotel (hotelid VARCHAR(32) PRIMARY KEY, hotelname VARCHAR(64) NOT NULL, streetaddress VARCHAR(64) NOT NULL, city VARCHAR(64) NOT NULL, state VARCHAR(64), longitude DOUBLE PRECISION, latitude DOUBLE PRECISION, country VARCHAR(64));";
	private static final String CREATE_SAVED_HOTEL_SQL = "CREATE TABLE saved_hotel (hotelid VARCHAR(32) UNIQUE, hotelname VARCHAR(64) NOT NULL, username VARCHAR(32) NOT NULL, FOREIGN KEY(hotelid) REFERENCES hotel(hotelid), FOREIGN KEY(username) REFERENCES users(username));";
	private static final String CREATE_REVIEW_SQL = "CREATE TABLE review (reviewid VARCHAR(40) PRIMARY KEY, hotelid VARCHAR(32) NOT NULL, rating INTEGER, reviewtitle VARCHAR(80), review VARCHAR(1000) CHARACTER SET utf8mb4, isrecom BOOLEAN, date DATE, username VARCHAR(32), userid INTEGER NOT NULL, FOREIGN KEY(hotelid) REFERENCES hotel(hotelid));";

	/** insert queries */
	private static final String REGISTER_SQL = "INSERT INTO users (username, password, usersalt) VALUES (?, ?, ?);";
	private static final String LOAD_HOTEL_SQL = "INSERT INTO hotel (hotelid, hotelname, streetaddress, city, state, longitude, latitude, country) VALUES(?,?,?,?,?,?,?,?);";
	private static final String LOAD_REVIEW_SQL = "INSERT INTO review (reviewid, hotelid, rating, reviewtitle, review, isrecom, date, username, userid) VALUES(?,?,?,?,?,?,?,?,?);";
	private static final String LOAD_USERS_SQL = "INSERT INTO users (username, password, usersalt, userid) SELECT DISTINCT(username), ? as password, ? as usersalt, userid FROM review GROUP BY username;";
	private static final String INSERT_LAST_LOGIN_SQL = "INSERT INTO user_history (username, logintime) VALUES(?,?);";
	private static final String SAVE_HOTEL_SQL = "INSERT INTO saved_hotel (hotelid, hotelname, username) VALUES(?,?,?);";

	/** select queries */
	private static final String AUTH_SQL = "SELECT username FROM users WHERE username = ? AND password = ?;";
	private static final String SALT_SQL = "SELECT usersalt FROM users WHERE username = ?;";
	private static final String CHECK_HOTELDATA_EXISTS_SQL = "SELECT COUNT(*) FROM hotel;";
	private static final String CHECK_REVIEWDATA_EXISTS_SQL = "SELECT COUNT(*) FROM review;";
	private static final String CHECK_REVIEW_EXISTS__SQL = "SELECT COUNT(*) FROM review WHERE username = ? and hotelid = ?;";
	private static final String CHECK_USERID_EXISTS_SQL = "SELECT COUNT(userid) FROM users WHERE userid in (SELECT userid FROM review);";
	private static final String CHECK_USER_SQL = "SELECT username FROM users WHERE username = ?;";
	private static final String GET_MAX_USERID_SQL = "SELECT max(userid) FROM users;";
	private static final String GET_USERID_SQL = "SELECT userid FROM users WHERE username = ?;";
	private static final String GET_LAST_LOGIN_SQL = "SELECT logintime FROM user_history WHERE username = ?;";
	private static final String GET_CITY_STATE_SQL = "SELECT CONCAT(city,',',state) as citystate FROM hotel WHERE state IS NOT NULL AND city in (SELECT DISTINCT city FROM hotel) GROUP BY city;";
	private static final String GET_HOTELS_SQL = "SELECT hotelid, hotelname FROM hotel;";
	private static final String GET_SAVED_HOTELS_SQL = "SELECT hotelid, hotelname FROM saved_hotel where username = ?;";
	private static final String GET_HOTEL_INFO_SQL = "SELECT hotelname, latitude, longitude, CONCAT(streetaddress,',<br>',city,',<br>',state,',<br>',country) as address, (SELECT AVG(rating) FROM review WHERE hotel.hotelid = review.hotelid) as rating FROM hotel WHERE hotelid = ?;";
	private static final String GET_HOTEL_BY_NAME_SQL = "SELECT hotelid, hotelname FROM hotel WHERE hotelname like ?;";
	private static final String GET_HOTEL_BY_CITY_STATE_SQL = "SELECT hotelid, hotelname FROM hotel WHERE city = ? and state = ?;";
	private static final String CHECK_SAVED_HOTEL_SQL = "SELECT COUNT(*) FROM saved_hotel WHERE username = ? and hotelid = ?;";
	private static final String GET_REVIEW_SQL = "SELECT SQL_CALC_FOUND_ROWS reviewid, username, isrecom, reviewtitle, review, rating, date FROM review WHERE hotelid = ? LIMIT ?, 5;";
	private static final String GET_REVIEW_SORT_DATE_SQL = "SELECT SQL_CALC_FOUND_ROWS reviewid, username, isrecom, reviewtitle, review, rating, date FROM review WHERE hotelid = ? ORDER BY date DESC LIMIT ?, 5;";
	private static final String GET_REVIEW_SORT_RATING_SQL = "SELECT SQL_CALC_FOUND_ROWS reviewid, username, isrecom, reviewtitle, review, rating, date FROM review WHERE hotelid = ? ORDER BY rating DESC LIMIT ?, 5;";
	private static final String GET_HOTEL_ADDRESS_SQL = "SELECT city, latitude, longitude FROM hotel WHERE hotelid = ?;";

	/** update queries */
	private static final String UPDATE_LAST_LOGIN_SQL = "UPDATE user_history set logintime = ? WHERE username = ?";
	private static final String UPDATE_REVIEW_SQL = "UPDATE review set rating = ?, reviewtitle = ?, review = ?, isrecom = ?, date = ? WHERE reviewid = ?";

	/** delete queries */
	private static final String DELETE_SAVED_HOTEL_SQL = "DELETE FROM saved_hotel WHERE username = ?";
	private static final String DELETE_REVIEW_SQL = "DELETE FROM review WHERE reviewid = ?";

	private TreeMap<String, String> tablenames;
	/** Used to configure connection to database. */
	private DatabaseConnector db;

	/** Used to generate password hash salt for user. */
	private Random random;
	private Pattern pattern = Pattern.compile("(?=.*[A-Z]+.*)(?=.*[a-z]+.*)(?=.*[0-9]+.*)([\\w]{8,20})"); // (?=.*[A-Z]+.*)(?=.*[a-z]+.*)(?=.*[0-9]+.*)(?=.*[@#]+.*)([a-zA-Z0-9@#]{8,15})

	/** to store the page numbers for pagination */
	private int noOfRecords;

	/**
	 * This class is a singleton, so the constructor is private. Other classes
	 * need to call getInstance()
	 */
	private DatabaseHandler() {
		random = new Random(System.currentTimeMillis());

		try {
			tablenames = new TreeMap<>();
			tablenames.put("1:users", CREATE_USERS_SQL);
			tablenames.put("2:user_history", CREATE_USER_HISTORY_SQL);
			tablenames.put("3:hotel", CREATE_HOTEL_SQL);
			tablenames.put("4:saved_hotel", CREATE_SAVED_HOTEL_SQL);
			tablenames.put("5:review", CREATE_REVIEW_SQL);
			db = new DatabaseConnector("database.properties");
			if (db.testConnection())
				setupTables(tablenames);

		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException : " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException : " + e.getMessage());
		}

	}

	/**
	 * Gets the single instance of the database handler.
	 *
	 * @return instance of the database handler
	 */
	public static DatabaseHandler getInstance() {
		return singleton;
	}

	/**
	 * Checks to see if a String is null or empty.
	 * 
	 * @param text
	 *            - String to check
	 * @return true if non-null and non-empty
	 */
	public static boolean isBlank(String text) {
		return (text == null) || text.trim().isEmpty();
	}

	/**
	 * Get the list of tables from TreeMap and create them in database in the
	 * mentioned order
	 * 
	 * @param tablenames
	 */
	private void setupTables(TreeMap<String, String> tablenames) {

		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection.prepareStatement(CHECK_TABLES_SQL);) {
			for (String table : tablenames.keySet()) {
				String[] tablevalue = table.split(":");

				statement.setString(1, tablevalue[1]);

				if (!statement.executeQuery().next()) {
					statement.executeUpdate(tablenames.get(table));
				}

			}
		} catch (SQLException e) {
			System.out.println("SQL Exception in setupTables : " + e.getMessage());
		}

	}

	/**
	 * Tests if a user already exists in the database. Requires an active
	 * database connection.
	 *
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - username to check
	 * @return Status.OK if user does not exist in database
	 * @throws SQLException
	 */
	private Status duplicateUser(Connection connection, String user) {

		assert connection != null;
		assert user != null;

		Status status = Status.ERROR;

		try (PreparedStatement statement = connection.prepareStatement(CHECK_USER_SQL);) {
			statement.setString(1, user);

			ResultSet results = statement.executeQuery();
			status = results.next() ? Status.DUPLICATE_USER : Status.OK;
		} catch (SQLException e) {
			status = Status.SQL_EXCEPTION;
			System.out.println("Exception occured while processing SQL statement:" + e);
		}

		return status;
	}

	/**
	 * Returns the hex encoding of a byte array.
	 *
	 * @param bytes
	 *            - byte array to encode
	 * @param length
	 *            - desired length of encoding
	 * @return hex encoded byte array
	 */
	public static String encodeHex(byte[] bytes, int length) {
		BigInteger bigint = new BigInteger(1, bytes);
		String hex = String.format("%0" + length + "X", bigint);

		assert hex.length() == length;
		return hex;
	}

	/**
	 * Calculates the hash of a password and salt using SHA-256.
	 *
	 * @param password
	 *            - password to hash
	 * @param salt
	 *            - salt associated with user
	 * @return hashed password
	 */
	public static String getHash(String password, String salt) {
		String salted = salt + password;
		String hashed = salted;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salted.getBytes());
			hashed = encodeHex(md.digest(), 64);
		} catch (Exception ex) {
			System.out.println("Unable to properly hash password." + ex);
		}

		return hashed;
	}

	/**
	 * Registers a new user, placing the username, password hash, and salt into
	 * the database if the username does not already exist.
	 *
	 * @param newuser
	 *            - username of new user
	 * @param newpass
	 *            - password of new user
	 * @return {@link Status.OK} if registration successful
	 */
	public Status registerUser(String newuser, String newpass) {
		Status status = Status.ERROR;

		// make sure we have non-null and non-emtpy values for login
		if (isBlank(newuser) || isBlank(newpass)) {
			status = Status.INVALID_LOGIN;
			return status;
		}

		Matcher matcher = pattern.matcher(newpass);
		if (matcher.matches()) {
			// try to connect to database and test for duplicate user
			try (Connection connection = db.getConnection();) {
				status = duplicateUser(connection, newuser);

				// if okay so far, try to insert new user
				if (status == Status.OK) {
					// generate salt
					byte[] saltBytes = new byte[16];
					random.nextBytes(saltBytes);

					String usersalt = encodeHex(saltBytes, 32); // hash salt
					String passhash = getHash(newpass, usersalt); // combine
					// password and
					// salt and hash
					// again

					// add user info to the database table
					try (PreparedStatement statement = connection.prepareStatement(REGISTER_SQL);) {
						statement.setString(1, newuser);
						statement.setString(2, passhash);
						statement.setString(3, usersalt);
						statement.executeUpdate();

						status = Status.OK;
					}
				}
			} catch (SQLException ex) {
				status = Status.CONNECTION_FAILED;
				System.out.println("Error while connecting to the database: " + ex);
			}
		}

		else {
			System.out.println("Password rule is not satisfied");
			status = Status.INVALID_PASSWORD;
		}

		return status;
	}

	/**
	 * Get the usersalt for this user and generate password
	 * 
	 * @param user
	 * @param password
	 * @return status OK if user is authenticated, error otherwise
	 */
	public Status userLogin(String user, String password) {

		Status status = Status.ERROR;

		// make sure we have non-null and non-emtpy values for login
		if (isBlank(user) || isBlank(password)) {
			System.out.println("Invalid login");
			return Status.INVALID_LOGIN;
		}

		// establish db connection
		try (Connection connection = db.getConnection();) {
			String usersalt = getSalt(connection, user);
			if (usersalt != null) {
				String hashedPassword = getHash(password, usersalt);
				if (authenticateUser(connection, user, hashedPassword)) {
					status = Status.OK;
				}
			} else {
				System.out.println("invalid user");
				return Status.INVALID_USER;
			}

		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}

		return status;

	}

	/**
	 * Gets the salt for a specific user.
	 *
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - which user to retrieve salt for
	 * @return salt for the specified user or null if user does not exist
	 * @throws SQLException
	 *             if any issues with database connection
	 */
	private String getSalt(Connection connection, String user) throws SQLException {
		assert connection != null;
		assert user != null;

		String salt = null;

		try (PreparedStatement statement = connection.prepareStatement(SALT_SQL);) {
			statement.setString(1, user);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				salt = results.getString("usersalt");
			}
		}

		return salt;
	}

	/**
	 * Get the generated password and check from table if this is the password
	 * stored for this user
	 * 
	 * @param connection
	 * @param user
	 * @param hashedPassword
	 * @return true if password matches, false otherwise
	 * @throws SQLException
	 */
	private boolean authenticateUser(Connection connection, String user, String hashedPassword) throws SQLException {
		assert connection != null;
		assert user != null;

		boolean success = false;

		try (PreparedStatement statement = connection.prepareStatement(AUTH_SQL);) {
			statement.setString(1, user);
			statement.setString(2, hashedPassword);
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				success = true;
			}
		}

		return success;
	}

	/**
	 * Populate tables hotel, review and users with the json file information
	 * 
	 * @param data
	 * @throws SQLException
	 */
	public void loadHotelAndReview(ThreadSafeHotelData data) {
		loadHotel(data);
		loadReview(data);
		loadUsers();
	}

	/**
	 * Check if hotel data already exists. If not, get the data from
	 * ThreadSafeHotelData and populate hotel table
	 * 
	 * @param data
	 * @throws SQLException
	 */
	private void loadHotel(ThreadSafeHotelData data) {
		PreparedStatement prepstatement = null;
		ResultSet rs = null;

		try (Connection connection = db.getConnection(); Statement statement = connection.createStatement();) {

			rs = statement.executeQuery(CHECK_HOTELDATA_EXISTS_SQL);
			List<String> hotelList = data.getHotels();
			if (rs.next()) {
				if (rs.getInt(1) != hotelList.size()) {
					prepstatement = connection.prepareStatement(LOAD_HOTEL_SQL);

					for (int i = 0; i < hotelList.size(); i++) {

						prepstatement.setString(1, data.getHotelInfo(hotelList.get(i)).getHotelId());
						prepstatement.setString(2, data.getHotelInfo(hotelList.get(i)).getHotelName());
						prepstatement.setString(3, data.getHotelInfo(hotelList.get(i)).getAddr().getStreetAddress());
						prepstatement.setString(4, data.getHotelInfo(hotelList.get(i)).getAddr().getCity());
						prepstatement.setString(5, data.getHotelInfo(hotelList.get(i)).getAddr().getState());
						prepstatement.setDouble(6, data.getHotelInfo(hotelList.get(i)).getAddr().getLongitude());
						prepstatement.setDouble(7, data.getHotelInfo(hotelList.get(i)).getAddr().getLatitude());
						prepstatement.setString(8, data.getHotelInfo(hotelList.get(i)).getAddr().getCountry());
						prepstatement.executeUpdate();
					}

				}
			}

		} catch (SQLException e) {
			System.out.println("SQLException in method loadHotel : " + e.getMessage());
		} finally {
			try {
				if (prepstatement != null) {
					prepstatement.close();
					prepstatement = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException se) {
				System.out.println(se.getMessage());
			}
		}
	}

	/**
	 * Get hotel id, hotel name, concatenated address and average rating of
	 * hotel from table and store them in array list
	 * 
	 * @return array list of column names and column values
	 */
	public ArrayList<HashMap<String, Object>> getHotel() {

		ArrayList<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> row = null;

		ResultSet results = null;
		try (Connection connection = db.getConnection();
				PreparedStatement prepstatement = connection.prepareStatement(GET_HOTELS_SQL);) {

			results = prepstatement.executeQuery();
			ResultSetMetaData metaData = results.getMetaData();
			Integer columnCount = metaData.getColumnCount();
			while (results.next()) {
				row = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					row.put(metaData.getColumnName(i), results.getObject(i));
				}
				resultList.add(row);
			}

		} catch (SQLException e) {
			System.out.println("SQLException in method getHotel : " + e.getMessage());
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				results = null;
			}
		}
		return resultList;
	}

	/**
	 * Get the list of hotels saved by a specific user
	 * 
	 * @param user
	 * @return array list of column names and column values
	 */
	public ArrayList<HashMap<String, Object>> getSavedHotel(String user) {
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> row = null;

		ResultSet results = null;
		try (Connection connection = db.getConnection();
				PreparedStatement prepstatement = connection.prepareStatement(GET_SAVED_HOTELS_SQL);) {
			prepstatement.setString(1, user);
			results = prepstatement.executeQuery();
			ResultSetMetaData metaData = results.getMetaData();
			Integer columnCount = metaData.getColumnCount();
			while (results.next()) {
				row = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					row.put(metaData.getColumnName(i), results.getObject(i));
				}
				resultList.add(row);
			}

		} catch (SQLException e) {
			System.out.println("SQLException in method get saved Hotel : " + e.getMessage());
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				results = null;
			}
		}
		return resultList;
	}

	/**
	 * Get the hotel information for a given hotelId
	 * 
	 * @param hotelId
	 * @return array list of column names and column values
	 */
	public ArrayList<HashMap<String, Object>> getHotelInfo(String hotelId) {

		ArrayList<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> row = null;

		ResultSet results = null;
		try (Connection connection = db.getConnection();
				PreparedStatement prepstatement = connection.prepareStatement(GET_HOTEL_INFO_SQL);) {
			prepstatement.setString(1, hotelId);
			results = prepstatement.executeQuery();
			ResultSetMetaData metaData = results.getMetaData();
			Integer columnCount = metaData.getColumnCount();
			while (results.next()) {
				row = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					row.put(metaData.getColumnName(i), results.getObject(i));
				}
				resultList.add(row);
			}

		} catch (SQLException e) {
			System.out.println("SQLException in method getHotelInfo : " + e.getMessage());
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				results = null;
			}
		}
		return resultList;
	}

	/**
	 * Get the hotel list that matches with a given name
	 * 
	 * @param hotelname
	 * @return array list of column names and column values
	 */
	public ArrayList<HashMap<String, Object>> getHotelByName(String hotelname) {

		ArrayList<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> row = null;

		ResultSet results = null;
		try (Connection connection = db.getConnection();
				PreparedStatement prepstatement = connection.prepareStatement(GET_HOTEL_BY_NAME_SQL);) {
			prepstatement.setString(1, "%" + hotelname + "%");
			results = prepstatement.executeQuery();
			ResultSetMetaData metaData = results.getMetaData();
			while (results.next()) {
				row = new HashMap<String, Object>();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					row.put(metaData.getColumnName(i), results.getObject(i));
				}
				resultList.add(row);
			}

		} catch (SQLException e) {
			System.out.println("SQLException in method getHotel by name : " + e.getMessage());
		} finally {
			if (results != null) {
				try {
					results.close();
					results = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
				results = null;
			}
		}
		return resultList;
	}

	/**
	 * Get the city and state combination available in hotel table
	 * 
	 * @return array list of column names and column values
	 */
	public ArrayList<HashMap<String, Object>> getCityState() {

		ArrayList<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> row = null;

		ResultSet results = null;
		try (Connection connection = db.getConnection(); Statement statement = connection.createStatement();) {

			results = statement.executeQuery(GET_CITY_STATE_SQL);
			ResultSetMetaData metaData = results.getMetaData();
			while (results.next()) {
				row = new HashMap<String, Object>();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					row.put(metaData.getColumnName(i), results.getObject(i));
				}
				resultList.add(row);
			}

		} catch (SQLException e) {
			System.out.println("SQLException in method get City State : " + e.getMessage());
		} finally {
			if (results != null) {
				try {
					results.close();
					results = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
				results = null;
			}
		}
		return resultList;
	}

	/**
	 * Get the hotel with a given city and state combination
	 * 
	 * @param city
	 * @param state
	 * @return array list of column names and column values
	 */
	public ArrayList<HashMap<String, Object>> getHotelByCityState(String city, String state) {

		ArrayList<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> row = null;

		ResultSet results = null;
		try (Connection connection = db.getConnection();
				PreparedStatement prepstatement = connection.prepareStatement(GET_HOTEL_BY_CITY_STATE_SQL);) {
			prepstatement.setString(1, city);
			prepstatement.setString(2, state);
			results = prepstatement.executeQuery();
			ResultSetMetaData metaData = results.getMetaData();
			while (results.next()) {
				row = new HashMap<String, Object>();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					row.put(metaData.getColumnName(i), results.getObject(i));
				}
				resultList.add(row);
			}

		} catch (SQLException e) {
			System.out.println("SQLException in method getHotel by city state : " + e.getMessage());
		} finally {
			if (results != null) {
				try {
					results.close();
					results = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
				results = null;
			}
		}
		return resultList;
	}

	/**
	 * Get the latitude, longitude and city information for a given hotel
	 * 
	 * @param hotelid
	 * @return array list of column names and column values
	 */
	public ArrayList<HashMap<String, Object>> getHotelAddress(String hotelid) {

		ArrayList<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> row = null;

		ResultSet results = null;
		try (Connection connection = db.getConnection();
				PreparedStatement prepstatement = connection.prepareStatement(GET_HOTEL_ADDRESS_SQL);) {
			prepstatement.setString(1, hotelid);
			results = prepstatement.executeQuery();
			ResultSetMetaData metaData = results.getMetaData();
			if (results.next()) {
				row = new HashMap<String, Object>();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					row.put(metaData.getColumnName(i), results.getObject(i));
				}
				resultList.add(row);
			}

		} catch (SQLException e) {
			System.out.println("SQLException in method getHoteladdress : " + e.getMessage());
		} finally {
			if (results != null) {
				try {
					results.close();
					results = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return resultList;
	}

	/**
	 * Check if review data already exists. If not, populate the table with the
	 * json information. Get the max of userid from users table and increment it
	 * for the next user in review table
	 * 
	 * @param data
	 * @throws SQLException
	 */
	private void loadReview(ThreadSafeHotelData data) {
		PreparedStatement prepstatement = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try (Connection connection = db.getConnection(); Statement statement = connection.createStatement();) {

			rs = statement.executeQuery(CHECK_REVIEWDATA_EXISTS_SQL);
			List<String> hotelList = data.getHotels();
			if (rs.next()) {
				if (rs.getInt(1) == 0) {

					prepStmt = connection.prepareStatement(GET_MAX_USERID_SQL);
					rs1 = prepStmt.executeQuery();
					int userId = 0;
					if (rs1.next()) {
						userId = rs1.getInt(1);
					}

					prepstatement = connection.prepareStatement(LOAD_REVIEW_SQL);

					for (int i = 0; i < hotelList.size(); i++) {
						TreeSet<Review> reviewTS = data.getReviewInfo(hotelList.get(i));
						if (reviewTS != null) {
							for (Review review : reviewTS) {
								if (!review.getUsername().equalsIgnoreCase("anonymous")) {
									userId++;
									String reviewStr = review.getReview();
									if (reviewStr.length() > 1000)
										reviewStr = reviewStr.substring(0, 1000);
									prepstatement.setString(1, review.getReviewId());
									prepstatement.setString(2, review.getHotelId());
									prepstatement.setInt(3, review.getRating());
									prepstatement.setString(4, review.getReviewTitle());
									prepstatement.setString(5, reviewStr);
									prepstatement.setBoolean(6, review.isRecom());
									prepstatement.setDate(7, new java.sql.Date(review.getDate().getTime()));
									prepstatement.setString(8, review.getUsername());
									prepstatement.setInt(9, userId);
									prepstatement.executeUpdate();
								}
							}
						}
					}

				}
			}

		} catch (SQLException e) {
			System.out.println("SQLException in method loadReview : " + e.getMessage());
		} finally {
			try {
				if (prepstatement != null) {
					prepstatement.close();
					prepstatement = null;
				}
				if (prepStmt != null) {
					prepStmt.close();
					prepStmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
			} catch (SQLException se) {
				System.out.println(se.getMessage());
			}
		}
	}

	/**
	 * Get review information in a limited number of rows in order to display as
	 * pagination in the website
	 * 
	 * @param hotelId
	 * @return array list of column names and column values
	 */
	public ArrayList<HashMap<String, Object>> getReview(String hotelId, int offset, String sortBy) {

		ArrayList<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> row = null;
		PreparedStatement prepstatement = null;
		ResultSet results = null;
		try (Connection connection = db.getConnection();) {
			if (sortBy.equalsIgnoreCase("date")) {
				prepstatement = connection.prepareStatement(GET_REVIEW_SORT_DATE_SQL);
			} else if (sortBy.equalsIgnoreCase("rating")) {
				prepstatement = connection.prepareStatement(GET_REVIEW_SORT_RATING_SQL);
			} else
				prepstatement = connection.prepareStatement(GET_REVIEW_SQL);
			prepstatement.setString(1, hotelId);
			prepstatement.setInt(2, offset);
			results = prepstatement.executeQuery();
			ResultSetMetaData metaData = results.getMetaData();
			while (results.next()) {
				row = new HashMap<String, Object>();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					row.put(metaData.getColumnName(i), results.getObject(i));
				}
				resultList.add(row);
			}
			results.close();
			results = prepstatement.executeQuery("SELECT FOUND_ROWS()");
			if (results.next()) {
				this.noOfRecords = results.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("SQLException in method getReview : " + e.getMessage());
		} finally {
			if (results != null) {
				try {
					results.close();
					results = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return resultList;
	}

	/**
	 * Get the global variable noOfRecords
	 * 
	 * @return number of records returned by select query
	 */
	public int getNoOfRecords() {
		return noOfRecords;
	}

	/**
	 * Get the last login time for a given user. If the user logs in for the
	 * first time, insert a row. Else, update existing row with current time
	 * 
	 * @param username
	 * @return date
	 */
	public Date getLastLogin(String username) {
		ResultSet results = null;
		Timestamp time = null;
		Date logintime = null;
		PreparedStatement prep = null;
		PreparedStatement prep1 = null;
		try (Connection connection = db.getConnection();
				PreparedStatement prepstatement = connection.prepareStatement(GET_LAST_LOGIN_SQL)) {

			prepstatement.setString(1, username);
			results = prepstatement.executeQuery();

			if (results.next()) {
				time = results.getTimestamp(1);
				logintime = time;
			}
			if (logintime != null) {
				prep = connection.prepareStatement(UPDATE_LAST_LOGIN_SQL);
				prep.setTimestamp(1, new java.sql.Timestamp(new java.util.Date().getTime()));
				prep.setString(2, username);
				prep.executeUpdate();
			} else {
				prep1 = connection.prepareStatement(INSERT_LAST_LOGIN_SQL);
				prep1.setString(1, username);
				prep1.setTimestamp(2, new java.sql.Timestamp(new java.util.Date().getTime()));
				prep1.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("SQLException in method getLastLogin : " + e.getMessage());
		} finally {
			try {
				if (results != null) {
					results.close();
					results = null;
				}
				if (prep != null) {
					prep.close();
					prep = null;
				}
				if (prep1 != null) {
					prep1.close();
					prep1 = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return logintime;
	}

	/**
	 * Check if user data exists in table. If not, populate the table with user
	 * name from review table. Generate a default password for all the users.
	 * 
	 * @throws SQLException
	 */
	private void loadUsers() {
		PreparedStatement prepstatement = null;
		ResultSet rs = null;
		try (Connection connection = db.getConnection(); Statement statement = connection.createStatement();) {

			rs = statement.executeQuery(CHECK_USERID_EXISTS_SQL);
			if (rs.next()) {
				if (rs.getInt(1) == 0) {

					byte[] saltBytes = new byte[16];
					random.nextBytes(saltBytes);

					String usersalt = encodeHex(saltBytes, 32);
					String passhash = getHash("password", usersalt);
					prepstatement = connection.prepareStatement(LOAD_USERS_SQL);

					prepstatement.setString(1, passhash);
					prepstatement.setString(2, usersalt);

					prepstatement.executeUpdate();

				}
			}

		} catch (SQLException e) {
			System.out.println("SQLException in method loadUsers : " + e.getMessage());
		} finally {
			try {
				if (prepstatement != null) {
					prepstatement.close();
					prepstatement = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException se) {
				System.out.println(se.getMessage());
			}
		}
	}

	/**
	 * Get the user id from users table. Check if rating is between 1 and 5.
	 * Generate a reviewId and add the review to table
	 * 
	 * @return status is success if review added successfully
	 */
	public Status addReview(String hotelId, int rating, String reviewTitle, String review, boolean isRecom,
			String user) {
		PreparedStatement prepstatement = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		ResultSet results = null;
		Status status = Status.ADD_REVIEW_FAILURE;

		try (Connection connection = db.getConnection();PreparedStatement prep=connection.prepareStatement(CHECK_REVIEW_EXISTS__SQL);) {
			prep.setString(1, user);
			prep.setString(2, hotelId);
			results = prep.executeQuery();

			if (results.next()) {
				if (results.getInt(1) == 0) {
					prepStmt = connection.prepareStatement(GET_USERID_SQL);
					prepStmt.setString(1, user);
					rs = prepStmt.executeQuery();
					int userId = 0;
					if (rs.next()) {
						userId = rs.getInt(1);
					}

					prepstatement = connection.prepareStatement(LOAD_REVIEW_SQL);
					byte[] reviewIdBytes = new byte[20];
					random.nextBytes(reviewIdBytes);
					BigInteger bigint = new BigInteger(1, reviewIdBytes);
					String reviewId = bigint.toString(32);
					prepstatement.setString(1, reviewId);
					prepstatement.setString(2, hotelId);
					prepstatement.setInt(3, rating);
					prepstatement.setString(4, reviewTitle);
					prepstatement.setString(5, review);
					prepstatement.setBoolean(6, isRecom);
					prepstatement.setDate(7, new java.sql.Date(new java.util.Date().getTime()));
					prepstatement.setString(8, user);
					prepstatement.setInt(9, userId);
					prepstatement.executeUpdate();
					status = Status.ADD_REVIEW_SUCCESS;
				}
				else{
					status = Status.ADD_REVIEW_EXISTS;
				}
			}
		} catch (SQLException e) {
			System.out.println("SQL Exception in method AddReview : " + e.getMessage());
		} finally {
			try {
				if (prepStmt != null) {
					prepStmt.close();
					prepStmt = null;
				}
				if (prepstatement != null) {
					prepstatement.close();
					prepstatement = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException se) {
				System.out.println("SQL Exception in Add review " + se.getMessage());
			}
		}
		return status;
	}

	/**
	 * Update review with the information entered by user
	 * 
	 * @param reviewId
	 * @param rating
	 * @param reviewTitle
	 * @param review
	 * @param isRecom
	 * @return status OK if review updated successfully
	 */
	public Status modifyReview(String reviewId, int rating, String reviewTitle, String review, boolean isRecom) {
		Status status = Status.ERROR;

		try (Connection connection = db.getConnection();
				PreparedStatement prepstatement = connection.prepareStatement(UPDATE_REVIEW_SQL);) {
			prepstatement.setInt(1, rating);
			prepstatement.setString(2, reviewTitle);
			prepstatement.setString(3, review);
			prepstatement.setBoolean(4, isRecom);
			prepstatement.setDate(5, new java.sql.Date(new java.util.Date().getTime()));
			prepstatement.setString(6, reviewId);
			prepstatement.executeUpdate();
			status = Status.OK;

		} catch (SQLException e) {
			System.out.println("SQL Exception in method modifyReview : " + e.getMessage());
		}
		return status;
	}

	/**
	 * Check if hotel is already saved in the table. If not, insert the hotel
	 * information for that user
	 * 
	 * @param hotelid
	 * @param hotelname
	 * @param username
	 * @return status success if successfully saved
	 */
	public Status saveHotel(String hotelid, String hotelname, String username) {
		Status status = Status.SAVE_HOTEL_FAILURE;
		ResultSet results = null;
		PreparedStatement prep = null;
		try (Connection connection = db.getConnection();
				PreparedStatement prepstatement = connection.prepareStatement(CHECK_SAVED_HOTEL_SQL)) {

			prepstatement.setString(1, username);
			prepstatement.setString(2, hotelid);
			results = prepstatement.executeQuery();

			if (results.next()) {
				if (results.getInt(1) == 0) {
					prep = connection.prepareStatement(SAVE_HOTEL_SQL);
					prep.setString(1, hotelid);
					prep.setString(2, hotelname);
					prep.setString(3, username);
					prep.executeUpdate();
					status = Status.SAVE_HOTEL_SUCCESS;
				}
			}
		} catch (SQLException e) {
			System.out.println("SQLException in method save Hotel: " + e.getMessage());
		} finally {
			try {
				if (results != null) {
					results.close();
					results = null;
				}
				if (prep != null) {
					prep.close();
					prep = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return status;
	}

	/**
	 * Delete the saved hotel for a given user
	 * 
	 * @param username
	 * @return success if successfully deleted
	 */
	public Status clearSavedHotel(String username) {
		Status status = Status.CLEAR_HOTEL_FAILURE;
		try (Connection connection = db.getConnection();
				PreparedStatement prepstatement = connection.prepareStatement(DELETE_SAVED_HOTEL_SQL)) {
			prepstatement.setString(1, username);
			prepstatement.executeUpdate();
			status = Status.CLEAR_HOTEL_SUCCESS;
		} catch (SQLException e) {
			System.out.println("SQLException in method clear saved Hotel: " + e.getMessage());
		}
		return status;
	}

	/**
	 * Delete a given review
	 * 
	 * @param reviewid
	 * @return status OK if successfully deleted
	 */
	public Status deleteReview(String reviewid) {
		Status status = Status.ERROR;
		try (Connection connection = db.getConnection();
				PreparedStatement prepstatement = connection.prepareStatement(DELETE_REVIEW_SQL)) {
			prepstatement.setString(1, reviewid);
			prepstatement.executeUpdate();
			status = Status.OK;
		} catch (SQLException e) {
			System.out.println("SQLException in method clear saved Hotel: " + e.getMessage());
		}
		return status;
	}
}
