<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="cs601.project.*"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%
	String user = (String) session.getAttribute("user");
	String hotelId = (String) request.getParameter("hotelId");
%>
<html>
<head>
</head>
<form>
<div class="form-group">
			<label class="control-label col-sm-2" for="email">Is Recommended:</label>
			<div class="col-sm-10">
				<input type="checkbox" class="form-control" id="isrecom" style="width:15px">
			</div>
		</div>
<div class="form-group">
			<label class="control-label col-sm-2" for="email">Review Title:</label>
			<div class="col-sm-10">
				<input type="text" class="form-control" id="reviewtitle" size="30" style="width:200px;" placeholder="Review Title">
			</div>
		</div>
		<div class="form-group">
			<label class="control-label col-sm-2" for="pwd">Review:</label>
			<div class="col-sm-10">
				<input type="text" class="form-control" id="review" size="30" style="width:200px;" placeholder="Review">
			</div>
		</div>
		<div class="form-group">
			<label class="control-label col-sm-2" for="pwd">Rating:</label>
			<div class="col-sm-10">
				<input type="text" class="form-control" id="rating" size="30" style="width:200px;" placeholder="Rating" onkeyup="validateRating(this);">
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-10">
				<br>
		<button type="button" class="btn btn-default" onclick="addReview()">Submit</button>
			</div>
		</div>
<br><br>
<div id='display'></div>
</form>
<script>function addReview(){
	var user = "<%=user%>";
	var hotelId = "<%=hotelId%>";
		var rating = document.getElementById("rating").value;
		var isrecom = document.getElementById("isrecom").checked;
		var reviewtitle = document.getElementById("reviewtitle").value;
		var review = document.getElementById("review").value;

		var xhttp = new XMLHttpRequest();
		xhttp.open("POST", "addreview", true);
		xhttp.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		xhttp.send("hotelId=" + hotelId + "&rating=" + rating + "&review="
				+ review + "&reviewtitle=" + reviewtitle + "&isrecom="
				+ isrecom + "&user=" + user);
		xhttp.onreadystatechange = function() {
			console.log(this.readyState);
			if (this.readyState == 4 && this.status == 200) {
				document.getElementById("display").innerHTML = this.responseText;
			}
		};
	}
</script>
<script>
	function validateRating(rating) {
		var ratingval = rating.value;
		if (!(ratingval >= 1) || !(ratingval <= 5)) {
			alert("Invalid Rating. Please enter a value from 1 to 5.");
		}
	}
</script>
</html>