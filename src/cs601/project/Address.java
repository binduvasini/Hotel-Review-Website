package cs601.project;

/**
 * 
 * @author vasini
 * 
 *         Class Address - getter setter for address related information
 *
 */
public class Address {

	private String streetAddress;
	private String city;
	private String state;
	private double longitude;
	private double latitude;
	private String country;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public Address(String streetAddress, String city, String state, double longitude, double latitude, String country) {
		super();
		this.streetAddress = streetAddress;
		this.city = city;
		this.state = state;
		this.longitude = longitude;
		this.latitude = latitude;
		this.country = country;
	}

}
