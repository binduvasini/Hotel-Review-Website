package cs601.project;

public class Hotel implements Comparable<Hotel>{

	private String hotelId;
	private String hotelName;
	private Address addr;

	public String getHotelId() {
		return hotelId;
	}
	public void setHotelId(String hotelId) {
		this.hotelId = hotelId;
	}
	public String getHotelName() {
		return hotelName;
	}
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public Address getAddr() {
		return addr;
	}
	public void setAddr(Address addr) {
		this.addr = addr;
	}

	public Hotel(String hotelId, String hotelName, Address addr) {
		super();
		this.hotelId = hotelId;
		this.hotelName = hotelName;
		this.addr = addr;
	}
	@Override
	public int compareTo(Hotel o) {
		//compare based on names
		return this.hotelName.compareTo(o.hotelName);
	}
}
