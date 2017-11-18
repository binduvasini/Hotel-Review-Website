<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<title>Modify Review</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<!-- <script src="/js/review.js"></script> -->
</head>
<body>
	<nav class="navbar navbar-inverse">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="home.jsp">Home</a>
			</div>
			<ul class="nav navbar-nav">
				<li class="active"><a href="hotels">Hotels</a></li>
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li><a href="logout"><span
						class="glyphicon glyphicon-log-in"></span> Logout</a></li>
			</ul>
		</div>
	</nav>
	<div class="container">
		<h2>Modify Review</h2>
		<form action="modifyreview" method="post">
		<%
				String status = "";
				if(request.getParameter("status")!=null){
					status = (String)request.getParameter("status");
						%><div class="alert alert-danger alert-dismissable">
						<a href="#" class="close" data-dismiss="alert" aria-label="close">×</a>
						<%=status%>
					</div>
					<%
				}
				
				%>
			<div class="form-group">
				<label for="reviewtitle">Review Title:</label> <input type="text"
					class="form-control" name="reviewtitle">
			</div>
			<div class="form-group">
				<label for="review">Review:</label> <input type="text"
					class="form-control" name="review">
			</div>
			<div class="form-group">
				<label for="rating">Rating</label> <input type="text"
					class="form-control" name="rating" onkeyup="validateRating(this);">
			</div>
			<div class="checkbox">
				<label><input type="checkbox" name="isrecom">Is Recommended</label>
			</div>
			<button type="submit" class="btn btn-default">Submit</button>
			<input type="hidden" name="reviewId" value=<%=request.getParameter("reviewId")%>>
			<input type="hidden" name="hotelName" value=<%=request.getParameter("hotelName")%>>
		</form>
	</div>
</body>
<script>
function validateRating(rating) {
	var ratingval = rating.value;
	if (!(ratingval >= 1) || !(ratingval <= 5)) {
		alert("Invalid Rating. Please enter a value from 1 to 5.");
	}
}
</script>
</html>