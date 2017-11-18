package cs601.project;



/**
 * Creates a Status enum type for tracking errors. Each Status enum type
 * will use the ordinal as the error code, and store a message describing
 * the error.
 * @author Prof. Engle
 *
 * @see StatusTester
 */
public enum Status {

	/*
	 * Creates several Status enum types. The Status name and message is
	 * given in the NAME(message) format below. The Status ordinal is
	 * determined by its position in the list. (For example, OK is the
	 * first element, and will have ordinal 0.)
	 */

	OK("No errors occured."),
	ERROR("Unknown error occurred."),
	MISSING_CONFIG("Unable to find configuration file."),
	MISSING_VALUES("Missing values in configuration file."),
	CONNECTION_FAILED("Failed to establish a database connection."),
	CREATE_FAILED("Failed to create necessary tables."),
	INVALID_LOGIN("Invalid username and/or password."),
	INVALID_USER("User does not exist."),
	DUPLICATE_USER("User with that username already exists."),
	SQL_EXCEPTION("Unable to execute SQL statement."),
	INVALID_RATING("Invalid Rating. Please enter a value from 1 to 5."),
	INVALID_PASSWORD("Password must have 8 to 20 characters and contain at least 1 uppercase, 1 lowercase and 1 digit"),
	SAVE_HOTEL_SUCCESS("Hotel successfully saved"),
	SAVE_HOTEL_FAILURE("Hotel not saved"),
	CLEAR_HOTEL_SUCCESS("Saved hotels successfully cleared"),
	CLEAR_HOTEL_FAILURE("Unable to clear saved hotels"),
	RADIO_NOTSELECTED("Please select a radius"),
	ADD_REVIEW_SUCCESS("Review added successfully. Please refresh the page to see the updated reviews"),
	ADD_REVIEW_FAILURE("Unable to add review. Please try again"),
	ADD_REVIEW_EXISTS("Only one review allowed per user. Review already exists. Please delete or modify existing review"),
	REVIEW_MANDATORY_FIELDS_UNFILLED("All text fields are mandatory");

	private final String message;

	private Status(String message) {
		this.message = message;
	}

	public String message() {
		return message;
	}

	@Override
	public String toString() {
		return this.message;
	}
}