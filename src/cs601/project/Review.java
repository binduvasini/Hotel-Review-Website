package cs601.project;

import java.util.Date;

public class Review implements Comparable<Review> {
	private String hotelId;
	private String reviewId;
	private int rating;
	private String reviewTitle;
	private String review;
	private boolean isRecom;
	private Date date;
	private String username;

	public String getHotelId() {
		return hotelId;
	}

	public void setHotelId(String hotelId) {
		this.hotelId = hotelId;
	}

	public String getReviewId() {
		return reviewId;
	}

	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getReviewTitle() {
		return reviewTitle;
	}

	public void setReviewTitle(String reviewTitle) {
		this.reviewTitle = reviewTitle;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public boolean isRecom() {
		return isRecom;
	}

	public void setRecom(boolean isRecom) {
		this.isRecom = isRecom;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Review(String hotelId, String reviewId, int rating, String reviewTitle, String review, boolean isRecom,
			Date date, String username) {
		super();
		this.hotelId = hotelId;
		this.reviewId = reviewId;
		this.rating = rating;
		this.reviewTitle = reviewTitle;
		this.review = review;
		this.isRecom = isRecom;
		this.date = date;
		this.username = username;
	}

	@Override
	public int compareTo(Review o) {
		// compare the dates. If dates are equal, compare the usernames
		if (this.date.compareTo(o.date) == 0) {
			if (this.username.compareTo(o.username) == 0)
				return this.reviewId.compareTo(o.reviewId);
			else
				return this.username.compareTo(o.username);
		} else
			return this.date.compareTo(o.date);
	}
}
